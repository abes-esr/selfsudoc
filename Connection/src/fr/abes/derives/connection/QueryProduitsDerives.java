package fr.abes.derives.connection;

import fr.abes.utils.LogHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryProduitsDerives {

    final static String STATUS_WAIT = "W";
    final static String STATUS_OK = "O";
    final static String STATUS_EXCEPTION = "X";
    final static String STATUS_IGNORE = "I";
    final static String COLUMN_PREFIX = "ETAT_";
    final static String SQL_PREPARE_EXPORT = "insert into produits_derives.demandes (id, iln, coordinateur, rcr, rcr_shortname, dateextraction,nbnotices,formatfinal,aveccollections,excludeddatafields,layout,low_limit,high_limit,etat_extracted) values(?,?,?,?,?,TO_DATE(?, 'YYYY/MM/DD HH24:MI:SS'),?,?,?,?,?,?,?,'" + STATUS_WAIT + "')";
    final static String PARTIAL_SQL_ETAT_EXTRACTED_OK = "update produits_derives.demandes set etat_extracted='" + STATUS_OK + "', etat_cleaned='" + STATUS_WAIT + "', etat_grouped='" + STATUS_WAIT + "', etat_filtered='" + STATUS_WAIT + "', etat_sorted='" + STATUS_WAIT + "'";
    final static String ENDING_SQL_ETAT_EXTRACTED_OK = ", nbnotices=? where (formatfinal=? and rcr=? and id=?)";
    final static String SQL_ETAT_EXTRACTED_EXCEPTION = "update produits_derives.demandes set etat_extracted='" + STATUS_EXCEPTION + "', exceptionmsg=? where (formatfinal=? and rcr=? and id=?)";
    final static String SQL_UPDATE_COUNT_RCR = "update produits_derives.rcr_datas set count=? where rcr=?";
    final static String SQL_INSERT_COUNT_RCR = "insert into produits_derives.rcr_datas (rcr, count, isconvention) values(?,?,?)";
    final static String SQL_LIST_ILN = "select rcr, iln, short_name from lib_profile where main_library<>'Y' and not (short_name like 'SUP/%' or short_name like 'SUP /%') and iln=? order by short_name";
    final static String SQL_NOTMAIN_RCR_TO_ILN = "select rcr, iln, short_name from lib_profile where main_library<>'Y' and not (short_name like 'SUP/%' or short_name like 'SUP /%')";
    final static String SQL_ISMAIN_RCR_TO_ILN_MAIN = "select rcr, iln, short_name from lib_profile where main_library='Y' and not (short_name like 'SUP/%' or short_name like 'SUP /%')";
    final static String SQL_LIST_LAYOUTS = "select * from produits_derives.layout";
    final static String SQL_LIST_RCR = "select * from produits_derives.rcr_datas";
    final static String SQL_LIST_EXCLUSION = "select * from produits_derives.exclusion";
    final static String SQL_READ_JOB = "select * from produits_derives.demandes where id=?";
    final static String ORDER_BY_PATTERN = "ORDER_BY_PATTERN";
    final static String DEFAULT_ORDER_BY = "id desc, rcr";
    final static String SORT_BY_DATEEXTRACTION_ASC = "dateextraction";
    final static String SORT_BY_DATEEXTRACTION_DESC = "-dateextraction";
    final static String ORDER_BY_DATE_EXTRACTION_ASC = "dateextraction, " + DEFAULT_ORDER_BY;
    final static String ORDER_BY_DATE_EXTRACTION_DESC = "dateextraction desc, " + DEFAULT_ORDER_BY;
    final static String SQL_LIST_ALL_JOBS_LIMITED = "Select * from ( Select a.*, rownum rnum From ( select * from produits_derives.demandes order by " + ORDER_BY_PATTERN + ") a where rownum <= ?) where rnum >= ?";
    final static String SQL_LIST_JOBS_FOR_ILN_LIMITED = "Select * from ( Select a.*, rownum rnum From ( select * from produits_derives.demandes where iln = ? order by " + ORDER_BY_PATTERN + ") a where rownum <= ?) where rnum >= ?";
    final static String SQL_COUNT_ALL_JOBS = "select count(*) from produits_derives.demandes";
    final static String SQL_LIST_ALL_JOBS = "select * from produits_derives.demandes order by " + ORDER_BY_PATTERN;
    final static String SQL_LIST_JOBS_FOR_ILN = "select * from produits_derives.demandes where iln=? order by " + ORDER_BY_PATTERN;
    final static String SQL_COUNT_JOBS_FOR_ILN = "select count(*) from produits_derives.demandes where iln=?";
    final static String SQL_USER_PROFILE = "select user_num, user_group, short_name, library, login_allowed, password from user_profile where user_key=?";
    final private static String REGEX = "(.*)" + ORDER_BY_PATTERN + "(.*)";
    final private static Pattern PATTERN = Pattern.compile(REGEX);
    static LogHelper logger = new LogHelper(QueryProduitsDerives.class);

    public static String prefixColumnName(String format) {
        return (COLUMN_PREFIX + format).toUpperCase();
    }

    /**
     *
     * Flaq in db a demdand for a new job, job will be collected by loop when searching for a SudocXML extraction to process (EXTAT_EXTRACTED='W' = waiting)
     *
     * @param iln
     * @param uniqueExportID
     * @param rcrS
     * @param withCollections
     * @param formatFinal
     * @param coordinateur
     * @param excludedDataFields
     * @param coordinateur
     * @param conn
     * @throws SQLException
     */
    public static void prepareExport(
            int iln,
            long uniqueExportID,
            Set<String> rcrS,
            boolean withCollections,
            String formatFinal,
            String coordinateur,
            Map<String, Set<String>> excludedDataFields,
            Map<String, String> layouts,
            Map<String, Integer> lowLimitMap,
            Map<String, Integer> highLimitMap,
            Map<String, String> shortNamesMap,
            Connection conn
    ) throws SQLException {

        if (layouts == null) {
            throw new SQLException("layouts MUST not be null !");
        }

        logger.debug("iln", Integer.toString(iln));

        Date dateextraction = new Date(uniqueExportID);
        SimpleDateFormat oracleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String dateString = oracleDateFormat.format(dateextraction);
        logger.debug("dateextraction", dateString);
        logger.debug("withCollections ", Boolean.toString(withCollections));
        logger.debug("formatFinal ", formatFinal);

        PreparedStatement statement = conn.prepareStatement(SQL_PREPARE_EXPORT);
        Iterator<String> iter = rcrS.iterator();
        while (iter.hasNext()) {
            //for each RCR

            String rcr = iter.next();

            String shortName = shortNamesMap.get(rcr);
            logger.debug("shortName ", shortName);

            statement.setString(1, Long.toString(uniqueExportID));
            statement.setString(2, Integer.toString(iln));
            statement.setString(3, coordinateur);
            statement.setString(4, rcr);
            statement.setString(5, shortName);
            statement.setString(6, dateString);
            statement.setString(7, null);
            statement.setString(8, formatFinal);
            int boolValue = (withCollections) ? 1 : 0; //Oracle boolean
            statement.setString(9, Integer.toString(boolValue));

            if (excludedDataFields == null) {
                // nothing to exclude for any RCR
                statement.setString(10, null);
            } else {
                // some RCR have excluded data fields
                Set<String> dataFields = excludedDataFields.get(rcr);
                if (dataFields == null) {
                    // nothing to exclude for THIS specific RCR
                    statement.setString(10, null);
                } else {
                    // Data fields to exclude for this RCR
                    statement.setString(10, dataFields.toString());
                }
            }

            String layout = layouts.get(rcr);
            statement.setString(11, layout);

            Integer lowLimit = lowLimitMap.get(rcr);
            Integer highLimit = highLimitMap.get(rcr);
            statement.setString(12, (lowLimit == null) ? null : Integer.toString(lowLimit));
            statement.setString(13, (highLimit == null) ? null : Integer.toString(highLimit));

            statement.addBatch();
        }

        statement.executeBatch();
    }

    /**
     *
     * excludeddatafields and layout columns will be processed in some cases with "html pivot/slk" workers
     *
     * @param columnDestinationName
     * @param conn
     * @return
     * @throws SQLException
     */
    public static SimpleImmutableEntry<PreparedStatement, ResultSet> listWaitingToExtract(
            String columnDestinationName,
            Connection conn
    ) throws SQLException {

        // Stats
        long time0, time1;
        time0 = System.currentTimeMillis();

        String SQL_LIST_TO_EXTRACT = "select id,rcr,formatfinal,aveccollections,low_limit,high_limit from produits_derives.demandes where "
                + columnDestinationName
                + "='"
                + STATUS_WAIT
                + "' order by id";

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        statement = conn.prepareStatement(SQL_LIST_TO_EXTRACT);

        logger.debug("executing query : queryString = " + SQL_LIST_TO_EXTRACT);
        resultSet = statement.executeQuery();
        time1 = System.currentTimeMillis();
        logger.debug("query executed in " + (time1 - time0) + "ms");

        return new SimpleImmutableEntry<PreparedStatement, ResultSet>(statement, resultSet); // A Pair
    }

    public static void reportExportErrors(
            long uniqueExportID,
            Map<String, String> errorsRCR,
            String formatFinal,
            Connection conn
    ) throws SQLException {

        logger.debug("formatFinal ", formatFinal);

        PreparedStatement statement = conn.prepareStatement(SQL_ETAT_EXTRACTED_EXCEPTION);
        Iterator<String> iter = errorsRCR.keySet().iterator();
        while (iter.hasNext()) {

            String rcr = iter.next();
            String excpetionMessage = errorsRCR.get(rcr);

            statement.setString(1, excpetionMessage);
            statement.setString(2, formatFinal);
            statement.setString(3, rcr);
            statement.setString(4, Long.toString(uniqueExportID));
            statement.addBatch();
        }

        statement.executeBatch();
    }

    public static void updateCounted(
            Map<String, Long> updateRCRs,
            boolean insert,
            Connection conn
    ) throws SQLException {

        PreparedStatement statement = null;
        if (insert) {
            statement = conn.prepareStatement(SQL_INSERT_COUNT_RCR);
        } else {
            statement = conn.prepareStatement(SQL_UPDATE_COUNT_RCR);
        }

        Iterator<String> iter = updateRCRs.keySet().iterator();
        while (iter.hasNext()) {
            String rcr = iter.next();
            Long count = updateRCRs.get(rcr);

            if (insert) {
                statement.setString(1, rcr);
                statement.setLong(2, count);
                int boolValue = (false) ? 1 : 0; //Oracle boolean
                statement.setString(3, Integer.toString(boolValue));
            } else {
                statement.setLong(1, count);
                statement.setString(2, rcr);
            }

            statement.addBatch();
        }

        statement.executeBatch();
    }

    public static SimpleImmutableEntry<PreparedStatement, ResultSet> updateOKExtractStatus(
            long uniqueExportID,
            String rcr,
            int count,
            String formatFinal,
            Connection conn
    ) throws SQLException {

        Map<Integer, String> params = new HashMap<Integer, String>();

        params.put(1, Integer.toString(count));
        params.put(2, formatFinal);
        params.put(3, rcr);
        params.put(4, Long.toString(uniqueExportID));

        logger.debug("count ", Integer.toString(count));
        logger.debug("formatFinal ", formatFinal);

        String columnName = prefixColumnName(formatFinal);

        //Build SQL by switching on the given final format
        StringBuilder sql = new StringBuilder(PARTIAL_SQL_ETAT_EXTRACTED_OK);
        sql.append(", " + columnName + "='" + STATUS_WAIT + "'");
        if ("slk".equals(formatFinal)) {
            //if .SLK then .xhtml not needed !
            sql.append(", etat_xhtml='" + STATUS_IGNORE + "'");
        } else {
            sql.append(", etat_xhtml='" + STATUS_WAIT + "'");
        }
        sql.append(ENDING_SQL_ETAT_EXTRACTED_OK);

        logger.debug("updateOKExtractStatus SQL= " + sql.toString());

        return ConnectionHelper.executeQuery(conn, sql.toString(), params);
    }

    /**
     * Print the byte array contents
     */
    static void showValue(byte[] bytes) throws SQLException {
        if (bytes == null)
            System.out.println("null");
        else if (bytes.length == 0)
            System.out.println("empty");
        else {
            for (int i = 0; i < bytes.length; i++)
                System.out.print((bytes[i] & 0xff) + " ");
            System.out.println();
        }
    }

    public static SimpleImmutableEntry<PreparedStatement, ResultSet> listILN(int iln, Connection conn) throws SQLException {

        // Stats
        long time0, time1;
        time0 = System.currentTimeMillis();

        Map<Integer, String> params = new HashMap<Integer, String>();
        params.put(1, Integer.toString(iln));

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        statement = conn.prepareStatement(SQL_LIST_ILN);
        statement.setInt(1, iln);

        logger.info("executing query : queryString = " + SQL_LIST_ILN);
        resultSet = statement.executeQuery();
        time1 = System.currentTimeMillis();
        logger.info("query executed in " + (time1 - time0) + "ms");

        return new SimpleImmutableEntry<PreparedStatement, ResultSet>(statement, resultSet); // A Pair
    }

    public static SimpleImmutableEntry<PreparedStatement, ResultSet> listJobs(
            Integer iln,
            int start,
            int count,
            String sortOrderBy,
            Connection conn
    ) throws SQLException {

        String query = null;

        Map<Integer, String> params = new HashMap<Integer, String>();
        if (iln != null) {
            params.put(1, iln.toString());
            query = SQL_LIST_JOBS_FOR_ILN;

            if (start >= 0 && count > 0) {
                int rownumMIN = start + 1;
                int rownumMAX = start + count;
                logger.debug("rownum between " + rownumMIN + " and " + rownumMAX);
                query = SQL_LIST_JOBS_FOR_ILN_LIMITED; //limit query
                params.put(2, Integer.toString(rownumMAX));
                params.put(3, Integer.toString(rownumMIN));
            }
        } else {
            //iln = null in admin only
            query = SQL_LIST_ALL_JOBS;

            if (start >= 0 && count > 0) {

                int rownumMIN = start + 1;
                int rownumMAX = start + count;
                logger.debug("rownum between " + rownumMIN + " and " + rownumMAX);
                query = SQL_LIST_ALL_JOBS_LIMITED; //limit query
                params.put(1, Integer.toString(rownumMAX));
                params.put(2, Integer.toString(rownumMIN));
            }
        }

        // Get the correct Order By;
        Matcher matcher = PATTERN.matcher(query);
        if (matcher.find()) {
            if (SORT_BY_DATEEXTRACTION_ASC.equals(sortOrderBy)) {
                query = matcher.group(1) + ORDER_BY_DATE_EXTRACTION_ASC
                        + matcher.group(2);
            } else {
                if (SORT_BY_DATEEXTRACTION_DESC.equals(sortOrderBy)) {
                    query = matcher.group(1) + ORDER_BY_DATE_EXTRACTION_DESC
                            + matcher.group(2);
                } else {
                    // default if no sort given
                    query = matcher.group(1) + DEFAULT_ORDER_BY
                            + matcher.group(2);
                }
            }
        }

        return ConnectionHelper.executeQuery(conn, query, params);
    }

    public static SimpleImmutableEntry<PreparedStatement, ResultSet> readJob(Long orderId, Connection conn) throws SQLException {
        Map<Integer, String> params = new HashMap<Integer, String>();
        params.put(1, Long.toString(orderId));
        return ConnectionHelper.executeQuery(conn, SQL_READ_JOB, params);
    }

    public static SimpleImmutableEntry<PreparedStatement, ResultSet> countJobs(Integer iln, Connection conn) throws SQLException {

        String query = SQL_COUNT_ALL_JOBS;

        Map<Integer, String> params = new HashMap<Integer, String>();
        if (iln != null) {
            params.put(1, iln.toString());
            query = SQL_COUNT_JOBS_FOR_ILN;
        }

        return ConnectionHelper.executeQuery(conn, query, params);
    }

    public static SimpleImmutableEntry<PreparedStatement, ResultSet> readUserProfile(String userKey, Connection conn) throws SQLException {
        Map<Integer, String> params = new HashMap<Integer, String>();
        params.put(1, userKey);
        return ConnectionHelper.executeQuery(conn, SQL_USER_PROFILE, params);
    }

    public static SimpleImmutableEntry<PreparedStatement, ResultSet> listRCRToILN(boolean isMain, Connection conn) throws SQLException {

        Map<Integer, String> params = new HashMap<Integer, String>();
        String query = null;
        if (isMain) {
            query = SQL_ISMAIN_RCR_TO_ILN_MAIN;
        } else {
            query = SQL_NOTMAIN_RCR_TO_ILN;
        }
        return ConnectionHelper.executeQuery(conn, query, params);
    }

    public static SimpleImmutableEntry<PreparedStatement, ResultSet> listLayouts(Connection conn) throws SQLException {
        Map<Integer, String> params = new HashMap<Integer, String>();
        return ConnectionHelper.executeQuery(conn, SQL_LIST_LAYOUTS, params);
    }

    public static SimpleImmutableEntry<PreparedStatement, ResultSet> listRCRs(Connection conn) throws SQLException {
        Map<Integer, String> params = new HashMap<Integer, String>();
        return ConnectionHelper.executeQuery(conn, SQL_LIST_RCR, params);
    }

    public static SimpleImmutableEntry<PreparedStatement, ResultSet> listEXCLUSION(Connection conn) throws SQLException {
        Map<Integer, String> params = new HashMap<Integer, String>();
        return ConnectionHelper.executeQuery(conn, SQL_LIST_EXCLUSION, params);
    }

    /**
     *
     * read produits_derives.rcr_datas and return number of notices per RCR
     *
     * @param conn
     * @return
     */
    public static Map<String, Long> rcrAlreadyCounted(Connection conn) {
        Map<String, Long> result = new HashMap<String, Long>();

        // Stats
        long time0, time1;

        SimpleImmutableEntry<PreparedStatement, ResultSet> pair = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            pair = listRCRs(conn);

            resultSet = pair.getValue();
            statement = pair.getKey();

            time0 = System.currentTimeMillis();

            String rcr = null;
            while (resultSet.next()) {
                rcr = resultSet.getString("RCR");
                Long count = resultSet.getLong("COUNT");
                result.put(rcr, count);
            }

            time1 = System.currentTimeMillis();
            logger.info("end of resulset "
                    + (time1 - time0) + "ms");

        } catch (SQLException e) {
            logger.error(e.getMessage());
        } finally {
            ConnectionHelper.release(resultSet, statement);
            resultSet = null;
            statement = null;
        }
        return result;
    }
}
