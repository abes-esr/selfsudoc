package fr.abes.derives.web;

import fr.abes.derives.connection.ConnectionHelper;
import fr.abes.derives.connection.QueryProduitsDerives;
import fr.abes.utils.LogHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Date;

public class OrdersJSONCommand implements ICommand {

    private static LogHelper logger = new LogHelper(OrdersJSONCommand.class);

    private DataSource produitsDerivesDataSource = null;

    private Integer iln = null;
    private boolean isQuery = false;
    private int start = 0;
    private int count = 0;
    private Long orderId = null;
    private String sortOrderBy = null;

    public OrdersJSONCommand(Integer iln, Long orderId, boolean isQuery, int start, int count, String sortOrderBy, DataSource produitsDerivesDataSource) {
        super();
        this.iln = iln;
        this.produitsDerivesDataSource = produitsDerivesDataSource;
        this.isQuery = isQuery;
        this.start = start;
        this.count = count;
        this.orderId = orderId;
        this.sortOrderBy = sortOrderBy;
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

    public String execute(RequestHelper helper) throws ServletException, IOException {
        logger.info(helper, "OrdersJSONCommand");

        Connection connProduitsDerives = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        //needed if store request
        PreparedStatement statement2 = null;
        ResultSet resultSet2 = null;

        JSONArray orders = new JSONArray();

        try {
            if (this.produitsDerivesDataSource != null) {
                connProduitsDerives = this.produitsDerivesDataSource
                        .getConnection(ConnectionServlet.getLoginProduitsDerives(), ConnectionServlet.getPasswordProduitsDerives());
            }

            SimpleImmutableEntry<PreparedStatement, ResultSet> pair = null;

            connProduitsDerives.setAutoCommit(false);

            if (this.orderId != null) {
                // only this order to show status
                pair = QueryProduitsDerives.readJob(this.orderId, connProduitsDerives);
            } else {
                // historic view limited to this ILN
                pair = QueryProduitsDerives.listJobs(this.iln, start, count, this.sortOrderBy, connProduitsDerives);
            }
            resultSet = pair.getValue();
            statement = pair.getKey();

            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            while (resultSet.next()) {//! MUST BE ORDERED BY ID
                long jobExportID = resultSet.getLong("id");
                int jobILN = resultSet.getInt("iln");
                String jobRCR = resultSet.getString("rcr");
                String jobFormat = resultSet.getString("formatfinal");
                Date jobDateExtraction = resultSet.getTimestamp("dateextraction");
                int jobNbNotices = resultSet.getInt("nbnotices");
                String jobExceptionMsg = resultSet.getString("exceptionmsg");
                String jobEtatExtracted = resultSet.getString("etat_extracted");
                String jobEtatCleaned = resultSet.getString("etat_cleaned");
                String jobEtatGrouped = resultSet.getString("etat_grouped");
                String jobEtatFiltered = resultSet.getString("etat_filtered");
                String jobEtatSorted = resultSet.getString("etat_sorted");
                String jobEtatXHTML = resultSet.getString("etat_xhtml");
                String jobEtatPDF = resultSet.getString("etat_pdf");
                String jobEtatRTF = resultSet.getString("etat_rtf");
                String jobEtatSYLK = resultSet.getString("etat_slk");
                String jobMD5Hash = resultSet.getString("md5hash");
                String jobBytesLength = resultSet.getString("byteslength");

                int bytes = (jobBytesLength != null) ? Integer.valueOf(jobBytesLength) : 0;
                int kBytes = bytes / 1024;

                JSONObject obj = new JSONObject();

                obj.put("exportid", jobExportID);
                obj.put("iln", jobILN);
                obj.put("rcr", jobRCR);
                obj.put("format", jobFormat);
                obj.put("dateextraction", dateFormat.format(jobDateExtraction));
                obj.put("nbnotices", jobNbNotices);
                obj.put("exceptionmsg", jobExceptionMsg);
                obj.put("etatextracted", jobEtatExtracted);
                obj.put("etatcleaned", jobEtatCleaned);
                obj.put("etatgrouped", jobEtatGrouped);
                obj.put("etatfiltered", jobEtatFiltered);
                obj.put("etatsorted", jobEtatSorted);
                obj.put("etatxhtml", jobEtatXHTML);
                obj.put("etatpdf", jobEtatPDF);
                obj.put("etatrtf", jobEtatRTF);
                obj.put("etatslk", jobEtatSYLK);
                obj.put("md5hashwithextension", jobMD5Hash + "." + jobFormat);
                obj.put("byteslength", String.valueOf(kBytes + " Ko"));

                String uniqueID = jobExportID + "_" + jobRCR + "_" + jobFormat;
                obj.put("uniqueID", uniqueID);

                orders.put(obj);
            }

            connProduitsDerives.commit();


            //Query read store ?
            if (isQuery) {
                //yes, we return a store object wrapping orders array
                //data source must provide a method by which to uniquely identify each item if we want to use identity stores on client side
                JSONObject store = new JSONObject();
                store.put("identifier", "uniqueID");
                store.put("name", "uniqueID");
                store.put("items", orders);

                SimpleImmutableEntry<PreparedStatement, ResultSet> pair2 = null;

                int i = 0;
                if (this.orderId != null) {
                    //unique order ID => number of jobs = orders list size
                    i = orders.length();
                } else {
                    //total number of jobs without limited pagination
                    connProduitsDerives.setAutoCommit(false);
                    pair2 = QueryProduitsDerives.countJobs(this.iln, connProduitsDerives);// may be limited to logged user's ILN
                    resultSet2 = pair2.getValue();
                    statement2 = pair2.getKey();

                    if (resultSet2.next()) {
                        i = resultSet2.getInt(1);
                    }
                    connProduitsDerives.commit();
                }

                logger.info("Total jobs (numRows) for ILN " + iln + "=" + i);
                store.put("numRows", i); //TOTAL number of records without limits !

                RequestHelper.flushOkJSON(helper.getResponse(), store);
            } else {
                //no we return the list of orders
                RequestHelper.flushOkJSON(helper.getResponse(), orders);
            }
        } catch (SQLException e) {
            logger.error(e);
            RequestHelper.errorFlushNotOkJSON(helper.getResponse(), e);
            throw new ServletException(e);
        } catch (JSONException e) {
            logger.error(e);
            RequestHelper.errorFlushNotOkJSON(helper.getResponse(), e);
            throw new ServletException(e);
        } finally {
            ConnectionHelper.release(resultSet, statement);
            ConnectionHelper.release(resultSet2, statement2);
            resultSet = null;
            statement = null;
            resultSet2 = null;
            statement2 = null;
            release(connProduitsDerives);
            connProduitsDerives = null;
        }

        return null; //dispatcher.forward
    }
}
