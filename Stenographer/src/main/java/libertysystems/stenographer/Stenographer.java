/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package libertysystems.stenographer;

import org.apache.log4j.Logger;

/**
 * This class is a facade for the underlying logging facilities.
 *
 * It is instantiated using StenographerFactory
 *
 * @see StenographerFactory
 *
 * @author Ian Hudson
 */
public class Stenographer
{

    private Logger logger = null;
    private String logAs = null;
    private LogLevel currentLogLevel = null;
    private String myFQCN = this.getClass().getCanonicalName();
    private LogLevel loglevel = null;

    protected Stenographer(String logAs, LogLevel loglevel)
    {
        this.logAs = logAs;
        this.loglevel = loglevel;
        this.currentLogLevel = loglevel;
        logger = Logger.getLogger(logAs);
        this.changeLogLevel(loglevel);
    }

    /**
     * Logs a message at the chosen LogLevel
     *
     * @param message The message to add to the log.
     *
     * @param logLevel The logging level to be applied to this entity.
     *
     * @see LogLevel
     */
    public void log(String message, LogLevel logLevel)
    {
        if (currentLogLevel.isLoggable(logLevel))
        {
            logger.log(myFQCN, logLevel.getLog4JLevel(), message, null);
        }
    }
    
    /**
     * Logs a message at the chosen LogLevel
     *
     * @param message The message to add to the log.
     *
     * @param logLevel The logging level to be applied to this entity.
     *
     * @see LogLevel
     */
    public void log(String message, LogLevel logLevel, Exception ex)
    {
        if (currentLogLevel.isLoggable(logLevel))
        {
            logger.log(myFQCN, logLevel.getLog4JLevel(), message, ex);
        }
    }    

    /**
     * Logs a message at LogLevel.TRACE
     *
     * @param message The message to add to the log.
     *
     */
    public void trace(String message)
    {
        this.log(message, LogLevel.TRACE);
    }

    /**
     * Logs a message at LogLevel.DEBUG
     *
     * @param message The message to add to the log.
     *
     */
    public void debug(String message)
    {
        this.log(message, LogLevel.DEBUG);
    }

    /**
     * Logs a message at LogLevel.INFO
     *
     * @param message The message to add to the log.
     *
     */
    public void info(String message)
    {
        this.log(message, LogLevel.INFO);
    }

    /**
     * Logs a message at LogLevel.WARNING
     *
     * @param message The message to add to the log.
     *
     */
    public void warning(String message)
    {
        this.log(message, LogLevel.WARNING);
    }

    /**
     * Logs a message at LogLevel.ERROR
     *
     * @param message The message to add to the log.
     *
     */
    public void error(String message)
    {
        this.log(message, LogLevel.ERROR);
    }
    
    /**
     * Logs a message at LogLevel.ERROR and also output the exception
     *
     * @param message The message to add to the log.
     *
     */
    public void exception(String message, Exception ex)
    {
        this.log(message, LogLevel.ERROR, ex);
    }    

    /**
     * Logs a message at LogLevel.DEBUG
     *
     * @param message The message to add to the log.
     *
     */
    public void passthrough(String message)
    {
        this.log(message, LogLevel.PASSTHROUGH);
    }

    /**
     * Allows the log level for a specific Stenographer to be changed directly.
     *
     * This method will change the log level for the Stenographer independently from other instances.
     *
     * @param newLevel The logging level to be applied to this entity.
     *
     * @see StenographerFactory LogLevel
     */
    public void changeLogLevel(LogLevel newLevel)
    {
        currentLogLevel = newLevel;
        logger.setLevel(newLevel.getLog4JLevel());
    }
    
    /**
     * Returns the log level of this Stenographer
     * @return 
     *
     * @see StenographerFactory LogLevel
     */
    public LogLevel getCurrentLogLevel()
    {
        return currentLogLevel;
    }
}
