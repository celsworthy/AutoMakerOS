package celtech.automaker;

import celtech.appManager.ApplicationMode;
import celtech.appManager.ApplicationStatus;
import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.configuration.BaseConfiguration;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Ian
 */
class WelcomeToApplicationManager
{

    static void displayWelcomeIfRequired()
    {
        if (applicationJustInstalled())
        {
            showWelcomePage();
            String localVersionFilename = BaseConfiguration.getApplicationName()
                    + "lastRunVersion";
            try
            {
                FileUtils.writeStringToFile(new File(BaseConfiguration.
                        getApplicationStorageDirectory() + localVersionFilename), BaseConfiguration.getApplicationVersion(), "US-ASCII");
            } catch (IOException ex)
            {
                System.err.println("Failed to write last run version");
            }
        }
    }

    private static boolean applicationJustInstalled()
    {
        boolean needToDisplayWelcome = false;
        String localVersionFilename = BaseConfiguration.getApplicationName()
                + "lastRunVersion";

        try
        {
            String lastRunVersion = FileUtils.readFileToString(new File(BaseConfiguration.
                    getApplicationStorageDirectory() + localVersionFilename), "US-ASCII");

            if (!lastRunVersion.equalsIgnoreCase(BaseConfiguration.getApplicationVersion()))
            {
                needToDisplayWelcome = true;
            }
        } catch (IOException ex)
        {
            //The file did not exist
            needToDisplayWelcome = true;
        }

        return needToDisplayWelcome;
    }

    private static void showWelcomePage()
    {
        BaseLookup.getTaskExecutor().runOnGUIThread(() ->
        {
            ApplicationStatus.getInstance().setMode(ApplicationMode.WELCOME);
        });
    }
}
