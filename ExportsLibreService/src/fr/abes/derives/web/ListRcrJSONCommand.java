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
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map;

public class ListRcrJSONCommand implements ICommand {

    private static LogHelper logger = new LogHelper(ListRcrJSONCommand.class);

    private DataSource sybaseILNDataSource = null;
    private Map<String, Long> countsByRCR = null;

    private Integer iln = null;

    public ListRcrJSONCommand(Integer iln, Map<String, Long> countsByRCR, DataSource sybaseILNDataSource) {
        super();
        this.iln = iln;
        this.sybaseILNDataSource = sybaseILNDataSource;
        this.countsByRCR = countsByRCR;
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

        logger.info(helper, "ListRcrJSONCommand");

        //undefined RCR
        if (this.iln == null) {
            RequestHelper.errorFlushNotOkJSON(helper.getResponse(), new IllegalArgumentException("ILN is undefined"));
            return null;
        }

        if (this.iln == 0) {
            RequestHelper.errorFlushNotOkJSON(helper.getResponse(), new IllegalArgumentException("Wrong ILN : 0"));
            return null;
        }

        Connection connSybaseILN = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        JSONArray libraries = new JSONArray();

        try {
            if (this.sybaseILNDataSource != null) {
                connSybaseILN = this.sybaseILNDataSource.getConnection();
                connSybaseILN.setAutoCommit(true);
            }

            SimpleImmutableEntry<PreparedStatement, ResultSet> pair = null;
            pair = QueryProduitsDerives.listILN(this.iln, connSybaseILN);
            resultSet = pair.getValue();
            statement = pair.getKey();

            while (resultSet.next()) {
                String libraryShortName = resultSet.getString("short_name");
                String libraryRCR = resultSet.getString("rcr");
                libraryRCR = (libraryRCR == null) ? null : libraryRCR.trim(); //trim
                String libraryILN = resultSet.getString("iln");

                JSONObject obj = new JSONObject();

                obj.put("short_name", libraryShortName);
                obj.put("library", libraryRCR);
                obj.put("iln", libraryILN);
                Long l = countsByRCR.get(libraryRCR);
                String counted = (l == null) ? "0" : l.toString();
                obj.put("counted", counted);

                if (counted != null && Integer.parseInt(counted) > RequestHelper.DEFAULT_HIGH_LIMIT) {
                    //add flag overflow to object
                    obj.put("mustBeLimited", true);
                }

                libraries.put(obj);
            }
            RequestHelper.flushOkJSON(helper.getResponse(), libraries);
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
            resultSet = null;
            statement = null;
            release(connSybaseILN);
            connSybaseILN = null;
        }
        return null; //dispatcher.forward
    }
}
