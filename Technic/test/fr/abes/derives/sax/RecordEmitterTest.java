package fr.abes.derives.sax;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import fr.abes.utils.BufferedRW;
import fr.abes.utils.TestUtils;

public class RecordEmitterTest {

	
	private Reader brXML = null;
	InputSource source = null;
	private Writer writer = null;
	private File tmpOut = null;
	private RecordEmitter handler = null;
	private XMLReader saxReader = null;

	@Before
	public void setUp() throws Exception {
		
		//Hardcode running configuation
		System.setProperty("javax.xml.parsers.SAXParserFactory","org.apache.xerces.jaxp.SAXParserFactoryImpl");		

		
		String xmlSourceFileName = "test/unicoded.xml";
		// xmlSource XML read from file
		brXML = TestUtils.UTF8Reader(xmlSourceFileName);
		source = new InputSource(brXML);

		// Use temp outFile
		tmpOut = File.createTempFile("dat", null);
		writer = BufferedRW.getBufferedWriter(tmpOut, TestUtils.UTF8);
		handler = new RecordEmitter();
		handler.setWriter(writer);

		
		System.out.println(TestUtils.getJaxpImplementationInfo("SAXParserFactory",
				SAXParserFactory.newInstance().getClass()));
		

		saxReader = XMLReaderFactory.createXMLReader();	
		saxReader.setFeature("http://xml.org/sax/features/validation", false);
		saxReader.setFeature(
				"http://apache.org/xml/features/validation/schema", false);
		saxReader.setContentHandler(handler);

	}

	@After
	public void tearDown() throws Exception {

		if (writer != null) {
			writer.close();
		}
		// free resources
		if (brXML!=null) {brXML.close();}

		if (tmpOut != null) {
			tmpOut.deleteOnExit();
		}

		saxReader = null;
	}

	@Test
	public void testParse() {


		final StringBuilder sb = new StringBuilder();
		sb.append(TestUtils.XMLPROLOG);
		try {
			writer.write(sb.toString());
			saxReader.parse(source);
			writer.flush();

		} catch (IOException e) {
			fail(e.getMessage());
		} catch (SAXException e) {
			Exception e1 = e.getException();
			if (e1 == null) {
				fail(e.getMessage());
			} else {
				// SAX embedded
				fail(e1.getMessage());
			}
		}

		Reader brOut = null;
		System.out.print(tmpOut.getPath());
		brOut = TestUtils.UTF8Reader(tmpOut.getPath());

		try {
			// compare tmp result with XSLTransformed notices.xml

			brXML.close();
			brXML = TestUtils.UTF8Reader("test/nounicodefromxslt.xml");

			// Diff d=new Diff(brXML, brOut);
			DetailedDiff d = new DetailedDiff(new Diff(brOut, brXML));
			List<Difference> allDifferences = d.getAllDifferences();
			assertEquals(d.toString(), 0, allDifferences.size());
			assertTrue(d.toString(), d.similar());

		} catch (IOException e) {
			fail(e.getMessage());
		} catch (SAXException e) {
			Exception e1 = e.getException();
			if (e1 == null) {
				fail(e.getMessage());
			} else {
				// SAX embedded
				fail(e1.getMessage());
			}
		}

	}
	
	@Test
	public void testParseAndReplace() {
		//TODO TMX : add a new test for parse and replace with build of minimal Map
		assertTrue(true);
	}


}
