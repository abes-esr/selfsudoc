package fr.abes.derives.sax;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;

import fr.abes.utils.LogHelper;

/**
 * ContentHandler to emit records XML without Unicode \u0098 \u009C
 * 
 * @author michaux
 * 
 */
public class RecordEmitter implements ContentHandler {

	private static LogHelper logger = new LogHelper(RecordEmitter.class);

	// MARC characters illegal in HTML
	final private static char START_OF_STRING = '\u0098';
	final private static char STRING_TERMINATOR = '\u009C';
	final private static char AMPERSAND = '\u0026';
	final private static char LESSTHAN = '\u003C';

	final public static String REGEX0 = "" + START_OF_STRING;
	final private static String REGEX1 = "[" + START_OF_STRING
			+ STRING_TERMINATOR + "]";
	final public static String REGEX2 = "" + STRING_TERMINATOR;
	final public static String REGEX3 = "" + AMPERSAND;
	final public static String REGEX4 = "" + LESSTHAN;

	// final private static String REGEX =
	// "<subfield code=\"(.)\">(.+?)</subfield>";
	// final private static Pattern PATTERN = Pattern.compile(REGEX);

	private Locator locator = null;
	private Writer writer = null;
	private boolean isSubfield = false;
	private StringBuilder subfieldText = null;
	private String subfieldCode = null;
	private String datafieldTag = null;
	private Map<String, String> dataCodes = null;

	public RecordEmitter() {
		super();
		this.locator = new LocatorImpl();
		this.writer = null;
		this.isSubfield = false;
	}
	
	public RecordEmitter(Map<String, String> paramDataCodes) {
		super();
		this.locator = new LocatorImpl();
		this.writer = null;
		this.isSubfield = false;
		this.dataCodes = paramDataCodes;
	}
	
	

	private static String encodeXML(char ch[], int start, int length)
			throws SAXException {

		StringBuilder result = new StringBuilder();

		for (int i = start; i < start + length; i++) {

			switch (ch[i]) {
			case AMPERSAND:
				result.append("&amp;");
				break;
			case LESSTHAN:
				result.append("&lt;");
				break;
			default:
				result.append(ch[i]);
			}

		}

		logger.debug("converted to : ",result.toString());
		return result.toString();
	}

