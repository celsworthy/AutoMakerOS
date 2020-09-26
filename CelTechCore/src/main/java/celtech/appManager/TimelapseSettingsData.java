/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.appManager;

import celtech.roboxbase.camera.CameraInfo;
import celtech.roboxbase.configuration.fileRepresentation.CameraProfile;
import java.util.Optional;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 *
 * @author tonya
 */
public class TimelapseSettingsData {
    
    private boolean timelapseTriggerEnabled = false;
    private Optional<CameraProfile> timelapseProfile = Optional.empty();
    private Optional<CameraInfo> timelapseCamera = Optional.empty();
    private final BooleanProperty dataChanged = new SimpleBooleanProperty(false);
    
    public TimelapseSettingsData() {
    }
    
    public void toggleDataChanged()
    {
        dataChanged.set(dataChanged.not().get());
    }

    public ReadOnlyBooleanProperty getDataChanged()
    {
        return dataChanged;
    }

    public boolean getTimelapseTriggerEnabled()
    {
        return timelapseTriggerEnabled;
    }

    public void setTimelapseTriggerEnabled(boolean triggerEnabled)
    {
        if (this.timelapseTriggerEnabled != triggerEnabled)
        {
            this.timelapseTriggerEnabled = triggerEnabled;
            toggleDataChanged();
        }
    }

    public boolean isTimelapseEnabled()
    {
        return timelapseTriggerEnabled && timelapseProfile.isPresent() && timelapseCamera.isPresent();
    }

    public Optional<CameraProfile> getTimelapseProfile()
    {
        return timelapseProfile;
    }

    public void setTimelapseProfile(Optional<CameraProfile> profile)
    {
        if (!timelapseProfile.equals(profile))
        {
            this.timelapseProfile = profile;
            toggleDataChanged();
        }            
    }

    public Optional<CameraInfo> getTimelapseCamera()
    {
        return timelapseCamera;
    }

    public void setTimelapseCamera(Optional<CameraInfo> camera)
    {
        if (!timelapseCamera.equals(camera))
        {
            this.timelapseCamera = camera;
            toggleDataChanged();
        }            
    }
}
