package fr.abes.derives.cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.html.simpleparser.StyleSheet;
import com.itextpdf.text.pdf.BaseFont;
import com.lowagie.text.rtf.RtfWriter2;

import fr.abes.utils.BufferedRW;
import fr.abes.utils.LogHelper;

public class RTFWorker extends AbstractFileWorker implements IFileWorker {
	
	private static LogHelper logger = new LogHelper(RTFWorker.class);
	
	private StyleSheet st = null;
	private String inputFileExt = null;
	private String runtimeDir = null;
	
	public RTFWorker(String paramRuntimeDir, File sourceDir, File targetDir, String inputFileExt) {
		super(sourceDir, targetDir);
		this.runtimeDir = paramRuntimeDir;
		FontFactory.registerDirectories();
		logger.info(FontFactory.getRegisteredFamilies());
		st = new StyleSheet();
		st.loadTagStyle("body", "face", "arial unicode ms"); //for printig non iso-latin-1
		//st.loadTagStyle("body", "face", "code2000"); //PDF Only
		st.loadTagStyle("body", "encoding", BaseFont.IDENTITY_H);
		st.loadTagStyle("body", "size", "1");
		
		st.loadTagStyle("div", "leading", "0f,1f");
		st.loadTagStyle("p", "leading", "0f,1f");
				
		st.loadStyle("arabic", "direction", "rtl");		
		//st.loadStyle("arabic", "size", "2");
		this.inputFileExt = inputFileExt;
		logger.debug("RTFWorker constuct OK");
	}


	public Writer processFile(Reader bufferedReader, File tmpOut,
			String absolutePath) throws WorkerException {
		
		try {
			Writer writer = BufferedRW.getBufferedWriter(tmpOut,
					BufferedRW.UTF8);
			
			Document document = new Document();
			//TODO TMX : ? possible to have same RtfWriter2 in constructor and give each time FileOutputStream ?   
			RtfWriter2 rtf = RtfWriter2.getInstance(document, new FileOutputStream(
					tmpOut));
			document.open();
			ArrayList<Element> p = HTMLWorker.parseToList(bufferedReader, st);
			for (int k = 0; k < p.size(); ++k)
				document.add((Element) p.get(k));
			document.close();		
			rtf.flush();
			rtf.close();
			bufferedReader.close();
			return writer;
			
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			throw new WorkerException(e,absolutePath);
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
			throw new WorkerException(e,absolutePath);
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new WorkerException(e,absolutePath);
		} catch (DocumentException e) {
			logger.error(e.getMessage());
			throw new WorkerException(e,absolutePath);
		}


	}


	@Override
	public String outFileExtension() {
		return BufferedRW.EXT_DOT_RTF;
	}
	
	@Override
	public String inputFileExtension() {
		return this.inputFileExt;
	}

	

}
