package fr.abes.technic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.CharBuffer;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import fr.abes.utils.LogHelper;

public class RhinoScripting {

	private static LogHelper logger = new LogHelper(RhinoScripting.class);
	
	public static final String UNDEFINED_FUNCTION = " is undefined or not a function.";

	private Context cx = null;
	private Scriptable scope = null;
	private String script = "";

	public static String readFile(String fileName, String charsetName) throws FileNotFoundException, IOException {

		Reader bfr = null;
		CharBuffer target = null;
		String script = null;

		

			File file = new File(fileName);
			if (!file.exists()) {
				throw new FileNotFoundException(file.getPath());
			}

			long l = file.length();
			String str = Long.toString(l);
			int capacity = Integer.parseInt(str);
			target = CharBuffer.allocate(capacity);

			InputStream input = new FileInputStream(file);
			bfr = new BufferedReader(new InputStreamReader(input,charsetName));
			bfr.read(target);
			bfr.close();


		if (target != null) {
			target.rewind();
			script = target.toString();
			logger.debug(fileName + " : " + script.length());
			//logger.debug(script);
		}

		return script;

	}

	public RhinoScripting(String fileName, String charsetName) throws FileNotFoundException, IOException {		
		super();
		
		logger.debug(System.getProperty("java.home"));
		logger.debug("file.encoding",System.getProperty("file.encoding"));

		this.cx = Context.enter();
		scope = cx.initStandardObjects();
		this.script = readFile(fileName,charsetName); 		
	}

	/**
	 * @param args
	 */
	public void evaluate(int lineno) {

		// Now evaluate the string we've collected. We'll ignore the result.
		cx.evaluateString(scope, script, "<cmd>", lineno, null);
		logger.debug("finish evaluate");

	}

	/**
	 * 
	 * Print the value of variable named "name"
	 * 
	 * @param name
	 */
	public String getFromScope(String name) {
		Object vObj = scope.get(name, scope);
		if (vObj == Scriptable.NOT_FOUND) {
			String s = name + " is not defined.";
			logger.warn(s);
			return s;
		} else {
			String s = Context.toString(vObj);
			logger.debug(name, s);
			return s;
		}
	}

	/**
	 * 
	 * Call function "functionName(functionArgs[])" and return its result as
	 * String
	 * 
	 * @param functionName
	 * @param functionArgs
	 */
	private String call(String functionName, Object functionArgs[]) {
		Object fObj = scope.get(functionName, scope);
		if (!(fObj instanceof Function)) {
			String s = functionName + UNDEFINED_FUNCTION;
			logger.warn(s);
			return s;
		} else {
			Function f = (Function) fObj;
			Object result = f.call(cx, scope, scope, functionArgs);
			String s = Context.toString(result);
			logger.debug(functionName, s);
			return s;
		}
	}

	/**
	 * 
	 * Call function "functionName(String arg0)" and return its result as String
	 * 
	 * @param functionName
	 * @param arg0
	 * @return
	 */
	public String call(String functionName) {
		Object fObj = scope.get(functionName, scope);
		if (!(fObj instanceof Function)) {
			String s = functionName + UNDEFINED_FUNCTION;
			logger.warn(s);
			return s;
		} else {
			Function f = (Function) fObj;
			Object result = f.call(cx, scope, scope, null);
			String s = Context.toString(result);
			logger.debug(functionName, s);
			return s;
		}

	}

	/**
	 * 
	 * Call function "functionName(String arg0)" and return its result as String
	 * 
	 * @param functionName
	 * @param arg0
	 * @return
	 */
	public String call(String functionName, String arg0) {
		return call(functionName, new Object[] { arg0 });
	}

	/**
	 * 
	 * Call function "functionName(String arg0, String arg1)" and return its
	 * result as String
	 * 
	 * @param functionName
	 * @param arg0
	 * @return
	 */
	public String call(String functionName, String arg0, String arg1) {
		logger.debug("arg0",arg0);
		logger.debug("arg1",arg1);
		return call(functionName, new Object[] { arg0, arg1 });
	}

	public Context getCx() {
		return cx;
	}

	public void setCx(Context cx) {
		this.cx = cx;
		scope = cx.initStandardObjects();
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

}
