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

/**
 * We extend XSLTWorker so we can consider Sylk format production as a final format class in loop code 
 * @see fr.abes.derives.cli.AbstractFileWorker.finalRendererFormat(IFileWorker fileWorker)
 * @author michaux
 *
 */
public class SYLKWorker extends XSLTWorker {
		
	private static LogHelper logger = new LogHelper(SYLKWorker.class);	

	public SYLKWorker(String paramRuntimeDir, File sourceDir, File targetDir)
			throws TransformerConfigurationException, IOException {
		super(paramRuntimeDir, null, TransformWrapper.OUTPUT_TEXT, sourceDir,
				targetDir);

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
			setTransformer(pivotStyleSheet,TransformWrapper.OUTPUT_TEXT);
			
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

			wrapper.transform(bufferedReader, writer);
			wrapper.resetTransformer(); // !!!Needed to avoid OutOfMemory
			logger.info("processed " + absolutePath + " using xsl="
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

	
	@Override
	public String outFileExtension() {
		//return BufferedRW.EXT_DOT_SLK;
		return BufferedRW.EXT_DOT_CSV;
	}

}