	private String removedUnicode989C(String s) {

		String[] splitted = null;
		StringBuilder builder = null;

		// Matcher matcher = PATTERN.matcher(s);
		// while (matcher.find()) {
		// logger.debug("<subfield code=\"" + matcher.group(1) + "\">"
		// + matcher.group(2) + "</subfield>");
		// }

		splitted = s.split(REGEX1); // Faster than String.replaceAll or
		// Matcher.replaceAll if no need to capture
		// matching groups
		int l = splitted.length;
		int last = l - 1;
		if (l > 3) {
			int lineNumber = this.locator.getLineNumber();
			String msg = "line "+lineNumber+ " has unexptected split in " + l
					+ " parts > 3 : !!! datafield="+this.datafieldTag+" subfield="+this.subfieldCode+" text() = "
					+ s;
			logger.error(msg);
			
			//throw new IllegalArgumentException(msg);
			return s;
		}

		String newCode = "classified-" + subfieldCode;

		builder = new StringBuilder(splitted[0]);
		// cleaned text() without Unicode \U0098, \U009C
		for (int i = 1; i < l; i++) {
			builder.append(splitted[i]);
		}

		String classfiedUnder = splitted[last]; // !!![l-1] instead of [2] to
		// avoid
		// ArrayIndexOutOfBoundsException
		// when wrong use only \U0098
		// without closing \U009C

		logger.debug(splitted[1], " removed ");

		if (l < 3 && !s.contains(REGEX2)) {
			logger.warn("!!! Splitted only in " + l
					+ " parts missing \\U009C at the end");
			logger.warn("classified under = " + classfiedUnder
					+ " (original was = " + s + ")");
		}

		if (l < 3 && s.contains(REGEX2)) {
			// Non-Classified is at the end : swap with start
			logger.warn("Swapping (only 2 parts and nothing after \\U009C)");
			classfiedUnder = splitted[0]; // Swap
			logger.warn("classified under = " + classfiedUnder
					+ " (original was = " + s + ")");
		}

		StringBuilder result = new StringBuilder(builder.append("</subfield>"));
		result.append("<subfield code=\"").append(newCode).append("\">")
				.append(classfiedUnder);

		return result.toString();
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {

		String s = new String(ch, start, length);

		if (s.contains(REGEX3) || s.contains(REGEX4)) {
			logger.debug("not allowed in XML characters : ",s);
			s = encodeXML(ch, start, length);
		}

		if (isSubfield) {
			// write delayed to </subfield>
			subfieldText.append(s);
		} else {
			// Not in a <subfield> tag
			try {
				// No special process, just copy
				writer.write(s);
			} catch (IOException e) {
				logger.error(e.getMessage());
				throw new SAXException(e);
			}
		}
	}

	public void endDocument() throws SAXException {
		logger.debug("Fin de l'analyse du document");

	}

	public void endElement(String nameSpaceURI, String localName, String qName)
			throws SAXException {

		if (isSubfield) {
			
			logger.debug("datafield tag = ", datafieldTag);
			logger.debug("subfield code = ", subfieldCode);


			String s = subfieldText.toString();
			if (s.contains(REGEX0)) {// Contains unwanted Unicode
				
				logger.debug("text() length = ", Integer.toString(s.length()));
				logger.debug("raw #PCDATA", s);
				try {
					writer.write(removedUnicode989C(s));
				} catch (Exception e) {//wrapping IllegalArgumentException too
					logger.error(e.getMessage());
					throw new SAXException(e);
				}

			} else {
				//Unicode cleaned, no we go for data codes replace
								
				if (dataCodes!=null) {
					//if needed replace coded datas with values					
					String key = datafieldTag+"$"+subfieldCode+s;
					logger.debug("key = ", key);

					if (dataCodes.containsKey(key)) {												
						//Replace !
						s = dataCodes.get(key);
						logger.debug("subfield/text() replaced by data coded value=",s);
					}
					
				}
				
				try {
					writer.write(s);
				} catch (IOException e) {
					logger.error(e.getMessage());
					throw new SAXException(e);
				}
			}
			try {
				writer.write("</subfield>");
			} catch (IOException e) {
				logger.error(e.getMessage());
				throw new SAXException(e);
			}

			isSubfield = false;
			subfieldText = null;
			subfieldCode = null;
		} else {

			// Not a subfield
			try {
				writer.write("</" + localName + ">");
			} catch (IOException e) {
				logger.error(e.getMessage());
				throw new SAXException(e);
			}

		}

		logger.debug("Fermeture de la balise : ", localName);

	}

	public void endPrefixMapping(String prefix) throws SAXException {
		logger.debug("Fin de traitement de l'espace de nommage : ", prefix);

	}

	public void ignorableWhitespace(char[] ch, int start, int end)
			throws SAXException {
		logger.debug("espaces inutiles rencontres : ..."
				+ new String(ch, start, end) + "...");
	}

	public void processingInstruction(String target, String data)
			throws SAXException {
		logger.debug("Instruction de fonctionnement : ", target);
		logger.debug("  dont les arguments sont : ", data);

	}

	public void setDocumentLocator(Locator arg0) {
		this.locator = arg0;

	}

	public void skippedEntity(String arg0) throws SAXException {
	}

	public void startDocument() throws SAXException {
		isSubfield = false;
		logger.debug("Debut de l'analyse du document");
	}

	public void startElement(String nameSpaceURI, String localName,
			String qName, Attributes atts) throws SAXException {

		isSubfield = false;

		try {
			writer.write("<" + localName);
			logger.debug("Ouverture de la balise : ", localName);

			if ((!"".equals(nameSpaceURI)) && "record".equals(localName)) { // espace
				// de
				// nommage
				// particulier
				// sur
				// certain
				// records
				writer.write(" xmlns=\"");
				writer.write(nameSpaceURI);
				writer.write("\"");
				logger.debug("  appartenant a l'espace de nom : "
						+ nameSpaceURI);
			}

			for (int index = 0; index < atts.getLength(); index++) {
				writer.write(" ");
				writer.write(atts.getLocalName(index));
				writer.write("=\"");
				writer.write(atts.getValue(index));
				writer.write("\"");
			}

			writer.write(">");

		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new SAXException(e);
		}

		subfieldText = new StringBuilder();

		if ("subfield".equals(localName)) {
			isSubfield = true;

			subfieldCode = atts.getValue("code");
		}
		if ("datafield".equals(localName)) {
			datafieldTag = atts.getValue("tag");
		}

	}

	public void startPrefixMapping(String prefix, String URI)
			throws SAXException {
		logger.debug("Traitement de l'espace de nommage : " + URI
				+ ", prefixe choisi : " + prefix);

	}

	public void setWriter(Writer writer) {
		this.writer = writer;
	}

}
