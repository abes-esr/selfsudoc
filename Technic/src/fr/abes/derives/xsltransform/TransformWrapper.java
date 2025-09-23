package fr.abes.derives.xsltransform;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import fr.abes.utils.BufferedRW;
import fr.abes.utils.LogHelper;

public class TransformWrapper {
	
	private static LogHelper logger=new LogHelper(TransformWrapper.class);
	
	
	
	public static final int OUTPUT_XML=1;
	public static final int OUTPUT_HTML=2;
	public static final int OUTPUT_XHTML=3;
	public static final int OUTPUT_TEXT=4;
	public static final int OUTPUT_STX=5;
	

	private Transformer transformer = null;
	private Templates template = null;	
	private TransformerFactory tFactory = null;
	
	private static Properties propertiesXML(String charsetName){
		Properties outputProperties = new java.util.Properties();
		outputProperties.put(OutputKeys.METHOD, "xml");
		outputProperties.put(OutputKeys.VERSION, "1.0");
		outputProperties.put(OutputKeys.MEDIA_TYPE, "application/xml");
		outputProperties.put(OutputKeys.INDENT, "no");
		outputProperties.put(OutputKeys.OMIT_XML_DECLARATION,"no");
		outputProperties.put(OutputKeys.ENCODING, charsetName);		
		return outputProperties;
	}
	
	private static Properties propertiesHTML(String charsetName){
		Properties outputProperties = new java.util.Properties();
		outputProperties.put(OutputKeys.METHOD, "html");
		outputProperties.put(OutputKeys.VERSION, "4.01");
		outputProperties.put(OutputKeys.DOCTYPE_PUBLIC,"-//W3C//DTD HTML 4.01//EN");
		outputProperties.put(OutputKeys.DOCTYPE_SYSTEM,"http://www.w3.org/TR/html4/strict.dtd");
		outputProperties.put(OutputKeys.MEDIA_TYPE, "text/html");
		outputProperties.put(OutputKeys.INDENT, "no");
		outputProperties.put(OutputKeys.OMIT_XML_DECLARATION, "yes");
		outputProperties.put(OutputKeys.ENCODING, charsetName);		
		return outputProperties;
	}

	
	private static Properties propertiesXHTML(String charsetName){
		Properties outputProperties = new java.util.Properties();
		outputProperties.put(OutputKeys.METHOD, "xml");
		outputProperties.put(OutputKeys.VERSION, "1.0");
		outputProperties.put(OutputKeys.DOCTYPE_PUBLIC,"-//W3C//DTD XHTML 1.0 Strict//EN");
		outputProperties.put(OutputKeys.DOCTYPE_SYSTEM,"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd");
		outputProperties.put(OutputKeys.MEDIA_TYPE, "application/xhtml+xml");
		outputProperties.put(OutputKeys.INDENT, "no");
		outputProperties.put(OutputKeys.OMIT_XML_DECLARATION, "no");
		outputProperties.put(OutputKeys.ENCODING, charsetName);						
		return outputProperties;
	}
	
	private static Properties propertiesSTX(String charsetName){
		Properties outputProperties = new java.util.Properties();
		outputProperties.put(OutputKeys.METHOD, "xml");
		outputProperties.put(OutputKeys.VERSION, "1.0");
		outputProperties.put(OutputKeys.ENCODING, charsetName);						
		return outputProperties;
	}
	
	private static Properties propertiesText(String charsetName){
		Properties outputProperties = new java.util.Properties();
		outputProperties.put(OutputKeys.METHOD, "text");
		outputProperties.put(OutputKeys.MEDIA_TYPE, "text/plain");
		outputProperties.put(OutputKeys.INDENT, "no");
		outputProperties.put(OutputKeys.OMIT_XML_DECLARATION, "yes");
		outputProperties.put(OutputKeys.ENCODING, charsetName);		
		return outputProperties;
	}
	
	public static Properties outPutProperties(int outPutMethod) {
		Properties props = null;
		switch (outPutMethod) {
		case 1: {
			props = TransformWrapper.propertiesXML(BufferedRW.UTF8); // XSL-FO, XML
			break;
		}
		case 2: {
			props = TransformWrapper.propertiesHTML(BufferedRW.UTF8); // HTML 4.01
			break;
		}
		case 3: {
			props = TransformWrapper.propertiesXHTML(BufferedRW.UTF8); // XHTML
			break;
		}
		case 4: {
			props = TransformWrapper.propertiesText(BufferedRW.UTF8); // TEXT
			break;
		}
		case 5: {
			props = TransformWrapper.propertiesSTX(BufferedRW.UTF8); // STX
			break;
		}			
		default: {
		}
			break;
		}
		
		return props;
	}
	
	public Properties getOutputProperties() {
		return transformer.getOutputProperties();
	}
	
