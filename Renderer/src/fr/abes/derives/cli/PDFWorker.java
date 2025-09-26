package fr.abes.derives.cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.html.simpleparser.StyleSheet;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import fr.abes.utils.BufferedRW;
import fr.abes.utils.LogHelper;

public class PDFWorker extends AbstractFileWorker implements IFileWorker {

	private static LogHelper logger = new LogHelper(PDFWorker.class);
	
	private StyleSheet st = null;
	private String inputFileExt = null;
	private String runtimeDir = null;
	
	public static final String COPYRIGHT  = "\u00a9";	
	
	public PDFWorker(String paramRuntimeDir, File sourceDir, File targetDir, String inputFileExt) {
		super(sourceDir, targetDir);
		this.runtimeDir = paramRuntimeDir;
		FontFactory.registerDirectories();
		logger.info(FontFactory.getRegisteredFamilies());
		st = new StyleSheet();
		
		//st.loadTagStyle("body", "face", "arial unicode ms"); //for printig non iso-latin-1
		st.loadTagStyle("body", "face", "code2000"); //for printig non iso-latin-1
		st.loadTagStyle("body", "encoding", BaseFont.IDENTITY_H);
		st.loadTagStyle("body", "size", "1");
		
		st.loadTagStyle("div", "leading", "0f,1f");
		st.loadTagStyle("p", "leading", "0f,1f");
				
		st.loadStyle("arabic", "direction", "rtl");		
		
//		st.loadStyle("borderodd", "border-style", "solid");
//		st.loadStyle("borderodd", "border-width", "1px");
//		st.loadStyle("borderodd", "border-color", "white #dddddd black");

		this.inputFileExt = inputFileExt;
		logger.debug("PDFWorker constuct OK");
	}

	// TODO TMX : need to test PdfWriterLayoutTwoColumns instead of table in
	// HTML

	public Writer processFile(Reader bufferedReader, File tmpOut,
			String absolutePath) throws WorkerException {

		try {
			Writer writer = BufferedRW.getBufferedWriter(tmpOut,
					BufferedRW.UTF8);

			Document document = new Document();
			// TODO TMX : ? possible to have same RtfWriter2 in constructor and
			// give
			// each time FileOutputStream ?
			PdfWriter pdf = PdfWriter.getInstance(document,
					new FileOutputStream(tmpOut));
			pdf.setViewerPreferences(PdfWriter.PageLayoutTwoColumnLeft);
			pdf.setPageEvent(new PageXofY(new Date()));
			document.open();
			ArrayList<Element> p = HTMLWorker.parseToList(bufferedReader, st);
			for (int k = 0; k < p.size(); ++k)
				document.add((Element) p.get(k));
			
	        // Add meta-data parameters to generated PDF document
			//TODO TMX : set by parameters
			String title="";
			String author="ABES";
			String subject="";
			String keywords="";
			String creator=COPYRIGHT+"iText under AGPL license see https://itextpdf.com/node/10041";			
			
			document.addTitle(title);
			document.addAuthor(author);
			document.addSubject(subject);
			document.addKeywords(keywords);
			document.addCreator(creator);

			document.close();
			pdf.flush();
			pdf.close();
			bufferedReader.close();
			return writer;
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			throw new WorkerException(e,absolutePath);
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
			throw new WorkerException(e,absolutePath);
		} catch (DocumentException e) {
			logger.error(e.getMessage());
			throw new WorkerException(e,absolutePath);
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new WorkerException(e,absolutePath);
		}

	}
	
	@Override
	public String outFileExtension() {
		return BufferedRW.EXT_DOT_PDF;
	}
	
	@Override
	public String inputFileExtension() {
		return this.inputFileExt;
	}
	

}
