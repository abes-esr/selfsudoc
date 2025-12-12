package fr.abes.derives.extract;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import fr.abes.derives.connection.ConnectionHelper;
import fr.abes.derives.connection.QueryProduitsDerives;
import fr.abes.utils.LogHelper;

public class CountByRCR {

    private static LogHelper logger = new LogHelper(CountByRCR.class);

    /**
     * @param args
     */
    public static void main(String[] args) {

        logger.info("java.home=", System.getProperty("java.home"));
        logger.info("java.io.tmpdir=", System.getProperty("java.io.tmpdir"));
        logger.info("file.encoding=", System.getProperty("file.encoding"));
        logger.info("docBaseDir=", Extract.DOCBASEDIR);

        // Stats
        long time0, time1;
        time0 = System.currentTimeMillis();

        boolean withCollections = false;
        if (args.length > 0) {
            withCollections = Boolean.valueOf(args[0]);
        }

        for (Enumeration<Driver> e = DriverManager.getDrivers(); e.hasMoreElements(); ) {
            Driver driver = e.nextElement();
            int majorVersion = driver.getMajorVersion();
            int minorVersion = driver.getMinorVersion();
            logger.debug("Driver = " + driver.getClass() +
                    " v" + majorVersion + "." + minorVersion);
        }

        Extract.readConfiguration();

        Connection connProduitsDerives = null;
        try {
            // TC4 need  -Djdbc.drivers=oracle.jdbc.driver.OracleDriver
            connProduitsDerives = ConnectionHelper.getConnectionThinDriver(Extract.LOGINPRODUITSDERIVES, Extract.PASSWORDPRODUITSDERIVES, Extract.DBHOSTNAME, Extract.SERVICEABESDB);
            logger.info("getConnection");
        } catch (SQLException e) {
            String errorMessage = e.getMessage();
            logger.error(errorMessage);
            System.err.print(errorMessage);
            System.exit(1);
        }

        Connection connSudocXML = null;
        try {
            // TC4 need  -Djdbc.drivers=oracle.jdbc.driver.OracleDriver
            connSudocXML = ConnectionHelper.getConnectionThinDriver(Extract.LOGINSUDOCXML, Extract.PASSWORDSUDOCXML, Extract.SUDOCXMLHOSTNAME, Extract.SERVICEAPISUDOCDB, Extract.SUDOCXMLPORT);
            logger.info("getConnection");

        } catch (SQLException e) {
            String errorMessage = e.getMessage();
            logger.error(errorMessage);
            System.err.print(errorMessage);
            System.exit(1);
        }

        Map<String, Long> insertRCRs = new HashMap<String, Long>();
        Map<String, Long> updateRCRs = new HashMap<String, Long>();

        //previously counted
        Map<String, Long> alreadyCountedRCRs = null;
        alreadyCountedRCRs = QueryProduitsDerives.rcrAlreadyCounted(connProduitsDerives);

        //fresh data count from SUDOC
        Map<String, Long> mapCountSUDOC = null;
        try {
            mapCountSUDOC = QuerySudocXML.mapCountByRCR(true, connSudocXML);
        } catch (DisconnectSQLException e) {
            // ALWAYS BEFORE SQLException !!!
            logger.error("Disconnected from SudocXML !!!");
            logger.error(e);
            System.err.print(e.getMessage());
            System.exit(1);
        } catch (ExtractSQLException e) {
            logger.error("Extracted data may be corrupted !!!");
            logger.error(e);
            System.err.print(e.getMessage());
            System.exit(1);
        } catch (SQLException e) {
            logger.error(e);
            System.err.print(e.getMessage());
            System.exit(1);
        }

        //refresh count or add not previously counted RCR
        Iterator<String> iter = mapCountSUDOC.keySet().iterator();
        while (iter.hasNext()) {
            String rcr = iter.next();
            Long count = mapCountSUDOC.get(rcr);
            if (alreadyCountedRCRs.keySet().contains(rcr)) {
                updateRCRs.put(rcr, count);
            } else {
                insertRCRs.put(rcr, count);
            }
        }

        boolean exceptionOccured = false;
        String exceptionMessage = null;

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connProduitsDerives.setAutoCommit(false);
            logger.debug("trying to update " + updateRCRs.size() + " RCRs");
            QueryProduitsDerives.updateCounted(updateRCRs, false, connProduitsDerives); //update count datas from SUDOC
            logger.debug("trying to insert " + insertRCRs.size() + " RCRs");
            QueryProduitsDerives.updateCounted(insertRCRs, true, connProduitsDerives); //insert new count datas from SUDOC
        } catch (SQLException e) {
            exceptionMessage = e.getMessage();
            logger.error(exceptionMessage);
            exceptionOccured = true;
        } finally {

            time1 = System.currentTimeMillis();

            if (exceptionOccured) {
                //Error
                logger.warn(" ! need to roll back transaction...  reason = " + exceptionMessage);
                try {
                    connProduitsDerives.rollback();
                    logger.warn("transaction rolled back ! reason = " + exceptionMessage);
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                    System.err.print(e.getMessage());
                    System.exit(1);
                }

            } else {
                //everything OK, we commit
                logger.debug("trying to commit transaction...");
                try {
                    connProduitsDerives.commit();
                    logger.debug("transaction committed : OK");
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                    System.err.print(e.getMessage());
                    System.exit(1);
                }
            }
            exceptionOccured = exceptionOccured || ConnectionHelper.closeConnection(null, null, connSudocXML);
            exceptionOccured = exceptionOccured || ConnectionHelper.closeConnection(resultSet, statement, connProduitsDerives);
        }

        logger.info("finish");
        logger.info(time1 - time0 + "ms");

        if (exceptionOccured) {
            //Error
            System.out.println("an error occured = " + exceptionMessage);
            System.err.print(exceptionMessage);
            System.exit(1);
        } else {
            //Success
            int counted = (mapCountSUDOC == null) ? 0 : mapCountSUDOC.size();
            System.out.println("Successfully counted " + counted + " RCRs from SUDOC");
            System.out.println("updated " + updateRCRs.size() + " old RCRs");
            System.out.println("inserted " + insertRCRs.size() + " new RCRs");
            System.exit(0);
        }
    }
}
