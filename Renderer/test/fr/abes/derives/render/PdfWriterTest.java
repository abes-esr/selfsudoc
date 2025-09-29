package fr.abes.derives.render;


import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.security.CodeSource;
import java.text.MessageFormat;
import java.util.ArrayList;

import javax.xml.parsers.SAXParserFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.html.simpleparser.StyleSheet;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import fr.abes.utils.BufferedRW;

public class PdfWriterTest {

	private Document document = null;
	private Reader br = null;
	private StyleSheet st = null;
	private File tmpOut = null;
	private PdfWriter writer = null;

	@Before
	public void setUp() throws Exception {
		
		// Hardcode running configuation
		System.setProperty("javax.xml.parsers.SAXParserFactory",
				"org.apache.xerces.jaxp.SAXParserFactoryImpl");
		System.out.println(getJaxpImplementationInfo("SAXParserFactory",
				SAXParserFactory.newInstance().getClass()));
		
		
		


		// Use temp outFile
		tmpOut = File.createTempFile("dat", null);
		System.out.println("TMP Out File="+tmpOut);	
		System.out.println("Reader="+br);	
		document = new Document();
		System.out.println("Document="+document);	
		br = UTF8Reader("test/notices.xhtml");
		//br = UTF8Reader("test/miseenpage.html");
		System.out.println("Reader="+br);	
		
		FontFactory.registerDirectories(); 
		System.out.println(FontFactory.getRegisteredFamilies());
		
		st = new StyleSheet();
		st.loadTagStyle("body", "face", "arial unicode ms"); //for printig non iso-latin-1
		//st.loadTagStyle("body", "face", "code2000"); 
		st.loadTagStyle("body", "encoding", BaseFont.IDENTITY_H);
		st.loadTagStyle("body", "size", "1");
				
		st.loadStyle("arabic", "direction", "rtl");			

	}

	@After
	public void tearDown() throws Exception {

		if (document != null) {
			document.close();
		}

		if (writer != null) {
			writer.close();
		}
		if (br != null) {
			br.close();
		}

		if (tmpOut != null) {
			//tmpOut.deleteOnExit();
		}

	}

	@Test
	public void testParse() {

		try {

			writer = PdfWriter.getInstance(document, new FileOutputStream(
					tmpOut));
			System.out.println("PdfWriter="+writer);	
			document.open();
			System.out.println("Document opened");
			ArrayList<Element> p = HTMLWorker.parseToList(br, st);
			for (int k = 0; k < p.size(); ++k) {
				Element e = (Element) p.get(k);
				//System.out.println("k="+k+" e="+e);
				document.add(e);
			}
			document.close();
			System.out.println("Document closed");
			writer.flush();
			writer.close();
			br.close();



		} catch (FileNotFoundException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (DocumentException e) {
			fail(e.getMessage());
		}

		assertTrue(tmpOut.length() > 0); //TODO TMX : need detailed test to check it's a valid PDF document, how ?
	}

	/**
	 * utility method to provide buffered reader on an UTF-8 file and call
	 * Assert.fail on FileNotFoundException, UnsupportedEncodingException
	 */
	public static Reader UTF8Reader(String fileName) {
		Reader br = null;
		try {
			br = BufferedRW.getBufferedReader(fileName, BufferedRW.UTF8);
		} catch (FileNotFoundException e) {
			fail(e.getMessage());
		} catch (UnsupportedEncodingException e) {
			fail(e.getMessage());
		}

		return br;

	}
	
	/**
	 * Provides implementation info
	 * 
	 * @param componentName
	 * @param componentClass
	 * @return
	 */
	public static String getJaxpImplementationInfo(String componentName,
			Class<?> componentClass) {
		CodeSource source = componentClass.getProtectionDomain()
				.getCodeSource();
		return MessageFormat.format("{0} implementation: {1} loaded from: {2}",
				componentName, componentClass.getName(),
				source == null ? "Java Runtime" : source.getLocation());
	}


}
