package celtech.roboxbase.comms.remote.clear;

import celtech.roboxbase.camera.CameraInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 *
 * @author George Salter
 */
public class ListCamerasResponse 
{
    private List<CameraInfo> cameras;
    
    /**
     * For Jackson
     */
    public ListCamerasResponse() {}
    
    public ListCamerasResponse(List<CameraInfo> cameras)
    {
        this.cameras = cameras;
    }

    @JsonProperty
    public List<CameraInfo> getCameras() 
    {
        return cameras;
    }

    @JsonProperty
    public void setCameras(List<CameraInfo> cameras)
    {
        this.cameras = cameras;
    }
    
    @Override
    public String toString()
    {
        StringBuilder output = new StringBuilder();
        output.append("ListCamerasResponse");
        output.append('\n');
        output.append('\n');
        cameras.forEach(camInfo ->
        {
            output.append(camInfo.toString());
            output.append('\n');
        });
        output.append("================");

        return output.toString();
    }
}
