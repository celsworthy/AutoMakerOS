package celtech.appManager;

import celtech.configuration.ApplicationConfiguration;
import celtech.coreUI.controllers.MyMiniFactoryLoaderController;
import celtech.coreUI.controllers.panels.AboutPanelController;
import celtech.coreUI.controllers.panels.CalibrationInsetPanelController;
import celtech.coreUI.controllers.panels.ExtrasMenuPanelController;
import celtech.coreUI.controllers.panels.LibraryMenuPanelController;
import celtech.coreUI.controllers.panels.LoadModelInsetPanelController;
import celtech.coreUI.controllers.panels.MaintenanceInsetPanelController;
import celtech.coreUI.controllers.panels.PurgeInsetPanelController;
import celtech.coreUI.controllers.panels.RegistrationInsetPanelController;
import celtech.coreUI.controllers.panels.WelcomeInsetPanelController;

/**
 *
 * @author ianhudson
 */
public enum ApplicationMode
{

    WELCOME("Welcome", WelcomeInsetPanelController.class),
    CALIBRATION_CHOICE("Calibration", CalibrationInsetPanelController.class),
    REGISTRATION("registration", RegistrationInsetPanelController.class),
    PURGE("purge", PurgeInsetPanelController.class),
    MAINTENANCE("Maintenance", MaintenanceInsetPanelController.class),
    ABOUT("about", AboutPanelController.class),
    SYSTEM_INFORMATION("systemInformation", null),
    EXTRAS_MENU("extrasMenu", ExtrasMenuPanelController.class),
    //TODO printer status has to be last otherwise the temperature graph doesn't work!! Fix in DisplayManager
    STATUS(null, null),
    /**
     *
     */
    LAYOUT(null, null),
    ADD_MODEL("loadModel", LoadModelInsetPanelController.class),
    MY_MINI_FACTORY("myMiniFactoryLoader", MyMiniFactoryLoaderController.class),
    /**
     *
     */
    SETTINGS(null, null),
    LIBRARY("extrasMenu", LibraryMenuPanelController.class);
    
    //    NEWS("news", NewsController.class);

    private final String insetPanelFXMLPrefix;
    private final Class controllerClass;

    private ApplicationMode(String insetPanelFXMLPrefix, Class<?> controllerClass)
    {
        this.insetPanelFXMLPrefix = insetPanelFXMLPrefix;
        this.controllerClass = controllerClass;
    }

    /**
     *
     * @return
     */
    public String getInsetPanelFXMLName()
    {
        return ApplicationConfiguration.fxmlPanelResourcePath + insetPanelFXMLPrefix + "InsetPanel" + ".fxml";
    }

    public Class getControllerClass()
    {
        return controllerClass;
    }
}
