package other;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import fr.abes.derives.cli.IFileWorker;
import fr.abes.derives.cli.RTFWorker;

import fr.abes.derives.cli.WorkerException;
import fr.abes.utils.BufferedRW;

public class RunRTFRenderer {
	
	final public static String HOMEDIR=System.getenv("EOD_HOME");
	final private static String RUNTIMEDIR=HOMEDIR; //Production config

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		File xhtmlDir = null;
		File rtfDir = null;

		
		String XHTMLDIRECTORY = null;
		if (args.length > 0) {
			XHTMLDIRECTORY = args[0];
		}
		xhtmlDir = new File(XHTMLDIRECTORY);
		
		String RTFDIRECTORY = null;
		if (args.length > 1) {
			RTFDIRECTORY = args[1];
		}
		rtfDir = new File(RTFDIRECTORY);

		
		String fileName = null;
		if (args.length > 2) {
			fileName = args[2];
		}
		
		IFileWorker rtf = null;
		
		rtf = new RTFWorker(RUNTIMEDIR,xhtmlDir,
				rtfDir,BufferedRW.EXT_DOT_HTML);
		
		
//		File f = null;		
//		new File(fileName);
		
		String inNameWithExt = RUNTIMEDIR+"/"+rtf.getSourceDir()+"/"+fileName+rtf.inputFileExtension();
		System.out.println("inNameWithExt="+inNameWithExt);
		
		String outNameWithExt = RUNTIMEDIR+"/"+rtf.getTargetDir()+"/"+fileName+rtf.outFileExtension();
		System.out.println("outNameWithExt="+outNameWithExt);
		
		File tmpOut = null;
		Reader br = null;
		Writer writer = null;
		
		long bytesLength = 0;
		
		
		try {
			tmpOut = File.createTempFile("dat", null);
			br = BufferedRW.getBufferedReader(new File(inNameWithExt),
					BufferedRW.UTF8);
			writer = rtf.processFile(br, tmpOut, inNameWithExt);		
			writer.flush();
			writer.close();
			br.close();
			
			if (tmpOut != null) {

				System.out.println("trying to rename to " + outNameWithExt);
				File f = new File(outNameWithExt);
				boolean success = BufferedRW.moveNFSProof(tmpOut, f, BufferedRW.UTF8); //NFS Proof
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
		