	public void clearParameters() {
		transformer.clearParameters();
	}
	
	public void setParameter(String paramString, Object paramObject) {
		transformer.setParameter(paramString, paramObject);
	}
	
	public Object getParameter(String paramString) {
		return transformer.getParameter(paramString);
	}
	
	
	
	
	/**
	 *
	 * 	change implementation with -Djavax.xml.transform.TransformerFactory=... and jars
	 *	default JAXP RI : class com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl
	 *	Saxon : net.sf.saxon.TransformerFactoryImpl
	 *	Xalan Interpretive : org.apache.xalan.processor.TransformerFactoryImpl
	 *	XSLTC Compiling processor : org.apache.xalan.xsltc.trax.TransformerFactoryImpl
	 *
	 */
	public TransformWrapper() {
		super();
		logger.debug(System.getProperty("java.home"));
		logger.debug("file.encoding",System.getProperty("file.encoding"));		
		
		this.tFactory = TransformerFactory.newInstance();		
		
		if (tFactory.getClass().getName().equals("org.apache.xalan.xsltc.trax.TransformerFactoryImpl")) {

			tFactory.setAttribute("debug", Boolean.TRUE);
			tFactory.setAttribute("translet-name", "notices");
			Boolean useClassPath = Boolean.FALSE;
			tFactory.setAttribute("use-classpath", useClassPath);

			if (useClassPath) {
				// !!! translets MUST have been generated prior to call XSLTC
				logger.debug("useClassPath", useClassPath.toString());
			} else {
				// will generate if needed
				tFactory.setAttribute("generate-translet", Boolean.TRUE);
			}

} 

		
		

	}

	private static Source readSource(Reader brStylesheet)
			throws FileNotFoundException {

		Source source = null;
		source = new StreamSource(brStylesheet);
		return source;
	}
	
	public Boolean isXSLTCTranslets() {
		
		final String XSLTC="org.apache.xalan.xsltc.trax.TransformerFactoryImpl";
		String tFactoryClassName=tFactory.getClass().getName();
		boolean isXSLTCImpl = XSLTC.equals(tFactoryClassName);
		
		
		Boolean useClassPath = null;
		
		if (isXSLTCImpl) {
			try {
				useClassPath = (Boolean) tFactory.getAttribute("use-classpath");			
			} catch (IllegalArgumentException e) {
				logger.warn(e);
			}
		} else {
			
		}
		
		boolean result = (isXSLTCImpl && useClassPath);		
		logger.debug("isTranslets", Boolean.toString(result));
		return result;
	}
	
	public void resetTransformer() {				
		transformer.reset();
		logger.info("resetted");
	}
	
	/**
	 * Build a new template/transformer with brStylesheet and fire transformation
	 * 
	 * @param brXML
	 * @param brStylesheet
	 * @param writer
	 * @param paramProperties
	 * @throws TransformerException
	 * @throws IOException
	 */
	public void transform(Reader brXML, Reader brStylesheet,
			Writer writer, Properties paramProperties)
			throws TransformerException, IOException {

		Source xmlSource = new StreamSource(brXML);
		Source stylesheet = readSource(brStylesheet);
		logger.debug("stylesheet read");
		Result outStream = new StreamResult(writer);

		template = tFactory.newTemplates(stylesheet);
		transformer = template.newTransformer();

		// TODO TMX : autodetect properties from <xsl:output> tag ?
		transformer.setOutputProperties(paramProperties);
		transformer.transform(xmlSource, outStream);
		writer.flush();
		
		

	}
	
	/**
	 * 
	 * Build a new template/transformer with brStylesheet
	 * 
	 * @param brStylesheet
	 * @throws FileNotFoundException 
	 * @throws TransformerConfigurationException 
	 */
	public void setTransformer(Reader brStylesheet, Properties paramProperties ) throws FileNotFoundException, TransformerConfigurationException {
		Source stylesheet = readSource(brStylesheet);
		logger.debug("stylesheet read");
		template = tFactory.newTemplates(stylesheet);
		transformer = template.newTransformer();
		transformer.setOutputProperties(paramProperties);
		stylesheet = null;
	}

	/**
	 * Use current transformer'stylesheet and fire transformation
	 * 
	 * @param brXML
	 * @param writer
	 * @param paramProperties
	 * @throws TransformerException
	 * @throws IOException
	 */
	public void transform(Reader brXML,
			Writer writer)
			throws TransformerException, IOException {

		Source xmlSource = new StreamSource(brXML);
		Result outStream = new StreamResult(writer);

		// TODO TMX : autodetect properties from <xsl:output> tag ?
		transformer.transform(xmlSource, outStream);
		writer.flush();
		logger.info("transformed");
		
		

	}


}
