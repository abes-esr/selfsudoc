package fr.abes.technic;


public class RCRUtils {
	
	final public static String RCRSPLITFILENAME="_";

	/**
	 * Convention : file format is notices_RCR_TIMESTAMP.xml
	 * 
	 * @param absolutePath
	 * @return RCR part of filename
	 */
	public static String getSingleRCR(String absolutePath) {
		
		
		
		//System.out.println("absolutePath="+absolutePath+"\n");

		String[] splitted = null;
		splitted = absolutePath.split(RCRSPLITFILENAME);
		String result = splitted[1];
		
		//System.out.println("splitted[1]="+result+"\n");
		
		return result;

	}

}
