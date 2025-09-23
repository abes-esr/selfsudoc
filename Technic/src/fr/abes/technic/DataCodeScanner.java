package fr.abes.technic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataCodeScanner {

	private Map<String, String> datas = null; // TODO TMX : refactor to
	// Singleton Pattern ?

	final private static String REGEX = "(.+?)\t(.+?)\t(.+)";
	final private static Pattern PATTERN = Pattern.compile(REGEX);

	int readLines = 0, matchedLines = 0, errorLine = 0;

	public DataCodeScanner(String fileName, String charsetName) throws FileNotFoundException,
			IOException {
		super();
		readLines = 0;
		matchedLines = 0;
		errorLine = 0;
		this.datas = readFile(fileName, charsetName);
	}

	public Map<String, String> readFile(String fileName, String charsetName)
			throws FileNotFoundException, UnsupportedEncodingException {

		datas = new HashMap<String, String>();

		Reader bfr = null;

		File file = new File(fileName);
		if (!file.exists()) {
			throw new FileNotFoundException(file.getPath());
		}

		InputStream input = new FileInputStream(file);
		bfr = new BufferedReader(new InputStreamReader(input,charsetName));
		Scanner scanner = new Scanner(bfr);

		boolean errorFound = false;
		boolean matchedCurrent = false;

		while (scanner.hasNextLine()) {
			readLines++;
			matchedCurrent = procesLine(scanner.nextLine());
			if (!matchedCurrent && !errorFound) {
				// keep track of first not matched
				errorFound = true;
				errorLine = readLines;
			}
			;
		}

		scanner.close();

		if (errorFound) {
			// TODO TMX : just warn and ignore ?
			int diff = readLines - datas.keySet().size();
			throw new IllegalArgumentException("line(s) omitted : " + diff
					+ ", no match at line : " + errorLine);
		}
		return datas;

	}

	private boolean procesLine(String input) {

		Matcher matcher = PATTERN.matcher(input);

		String key = null;

		while (matcher.find()) {

			matchedLines++;
			key = matcher.group(1) + matcher.group(2);
			if (datas.containsKey(key)) {
				throw new IllegalArgumentException("duplicated key : " + key);
			} else {
				datas.put(key, matcher.group(3));
			}
		}

		return (matchedLines == readLines);

	}

	public Map<String, String> getDatas() {
		return datas;
	}
	
	/**
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static Map<String,String> readDataCodes(String fileName, String charsetName) throws FileNotFoundException, IOException {
		DataCodeScanner scanner = null;
		scanner = new DataCodeScanner(fileName, charsetName);
		return scanner.getDatas();

	}

}
