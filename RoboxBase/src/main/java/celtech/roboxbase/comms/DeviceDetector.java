package celtech.roboxbase.comms;

import java.util.List;

/**
 *
 * @author Ian
 */
public abstract class DeviceDetector
{
    public static final String NOT_CONNECTED_STRING = "NOT_CONNECTED";
    
    public DeviceDetector()
    {
    }

    public enum DeviceConnectionType
    {
        SERIAL,
        ROBOX_REMOTE,
        DUMMY,
        USB
    }
    
    public abstract List<DetectedDevice> searchForDevices();
}
