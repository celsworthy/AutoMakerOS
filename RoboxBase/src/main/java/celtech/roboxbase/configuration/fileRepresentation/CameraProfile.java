package celtech.roboxbase.configuration.fileRepresentation;

import celtech.roboxbase.configuration.BaseConfiguration;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author George Salter
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CameraProfile 
{
    @JsonProperty("profileName")
    private String profileName = BaseConfiguration.defaultCameraProfileName;
    
    @JsonProperty("captureHeight")
    private int captureHeight = 720;
    
    @JsonProperty("captureWidth")
    private int captureWidth = 1080;
    
    @JsonProperty("headLightOff")
    private boolean headLightOff = true;
    
    @JsonProperty("ambientLightOff")
    private boolean ambientLightOff = false;
    
    @JsonProperty("moveBeforeCapture")
    private boolean moveBeforeCapture = true;

    @JsonProperty("moveToX")
    private int moveToX = 50;

    @JsonProperty("moveToY")
    private int moveToY = 50;

    @JsonProperty("cameraName")
    private String cameraName = "";

    @JsonProperty("controlSettings")
    private Map<String, String> controlSettings = new HashMap<>();

    @JsonIgnore()
    private boolean systemProfile = false;
    
    /**
     * Default constructor of Jackson
     */
    public CameraProfile() {}
    
    public CameraProfile(CameraProfile profileToCopy) 
    {
        profileName = "";
        captureHeight = profileToCopy.getCaptureHeight();
        captureWidth = profileToCopy.getCaptureWidth();
        headLightOff = profileToCopy.isHeadLightOff();
        ambientLightOff = profileToCopy.isAmbientLightOff();
        moveBeforeCapture = profileToCopy.isMoveBeforeCapture();
        moveToX = profileToCopy.getMoveToX();
        moveToY = profileToCopy.getMoveToY();
        cameraName = profileToCopy.getCameraName();
        controlSettings = new HashMap<>(profileToCopy.getControlSettings());
        systemProfile = false;
    }

    public String getProfileName()
    {
        return profileName;
    }
    
    public void setProfileName(String profileName)
    {
        this.profileName = profileName;
    }
    
    public int getCaptureHeight()
    {
        return captureHeight;
    }

    public void setCaptureHeight(int captureHeight)
    {
        this.captureHeight = captureHeight;
    }

    public int getCaptureWidth()
    {
        return captureWidth;
    }

    public void setCaptureWidth(int captureWidth) 
    {
        this.captureWidth = captureWidth;
    }

    public boolean isHeadLightOff() 
    {
        return headLightOff;
    }

    public void setHeadLightOff(boolean headLightOff) 
    {
        this.headLightOff = headLightOff;
    }

    public void setAmbientLightOff(boolean ambientLightOff) 
    {
        this.ambientLightOff = ambientLightOff;
    }

    public boolean isAmbientLightOff() 
    {
        return ambientLightOff;
    }

    public void setMoveBeforeCapture(boolean moveBeforeCapture) 
    {
        this.moveBeforeCapture = moveBeforeCapture;
    }
    
    public boolean isMoveBeforeCapture() 
    {
        return moveBeforeCapture;
    }

    public void setMoveToX(int moveToX) 
    {
        this.moveToX = moveToX;
    }
    
    public int getMoveToX() 
    {
        return moveToX;
    }

    public void setMoveToY(int moveToY) 
    {
        this.moveToY = moveToY;
    }
    
    public int getMoveToY() 
    {
        return moveToY;
    }

    public String getCameraName()
    {
        return cameraName;
    }
    
    public void setCameraName(String cameraName)
    {
        this.cameraName = cameraName;
    }
    
    public Map<String, String> getControlSettings() 
    {
        return controlSettings;
    }

    public void setControlSettings(Map<String, String> controlSettings) 
    {
        this.controlSettings = controlSettings;
    }

    public String getControlSetting(String control) 
    {
        return controlSettings.get(control);
    }

    public void setControlSetting(String control, String value) 
    {
        controlSettings.put(control, value);
    }
    
    @JsonIgnore()
    public boolean isSystemProfile() 
    {
        return systemProfile;
    }

    @JsonIgnore()
    public void setSystemProfile(boolean systemProfile) 
    {
        this.systemProfile = systemProfile;
    }
}
