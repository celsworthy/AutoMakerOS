package celuk.groot;

import com.beust.jcommander.Parameter;
import java.util.Locale;

/**
 * Command line arguments for GRoot.
 * 
 * @author Tony Aldhous
 */
public class GRootCommandLineArgs {
    @Parameter(names={"--config-directory", "-c"}, description = "Directory in which application configuration is saved")
    public String configurationDirectory = "";

    @Parameter(names={"--host", "-h"}, description = "Host name of Root server")
    public String hostName = "localhost";

    @Parameter(names={"--install-directory", "-i"}, description = "Directory in which application is installed")
    public String installDirectory = "./";

    @Parameter(names={"--language-tag", "-l"}, description = "tag to indicate the interface language")
    public String languageTag = "en";

    @Parameter(names={"--pin"}, description = "Access PIN of Root server")
    public String pin = "";

    @Parameter(names={"--port", "-p"}, description = "Port number of Root server")
    public String portNumber = "8080";

    @Parameter(names={"--show-splash-screen", "-s"}, description = "Show splash screen during startup")
    boolean showSplashScreen = false;

    @Parameter(names={"--update-interval", "-u"}, description = "Update interval in milliseconds")
    public int updateInterval = 2000;

    public GRootCommandLineArgs() {
        // Default to the system locale.
        Locale appLocale = Locale.getDefault();
        if (appLocale != null)
            languageTag = appLocale.getLanguage();
    }
}
