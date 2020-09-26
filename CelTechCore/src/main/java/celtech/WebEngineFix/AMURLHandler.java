package celtech.WebEngineFix;

import celtech.coreUI.controllers.panels.RootScannerPanelController;
import celtech.roboxbase.comms.remote.StringToBase64Encoder;
import celtech.roboxbase.configuration.BaseConfiguration;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import sun.net.www.protocol.http.HttpURLConnection;

/**
 *
 * @author Ian
 */
public class AMURLHandler extends URLStreamHandler
{

    @Override
    protected URLConnection openConnection(URL url) throws IOException
    {
        HttpURLConnection con = new HttpURLConnection(url, null);

        con.setRequestProperty("User-Agent", BaseConfiguration.getApplicationName());
        con.setRequestProperty("Authorization", "Basic " + StringToBase64Encoder.encode("root:" + RootScannerPanelController.pinForCurrentServer));
        return con;
    }

}
