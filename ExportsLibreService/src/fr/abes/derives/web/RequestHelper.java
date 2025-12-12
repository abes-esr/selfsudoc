package fr.abes.derives.web;

import fr.abes.derives.connection.ConnectionHelper;
import fr.abes.derives.connection.QueryProduitsDerives;
import fr.abes.utils.BufferedRW;
import fr.abes.utils.LogHelper;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.*;

public class RequestHelper {

    public final static String URL_ADD = "/add";
    public final static String URL_LISTFILES = "/listfiles";
    public final static String URL_LISTFILESJSON = "/listfilesJSON";
    public final static String URL_LISTLIBRARIESJSON = "/listlibrariesJSON";
    public final static String URL_ORDERSJSON = "/ordersJSON";
    public final static String URL_WELCOME = "/welcome";
    public final static String URL_LOGOUT = "/logout";
    public final static String URL_ACCEPTTERMS = "/acceptterms";
    public final static String PARAMETER_ILN = "iln";
    public final static String PARAMETER_WITHCOLLECTIONS = "withCollections";
    public final static String PARAMETER_RCRFORMATS = "rcrformats";
    public final static String PARAMETER_EXCLUDED = "excluded";
    public final static String PARAMETER_PIVOT_LAYOUT = "pivotlayout";
    public final static String PARAMETER_SYLK_LAYOUT = "sylklayout";
    public final static String PARAMETER_QUERY_START = "start";
    public final static String PARAMETER_QUERY_COUNT = "count";
    public final static String PARAMETER_SORTORDERBY = "sort";
    public final static String PARAMETER_SECRETCANDIDATE = "s";
    public final static String ATTRIBUTE_ORDER_ID = "orderid";
    public final static String PARAMETER_PREFIX_LOW = "lowlimit";
    public final static String PARAMETER_PREFIX_HIGH = "highlimit";
    public final static int DEFAULT_HIGH_LIMIT = 26000;
    public final static int DEFAULT_LOW_LIMIT = 0;
    public final static int MAX_SIZE = DEFAULT_HIGH_LIMIT - DEFAULT_LOW_LIMIT;
    public final static String KEY_MAP_NOTMAIN_RCR_TO_ILN = "NOTMAINRCRTOILNKEY";
    public final static String KEY_MAP_ISMAIN_RCR_TO_ILN = "ISMAINRCRTOILNKEY";
    public final static String KEY_MAP_LAYOUTS = "LAYOUTSKEY";
    public final static String KEY_PROPS_COUNTS_BY_RCR = "PROPSCOUNTRCRKEY";
    public final static String KEY_AUTH_ERROR_MSG = "AUTHERRORMSG";
    public final static String KEY_MAP_EXCLUSIONS = "EXLCUSIONSKEY";
    public final static String KEY_MAP_SHORTNAMES = "SHORTNAMES";
    public final static String KEY_ACCEPTTERMS = "ACCEPTTERMS";
    public final static String KEY_SHAREDSECRET = "SHAREDSECRET";
    public final static String KEY_PROPS_LOCALHOSTNAME = "LOCALHOSTNAME";
    public final static String MSG_NOT_AUTHENTICATED = "Not Authenticated !";
    public final static String MSG_NOT_GRANTED = "User not Granted !";
    public final static String MSG_UNPREDICTABLE_POLICY = "Your account as wrong policy !";
    public final static String MSG_SESSION_EXPIRED = "Session expired !";
    public final static String MSG_NOT_ADMINISTRATOR = "You must be administrator !";
    public final static String PDF = "pdf";
    public final static String RTF = "rtf";
    public final static String SYLK = "slk";
    public final static String PDFRTF = PDF + RTF;
    public final static String DOWNLOAD_URI = "/exportsdemandes/download/";
    final static String DOCTYPE = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">";
    final static String META = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">";
    private static LogHelper logger = new LogHelper(RequestHelper.class);
    private String runtimeDir = null;
    private DataSource produitsDerivesDataSource = null;
    private DataSource sybaseILNDataSource = null;
    private HttpServletRequest request = null;
    private HttpServletResponse response = null;

