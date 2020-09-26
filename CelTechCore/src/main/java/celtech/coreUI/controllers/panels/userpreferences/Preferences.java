/*
 * Copyright 2015 CEL UK
 */
package celtech.coreUI.controllers.panels.userpreferences;

import celtech.configuration.UserPreferences;
import celtech.coreUI.controllers.panels.PreferencesInnerPanelController;
import celtech.coreUI.controllers.panels.PreferencesInnerPanelController.Preference;
import celtech.roboxbase.configuration.BaseConfiguration;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Preferences creates collections of the Preference class.
 *
 * @author tony
 */
public class Preferences
{

    public static List<PreferencesInnerPanelController.Preference> createPrintingPreferences(
            UserPreferences userPreferences)
    {
        List<PreferencesInnerPanelController.Preference> preferences = new ArrayList<>();

        Preference slicerTypePref = new SlicerTypePreference(userPreferences);

        Preference safetyFeaturesOnPref = new TickBoxPreference(userPreferences.
                safetyFeaturesOnProperty(), "preferences.safetyFeaturesOn");

        Preference detectFilamentLoadedPref = new TickBoxPreference(userPreferences.
                detectLoadedFilamentProperty(), "preferences.detectLoadedFilament");

        preferences.add(slicerTypePref);
        preferences.add(safetyFeaturesOnPref);
        preferences.add(detectFilamentLoadedPref);

        return preferences;
    }

    public static List<PreferencesInnerPanelController.Preference> createEnvironmentPreferences(
            UserPreferences userPreferences)
    {
        List<PreferencesInnerPanelController.Preference> preferences = new ArrayList<>();

        Preference languagePref = new LanguagePreference(userPreferences);
        Preference showTooltipsPref = new TickBoxPreference(userPreferences.showTooltipsProperty(),
                "preferences.showTooltips");
        Preference logLevelPref = new LogLevelPreference(userPreferences);
        Preference firstUsePref = new TickBoxPreference(userPreferences.firstUseProperty(),
                "preferences.firstUse");

        Preference currencySymbolPref = new CurrencySymbolPreference(userPreferences);
        Preference currencyGBPToLocalMultiplierPref = new FloatingPointPreference(userPreferences.currencyGBPToLocalMultiplierProperty(),
                2, 7, false, "preferences.currencyGBPToLocalMultiplier");

        Preference loosePartSplitPref = new TickBoxPreference(userPreferences.loosePartSplitOnLoadProperty(),
                "preferences.loosePartSplit");

        Preference autoGCodePreviewPref = new TickBoxPreference(userPreferences.autoGCodePreviewProperty(),
            "preferences.autoGCodePreview");

        Preference searchForRemoteCamerasPref = new TickBoxPreference(userPreferences.searchForRemoteCamerasProperty(),
            "preferences.searchForRemoteCameras");

        preferences.add(firstUsePref);
        preferences.add(languagePref);
        preferences.add(logLevelPref);
        preferences.add(currencySymbolPref);
        preferences.add(currencyGBPToLocalMultiplierPref);
        preferences.add(loosePartSplitPref);
        preferences.add(autoGCodePreviewPref);
        preferences.add(searchForRemoteCamerasPref);

        return preferences;
    }

    public static List<PreferencesInnerPanelController.Preference> createAdvancedPreferences(
            UserPreferences userPreferences)
    {
        List<PreferencesInnerPanelController.Preference> preferences = new ArrayList<>();

        TickBoxPreference advancedModePref = new TickBoxPreference(userPreferences.advancedModeProperty(),
                "preferences.advancedMode");

        TickBoxPreference showDiagnosticsPref = new TickBoxPreference(userPreferences.showDiagnosticsProperty(),
                "preferences.showDiagnostics");
        showDiagnosticsPref.disableProperty(advancedModePref.getSelectedProperty().not());

        TickBoxPreference showGCodePref = new TickBoxPreference(userPreferences.showGCodeProperty(),
                "preferences.showGCode");
        showGCodePref.disableProperty(advancedModePref.getSelectedProperty().not());

        TickBoxPreference showAdjustmentsPref = new TickBoxPreference(userPreferences.showAdjustmentsProperty(),
                "preferences.showAdjustments");
        showAdjustmentsPref.disableProperty(advancedModePref.getSelectedProperty().not());
        
        TickBoxPreference showSnapshotPref = new TickBoxPreference(userPreferences.showSnapshotProperty(),
                "preferences.showSnapshot");
        showSnapshotPref.disableProperty(advancedModePref.getSelectedProperty().not());
        
        advancedModePref.getSelectedProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1)
            {
                showDiagnosticsPref.getSelectedProperty().set(t1);
                showGCodePref.getSelectedProperty().set(t1);
                showAdjustmentsPref.getSelectedProperty().set(t1);
                showSnapshotPref.getSelectedProperty().set(t1);
            }
        });

        preferences.add(advancedModePref);
        preferences.add(showDiagnosticsPref);
        preferences.add(showGCodePref);
        preferences.add(showAdjustmentsPref);
        preferences.add(showSnapshotPref);
 
        return preferences;
    }

    public static List<PreferencesInnerPanelController.Preference> createCustomPrinterPreferences(
        UserPreferences userPreferences) {
        List<PreferencesInnerPanelController.Preference> preferences = new ArrayList<>();
        
        BooleanProperty customPrinterEnabled = userPreferences.customPrinterEnabledProperty();
        Preference enableCustomPrinterPref = new TickBoxPreference(customPrinterEnabled, "preferences.customPrinterEnabled");
        Preference customPrinterTypePref = new CustomPrinterTypePreference(userPreferences);
        Preference customPrinterHeadPref = new CustomPrinterHeadPreference(userPreferences);
        
        BooleanProperty windows32Bit = new SimpleBooleanProperty(BaseConfiguration.isWindows32Bit());
        enableCustomPrinterPref.disableProperty(windows32Bit);
        
        preferences.add(enableCustomPrinterPref);
        preferences.add(customPrinterTypePref);
        preferences.add(customPrinterHeadPref);
        
        return preferences;
    }
    
        public static List<PreferencesInnerPanelController.Preference> createRootPreferences(
            UserPreferences userPreferences)
    {
        List<PreferencesInnerPanelController.Preference> preferences = new ArrayList<>();

//        Preference
//        Preference slicerTypePref = new SlicerTypePreference(userPreferences);
//
//        Preference safetyFeaturesOnPref = new TickBoxPreference(userPreferences.
//                safetyFeaturesOnProperty(), "preferences.safetyFeaturesOn");
//
//        Preference detectFilamentLoadedPref = new TickBoxPreference(userPreferences.
//                detectLoadedFilamentProperty(), "preferences.detectLoadedFilament");
//
//        preferences.add(slicerTypePref);
//        preferences.add(safetyFeaturesOnPref);
//        preferences.add(detectFilamentLoadedPref);

        return preferences;
    }
}
