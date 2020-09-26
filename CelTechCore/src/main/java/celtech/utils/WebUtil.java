package celtech.utils;

import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.configuration.MachineType;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 * @author George Salter
 */
public class WebUtil 
{
    public static void launchURL(String url)
    {
        if (Desktop.isDesktopSupported()
                && BaseConfiguration.getMachineType()
                != MachineType.LINUX_X86
                && BaseConfiguration.getMachineType()
                != MachineType.LINUX_X64)
        {
            try
            {
                URI linkToVisit = new URI(url);
                Desktop.getDesktop().browse(linkToVisit);
            } catch (IOException | URISyntaxException ex)
            {
                System.err.println("Error when attempting to browse to "
                        + url);
            }
        } else if (BaseConfiguration.getMachineType() == MachineType.LINUX_X86
                || BaseConfiguration.getMachineType() == MachineType.LINUX_X64)
        {
            try
            {
                if (Runtime.getRuntime().exec(new String[]
                {
                    "which", "xdg-open"
                }).getInputStream().read() != -1)
                {
                    Runtime.getRuntime().exec(new String[]
                    {
                        "xdg-open", url
                    });
                }
            } catch (IOException ex)
            {
                System.err.println("Failed to run linux-specific browser command");
            }
        } else
        {
            System.err.println(
                    "Couldn't get Desktop - not able to support hyperlinks");
        }
    }
}
