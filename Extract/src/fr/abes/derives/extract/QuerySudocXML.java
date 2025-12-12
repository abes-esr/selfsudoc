package fr.abes.derives.extract;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import oracle.jdbc.OracleResultSet;
import oracle.jdbc.OracleResultSetMetaData;
import oracle.jdbc.OracleTypes;
import oracle.sql.BLOB;
import fr.abes.derives.connection.ConnectionHelper;
import fr.abes.utils.BufferedRW;
import fr.abes.utils.LogHelper;

public class QuerySudocXML {

    public final static int DEFAULT_HIGH_LIMIT = 26000;
    public final static int DEFAULT_LOW_LIMIT = 0;
    final static String XML_PATTERN = "XML_PATTERN";
    final static String LOCALISED_RCR_PATTERN = "LOCALISED_RCR_PATTERN";
    final static String FULL_XML = "z.data_xml";
    final static String DELETE_XML = "XMLSERIALIZE(CONTENT DELETEXML(z.data_xml, '(/record/datafield[starts-with(@tag,\"9\")][ (./subfield[@code=\"5\"][not (contains(.,\"" + LOCALISED_RCR_PATTERN + "\"))]) ])') as BLOB )";
    final static String SQL_PERIODIQ_ET_COLLEC_LIMITED = "select " + XML_PATTERN + " from autorites.noticesbibio z where id in ( select id from (select d.*, rownum rnum from (select b.id from autorites.biblio_table_generale b,autorites.biblio_table_localisations c where c.D930_B=? and b.id=c.id and (b.typecontrol='s' or b.typecontrol='i' or b.typecontrol='d') and (b.typerecord='c' or b.typerecord='n')"
            + " order by b.id) d where rownum <= ? ) where rnum >= ?)";
    final static String SQL_PERIODIQ_ONLY_LIMITED = "select " + XML_PATTERN + " from autorites.noticesbibio z where id in ( select id from (select d.*, rownum rnum from (select b.id from autorites.biblio_table_generale b,autorites.biblio_table_localisations c where c.D930_B=? and b.id=c.id and (b.typecontrol='s' or b.typecontrol='i') and (b.typerecord='c' or b.typerecord='n') "
            + " order by b.id) d where rownum <= ? ) where rnum >= ?)";
    final static String SQL_COUNT_BY_RCR =

            /*
             * TODO TMX : ne pas compter les doublons plusieurs loc du m?me PPN
             *
             * select count(*)  as nb,D930_B from (
             *
             * select  distinct c.ppn,c.D930_B from autorites.biblio_table_generale b,autorites.biblio_table_localisations c
             * where c.id=b.id
             * and  (b.typecontrol='s' or b.typecontrol='i' or b.typecontrol='d')
             * and (b.typerecord='c' or b.typerecord='n')
             * -- and (b.typerecord!='d')
             * ) loc
             *
             * group by D930_B;
             */

            "select count(*) as nb,c.D930_B from autorites.noticesbibio a,autorites.biblio_table_generale b,autorites.biblio_table_localisations c where a.id=c.id and a.id=b.id and  (b.typecontrol='s' or b.typecontrol='i' or b.typecontrol='d') and (b.typerecord='c' or b.typerecord='n') "
                    + " group by c.D930_B order by  nb";
    private final static String SQLSTATE_KILLED_OR_SHUT_OR_CLOSED = "61000";
    private final static String SQLSTATE_INTERRUPTED = "08003";
    private static LogHelper logger = new LogHelper(QuerySudocXML.class);

    public static int writeMARCXML(
            String selectedRCR,
            boolean withCollections,
            String docRootBegin,
            String docRootEnd,
            Integer lowLimit,
            Integer highLimit,
            Connection conn,
            Writer output
    ) throws DisconnectSQLException, SQLException, ExtractSQLException {

        if (conn == null) {
            throw new DisconnectSQLException("Connection IS NULL !!!");
        }

        // Stats
        long time0, time1;

        int count = 0;

        Map<Integer, String> params = new HashMap<Integer, String>();
        params.put(1, selectedRCR);

        String delete = DELETE_XML;
        String delete_loc = delete.replaceAll(LOCALISED_RCR_PATTERN, selectedRCR);
        logger.debug("delete_loc " + delete_loc);
        logger.debug("lowLimit " + lowLimit);
        logger.debug("highLimit " + highLimit);

        String vLowLimit = null;
        String vHighLimit = null;

        if (lowLimit == null) {
            vLowLimit = Integer.toString(DEFAULT_LOW_LIMIT);
        } else {
            vLowLimit = Integer.toString(lowLimit);
        }

        if (highLimit == null) {
            vHighLimit = Integer.toString(DEFAULT_HIGH_LIMIT);
        } else {
            vHighLimit = Integer.toString(highLimit);
        }

        params.put(2, vHighLimit); //sup
        params.put(3, vLowLimit); //inf

        SimpleImmutableEntry<PreparedStatement, ResultSet> pair = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        logger.debug("withCollections ", Boolean.toString(withCollections));

        try {
            logger.debug("before ConnectionHelper.executeQuery");
            for (Iterator<String> iterator = params.values().iterator(); iterator.hasNext(); ) {
                String v = iterator.next();
                logger.debug("param " + v);
            }

            conn.setAutoCommit(false);

            String query = null;

            if (withCollections) {
                query = SQL_PERIODIQ_ET_COLLEC_LIMITED;
            } else {
                query = SQL_PERIODIQ_ONLY_LIMITED;
            }

            String q2 = query.replaceAll(XML_PATTERN, delete_loc);
            logger.debug("q2=" + q2);
            pair = ConnectionHelper.executeQuerySpecialCursors(conn, q2, params);

            resultSet = pair.getValue();

            OracleResultSet orset = (OracleResultSet) resultSet;
            OracleResultSetMetaData mdata = (OracleResultSetMetaData) orset.getMetaData();
            logger.debug("OracleTypes.OPAQUE = " + Boolean.toString(OracleTypes.OPAQUE == mdata.getColumnType(1)));
            logger.debug("OracleTypes.CLOB = " + Boolean.toString(OracleTypes.CLOB == mdata.getColumnType(1)));
            logger.debug("SYS.XMLTYPE = " + "SYS.XMLTYPE".equals(mdata.getColumnTypeName(1)));

            statement = pair.getKey();

            StringBuilder sb = new StringBuilder();
            sb.append(BufferedRW.XMLPROLOG).append(docRootBegin).append("\n");
            output.write(sb.toString());

            logger.info("writing resultset : begin");
            time0 = System.currentTimeMillis();

            BufferedReader r = null;
            BLOB b = null;
            String line = null;

            while (orset.next()) {
                b = orset.getBLOB(1);
                r = new BufferedReader(new InputStreamReader(b.binaryStreamValue()));
                while ((line = r.readLine()) != null) {
                    output.write(line);
                }
                r.close();
                count++;
            }

            conn.commit();

            final StringBuilder sb2 = new StringBuilder();
            sb2.append("\n").append(docRootEnd);
            output.write(sb2.toString());
            output.flush();

            time1 = System.currentTimeMillis();
            logger.info("end of resulset writing " + count + " notices in " + (time1 - time0) + "ms");

        } catch (SQLException e) {
            findCauseSQLException(conn, e);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            logger.debug("in finally block");
            ConnectionHelper.release(resultSet, statement);
            logger.debug("after resultSet and statement release");
            resultSet = null;
            statement = null;
        }

        return count;
    }

