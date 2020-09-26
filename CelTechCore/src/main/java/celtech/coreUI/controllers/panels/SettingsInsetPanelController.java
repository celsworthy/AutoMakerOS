package celtech.coreUI.controllers.panels;

import celtech.Lookup;
import celtech.appManager.ApplicationMode;
import celtech.appManager.ApplicationStatus;
import celtech.appManager.ModelContainerProject;
import celtech.appManager.Project;
import celtech.appManager.TimelapseSettingsData;
import celtech.coreUI.DisplayManager;
import celtech.coreUI.components.Notifications.ConditionalNotificationBar;
import celtech.coreUI.components.RestrictedNumberField;
import celtech.coreUI.controllers.ProjectAwareController;
import celtech.modelcontrol.ProjectifiableThing;
import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.MaterialType;
import celtech.roboxbase.appManager.NotificationType;
import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.configuration.Filament;
import celtech.roboxbase.configuration.RoboxProfile;
import celtech.roboxbase.configuration.SlicerType;
import celtech.roboxbase.configuration.datafileaccessors.FilamentContainer;
import celtech.roboxbase.configuration.datafileaccessors.HeadContainer;
import celtech.roboxbase.configuration.datafileaccessors.RoboxProfileSettingsContainer;
import celtech.roboxbase.configuration.fileRepresentation.PrinterSettingsOverrides;
import celtech.roboxbase.configuration.fileRepresentation.SupportType;
import celtech.roboxbase.printerControl.model.Head;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.printerControl.model.PrinterListChangesAdapter;
import celtech.roboxbase.printerControl.model.PrinterListChangesListener;
import celtech.roboxbase.services.slicer.PrintQualityEnumeration;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 * FXML Controller class
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class SettingsInsetPanelController implements Initializable, ProjectAwareController, ModelContainerProject.ProjectChangesListener
{

    private final Stenographer steno = StenographerFactory.getStenographer(
            SettingsInsetPanelController.class.getName());

    private static final RoboxProfileSettingsContainer ROBOX_PROFILE_SETTINGS_CONTAINER = RoboxProfileSettingsContainer.getInstance();
    
    @FXML
    private HBox settingsInsetRoot;

    @FXML
    private Slider brimSlider;

    @FXML
    private ComboBox<RoboxProfile> customProfileChooser;

    @FXML
    private ComboBox<SupportType> supportComboBox;

    @FXML
    private HBox customProfileBox;

    @FXML
    private Label createProfileLabel;

    @FXML
    private Button editPrintProfileButton;

    @FXML
    private HBox raftHBox;

    @FXML
    private CheckBox raftButton;

    @FXML
    private HBox supportHBox;

    @FXML
    private CheckBox supportButton;

    @FXML
    private HBox supportGapHBox;

    @FXML
    private CheckBox supportGapButton;

    @FXML
    private HBox brimHBox;

    @FXML
    private HBox raftSupportBrimChooserBox;

    @FXML
    private CheckBox spiralPrintCheckbox;

    @FXML
    private HBox fillDensityHBox;
    
    @FXML
    private CheckBox overrideFillDensityCheckbox;

    @FXML
    private HBox spiralPrintHBox;

    @FXML
    private RestrictedNumberField fillDensityPercentEntry;

    @FXML
    private Slider fillDensitySlider;

    private Printer currentPrinter;
    private Project currentProject;
    private PrinterSettingsOverrides printerSettings;
    private String currentHeadType = HeadContainer.defaultHeadID;
    private ObjectProperty<PrintQualityEnumeration> printQuality;
    private boolean populatingForProject = false;

    private final BooleanProperty inPLACompatibilityMode = new SimpleBooleanProperty(false);
    private ConditionalNotificationBar PLACompatibilityModeNotificationBar;
    
    private final ChangeListener<Printer> selectedPrinterChangeListener = 
            (ObservableValue<? extends Printer> observable, Printer oldValue, Printer newValue) -> {
        whenPrinterChanged(newValue);
    };

    private final ChangeListener<ApplicationMode> applicationModeChangeListener = new ChangeListener<ApplicationMode>()
    {
        @Override
        public void changed(ObservableValue<? extends ApplicationMode> observable, ApplicationMode oldValue, ApplicationMode newValue)
        {
            if (newValue == ApplicationMode.SETTINGS)
            {
                settingsInsetRoot.setVisible(true);
                settingsInsetRoot.setMouseTransparent(false);
                if (currentProject != null
                        && currentPrinter != null)
                {
                    dealWithPrintOptimisation();
                }
            } else
            {
                settingsInsetRoot.setVisible(false);
                settingsInsetRoot.setMouseTransparent(true);
            }
        }
    };

    private final ListChangeListener<RoboxProfile> roboxProfileChangeListener = 
            (ListChangeListener.Change<? extends RoboxProfile> change) -> 
    {
        while(change.next())
        {
            populateCustomProfileChooser();
            showPleaseCreateProfile(customProfileChooser.getItems().isEmpty());
            if(change.wasAdded())
            {
                RoboxProfile savedProfile = change.getAddedSubList().get(0);
                customProfileChooser.getSelectionModel().select(savedProfile);
            }
            clearSettingsIfNoCustomProfileAvailable();
        }
    };
        
    private final ChangeListener<SlicerType> slicerTypeChangeListener = 
            (ObservableValue<? extends SlicerType> observable, SlicerType oldValue, SlicerType newValue) -> {
        populateCustomProfileChooser();
        populateSupportChooser();
        showPleaseCreateProfile(customProfileChooser.getItems().isEmpty());
        clearSettingsIfNoCustomProfileAvailable();
    };

    private final PrinterListChangesListener printerListChangesListener = new PrinterListChangesAdapter()
    {
        @Override
        public void whenHeadAdded(Printer printer)
        {
            if (printer == currentPrinter)
            {
                whenPrinterChanged(printer);
                updateSupportCombo(printer);
            }
        }

        @Override
        public void whenExtruderAdded(Printer printer, int extruderIndex)
        {
            if (printer == currentPrinter)
            {
                updateSupportCombo(printer);
            }
        }

    };

    private final MapChangeListener<Integer, Filament> filamentListener = new MapChangeListener<Integer, Filament>()
    {
        @Override
        public void onChanged(MapChangeListener.Change<? extends Integer, ? extends Filament> change)
        {
            dealWithPrintOptimisation();
        }
    };

    /**
     * Initialises the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        PLACompatibilityModeNotificationBar = new ConditionalNotificationBar("notification.printSettingsAutomaticallyAdjustedForPLA", NotificationType.NOTE);
        PLACompatibilityModeNotificationBar.setAppearanceCondition(ApplicationStatus.getInstance().modeProperty().isEqualTo(ApplicationMode.SETTINGS).and(inPLACompatibilityMode));

        try {
            supportComboBox.getItems().clear();

            populateSupportChooser();

            setupCustomProfileChooser();

            setupOverrides();
            
            if(getSlicerType() == SlicerType.Cura4)
            {
                supportComboBox.getSelectionModel().select(SupportType.AS_PROFILE);
            } else 
            {
                supportComboBox.getSelectionModel().select(SupportType.MATERIAL_2);
            }

            Lookup.getSelectedPrinterProperty().addListener(selectedPrinterChangeListener);

            ApplicationStatus.getInstance().modeProperty().addListener(applicationModeChangeListener);

            ROBOX_PROFILE_SETTINGS_CONTAINER.addProfileChangeListener(roboxProfileChangeListener);
            Lookup.getUserPreferences().getSlicerTypeProperty().addListener(slicerTypeChangeListener);

            showPleaseCreateProfile(customProfileChooser.getItems().isEmpty());

            whenPrinterChanged(Lookup.getSelectedPrinterProperty().get());

            BaseLookup.getPrinterListChangesNotifier().addListener(printerListChangesListener);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        fillDensitySlider.valueProperty().addListener((ObservableValue<? extends Number> ov, Number t, Number t1) -> {
            fillDensityPercentEntry.setValue(t1.doubleValue());
        });
        
        fillDensityPercentEntry.valueChangedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            fillDensitySlider.setValue(fillDensityPercentEntry.getAsDouble());
        });

        spiralPrintCheckbox.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            if (t1.booleanValue())
            {
                raftButton.setSelected(false);
                supportButton.setSelected(false);
                supportGapButton.setSelected(false);
                brimSlider.setValue(0);
            }
            
            raftHBox.setDisable(t1);
            supportHBox.setDisable(t1);
            supportGapHBox.setDisable(t1);
            supportComboBox.setDisable(t1);
            brimHBox.setDisable(t1);
            fillDensityHBox.setDisable(t1);
            fillDensityPercentEntry.setDisable(t1);
            fillDensitySlider.setDisable(t1);
        });

        raftSupportBrimChooserBox.disableProperty().bind(spiralPrintCheckbox.selectedProperty()
                .or(supportButton.selectedProperty().not()
                        .and(brimSlider.valueProperty().lessThanOrEqualTo(0))
                        .and(raftButton.selectedProperty().not())));
        
        raftHBox.disableProperty().bind(brimSlider.valueProperty().greaterThan(0));
        
        brimHBox.disableProperty().bind(raftButton.selectedProperty());
        
        fillDensityHBox.disableProperty().bind(overrideFillDensityCheckbox.selectedProperty().not());
        fillDensitySlider.disableProperty().bind(overrideFillDensityCheckbox.selectedProperty().not());
    }

    PropertyChangeListener customSettingsListener = (PropertyChangeEvent evt) -> {
        if (evt.getPropertyName().equals("fillDensity_normalised")) {
            fillDensitySlider.valueProperty().set(((Number) evt.getNewValue()).doubleValue()
                    * 100);
        }
    };

    private void setupCustomProfileChooser()
    {
        populateCustomProfileChooser();
        clearSettingsIfNoCustomProfileAvailable();

        customProfileChooser.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends RoboxProfile> observable, RoboxProfile oldValue, RoboxProfile newValue) -> {

            if (populatingForProject) {
                return;
            }

            if (newValue != null) {
                whenCustomProfileChanges(newValue);
            }
        });
    }

    private void populateCustomProfileChooser() {
        SlicerType slicerType = Lookup.getUserPreferences().getSlicerType();
        ObservableList<RoboxProfile> filesForHeadType = ROBOX_PROFILE_SETTINGS_CONTAINER.getCustomRoboxProfilesForSlicer(slicerType).getOrDefault(currentHeadType, FXCollections.emptyObservableList());
        customProfileChooser.setItems(filesForHeadType);
        if (currentProject != null
                && currentProject.getPrinterSettings().getPrintQuality() == PrintQualityEnumeration.CUSTOM) {
            selectCurrentCustomSettings();
        }

    }
    
    private void populateSupportChooser()
    {
        boolean populationForProjectState = populatingForProject;
        populatingForProject = true;
        
        supportComboBox.getItems().clear();
        supportComboBox.getItems().addAll(SupportType.values());
        SupportType typeToSelect = SupportType.AS_PROFILE;
        boolean allowSaveSupportType = false;
        
        
        if (HeadContainer.getHeadByID(currentHeadType).getType() == Head.HeadType.DUAL_MATERIAL_HEAD 
                && printerSettings != null)
        {
            if (getSlicerType() == SlicerType.Cura)
            {
                // For a dual material head and old Cura default to Material 2, there is no as profile
                supportComboBox.getItems().remove(SupportType.AS_PROFILE);
                typeToSelect = SupportType.MATERIAL_2;
                allowSaveSupportType = true;
            }
            
            if (printerSettings.getPrintSupportTypeOverride() != null
                    && printerSettings.getPrintSupportTypeOverride() != SupportType.AS_PROFILE)
            {
                // If we have some saved settings use the support type selected unless we aren't using Dual material. Then we just stay with the defaults
                typeToSelect = printerSettings.getPrintSupportTypeOverride();
                allowSaveSupportType = true;
            }
        } else if (HeadContainer.getHeadByID(currentHeadType).getType() == Head.HeadType.SINGLE_MATERIAL_HEAD 
                && printerSettings != null)
        {
            allowSaveSupportType = true;
        }

        populatingForProject = !allowSaveSupportType;
        // Once populated then set
        supportComboBox.getSelectionModel().select(typeToSelect);
        populatingForProject = populationForProjectState;
    }

    private void whenCustomProfileChanges(RoboxProfile newValue)
    {
        //if (getCustomSettings().isPresent()) {
        //    getCustomSettings().removePropertyChangeListener(customSettingsListener);
        //}
        printerSettings.setSettingsName(newValue.getName());
        //if (getCustomSettings().isPresent()) {
        //    getCustomSettings().addPropertyChangeListener(customSettingsListener);
        //}
        printQualityWidgetsUpdate(printerSettings != null ? printerSettings.getPrintQuality() : PrintQualityEnumeration.DRAFT);
    }

    private void setupOverrides()
    {
        supportComboBox.valueProperty().addListener(
                (ObservableValue<? extends SupportType> ov, SupportType lastSupportValue, SupportType newSupportValue) ->
        {
            if (populatingForProject)
            {
                return;
            }

            dealWithPrintOptimisation();

            if (printerSettings != null
                    && lastSupportValue != newSupportValue)
            {
                printerSettings.setPrintSupportTypeOverride(newSupportValue);
            }
        });

        supportButton.selectedProperty().addListener(
                (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean selected) ->
        {
            if (populatingForProject)
            {
                return;
            }

            updateSupportCombo(currentPrinter);
            dealWithPrintOptimisation();

            printerSettings.setPrintSupportOverride(selected);
        });

        supportGapButton.selectedProperty().addListener(
                (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean selected) ->
        {
            if (populatingForProject)
            {
                return;
            }

            printerSettings.setPrintSupportGapEnabledOverride(selected);
        });

        raftButton.selectedProperty().addListener(
                (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean selected) -> {
            if (populatingForProject) {
                return;
            }

            printerSettings.setRaftOverride(selected);
            if(selected) {
                brimSlider.setValue(0);
            }
        });

        fillDensitySlider.valueProperty().addListener((ObservableValue<? extends Number> observable, Number was, Number now) -> {
                    if (!fillDensitySlider.isValueChanging()
                            || now.doubleValue() >= fillDensitySlider.getMax()
                            || now.doubleValue() <= fillDensitySlider.getMin())
                    {
                        if (getSlicerType() == SlicerType.Cura4)
                        {
                            printerSettings.setFillDensityOverride(now.floatValue());
                        } else
                        {
                            printerSettings.setFillDensityOverride(now.floatValue() / 100.0f);
                        }
                    }
                });

        brimSlider.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (!brimSlider.isValueChanging()
                    || newValue.doubleValue() >= brimSlider.getMax()
                    || newValue.doubleValue() <= brimSlider.getMin()) {
                printerSettings.setBrimOverride(newValue.intValue());
                if(newValue.intValue() > 0) {
                    raftButton.setSelected(false);
                }
            }
        });

        spiralPrintCheckbox.selectedProperty().addListener(
                (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean selected) ->
        {
            if (populatingForProject)
            {
                return;
            }

            printerSettings.setSpiralPrintOverride(selected);
        });
        
        overrideFillDensityCheckbox.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            if (populatingForProject)
            {
                return;
            }
            
            printerSettings.setFillDensityChangedByUser(t1);
            printQualityWidgetsUpdate(printQuality.get());
        });
    }

    @FXML
    void editPrintProfile(ActionEvent event)
    {
        DisplayManager.getInstance().showAndSelectPrintProfile(customProfileChooser.getValue());
    }

    /**
     * If no profiles are left available then clear the settingsName.
     */
    private void clearSettingsIfNoCustomProfileAvailable() {
        if (customProfileChooser.getItems().isEmpty()) {
            if (printerSettings != null) {
                printerSettings.setSettingsName("");
            }
        }
    }

    private void whenPrinterChanged(Printer printer)
    {
        if (currentPrinter != null)
        {
            currentPrinter.effectiveFilamentsProperty().removeListener(filamentListener);
        }

        currentPrinter = printer;
        if (printer != null)
        {
            updateSupportCombo(printer);
            String headTypeCode;
            if (printer.headProperty().get() != null)
            {
                headTypeCode = printer.headProperty().get().typeCodeProperty().get();
            } else
            {
                headTypeCode = HeadContainer.defaultHeadID;
            }
            if (!headTypeCode.equals(currentHeadType))
            {
                if (currentProject != null)
                {
                    currentProject.invalidate();
                }
            }
            currentHeadType = headTypeCode;

            populateCustomProfileChooser();
            populateSupportChooser();
            showPleaseCreateProfile(customProfileChooser.getItems().isEmpty());
            updateSupportCombo(currentPrinter);

            currentPrinter.effectiveFilamentsProperty().addListener(filamentListener);
        }
    }

    private int getNumExtruders(Printer printer)
    {
        int numExtruders = 1;
        if (printer != null && printer.extrudersProperty().get(1).isFittedProperty().get())
        {
            numExtruders = 2;
        }
        return numExtruders;
    }

    private void updateSupportCombo(Printer printer)
    {
        if (printer != null
                && printer.headProperty().get() != null)
        {
            populatingForProject = true;

            if (getNumExtruders(printer) > 1
                    && (printer.headProperty().get() != null
                    && printer.headProperty().get().headTypeProperty().get() == Head.HeadType.DUAL_MATERIAL_HEAD))
            {
                raftSupportBrimChooserBox.setVisible(true);
                raftSupportBrimChooserBox.setMinHeight(-1);
                raftSupportBrimChooserBox.setPrefHeight(-1);
            } else
            {
                raftSupportBrimChooserBox.setVisible(false);
                raftSupportBrimChooserBox.setMinHeight(0);
                raftSupportBrimChooserBox.setPrefHeight(0);
            }

            populatingForProject = false;
        }
    }

    @Override
    public void setProject(Project project)
    {
        if (currentProject != null)
        {
            currentProject.removeProjectChangesListener(this);
        }

        if (project != null)
        {
            project.addProjectChangesListener(this);
            whenProjectChanged(project);
        }
    }

    private void whenProjectChanged(Project project)
    {
        populatingForProject = true;

        currentProject = project;
        printerSettings = project.getPrinterSettings();
        printQuality = printerSettings.printQualityProperty();

        int saveBrim = printerSettings.getBrimOverride();
        float saveFillDensity = printerSettings.getFillDensityOverride();
        boolean autoSupport = printerSettings.getPrintSupportOverride();
        SupportType saveSupports = printerSettings.getPrintSupportTypeOverride();
        boolean savePrintRaft = printerSettings.getRaftOverride();
        boolean saveSpiralPrint = printerSettings.getSpiralPrintOverride();
        boolean saveSupportGapEnabled = printerSettings.getPrintSupportGapEnabledOverride();
        boolean saveOverrideFillDensity = printerSettings.isFillDensityChangedByUser();

        // printer settings name is cleared by combo population so must be saved
        String savePrinterSettingsName = project.getPrinterSettings().getSettingsName();

        printQuality.addListener(
                (ObservableValue<? extends PrintQualityEnumeration> observable, PrintQualityEnumeration oldValue, PrintQualityEnumeration newValue) -> {
            printQualityWidgetsUpdate(newValue);
        });

        brimSlider.setValue(saveBrim);
        
        if(getSlicerType() == SlicerType.Cura4)
        {
            fillDensitySlider.setValue(saveFillDensity);
        } else
        {
            fillDensitySlider.setValue(saveFillDensity * 100);
        }

        raftButton.setSelected(savePrintRaft);

        //supportComboBox.setValue(saveSupports);
        populateSupportChooser();

        printerSettings.getPrintSupportTypeOverrideProperty()
                .addListener((ObservableValue<? extends SupportType> observable, SupportType oldValue, SupportType newValue) -> {
            populatingForProject = true;
            supportComboBox.getSelectionModel().select(newValue);
            updateSupportCombo(currentPrinter);
            populatingForProject = false;
        });

        supportButton.setSelected(autoSupport);

        if (savePrinterSettingsName.length() > 0)
        {
            List<RoboxProfile> profiles = ROBOX_PROFILE_SETTINGS_CONTAINER.getRoboxProfilesForSlicer(getSlicerType()).get(currentHeadType);
            Optional<RoboxProfile> chosenProfile = profiles.stream()
                    .filter(profile -> profile.getName().equals(savePrinterSettingsName))
                    .findFirst();
            if(chosenProfile.isPresent()) {
                customProfileChooser.getSelectionModel().select(chosenProfile.get());
            } else {
                customProfileChooser.getSelectionModel().clearSelection();
            }
        }

        spiralPrintCheckbox.setSelected(saveSpiralPrint);
        overrideFillDensityCheckbox.setSelected(saveOverrideFillDensity);
        supportGapButton.setSelected(saveSupportGapEnabled);

        dealWithPrintOptimisation();

        populatingForProject = false;
        
        printQualityWidgetsUpdate(printQuality.get());
    }

    private void dealWithPrintOptimisation()
    {
        dealWithSpiralness();
        dealWithSupportGap();
        dealWithIncompatibleMaterials();
    }

    private void dealWithSpiralness()
    {
        if (currentProject instanceof ModelContainerProject)
        {
            spiralPrintHBox.disableProperty().set(currentProject.getAllModels().size() != 1
                    || !((ModelContainerProject) currentProject).allModelsOnSameExtruder(currentPrinter));

            spiralPrintCheckbox.setSelected(spiralPrintCheckbox.selectedProperty().get()
                    && currentProject.getAllModels().size() == 1
                    && ((ModelContainerProject) currentProject).allModelsOnSameExtruder(currentPrinter));
        }
    }

    private void dealWithSupportGap()
    {
        if (currentProject instanceof ModelContainerProject)
        {
            supportGapHBox.disableProperty().set(!supportButton.isSelected());

            boolean supportGapEnabledDriver = currentPrinter != null
                    && supportButton.isSelected()
                    && !(currentPrinter.effectiveFilamentsProperty().get(0).getMaterial() != currentPrinter.effectiveFilamentsProperty().get(1).getMaterial()
                    && !((ModelContainerProject) currentProject).getPrintingExtruders(currentPrinter).get(supportComboBox.getSelectionModel().getSelectedItem().getExtruderNumber()));

            if(getSlicerType() == SlicerType.Cura) {
                supportGapButton.setSelected(supportGapEnabledDriver);
            }
        }
    }

    private void dealWithIncompatibleMaterials()
    {
        boolean triggerPLAIncompatibility = currentPrinter != null
                //Neither material is UNKNOWN
                && currentPrinter.effectiveFilamentsProperty().get(0) != FilamentContainer.UNKNOWN_FILAMENT
                && currentPrinter.effectiveFilamentsProperty().get(1) != FilamentContainer.UNKNOWN_FILAMENT
                //Materials are not the same
                && currentPrinter.effectiveFilamentsProperty().get(0).getMaterial() != currentPrinter.effectiveFilamentsProperty().get(1).getMaterial()
                //One of the materials is PLA
                && (currentPrinter.effectiveFilamentsProperty().get(0).getMaterial() == MaterialType.PLA || currentPrinter.effectiveFilamentsProperty().get(1).getMaterial() == MaterialType.PLA)
                //Both materials are required for the print
                && currentProject instanceof ModelContainerProject
                && ((ModelContainerProject) currentProject).getPrintingExtruders(currentPrinter).get(0)
                && ((ModelContainerProject) currentProject).getPrintingExtruders(currentPrinter).get(1);

        if (triggerPLAIncompatibility)
        {
            SupportType requiredSupportType = (currentPrinter.effectiveFilamentsProperty().get(0).getMaterial() == MaterialType.PLA) ? SupportType.MATERIAL_1 : SupportType.MATERIAL_2;
            raftButton.setSelected(true);
            supportComboBox.getSelectionModel().select(requiredSupportType);
        }

        if (triggerPLAIncompatibility != inPLACompatibilityMode.get())
        {
            inPLACompatibilityMode.set(triggerPLAIncompatibility);
        }
    }

    private void selectCurrentCustomSettings() 
    {
        if (currentPrinter != null) 
        {
            Head currentHead = currentPrinter.headProperty().get();
            String headType = HeadContainer.defaultHeadID;
            if (currentHead != null) 
            {
                headType = currentHead.typeCodeProperty().get();
            }
            RoboxProfile customSettings = printerSettings.getSettings(headType, getSlicerType());
            customProfileChooser.getSelectionModel().select(customSettings);
        }
    }

    private void enableCustomChooser(boolean enable)
    {
        customProfileBox.setDisable(!enable);
    }

    private void showPleaseCreateProfile(boolean show) {
        customProfileChooser.setVisible(!show);
        createProfileLabel.setVisible(show);
    }

    private void printQualityWidgetsUpdate(PrintQualityEnumeration quality)
    {
        Optional<RoboxProfile> settings = Optional.empty();
        
        switch (quality) {
            case DRAFT:
                settings = ROBOX_PROFILE_SETTINGS_CONTAINER
                        .getRoboxProfileWithName(BaseConfiguration.draftSettingsProfileName, getSlicerType(), currentHeadType);
                enableCustomChooser(false);
                break;
            case NORMAL:
                settings = ROBOX_PROFILE_SETTINGS_CONTAINER
                        .getRoboxProfileWithName(BaseConfiguration.normalSettingsProfileName, getSlicerType(), currentHeadType);
                enableCustomChooser(false);
                break;
            case FINE:
               settings = ROBOX_PROFILE_SETTINGS_CONTAINER
                        .getRoboxProfileWithName(BaseConfiguration.fineSettingsProfileName, getSlicerType(), currentHeadType);
                enableCustomChooser(false);
                break;
            case CUSTOM:
                settings = getCustomSettings();
                enableCustomChooser(true);
                break;
            default:
                break;
        }

        if (currentProject != null) {
            if (settings.isPresent() && !printerSettings.isFillDensityChangedByUser()) {
                if(getSlicerType() == SlicerType.Cura4)
                {
                    int fillDensity = settings.get().getSpecificIntSetting("fillDensity_normalised");
                    printerSettings.setFillDensityOverride(fillDensity);
                    fillDensitySlider.setValue(fillDensity);
                } else
                {
                    float fillDensity = settings.get().getSpecificFloatSetting("fillDensity_normalised");
                    printerSettings.setFillDensityOverride(fillDensity);
                    fillDensitySlider.setValue(fillDensity * 100.0);
                }
            }
        }
    }

    private Optional<RoboxProfile> getCustomSettings()
    {
        String customSettingsName = printerSettings.getSettingsName();
        if (customSettingsName.equals("")) 
        {
            return Optional.empty();
        } else 
        {
            return ROBOX_PROFILE_SETTINGS_CONTAINER.getRoboxProfileWithName(customSettingsName, getSlicerType(), currentHeadType);
        }
    }

    @Override
    public void whenModelAdded(ProjectifiableThing modelContainer)
    {
        updateSupportCombo(currentPrinter);
    }

    @Override
    public void whenModelsRemoved(Set<ProjectifiableThing> modelContainers)
    {
        updateSupportCombo(currentPrinter);
    }

    @Override
    public void whenAutoLaidOut()
    {
    }

    @Override
    public void whenModelsTransformed(Set<ProjectifiableThing> modelContainers)
    {
    }

    @Override
    public void whenModelChanged(ProjectifiableThing modelContainer, String propertyName)
    {
        updateSupportCombo(currentPrinter);
    }

    @Override
    public void whenPrinterSettingsChanged(PrinterSettingsOverrides printerSettings)
    {
    }

    @Override
    public void whenTimelapseSettingsChanged(TimelapseSettingsData timelapseSettings)
    {
    }

    @Override
    public void shutdownController()
    {
        if (currentPrinter != null)
        {
            currentPrinter.effectiveFilamentsProperty().removeListener(filamentListener);
        }

        if (currentProject != null)
        {
            currentProject.removeProjectChangesListener(this);
        }
        currentProject = null;

        Lookup.getSelectedPrinterProperty().removeListener(selectedPrinterChangeListener);

        ApplicationStatus.getInstance().modeProperty().removeListener(applicationModeChangeListener);

        ROBOX_PROFILE_SETTINGS_CONTAINER.removeProfileChangeListener(roboxProfileChangeListener);

        BaseLookup.getPrinterListChangesNotifier().removeListener(printerListChangesListener);

        PLACompatibilityModeNotificationBar.destroyBar();
    }
    
    private SlicerType getSlicerType() {
        return Lookup.getUserPreferences().getSlicerType();
    }

}
