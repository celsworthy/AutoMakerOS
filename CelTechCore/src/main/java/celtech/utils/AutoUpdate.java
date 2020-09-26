package celtech.utils;

import celtech.Lookup;
import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.configuration.CoreMemory;
import celtech.roboxbase.configuration.MachineType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class AutoUpdate extends Thread
{

    private static final Stenographer steno = StenographerFactory.getStenographer(AutoUpdate.class.getName());

    private String applicationName = null;
    private final int ERROR = -1;
    private final int UPGRADE_NOT_REQUIRED = 0;
    private final int UPGRADE_REQUIRED = 1;
    private final int UPGRADE_NOT_AVAILABLE_FOR_THIS_RELEASE = 2;

    private boolean keepRunning = true;
    private Class parentClass = null;
    private AutoUpdateCompletionListener completionListener = null;
    private String appDirectory = null;

    private final Pattern versionMatcherPattern = Pattern.compile(".*version>(.*)</version.*");

    /**
     *
     * @param applicationName
     * @param appDirectory
     * @param completionListener
     */
    public AutoUpdate(String applicationName, String appDirectory, AutoUpdateCompletionListener completionListener)
    {
        this.applicationName = applicationName;
        this.appDirectory = appDirectory;
        this.setName("AutoUpdate");
        this.parentClass = completionListener.getClass();
        this.completionListener = completionListener;
    }

    /**
     *
     */
    @Override
    public void run()
    {
        int strikes = 0;

        //Check for a new version 15 secs after startup
        try
        {
            this.sleep(1000);
        } catch (InterruptedException ex)
        {
            steno.warning("AutoUpdate sleep was interrupted");
        }

        while (strikes < 1 && keepRunning)
        {
            int status = checkForUpdates();

            switch (status)
            {
                case UPGRADE_NOT_REQUIRED:
                    keepRunning = false;
                    completionListener.autoUpdateComplete(false);
                    break;
                case UPGRADE_NOT_AVAILABLE_FOR_THIS_RELEASE:
                    Platform.runLater(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            BaseLookup.getSystemNotificationHandler().showInformationNotification(Lookup.i18n("dialogs.updateApplicationTitle"), Lookup.i18n("dialogs.updateApplicationNotAvailableForThisRelease")
                                    + " " + applicationName);
                        }
                    });
                    keepRunning = false;
                    completionListener.autoUpdateComplete(false);
                    break;
                case UPGRADE_REQUIRED:

                    boolean upgradeApplication = BaseLookup.getSystemNotificationHandler().showApplicationUpgradeDialog(applicationName);
                    if (upgradeApplication)
                    {
                        //Run the autoupdater in the background in download mode
                        startUpdate();
                        completionListener.autoUpdateComplete(true);
                    } else
                    {
                        completionListener.autoUpdateComplete(false);
                    }
                    keepRunning = false;
                    break;
                case ERROR:
                    Platform.runLater(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            BaseLookup.getSystemNotificationHandler().showErrorNotification(Lookup.i18n("dialogs.updateApplicationTitle"), Lookup.i18n("dialogs.updateFailedToContact"));
                        }
                    });
                    try
                    {
                        this.sleep(5000);
                    } catch (InterruptedException ex)
                    {
                        steno.warning("AutoUpdate sleep was interrupted");
                    }

                    strikes++;
                    break;
            }
        }

        if (keepRunning)
        {
            //We must have struck out
            steno.error("Failed to check for update");
            completionListener.autoUpdateComplete(false);
        }
    }

    private int checkForUpdates()
    {
        int upgradeStatus = ERROR;

        String url = "https://downloads.cel-uk.com/software/update/" + appDirectory + "/" + applicationName + "-update.xml";

        String encodedSwVersion = null;
        try
        {
            encodedSwVersion = URLEncoder.encode(BaseConfiguration.getApplicationVersion(), "UTF-8");
            url += "?sw=" + encodedSwVersion;
        } catch (UnsupportedEncodingException ex)
        {
        }

        String encodedFwVersion = null;
        try
        {
            encodedFwVersion = URLEncoder.encode(String.format(Locale.UK, "%.2f", CoreMemory.getInstance().getLastPrinterFirmwareVersion()), "UTF-8");
            url += "&fw=" + encodedFwVersion;
        } catch (UnsupportedEncodingException ex)
        {
        }

        String encodedHwVersion = null;
        try
        {
            if (CoreMemory.getInstance().getLastPrinterSerial() != null)
            {
                encodedHwVersion = URLEncoder.encode(CoreMemory.getInstance().getLastPrinterSerial(), "UTF-8");
                url += "&hw=" + encodedHwVersion;
            }
        } catch (UnsupportedEncodingException ex)
        {
        }

        try
        {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("User-Agent", BaseConfiguration.getApplicationName());

            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            
            int responseCode = con.getResponseCode();

            if (responseCode == 200)
            {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null)
                {
                    response.append(inputLine);
                }
                in.close();

                Matcher versionMatcher = versionMatcherPattern.matcher(response);

                if (versionMatcher.find())
                {
                    String concatenatedServerVersionField = versionMatcher.group(1).replaceAll("\\.", "");
                    String concatenatedAppVersionField = BaseConfiguration.getApplicationVersion().replaceAll("\\.", "");

                    int serverVersionNumber = Integer.valueOf(concatenatedServerVersionField);
                    try
                    {
                        int appVersionNumber = Integer.valueOf(concatenatedAppVersionField);
                        if (serverVersionNumber > appVersionNumber)
                        {
                            upgradeStatus = UPGRADE_REQUIRED;
                        } else
                        {
                            upgradeStatus = UPGRADE_NOT_REQUIRED;
                        }
                    } catch (NumberFormatException ex)
                    {
                        upgradeStatus = UPGRADE_NOT_AVAILABLE_FOR_THIS_RELEASE;
                    }

                }
            }
        } catch (IOException ex)
        {
            steno.error("Exception whilst attempting to contact update server");
        }

        return upgradeStatus;
    }

    private void startUpdate()
    {
        String osName = System.getProperty("os.name");

        MachineType machineType = BaseConfiguration.getMachineType();

        ArrayList<String> commands = new ArrayList<>();

        switch (machineType)
        {
            case WINDOWS_95:
                commands.add("command.com");
                commands.add("/S");
                commands.add("/C");
                commands.add("\"\"" + BaseConfiguration.getApplicationInstallDirectory(parentClass) + applicationName + "-update-windows.exe\"\"");
                break;
            case WINDOWS:
                commands.add("cmd.exe");
                commands.add("/S");
                commands.add("/C");
                if (BaseConfiguration.isWindows32Bit())
                    commands.add("\"\"" + BaseConfiguration.getApplicationInstallDirectory(parentClass) + applicationName + "-update-windows.exe\"\"");
                else
                    commands.add("\"\"" + BaseConfiguration.getApplicationInstallDirectory(parentClass) + applicationName + "-update-windows-x64.exe\"\"");
                break;
            case MAC:
                commands.add(BaseConfiguration.getApplicationInstallDirectory(parentClass) + applicationName + "-update-osx.app/Contents/MacOS/installbuilder.sh");
                break;
            case LINUX_X86:
                commands.add(BaseConfiguration.getApplicationInstallDirectory(parentClass) + applicationName + "-update-linux.run");
                break;
            case LINUX_X64:
                commands.add(BaseConfiguration.getApplicationInstallDirectory(parentClass) + applicationName + "-update-linux-x64.run");
                break;
        }
        /*
         * Return codes from the (BitRock) autoupdater
         * 
         * 0: Successfully downloaded and executed the installer.
         * 1: No updates available
         * 2: Error connecting to remote server or invalid XML file
         * 3: An error occurred downloading the file
         * 4: An error occurred executing the downloaded update or evaluating its <postUpdateDownloadActionList>
         * 5: Update check disabled through check_for_updates setting
         */

        if (commands.size() > 0)
        {
            ProcessBuilder autoupdateProcess = new ProcessBuilder(commands);
            autoupdateProcess.inheritIO();
            try
            {
                final Process updateProc = autoupdateProcess.start();
                steno.info("Autoupdate initiated");
            } catch (IOException ex)
            {
                steno.error("Exception whilst running autoupdate: " + ex);
            }
        } else
        {
            steno.error("Couldn't run autoupdate - no commands for OS " + osName);
        }
    }

    /**
     *
     */
    public void shutdown()
    {
        keepRunning = false;
    }
}