    public static Map<String, Long> mapCountByRCR(boolean withCollections, Connection conn) throws DisconnectSQLException, SQLException, ExtractSQLException {

        if (conn == null) {
            throw new DisconnectSQLException("Connection IS NULL !!!");
        }

        Map<String, Long> result = new HashMap<String, Long>();

        // Stats
        long time0, time1;

        int count = 0;

        Map<Integer, String> params = new HashMap<Integer, String>();

        SimpleImmutableEntry<PreparedStatement, ResultSet> pair = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        logger.debug("withCollections ", Boolean.toString(withCollections));

        try {
            conn.setAutoCommit(false);
            if (withCollections) {
                pair = ConnectionHelper.executeQuerySpecialCursors(conn, SQL_COUNT_BY_RCR, params);
            } else {
                pair = ConnectionHelper.executeQuerySpecialCursors(conn, SQL_COUNT_BY_RCR, params);
            }

            resultSet = pair.getValue();
            statement = pair.getKey();

            time0 = System.currentTimeMillis();

            long noticesNb = 0;
            String rcr = null;

            while (resultSet.next()) {
                noticesNb = resultSet.getLong(1);
                rcr = resultSet.getString(2);
                if (!"4° SS 538".equals(rcr) && !rcr.contains("-")) {
                    if (rcr.length() > 9) {
                        logger.warn("RCR label >9 car. START" + rcr + "STOP");
                    } else {
                        result.put(rcr, noticesNb);
                        count++;
                    }
                }
            }

            conn.commit();

            time1 = System.currentTimeMillis();
            logger.info("end of resulset, counted " + count + " RCRs in " + (time1 - time0) + "ms");

        } catch (SQLException e) {
            findCauseSQLException(conn, e);
        } finally {
            ConnectionHelper.release(resultSet, statement);
            resultSet = null;
            statement = null;
        }

        return result;
    }

    /**
     * OR :
     * <p>
     * - lever une exception applicative "DisconnectSQLException" qui sera reprise par l'appelant pour retenter une connexion
     * - lever une exception applicative "ExtractSQLException" pour l'informer d'un ?chec
     * - ou re-throw SQLException si non r?cup?rable
     *
     * @param conn
     * @param e
     * @throws SQLException
     * @throws DisconnectSQLException
     * @throws ExtractSQLException
     */
    private static void findCauseSQLException(Connection conn, SQLException e) throws SQLException, DisconnectSQLException, ExtractSQLException {

        logger.error(e.getMessage());
        String sqlState = e.getSQLState();
        logger.error("actuel SQLSTATE=" + sqlState);

        // analyze error

        String message = null;

        try {
            if (SQLSTATE_INTERRUPTED.equals(sqlState)
                    || SQLSTATE_KILLED_OR_SHUT_OR_CLOSED.equals(sqlState)) {
                // Oracle specific SQLSTATE
                message = "SQLSTATE=" + sqlState;
                logger.warn(message);
                throw new DisconnectSQLException(message, e);
            } else {
                // unknown SQLSTATE
                boolean isClosed = conn.isClosed();
                logger.warn("closed ? " + isClosed);

                if (isClosed) {
                    message = "Connection is Closed !";
                    logger.warn(message);
                    throw new DisconnectSQLException(message, e);

                } else {

                    boolean isValid = conn.isValid(5); // wait 5secs
                    logger.warn("valid ? " + isValid);

                    if (!isValid) {
                        message = "Connection is Not Valid !";
                        logger
                                .error(message);
                        throw new DisconnectSQLException(message, e);
                    } else {
                        message = "Extracted data may be corrupted !!!";
                        logger.error(message);
                        throw new ExtractSQLException(message, e);
                    }
                }
            }
        } catch (SQLException e1) {
            // NON RECOVERABLE, re-throw SQLException
            logger.error("We can't recover from " + e1.getMessage());
            throw new SQLException(e1);
        }
    }
}
