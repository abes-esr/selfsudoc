package other;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerException;

import fr.abes.derives.xsltransform.TransformWrapper;
import fr.abes.utils.BufferedRW;
import fr.abes.utils.LogHelper;

public class RunTransformation {
	
	private static LogHelper logger=new LogHelper(RunTransformation.class);
	
	private static Reader reader(String fileName) {
		Reader br = null;
		try {
			br = BufferedRW.getBufferedReader(fileName,
					BufferedRW.UTF8);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			System.exit(1);
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
			System.exit(1);
		}

		return br;

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Map<String, Integer> ouputMethods = new HashMap<String, Integer>();
		ouputMethods.put("HTML", TransformWrapper.OUTPUT_HTML);
		ouputMethods.put("XML", TransformWrapper.OUTPUT_XML);
		ouputMethods.put("XHTML", TransformWrapper.OUTPUT_XHTML);
		ouputMethods.put("TEXT", TransformWrapper.OUTPUT_TEXT);
		ouputMethods.put("STX", TransformWrapper.OUTPUT_STX);
		
		//Stats
        long time0,time1;
		
		String styleSheetFileName=null;
		String xmlSourceFileName=null;
		String outFileName = null; 		//TODO TMX : test file already exists : exit 1
		int outPutMethod = 0;
		
		if (args.length > 0) {
			xmlSourceFileName = args[0];
		}
		if (args.length > 1) {
			styleSheetFileName = args[1];
		}
		if (args.length > 2) {
			outFileName = args[2];
		}
		if (args.length > 3) {
			outPutMethod = ouputMethods.get(args[3]);
		}
		

		time0=System.currentTimeMillis();
		
		// xmlSource XML read from file
		Reader brXML=reader(xmlSourceFileName);		
		logger.info("Reader brXML",brXML.toString());
		
		
		//Use temp outFile
		File tmpOut=null;
		try {
			tmpOut = File.createTempFile("dat", null);
		} catch (IOException e) {
			logger.error(e.getMessage());
			System.exit(1);
		}
		Writer writer = null;
		try {
			writer = BufferedRW.getBufferedWriter(tmpOut, BufferedRW.UTF8);			
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
			System.exit(1);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			System.exit(1);
		}		
		logger.info("Writer",writer.toString());

		

		TransformWrapper t = new TransformWrapper();
		
		//XSLTStylesheet
		Reader brStylesheet = null;
		if (!t.isXSLTCTranslets()) {brStylesheet=reader(styleSheetFileName);}
		logger.info("Reader brStylesheet",brStylesheet.toString());
		
		try {
			t.transform(brXML, brStylesheet, writer, TransformWrapper.outPutProperties(outPutMethod));
		} catch (TransformerException e) {
			logger.error(e.getMessage());
			System.exit(1);			
		} catch (IOException e) {
			logger.error(e.getMessage());
			System.exit(1);
		}
		
		finally {
			time1=System.currentTimeMillis();
			if (writer != null) {
				try {
					writer.close();					
				} catch (IOException e) {
					logger.error(e.getMessage());
					System.exit(1);
				}
			}
			//free resources
			try {
				//TODO TMX : add test if (brXML!=null) avoid NPE
				brXML.close();
				brStylesheet.close();						
			} catch (IOException e) {
				logger.error(e.getMessage());
				System.exit(1);
			}			
			//transformer.reset();			
		}
		
		// Rename
		if (tmpOut != null) {
			
			
				boolean success=false;
				try {
					success = BufferedRW.moveNFSProof(tmpOut, new File(outFileName), BufferedRW.UTF8);
				} catch (FileNotFoundException e) {
					logger.error(e.getMessage());
					System.exit(1);
				} catch (UnsupportedEncodingException e) {
					logger.error(e.getMessage());
					System.exit(1);
				}
				if (success) {
					logger.info("successfully renamed to ", outFileName);
					tmpOut.deleteOnExit();
				} else {
					logger.error("unable to rename .tmp file");
					System.exit(1);
				}
			
		}
		
		logger.info("finish");
		logger.info(time1-time0+"ms");

		
		

	}

}
