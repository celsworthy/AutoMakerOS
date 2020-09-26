package celtech.coreUI.controllers.panels.userpreferences;

import celtech.Lookup;
import celtech.configuration.UserPreferences;
import celtech.coreUI.controllers.panels.PreferencesInnerPanelController;
import celtech.roboxbase.BaseLookup;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 *
 * @author Ian
 */
public class LanguagePreference implements PreferencesInnerPanelController.Preference
{

    private final ComboBox<Object> control;
    private final UserPreferences userPreferences;
    private static final String SYSTEM_DEFAULT = "System Default";

    public LanguagePreference(UserPreferences userPreferences)
    {
        this.userPreferences = userPreferences;
        control = new ComboBox<>();
        control.getStyleClass().add("cmbCleanCombo");

        setupCellFactory(control);

        List<Object> localesList = new ArrayList<>();
        localesList.add(SYSTEM_DEFAULT);
        localesList.addAll(BaseLookup.getAvailableLocales());
        localesList.sort((Object o1, Object o2) ->
        {
            // Make "System Default" come at the top of the combo
            if (o1 instanceof String)
            {
                return -1;
            } else if (o2 instanceof String)
            {
                return 1;
            }
            // o1 and o2 are both Locales
            return ((Locale) o1).getDisplayName().compareTo(((Locale) o2).getDisplayName());
        });
        control.setItems(FXCollections.observableArrayList(localesList));
        control.setPrefWidth(200);
        control.setMinWidth(control.getPrefWidth());
        control.valueProperty().addListener(
                (ObservableValue<? extends Object> observable, Object oldValue, Object newValue) ->
                {
                    updateValueFromControl();
                });
    }

    @Override
    public void updateValueFromControl()
    {
        if (control.getValue() instanceof Locale)
        {
            Locale localeToUse = ((Locale) control.getValue());
            if (localeToUse.getVariant().length() > 0)
            {
                userPreferences.setLanguageTag(localeToUse.getLanguage() + "-"
                        + localeToUse.getCountry() + "-" + localeToUse.getVariant());
            } else if (localeToUse.getCountry().length() > 0)
            {
                userPreferences.setLanguageTag(localeToUse.getLanguage() + "-"
                        + localeToUse.getCountry());
            } else
            {
                userPreferences.setLanguageTag(localeToUse.getLanguage());
            }
        } else
        {
            userPreferences.setLanguageTag("");
        }
    }

    @Override
    public void populateControlWithCurrentValue()
    {
        Object preferredLocale;
        String userPrefLanguageTag = userPreferences.getLanguageTag();

        if (userPrefLanguageTag == null || userPrefLanguageTag.equals(""))
        {
            preferredLocale = SYSTEM_DEFAULT;
        } else
        {
            preferredLocale = Locale.forLanguageTag(userPrefLanguageTag);
        }
        control.setValue(preferredLocale);
    }

    @Override
    public Control getControl()
    {
        return control;
    }

    @Override
    public String getDescription()
    {
        return Lookup.i18n("preferences.language");
    }

    private void setupCellFactory(ComboBox<Object> control)
    {
        Callback<ListView<Object>, ListCell<Object>> cellFactory = new Callback<ListView<Object>, ListCell<Object>>()
        {
            @Override
            public ListCell<Object> call(ListView<Object> p)
            {
                return new ListCell<Object>()
                {
                    @Override
                    protected void updateItem(Object item, boolean empty)
                    {
                        super.updateItem(item, empty);
                        if (item != null && !empty)
                        {
                            if (item instanceof Locale)
                            {
                                setText(((Locale) item).getDisplayName(BaseLookup.getApplicationLocale()));
                            } else
                            {
                                setText((String) item);
                            }
                        }
                    }
                };
            }
        };

        control.setButtonCell(cellFactory.call(null));
        control.setCellFactory(cellFactory);
    }

    @Override
    public void disableProperty(ObservableValue<Boolean> disableProperty)
    {
        control.disableProperty().unbind();
        control.disableProperty().bind(disableProperty);
    }
}
