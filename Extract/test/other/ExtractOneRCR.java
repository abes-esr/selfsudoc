package other;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;

import fr.abes.derives.connection.ConnectionHelper;
import fr.abes.derives.extract.DisconnectSQLException;
import fr.abes.derives.extract.Extract;
import fr.abes.derives.extract.ExtractSQLException;
import fr.abes.derives.extract.QuerySudocXML;
import fr.abes.utils.BufferedRW;
import fr.abes.utils.LogHelper;

public class ExtractOneRCR {

    private static LogHelper logger = new LogHelper(ExtractOneRCR.class);

    private static Connection connSudocXML = null;
    private static PreparedStatement statement = null;
    private static ResultSet resultSet = null;

    private static Writer output = null;

    public static void usage() {
        String message = "Usage: java other.ExtractOneRCR selectedRCR [true]";
        logger.warn(message);
        System.err.println(message);
        System.exit(1);
    }

    /**
     * runtime : use jdbc.divers properties to specify
     * oracle.jdbc.driver.OracleDriver
     *
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

        if (Extract.HOMEDIR == null || "".equals(Extract.HOMEDIR)) {
            String errorMessage = "HOMEDIR is undefined !";
            logger.error(errorMessage);
            System.err.print(errorMessage);
            System.exit(1);
        }

        Extract.readConfiguration();

        for (Enumeration<Driver> e = DriverManager.getDrivers(); e.hasMoreElements(); ) {
            Driver driver = e.nextElement();
            int majorVersion = driver.getMajorVersion();
            int minorVersion = driver.getMinorVersion();
            logger.debug("Driver = " + driver.getClass() + " v" + majorVersion + "." + minorVersion);
        }

        // Stats
        long time0, time1;

        time0 = System.currentTimeMillis();

        boolean exceptionOccured = false;
        String exceptionMessage = null;

        String selectedRCR = null;
        if (args.length > 0) {
            selectedRCR = args[0];
        } else {
            usage();
        }

        boolean withCollections = false;
        if (args.length > 1) {
            withCollections = Boolean.valueOf(args[1]);
        }

        int count = 0;

        // Use Temp file
        File tmp = null;
        try {
            tmp = File.createTempFile("dat", null);
        } catch (IOException e) {
            String errorMessage = e.getMessage();
            logger.error(errorMessage);
            System.err.print(errorMessage);
            System.exit(1);
        }

        try {
            output = BufferedRW.getBufferedWriter(tmp, BufferedRW.UTF8);
        } catch (FileNotFoundException e) {
            exceptionMessage = e.getMessage();
            logger.error(exceptionMessage);
            exceptionOccured = true;
        } catch (UnsupportedEncodingException e) {
            String errorMessage = e.getMessage();
            logger.error(errorMessage);
            System.err.print(errorMessage);
            System.exit(1);
        }

        try {
            connSudocXML = ConnectionHelper.getConnectionThinDriver(Extract.LOGINSUDOCXML, Extract.PASSWORDSUDOCXML, Extract.SUDOCXMLHOSTNAME, Extract.SERVICEAPISUDOCDB, Extract.SUDOCXMLPORT);
            logger.info("getConnection");

            // do Work
            count = QuerySudocXML.writeMARCXML(selectedRCR,
                    withCollections, Extract.DOC_ROOT_BEGIN, Extract.DOC_ROOT_END,
                    QuerySudocXML.DEFAULT_LOW_LIMIT,
                    QuerySudocXML.DEFAULT_HIGH_LIMIT, connSudocXML, output);


            output.flush();

        } catch (IOException e) {
            exceptionMessage = e.getMessage();
            logger.error(exceptionMessage);
            exceptionOccured = true;
        } catch (DisconnectSQLException e) {
            // ALWAYS BEFORE SQLException !!!
            logger.error("Disconnected from SudocXML !!!");
            exceptionMessage = e.getMessage();
            logger.error(exceptionMessage);
            exceptionOccured = true;
        } catch (ExtractSQLException e) {
            logger.error("Extracted data may be corrupted !!!");
            exceptionMessage = e.getMessage();
            logger.error(exceptionMessage);
            exceptionOccured = true;
        } catch (SQLException e) {
            exceptionMessage = e.getMessage();
            logger.error(exceptionMessage);
            exceptionOccured = true;
        } finally {
            time1 = System.currentTimeMillis();
            exceptionOccured = ConnectionHelper.closeConnection(resultSet, statement, connSudocXML);
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    exceptionMessage = e.getMessage();
                    logger.error(exceptionMessage);
                    exceptionOccured = true;
                }
            }
        }

        // Rename
        if (tmp != null) {
            final String dirExtracted = Extract.DOCBASEDIR + "/" + "extracted/";

            String s = dirExtracted + "notices" + "_" + selectedRCR + "_"
                    + System.currentTimeMillis() + BufferedRW.EXT_DOT_XML;

            boolean success = false;
            try {
                logger.debug("before rename");
                success = BufferedRW.moveNFSProof(tmp, new File(s), BufferedRW.UTF8); //NFS Proof
                logger.debug("after rename");
            } catch (Exception e) {
                logger.error("Exception occured while renaming : ", e.getMessage());
                e.printStackTrace();
                success = false;
                exceptionOccured = true;
            }

            if (success) {
                logger.info("successfully renamed to ", s);
                tmp.delete();
            } else {
                exceptionMessage = "failed to rename .tmp file to " + s;
                logger.error(exceptionMessage);
                exceptionOccured = true;
            }
        }

        logger.info("finish");
        logger.info(time1 - time0 + "ms");

        if (exceptionOccured) {
            //Error
            System.err.println("an error occured = " + exceptionMessage);
            System.exit(1);

        } else {
            //Success
            System.out.println("RCR " + selectedRCR + " successfull=" + count);
            System.exit(0);
        }
    }
}
