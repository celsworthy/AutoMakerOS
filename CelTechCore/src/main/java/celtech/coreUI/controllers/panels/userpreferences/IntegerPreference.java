package celtech.coreUI.controllers.panels.userpreferences;

import celtech.Lookup;
import celtech.coreUI.components.RestrictedNumberField;
import celtech.coreUI.controllers.panels.PreferencesInnerPanelController;
import javafx.beans.property.IntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Control;

/**
 *
 * @author Ian
 */
public class IntegerPreference implements PreferencesInnerPanelController.Preference
{

    private final RestrictedNumberField control;
    private final IntegerProperty integerProperty;
    private final String caption;

    public IntegerPreference(IntegerProperty integerProperty,
            String caption)
    {
        this.integerProperty = integerProperty;
        this.caption = caption;

        control = new RestrictedNumberField();
        control.setPrefWidth(150);
        control.setMinWidth(control.getPrefWidth());
        control.setMaxWidth(control.getPrefWidth());
        control.setAllowedDecimalPlaces(0);
        control.setAllowNegative(false);
        control.setMaxLength(4);
        control.valueChangedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) ->
        {
            updateValueFromControl();
        });
    }

    @Override
    public void updateValueFromControl()
    {
        integerProperty.set(control.getAsInt());

        // User Preferences controls whether the property can be set - read back just in case our selection was overridden
        control.setValue(integerProperty.get());
    }

    @Override
    public void populateControlWithCurrentValue()
    {
        control.setValue(integerProperty.get());
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
