package celtech.roboxremote.comms;

import celtech.roboxbase.comms.DetectedDevice;
import celtech.roboxbase.comms.DeviceDetector;

/**
 *
 * @author George Salter
 */
public class DetectedCamera extends DetectedDevice
{

    public DetectedCamera(DeviceDetector.DeviceConnectionType connectionType, String connectionHandle) 
    {
        super(connectionType, connectionHandle);
    }

}
