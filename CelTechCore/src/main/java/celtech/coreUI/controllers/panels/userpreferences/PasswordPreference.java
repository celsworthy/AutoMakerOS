package celtech.coreUI.controllers.panels.userpreferences;

import celtech.Lookup;
import celtech.coreUI.controllers.panels.PreferencesInnerPanelController;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Control;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 *
 * @author Ian
 */
public class PasswordPreference implements PreferencesInnerPanelController.Preference
{

    private final TextField control;
    private final StringProperty stringProperty;
    private final String caption;

    public PasswordPreference(StringProperty stringProperty, String caption)
    {
        this.stringProperty = stringProperty;
        this.caption = caption;

        control = new PasswordField();
        control.setPrefWidth(250);
        control.setMinWidth(control.getPrefWidth());
        control.setMaxWidth(control.getPrefWidth());
        control.textProperty().addListener(
                (ObservableValue<? extends String> observable, String oldValue, String newValue) ->
                {
                    updateValueFromControl();
                });
        stringProperty.addListener(
                (ObservableValue<? extends String> observable, String oldValue, String newValue) ->
                {
                    control.setText(newValue);
                });
    }

    @Override
    public void updateValueFromControl()
    {
        stringProperty.set(control.getText());

        // User Preferences controls whether the property can be set - read back just in case our selection was overridden
        control.setText(stringProperty.get());
    }

    @Override
    public void populateControlWithCurrentValue()
    {
        control.setText(stringProperty.get());
    }

    @Override
    public Control getControl()
    {
        return control;
    }

    @Override
    public String getDescription()
    {
        return Lookup.i18n(caption);
    }

    @Override
    public void disableProperty(ObservableValue<Boolean> disableProperty)
    {
        control.disableProperty().unbind();
        control.disableProperty().bind(disableProperty);
    }
}
