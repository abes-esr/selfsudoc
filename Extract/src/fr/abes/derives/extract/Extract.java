package fr.abes.derives.extract;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.AbstractMap.SimpleImmutableEntry;

import fr.abes.derives.connection.ConnectionHelper;
import fr.abes.derives.connection.QueryProduitsDerives;
import fr.abes.utils.BufferedRW;
import fr.abes.utils.LogHelper;

public class Extract {

    final public static String DOCBASE = "docBase";
    final public static String HOMEDIR = System.getenv("EOD_HOME");
    final public static String DOCBASEDIR = HOMEDIR + "/" + DOCBASE; //will be served later by Tomcat
    final public static String DOC_ROOT_BEGIN = "<catalogue>";
    final public static String DOC_ROOT_END = "</catalogue>";
    public static String DBHOSTNAME = null;
    public static String SUDOCXMLHOSTNAME = null;
    public static String SUDOCXMLPORT = null;
    public static String LOGINSUDOCXML = null;
    public static String PASSWORDSUDOCXML = null;
    public static String SERVICEAPISUDOCDB = null;
    protected static String LOGINPRODUITSDERIVES = null;
    protected static String PASSWORDPRODUITSDERIVES = null;
    protected static String SERVICEABESDB = null;
    private static LogHelper logger = new LogHelper(Extract.class);
    private static Properties configuration = null;

    private static String renameTo(String directory, long uniqueExportID, String rcr) {
        final String dirExtracted = directory + "/" + "extracted/";
        String filename = OrderWrapper.RCR_FILE_PREFIX + "_" + rcr + "_" + uniqueExportID;
        return dirExtracted + filename + BufferedRW.EXT_DOT_XML;
    }

    private static boolean rename(File tmp, String renameTo) {
        //TODO TMX : change boolean return type to renamed value if success, null otherwise
        if (tmp != null) {
            boolean success = false;
            try {
                logger.debug("before rename");
                success = BufferedRW.moveNFSProof(tmp, new File(renameTo), BufferedRW.UTF8); //NFS Proof
                logger.debug("after rename");
            } catch (Exception e) {
                logger.error("Exception occured while renaming : ", e.getMessage());
                e.printStackTrace();
                success = false;
            }

            if (success) {
                logger.debug("successfully renamed to ", renameTo);
                tmp.delete();
            } else {
                logger.error("failed to rename .tmp file to " + renameTo);
            }

            return success;
        } else {
            //tmp is null
            return false;
        }
    }

    public static void usage() {
        String message = "Usage: java fr.abes.derives.extract.Extract EXTRACTEDDIRECTORY";
        logger.warn(message);
        System.err.println(message);
        System.exit(1);
    }

