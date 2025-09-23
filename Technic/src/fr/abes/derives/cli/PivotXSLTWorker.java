package fr.abes.derives.cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Set;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import fr.abes.derives.xsltransform.TransformWrapper;
import fr.abes.utils.BufferedRW;
import fr.abes.utils.LogHelper;

public class PivotXSLTWorker extends XSLTWorker {

	private static LogHelper logger = new LogHelper(PivotXSLTWorker.class);
	final public static String PARAMNAME_EXCLUDED_DATAFIELDS = "excludeddatafields";
	final public static String PARAMNAME_SHORTNAME = "shortname";
	final public static String PARAMNAME_WITHCOLLECTIONS = "withcollections";
	final public static String PARAMNAME_RUNTIMEDIR = "runtimedir";

	public PivotXSLTWorker(String paramRuntimeDir, int paramOutPutMethod,
			File sourceDir, File targetDir)
			throws TransformerConfigurationException, IOException {
		super(paramRuntimeDir, null, paramOutPutMethod,
				sourceDir, targetDir);
	}

	@Override
	public Writer processFile(Reader bufferedReader, File tmpOut,
			String absolutePath) throws WorkerException {

		try {

			Writer writer = BufferedRW.getBufferedWriter(tmpOut,
					BufferedRW.UTF8);
			
			JobBasedSpecificDatas jobDatas = this.getJobBasedDatas();
			logger.debug("jobDatas = " + jobDatas);
			
			String pivotStyleSheet = jobDatas.getPivotStyleSheetFileName();
			logger.debug("current styleSheetFileName = "+this.styleSheetFileName);
			logger.debug("expected styleSheetFileName = "+pivotStyleSheet);
			//TODO TMX : add test : if different from transformer current stylesheet then change transformer's one (setTansform), otherwise do nothing			
			setTransformer(pivotStyleSheet,TransformWrapper.OUTPUT_XHTML);
			
			wrapper.clearParameters();
			
			Set<String> excludedDataFields = jobDatas.getExcludedDataFields();

			if (excludedDataFields != null) {
				String excluded = excludedDataFields.toString();
				// TODO TMX : remove brackets on toString() ?
				wrapper.setParameter(PivotXSLTWorker.PARAMNAME_EXCLUDED_DATAFIELDS,
						excluded);
				excluded = (String) wrapper
						.getParameter(PivotXSLTWorker.PARAMNAME_EXCLUDED_DATAFIELDS);
				logger.debug("excluded style sheet parameter = ", excluded);
			}
			
			Boolean withCollections = jobDatas.isWithCollections();
			wrapper.setParameter(PivotXSLTWorker.PARAMNAME_WITHCOLLECTIONS,
					withCollections);						
			withCollections = (Boolean) wrapper
			.getParameter(PivotXSLTWorker.PARAMNAME_WITHCOLLECTIONS);			
			logger.debug("withCollections style sheet parameter = ", String.valueOf(withCollections));
			
			String shortName = jobDatas.getShortName();
			wrapper.setParameter(PivotXSLTWorker.PARAMNAME_SHORTNAME,
					shortName);
			shortName = (String) wrapper
					.getParameter(PivotXSLTWorker.PARAMNAME_SHORTNAME);
			logger.debug("shortName style sheet parameter = ", shortName);
			
			
			wrapper.setParameter(PivotXSLTWorker.PARAMNAME_RUNTIMEDIR,
					this.runtimeDir);								
			logger.debug("runtimedir style sheet parameter = ", (String) wrapper
					.getParameter(PivotXSLTWorker.PARAMNAME_RUNTIMEDIR));
			

			wrapper.transform(bufferedReader, writer);
			wrapper.resetTransformer(); // !!!Needed to avoid OutOfMemory
			logger.info("transformed " + absolutePath + " using xsl="
					+ this.styleSheetFileName);
			return writer;

		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			throw new WorkerException(e, absolutePath);
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
			throw new WorkerException(e, absolutePath);
		} catch (TransformerException e) {
			logger.error(e.getMessage());
			throw new WorkerException(e, absolutePath);
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new WorkerException(e, absolutePath);
		}

	}

}
