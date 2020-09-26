package celtech.coreUI.controllers.panels.userpreferences;

import celtech.Lookup;
import celtech.coreUI.controllers.panels.PreferencesInnerPanelController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;

/**
 *
 * @author Ian
 */
public class TickBoxPreference implements PreferencesInnerPanelController.Preference
{

    private final CheckBox control;
    private final BooleanProperty booleanProperty;
    private final String caption;

    public TickBoxPreference(BooleanProperty booleanProperty, String caption)
    {
        this.booleanProperty = booleanProperty;
        this.caption = caption;

        control = new CheckBox();
        control.setPrefWidth(150);
        control.setMinWidth(control.getPrefWidth());
        control.selectedProperty().addListener(
                (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) ->
                {
                    updateValueFromControl();
                });
        booleanProperty.addListener(
                (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) ->
                {
                    control.setSelected(newValue);
                });
    }

    @Override
    public void updateValueFromControl()
    {
        booleanProperty.set(control.isSelected());

        // User Preferences controls whether the property can be set - read back just in case our selection was overridden
        control.selectedProperty().set(booleanProperty.get());
    }

    @Override
    public void populateControlWithCurrentValue()
    {
        control.setSelected(booleanProperty.get());
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

    public BooleanProperty getSelectedProperty()
    {
        return control.selectedProperty();
    }
}