    public static void readConfiguration() {

        //TODO TMX : refactor and move to Utils + unit testing :
        // - set of property names
        // - check missing property names with .propertyNames();
        // - loop to get values and check missing values

        String KEY_DBHOSTNAME = "dbHostName";
        String KEY_LOGINPRODUITSDERIVES = "loginProduitsDerives";
        String KEY_PASSWORDPRODUITSDERIVES = "passwordProduitsDerives";
        String KEY_SERVICEABESDB = "serviceABESDB";
        String KEY_SUDOCXMLHOSTNAME = "sudocXMLHostName";
        String KEY_SUDOCXMLPORT = "sudocXMLPort";
        String KEY_LOGINSUDOCXML = "loginSudocXML";
        String KEY_PASSWORDSUDOCXML = "passwordSudocXML";
        String KEY_SERVICEAPISUDOCDB = "serviceAPISUDOCDB";

        String fileName = HOMEDIR + "/conf/" + "config.xml";

        InputStream input = null;
        boolean error = false;
        String errorMessage = null;

        File file = null;
        file = new File(fileName);
        if (!file.exists()) {
            errorMessage = "File does not exist : " + file.getPath();
            logger.error(errorMessage);
            System.err.print(errorMessage);
            System.exit(1);
        }

        try {
            input = new FileInputStream(file);
            configuration = new Properties();
            configuration.loadFromXML(input);
            configuration.list(System.out);

            //Base Exports a la demande

            DBHOSTNAME = configuration.getProperty(KEY_DBHOSTNAME);
            if (DBHOSTNAME == null || "".equals(DBHOSTNAME.trim())) {
                error = true;
                errorMessage = KEY_DBHOSTNAME;
            }

            LOGINPRODUITSDERIVES = configuration
                    .getProperty(KEY_LOGINPRODUITSDERIVES);
            if (LOGINPRODUITSDERIVES == null
                    || "".equals(LOGINPRODUITSDERIVES.trim())) {
                error = true;
                errorMessage = KEY_LOGINPRODUITSDERIVES;
            }

            PASSWORDPRODUITSDERIVES = configuration
                    .getProperty(KEY_PASSWORDPRODUITSDERIVES);
            if (PASSWORDPRODUITSDERIVES == null
                    || "".equals(PASSWORDPRODUITSDERIVES.trim())) {
                error = true;
                errorMessage = KEY_PASSWORDPRODUITSDERIVES;
            }

            SERVICEABESDB = configuration.getProperty(KEY_SERVICEABESDB);
            if (SERVICEABESDB == null || "".equals(SERVICEABESDB.trim())) {
                error = true;
                errorMessage = KEY_SERVICEABESDB;
            }

            //Sudoc XML

            SUDOCXMLHOSTNAME = configuration.getProperty(KEY_SUDOCXMLHOSTNAME);
            if (SUDOCXMLHOSTNAME == null || "".equals(SUDOCXMLHOSTNAME.trim())) {
                error = true;
                errorMessage = KEY_SUDOCXMLHOSTNAME;
            }

            //Port needed for tunneling in dev
            SUDOCXMLPORT = configuration.getProperty(KEY_SUDOCXMLPORT);
            if (SUDOCXMLPORT == null || "".equals(SUDOCXMLPORT.trim())) {
                error = true;
                errorMessage = KEY_SUDOCXMLPORT;
            }

            LOGINSUDOCXML = configuration.getProperty(KEY_LOGINSUDOCXML);
            if (LOGINSUDOCXML == null || "".equals(LOGINSUDOCXML.trim())) {
                error = true;
                errorMessage = KEY_LOGINSUDOCXML;
            }

            PASSWORDSUDOCXML = configuration.getProperty(KEY_PASSWORDSUDOCXML);
            if (PASSWORDSUDOCXML == null || "".equals(PASSWORDSUDOCXML.trim())) {
                error = true;
                errorMessage = KEY_PASSWORDSUDOCXML;
            }

            SERVICEAPISUDOCDB = configuration.getProperty(KEY_SERVICEAPISUDOCDB);
            if (SERVICEAPISUDOCDB == null
                    || "".equals(SERVICEAPISUDOCDB.trim())) {
                error = true;
                errorMessage = KEY_SERVICEAPISUDOCDB;
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
            error = true;
            errorMessage = e.getMessage();
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
                error = true;
                errorMessage = e.getMessage();
            }
        }

        if (error) {
            System.err.print("Error : " + errorMessage);
            System.exit(1);
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        logger.info("java.home=", System.getProperty("java.home"));
        logger.info("java.vendor=", System.getProperty("java.vendor"));
        logger.info("java.vendor.url=", System.getProperty("java.vendor.url"));
        logger.info("java.version=", System.getProperty("java.version"));
        logger.info("java.class.path=", System.getProperty("java.class.path"));
        logger.info("java.version=", System.getProperty("java.version"));
        logger.info("java.class.version=", System.getProperty("java.class.version"));
        logger.info("file.encoding=", System.getProperty("file.encoding"));
        logger.info("docBaseDir=", Extract.DOCBASEDIR);

        if (HOMEDIR == null || "".equals(HOMEDIR)) {
            String errorMessage = "HOMEDIR is undefined !";
            logger.error(errorMessage);
            System.err.print(errorMessage);
            System.exit(1);
        }

        String EXTRACTEDDIRECTORY = null;
        if (args.length > 0) {
            EXTRACTEDDIRECTORY = args[0];
        } else {
            usage();
        }

        try {
            File runtimeDestinationDir = new File(DOCBASEDIR + "/" + EXTRACTEDDIRECTORY);
            BufferedRW.validateDirectory(runtimeDestinationDir);
            logger.debug(runtimeDestinationDir + " is a valid directory");
        } catch (FileNotFoundException e) {
            String errorMessage = e.getMessage();
            logger.error(errorMessage);
            System.err.print(errorMessage);
            System.exit(1);
        }

        readConfiguration();

        for (Enumeration<Driver> e = DriverManager.getDrivers(); e.hasMoreElements(); ) {
            Driver driver = e.nextElement();
            int majorVersion = driver.getMajorVersion();
            int minorVersion = driver.getMinorVersion();
            logger.debug("Driver = " + driver.getClass() +
                    " v" + majorVersion + "." + minorVersion);
        }

        Connection connProduitsDerives = null;
        try {
            // TC4 need  -Djdbc.drivers=oracle.jdbc.driver.OracleDriver
            connProduitsDerives = ConnectionHelper.getConnectionThinDriver(LOGINPRODUITSDERIVES, PASSWORDPRODUITSDERIVES, DBHOSTNAME, SERVICEABESDB);
            logger.info("connProduitsDerives getConnection");
        } catch (SQLException e) {
            String errorMessage = "Impossible de lancer le programme car " + SERVICEABESDB + " sur " + DBHOSTNAME + " refuse la connexion ! : " + e.getMessage();
            logger.error(errorMessage);
            System.err.print(errorMessage);
            System.exit(1);
        }

        Connection connSudocXML = null;
        try {
            // TC4 need  -Djdbc.drivers=oracle.jdbc.driver.OracleDriver
            connSudocXML = ConnectionHelper.getConnectionThinDriver(LOGINSUDOCXML, PASSWORDSUDOCXML, SUDOCXMLHOSTNAME, SERVICEAPISUDOCDB, SUDOCXMLPORT);
            logger.info("connSudocXML getConnection");
        } catch (SQLException e) {
            String errorMessage = "Impossible de lancer le programme car " + SERVICEAPISUDOCDB + " sur " + SUDOCXMLHOSTNAME + " refuse la connexion sur le port " + SUDOCXMLPORT + " ! : " + e.getMessage();
            logger.error(errorMessage);
            System.err.print(errorMessage);
            System.exit(1);
        }

        Map<Long, OrderWrapper> orders = null;

        do {
            orders = mapWaitingOrders(connProduitsDerives, EXTRACTEDDIRECTORY);
            Set<Long> ordersIDs = orders.keySet();

            try {//try because maybe we have lost connection to SudocXML

                for (Long orderID : ordersIDs) { // for each order
                    if (orderID != 0) // 0 = fake we ignore
                    {
                        OrderWrapper export = orders.get(orderID);
                        logger.info("order ID=" + orderID, export.toString());
                        Map<String, Set<String>> rcrS = export.getRcrS();
                        // store RCRs dispatched by key=final format (pdf, rtf...)
                        boolean withCollections = export.isWithCollections();

                        Set<String> rcrNoDupAllFormats = new HashSet<String>();

                        // Collapse if we have same RCR in multiple formats ->
                        // unique extraction
                        Set<String> formats = rcrS.keySet();
                        for (String format : formats) {
                            Set<String> rcrForFormat = rcrS.get(format);
                            rcrNoDupAllFormats.addAll(rcrForFormat);
                        }

                        // Erros handling : format->rcr->message
                        Map<String, Map<String, String>> errorsRCR = new HashMap<String, Map<String, String>>();
                        errorsRCR.put(OrderWrapper.PDF,
                                new HashMap<String, String>());
                        errorsRCR.put(OrderWrapper.RTF,
                                new HashMap<String, String>());
                        errorsRCR.put(OrderWrapper.SYLK,
                                new HashMap<String, String>());

                        Iterator<String> iter = rcrNoDupAllFormats.iterator();

                        while (iter.hasNext()) {
                            extractRCR(DOCBASEDIR, orderID, withCollections,
                                    iter.next(), rcrS, export.getLowLimitMap(),
                                    export.getHighLimitMap(), connSudocXML,
                                    connProduitsDerives, errorsRCR);
                        }
                    }
                }

            } catch (DisconnectSQLException e) {
                //WE NEED TO RECONNECT TO SUDOCXML !!!
                logger.info("connSudocXML try closing");
                ConnectionHelper.closeConnection(null, null, connSudocXML);
                logger.info("connSudocXML cleanly closed");

                try {
                    connSudocXML = ConnectionHelper.getConnectionThinDriver(LOGINSUDOCXML, PASSWORDSUDOCXML, SUDOCXMLHOSTNAME, SERVICEAPISUDOCDB, SUDOCXMLPORT);
                    logger.info("connSudocXML connection is BACK !!!");
                } catch (SQLException e1) {
                    String errorMessage = "Impossible de lancer le programme car " + SERVICEAPISUDOCDB + " sur " + SUDOCXMLHOSTNAME + " refuse la connexion sur le port " + SUDOCXMLPORT + " ! : " + e1.getMessage();
                    //we should WARN administrator !
                    logger.error(errorMessage);
                    System.err.print(errorMessage);
                }
            }

            // pause before expecting new order have been added to database
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                logger.debug(e.getMessage());
            }
        }
        while (true);
    }