    public RequestHelper(HttpServletRequest request, HttpServletResponse response) {
        super();
        this.request = request;
        this.response = response;
    }

    public static String getUserKey(HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        // check exists session without creating a new one
        if (session == null) {
            logger.warn("session is null !");
            return null;
        } else {
            // we are in session
            logger.debug("httpSession=" + session);
            String userKey = (String) session.getAttribute(UserBean.KEY_USER);
            logger.debug("userKey from session =" + userKey);
            if (userKey == null) {
                // first
                userKey = request.getRemoteUser();
                logger.debug("first time (not in session) get userKey from getRemoteUser =" + userKey);
                if (userKey == null) {
                    logger.error("something went wrong with authentication");
                    return null;
                } else {
                    session.setAttribute(UserBean.KEY_USER, userKey);
                    logger.debug("put in session userKey " + userKey);
                    return userKey;
                }
            } else {
                return userKey; // from session
            }
        }
    }

    /**
     *
     * @param ds
     * @param isMain if true then read MAIN='Y' RCR table content from DB else read only non main libraries
     * @return
     */
    protected static Map<String, Integer> mapRCRToILNfromDB(DataSource ds, boolean isMain) {
        //TODO TMX : add a refresh method to force re-read from DB

        Connection connSybaseCBS = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        SimpleImmutableEntry<PreparedStatement, ResultSet> pair = null;

        Map<String, Integer> result = new HashMap<String, Integer>();

        try {
            if (ds != null) {
                connSybaseCBS = ds.getConnection();
                connSybaseCBS.setAutoCommit(true);
            }

            pair = QueryProduitsDerives.listRCRToILN(isMain, connSybaseCBS);
            resultSet = pair.getValue();
            statement = pair.getKey();

            while (resultSet.next()) {
                String libraryRCR = resultSet.getString("rcr");
                libraryRCR = (libraryRCR == null) ? null : libraryRCR.trim(); // trim
                int libraryILN = resultSet.getInt("iln");
                result.put(libraryRCR, libraryILN);
            }

            return result;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return null;
        } finally {
            ConnectionHelper.release(resultSet, statement);
            resultSet = null;
            statement = null;

            try {
                if (connSybaseCBS != null) {
                    connSybaseCBS.close();
                }
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
            connSybaseCBS = null;
        }
    }

    /**
     * read EXCLUSION table content from DB
     */
    protected static Map<String, String> mapExclusionsfromDB(DataSource produitsDerivesDataSource) {
        //TODO TMX : add a refresh method to force re-read from DB

        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        SimpleImmutableEntry<PreparedStatement, ResultSet> pair = null;

        //Implementation insure sort on keys
        SortedMap<String, String> result = new TreeMap<String, String>();

        try {
            if (produitsDerivesDataSource != null) {
                conn = produitsDerivesDataSource.getConnection(ConnectionServlet.getLoginProduitsDerives(), ConnectionServlet.getPasswordProduitsDerives());
            }

            conn.setAutoCommit(false);
            pair = QueryProduitsDerives.listEXCLUSION(conn);
            resultSet = pair.getValue();
            statement = pair.getKey();

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String url = resultSet.getString("url");
                result.put(id, url);
            }

            conn.commit();
            return result;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return null;
        } finally {
            ConnectionHelper.release(resultSet, statement);
            resultSet = null;
            statement = null;

            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
            conn = null;
        }
    }

