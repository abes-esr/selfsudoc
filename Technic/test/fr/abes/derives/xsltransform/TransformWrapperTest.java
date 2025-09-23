package fr.abes.derives.xsltransform;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.xpath.XPathFactory;

import org.custommonkey.xmlunit.Diff;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import fr.abes.utils.BufferedRW;
import fr.abes.utils.TestUtils;

public class TransformWrapperTest {

	private TransformWrapper wrapper = null;
	private Reader brXML = null;
	private Writer writer = null;
	private File tmpOut = null;
	Reader brStylesheet = null;
	int outPutMethod = 0;

	@Before
	public void setUp() throws Exception {
		
		//Hardcode running configuation
		//System.setProperty("javax.xml.parsers.SAXParserFactory","org.apache.xerces.jaxp.SAXParserFactoryImpl");		
		//System.setProperty("javax.xml.transform.TransformerFactory","net.sf.saxon.TransformerFactoryImpl");
		//System.setProperty("javax.xml.xpath.XPathFactory:http://java.sun.com/jaxp/xpath/dom","net.sf.saxon.xpath.XPathFactoryImpl");
	
	System.out.println(TestUtils.getJaxpImplementationInfo("DocumentBuilderFactory", DocumentBuilderFactory.newInstance().getClass()));	
	System.out.println(TestUtils.getJaxpImplementationInfo("SAXParserFactory", SAXParserFactory.newInstance().getClass()));
	System.out.println(TestUtils.getJaxpImplementationInfo("XPathFactory", XPathFactory.newInstance().getClass()));
	System.out.println(TestUtils.getJaxpImplementationInfo("TransformerFactory", TransformerFactory.newInstance().getClass()));
			
		// Use temp outFile
		tmpOut = File.createTempFile("dat", null);
		writer = BufferedRW.getBufferedWriter(tmpOut, TestUtils.UTF8);
		wrapper = new TransformWrapper();	
		brStylesheet = null;
		outPutMethod = 0;

	}

	@After
	public void tearDown() throws Exception {

		if (writer != null) {
			writer.close();
		}
		// free resources
		brXML.close();
		brStylesheet.close();

		if (tmpOut != null) {
			tmpOut.deleteOnExit();
		}

		wrapper.resetTransformer();

	}

	@Test
	public void testTransform() {
		
		String xmlSourceFileName = "test/test.xml";
		// xmlSource XML read from file
		brXML = TestUtils.UTF8Reader(xmlSourceFileName);

		String styleSheetFileName = "test/test.xsl"; // XSLTStylesheet
		if (!wrapper.isXSLTCTranslets()) {
			brStylesheet = TestUtils.UTF8Reader(styleSheetFileName);
		}
		outPutMethod = TransformWrapper.OUTPUT_HTML;

		try {
			wrapper.transform(brXML, brStylesheet,
					writer, TransformWrapper.outPutProperties(outPutMethod));

		} catch (TransformerException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		}

		assertTrue(tmpOut.length() > 0);
		// TODO TMX : detailed assertions
	}

	@Test
	public void testTransformFO() {
		
		String xmlSourceFileName = "test/test.xml";
		// xmlSource XML read from file
		brXML = TestUtils.UTF8Reader(xmlSourceFileName);

		String styleSheetFileName = "test/testFO.xsl"; // XSLTStylesheet
		if (!wrapper.isXSLTCTranslets()) {
			brStylesheet = TestUtils.UTF8Reader(styleSheetFileName);
		}
		outPutMethod = TransformWrapper.OUTPUT_XML;

		try {
			wrapper.transform(brXML, brStylesheet,
					writer, TransformWrapper.outPutProperties(outPutMethod));

		} catch (TransformerException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		}

		assertTrue(tmpOut.length() > 0);
		// TODO TMX : detailed assertions
	}

	@Test
	public void testTransformXHTML() {
		
		String xmlSourceFileName = "test/test.xml";
		// xmlSource XML read from file
		brXML = TestUtils.UTF8Reader(xmlSourceFileName);

		String styleSheetFileName = "test/testXHTML.xsl"; // XSLTStylesheet
		if (!wrapper.isXSLTCTranslets()) {
			brStylesheet = TestUtils.UTF8Reader(styleSheetFileName);
		}
		outPutMethod = TransformWrapper.OUTPUT_XHTML;

		try {
			wrapper.transform(brXML, brStylesheet,
					writer, TransformWrapper.outPutProperties(outPutMethod));

		} catch (TransformerException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		}

		assertTrue(tmpOut.length() > 0);
		// TODO TMX : detailed assertions
	}

