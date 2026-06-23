package fr.abes.derives.cli;

import fr.abes.derives.xsltransform.TransformWrapper;
import fr.abes.technic.RCRUtils;
import fr.abes.utils.BufferedRW;
import fr.abes.utils.HealthHeartbeat;
import fr.abes.utils.LogHelper;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerConfigurationException;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.*;

public class Chain {

	private static LogHelper logger = new LogHelper(Chain.class);
	
	final public static String DOCBASE="docBase";
	
	final public static String HOMEDIR=System.getenv("EOD_HOME");
	final private static String RUNTIMEDIR=HOMEDIR; //Production config

	
	final private static String DATACODESFILENAME = RUNTIMEDIR+"/"+"DonnesCodesUnm.txt";
	
	
	private static Properties configuration = null;	
	
	private static String DBHOSTNAME = null;		
	private static String LOGINPRODUITSDERIVES = null;
	private static String PASSWORDPRODUITSDERIVES = null;
	private static String SERVICEABESDB = null;
	
	/**
	 * use a java.utilScanner to parse string using "," as delimiter
	 * 
	 * @param aString
	 * @return a set of parsed elements
	 */
	private static Set<String> scannerStringToSet(String aString) {
		
		logger.debug("string to parse ="+aString);
		
		Set<String> result=new HashSet<String>();
		Scanner sc = new Scanner(aString);
			sc.useDelimiter(",");
			while (sc.hasNext()) {
		          String parsed = sc.next();
		          parsed=parsed.trim();
		          logger.debug("parsed = "+parsed);
		          result.add(parsed);
		      }
		sc.close();
		return result;		
	}
	
	
	/**
	 * 
	 */
	public static void readConfiguration() {
		
		String KEY_DBHOSTNAME = "dbHostName";
		String KEY_LOGINPRODUITSDERIVES = "loginProduitsDerives";
		String KEY_PASSWORDPRODUITSDERIVES = "passwordProduitsDerives";
		String KEY_SERVICEABESDB = "serviceABESDB";
		
		String fileName = HOMEDIR+"/conf/"+"config.xml";
		
		InputStream input = null;
		boolean error = false;
		String errorMessage = null;
		
		
		File file = null;
		file = new File(fileName);
		if (!file.exists()) {
			errorMessage = "File does not exist : "+file.getPath(); 
			logger.error(errorMessage);
			System.err.print(errorMessage);
			System.exit(1);			
		}	
		
		try {
			input = new FileInputStream(file);
			configuration = new Properties();
			configuration.loadFromXML(input);
			configuration.list(System.out);
			
			/*
			 * Base Exports � la demande
			 */

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
			System.err.print("Error : "+errorMessage);
			System.exit(1);
		}
		
	}

	

