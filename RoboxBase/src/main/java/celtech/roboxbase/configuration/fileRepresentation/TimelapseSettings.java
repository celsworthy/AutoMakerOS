package celtech.roboxbase.configuration.fileRepresentation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 *
 * @author Tony Aldhous
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TimelapseSettings 
{
    @JsonProperty("triggerEnabled")
    private boolean triggerEnabled = false;

    @JsonProperty("cameraProfileName")
    private String cameraProfileName;
    
    @JsonProperty("cameraName")
    private String cameraName;
    
    @JsonIgnore
    private final BooleanProperty dataChanged = new SimpleBooleanProperty(false);

    /**
     * Default constructor for Jackson
     */
    public TimelapseSettings() {}
    
    public TimelapseSettings(TimelapseSettings settingsToCopy) 
    {
        this.triggerEnabled = settingsToCopy.triggerEnabled;
        this.cameraProfileName = settingsToCopy.cameraProfileName;
        this.cameraName = settingsToCopy.cameraName;
        this.dataChanged.set(false);
    }
    
    @JsonIgnore
    private void toggleDataChanged()
    {
        dataChanged.set(dataChanged.not().get());
    }

    @JsonIgnore
    public ReadOnlyBooleanProperty getDataChanged()
    {
        return dataChanged;
    }
    
    public boolean getTriggerEnabled()
    {
        return triggerEnabled;
    }

    public void setTriggerEnabled(boolean triggerEnabled)
    {
        if (this.triggerEnabled != triggerEnabled)
        {
            this.triggerEnabled = triggerEnabled;
            toggleDataChanged();
        }
    }

    public String getCameraProfileName()
    {
        return cameraProfileName;
    }
    
    public void setCameraProfileName(String cameraProfileName)
    {
        if ((cameraProfileName == null || cameraProfileName.isEmpty())
             && !this.cameraProfileName.isEmpty()) {
            this.cameraProfileName = "";
            toggleDataChanged();
        }
        else if (!cameraProfileName.equals(this.cameraProfileName))
        {
            this.cameraProfileName = cameraProfileName;
            toggleDataChanged();
        }
    }

    public String getCameraName()
    {
        return cameraName;
    }
    
    public void setCameraName(String cameraName)
    {
        if ((cameraName == null || cameraName.isEmpty())
             && !this.cameraName.isEmpty()) {
            this.cameraName = "";
            toggleDataChanged();
        }
        else if (!cameraName.equals(this.cameraName))
        {
            this.cameraName = cameraName;
            toggleDataChanged();
        }
    }
}
