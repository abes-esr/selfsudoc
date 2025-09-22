package fr.abes.utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogHelper {
	
	//Savoir si la configuration a été réalisée
    private static boolean initialized = false;

    private Logger logger = null;

    /**
     * Constructeur par défaut
     * 
     * @@param category
     */
    public LogHelper(Class<?> category) {
        if (!initialized)
            init();

        long uniqueLogID = System.currentTimeMillis();
		System.setProperty("logFilename", "selfSudoc_"+uniqueLogID+".log");
        logger = LogManager.getLogger(category);
    }

    private static synchronized void init() {
        if (!initialized) {
            //Sous Eclipse l'initialisation se résume à affecter une variable
            // de la JVM :
            //-Dlog4j.configuration=file:///C:\workspace\ExportOnDemand\conf\log4j.xml
            initialized = true;
        }
    }

    public boolean isDebugEnabled() {
        return logger.isEnabled(Level.DEBUG);
    }

    public boolean isInfoEnabled() {
        return logger.isEnabled(Level.INFO);
    }

    public boolean isWarnEnabled() {
        return logger.isEnabled(Level.WARN);
    }

    public boolean isErrorEnabled() {
        return logger.isEnabled(Level.ERROR);
    }

    //-------------------------------------------------------------------
    // DEBUG
    //-------------------------------------------------------------------

    public void debug(Object message) {
        logger.log(Level.DEBUG, message.toString());
    }

    public void debug(Object message, Throwable t) {
    	logger.log(Level.DEBUG, message.toString(),t);
    }

    public void debug(Object message, String methodName) {
        if (isDebugEnabled()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append(methodName);
            buffer.append(" - ");
            buffer.append(message.toString());
            debug(buffer);
        }
    }

    public void debug(Object message, String methodName, Throwable t) {
        if (isDebugEnabled()) {
        	StringBuilder buffer = new StringBuilder();
            buffer.append(methodName);
            buffer.append(" - ");
            buffer.append(message.toString());
            debug(buffer, t);
        }
    }

    //-------------------------------------------------------------------
    // INFO
    //-------------------------------------------------------------------

    public void info(Object message) {
    	logger.log(Level.INFO, message.toString());
    }

    public void info(Object message, Throwable t) {
    	logger.log(Level.INFO, message.toString(),t);
    }

    public void info(Object message, String methodName) {
        if (isInfoEnabled()) {
        	StringBuilder buffer = new StringBuilder();
            buffer.append(methodName);
            buffer.append(" - ");
            buffer.append(message.toString());
            info(buffer);
        }
    }

    public void info(Object message, String methodName, Throwable t) {
        if (isInfoEnabled()) {
        	StringBuilder buffer = new StringBuilder();
            buffer.append(methodName);
            buffer.append(" - ");
            buffer.append(message.toString());
            info(buffer, t);
        }
    }

    //-------------------------------------------------------------------
    // WARNING
    //-------------------------------------------------------------------

    public void warn(Object message) {
    	logger.log(Level.WARN, message.toString());
    }

    public void warn(Object message, Throwable t) {
    	logger.log(Level.WARN, message.toString(),t);
    }

    public void warn(Object message, String methodName) {
        if (isWarnEnabled()) {
        	StringBuilder buffer = new StringBuilder();
            buffer.append(methodName);
            buffer.append(" - ");
            buffer.append(message.toString());
            warn(buffer);
        }
    }

    public void warn(Object message, String methodName, Throwable t) {
    	if (isWarnEnabled()) {
        	StringBuilder buffer = new StringBuilder();
            buffer.append(methodName);
            buffer.append(" - ");
            buffer.append(message.toString());
            warn(buffer, t);
        }
    }

    //-------------------------------------------------------------------
    // ERROR
    //-------------------------------------------------------------------

    public void error(Object message) {
    	logger.log(Level.ERROR, message.toString());
    }

    public void error(Object message, Throwable t) {
    	logger.log(Level.ERROR, message.toString(),t);
    }

    public void error(Object message, String methodName) {
        if (isErrorEnabled()) {
        	StringBuilder buffer = new StringBuilder();
            buffer.append(methodName);
            buffer.append(" - ");
            buffer.append(message.toString());
            error(buffer);
        }
    }

    public void error(Object message, String methodName, Throwable t) {
    	if (isErrorEnabled()) {
        	StringBuilder buffer = new StringBuilder();
            buffer.append(methodName);
            buffer.append(" - ");
            buffer.append(message.toString());
            error(buffer, t);
        }
    }

}
