/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package libertysystems.stenographer;

import org.apache.log4j.Level;

/**
 *
 * @author ian_2
 */
/**
 * Provides a class that maps between our error levels and those of log4j
 *
 * Log levels in order of priority are OFF, TRACE, DEBUG, INFO, WARNING, ERROR, ALL
 * 
 * Use the _ONLY version to set logging to only see the specified level (will cause any others to be masked - warning!)
 *
 * @see Stenographer
 *
 * @author Ian Hudson
 */

public enum LogLevel
{

    OFF(0, false, Level.OFF),
    TRACE(1, false, Level.TRACE), TRACE_ONLY (1, true, Level.TRACE),
    DEBUG(2, false, Level.DEBUG), DEBUG_ONLY(2, true, Level.DEBUG),
    INFO(3, false, Level.INFO), INFO_ONLY(3, true, Level.INFO),
    WARNING(4, false, Level.WARN), WARNING_ONLY(4, true, Level.WARN),
    ERROR(5, false, Level.ERROR), ERROR_ONLY(5, true, Level.ERROR),
    PASSTHROUGH(6, false, Level.FATAL),
    ALL (10, false, Level.ALL);
    
    private final int logValue;
    private final boolean singleLevelLog;
    private Level log4jLevel = null;

    LogLevel(int logValue, boolean singleLevelLog, Level log4JLevel)
    {
        this.logValue = logValue;
        this.singleLevelLog = singleLevelLog;
        this.log4jLevel = log4JLevel;
    }

    private int getValue()
    {
        return logValue;
    }
    
    /**
     * Converts to a log4j level
     *
     * @return log4j level
     *
     * @see LogLevel
     */
    public Level getLog4JLevel()
    {
        return log4jLevel;
    }

    /**
     * Indicates whether the specified level should be logged or not
     *
     * @param newEventLevel The levelf of the event to be logged
     *
     * @return true for should be logged and false for ignore
     *
     */
    public boolean isLoggable(LogLevel newEventLevel)
    {
        boolean logThisEvent = false;

        if (singleLevelLog)
        {
            if (newEventLevel.getValue() == logValue)
            {
                logThisEvent = true;
            }
        } else
        {
            if (newEventLevel.getValue() >= logValue)
            {
                logThisEvent = true;
            }
        }

        return logThisEvent;
    }

    @Override
    public String toString()
    {
        return name();
    }
}
