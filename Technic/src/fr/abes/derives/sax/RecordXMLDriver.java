package fr.abes.derives.sax;

import java.io.IOException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import fr.abes.utils.LogHelper;

/**
 * Use specific contentHandler to emit records XML without Unicode \u0098 \u009C
 * 
 * @author michaux
 * 
 */
public class RecordXMLDriver {

	private static LogHelper logger = new LogHelper(RecordXMLDriver.class);

	private RecordEmitter handler = null;
	private XMLReader saxReader = null;

	public RecordXMLDriver() throws SAXException {
		super();
				
		this.handler = null;
		this.saxReader = XMLReaderFactory.createXMLReader();

	}

	/**
	 * Validation and schema Off
	 * 
	 * @param handler
	 * @throws SAXNotRecognizedException
	 * @throws SAXNotSupportedException
	 */
	public void setHandler(RecordEmitter handler)
			throws SAXNotRecognizedException, SAXNotSupportedException {
		this.handler = handler;		
		saxReader.setFeature("http://xml.org/sax/features/validation", false);
		saxReader.setFeature(
				"http://apache.org/xml/features/validation/schema", false);
		saxReader.setContentHandler(handler);
	}

	public void parse(InputSource source) throws IOException, SAXException {
		saxReader.parse(source);
	}

}
