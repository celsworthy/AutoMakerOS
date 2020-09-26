package celtech.roboxbase.comms;

import celtech.roboxbase.camera.CameraInfo;
import celtech.roboxbase.configuration.CoreMemory;
import java.util.ArrayList;
import java.util.List;

/**
 * This class currently does not extend DeviceDetector as the search for devices method wants to return
 * a list of DetectedDevices, at this point though we have a list of CameraInfo objects which we want to return.
 * 
 * @author George Salter
 */
public class RemoteCameraDetector
{
    public RemoteCameraDetector()
    {
        super();
    }
    
    public List<CameraInfo> searchForDevices()
    {
        //Take a copy of the list in case it gets changed under our feet
        List<DetectedServer> activeRoboxRoots = new ArrayList<>(CoreMemory.getInstance().getActiveRoboxRoots());
        List<CameraInfo> allConnectedCameras = new ArrayList<>();
        
        // Search the roots that have been registered in core memory
        activeRoboxRoots.stream().filter(server -> (server.getServerStatus() == DetectedServer.ServerStatus.CONNECTED)).forEachOrdered((server) -> {
            List<CameraInfo> attachedCameras = server.listAttachedCameras();
            attachedCameras.forEach(camInfo -> camInfo.setServerIP(server.getServerIP()));
            allConnectedCameras.addAll(attachedCameras);
        });
        
        return allConnectedCameras;
    }
    
}
