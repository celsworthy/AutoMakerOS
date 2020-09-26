package celtech.roboxbase.comms;

import java.net.InetAddress;

/**
 *
 * @author Ian
 */
public class RemotePrinterHost
{

    private final InetAddress hostAddress;

    public RemotePrinterHost(InetAddress address)
    {
        this.hostAddress = address;
    }

    public InetAddress getHostAddress()
    {
        return hostAddress;
    }

}
