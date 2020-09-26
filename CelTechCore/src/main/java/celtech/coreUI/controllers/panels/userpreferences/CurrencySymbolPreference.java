package celtech.coreUI.controllers.panels.userpreferences;

import celtech.Lookup;
import celtech.configuration.UserPreferences;
import celtech.configuration.units.CurrencySymbol;
import celtech.coreUI.controllers.panels.PreferencesInnerPanelController;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 *
 * @author Ian
 */
public class CurrencySymbolPreference implements PreferencesInnerPanelController.Preference
{

    private final UserPreferences userPreferences;
    private final ComboBox<CurrencySymbol> control;

    public CurrencySymbolPreference(UserPreferences userPreferences)
    {
        this.userPreferences = userPreferences;

        control = new ComboBox<>();
        control.getStyleClass().add("cmbCleanCombo");
        control.setPrefWidth(200);
        control.setMinWidth(control.getPrefWidth());

        Callback<ListView<CurrencySymbol>, ListCell<CurrencySymbol>> currencySymbolCellFactory
                = (ListView<CurrencySymbol> list) -> new CurrencySymbolListCell();
        control.setCellFactory(currencySymbolCellFactory);
        control.setButtonCell(currencySymbolCellFactory.call(null));

        control.getItems().setAll(CurrencySymbol.values());
        control.getSelectionModel().selectedItemProperty()
                .addListener((ObservableValue<? extends CurrencySymbol> observable, CurrencySymbol oldValue, CurrencySymbol newValue) ->
                        {
                            updateValueFromControl();
                });
    }

    @Override
    public void updateValueFromControl()
    {
        userPreferences.setCurrencySymbol(control.getSelectionModel().selectedItemProperty().get());
    }

    @Override
    public void populateControlWithCurrentValue()
    {
        control.getSelectionModel().select(userPreferences.getCurrencySymbol());
    }

    @Override
    public Control getControl()
    {
        return control;
    }

    @Override
    public String getDescription()
    {
        return Lookup.i18n("preferences.currencySymbol");
    }

    @Override
    public void disableProperty(ObservableValue<Boolean> disableProperty)
    {
        control.disableProperty().unbind();
        control.disableProperty().bind(disableProperty);
    }
}
