package fr.abes.derives.cli;

import java.io.File;
import java.io.Reader;
import java.io.Writer;

public interface IFileWorker {

	public Writer processFile(Reader bufferedReader, File tmpOut, String absolutePath) throws WorkerException;

	public File getSourceDir();

	public File getTargetDir();
	
	public String outFileExtension();
	
	public String inputFileExtension();
	
	//will be processed in some cases with "html pivot/slk" workers
	public JobBasedSpecificDatas getJobBasedDatas(); 
	
	public void setJobBasedDatas(JobBasedSpecificDatas jobBasedData);
}
