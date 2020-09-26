package celtech.roboxbase.camera;

import celtech.roboxbase.comms.DetectedServer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import org.parboiled.common.FileUtils;

/**
 *
 * @author George Salter
 */
public class CameraInfo implements Comparable<CameraInfo>
{
    private String udevName = "";
    private String cameraName = "";
    private int cameraNumber = 0;
    private String serverIP = "";
    private DetectedServer server = null;

    public CameraInfo() {
    }

    @JsonProperty
    public String getUdevName() 
    {
        return udevName;
    }

    @JsonProperty
    public void setUdevName(String udevName) 
    {
        this.udevName = udevName;
    }

    @JsonProperty
    public String getCameraName()
    {
        return cameraName;
    }

    @JsonProperty
    public void setCameraName(String cameraName) 
    {
        this.cameraName = cameraName;
    }

    @JsonProperty
    public int getCameraNumber() 
    {
        return cameraNumber;
    }

    @JsonProperty
    public void setCameraNumber(int cameraNumber)
    {
        this.cameraNumber = cameraNumber;
    }

    @JsonProperty
    public String getServerIP() 
    {
        return serverIP;
    }
    
    @JsonProperty
    public void setServerIP(String serverIP)
    {
        this.serverIP = serverIP;
    }
    
    @JsonIgnore
    public DetectedServer getServer() 
    {
        return server;
    }
    
    @JsonIgnore
    public void setServer(DetectedServer server)
    {
        this.server = server;
    }

    @Override
    public String toString()
    {
        String cameraInfoString = 
                "Camera name: " + cameraName + "\n" +
                "UDev name: " + udevName + "\n" +
                "Camera Number: " + cameraNumber + "\n" +
                "Server addess: " + serverIP;
        return cameraInfoString;
    }

    @Override
    public int hashCode() 
    {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.udevName);
        hash = 79 * hash + Objects.hashCode(this.cameraName);
        hash = 79 * hash + this.cameraNumber;
        hash = 79 * hash + Objects.hashCode(this.serverIP);
        return hash;
    }

    @Override
    public boolean equals(Object obj) 
    {
        if (this == obj) 
        {
            return true;
        }
        if (obj == null) 
        {
            return false;
        }
        if (getClass() != obj.getClass()) 
        {
            return false;
        }
        
        final CameraInfo other = (CameraInfo) obj;
        
        if (this.cameraNumber != other.cameraNumber) 
        {
            return false;
        }
        if (!Objects.equals(this.udevName, other.udevName)) 
        {
            return false;
        }
        if (!Objects.equals(this.cameraName, other.cameraName))
        {
            return false;
        }
        if (!Objects.equals(this.serverIP, other.serverIP)) 
        {
            return false;
        }
        if (!Objects.equals(this.server, other.server)) 
        {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(CameraInfo other) {
        int c = serverIP.compareTo(serverIP);
        if (c == 0) {
            c = cameraNumber - other.cameraNumber;
        }
        return c;
    }
    
    @JsonIgnore
    public void writeToFile(String fileLocation) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();

        File file = new File(fileLocation);
        FileUtils.ensureParentDir(file);
        mapper.writeValue(file, this);
    }

    @JsonIgnore
    public static CameraInfo readFromFile(String fileLocation) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();

        File file = new File(fileLocation);
        return mapper.readValue(file, CameraInfo.class);
    }
}