	/**
	 * 
	 * Connexion � la database pour calculer en fonction des status la liste des demandes en attente de traitement 
	 * 
	 * @param conn
	 * @param prefixFileName
	 * @param dirSource
	 * @param dirDestination
	 * @return
	 */
	private static List<RcrJobWrapper> listRcrJobsWaiting(Connection connProduitsDerives, File dirSource, File dirDestination) {
		
		logger.debug("listRcrJobsWaiting");

		SimpleImmutableEntry<PreparedStatement, ResultSet> pair = null;	
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		
		List<RcrJobWrapper> result = new ArrayList<RcrJobWrapper>();
		try {
			
			// directories are valid ?
			File runtimeSourceDir = new File(RUNTIMEDIR+"/"+DOCBASE+"/"+dirSource);
			File runtimeDestinationDir = new File(RUNTIMEDIR+"/"+DOCBASE+"/"+dirDestination);
			BufferedRW.validateDirectory(runtimeSourceDir);
			logger.debug(runtimeSourceDir+" is a valid directory");
			BufferedRW.validateDirectory(runtimeDestinationDir);
			logger.debug(runtimeDestinationDir+" is a valid directory");
			
			pair = ConnectionHelper.listWaitingJobs(RcrJobWrapper.getColumnName(dirSource), RcrJobWrapper.getColumnName(dirDestination), connProduitsDerives);
			connProduitsDerives.commit();
			
			resultSet = pair.getValue();
			statement = pair.getKey();

			while (resultSet.next()) {
				long exportID = resultSet.getLong(1);
				String rcr = resultSet.getString(2);
				String shortName = resultSet.getString(3);
				String excludedAsString = resultSet.getString(4);
				boolean withCollections = resultSet.getBoolean(5);
				
				Set<String> excludedDataFields  = null;
				if (excludedAsString!=null) {
					//remove brackets before parse
					excludedAsString = excludedAsString.replaceAll("\\[", "");
					excludedAsString = excludedAsString.replaceAll("\\]", "");
					excludedDataFields  = scannerStringToSet(excludedAsString);
				}
				
				
				RcrJobWrapper job = new RcrJobWrapper(exportID, rcr, shortName, excludedDataFields, withCollections, null, dirSource, dirDestination, RUNTIMEDIR); 				
				result.add(job);
			}
			
		} catch (SQLException e) {
			logger.error(e.getMessage());
			System.exit(1);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			System.exit(1);
		}
		
		finally {
			
			ConnectionHelper.release(resultSet, statement);
			resultSet = null;
			statement = null;
			
			
		}
		
		logger.debug("" + result.size() + " database jobs are waiting for being processed");

		return result;

	}

	/**
	 * 
	 * @param connProduitsDerives
	 * @param dirSource
	 * @param dirDestination
	 */
	private static Map<String,String> listLayoutsWaiting(Connection connProduitsDerives, File dirSource, File dirDestination) {
		

		
		logger.debug("listLayoutsWaiting");

		SimpleImmutableEntry<PreparedStatement, ResultSet> pair = null;	
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		
		Map<String,String> result= new HashMap<String,String>();
		
		//List<RcrJobWrapper> result = new ArrayList<RcrJobWrapper>();
		try {
			
			// directories are valid ?
			File runtimeSourceDir = new File(RUNTIMEDIR+"/"+DOCBASE+"/"+dirSource);
			File runtimeDestinationDir = new File(RUNTIMEDIR+"/"+DOCBASE+"/"+dirDestination);
			BufferedRW.validateDirectory(runtimeSourceDir);
			logger.debug(runtimeSourceDir+" is a valid directory");
			BufferedRW.validateDirectory(runtimeDestinationDir);
			logger.debug(runtimeDestinationDir+" is a valid directory");
			
			pair = ConnectionHelper.listWaitingLayouts(RcrJobWrapper.getColumnName(dirSource), RcrJobWrapper.getColumnName(dirDestination), connProduitsDerives);
			connProduitsDerives.commit();
			
			resultSet = pair.getValue();
			statement = pair.getKey();

			while (resultSet.next()) {
				long exportID = resultSet.getLong(1);
				String rcr = resultSet.getString(2);
				String layout = resultSet.getString(3);
				
				//TODO TMX : update map of jobs
				logger.debug("exportID="+exportID+ "rcr="+rcr+" layout="+layout);
				result.put(rcr+"_"+exportID, layout);
			}
			
		} catch (SQLException e) {
			logger.error(e.getMessage());
			System.exit(1);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			System.exit(1);
		}
		
		finally {
			
			ConnectionHelper.release(resultSet, statement);
			resultSet = null;
			statement = null;
			
			
		}
		
		return result;
		

	}
	