    /**
     * read table content from DB
     * <p>
     * Map format->XSL->Label
     */
    protected static Map<String, Map<String, String>> mapLayoutsfromDB(DataSource produitsDerivesDataSource) {
        //TODO TMX : add a refresh method to force re-read from DB

        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        SimpleImmutableEntry<PreparedStatement, ResultSet> pair = null;

        Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();

        Map<String, String> sylkLayouts = new HashMap<String, String>();
        Map<String, String> pivotLayouts = new HashMap<String, String>();

        try {
            if (produitsDerivesDataSource != null) {
                conn = produitsDerivesDataSource.getConnection(ConnectionServlet.getLoginProduitsDerives(), ConnectionServlet.getPasswordProduitsDerives());
            }

            conn.setAutoCommit(false);
            pair = QueryProduitsDerives.listLayouts(conn);
            resultSet = pair.getValue();
            statement = pair.getKey();

            while (resultSet.next()) {
                String key = resultSet.getString("XSL_STYLESHEET");
                String label = resultSet.getString("LIBELLE");
                String outputFormat = resultSet.getString("OUTPUT_FORMAT");

                if (SYLK.equals(outputFormat)) {
                    sylkLayouts.put(key, label);
                } else {
                    pivotLayouts.put(key, label);
                }
            }

            conn.commit();

            result.put(SYLK, sylkLayouts);
            result.put(PDFRTF, pivotLayouts);

            return result;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return null;
        } finally {
            ConnectionHelper.release(resultSet, statement);
            resultSet = null;
            statement = null;

            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
            conn = null;
        }
    }

