package celtech.coreUI.controllers.panels.userpreferences;

import celtech.Lookup;
import celtech.configuration.UserPreferences;
import celtech.coreUI.controllers.panels.PreferencesInnerPanelController;
import celtech.roboxbase.comms.DetectedDevice;
import celtech.roboxbase.comms.RoboxCommsManager;
import celtech.roboxbase.configuration.hardwarevariants.PrinterType;
import java.util.Optional;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.util.StringConverter;

/**
 *
 * @author George Salter
 */
public class CustomPrinterTypePreference implements PreferencesInnerPanelController.Preference {

    private final ComboBox<PrinterType> control;
    private final UserPreferences userPreferences;
    
    public CustomPrinterTypePreference(UserPreferences userPreferences) {
        this.userPreferences = userPreferences;
        
        control = new ComboBox<>();
        control.getStyleClass().add("cmbCleanCombo");
        control.setPrefWidth(200);
        control.getItems().setAll(PrinterType.values());
        control.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> updateValueFromControl());
        control.setConverter(new StringConverter<PrinterType>() 
        {
            @Override
            public String toString(PrinterType printerType) 
            {
                return printerType.getDisplayName();
            }

            @Override
            public PrinterType fromString(String displayName) 
            {
                return PrinterType.getPrinterTypeForDisplayName(displayName);
            }
        });
    }
    
    @Override
    public void updateValueFromControl() {
        RoboxCommsManager comms = RoboxCommsManager.getInstance();
        
        PrinterType printerType = control.getSelectionModel().selectedItemProperty().get();
        userPreferences.setCustomPrinterType(printerType);
        comms.setDummyPrinterType(printerType);
        
        Optional<DetectedDevice> dummyPrinterHandle = comms.getDummyPrinter(RoboxCommsManager.CUSTOM_CONNECTION_HANDLE);
        if(dummyPrinterHandle.isPresent()) {
            comms.removeDummyPrinter(dummyPrinterHandle.get());
            comms.addDummyPrinter(true);
        }
    }

    @Override
    public void populateControlWithCurrentValue() {
        control.getSelectionModel().select(userPreferences.getCustomPrinterType());
    }

    @Override
    public Control getControl() {
        return control;
    }

    @Override
    public String getDescription() {
        return Lookup.i18n("preferences.printerType");
    }

    @Override
    public void disableProperty(ObservableValue<Boolean> disableProperty) {
        control.disableProperty().unbind();
        control.disableProperty().bind(disableProperty);
    }
    
}
