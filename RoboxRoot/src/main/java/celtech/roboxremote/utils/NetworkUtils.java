package celtech.roboxremote.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 *
 * @author George Salter
 */
public class NetworkUtils 
{
    public static String determineIPAddress() throws SocketException
    {
        String hostAddress = "Unknown";
        
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface
                        .getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements())
        {
            NetworkInterface ni = (NetworkInterface) networkInterfaces
                    .nextElement();
            Enumeration<InetAddress> nias = ni.getInetAddresses();
            while (nias.hasMoreElements())
            {
                InetAddress ia = (InetAddress) nias.nextElement();
                if (!ia.isLinkLocalAddress()
                        && !ia.isLoopbackAddress()
                        && ia instanceof Inet4Address)
                {
                    hostAddress = ia.getHostAddress();
                    break;
                }
            }
        }
        
        return hostAddress;
    }
}
