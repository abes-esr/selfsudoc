package fr.abes.derives.cli;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.itextpdf.text.Document;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

public class PageXofY extends PdfPageEventHelper {
	
	public PageXofY(Date jobDateExtraction) {
		super();
		this.jobDateExtraction = jobDateExtraction;
	}

	//DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	private Date jobDateExtraction;
	
	/** The PdfTemplate that contains the total number of pages. */
	protected PdfTemplate total;
	
	/** The font that will be used. */
	protected BaseFont helv;
	
	private float FONTSIZE=6;



	@Override
	public void onCloseDocument(PdfWriter writer, Document document) {
		total.beginText();
		total.setFontAndSize(helv, FONTSIZE);
		total.setTextMatrix(0, 0);
		total.showText(String.valueOf(writer.getPageNumber() - 1));
		total.endText();

	}

	@Override
	public void onEndPage(PdfWriter writer, Document document) {
		if (writer.getPageNumber()>1) {//sauf page de couverture
		PdfContentByte cb = writer.getDirectContent();
		cb.saveState();
		String text = "imprimé le "+dateFormat.format(jobDateExtraction)+" - page "  + writer.getPageNumber() + "/";
		float textBase = document.bottom() - 20;
		float textSize = helv.getWidthPoint(text, FONTSIZE);
		cb.beginText();
		cb.setFontAndSize(helv, FONTSIZE);
		
		if ((writer.getPageNumber() % 2) == 0 ) {
			cb.setTextMatrix(document.left(), textBase);
			cb.showText(text);
			cb.endText();
			cb.addTemplate(total, document.left() + textSize, textBase);
		}
		// for even numbers, show the footer at the right
		else {
			float adjust = helv.getWidthPoint("0", FONTSIZE);
			cb.setTextMatrix(document.right() - textSize - adjust, textBase);
			cb.showText(text);
			cb.endText();
			cb.addTemplate(total, document.right() - adjust, textBase);
		}
		cb.restoreState();
		}

	}

	@Override
	public void onOpenDocument(PdfWriter writer, Document document) {
		total = writer.getDirectContent().createTemplate(100, 100);
		total.setBoundingBox(new Rectangle(-20, -20, 100, 100));
		try {
			helv = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI,
					BaseFont.NOT_EMBEDDED);
		} catch (Exception e) {
			throw new ExceptionConverter(e);
		}

	}

}
