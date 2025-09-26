package fr.abes.derives.cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Set;

import fr.abes.utils.BufferedRW;

/**
 * 
 * To aggregate informations from database entry around a file on filesystem
 * entry
 * 
 * @author michaux
 * 
 */
public class RcrJobWrapper {

	protected final static String RCR_FILE_PREFIX = "notices";
	private final static String COLUMN_PREFIX = "ETAT_";
	
	protected final static String STATUS_WAIT = "W";
	protected final static String STATUS_OK = "O";
	protected final static String STATUS_EXCEPTION = "X";


	private long exportID = 0;
	private String rcr = null;
	private String shortName = null;
	public String getShortName() {
		return shortName;
	}




	private String fileName = null;
	private String runtimeDir = null;
	private File dirSource = null;
	private File dirDestination = null;
	private Exception exception = null;
	private Set<String> excludedDataFields = null;
	private String layout = null;
	private boolean withCollections = false;
	
	
	public static String getColumnName(File directory) {
		//Mapping directories to database columns
		return (COLUMN_PREFIX + directory).toUpperCase();
	}
	
	
	

	public RcrJobWrapper(long exportID, String rcr, String shortName, Set<String> excludedDataFields, boolean withCollections, String layout, File dirSource,
			File dirDestination, String runtimeDir) throws FileNotFoundException {
		super();
		this.runtimeDir = runtimeDir;
		// directories are valid ?
		File runtimeSourceDir = new File(this.runtimeDir+"/"+Chain.DOCBASE+"/"+dirSource);
		File runtimeDestinationDir = new File(this.runtimeDir+"/"+Chain.DOCBASE+"/"+dirDestination);
		BufferedRW.validateDirectory(runtimeSourceDir);
		BufferedRW.validateDirectory(runtimeDestinationDir);
		this.dirSource = dirSource;
		this.dirDestination = dirDestination;

		this.exportID = exportID;
		this.rcr = rcr;
		this.shortName = shortName;
		this.fileName = dirSource + "/" + RCR_FILE_PREFIX + "_" + this.rcr
				+ "_" + this.exportID;
		this.excludedDataFields=excludedDataFields;
		this.layout = layout;
		this.withCollections = withCollections;

	}


	public String getFileName() {
		return fileName;
	}




	public Exception getException() {
		return exception;
	}




	public void setException(Exception exception) {
		this.exception = exception;
	}




	public long getExportID() {
		return exportID;
	}




	public String getRcr() {
		return rcr;
	}




	public Set<String> getExcludedDataFields() {
		return excludedDataFields;
	}




	public String getLayout() {
		return layout;
	}




	public void setLayout(String layout) {
		this.layout = layout;
	}




	@Override
	public String toString() {
		
		String excludedStr=(excludedDataFields!=null)?excludedDataFields.toString():null;
		
		return "RcrJobWrapper [dirDestination=" + dirDestination
				+ ", dirSource=" + dirSource + ", exportID=" + exportID
				+ ", fileName=" + fileName + ", layout=" + layout + ", rcr="
				+ rcr + ", shortName=" + shortName + ", excludedDataFields=" + excludedStr + "]";
	}




	public boolean isWithCollections() {
		return withCollections;
	}

}