    /**
     *
     * @param ds
     * @return map RCR->short_name
     */
    protected static Map<String, String> mapRCRToShortNames(DataSource ds) {

        logger.debug("mapRCRToShortNames");

        Connection connSybaseCBS = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        SimpleImmutableEntry<PreparedStatement, ResultSet> pair = null;

        Map<String, String> result = new HashMap<String, String>();

        try {
            if (ds != null) {
                connSybaseCBS = ds.getConnection();
                connSybaseCBS.setAutoCommit(true);
            }

            pair = QueryProduitsDerives.listRCRToILN(false, connSybaseCBS);
            resultSet = pair.getValue();
            statement = pair.getKey();

            while (resultSet.next()) {
                String libraryRCR = resultSet.getString("rcr");
                libraryRCR = (libraryRCR == null) ? null : libraryRCR.trim(); // trim
                String libraryShortName = resultSet.getString("short_name");
                result.put(libraryRCR, libraryShortName);
            }

            return result;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return null;
        } finally {
            ConnectionHelper.release(resultSet, statement);
            resultSet = null;
            statement = null;

            try {
                if (connSybaseCBS != null) {
                    connSybaseCBS.close();
                }
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
            connSybaseCBS = null;
        }
    }

    /**
     *
     * @param user
     * @return (user_group='coordinateur', user_key format 0..XX.... or 1..XX... , login_allowed='Y')
     */
    public static boolean isCoordinateur(UserBean user) {
        String sGroup = user.getUserGroup().trim().toUpperCase();
        String sLoginAllowed = user.getLoginAllowed().trim().toUpperCase();
        String sUserKey = user.getUserKey().trim().toUpperCase();
        boolean coordinateur = "COORDINATEUR".equals(sGroup)
                && sLoginAllowed.startsWith("Y") && sUserKey.contains("XX")
                && (sUserKey.startsWith("0") || sUserKey.startsWith("1") || sUserKey.startsWith("4"));
        logger.debug("coordinateur = " + coordinateur);
        return (coordinateur);
    }

    /**
     *
     * @param user
     * @return (user_group='crcat', user_key format 2..XX..., login_allowed='Y')
     */
    public static boolean isCentreRegional(UserBean user) {
        String sGroup = user.getUserGroup().trim().toUpperCase();
        String sLoginAllowed = user.getLoginAllowed().trim().toUpperCase();
        String sUserKey = user.getUserKey().trim().toUpperCase();
        boolean centreRegional = "CRCAT".equals(sGroup)
                && sLoginAllowed.startsWith("Y") && sUserKey.contains("XX")
                && sUserKey.startsWith("2");
        logger.debug("centreRegional = " + centreRegional);
        return (centreRegional);
    }

    /**
     *
     * @param user
     * @return
     */
    public static boolean isAdmin(UserBean user) {
        boolean admin = user.isAdmin();
        logger.debug("admin = " + admin);
        return (admin);
    }

    public static boolean isManager(UserBean user) {
        String sUserKey = user.getUserKey().trim().toUpperCase();
        String sLoginAllowed = user.getLoginAllowed().trim().toUpperCase();
        boolean manager = sUserKey.startsWith("M") && sLoginAllowed.startsWith("Y");
        logger.debug("manager = " + manager);
        return (manager);
    }

    /**
     * Check authorizations : allow coordinateurs, centre regionaux and ABES (ILN=001) members
     *
     * @param user
     * @return coordinateur or centre regional or user_key format 001....
     */
    public static boolean isGranted(UserBean user) {
        String sUserKey = user.getUserKey().trim().toUpperCase();
        logger.debug("sUserKey = " + sUserKey);
        boolean granted = isCoordinateur(user) || isCentreRegional(user) || isAdmin(user) || isManager(user);
        logger.debug("granted = " + granted);
        return (granted);
    }

    public static void flushOkJSON(HttpServletResponse response, JSONArray jsonArray) throws IOException {
        String s = jsonArray.toString();
        jsonArray = null;
        setResponseOkJSON(response);
        response.getWriter().write(s);
        response.getWriter().flush();
    }

    public static void flushOkJSON(HttpServletResponse response, JSONObject jsonObject) throws IOException {
        String s = jsonObject.toString();
        setResponseOkJSON(response);
        response.getWriter().write(s);
        response.getWriter().flush();
    }

    public static void errorFlushNotOkJSON(HttpServletResponse response, Exception e) throws IOException {
        setResponseNotOkJSON(response);
        response.getWriter().write(e.getMessage());
        response.getWriter().flush();
    }

    public static void errorFlushNotOkHTML(HttpServletResponse response, Exception e) throws IOException {
        setResponseNotOkHTML(response);
        response.getWriter().write(e.getMessage());
        response.getWriter().flush();
    }

    public static void errorUnauthorizedHTML(HttpServletResponse response, Exception e) throws IOException {
        setResponseUnauthorizedHTML(response);
        response.getWriter().write(e.getMessage());
        response.getWriter().flush();
    }

    public static void errorUnauthorizedJSON(HttpServletResponse response, Exception e) throws IOException {
        setResponseUnauthorizedJSON(response);
        response.getWriter().write(e.getMessage());
        response.getWriter().flush();
    }

    private static void setResponseOkJSON(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding(BufferedRW.UTF8);
        response.setContentType("text/json;charset=" + BufferedRW.UTF8);
        response.setHeader("Cache-Control", "no-store");
        response.addHeader("Cache-Control", "no-cache");
        response.addHeader("Cache-Control", "max-age=0");
        response.setDateHeader("Expires", 0);

    }

    private static void setResponseNotOkJSON(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setCharacterEncoding(BufferedRW.UTF8);
        response.setContentType("text/json;charset=" + BufferedRW.UTF8);
        response.setHeader("Cache-Control", "no-store");
        response.addHeader("Cache-Control", "no-cache");
        response.addHeader("Cache-Control", "max-age=0");
        response.setDateHeader("Expires", 0);
    }

    private static void setResponseNotOkHTML(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setCharacterEncoding(BufferedRW.UTF8);
        response.setContentType("text/html;charset=" + BufferedRW.UTF8);
    }

    protected static void setResponseOkHTML(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding(BufferedRW.UTF8);
        response.setContentType("text/html;charset=" + BufferedRW.UTF8);
    }

    private static void setResponseOkXML(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding(BufferedRW.UTF8);
        response.setContentType("application/xml;charset=" + BufferedRW.UTF8);
        response.setHeader("Cache-Control", "no-store");
        response.addHeader("Cache-Control", "no-cache");
        response.addHeader("Cache-Control", "max-age=0");
        response.setDateHeader("Expires", 0);
    }

    /**
     * method must be kept with public visibility, because it's called from checkgranted.jsp
     *
     * @param response
     * @throws IOException
     */
    public static void setResponseUnauthorizedHTML(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(BufferedRW.UTF8);
        response.setContentType("text/html;charset=" + BufferedRW.UTF8);
    }

    private static void setResponseUnauthorizedJSON(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(BufferedRW.UTF8);
        response.setContentType("text/json;charset=" + BufferedRW.UTF8);
        response.setHeader("Cache-Control", "no-store");
        response.addHeader("Cache-Control", "no-cache");
        response.addHeader("Cache-Control", "max-age=0");
        response.setDateHeader("Expires", 0);
    }

    public String[][] debug(HttpServlet servlet) {

        String[][] variables =
                {{"AUTH_TYPE", request.getAuthType()},
                        {"CONTENT_LENGTH", String.valueOf(request.getContentLength())},
                        {"CONTENT_TYPE", request.getContentType()},
                        {"DOCUMENT_ROOT", servlet.getServletContext().getRealPath("/")},
                        {"PATH_INFO", request.getPathInfo()},
                        {"PATH_TRANSLATED", request.getPathTranslated()},
                        {"QUERY_STRING", request.getQueryString()},
                        {"REMOTE_ADDR", request.getRemoteAddr()},
                        {"REMOTE_HOST", request.getRemoteHost()},
                        {"REMOTE_USER", request.getRemoteUser()},
                        {"REQUEST_METHOD", request.getMethod()},
                        {"SCRIPT_NAME", request.getServletPath()},
                        {"SERVER_NAME", request.getServerName()},
                        {"SERVER_PORT", String.valueOf(request.getServerPort())},
                        {"SERVER_PROTOCOL", request.getProtocol()},
                        {"SERVER_SOFTWARE", servlet.getServletContext().getServerInfo()}
                };

        return variables;
    }

    protected UserBean readUserProfile(String userKey) {

        logger.debug("readUserProfile");

        Connection connSybaseCBS = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        SimpleImmutableEntry<PreparedStatement, ResultSet> pair = null;

        UserBean user = null;

        try {
            if (this.sybaseILNDataSource != null) {
                connSybaseCBS = this.sybaseILNDataSource.getConnection();
                connSybaseCBS.setAutoCommit(true);
            }

            pair = QueryProduitsDerives.readUserProfile(userKey, connSybaseCBS);
            resultSet = pair.getValue();
            statement = pair.getKey();

            if (resultSet.next()) {

                Integer userNum = resultSet.getInt("user_num");
                String shortName = resultSet.getString("short_name");
                String userGroup = resultSet.getString("user_group");
                String libraryRCR = resultSet.getString("library");
                libraryRCR = (libraryRCR == null) ? null : libraryRCR.trim(); // trim
                String loginAllowed = resultSet.getString("login_allowed");
                //ILN is null at creation time, must be filled later
                user = new UserBean(userKey, userNum, shortName, userGroup, libraryRCR, loginAllowed, null);
                return user;

            } else {
                //user_key not found in DB
                return null;
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return null;

        } finally {
            ConnectionHelper.release(resultSet, statement);
            resultSet = null;
            statement = null;

            try {
                if (connSybaseCBS != null) {
                    connSybaseCBS.close();
                }
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
            connSybaseCBS = null;
        }
    }

    /**
     *
     * Map final format -> RCRs
     *
     * @param formatSToTrim
     * @return
     */
    private Map<String, Set<String>> rcrSArrayToMap(String[] formatSToTrim) {

        Map<String, Set<String>> rcrS = new HashMap<String, Set<String>>();

        Set<String> rcrPDF = new HashSet<String>();
        Set<String> rcrRTF = new HashSet<String>();
        Set<String> rcrSYLK = new HashSet<String>();

        if (formatSToTrim != null) {
            for (int i = 0; i < formatSToTrim.length; i++) {
                String finalFormat = (formatSToTrim[i] == null) ? null : formatSToTrim[i].trim();
                if (finalFormat != null) {

                    String ext = BufferedRW.extension(finalFormat); //dispatch
                    int s = finalFormat.length();
                    int e = ext.length();
                    String rcr = finalFormat.substring(0, s - (1 + e)); //remove 1+e char = .size of extension
                    if (RequestHelper.PDF.equals(ext)) {
                        rcrPDF.add(rcr);
                    }
                    if (RequestHelper.RTF.equals(ext)) {
                        rcrRTF.add(rcr);
                    }
                    if (RequestHelper.SYLK.equals(ext)) {
                        rcrSYLK.add(rcr);
                    }
                }
            }
            rcrS.put(RequestHelper.PDF, rcrPDF);
            rcrS.put(RequestHelper.RTF, rcrRTF);
            rcrS.put(RequestHelper.SYLK, rcrSYLK);
        } else {
            rcrS = null;
        }

        return rcrS;
    }

    /**
     *
     * Map RCR->excluded data fields
     *
     * @param excludedS
     * @return
     */
    private Map<String, Set<String>> excludedSArrayToMap(String[] excludedS) {

        Map<String, Set<String>> excludedDataFields = new HashMap<String, Set<String>>();

        if (excludedS != null) {
            for (int i = 0; i < excludedS.length; i++) {
                String exRcrDataFied = (excludedS[i] == null) ? null : excludedS[i].trim();
                if (exRcrDataFied != null) {

                    String dataField = BufferedRW.extension(exRcrDataFied); //dispatch
                    int s = exRcrDataFied.length();
                    int d = dataField.length();
                    String rcr = exRcrDataFied.substring(0, s - (1 + d)); //remove 1+d char = .size of datafieldID

                    Set<String> alreadyExcluded = excludedDataFields.get(rcr);
                    if (alreadyExcluded == null) {
                        // create
                        alreadyExcluded = new HashSet<String>();
                    }
                    alreadyExcluded.add(dataField);
                    excludedDataFields.put(rcr, alreadyExcluded);
                }
            }

        } else {
            excludedDataFields = null;
        }

        return excludedDataFields;
    }

    /**
     *
     * Map RCR->runTimeDir+"/"+layout
     *
     * @param layoutsArray
     * @return
     */
    private Map<String, String> layoutsArrayToMap(String[] layoutsArray, String runTimeDir) {

        Map<String, String> layouts = new HashMap<String, String>();

        if (layoutsArray != null) {
            for (int i = 0; i < layoutsArray.length; i++) {
                String layoutStr = (layoutsArray[i] == null) ? null
                        : layoutsArray[i].trim();
                if (layoutsArray != null) {

                    String rcr = BufferedRW.extension(layoutStr); // dispatch
                    int s = layoutStr.length();
                    int r = rcr.length();
                    String layout = layoutStr.substring(0, s - (1 + r)); // remove 1+r char = .size of RCR ID

                    layouts.put(rcr, runTimeDir + "/" + layout);
                }
            }

        } else {
            layouts = null;
        }

        return layouts;
    }

    public ICommand getCommand() {

        String path = request.getServletPath();

        logger.debug("servelt path = " + path);

        if (URL_WELCOME.equals(path)) {
            return new WelcomeCommand();
        }

        if (URL_ADD.equals(path)) {

            String[] formatSToTrim = request.getParameterValues(PARAMETER_RCRFORMATS);
            Map<String, Set<String>> rcrS = rcrSArrayToMap(formatSToTrim);

            boolean withCollections = false;
            String strWithCollections = request.getParameter(PARAMETER_WITHCOLLECTIONS);

            logger.debug("strWithCollections=" + strWithCollections);
            if (strWithCollections == null || "".equals(strWithCollections.trim())) {
                withCollections = false;
            } else {
                withCollections = Boolean.valueOf(strWithCollections);
            }
            logger.debug(PARAMETER_WITHCOLLECTIONS + "=" + withCollections);

            HttpSession session = request.getSession();
            UserBean user = (UserBean) session.getAttribute(UserBean.KEY_PROFILE);
            logger.debug("iln=" + user.getIln());

            Map<String, Integer> lowLimitMap = new HashMap<String, Integer>();
            Map<String, Integer> highLimitMap = new HashMap<String, Integer>();
            //collect limits per RCR if any
            Enumeration<String> allParNames = request.getParameterNames();
            logger.debug("allParams=" + allParNames);
            while (allParNames.hasMoreElements()) {

                String pName = allParNames.nextElement();

                if (pName.startsWith(PARAMETER_PREFIX_LOW)) {
                    String key = BufferedRW.extension(pName);
                    String lowLimitS = request.getParameter(pName);
                    int lowLimit = 0;
                    if (lowLimitS == null || "".equals(lowLimitS.trim())) {
                        lowLimit = 0;
                    } else {
                        lowLimit = Integer.valueOf(lowLimitS);
                    }
                    lowLimitMap.put(key, lowLimit);
                }
                if (pName.startsWith(PARAMETER_PREFIX_HIGH)) {

                    String key = BufferedRW.extension(pName);
                    String highLimitS = request.getParameter(pName);
                    int highLimit = 0;
                    if (highLimitS == null || "".equals(highLimitS.trim())) {
                        highLimit = 0;
                    } else {
                        highLimit = Integer.valueOf(highLimitS);
                    }
                    highLimitMap.put(key, highLimit);
                }
            }

            //TODO TMX : server side check for all entries of low and high limit that diff < MAX_SIZE or throw exception

            String[] excludedS = request.getParameterValues(PARAMETER_EXCLUDED);
            logger.debug("excludedDataFields=" + String.valueOf(excludedS));
            Map<String, Set<String>> excludedDataFields = excludedSArrayToMap(excludedS);
            logger.debug("excludedDataFields=" + excludedDataFields);

            String[] layoutS = request.getParameterValues(PARAMETER_PIVOT_LAYOUT);
            logger.debug("pivotLayouts=" + String.valueOf(layoutS));
            Map<String, String> pivotLayouts = layoutsArrayToMap(layoutS, this.runtimeDir);
            logger.debug("pivotLayouts=" + pivotLayouts);

            layoutS = request.getParameterValues(PARAMETER_SYLK_LAYOUT);
            logger.debug("sylkLayouts=" + String.valueOf(layoutS));
            Map<String, String> sylkLayouts = layoutsArrayToMap(layoutS, this.runtimeDir);
            logger.debug("sylkLayouts=" + sylkLayouts);

            ServletContext appContext = session.getServletContext();
            Map<String, String> shortNamesMap = (Map<String, String>) appContext.getAttribute(RequestHelper.KEY_MAP_SHORTNAMES);

            return new AddOrderCommand(user.getIln(), rcrS, withCollections, pivotLayouts, sylkLayouts, excludedDataFields, lowLimitMap, highLimitMap, shortNamesMap, this.produitsDerivesDataSource);
        }

        if (URL_LISTFILES.equals(path)) {
            return new ListFileSystemCommand();
        }

        if (URL_LISTFILESJSON.equals(path)) {
            return new ListFileJSONCommand();
        }

        if (URL_LISTLIBRARIESJSON.equals(path)) {
            HttpSession session = request.getSession();
            UserBean user = (UserBean) session.getAttribute(UserBean.KEY_PROFILE);
            logger.debug("iln=" + user.getIln());

            ServletContext appContext = session.getServletContext();
            Map<String, Long> countsByRCR = (Map<String, Long>) appContext.getAttribute(RequestHelper.KEY_PROPS_COUNTS_BY_RCR);

            return new ListRcrJSONCommand(user.getIln(), countsByRCR, this.sybaseILNDataSource);
        }

        if (URL_ORDERSJSON.equals(path)) {
            HttpSession session = request.getSession();
            UserBean user = (UserBean) session.getAttribute(UserBean.KEY_PROFILE);
            logger.debug("user.getIln()=" + user.getIln());
            //if we get a param like 'name' it means we have a query, otherwise we just check the status of the last order
            boolean isQuery = !(this.request.getParameter("name") == null);

            //pagination
            String strStart = request.getParameter(PARAMETER_QUERY_START);
            String strCount = request.getParameter(PARAMETER_QUERY_COUNT);
            int start = 0;
            if (strStart == null || "".equals(strStart.trim())) {
                start = 0;
            } else {
                start = Integer.valueOf(strStart);
            }
            int count = 0;
            if (strCount == null || "".equals(strCount.trim())) {
                count = 0;
            } else {
                count = Integer.valueOf(strCount);
            }

            //orderid
            String strOrderId = request.getParameter(ATTRIBUTE_ORDER_ID);
            Long orderId = null;
            if (strOrderId == null || "".equals(strOrderId.trim())) {
                orderId = null;
            } else {
                try {
                    orderId = Long.valueOf(strOrderId);
                } catch (NumberFormatException e) {
                    logger.error("silent NumberFormatException on orderId " + strOrderId + " : " + e.getMessage());
                    orderId = new Long(0);
                }
            }

            Integer iln = null;
            if (!isAdmin(user)) {
                iln = user.getIln(); //not admin = restrict query to user's ILN
            } else {
                //admin
                String strIln = request.getParameter(PARAMETER_ILN);
                //do we filter ?
                if (strIln == null || "".equals(strIln.trim())) {
                    iln = null; //no, will query all ILNs
                } else {
                    try {
                        iln = Integer.valueOf(strIln); //yes !
                    } catch (NumberFormatException e) {
                        logger.error("silent NumberFormatException on ILN " + strIln + " : " + e.getMessage());
                        iln = 0;
                    }
                }
            }

            String sortOrderBy = request.getParameter(PARAMETER_SORTORDERBY);

            return new OrdersJSONCommand(iln, orderId, isQuery, start, count, sortOrderBy, this.produitsDerivesDataSource);
        }

        if (URL_LOGOUT.equals(path)) {
            return new LogoutCommand();
        }

        if (URL_ACCEPTTERMS.equals(path)) {
            Integer secretCandidate = null;
            String strSecretCandidate = request.getParameter(PARAMETER_SECRETCANDIDATE);
            if (strSecretCandidate == null || "".equals(strSecretCandidate.trim())) {
                secretCandidate = null; //no, will query all ILNs
            } else {
                try {
                    secretCandidate = Integer.valueOf(strSecretCandidate); //yes !
                } catch (NumberFormatException e) {
                    logger.error("silent NumberFormatException on secretCandidate " + strSecretCandidate + " : " + e.getMessage());
                    secretCandidate = null;
                }
            }

            return new AcceptTermCommand(secretCandidate);
        }

        return null;
    }

    public DataSource getProduitsDerivesDataSource() {
        return produitsDerivesDataSource;
    }

    public void setProduitsDerivesDataSource(DataSource ds) {
        this.produitsDerivesDataSource = ds;
    }

    public void setSybaseILNDataSource(DataSource sybaseILNDataSource) {
        this.sybaseILNDataSource = sybaseILNDataSource;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public String getRuntimeDir() {
        return runtimeDir;
    }

    public void setRuntimeDir(String runtimeDir) {
        this.runtimeDir = runtimeDir;
    }

}
