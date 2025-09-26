package fr.abes.derives.cli;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.AbstractMap.SimpleImmutableEntry;

import fr.abes.utils.BufferedRW;
import fr.abes.utils.LogHelper;

public class ConnectionHelper {
	
	private static LogHelper logger = new LogHelper(ConnectionHelper.class);
	
	/**
	 * TC4 : DO NOT use in container environment, only in standalone with :
	 * -Djdbc.drivers=oracle.jdbc.driver.OracleDriver
	 * 
	 * @return
	 * @throws SQLException
	 */
	protected static Connection getConnectionThinDriver(String user, String password, String host, String sid) throws SQLException {

		final Properties info = new java.util.Properties();
		info.put("user", user);
		info.put("password", password);
		Connection conn = DriverManager.getConnection(
				"jdbc:oracle:thin:@//"+host+":1521/"+sid, info);
		conn.setAutoCommit(false);
		conn
				.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
		logger.debug("conn getTransactionIsolation = "
				+ conn.getTransactionIsolation());
		return conn;

	}
	
	
	protected static void release(ResultSet resultSet, PreparedStatement statement) {

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
	
	
	private static SimpleImmutableEntry<PreparedStatement, ResultSet> executeQuery(
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

		logger.debug("executing query : queryString = "+query);
		// Stats
		long time0, time1;
		time0 = System.currentTimeMillis();		
		resultSet = statement.executeQuery();
		time1 = System.currentTimeMillis();
		logger.debug("query executed in " + (time1 - time0) + "ms");
		
		//!!!DO NOT try to use resultSet.getMetaData(); otherwise will generate an ORA-900 Invalid Sql Statement

		return new SimpleImmutableEntry<PreparedStatement, ResultSet>(
				statement, resultSet); // A Pair

	}
	
	/**
	 * 
	 * select while ignoring final format : multi-format (rtf,pdf, slk..) for same rcr gives one unique row (using DISTINCT)
	 * excludeddatafields and layout columns will be processed in some cases with "html pivot/slk" workers 
	 * 
	 * @param columnSourceName
	 * @param columnDestinationName
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	protected static SimpleImmutableEntry<PreparedStatement, ResultSet> listWaitingJobs(
			String columnSourceName, String columnDestinationName,
			Connection conn) throws SQLException {

		// Stats
		long time0, time1;
		time0 = System.currentTimeMillis();

		String SQL_LIST_TO_PROCESS = "select distinct id,rcr,rcr_shortname,excludeddatafields,aveccollections from produits_derives.demandes where " //multi-format (rtf,pdf, slk..) for same rcr gives one unique row
				+ columnSourceName
				+ "='"
				+ RcrJobWrapper.STATUS_OK
				+ "' and "
				+ columnDestinationName
				+ "='"
				+ RcrJobWrapper.STATUS_WAIT
				+ "' ";

		PreparedStatement statement = null;
		ResultSet resultSet = null;

		statement = conn.prepareStatement(SQL_LIST_TO_PROCESS);

		logger.debug("executing query : queryString = " + SQL_LIST_TO_PROCESS);
		resultSet = statement.executeQuery();
		time1 = System.currentTimeMillis();
		logger.debug("query executed in " + (time1 - time0) + "ms");

		return new SimpleImmutableEntry<PreparedStatement, ResultSet>(
				statement, resultSet); // A Pair

	}

	/**
	 * 
	 * for same rcr may give different rows with the SAME layout (if asked for pdf AND rtf for instance, pivot layout will be the same on the 2 jobs)
	 * 
	 * @param columnSourceName
	 * @param columnDestinationName
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	protected static SimpleImmutableEntry<PreparedStatement, ResultSet> listWaitingLayouts(String columnSourceName, String columnDestinationName,
			Connection conn) throws SQLException {
				
		// Stats
		long time0, time1;
		time0 = System.currentTimeMillis();

		String SQL_LIST_TO_PROCESS = "select id,rcr,layout from produits_derives.demandes where " 
				+ columnSourceName
				+ "='"
				+ RcrJobWrapper.STATUS_OK
				+ "' and "
				+ columnDestinationName
				+ "='"
				+ RcrJobWrapper.STATUS_WAIT
				+ "' ";

		PreparedStatement statement = null;
		ResultSet resultSet = null;

		statement = conn.prepareStatement(SQL_LIST_TO_PROCESS);

		logger.debug("executing query : queryString = " + SQL_LIST_TO_PROCESS);
		resultSet = statement.executeQuery();
		time1 = System.currentTimeMillis();
		logger.debug("query executed in " + (time1 - time0) + "ms");

		return new SimpleImmutableEntry<PreparedStatement, ResultSet>(
				statement, resultSet); // A Pair

	}
	
	/**
	 * 
	 * errors of a SAME worker (= same status column !) !!!
	 * 
	 * @param jobsErrors
	 * @param conn
	 * @throws SQLException
	 */
	protected static void reportErrors(Set<RcrJobWrapper> jobsErrors, File dirDestination, String formatFinal, Connection conn) throws SQLException {
		
		String columnName = RcrJobWrapper.getColumnName(dirDestination);
		logger.debug("columnName = "+columnName);
		
		Iterator<RcrJobWrapper> iter = jobsErrors.iterator();
		PreparedStatement statement = null;
		
		String sql = null;

		//intermediate common job or final job ?		
		if (formatFinal != null) {			
			//if given a final format, restrict update SQL to this row only
			
			sql = "update produits_derives.demandes set "+columnName+"='"+RcrJobWrapper.STATUS_EXCEPTION+"', exceptionmsg=? where (formatfinal=? and rcr=? and id=?)";
			statement = conn.prepareStatement(sql);
			while (iter.hasNext()) {

				RcrJobWrapper job = iter.next();			
				
				
				String exceptionMessage = job.getException().getMessage();

				statement.setString(1, exceptionMessage);
				statement.setString(2, formatFinal);				
				statement.setString(3, job.getRcr());
				statement.setString(4, Long.toString(job.getExportID()));
				statement.addBatch();
			}

			
		} else {
			//Common : we update all rows by ignoring final format
			sql = "update produits_derives.demandes set "+columnName+"='"+RcrJobWrapper.STATUS_EXCEPTION+"', exceptionmsg=? where (rcr=? and id=?)";
			statement = conn.prepareStatement(sql);
			while (iter.hasNext()) {

				RcrJobWrapper job = iter.next();			
				
				
				String exceptionMessage = job.getException().getMessage();

				statement.setString(1, exceptionMessage);
				statement.setString(2, job.getRcr());
				statement.setString(3, Long.toString(job.getExportID()));
				statement.addBatch();
			}

		}
		
		

		
				
		statement.executeBatch();
		conn.commit();
		
		try {
			if (statement != null) {
				statement.close();
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		statement = null;
	}
	
	/**
	 * jobs of a SAME worker !!!
	 * 
	 * @param dirDestination
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	public static SimpleImmutableEntry<PreparedStatement, ResultSet> updateOKStatus(RcrJobWrapper job, File dirDestination, String formatFinal, long bytesLength, Connection conn) throws SQLException {
		
		String columnName = RcrJobWrapper.getColumnName(dirDestination);
		logger.debug("columnName = "+columnName);
		
		Map<Integer, String> params = new HashMap<Integer, String>();
		

		if (formatFinal != null) {
			
			String md5Hash = BufferedRW.hashMD5(Long.toString(job.getExportID())+job.getRcr()+formatFinal+Long.toString(bytesLength));

			String sql = "update produits_derives.demandes set " + columnName+ "='" + RcrJobWrapper.STATUS_OK+ "', byteslength=?, md5hash=? where (formatfinal=? and rcr=? and id=?)";
			params.put(1, Long.toString(bytesLength));
			params.put(2, md5Hash);
			params.put(3, formatFinal);
			params.put(4, job.getRcr());
			params.put(5, Long.toString(job.getExportID()));

			return executeQuery(conn, sql, params);

		} else {

			String sql = "update produits_derives.demandes set " + columnName+ "='" + RcrJobWrapper.STATUS_OK+ "' where (rcr=? and id=?)";
			params.put(1, job.getRcr());
			params.put(2, Long.toString(job.getExportID()));

			return executeQuery(conn, sql, params);

		}		

		


				
		

	}
	
}
