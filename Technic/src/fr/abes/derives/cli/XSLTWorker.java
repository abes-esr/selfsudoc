package fr.abes.derives.cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import fr.abes.derives.xsltransform.TransformWrapper;
import fr.abes.technic.RCRUtils;
import fr.abes.utils.BufferedRW;
import fr.abes.utils.LogHelper;

public class XSLTWorker extends AbstractFileWorker implements IFileWorker {

	final public static String XSL_REMOVEXMLNS = "removexmlns.xsl";

	final public static String XSL_DATAGROUPLOC = "datagrouploc.xsl";
	final public static String XSL_FILTER_RCR = "filterRCR.xsl";
	final public static String XSL_SORT = "sort.xsl";
	final public static String XSL_HTML = "test/datagroupandscript.xsl";
	final public static String PARAMNAME_RCR_SELECTED = "rcrselected";
	private static LogHelper logger = new LogHelper(XSLTWorker.class);

	private int outPutMethod = 0;
	protected TransformWrapper wrapper = null;
	protected String styleSheetFileName = null;
	protected String runtimeDir = null;

	/**
	 * Build a new XSLTWorker using styleSheetFileName.
	 * 
	 * @param styleSheetFileName
	 * @param paramOutPutMethod
	 * @param sourceDir
	 * @param targetDir
	 * @throws TransformerConfigurationException
	 * @throws IOException
	 */
	public XSLTWorker(String paramRuntimeDir, String paramStyleSheetFileName, int paramOutPutMethod,
			File sourceDir, File targetDir)
			throws TransformerConfigurationException, IOException {

		super(sourceDir, targetDir);
		this.runtimeDir = paramRuntimeDir;
		if (paramStyleSheetFileName!=null) {
			this.styleSheetFileName = this.runtimeDir+"/"+paramStyleSheetFileName;
			// Save to test kind of transform later
		}
		logger.debug("Constructor styleSheetFileName="
				+ this.styleSheetFileName);		

		logger.debug("runtimeDir=",this.runtimeDir);
		

		wrapper = new TransformWrapper();

		Reader brStylesheet = null;
		if (!wrapper.isXSLTCTranslets()) {
			if (this.styleSheetFileName != null) {
				brStylesheet = BufferedRW.UTF8Reader(this.styleSheetFileName);
			}
		}

		outPutMethod = paramOutPutMethod;
		if (brStylesheet != null) {			
			wrapper.setTransformer(brStylesheet, TransformWrapper
					.outPutProperties(outPutMethod));
			logger.info("Constructor has setted ",this.styleSheetFileName);
			brStylesheet.close();
		}

	}

	/**
	 * call transformation
	 * 
	 * if (styleSheetFileName == XSL_FILTER_RCR), retrieve
	 * concerned RCR from file name and give it as PARAMNAME_RCR_SELECTED parameter to style sheet
	 * 
	 */
	public Writer processFile(Reader bufferedReader, File tmpOut,
			String absolutePath) throws WorkerException {
		
		logger.info("before processing "+absolutePath+" using xsl="+this.styleSheetFileName);
		
		
		
		try {
			
			Writer writer = BufferedRW.getBufferedWriter(tmpOut,
					BufferedRW.UTF8);
			
			wrapper.clearParameters();
			
			String rcrSelected = null; 
			String fromFileName = "UNDEFINED"; //default rcr value will remove all localisations			
			
			if ((this.runtimeDir+"/"+XSL_FILTER_RCR).equals(styleSheetFileName)) {						
				fromFileName = RCRUtils.getSingleRCR(absolutePath);
				logger.info("filtering rcr from fileName = ",fromFileName);
				wrapper.setParameter(PARAMNAME_RCR_SELECTED, fromFileName);				
				rcrSelected = (String) wrapper.getParameter(PARAMNAME_RCR_SELECTED);							
			}	
			
			logger.info("rcrSelected param from Transformer = ",rcrSelected);
			
			wrapper.transform(bufferedReader, writer);
			wrapper.resetTransformer(); // !!!Needed to avoid OutOfMemory
			logger.info("processed "+absolutePath+" using xsl="+this.styleSheetFileName);			
			return writer;
			
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			throw new WorkerException(e,absolutePath);
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
			throw new WorkerException(e,absolutePath);
		} catch (TransformerException e) {
			logger.error(e.getMessage());
			throw new WorkerException(e,absolutePath);
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new WorkerException(e,absolutePath);
		}
		


	}

	/**
	 * Change worker's with a new build template/transformer based on
	 * styleSheetFileName.
	 * 
	 * @param styleSheetFileName
	 * @param paramOutPutMethod
	 * @throws TransformerConfigurationException
	 * @throws IOException
	 */
	public void setTransformer(String paramStyleSheetFileName,
			int paramOutPutMethod) throws TransformerConfigurationException,
			IOException {

		this.styleSheetFileName = paramStyleSheetFileName;

		
		Reader brStylesheet = null;
		if (!wrapper.isXSLTCTranslets()) {
			if (this.styleSheetFileName != null) {
				brStylesheet = BufferedRW.UTF8Reader(this.styleSheetFileName);
			}
		}

		outPutMethod = paramOutPutMethod;
		
		
		wrapper.setTransformer(brStylesheet, TransformWrapper
				.outPutProperties(outPutMethod));
		logger.info("setted to ",this.styleSheetFileName);
		
		brStylesheet.close();

	}


	@Override
	public String outFileExtension() {
		String extension = null;
		
		switch (this.outPutMethod) {
		
		case TransformWrapper.OUTPUT_XML: {
			extension = BufferedRW.EXT_DOT_XML;
			break;
		}
		
		case TransformWrapper.OUTPUT_HTML: {
			extension = BufferedRW.EXT_DOT_HTML;
			break;
		}
		
		case TransformWrapper.OUTPUT_XHTML: {
			extension = BufferedRW.EXT_DOT_XHTML;
			break;
		}

		default:
			break;
		}

		return extension;
	}
	
	@Override
	public String inputFileExtension() {
		return BufferedRW.EXT_DOT_XML;
	}


}
