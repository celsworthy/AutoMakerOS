package celtech.coreUI.controllers.panels.userpreferences;

import celtech.Lookup;
import celtech.configuration.UserPreferences;
import celtech.coreUI.controllers.panels.PreferencesInnerPanelController;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import libertysystems.stenographer.LogLevel;

/**
 *
 * @author Ian
 */
public class LogLevelPreference implements PreferencesInnerPanelController.Preference
{

    private final ComboBox<LogLevel> control;
    private final UserPreferences userPreferences;

    public LogLevelPreference(UserPreferences userPreferences)
    {
        this.userPreferences = userPreferences;

        control = new ComboBox<>();
        control.getStyleClass().add("cmbCleanCombo");
        control.setPrefWidth(200);
        control.setMinWidth(control.getPrefWidth());
        control.getItems().setAll(LogLevel.values());
        control.getSelectionModel().selectedItemProperty()
                .addListener(
                        (ObservableValue<? extends LogLevel> observable, LogLevel oldValue, LogLevel newValue) ->
                        {
                            updateValueFromControl();
                        });
    }

    @Override
    public void updateValueFromControl()
    {
        userPreferences.setLoggingLevel(control.getSelectionModel().selectedItemProperty().get());
    }

    @Override
    public void populateControlWithCurrentValue()
    {
        control.getSelectionModel().select(userPreferences.getLoggingLevel());
    }

    @Override
    public Control getControl()
    {
        return control;
    }

    @Override
    public String getDescription()
    {
        return Lookup.i18n("preferences.logLevel");
    }

    @Override
    public void disableProperty(ObservableValue<Boolean> disableProperty)
    {
        control.disableProperty().unbind();
        control.disableProperty().bind(disableProperty);
    }
}