    private static Map<Long, OrderWrapper> mapWaitingOrders(Connection connProduitsDerives, String extractedDirectory) {
        SimpleImmutableEntry<PreparedStatement, ResultSet> pair = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        Map<Long, OrderWrapper> result = new HashMap<Long, OrderWrapper>();

        try {
            pair = QueryProduitsDerives.listWaitingToExtract((OrderWrapper.COLUMN_PREFIX + extractedDirectory).toUpperCase(), connProduitsDerives);

            resultSet = pair.getValue();
            statement = pair.getKey();

            long currentID = 0;
            OrderWrapper order = new OrderWrapper(false);

            while (resultSet.next()) {

                long exportID = resultSet.getLong(1);
                String rcr = resultSet.getString(2);
                String finalFormat = resultSet.getString(3);
                boolean withCollections = resultSet.getBoolean(4);
                Integer lowLimit = resultSet.getInt(5);
                if (resultSet.wasNull()) {
                    //null is NOT zero !
                    lowLimit = null;
                }
                Integer highLimit = resultSet.getInt(6);
                if (resultSet.wasNull()) {
                    //null is NOT zero !
                    highLimit = null;
                }

                if (exportID != currentID) {
                    //break on a new order
                    result.put(currentID, order);
                    order = new OrderWrapper(withCollections);
                }
                currentID = exportID;
                order.addRcr(finalFormat, rcr, lowLimit, highLimit);

            }
            //last one
            result.put(currentID, order);

        } catch (SQLException e) {
            logger.error(e.getMessage());
            System.err.print(e.getMessage());
            System.exit(1);
        } finally {
            ConnectionHelper.release(resultSet, statement);
            resultSet = null;
            statement = null;
        }

        logger.debug("" + (result.size() - 1) + " orders are waiting"); // 0 = fake we ignore

        return result;
    }

