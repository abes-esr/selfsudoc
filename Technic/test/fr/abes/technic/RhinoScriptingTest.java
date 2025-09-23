package fr.abes.technic;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RhinoScriptingTest {
	
	private final static String FILENAME = "script.js";
	private RhinoScripting myScript = null;

	@Before
	public void setUp() throws Exception {
		
		
		myScript = new RhinoScripting(FILENAME,"Cp1252");
		myScript.evaluate(1); //from line number 1
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testTraite916() {
		String functionName = "Traite916";
		String result = "";
		
		result = myScript.call(functionName, "4d6a");
		assertEquals("6 année(s)", result);
		
		result = myScript.call(functionName, "4d2a");
		assertEquals("2 année(s)", result);

		result = myScript.call(functionName, "4d6m");
		assertEquals("6 mois", result);
		
		result = myScript.call(functionName, "4d10a");
		assertEquals("10 année(s)", result);		
		
		result = myScript.call(functionName, "");
		assertEquals("error:", result);		
		
		String dummyArg="titi"+System.currentTimeMillis();
		result = myScript.call(functionName, dummyArg);
		assertEquals("error:"+dummyArg, result);
		
		String dummyFuncName="toto"+System.currentTimeMillis();		
		result = myScript.call(dummyFuncName, "");
		assertEquals(dummyFuncName+RhinoScripting.UNDEFINED_FUNCTION, result);
		
		result = myScript.call(null, "");
		assertEquals("null"+RhinoScripting.UNDEFINED_FUNCTION, result);

	}
	
	@Test
	public void testTraite955() {
		String functionName = "Traite955Add";
		String result = "";
		
		result = myScript.call("Traite955Init");
		result = myScript.call(functionName, "a","247");
		result = myScript.call(functionName, "i","1945");
		result = myScript.call(functionName, "i","");
		result = myScript.call(functionName, "j","");
		result = myScript.call(functionName, "k","");
		result = myScript.call(functionName, "a","");
		result = myScript.call(functionName, "b","");
		result = myScript.call(functionName, "c","");
		result = myScript.call(functionName, "z","Index : 1961/1978 ; 1979/1990 ; 1991/1997 [à consulter sur place]");
		result = myScript.call(functionName, "w","Lacunes :  avr. 1986 ; fév. 1999");
		result = myScript.call(functionName, "r","vol. 247 (1945)-....");
		result = myScript.call("Traite955");
		
		assertEquals(" "+"vol.247(1945) -  [Index : 1961/1978 ; 1979/1990 ; 1991/1997 [à consulter sur place]] [Lacunes :  avr. 1986 ; fév. 1999]", result);
		
		
	}
	
	@Test
	public void testAbesSTP4614() {
		String functionName = "Traite955Add";
		String result = "";
		
		result = myScript.call("Traite955Init");
		result = myScript.call(functionName, "j","jan");
		result = myScript.call(functionName, "i","1985");
		result = myScript.call(functionName, "a","237");
		result = myScript.call(functionName, "j","avr");
		result = myScript.call(functionName, "i","1998");
		result = myScript.call(functionName, "r","(jan-1985) - vol. 237  (avr-1998)");
		result = myScript.call("Traite955");
		
		System.out.println("result="+result);
		
		//we should compare to $r
		assertEquals("(jan1985)- vol.237(avr1998)", result);
		
		
	}

}
