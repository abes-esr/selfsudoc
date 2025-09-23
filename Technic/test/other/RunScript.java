package other;

import java.io.FileNotFoundException;
import java.io.IOException;

import fr.abes.technic.RhinoScripting;
import fr.abes.utils.BufferedRW;
import fr.abes.utils.LogHelper;

public class RunScript {
	
	private static LogHelper logger = new LogHelper(RunScript.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		logger.info("file.encoding",System.getProperty("file.encoding"));

		String fileName = null;
		String functionName = null;
		String param=null;
		
		if (args.length > 0) {
			fileName = args[0];
		}
		if (args.length > 1) {
			functionName = args[1];
		}
		if (args.length > 2) {
			param = args[2];
		}
		
		
		
		String result = "";

		RhinoScripting myScript=null;
		try {
			myScript = new RhinoScripting(fileName,BufferedRW.UTF8);
		} catch (FileNotFoundException e) {
			logger.error("File does not exist", e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			logger.error(e.getMessage());
			System.exit(1);
		}
		myScript.evaluate(1); //from line number 1

		result = myScript.call(functionName, param);
		System.out.println(result);

	}

}
