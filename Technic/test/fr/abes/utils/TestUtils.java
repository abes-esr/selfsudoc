package fr.abes.utils;

import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.security.CodeSource;
import java.text.MessageFormat;

public class TestUtils {

	final public static String UTF8 = "UTF-8";
	final public static String XMLVERSION = "1.0";
	final public static String XMLPROLOG = "<?xml version=\"" + XMLVERSION
			+ "\" encoding=\"" + UTF8 + "\"?>"; // !!!noblanks

	/**
	 * utility method to provide buffered reader on an UTF-8 file and call
	 * Assert.fail on FileNotFoundException, UnsupportedEncodingException
	 */
	public static Reader UTF8Reader(String fileName) {
		Reader br = null;
		try {
			br = BufferedRW.getBufferedReader(fileName, BufferedRW.UTF8);
		} catch (FileNotFoundException e) {
			fail(e.getMessage());
		} catch (UnsupportedEncodingException e) {
			fail(e.getMessage());
		}

		return br;

	}

	/**
	 * Provides implementation info
	 * 
	 * @param componentName
	 * @param componentClass
	 * @return
	 */
	public static String getJaxpImplementationInfo(String componentName,
			Class<?> componentClass) {
		CodeSource source = componentClass.getProtectionDomain()
				.getCodeSource();
		return MessageFormat.format("{0} implementation: {1} loaded from: {2}",
				componentName, componentClass.getName(),
				source == null ? "Java Runtime" : source.getLocation());
	}

}
