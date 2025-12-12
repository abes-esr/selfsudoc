package fr.abes.derives.connection;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.AbstractMap.SimpleImmutableEntry;

import fr.abes.utils.LogHelper;

public class ConnectionHelper {

    final public static String DRIVER_ORACLE_THIN = "jdbc:oracle:thin:@//";
    final public static String DRIVER_ORACLE_PORT = "1521";
    static LogHelper logger = new LogHelper(ConnectionHelper.class);

    /**
     * TC4 : DO NOT use in container environment, only in standalone with :
     * -Djdbc.drivers=oracle.jdbc.driver.OracleDriver
     *
     * @return
     * @throws SQLException
     */
    public static Connection getConnectionThinDriver(String user, String password, String host, String service, String portParam) throws SQLException {

        final Properties info = new java.util.Properties();
        info.put("user", user);
        info.put("password", password);
        Driver d = DriverManager.getDriver(DRIVER_ORACLE_THIN);
        int majorVersion = d.getMajorVersion();
        int minorVersion = d.getMinorVersion();
        logger.debug("Driver = " + d.getClass() +
                " v" + majorVersion + "." + minorVersion);

        String port = DRIVER_ORACLE_PORT;
        if (portParam != null && !("".equals(portParam))) {
            port = portParam;
        }

        String urlConnect = DRIVER_ORACLE_THIN + host + ":" + port + "/" + service;
        logger.debug("Connecting to " + urlConnect + " with user=" + user + " on port=" + port);
        Connection conn = d.connect(urlConnect, info);
        try {
            logPropertyInfo(d, urlConnect, info);
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
        return conn;

    }

    public static Connection getConnectionThinDriver(String user, String password, String host, String service) throws SQLException {
        return getConnectionThinDriver(user, password, host, service, null);
    }

    /**
     *
     * @param resultSet
     * @param statement
     * @param conn
     * @return
     */
    public static boolean closeConnection(ResultSet resultSet, PreparedStatement statement, Connection conn) {

        boolean exceptionOccured = false;

        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            exceptionOccured = true;
        }
        resultSet = null;
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            exceptionOccured = true;
        }
        statement = null;
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            exceptionOccured = true;
        }
        conn = null;
        return exceptionOccured;
    }

    public static void release(ResultSet resultSet, PreparedStatement statement) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public static SimpleImmutableEntry<PreparedStatement, ResultSet> executeQuery(
            Connection conn, String query, Map<Integer, String> params)
            throws SQLException {

        logger.debug("java.home", System.getProperty("java.home"));
        logger.debug("java.io.tmpdir", System.getProperty("java.io.tmpdir"));
        logger.debug("file.encoding", System.getProperty("file.encoding"));

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        statement = conn.prepareStatement(query);

        Set<Integer> keys = params.keySet();
        for (Integer i : keys) {
            statement.setString(i, params.get(i));

        }

        logger.info("executing query : queryString = " + query);
        // Stats
        long time0, time1;
        time0 = System.currentTimeMillis();
        resultSet = statement.executeQuery();
        time1 = System.currentTimeMillis();
        logger.info("query executed in " + (time1 - time0) + "ms");

        //!!!DO NOT try to use resultSet.getMetaData(); otherwise will generate an ORA-900 Invalid Sql Statement

        return new SimpleImmutableEntry<PreparedStatement, ResultSet>(statement, resultSet); // A Pair
    }

    public static SimpleImmutableEntry<PreparedStatement, ResultSet> executeQuerySpecialCursors(
            Connection conn, String query, Map<Integer, String> params)
            throws SQLException {

        logger.debug("java.home", System.getProperty("java.home"));
        logger.debug("java.io.tmpdir", System.getProperty("java.io.tmpdir"));
        logger.debug("file.encoding", System.getProperty("file.encoding"));

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        statement = conn.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.CLOSE_CURSORS_AT_COMMIT);

        if (params != null) {
            Set<Integer> keys = params.keySet();
            for (Integer i : keys) {
                logger.info("param i=" + i + " params.get(i)=" + params.get(i));
                statement.setString(i, params.get(i));

            }
        }

        logger.info("executing query : queryString = " + query);
        // Stats
        long time0, time1;
        time0 = System.currentTimeMillis();
        resultSet = statement.executeQuery();
        time1 = System.currentTimeMillis();
        logger.info("query executed in " + (time1 - time0) + "ms");

        //!!!DO NOT try to use resultSet.getMetaData(); otherwise will generate an ORA-900 Invalid Sql Statement
        return new SimpleImmutableEntry<PreparedStatement, ResultSet>(statement, resultSet); // A Pair
    }

    private static void logPropertyInfo(Driver aDriver, String jdbcURL, Properties daProps) throws Exception {
        // Get the DriverPropertyInfo of the given driver
        DriverPropertyInfo[] props = aDriver.getPropertyInfo(jdbcURL, daProps);

        // If the driver is poorly implemented, a null object may be returned.
        if (props == null) return;

        logger.debug("Resolving properties for: " + aDriver.getClass().getName());
        // List all properties.
        for (int i = 0; i < props.length; i++) {
            // Get the property metadata
            String propName = props[i].name;
            String[] propChoices = props[i].choices;
            boolean req = props[i].required;
            String propDesc = props[i].description;

            // Printout
            logger.debug("" + propName + " (Req: " + req + ")");
            if (propChoices == null) {
                logger.debug(" No choices.");
            } else {
                logger.debug(" Choices: ");
                for (int j = 0; j < propChoices.length; j++) {
                    logger.debug(" " + propChoices[j]);
                }
            }

            logger.debug(" Desc: " + propDesc);
        }
    }
}