	@Test
	public void testTransformScripting() {
		
		String xmlSourceFileName = "test/test.xml";
		// xmlSource XML read from file
		brXML = TestUtils.UTF8Reader(xmlSourceFileName);

		String styleSheetFileName = "test/datagroupandscript.xsl"; // XSLTStylesheet
		if (!wrapper.isXSLTCTranslets()) {
			brStylesheet = TestUtils.UTF8Reader(styleSheetFileName);
		}
		outPutMethod = TransformWrapper.OUTPUT_HTML;

		try {
			wrapper.transform(brXML, brStylesheet,
					writer, TransformWrapper.outPutProperties(outPutMethod));

		} catch (TransformerException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		}

		assertTrue(tmpOut.length() > 0);
		// TODO TMX : detailed assertions
	}
	
	@Test
	public void testTransformUnicode989C() {
				
		String xmlSourceFileName = "test/unicoded.xml";
		// xmlSource XML read from file
		brXML = TestUtils.UTF8Reader(xmlSourceFileName);		
		
		String styleSheetFileName = "unicode989C.xsl"; // XSLTStylesheet
		if (!wrapper.isXSLTCTranslets()) {
			brStylesheet = TestUtils.UTF8Reader(styleSheetFileName);
		}
		outPutMethod = TransformWrapper.OUTPUT_XML;
		
		
		try {
			wrapper.transform(brXML, brStylesheet,
					writer, TransformWrapper.outPutProperties(outPutMethod));
	
		} catch (TransformerException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		}
		
		assertTrue(tmpOut.length() > 0);
		
		//TODO TMX : XPath for detailed assertions		
		Reader brOut = null;
		try {
			System.out.print(tmpOut.getPath());
			
			brXML.close();			
			brXML = TestUtils.UTF8Reader("test/nounicodefromsax.xml"); //same result for xslt and sax parser
			brOut = TestUtils.UTF8Reader(tmpOut.getPath());
			
			Diff d=new Diff(brOut,brXML);
			assertTrue(d.identical());
	
		} 
	
		catch (IOException e) {
			fail(e.getMessage());
		} catch (SAXException e) {
			fail(e.getMessage());
		}
		
		finally {
			try {
				if (brOut != null) {
					brOut.close();
				}
			} catch (IOException e) {
				fail(e.getMessage());
			}
		}
		
	}
	
	@Test
	public void testTransformSorted() {
		
		//SE : sort in correct order if and only if there is no namespace on nodes
				
		String xmlSourceFileName = "test/noxmlns.xml";
		// xmlSource XML read from file
		brXML = TestUtils.UTF8Reader(xmlSourceFileName);		
		
		String styleSheetFileName = "sort.xsl"; // XSLTStylesheet
		if (!wrapper.isXSLTCTranslets()) {
			brStylesheet = TestUtils.UTF8Reader(styleSheetFileName);
		}
		outPutMethod = TransformWrapper.OUTPUT_XML;
		
		
		try {
			wrapper.transform(brXML, brStylesheet,
					writer, TransformWrapper.outPutProperties(outPutMethod));
	
		} catch (TransformerException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		}
		
		assertTrue(tmpOut.length() > 0);
		
		//TODO TMX : XPath for detailed assertions		
		Reader brOut = null;
		try {
			System.out.print(tmpOut.getPath());
			
			brXML.close();			
			brXML = TestUtils.UTF8Reader("test/sorted.xml");
			brOut = TestUtils.UTF8Reader(tmpOut.getPath());
			
			Diff d=new Diff(brOut,brXML);
			assertTrue(d.identical());
	
		} 
	
		catch (IOException e) {
			fail(e.getMessage());
		} catch (SAXException e) {
			fail(e.getMessage());
		}
		
		finally {
			try {
				brOut.close();
			} catch (IOException e) {
				fail(e.getMessage());
			}
		}
		
	}

	
}
