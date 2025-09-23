package fr.abes.derives.cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Map;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import fr.abes.derives.sax.RecordEmitter;
import fr.abes.derives.sax.RecordXMLDriver;
import fr.abes.technic.DataCodeScanner;
import fr.abes.utils.BufferedRW;
import fr.abes.utils.LogHelper;

public class SAXWorker extends AbstractFileWorker implements IFileWorker {
	
	private static LogHelper logger = new LogHelper(SAXWorker.class);
	
	private RecordXMLDriver driver = null;
	private RecordEmitter handler = null;
	
	
	
	public SAXWorker(Map<String, String> dataCodes, File sourceDir, File targetDir) throws SAXException {
		super(sourceDir, targetDir);
		driver = new RecordXMLDriver();
		handler = new RecordEmitter(dataCodes);

	}

	

	public Writer processFile(Reader bufferedReader, File tmpOut,
			String absolutePath) throws WorkerException {
		
		try {
			Writer writer = BufferedRW.getBufferedWriter(tmpOut,
					BufferedRW.UTF8);

			handler.setWriter(writer); // change writer
			
			StringBuilder sb = new StringBuilder(); //write <?xml version... start tag
			sb.append(BufferedRW.XMLPROLOG);
			writer.write(sb.toString());

			driver.setHandler(handler); // use new handler
			
			InputSource source = new InputSource(bufferedReader);
			driver.parse(source); // write from source into handler's
			
			logger.debug("processed "+absolutePath+" with SAX");			
			return writer;
			
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			throw new WorkerException(e,absolutePath);
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
			throw new WorkerException(e,absolutePath);
		} catch (SAXNotRecognizedException e) {
			logger.error(e.getMessage());
			throw new WorkerException(e,absolutePath);
		} catch (SAXNotSupportedException e) {
			logger.error(e.getMessage());
			throw new WorkerException(e,absolutePath);
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new WorkerException(e,absolutePath);
		} catch (SAXException e) {			
			Exception e1 = e.getException();
			if (e1 == null) {
				logger.error(e.getMessage());
				throw new WorkerException(e,absolutePath);
			} else {
				// SAX embedded
				logger.error(e1.getMessage());
				throw new WorkerException(e1,absolutePath);
			}

		}

	}
	
	public static Map<String, String> readDataCodes(String fileName, String charsetName) {

		Map<String, String> dataCodes = null;

		try {
			dataCodes = DataCodeScanner.readDataCodes(fileName,charsetName);
			logger.debug(dataCodes.values());
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			logger.error(e.getMessage());
			System.exit(1);
		}

		return dataCodes;
	}



	@Override
	public String outFileExtension() {
		return BufferedRW.EXT_DOT_XML;
	}
	
	@Override
	public String inputFileExtension() {
		return BufferedRW.EXT_DOT_XML;
	}




}
