/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.appManager;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author ianhudson
 */
public class ApplicationStatus
{

    private static ApplicationStatus instance = null;
    private final ObjectProperty<ApplicationMode> currentMode = new SimpleObjectProperty<ApplicationMode>(null);
    private final StringProperty modeStringProperty = new SimpleStringProperty();
    private final StringProperty modeDisplayStringProperty = new SimpleStringProperty();
    private boolean expertMode = false;
    private final DoubleProperty averageTimePerFrameProperty = new SimpleDoubleProperty(0);
    private static ApplicationMode lastMode = null;

    private ApplicationStatus()
    {
    }

    /**
     *
     * @return
     */
    public static ApplicationStatus getInstance()
    {
        if (instance == null)
        {
            instance = new ApplicationStatus();
        }

        return instance;
    }

    /**
     *
     * @param newMode
     */
    public void setMode(ApplicationMode newMode)
    {
        if (currentMode.get() != ApplicationMode.ABOUT
                && currentMode.get() != ApplicationMode.PURGE
                && currentMode.get() != ApplicationMode.CALIBRATION_CHOICE
                && currentMode.get() != ApplicationMode.EXTRAS_MENU
                && currentMode.get() != ApplicationMode.LIBRARY)
        {
            lastMode = currentMode.get();
        }
        currentMode.setValue(newMode);
    }

    /**
     *
     * @return
     */
    public final ApplicationMode getMode()
    {
        return currentMode.getValue();
    }

    /**
     *
     * @return
     */
    public final ObjectProperty<ApplicationMode> modeProperty()
    {
        return currentMode;
    }

    /**
     *
     * @param isExpertMode
     */
    public void setExpertMode(boolean isExpertMode)
    {
        expertMode = isExpertMode;
    }

    /**
     *
     * @return
     */
    public boolean isExpertMode()
    {
        return expertMode;
    }

    /**
     *
     * @param value
     */
    public final void setAverageTimePerFrame(double value)
    {
        averageTimePerFrameProperty.set(value);
    }

    /**
     *
     * @return
     */
    public final double getAverageTimePerFrame()
    {
        return averageTimePerFrameProperty.get();
    }

    /**
     *
     * @return
     */
    public final DoubleProperty averageTimePerFrameProperty()
    {
        return averageTimePerFrameProperty;
    }

    public void returnToLastMode()
    {
        if (lastMode != null)
        {
            setMode(lastMode);
        }
    }
}
