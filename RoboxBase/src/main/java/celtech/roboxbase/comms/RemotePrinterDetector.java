package celtech.roboxbase.comms;

import celtech.roboxbase.configuration.CoreMemory;
import java.util.ArrayList;
import java.util.List;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian
 */
public class RemotePrinterDetector extends DeviceDetector
{

    private final Stenographer steno = StenographerFactory.getStenographer("RemotePrinterDetector");

    public RemotePrinterDetector()
    {
        super();
    }

    @Override
    public List<DetectedDevice> searchForDevices()
    {
        List<DetectedDevice> newlyDetectedPrinters = new ArrayList();

        //Take a copy of the list in case it gets changed under our feet
        List<DetectedServer> activeRoboxRoots = new ArrayList<>(CoreMemory.getInstance().getActiveRoboxRoots());

        // Search the roots that have been registered in core memory
        for (DetectedServer server : activeRoboxRoots)
        {
            if (server.getServerStatus() == DetectedServer.ServerStatus.CONNECTED)
            {
                List<DetectedDevice> attachedPrinters = server.listAttachedPrinters();
                newlyDetectedPrinters.addAll(attachedPrinters);
            }
        }
        
        return newlyDetectedPrinters;
    }
}
