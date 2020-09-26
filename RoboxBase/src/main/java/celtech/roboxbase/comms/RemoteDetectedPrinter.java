package celtech.roboxbase.comms;

import java.util.Objects;

/**
 *
 * @author Ian
 */
public class RemoteDetectedPrinter extends DetectedDevice
{
    private final DetectedServer serverPrinterIsAttachedTo;

    public RemoteDetectedPrinter(DetectedServer serverPrinterIsAttachedTo, DeviceDetector.DeviceConnectionType connectionType, String connectionHandle)
    {
        super(connectionType, connectionHandle);
        this.serverPrinterIsAttachedTo = serverPrinterIsAttachedTo;
    }

    public DetectedServer getServerPrinterIsAttachedTo()
    {
        return serverPrinterIsAttachedTo;
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean equal = false;

        if (obj instanceof RemoteDetectedPrinter
                && ((RemoteDetectedPrinter) obj).getConnectionHandle().equals(getConnectionHandle())
                && ((RemoteDetectedPrinter) obj).getConnectionType() == getConnectionType()
                && ((RemoteDetectedPrinter) obj).serverPrinterIsAttachedTo.equals(serverPrinterIsAttachedTo))
        {
            equal = true;
        }

        return equal;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(getConnectionType());
        hash = 32 * hash + Objects.hashCode(getConnectionHandle());
        hash = 66 * hash + Objects.hashCode(this.serverPrinterIsAttachedTo);
        return hash;
    }

    @Override
    public String toString()
    {
        return super.toString() + ":" + this.serverPrinterIsAttachedTo.toString();
    }
}
