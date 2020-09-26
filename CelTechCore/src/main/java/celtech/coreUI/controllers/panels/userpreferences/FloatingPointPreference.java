package celtech.coreUI.controllers.panels.userpreferences;

import celtech.Lookup;
import celtech.coreUI.components.RestrictedNumberField;
import celtech.coreUI.controllers.panels.PreferencesInnerPanelController;
import javafx.beans.property.FloatProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Control;

/**
 *
 * @author Ian
 */
public class FloatingPointPreference implements PreferencesInnerPanelController.Preference
{

    private final RestrictedNumberField control;
    private final FloatProperty floatProperty;
    private final String caption;

    public FloatingPointPreference(FloatProperty floatProperty,
            int decimalPlaces,
            int digits,
            boolean negativeAllowed,
            String caption)
    {
        this.floatProperty = floatProperty;
        this.caption = caption;

        control = new RestrictedNumberField();
        control.setPrefWidth(150);
        control.setMaxWidth(control.getPrefWidth());
        control.setMinWidth(control.getPrefWidth());
        control.setAllowedDecimalPlaces(decimalPlaces);
        control.setAllowNegative(negativeAllowed);
        control.setMaxLength(digits);
        control.valueChangedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) ->
        {
            updateValueFromControl();
        });
    }

    @Override
    public void updateValueFromControl()
    {
        floatProperty.set(control.getAsFloat());

        // User Preferences controls whether the property can be set - read back just in case our selection was overridden
        control.setValue(floatProperty.get());
    }

    @Override
    public void populateControlWithCurrentValue()
    {
        control.setValue(floatProperty.get());
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
