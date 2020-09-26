/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package libertysystems.stenographer;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import libertysystems.configuration.ConfigNotLoadedException;
import libertysystems.configuration.Configuration;
import org.apache.log4j.*;

/**
 *
 * This class allows Stenographer logging objects to be created.
 *
 * Instantiate a Stenographer like this: Stenographer steno =
 * StenographerFactory.getStenographer("nameToAppearInLog");
 *
 * To configure the behaviour of the loggers create Stenographer.properties in the working
 * directory. The following entries show the default configuration that will be used if no file is
 * provided (and is in the format required by the file). logfilename=nologname.log loglevel=INFO
 * logmode=LOCAL logtee=YES
 *
 * logfilename - name of file (necessary in local mode) loglevel - one of OFF, DEBUG, INFO, WARNING
 * or ERROR logmode - LOCAL or REMOTE (LOCAL is to file - REMOTE is to syslog) logtee - NO or YES
 * (controls whether logging is copied to the console or not) maxfilesize - value as a string e.g.
 * 10MB or 1GB (the default is 20MB)
 *
 * @author Ian Hudson
 */
public class StenographerFactory
{

    private static final HashMap<String, Stenographer> stenographers = new HashMap<>();
    private static boolean initialised = false;
    private static LogLevel initialLogLevel = null;

    private StenographerFactory()
    {
    }

    /**
     * Returns a Stenographer object that provides logging facilities.
     *
     * This method will return a new object for each unique logAs string that is provided.
     * Successive calls to the method with the same string will yield a reference to the same
     * object.
     *
     * @param logAs The string that will appear in the log - usually the classname e.g.
     * this.getClass().getCanonicalName()
     *
     * @return a Stenographer object
     *
     *
     * @see Stenographer
     */
    public static Stenographer getStenographer(String logAs)
    {
        Stenographer stenoToReturn = null;

        if (!initialised)
        {
            if (!initLogger())
            {
                System.err.println("Failed to initialise logging factory.");
            } else
            {
                initialised = true;
            }
        }

        if (stenographers.containsKey(logAs))
        {

            stenoToReturn = stenographers.get(logAs);
        } else
        {

            stenoToReturn = new Stenographer(logAs, initialLogLevel);
            stenographers.put(logAs, stenoToReturn);
        }

        return stenoToReturn;
    }

    private static boolean initLogger()
    {
        boolean success = false;
        String configComponentName = "Stenographer";

        String defaultLogFile = "nologname.log";
        String defaultLogMode = "LOCAL";
        String defaultLogTee = "YES";
        String defaultMaxFilesize = "20MB";

        String logfilename = null;
        String logmode = null;
        String logtee = null;
        String maxfilesize = null;

        //Set up a basic console logger so the Commons Config stuff doesn't barf
        Logger rootLogger = Logger.getRootLogger();

        rootLogger.setLevel(Level.ERROR);

        LevelDependentPatternLayout levelDependentLayout = new LevelDependentPatternLayout(
            "%d [%t] %p - %m%n", "%d [%t] %p %l - %m%n", "%m%n");

        ConsoleAppender consoleAppender = new ConsoleAppender(levelDependentLayout);

        rootLogger.addAppender(consoleAppender);

        try
        {
            Configuration sysConfig = Configuration.getInstance();
            logfilename = sysConfig.getFilenameString(configComponentName, "logfilename",
                                                      defaultLogFile);

            logmode = sysConfig.getString(configComponentName, "logmode", defaultLogMode);
            logtee = sysConfig.getString(configComponentName, "logtee", defaultLogTee);
            maxfilesize = sysConfig.
                getString(configComponentName, "maxfilesize", defaultMaxFilesize);
        } catch (ConfigNotLoadedException ex)
        {
            System.err.println(
                "Couldn't get Stenographer configuration - config file could not be loaded - using defaults");
            logfilename = defaultLogFile;
            logmode = defaultLogMode;
            logtee = defaultLogTee;
            maxfilesize = defaultMaxFilesize;
        }

        initialLogLevel = LogLevel.TRACE;

        // Leave the root logger set at its lowest level - Stenographer will sort out whether to log or not
        rootLogger.setLevel(Level.WARN);

        if (logmode.equalsIgnoreCase("LOCAL"))
        {
            String testFilename = logfilename;

            try
            {
                RollingFileAppender defaultRollingAppender = new RollingFileAppender(
                    levelDependentLayout, testFilename, true);
                defaultRollingAppender.rollOver();
                defaultRollingAppender.setMaxFileSize(maxfilesize);
                defaultRollingAppender.setMaxBackupIndex(4);
                rootLogger.addAppender(defaultRollingAppender);
            } catch (IOException ex)
            {
                System.err.println("Stenographer failed to access " + testFilename + " for logging");
            }

            if (logtee.equalsIgnoreCase("YES"))
            {
                rootLogger.addAppender(consoleAppender);
            }

            success = true;

        } else
        {
            //Not implemented yet - syslog appender
            System.err.println("Didn't recognise Stenographer mode '" + logmode
                + "' from config file " + logfilename);
        }

        Stenographer steno = new Stenographer("StenographerFactory", initialLogLevel);

        // Redirect stderr and stdout to the root logger
        System.setErr(new PrintStream(new LoggingOutputStream(rootLogger, Level.FATAL), true));
        System.setOut(new PrintStream(new LoggingOutputStream(rootLogger, Level.FATAL), true));

        steno.info(
            "StenographerFactory initialised with logfile=" + logfilename + " loglevel="
            + " logmode=" + logmode);

        return success;
    }

    /**
     * Allows the log level for a specific Stenographer to be changed without using the object
     * directly.
     *
     * This method will change the log level for the Stenographer specified by the loggingEntity
     * string. E.g. if a Stenographer is created with StenographerFactory.getStenographer("fred");
     * you can alter the log level as follows: StenographerFactory.changeLogLevel("fred",
     * LogLevel.OFF);
     *
     * @param loggingEntity The string by which the Stenographer is identified in the log.
     *
     * @param newLevel The logging level to be applied to this entity.
     *
     * @return true for success and false for failure
     *
     * @see Stenographer
     */
    public static boolean changeLogLevel(String loggingEntity, LogLevel newLevel)
    {
        boolean success = false;

        Stenographer steno = stenographers.get(loggingEntity);
        if (steno != null)
        {
            steno.changeLogLevel(newLevel);
        } else
        {
            success = false;
        }

        return success;
    }

    /**
     * Allows the log level for all Stenographers to be changed.
     *
     * @param newLevel The logging level to be applied to this entity.
     *
     * @return true for success and false for failure
     *
     * @see Stenographer
     */
    public static boolean changeAllLogLevels(LogLevel newLevel)
    {
        boolean success = false;
        
        initialLogLevel = newLevel;

        stenographers.values().forEach(stenographer ->
        {
            stenographer.changeLogLevel(newLevel);
        });

        success = true;

        return success;
    }

}