    private static void extractRCR(
            String docBaseDir,
            long uniqueExportID,
            boolean withCollections,
            String rcr,
            Map<String, Set<String>> rcrS,
            Map<String, Integer> lowLimitMap,
            Map<String, Integer> highLimitMap,
            Connection connSudocXML,
            Connection connProduitsDerives,
            Map<String, Map<String, String>> errorsRCR
    ) throws DisconnectSQLException {

        logger.debug("extractRCR " + rcr);

        File tmp = null;
        Writer output = null;

        //Record for "DEMANDES"
        SimpleImmutableEntry<PreparedStatement, ResultSet> pair = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        boolean rollback = false;
        String rollbackMessage = null;

        Set<String> rcrPDF = rcrS.get(OrderWrapper.PDF);
        Set<String> rcrRTF = rcrS.get(OrderWrapper.RTF);
        Set<String> rcrSYLK = rcrS.get(OrderWrapper.SYLK);

        Map<String, String> errorsPDF = errorsRCR.get(OrderWrapper.PDF);
        Map<String, String> errorsRTF = errorsRCR.get(OrderWrapper.RTF);
        Map<String, String> errorsSYLK = errorsRCR.get(OrderWrapper.SYLK);

        try {
            // Use Temp file
            tmp = File.createTempFile("dat", null);
            output = BufferedRW.getBufferedWriter(tmp, BufferedRW.UTF8);
            // do Work

            Integer lowLimit = lowLimitMap.get(rcr);
            Integer highLimit = highLimitMap.get(rcr);

            logger.debug("lowLimit=" + lowLimit);
            logger.debug("highLimit=" + highLimit);

            logger.info("before QuerySudocXML.writeMARCXML " + rcr);
            int count = QuerySudocXML.writeMARCXML(rcr, withCollections,
                    DOC_ROOT_BEGIN, DOC_ROOT_END, lowLimit, highLimit, connSudocXML, output);
            logger.info("after QuerySudocXML.writeMARCXML " + rcr + " count=" + count);

            connProduitsDerives.setAutoCommit(false);
            if (rcrPDF.contains(rcr)) {
                pair = QueryProduitsDerives.updateOKExtractStatus(uniqueExportID, rcr, count, OrderWrapper.PDF, connProduitsDerives);
            }
            if (rcrRTF.contains(rcr)) {
                pair = QueryProduitsDerives.updateOKExtractStatus(uniqueExportID, rcr, count, OrderWrapper.RTF, connProduitsDerives);
            }
            if (rcrSYLK.contains(rcr)) {
                pair = QueryProduitsDerives.updateOKExtractStatus(uniqueExportID, rcr, count, OrderWrapper.SYLK, connProduitsDerives);
            }

            resultSet = pair.getValue();
            statement = pair.getKey();

        } catch (IOException e) {
            rollbackMessage = e.getMessage();
            logger.error(rollbackMessage);
            rollback = true;
        } catch (DisconnectSQLException e) {
            // ALWAYS BEFORE SQLException !!!
            logger.warn("Disconnected from SudocXML !!!");
            rollbackMessage = e.getMessage();
            logger.warn(rollbackMessage);
            rollback = true;
            //re-throwing for caller
            throw new DisconnectSQLException(e);
        } catch (ExtractSQLException e) {
            logger.error("Extracted data may be corrupted !!!");
            rollbackMessage = e.getMessage();
            logger.warn(rollbackMessage);
            rollback = true;
        } catch (SQLException e) {
            logger.error("We can't recover from this SQLException !!!");
            rollbackMessage = e.getMessage();
            logger.error(rollbackMessage);
            rollback = true;
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    rollbackMessage = e.getMessage();
                    logger.error(rollbackMessage);
                    rollback = true;
                }
            }

            if (!rollback) {
                // try to rename only if NO rollback !
                String renameTo = renameTo(docBaseDir, uniqueExportID, rcr);
                boolean renameOK = rename(tmp, renameTo);
                if (!renameOK) {
                    if (tmp != null) {
                        tmp.delete();
                    } // clean tmp
                    rollback = true;
                    rollbackMessage = "rename NOT OK ! (" + renameTo + ")";
                }
            }

            if (rollback) {
                logger.warn(" ! need to roll back transaction...  reason = " + rollbackMessage);
                //dispatch errors by format, store rcr causing errors for future reporting
                if (rcrPDF.contains(rcr)) {
                    errorsPDF.put(rcr, rollbackMessage);
                }
                if (rcrRTF.contains(rcr)) {
                    errorsRTF.put(rcr, rollbackMessage);
                }
                if (rcrSYLK.contains(rcr)) {
                    errorsSYLK.put(rcr, rollbackMessage);
                }
                try {
                    connProduitsDerives.rollback();
                    logger.warn("transaction rolled back ! reason = " + rollbackMessage);
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

            ConnectionHelper.release(resultSet, statement);
            resultSet = null;
            statement = null;
        }
    }
}
