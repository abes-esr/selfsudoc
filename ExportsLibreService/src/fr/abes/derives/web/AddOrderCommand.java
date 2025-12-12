package fr.abes.derives.web;

import fr.abes.derives.connection.QueryProduitsDerives;
import fr.abes.utils.LogHelper;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class AddOrderCommand implements ICommand {

    private static LogHelper logger = new LogHelper(AddOrderCommand.class);

    private Integer secretCandidate = null;

    final static String RCR_FILE_PREFIX = "notices";

    private int iln = 0;
    private Map<String, Set<String>> rcrS = null; //store RCRs dispatched by key=final format (pdf, rtf...)
    private boolean withCollections = false;
    private Map<String, Set<String>> excludedDataFields = null;
    private Map<String, String> pivotLayouts = null;
    private Map<String, String> sylkLayouts = null;

    private Map<String, Integer> lowLimitMap = null;
    private Map<String, Integer> highLimitMap = null;

    Map<String, String> shortNamesMap = null;

    private DataSource produitsDerivesDataSource = null;

    /**
     *
     * if errorsRCR is empty returns dispatcher.dispatch(this),
     * else update status with exception in database, report errors in html response and returns null
     *
     * @param errorsRCR
     * @param uniqueExportID
     * @param helper
     * @param connProduitsDerives
     * @return
     * @throws SQLException
     * @throws IOException
     */
    private String reportErrors(
            Map<String, Map<String, String>> errorsRCR,
            long uniqueExportID,
            RequestHelper helper,
            Connection connProduitsDerives
    ) throws SQLException, IOException {

        if (errorsRCR.size() > 0) {
            Map<String, String> errorsPDF = errorsRCR.get(RequestHelper.PDF);
            Map<String, String> errorsRTF = errorsRCR.get(RequestHelper.RTF);
            Map<String, String> errorsSYLK = errorsRCR.get(RequestHelper.SYLK);

            if (errorsPDF.size() <= 0 && errorsRTF.size() <= 0 && errorsSYLK.size() <= 0) {
                //empty errors Map = all committed ok
                MyDispatcher dispatcher = new MyDispatcher();
                return dispatcher.dispatch(this);
            } else {
                // store extract exception for rcr
                if (errorsPDF.size() > 0) {
                    connProduitsDerives.setAutoCommit(false);
                    QueryProduitsDerives.reportExportErrors(uniqueExportID,
                            errorsPDF, RequestHelper.PDF, connProduitsDerives);
                    connProduitsDerives.commit();
                }
                if (errorsRTF.size() > 0) {
                    connProduitsDerives.setAutoCommit(false);
                    QueryProduitsDerives.reportExportErrors(uniqueExportID,
                            errorsRTF, RequestHelper.RTF, connProduitsDerives);
                    connProduitsDerives.commit();
                }
                if (errorsSYLK.size() > 0) {
                    connProduitsDerives.setAutoCommit(false);
                    QueryProduitsDerives
                            .reportExportErrors(uniqueExportID, errorsSYLK,
                                    RequestHelper.SYLK, connProduitsDerives);
                    connProduitsDerives.commit();
                }

                logger.warn("reporting errors while extract from SudocXML !!!");
                // report errors
                StringBuilder sb = new StringBuilder();
                sb
                        .append("<h1>reporting errors while extract from SudocXML !!!</h1>");
                sb.append("<div>");

                for (Iterator<String> iterator = errorsPDF.keySet().iterator(); iterator
                        .hasNext(); ) {
                    String badRCR = iterator.next();
                    sb.append("bad extracted rcr (for PDF format) : ").append(badRCR).append(
                            " error=").append(errorsPDF.get(badRCR)).append(
                            "<br>");
                }

                for (Iterator<String> iterator = errorsRTF.keySet().iterator(); iterator
                        .hasNext(); ) {
                    String badRCR = iterator.next();
                    sb.append("bad extracted rcr (for RTF format) : ").append(
                            badRCR).append(" error=").append(
                            errorsRTF.get(badRCR)).append("<br>");
                }

                for (Iterator<String> iterator = errorsSYLK.keySet().iterator(); iterator
                        .hasNext(); ) {
                    String badRCR = iterator.next();
                    sb.append("bad extracted rcr (for SYLK format) : ").append(
                            badRCR).append(" error=").append(
                            errorsSYLK.get(badRCR)).append("<br>");
                }

                sb.append("</div>");
                // TODO TMX : put error map in attribute response to display
                RequestHelper.errorFlushNotOkHTML(helper.getResponse(), new Exception(sb.toString()));
                return null;
            }
        } else {
            //empty errors Map = all committed ok
            MyDispatcher dispatcher = new MyDispatcher();
            return dispatcher.dispatch(this);
        }
    }

    public AddOrderCommand(
            int iln,
            Map<String, Set<String>> rcrS,
            boolean withCollections,
            Map<String, String> pivotLayouts,
            Map<String, String> sylkLayouts,
            Map<String, Set<String>> excludedDataFields,
            Map<String, Integer> lowLimitMap,
            Map<String, Integer> highLimitMap,
            Map<String, String> shortNamesMap,
            DataSource produitsDerivesDataSource
    ) {
        super();
        this.iln = iln;
        this.rcrS = rcrS;
        this.withCollections = withCollections;
        this.produitsDerivesDataSource = produitsDerivesDataSource;
        this.excludedDataFields = excludedDataFields;
        this.pivotLayouts = pivotLayouts;
        this.sylkLayouts = sylkLayouts;
        this.lowLimitMap = lowLimitMap;
        this.highLimitMap = highLimitMap;
        this.shortNamesMap = shortNamesMap;
    }

    private static void release(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public String execute(RequestHelper helper) throws javax.servlet.ServletException, java.io.IOException {

        logger.debug(helper, "AddCommand");

        HttpSession session = helper.getRequest().getSession();

        //TODO TMX : generaliser le partage du challenge token ? toutes les actions qui modifient qqch en BDD
        if (this.secretCandidate == null) {
            logger.warn("secretCandidate is null");
        } else {
            logger.debug("secretCandidate=", Integer.toString(this.secretCandidate));
            Integer sharedSecret = (Integer) session.getAttribute(RequestHelper.KEY_SHAREDSECRET);

            if (!sharedSecret.equals(this.secretCandidate)) {
                // shared is not the same !
                logger.warn("secretCandidate is not sharedSecret !");
            } else {
                logger.info("secret is shared, ordering command accepted");
                // DO WRITING THINGS HERE !!!
            }
        }

        UserBean user = (UserBean) session.getAttribute(UserBean.KEY_PROFILE);
        String coordinateur = user.getUserKey();
        logger.debug("coordinateur=" + coordinateur);

        if (this.iln == 0) {
            RequestHelper.errorFlushNotOkHTML(helper.getResponse(), new IllegalArgumentException("Wrong ILN : 0"));
            return null;
        }

        Set<String> rcrPDF = null;
        Set<String> rcrRTF = null;
        Set<String> rcrSYLK = null;
        Set<String> rcrNoDupAllFormats = null;

        if (this.rcrS == null || this.rcrS.size() <= 0) {
            RequestHelper.errorFlushNotOkHTML(helper.getResponse(), new IllegalArgumentException("RCR is undefined"));
            return null;
        } else {
            rcrPDF = this.rcrS.get(RequestHelper.PDF);
            rcrRTF = this.rcrS.get(RequestHelper.RTF);
            rcrSYLK = this.rcrS.get(RequestHelper.SYLK);
            rcrNoDupAllFormats = new HashSet<String>();
            rcrNoDupAllFormats.addAll(rcrPDF);
            rcrNoDupAllFormats.addAll(rcrRTF);
            rcrNoDupAllFormats.addAll(rcrSYLK);

            if (rcrNoDupAllFormats.size() <= 0) {
                RequestHelper.errorFlushNotOkHTML(helper.getResponse(), new IllegalArgumentException("RCR is undefined"));
                return null;
            }

            // gala #1528 : check if rcrS'ILN is the same than user in session !
            boolean checked = true;

            ServletContext appContext = session.getServletContext();
            Map<String, Integer> mainRcrToIln = (Map<String, Integer>) appContext
                    .getAttribute(RequestHelper.KEY_MAP_ISMAIN_RCR_TO_ILN);
            Map<String, Integer> notMainRcrToIln = (Map<String, Integer>) appContext
                    .getAttribute(RequestHelper.KEY_MAP_NOTMAIN_RCR_TO_ILN);

            for (Iterator<String> iterator = rcrNoDupAllFormats.iterator(); iterator
                    .hasNext(); ) {
                String rcrToCheck = iterator.next();
                Integer ilnFromBasket = mainRcrToIln.get(rcrToCheck);
                if (ilnFromBasket == null) {
                    ilnFromBasket = notMainRcrToIln.get(rcrToCheck);
                }

                if (ilnFromBasket == null || !ilnFromBasket.equals(this.iln)) {
                    checked = false;
                }
            }

            if (!checked) {
                // basket is incoherent with user in session we redirect to homepage
                if (!(RequestHelper.isAdmin(user) || RequestHelper.isManager(user))) {
                    session.setAttribute(RequestHelper.KEY_ACCEPTTERMS, null);
                }
                RequestDispatcher dispatcher = appContext
                        .getRequestDispatcher("/");
                dispatcher.forward(helper.getRequest(), helper.getResponse());

                return null;
            }
        }

        if ((this.pivotLayouts == null && (rcrPDF.size() + rcrRTF.size()) > 0)
                || (this.sylkLayouts == null && rcrSYLK.size() > 0)) {
            RequestHelper.errorFlushNotOkHTML(helper.getResponse(), new IllegalArgumentException("layouts MUST not be null !"));
            return null;
        }

        logger.debug(helper, "number of pdf=" + rcrPDF.size() + " rtf=" + rcrRTF.size() + " sylk=" + rcrSYLK.size() + " " + RequestHelper.PARAMETER_WITHCOLLECTIONS + "=" + this.withCollections);

        //TODO TMX : change to get synchronized sequence ID from DB
        long uniqueExportID = System.currentTimeMillis();

        //String runtimeDir = helper.getRuntimeDir();

        Connection connProduitsDerives = null;

        //Map of maps  : format->rcr->message
        Map<String, Map<String, String>> errorsRCR = new HashMap<String, Map<String, String>>();
        errorsRCR.put(RequestHelper.PDF, new HashMap<String, String>());
        errorsRCR.put(RequestHelper.RTF, new HashMap<String, String>());
        errorsRCR.put(RequestHelper.SYLK, new HashMap<String, String>());

        try {
            if (this.produitsDerivesDataSource != null) {
                connProduitsDerives = this.produitsDerivesDataSource.getConnection(ConnectionServlet.getLoginProduitsDerives(), ConnectionServlet.getPasswordProduitsDerives());
            }

            //TODO TMX : add function variables

            //track in database the upcoming Export

            if (rcrPDF.size() > 0) {
                connProduitsDerives.setAutoCommit(false);
                QueryProduitsDerives.prepareExport(this.iln, uniqueExportID, rcrPDF, this.withCollections, RequestHelper.PDF, coordinateur, excludedDataFields, this.pivotLayouts, this.lowLimitMap, this.highLimitMap, this.shortNamesMap, connProduitsDerives);
                connProduitsDerives.commit();
            }
            if (rcrRTF.size() > 0) {
                connProduitsDerives.setAutoCommit(false);
                QueryProduitsDerives.prepareExport(this.iln, uniqueExportID, rcrRTF, this.withCollections, RequestHelper.RTF, coordinateur, excludedDataFields, this.pivotLayouts, this.lowLimitMap, this.highLimitMap, this.shortNamesMap, connProduitsDerives);
                connProduitsDerives.commit();
            }
            if (rcrSYLK.size() > 0) {
                connProduitsDerives.setAutoCommit(false);
                QueryProduitsDerives.prepareExport(this.iln, uniqueExportID, rcrSYLK, this.withCollections, RequestHelper.SYLK, coordinateur, excludedDataFields, this.sylkLayouts, this.lowLimitMap, this.highLimitMap, this.shortNamesMap, connProduitsDerives);
                connProduitsDerives.commit();
            }

            helper.getRequest().setAttribute(RequestHelper.ATTRIBUTE_ORDER_ID, uniqueExportID);

            String r = reportErrors(errorsRCR, uniqueExportID, helper, connProduitsDerives);
            return r;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new ServletException(e);
        } finally {
            release(connProduitsDerives);
            connProduitsDerives = null;
        }
    }
}
