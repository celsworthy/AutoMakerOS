package celtech.roboxbase.utils;

import celtech.roboxbase.configuration.BaseConfiguration;
import java.util.Date;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian
 */
public class ApplicationUtils
{

    private static final Stenographer steno = StenographerFactory.getStenographer(ApplicationUtils.class.
            getName());

    public static void outputApplicationStartupBanner(Class parentClass)
    {
        steno.info("**********************************************************************");
        steno.info("Starting " + BaseConfiguration.getApplicationName());
        steno.info("Date " + new Date());
        steno.info("Version: " + BaseConfiguration.getApplicationVersion());
        steno.info("Installation directory: " + BaseConfiguration.
                getApplicationInstallDirectory(parentClass));
        steno.info("Machine type: " + BaseConfiguration.getMachineType());
        steno.info("**********************************************************************");
    }

    public static void outputApplicationShutdownBanner()
    {
        steno.info("**********************************************************************");
        steno.info("Shutting down " + BaseConfiguration.getApplicationName());
        steno.info("Date " + new Date());
        steno.info("**********************************************************************");
    }
}
