package fr.abes.derives.cli;

import fr.abes.utils.BufferedRW;
import fr.abes.utils.LogHelper;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Set;

public abstract class AbstractFileWorker implements IFileWorker {
	
	private File sourceDir = null;
	private File targetDir = null;
	private JobBasedSpecificDatas jobBasedDatas = null;
	
	
	public AbstractFileWorker(File sourceDir, File targetDir) {
		super();
		this.sourceDir = sourceDir;
		this.targetDir = targetDir;
	}
	
	public void setSourceDir(File sourceDir) {
		this.sourceDir = sourceDir;
	}
	public void setTargetDir(File targetDir) {
		this.targetDir = targetDir;
	}
	public File getSourceDir() {
		return sourceDir;
	}
	public File getTargetDir() {
		return targetDir;
	}
	
	public String outFileExtension() {
		return null; //not defined

	}
	
	public String inputFileExtension() {
		return null; //not defined
	}
	

	
	/**
	 * 
	 * Given a worker returns database value for formatFinal column, null if not a pdf, rtf or sylk worker
	 * 
	 * @param fileWorker
	 * @return
	 */
	public static String finalRendererFormat(IFileWorker fileWorker) {
		
		if ("fr.abes.derives.cli.PDFWorker".equals(fileWorker.getClass().getName())) {return "pdf";}
		if ("fr.abes.derives.cli.RTFWorker".equals(fileWorker.getClass().getName())) {return "rtf";}
		if ("fr.abes.derives.cli.SYLKWorker".equals(fileWorker.getClass().getName())) {return "slk";}
		return null;
	}
	
	public static long doJob(IFileWorker fileWorker, String shortName, Set<String> excludedDataFields, boolean withCollections, String layout, File file, Reader br, Writer writer, File tmpOut, String runtimeDir) throws IOException, WorkerException {
	
	LogHelper logger = new LogHelper(fileWorker.getClass());
	
	//Stats
    long time0,time1;
    
    logger.debug("runtimeDir"+runtimeDir);
    logger.debug("file.getName()="+file.getName());
    logger.debug("file.getAbsolutePath()="+file.getAbsolutePath());
    logger.debug("fileWorker.getSourceDir()="+fileWorker.getSourceDir());
    
    logger.debug("fileWorker.getTargetDir().getAbsolutePath()="+fileWorker.getTargetDir().getAbsolutePath());

	String outNameWithExt = runtimeDir+"/"+fileWorker.getTargetDir()+"/"+file.getName()+fileWorker.outFileExtension();
	String inNameWithExt = runtimeDir+"/"+fileWorker.getSourceDir()+"/"+file.getName()+fileWorker.inputFileExtension();
	
	logger.debug("inNameWithExt="+inNameWithExt);
	logger.debug("outNameWithExt="+outNameWithExt);

	File inFile = new File(inNameWithExt);
	br = BufferedRW.getBufferedReader(inFile,
			BufferedRW.UTF8);

	// Use temp outFile for each new writer
	tmpOut = File.createTempFile("dat", null);
	long bytesLength = 0;

	JobBasedSpecificDatas jobDatas = new JobBasedSpecificDatas(excludedDataFields,layout, shortName, withCollections);
	//will be processed in some cases with "html pivot/slk" workers
	fileWorker.setJobBasedDatas(jobDatas);
	

	time0=System.currentTimeMillis();
	writer = fileWorker.processFile(br, tmpOut, inNameWithExt);
	

	writer.flush();
	writer.close();
	br.close();
	

	if (tmpOut != null) {

		
		logger.debug("trying to rename to "+outNameWithExt);
		File f = new File(outNameWithExt);
		String charsetName = null;
		boolean classIsPDFOrRTF = "fr.abes.derives.cli.RTFWorker".equals(fileWorker.getClass().getName())
				|| "fr.abes.derives.cli.PDFWorker".equals(fileWorker.getClass().getName());
        // PDF/RTF sont des sorties BINAIRES (iText ecrit les octets directement dans tmpOut).
        // Ce charset ne sert que si Files.move echoue et que moveNFSProof bascule sur la copie
        // caractere par caractere (fallback NFS) : ISO-8859-1 est le seul charset byte-transparent
        // (0x00-0xFF mappes 1:1) qui ne corrompt pas le binaire. NE PAS passer en UTF-8 ici,
        // sinon les octets >= 0x80 deviennent U+FFFD et le PDF/RTF est casse.
		if (classIsPDFOrRTF) {
				charsetName = BufferedRW.ISOLATIN1;
			} else {
				charsetName = BufferedRW.UTF8;
			}
		
		boolean success = BufferedRW.moveNFSProof(tmpOut, f,charsetName); //NFS Proof
		if (success) {
			logger.debug("renamed OK");
			bytesLength = f.length();			
			tmpOut.delete();
			if ("fr.abes.derives.cli.SAXWorker".equals(fileWorker
						.getClass().getName())
						|| "fr.abes.derives.cli.XSLTWorker".equals(fileWorker
								.getClass().getName()))
			{				
					// remove previous file
					logger.debug("finished with " + outNameWithExt
							+ ", now we can delete the old=" + inNameWithExt);
					inFile.delete();
			}
			
		} else {
			logger.error("unable to rename .tmp file to ",outNameWithExt);
			System.exit(1);
		}
		
	}
	
	time1=System.currentTimeMillis();
	logger.debug("processed in "+(time1-time0)+"ms");
	
	return bytesLength;
	
	
	
}

	public JobBasedSpecificDatas getJobBasedDatas() {
		return jobBasedDatas;
	}

	public void setJobBasedDatas(JobBasedSpecificDatas jobBasedData) {
		this.jobBasedDatas = jobBasedData;
	}



	

}