	private static String reportErrors(Set<RcrJobWrapper> jobsErrors,
			IFileWorker fileWorker, String formatFinal, Connection connProduitsDerives) {
		
		
		
		if (jobsErrors.size() > 0) {
			
			// report in database COMMON faulty jobs for this worker			
			try {
				ConnectionHelper.reportErrors(jobsErrors, fileWorker.getTargetDir(), formatFinal, connProduitsDerives);
			} catch (SQLException e) {
				logger.error(e.getMessage());
				System.exit(1);
			}

			// report errors
			StringBuilder sb = new StringBuilder();
			sb.append("reporting ").append(jobsErrors.size()).append(" errors for worker ").append(fileWorker).append("\n");

			for (Iterator<RcrJobWrapper> iterator = jobsErrors.iterator(); iterator
					.hasNext();) {
				RcrJobWrapper job = iterator.next();
				sb.append(job).append(" ERROR= ").append(job.getException())
						.append("\n");
			}

			return sb.toString();
		} else {
			return null;
		}

	}

	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HealthHeartbeat.touch();
		
		logger.info("java.home=",System.getProperty("java.home"));
		logger.info("java.vendor=",System.getProperty("java.vendor"));
		logger.info("java.vendor.url=",System.getProperty("java.vendor.url"));
		logger.info("java.version=",System.getProperty("java.version"));
		logger.info("java.class.path=",System.getProperty("java.class.path"));
		logger.info("java.version=",System.getProperty("java.version"));
		logger.info("java.class.version=",System.getProperty("java.class.version"));
		logger.info("file.encoding=",System.getProperty("file.encoding"));		
		logger.info("docBaseDir=",DOCBASE);
		
		if (HOMEDIR == null || "".equals(HOMEDIR) || HOMEDIR.contains(RCRUtils.RCRSPLITFILENAME) ) {
			String errorMessage = "HOMEDIR is undefined or contains illegal \""+RCRUtils.RCRSPLITFILENAME+"\" character !";
			logger.error(errorMessage);
			System.err.print(errorMessage);
			System.exit(1);
		}
		
		logger.info("Home Directory="+HOMEDIR);
		logger.info("Runtime Directory="+RUNTIMEDIR);

		File cleanedDir = null;
		File extractedDir = null;
		File groupedDir = null;
		File filteredDir = null;
		File sortedDir = null;
		File xhtmlDir = null;
		File rtfDir = null;
		File pdfDir = null;
		File sylkDir = null;

		String EXTRACTEDDIRECTORY =null;
		String CLEANEDDIRECTORY = null;
		String GROUPEDDIRECTORY = null;
		String FILTEREDDIRECTORY = null;
		String SORTEDDIRECTORY = null;
		String XHTMLDIRECTORY = null;
		String RTFDIRECTORY = null;
		String PDFDIRECTORY = null;
		String SYLKDIRECTORY = null;

		if (args.length > 0) {
			EXTRACTEDDIRECTORY = args[0];
		}
		if (args.length > 1) {
			CLEANEDDIRECTORY = args[1];
		}
		if (args.length > 2) {
			GROUPEDDIRECTORY = args[2];
		}
		if (args.length > 3) {
			FILTEREDDIRECTORY = args[3];
		}
		if (args.length > 4) {
			SORTEDDIRECTORY = args[4];
		}
		if (args.length > 5) {
			XHTMLDIRECTORY = args[5];
		}
		if (args.length > 6) {
			RTFDIRECTORY = args[6];
		}
		if (args.length > 7) {
			PDFDIRECTORY = args[7];
		}		
		if (args.length > 8) {
			SYLKDIRECTORY = args[8];
		}
		

		extractedDir = new File(EXTRACTEDDIRECTORY);
		cleanedDir = new File(CLEANEDDIRECTORY);
		groupedDir = new File(GROUPEDDIRECTORY);
		filteredDir = new File(FILTEREDDIRECTORY);
		sortedDir = new File(SORTEDDIRECTORY);
		xhtmlDir = new File(XHTMLDIRECTORY);
		rtfDir = new File(RTFDIRECTORY);
		pdfDir = new File(PDFDIRECTORY);
		sylkDir = new File(SYLKDIRECTORY); 

