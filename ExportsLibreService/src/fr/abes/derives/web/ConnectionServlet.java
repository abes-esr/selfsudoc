package fr.abes.derives.web;

import fr.abes.derives.connection.QueryProduitsDerives;
import fr.abes.utils.BufferedRW;
import fr.abes.utils.LogHelper;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ConnectionServlet extends HttpServlet {

    private static final long serialVersionUID = 3004199320339638373L;
    final private static String homeDir = System.getenv("EOD_HOME");
    private static LogHelper logger = new LogHelper(ConnectionServlet.class);

    private static DataSource produitsDerivesDataSource = null;
    private static DataSource sybaseCBSDataSource = null;
    private static String runtimeDir = null;
    private static String loginProduitsDerives = null;
    private static String passwordProduitsDerives = null;

    public static void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.setAttribute(UserBean.KEY_USER, null);
            session.setAttribute(UserBean.KEY_PROFILE, null);
            session.invalidate();
        }
        logger.debug("logged out");
    }

    public static String getLoginProduitsDerives() {
        return loginProduitsDerives;
    }

    public static String getPasswordProduitsDerives() {
        return passwordProduitsDerives;
    }

    @Override
    public String getServletInfo() {
        return "michaux@abes.fr 2011";
    }

    public void init() throws ServletException {

        Context envContext = null;

        logger.debug("homeDir=" + homeDir);
        try {
            // recuperation de la source de donnee
            Context initContext = new InitialContext();
            envContext = (Context) initContext.lookup("java:/comp/env");
            produitsDerivesDataSource = (DataSource) envContext.lookup("jdbc/ProduitsDerivesDataSource");
            sybaseCBSDataSource = (DataSource) envContext.lookup("jdbc/SybaseCBS");
            runtimeDir = (String) envContext.lookup("runtimeDir");
            //final private static String runtimeDir=homeDir; //Linux config
            loginProduitsDerives = (String) envContext.lookup("loginProduitsDerives");
            passwordProduitsDerives = (String) envContext.lookup("passwordProduitsDerives");

        } catch (NamingException e) {
            logger.error(e.getMessage());
            throw new ServletException(e);
        }

        logger.debug("runtimeDir=" + runtimeDir);
        logger.debug("loginProduitsDerives=" + loginProduitsDerives);

        if (envContext == null)
            throw new ServletException("Error: No Context");
        if (runtimeDir == null)
            throw new ServletException("Error: No runtimeDir");
        if (loginProduitsDerives == null)
            throw new ServletException("Error: No loginProduitsDerives");
        if (passwordProduitsDerives == null)
            throw new ServletException("Error: No passwordProduitsDerives");
        if (produitsDerivesDataSource == null)
            throw new ServletException("Error: No produitsDerivesDataSource");
        if (sybaseCBSDataSource == null)
            throw new ServletException("Error: No sybaseCBSDataSource");

        //Read and cache layouts in webapp context
        Map<String, Map<String, String>> layouts = RequestHelper.mapLayoutsfromDB(produitsDerivesDataSource);
        ServletContext appContext = getServletContext();
        logger.debug("layouts are cached in webapp context");
        appContext.setAttribute(RequestHelper.KEY_MAP_LAYOUTS, layouts);

        //Read and cache layouts in webapp context
        Map<String, String> exclusions = RequestHelper.mapExclusionsfromDB(produitsDerivesDataSource);
        logger.debug("exclusions zones are cached in webapp context");
        appContext.setAttribute(RequestHelper.KEY_MAP_EXCLUSIONS, exclusions);

        //Read and cache RCR shortNames in webapp context
        Map<String, String> shortNames = RequestHelper.mapRCRToShortNames(sybaseCBSDataSource);
        logger.debug("shortNames are cached in webapp context");
        appContext.setAttribute(RequestHelper.KEY_MAP_SHORTNAMES, shortNames);

        //Read from DB and cache Counts By RCR in webapp context
        Connection connProduitsDerives = null;
        Map<String, Long> countsByRCR = new HashMap<String, Long>();
        try {
            if (produitsDerivesDataSource != null) {
                connProduitsDerives = produitsDerivesDataSource.getConnection(this.loginProduitsDerives, this.passwordProduitsDerives);
            }
            connProduitsDerives.setAutoCommit(false);
            countsByRCR = QueryProduitsDerives.rcrAlreadyCounted(connProduitsDerives);
            connProduitsDerives.commit();
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new ServletException(e);
        } finally {
            try {
                if (connProduitsDerives != null) {
                    connProduitsDerives.close();
                }
            } catch (SQLException e) {
                logger.error(e.getMessage());
                throw new ServletException(e);
            }
            connProduitsDerives = null;
        }

        logger.debug(countsByRCR.size() + " count datas read from produits_derives.rcr_datas");
        appContext.setAttribute(RequestHelper.KEY_PROPS_COUNTS_BY_RCR, countsByRCR);

        try {
            String localHostName = InetAddress.getLocalHost().getHostName();
            String md5HashlocalHostName = BufferedRW.hashMD5(localHostName);
            appContext.setAttribute(RequestHelper.KEY_PROPS_LOCALHOSTNAME, md5HashlocalHostName);
        } catch (UnknownHostException e) {
            logger.error(e.getMessage());
            throw new ServletException(e);
        }
    }

    /**
     * Check and cache RCR->ILn in webapp context if not already done
     * <p>
     * Map.Values can be used to list all ILNs
     *
     * @param session
     */
    private void checkAndCacheMapInAppContext(ServletContext appContext) {

        Map<String, Integer> rcrToIln = (Map<String, Integer>) appContext.getAttribute(RequestHelper.KEY_MAP_NOTMAIN_RCR_TO_ILN);
        if (rcrToIln == null || rcrToIln.size() == 0) {
            // first = read from DB and cache in webapp context
            logger.debug("mapNOTMAINRCRToILNfromDB");
            rcrToIln = RequestHelper.mapRCRToILNfromDB(ConnectionServlet.sybaseCBSDataSource, false);
            logger.debug("need to cache " + rcrToIln.size() + " RCRs in webapp context");
            appContext.setAttribute(RequestHelper.KEY_MAP_NOTMAIN_RCR_TO_ILN, rcrToIln);
        }

        rcrToIln = (Map<String, Integer>) appContext.getAttribute(RequestHelper.KEY_MAP_ISMAIN_RCR_TO_ILN);
        if (rcrToIln == null || rcrToIln.size() == 0) {
            // first = read from DB and cache in webapp context
            logger.debug("mapISMAINRCRToILNfromDB");
            rcrToIln = RequestHelper.mapRCRToILNfromDB(ConnectionServlet.sybaseCBSDataSource, true);
            logger.debug("need to cache " + rcrToIln.size() + " RCRs in webapp context");
            appContext.setAttribute(RequestHelper.KEY_MAP_ISMAIN_RCR_TO_ILN, rcrToIln);
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     *
     * Handles user authentication and authorization on each request,
     * ask RequestHelper which command to execute based on request.getServletPath(),
     * execute command,
     * forward OK to URL returned by command's call to MyDispatcher.dispatch(ICommand command) or exception URL
     *
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        logger.debug("doPost");

        RequestHelper helper = new RequestHelper(request, response);
        helper.setProduitsDerivesDataSource(ConnectionServlet.produitsDerivesDataSource);
        helper.setSybaseILNDataSource(ConnectionServlet.sybaseCBSDataSource);
        logger.debug("runtimeDir=" + runtimeDir);
        helper.setRuntimeDir(runtimeDir);
        String next = null;

        String userKey = RequestHelper.getUserKey(helper.getRequest());
        if (userKey == null) {
            String errorMsg = RequestHelper.MSG_NOT_AUTHENTICATED;
            logger.error(errorMsg);
            logout(helper.getRequest());
            helper.getRequest().setAttribute(RequestHelper.KEY_AUTH_ERROR_MSG, errorMsg);
            next = MyDispatcher.AUTH_ERROR_PAGE;
        } else {
            HttpSession session = request.getSession(false);

            // profile already read in session ?
            UserBean user = (UserBean) session.getAttribute(UserBean.KEY_PROFILE);
            if (user == null) {
                //No : we try to read from dB and put in session
                user = helper.readUserProfile(userKey);
                if (user != null) {
                    // successfull read from DB
                    if (RequestHelper.isGranted(user)) {
                        ServletContext appContext = getServletContext();
                        checkAndCacheMapInAppContext(appContext);
                        // ILN is null at creation time, must be filled, read from cache in webapp context
                        Map<String, Integer> mainRcrToIln = (Map<String, Integer>) appContext
                                .getAttribute(RequestHelper.KEY_MAP_ISMAIN_RCR_TO_ILN);
                        Map<String, Integer> notMainRcrToIln = (Map<String, Integer>) appContext
                                .getAttribute(RequestHelper.KEY_MAP_NOTMAIN_RCR_TO_ILN);

                        Integer iln = null;

                        if (RequestHelper.isCentreRegional(user)) {
                            iln = mainRcrToIln.get(user.getLibrary());
                        } else {
                            iln = notMainRcrToIln.get(user.getLibrary());
                            if (iln == null && RequestHelper.isManager(user)) {
                                //Some manager accounts are attached to main libraries
                                iln = mainRcrToIln.get(user.getLibrary());
                            }
                        }

                        if (iln == null) {
                            // handle NPE on unpredictable login policy
                            String errorMsg = RequestHelper.MSG_UNPREDICTABLE_POLICY
                                    + " : " + user;
                            logger.warn(errorMsg);
                            logout(helper.getRequest());
                            helper.getRequest().setAttribute(
                                    RequestHelper.KEY_AUTH_ERROR_MSG, errorMsg);
                            next = MyDispatcher.AUTH_ERROR_PAGE;

                        } else {
                            // all checks are OK !
                            user.setIln(iln);
                            // we put user in session for caching
                            session.setAttribute(UserBean.KEY_PROFILE, user);
                            //no need to ask for terms for administrators and managers, always accepted
                            if (RequestHelper.isAdmin(user) || RequestHelper.isManager(user)) {
                                session.setAttribute(RequestHelper.KEY_ACCEPTTERMS, true);
                            }
                        }

                    } else {
                        //user not granted
                        String errorMsg = RequestHelper.MSG_NOT_GRANTED + " : " + user;
                        logger.warn(errorMsg);
                        logout(helper.getRequest());
                        helper.getRequest().setAttribute(RequestHelper.KEY_AUTH_ERROR_MSG, errorMsg);
                        next = MyDispatcher.AUTH_ERROR_PAGE;
                    }

                } else {
                    // something wrong, we can't read the profile
                    ServletException e = new ServletException(
                            "Can't read user profile  !");
                    logger.error(e.getMessage());
                    throw e;
                }
            }
        }

        ICommand command = null;
        if (!MyDispatcher.AUTH_ERROR_PAGE.equals(next)) {
            command = helper.getCommand();
            next = command.execute(helper);
        }

        logger.debug("next=" + next);

        if (next != null) {
            if (MyDispatcher.AUTH_ERROR_PAGE.equals(next)) {
                RequestHelper.setResponseUnauthorizedHTML(helper.getResponse());
            } else {
                RequestHelper.setResponseOkHTML(helper.getResponse());
            }
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher(next);
            dispatcher.forward(request, helper.getResponse());
        }
    }
}
