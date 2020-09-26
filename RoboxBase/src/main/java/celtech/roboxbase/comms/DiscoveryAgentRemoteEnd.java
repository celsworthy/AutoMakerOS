package celtech.roboxbase.comms;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian
 */
public class DiscoveryAgentRemoteEnd implements Runnable
{

    private Stenographer steno = StenographerFactory.getStenographer(DiscoveryAgentRemoteEnd.class.getName());
    private boolean keepRunning = true;
    private String applicationVersion = null;

    public DiscoveryAgentRemoteEnd(String applicationVersion)
    {
        this.applicationVersion = applicationVersion;
    }

    @Override
    public void run()
    {
        // The outer loop is to deal with disconnections that happen if the IP address changes
        boolean listeningToMulticast = true;

        while (keepRunning)
        {
            try
            {
                InetAddress group = InetAddress.getByName(RemoteDiscovery.multicastAddress);
                MulticastSocket s = new MulticastSocket(RemoteDiscovery.remoteSocket);
                s.joinGroup(group);
                listeningToMulticast = true;

                while (keepRunning)
                {
                    byte[] buf = new byte[100];
                    DatagramPacket recv = new DatagramPacket(buf, buf.length);
                    s.receive(recv);

                    if (Arrays.equals(Arrays.copyOf(buf, RemoteDiscovery.discoverHostsMessage.getBytes("US-ASCII").length),
                            RemoteDiscovery.discoverHostsMessage.getBytes("US-ASCII")))
                    {
                        byte[] responseBuf = (RemoteDiscovery.iAmHereMessage).getBytes("US-ASCII");
                        DatagramPacket response = new DatagramPacket(responseBuf, responseBuf.length, recv.getAddress(), recv.getPort());
                        s.send(response);
                    }

                }

                s.leaveGroup(group);
            } catch (IOException ex)
            {
                if (listeningToMulticast)
                {
                    steno.warning("Error listening for multicast messages");
                }
                listeningToMulticast = false;
            }

            try
            {
                Thread.sleep(1000);
            } catch (InterruptedException ex)
            {
            }
        }
    }

    public void shutdown()
    {
        keepRunning = false;
    }
}