		IFileWorker cleaned = null;
		IFileWorker grouped = null;
		IFileWorker filtered = null;
		IFileWorker sorted = null;
		IFileWorker xhtml = null;
		IFileWorker rtf = null;
		IFileWorker pdf = null;
		IFileWorker slk = null;


		Map<String, String> dataCodes = SAXWorker.readDataCodes(DATACODESFILENAME, "UTF-8");

		try {
			
			cleaned = new SAXWorker(dataCodes, extractedDir, cleanedDir);
			
			grouped = new XSLTWorker(RUNTIMEDIR,XSLTWorker.XSL_DATAGROUPLOC,
					TransformWrapper.OUTPUT_XML, cleanedDir, groupedDir);

			filtered = new XSLTWorker(RUNTIMEDIR,XSLTWorker.XSL_FILTER_RCR,
					TransformWrapper.OUTPUT_XML, groupedDir, filteredDir);

			sorted = new XSLTWorker(RUNTIMEDIR,XSLTWorker.XSL_SORT,
					TransformWrapper.OUTPUT_XML, filteredDir, sortedDir);

			logger.debug("before xhtml = new PivotXSLTWorker() RUNTIMEDIR="+RUNTIMEDIR+" sortedDir="+sortedDir+" xhtmlDir="+xhtmlDir );
			xhtml = new PivotXSLTWorker(RUNTIMEDIR,
					TransformWrapper.OUTPUT_XHTML, sortedDir,
					xhtmlDir); 
			logger.debug("after xhtml  = new PivotXSLTWorker()" );
			
			rtf = new RTFWorker(RUNTIMEDIR,xhtmlDir,
					rtfDir,BufferedRW.EXT_DOT_XHTML);			
			
			pdf = new PDFWorker(RUNTIMEDIR,xhtmlDir,
					pdfDir,BufferedRW.EXT_DOT_XHTML);
			
			slk = new SYLKWorker(RUNTIMEDIR, sortedDir, sylkDir);
			
			logger.info("all workers instantiated");

		} catch (TransformerConfigurationException e) {
			logger.error(e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			logger.error(e.getMessage());
			System.exit(1);
		} catch (SAXException e) {
			logger.error(e.getMessage());
			System.exit(1);
		}

		// ORDERED chain of process
		List<IFileWorker> workers = new ArrayList<IFileWorker>();
		workers.add(cleaned);
		workers.add(grouped);
		workers.add(filtered);
		workers.add(sorted);
		workers.add(xhtml);
		workers.add(rtf);
		workers.add(pdf);
		workers.add(slk);
		
		readConfiguration();

		
		Connection connProduitsDerives = null;
		try {
			// TC4 need  -Djdbc.drivers=oracle.jdbc.driver.OracleDriver
			connProduitsDerives = ConnectionHelper.getConnectionThinDriver(LOGINPRODUITSDERIVES, PASSWORDPRODUITSDERIVES, DBHOSTNAME, SERVICEABESDB);
			logger.info("connProduitsDerives getConnection");
		} catch (SQLException e) {
			String errorMessage = "Impossible de lancer le programme car "+SERVICEABESDB+" sur "+DBHOSTNAME+" refuse la connexion ! : "+e.getMessage();
			logger.error(errorMessage);
			System.err.print(errorMessage);
			System.exit(1);
		}
		
		

		do {// infinite loop
			HealthHeartbeat.touch();

			for (IFileWorker fileWorker : workers) { // for each worker of the chain
				
				//Intermediate files are common. NOT final format files ! (rtf,pdf, slk...)
				String formatFinal = AbstractFileWorker.finalRendererFormat(fileWorker);

				List<RcrJobWrapper> jobs = null;
				
				jobs = listRcrJobsWaiting(connProduitsDerives, fileWorker
						.getSourceDir(), fileWorker.getTargetDir());
				
				Map<String,String> layouts = null;
				String keyLayout = null;
				String layout = null;
				

				String classNameOfWorker = fileWorker.getClass().getName();
				if (!jobs.isEmpty() && (PivotXSLTWorker.class.getName().equals(classNameOfWorker) || SYLKWorker.class.getName().equals(classNameOfWorker))) {
					//TODO TMX : refactor by create intermediate class "JobBasedWorker" which will share this part of code					
					logger.debug(fileWorker.getClass().getName()
							+ " need to read additional features for "
							+ jobs.size() + " jobs");
					logger.debug("layouts Map read from DB");
					layouts = listLayoutsWaiting(connProduitsDerives, fileWorker
							.getSourceDir(), fileWorker.getTargetDir());					
					for (RcrJobWrapper rcrJobWrapper : jobs) {
						keyLayout = rcrJobWrapper.getRcr()+"_"+rcrJobWrapper.getExportID();
						layout = layouts.get(keyLayout);
						logger.debug("keyLayout="+keyLayout+" layout="+layout);
						rcrJobWrapper.setLayout(layout);						
					}
				}

				Set<RcrJobWrapper> jobsErrors = new HashSet<RcrJobWrapper>();
				
					
					
				Reader br = null;
				Writer writer = null;
				File tmpOut = null;
				
				SimpleImmutableEntry<PreparedStatement, ResultSet> pair = null;
				PreparedStatement statement = null;
				ResultSet resultSet = null;

					for (RcrJobWrapper job : jobs) { //for each waiting rcr job demand
						HealthHeartbeat.touch();
						boolean rollback = false;
						String rollbackMessage =null;

										
						try {
							
						Set<String> excludedDataFields = job.getExcludedDataFields();
						layout = job.getLayout();
						String shortName = job.getShortName();
						boolean withCollections = job.isWithCollections();
						
						File f = new File(job.getFileName());
						long bytesLength = AbstractFileWorker.doJob(fileWorker, shortName, excludedDataFields, withCollections, layout, f, br,
								writer, tmpOut, RUNTIMEDIR+"/"+DOCBASE);

						pair = ConnectionHelper.updateOKStatus(job, fileWorker.getTargetDir(), formatFinal, bytesLength, connProduitsDerives);
						
						resultSet = pair.getValue();
						statement = pair.getKey();
						
						
						} catch (FileNotFoundException e) {
							rollbackMessage =e.getClass().getName()+" : "+e.getMessage(); 
							rollback = true;
						} catch (UnsupportedEncodingException e) {
							rollbackMessage =e.getMessage(); 
							rollback = true;
						} catch (IOException e) {
							rollbackMessage =e.getMessage(); 
							rollback = true;
						} catch (WorkerException e) {
							rollbackMessage =e.getMessage(); 
							rollback = true;
						} catch (SQLException e) {
							rollbackMessage =e.getMessage(); 
							rollback = true;
						}

						finally {

							try {
								if (writer != null) {
									writer.close();
								}
								if (br != null) {
									br.close();
								}

								if (tmpOut != null) {
									tmpOut.delete();
								}

							} catch (IOException e) {
								rollbackMessage =e.getMessage(); 
								rollback = true;
							}
							
						if (rollback) {
							logger.warn(" ! need to roll back transaction...  ");
							// store jobs causing errors for future reporting
							job.setException(new Exception(rollbackMessage));
							jobsErrors.add(job);
							try {
								connProduitsDerives.rollback();
								logger.warn("transaction rolled back !");
							} catch (SQLException e) {
								logger.error(e.getMessage());
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
								System.exit(1);
							}
						}
							
							ConnectionHelper.release(resultSet, statement);
							resultSet = null;
							statement = null;
							
						}
						
					}
					
					String reporting = reportErrors(jobsErrors, fileWorker, formatFinal, connProduitsDerives);
					if (reporting!=null) {logger.warn(reporting);}
										
					jobs = null;
					jobsErrors = null;
				
			}

			// pause before expecting new XML files have been extracted from database
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				logger.debug(e.getMessage());
			}
		} while (true);
	}

}
