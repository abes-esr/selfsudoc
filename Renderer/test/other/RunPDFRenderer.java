package other;

import fr.abes.derives.cli.IFileWorker;
import fr.abes.derives.cli.PDFWorker;
import fr.abes.derives.cli.WorkerException;
import fr.abes.utils.BufferedRW;

import java.io.*;

public class RunPDFRenderer {
	
	final public static String HOMEDIR=System.getenv("EOD_HOME");
	final private static String RUNTIMEDIR=HOMEDIR; //Production config

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		File xhtmlDir = null;
		File pdfDir = null;

		
		String XHTMLDIRECTORY = null;
		if (args.length > 0) {
			XHTMLDIRECTORY = args[0];
		}
		xhtmlDir = new File(XHTMLDIRECTORY);
		
		String PDFDIRECTORY = null;
		if (args.length > 1) {
			PDFDIRECTORY = args[1];
		}
		pdfDir = new File(PDFDIRECTORY);

		
		String fileName = null;
		if (args.length > 2) {
			fileName = args[2];
		}
		
		IFileWorker pdf = null;
		
		pdf = new PDFWorker(RUNTIMEDIR,xhtmlDir,
				pdfDir,BufferedRW.EXT_DOT_HTML);
		
		
//		File f = null;		
//		new File(fileName);
		
		String inNameWithExt = RUNTIMEDIR+"/"+pdf.getSourceDir()+"/"+fileName+pdf.inputFileExtension();
		System.out.println("inNameWithExt="+inNameWithExt);
		
		String outNameWithExt = RUNTIMEDIR+"/"+pdf.getTargetDir()+"/"+fileName+pdf.outFileExtension();
		System.out.println("outNameWithExt="+outNameWithExt);
		
		File tmpOut = null;
		Reader br = null;
		Writer writer = null;
		
		long bytesLength = 0;
		
		
		try {
			tmpOut = File.createTempFile("dat", null);
			br = BufferedRW.getBufferedReader(new File(inNameWithExt),
					BufferedRW.UTF8);
			writer = pdf.processFile(br, tmpOut, inNameWithExt);		
			writer.flush();
			writer.close();
			br.close();
			
			if (tmpOut != null) {

				System.out.println("trying to rename to " + outNameWithExt);
				File f = new File(outNameWithExt);
                boolean success = BufferedRW.moveNFSProof(tmpOut, f, BufferedRW.ISOLATIN1); //NFS Proof (binaire : byte-transparent, voir AbstractFileWorker)
				if (success) {
					System.out.println("renamed OK");
					bytesLength = f.length();
					tmpOut.delete();
				} else {
					System.err.println("unable to rename .tmp file to "
							+ outNameWithExt);
					System.exit(1);
				}

			}
			
			
			
		} catch (FileNotFoundException e) {
			System.err.print(e.getMessage());
			System.exit(1);
		} catch (UnsupportedEncodingException e) {
			System.err.print(e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			System.err.print(e.getMessage());
			System.exit(1);
		} catch (WorkerException e) {
			System.err.print(e.getMessage());
			System.exit(1);
		}
		
		finally {
			
			try {
				if (writer != null) {
					writer.close();
				}
				if (br != null) {
					br.close();
				}

				if (tmpOut != null) {
					tmpOut.delete();
				}

			} catch (IOException e) {
				System.err.print(e.getMessage());
				System.exit(1);
				
			}
		}
		
		System.out.println("written "+bytesLength+" bytes");
		System.exit(0);
		

	}
}
		



