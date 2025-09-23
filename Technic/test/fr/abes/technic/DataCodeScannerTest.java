package fr.abes.technic;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DataCodeScannerTest {
	
	//TODO TMX : add UTF-8/CP1252 test

	private final static String FILENAME = "DonnesCodesUnm.txt";
	private static DataCodeScanner scanner = null;
	Map<String, String> result = null;

	@Before
	public void setUp() throws Exception {

		result = null;

	}

	@After
	public void tearDown() throws Exception {
		result = null;
		scanner = null;
	}

	@Test
	public void testGetDatas() {

		try {
			scanner = new DataCodeScanner(FILENAME,"Cp1252");
		} catch (FileNotFoundException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		}

		Map<String, String> result = null;
		result = scanner.getDatas();
		
		System.out.print("keys = "+result.keySet());

		// TODO TMX : more detailed tests : rewrite file from Map and compare
		// with original file
		assertTrue(894 == result.keySet().size());
	}

	@Test
	public void testGetDatasNotOK() {

		String exceptionClassName = null;

		try {
			scanner = new DataCodeScanner("test/NotOKDonnesCodesUnm.txt","Cp1252");
		} catch (FileNotFoundException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (Exception e) {
			exceptionClassName = e.getClass().getName();
		}

		assertTrue("java.lang.IllegalArgumentException"
				.equals(exceptionClassName));
	}
}
