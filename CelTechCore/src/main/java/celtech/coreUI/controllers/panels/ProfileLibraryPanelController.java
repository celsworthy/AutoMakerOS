package celtech.coreUI.controllers.panels;

import celtech.Lookup;
import celtech.appManager.Project;
import celtech.coreUI.DisplayManager;
import celtech.coreUI.components.RestrictedComboBox;
import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.configuration.RoboxProfile;
import celtech.roboxbase.configuration.SlicerType;
import celtech.roboxbase.configuration.datafileaccessors.HeadContainer;
import celtech.roboxbase.configuration.datafileaccessors.PrintProfileSettingsContainer;
import celtech.roboxbase.configuration.datafileaccessors.RoboxProfileSettingsContainer;
import celtech.roboxbase.configuration.datafileaccessors.SlicerMappingsContainer;
import celtech.roboxbase.configuration.fileRepresentation.HeadFile;
import celtech.roboxbase.configuration.profilesettings.PrintProfileSetting;
import celtech.roboxbase.configuration.profilesettings.PrintProfileSettings;
import celtech.roboxbase.printerControl.model.Head;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.services.slicer.PrintQualityEnumeration;
import celtech.utils.settingsgeneration.ProfileDetailsGenerator;
import celtech.utils.settingsgeneration.ProfileDetailsGenerator.ProfileDetailsGenerationException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 * FXML Controller class
 *
 * @author Ian Hudson and George Salter
 */
public class ProfileLibraryPanelController implements Initializable, MenuInnerPanel
{
    private static final Stenographer STENO = StenographerFactory.getStenographer(
            ProfileLibraryPanelController.class.getName());

    private static final PrintProfileSettingsContainer PRINT_PROFILE_SETTINGS_CONTAINER = PrintProfileSettingsContainer.getInstance();
    private static final RoboxProfileSettingsContainer ROBOX_PROFILE_SETTINGS_CONTAINER = RoboxProfileSettingsContainer.getInstance();
    private static final HashMap<String, Integer> standardProfileOrder = new HashMap<>();
    static {
        standardProfileOrder.put("DRAFT", 0);
        standardProfileOrder.put("NORMAL", 1);
        standardProfileOrder.put("FINE", 2);
        standardProfileOrder.put("CUSTOM", 3);
    }
    
    private final PseudoClass ERROR = PseudoClass.getPseudoClass("error");


    enum State {
        /**
         * Editing a new profile that has not yet been saved
         */
        NEW,
        /**
         * Editing a custom profile
         */
        CUSTOM,
        /**
         * Viewing a standard profile
         */
        ROBOX
    };

    private final ObjectProperty<ProfileLibraryPanelController.State> state = new SimpleObjectProperty<>();
    private final BooleanProperty isDirty = new SimpleBooleanProperty(false);
    private final BooleanProperty isEditable = new SimpleBooleanProperty(false);
    private final BooleanProperty canSave = new SimpleBooleanProperty(false);
    private final BooleanProperty canSaveAs = new SimpleBooleanProperty(false);
    private final BooleanProperty canDelete = new SimpleBooleanProperty(false);
    private final BooleanProperty isNameValid = new SimpleBooleanProperty(false);
    private final StringProperty currentHeadType = new SimpleStringProperty();
    private final IntegerProperty numNozzleHeaters = new SimpleIntegerProperty();
    private final IntegerProperty numNozzles = new SimpleIntegerProperty();
    private final BooleanProperty hasValves = new SimpleBooleanProperty(false);

    private ProfileDetailsGenerator profileDetailsFxmlGenerator;
    
    private String currentProfileName;
    
    @FXML
    private Label slicerInUseLabel;
    
    @FXML
    private VBox container;

    @FXML
    private ComboBox<String> cmbHeadType;

    @FXML
    private RestrictedComboBox<String> cmbPrintProfile;

    Map<String, RoboxProfile> roboxProfilesMap;
    
    private final ObservableList<String> nozzleOptions = FXCollections.observableArrayList(
            "0.3mm", "0.4mm", "0.6mm", "0.8mm");
    
    private final ChangeListener<String> dirtyStringListener = (ObservableValue<? extends String> ov, String oldString, String newString) -> {
        if (!newString.equals(currentProfileName))
        {
            isDirty.set(true);
        } else
        {
            isDirty.set(false);
        }
    };
    
    private Printer currentPrinter = null;

    private final ChangeListener<Head> headChangeListener = (ObservableValue<? extends Head> ov, Head t, Head t1) -> {
        headHasChanged(t1);
        regenerateSettings = true;
    };
    
    private boolean regenerateSettings;
    
    private final ChangeListener<SlicerType> slicerTypeChangeListener = 
            (ObservableValue<? extends SlicerType> observable, SlicerType oldValue, SlicerType newValue) -> {
                regenerateSettings = true;
    };

    public ProfileLibraryPanelController() {}

    /**
     * Initialises the controller class.
     * 
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {   
        Lookup.getSelectedPrinterProperty().addListener((ObservableValue<? extends Printer> ov, Printer oldValue, Printer newValue) -> {
            bindToPrinter(newValue);
        });

        if (Lookup.getSelectedPrinterProperty().get() != null) {
            bindToPrinter(Lookup.getSelectedPrinterProperty().get());
        }

        canSave.bind(isNameValid
                .and(isDirty
                        .and(state.isEqualTo(State.NEW)
                                .or(state.isEqualTo(State.CUSTOM)))));
        canSaveAs.bind(state.isNotEqualTo(State.NEW));
        canDelete.bind(state.isNotEqualTo(State.NEW).and(state.isNotEqualTo(State.ROBOX)));
        isEditable.bind(state.isNotEqualTo(State.ROBOX));

        PrintProfileSettings printProfileSettings = PRINT_PROFILE_SETTINGS_CONTAINER
                .getPrintProfileSettingsForSlicer(getSlicerType());
        profileDetailsFxmlGenerator = new ProfileDetailsGenerator(printProfileSettings, isDirty);
        
        setupSlicerInUseLabel();
        setupProfileNameChangeListeners();
        setupHeadType();
        setupPrintProfileCombo();
        selectDefaultPrintProfile();

        Lookup.getUserPreferences().getSlicerTypeProperty().addListener(slicerTypeChangeListener);
        
        DisplayManager.getInstance().libraryModeEnteredProperty().addListener((observable, oldValue, enteredLibraryMode) -> {
            if (enteredLibraryMode)
            {
                regenerateSettings(getSlicerType(), true);
                repopulateCmbPrintProfile();
                selectDefaultPrintProfile();
                setupSlicerInUseLabel();
            }
        });
        
        container.setOnKeyPressed(event -> {
            KeyCode keyCode = event.getCode();
            if(keyCode == KeyCode.F5) 
            {
                STENO.debug("F5 pressed, attempting refresh of print profile settings");
                PrintProfileSettingsContainer.loadPrintProfileSettingsFile();
                SlicerMappingsContainer.getInstance().loadSlicerMappingsFile();
                BaseLookup.setSlicerMappings(SlicerMappingsContainer.getInstance().getSlicerMappings());
                RoboxProfile printProfile = roboxProfilesMap.get(currentProfileName);
                updateSettingsFromProfile(printProfile, true);
            }
        });
    }
    
    private void regenerateSettings(SlicerType slicerType, boolean recreateTabs) 
    {
        STENO.debug("========== Begin regenerating settings ==========");
        profileDetailsFxmlGenerator.setPrintProfilesettings(PRINT_PROFILE_SETTINGS_CONTAINER.getPrintProfileSettingsForSlicer(slicerType));
        profileDetailsFxmlGenerator.setHeadType(currentHeadType.get());
        profileDetailsFxmlGenerator.setNozzleOptions(nozzleOptions);
        try
        {
            profileDetailsFxmlGenerator.generateSettingsForProfileDetails(container, recreateTabs);
            profileDetailsFxmlGenerator.bindTabsToEditableProperty(isEditable);
        } catch (ProfileDetailsGenerationException ex) 
        {
            STENO.exception("Settings not generated.", ex);
        }
        FXMLUtilities.addColonsToLabels(container);
        STENO.debug("========== Finished regenerating settings ==========");
    }

    private void headHasChanged(Head head) {
        if (head != null) {
            if (isDirty.get()) {
                whenSavePressed();
            }
            cmbHeadType.getSelectionModel().select(head.typeCodeProperty().get());
        }
    }

    private void bindToPrinter(Printer printer) {
        if (currentPrinter != null) {
            currentPrinter.headProperty().removeListener(headChangeListener);
        }

        if (printer != null) {
            printer.headProperty().addListener(headChangeListener);

            if (printer.headProperty().get() != null) {
                headHasChanged(printer.headProperty().get());
            }
        }

        currentPrinter = printer;
    }

    private void setupHeadType() {
        HeadContainer.getCompleteHeadList().forEach((head) -> {
            cmbHeadType.getItems().add(head.getTypeCode());
        });

        cmbHeadType.valueProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            HeadFile headDetails = HeadContainer.getHeadByID(newValue);
            currentHeadType.set(newValue);
            numNozzleHeaters.set(headDetails.getNozzleHeaters().size());
            numNozzles.set(headDetails.getNozzles().size());

            List<Float> nozzleSizes = headDetails.getNozzles()
                                                 .stream()
                                                 .map(n -> n.getDiameter())
                                                 .collect(Collectors.toList());

            List<String> nozzleSizeStrings = nozzleSizes.stream()
                                                        .map(n -> n.toString() + "mm")
                                                        .collect(Collectors.toList());
            
            nozzleOptions.setAll(nozzleSizeStrings);
            
            repopulateCmbPrintProfile();
            selectDefaultPrintProfile();
        });

        cmbHeadType.setValue(HeadContainer.defaultHeadID);
    }

    private void setupPrintProfileCombo() 
    {
        repopulateCmbPrintProfile();

        cmbPrintProfile.valueProperty().addListener(
                (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    selectPrintProfile();
                }
        );

        selectPrintProfile();
    }

    private void selectDefaultPrintProfile() 
    {
        if (cmbPrintProfile.getItems().size() > 0)
        {
            String headType = cmbHeadType.getValue();

            Project selectedProject = Lookup.getSelectedProjectProperty().get();
            Optional<RoboxProfile> profileOption = Optional.empty();
            if (selectedProject != null)
            {
                PrintQualityEnumeration printQuality = selectedProject.getPrinterSettings().getPrintQuality();
                profileOption = selectedProject.getPrinterSettings().getBaseProfile(headType, getSlicerType(), printQuality);
            }

            if (profileOption.isPresent())
            {
                cmbPrintProfile.setValue(profileOption.get().getName());
            } else
            {
                cmbPrintProfile.setValue(cmbPrintProfile.getItems().get(0));
            }
            selectPrintProfile();
        }
    }
    
    public void setAndSelectPrintProfile(RoboxProfile printProfile) {
        if (printProfile != null) {
            cmbHeadType.setValue(printProfile.getHeadType());
            cmbPrintProfile.setValue(printProfile.getName());
        }
    }

    private void selectPrintProfile()
    {
        String selectedPrintProfileName = cmbPrintProfile.getValue();
        
        if(selectedPrintProfileName == null || selectedPrintProfileName.isEmpty())
        {
            return;
        }
        
        RoboxProfile printProfile = roboxProfilesMap.get(selectedPrintProfileName);
        
        if(printProfile == null)
        {
            return;
        }
        
        currentProfileName = selectedPrintProfileName;
        
        updateSettingsFromProfile(printProfile, false);
        if (printProfile.isStandardProfile()) 
        {
            state.set(State.ROBOX);
            cmbPrintProfile.setEditable(false);
        } else
        {
            state.set(State.CUSTOM);
            cmbPrintProfile.setEditable(true);
        }
        
        cmbPrintProfile.getItems().removeIf(name -> !roboxProfilesMap.containsKey(name));
        cmbPrintProfile.setValue(selectedPrintProfileName);
        
        isDirty.set(false);
    }

    private void repopulateCmbPrintProfile() {
        Map<String, List<RoboxProfile>> roboxProfiles = ROBOX_PROFILE_SETTINGS_CONTAINER.getRoboxProfilesForSlicer(getSlicerType());
        String headType = cmbHeadType.getValue();
        List<RoboxProfile> filesForHeadType = roboxProfiles.getOrDefault(headType, new ArrayList<>());
        roboxProfilesMap = filesForHeadType.stream()
                .collect(Collectors.toMap(RoboxProfile::getName, Function.identity()));
        
        // Order profiles alphabetically, but with standard profiles before custom profiles.
        Collections.sort(filesForHeadType, (p1, p2) -> {
            if (p1.isStandardProfile()) {
                if (p2.isStandardProfile()) {
                    // Standard profiles are ordered according to standardProfileOrder map.
                    int p1Order = standardProfileOrder.getOrDefault(p1.getName().toUpperCase(), -1);
                    int p2Order = standardProfileOrder.getOrDefault(p2.getName().toUpperCase(), -1);
                    if (p1Order == p2Order)
                        return 0;
                    else if (p1Order < p2Order)
                        return -1;
                    else
                        return 1;
                }
                else    
                    return -1; // Standard profile is before custom profile.
            }
            else {
                if (p2.isStandardProfile())
                    return 1; // custom profile is after standard profile.
            }
            return p1.getName().compareToIgnoreCase(p2.getName()); // Custom profiles are ordered alphabetically.
        });

        List<String> nameList  = filesForHeadType.stream()
                                                 .map(RoboxProfile::getName)
                                                 .collect(Collectors.toList());
        cmbPrintProfile.setItems(FXCollections.observableArrayList(nameList));
    }
    
    private void setupSlicerInUseLabel() {
        String selectedSlicerStr = Lookup.i18n("profileLibrary.slicerInUse");
        selectedSlicerStr = selectedSlicerStr + " " + getSlicerType().name();
        slicerInUseLabel.setText(selectedSlicerStr);
    }

    private void setupProfileNameChangeListeners() 
    {
        cmbPrintProfile.getEditor().textProperty().addListener(
                (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (!validateProfileName()) 
                    {
                        isNameValid.set(false);
                        cmbPrintProfile.pseudoClassStateChanged(ERROR, true);
                    } else 
                    {
                        isNameValid.set(true);
                        cmbPrintProfile.pseudoClassStateChanged(ERROR, false);
                    }
                });

        cmbPrintProfile.getEditor().textProperty().addListener(dirtyStringListener);
    }

    /**
     * Method used when the settings are being reset for a new profile.
     * The current settings must be replaced with a copy of the default settings
     * from the selected profile.
     * 
     * @param roboxProfile the profile that has been selected
     */
    private void updateSettingsFromProfile(RoboxProfile roboxProfile, boolean recreateTabs)
    {
        PrintProfileSettings printProfileSettings = PRINT_PROFILE_SETTINGS_CONTAINER
                .getPrintProfileSettingsForSlicer(getSlicerType());
        PrintProfileSettings defaultPrintProfileSettings = PRINT_PROFILE_SETTINGS_CONTAINER
                .getDefaultPrintProfileSettingsForSlicer(getSlicerType());
        PrintProfileSettings defaultSettingsCopy = new PrintProfileSettings(defaultPrintProfileSettings);
        printProfileSettings.setHeaderSettings(defaultSettingsCopy.getHeaderSettings());
        printProfileSettings.setTabs(defaultSettingsCopy.getTabs());
        printProfileSettings.setHiddenSettings(defaultSettingsCopy.getHiddenSettings());
        
        overwriteSettingsFromProfile(printProfileSettings, roboxProfile);
        regenerateSettings(getSlicerType(), recreateTabs);
    }
    
    private void overwriteSettingsFromProfile(PrintProfileSettings settingsToOverwrite, RoboxProfile roboxProfile) {
        Map<String, PrintProfileSetting> printProfileSettingsMap = new HashMap<>();
        
        settingsToOverwrite.getAllSettings().forEach(setting -> printProfileSettingsMap.put(setting.getId(), setting));
        
        Map<String, String> roboxProfileSettings = roboxProfile.getSettings();
        roboxProfileSettings.entrySet().forEach((roboxSetting) -> {
            if(printProfileSettingsMap.containsKey(roboxSetting.getKey())) {
                PrintProfileSetting profileSetting = printProfileSettingsMap.get(roboxSetting.getKey());
                profileSetting.setValue(roboxSetting.getValue());
            }
        });
    }

    private boolean validateProfileName() 
    {
        boolean valid = true;
        String profileNameText = cmbPrintProfile.getEditor().getText();

        if (profileNameText.equals("")) 
        {
            valid = false;
        } else 
        {
            Map<String, List<RoboxProfile>> existingProfileMapForSlicer = 
                    ROBOX_PROFILE_SETTINGS_CONTAINER.getRoboxProfilesForSlicer(getSlicerType());
            List<RoboxProfile> profilesForHead = existingProfileMapForSlicer.get(currentHeadType.get());
            for (RoboxProfile profile : profilesForHead) 
            {
                if (!profile.getName().equalsIgnoreCase(currentProfileName)
                        && profile.getName().equalsIgnoreCase(profileNameText)) 
                {
                    valid = false;
                    break;
                }
            }
        }
        return valid;
    }

    void whenSavePressed()
    {
        assert (state.get() != ProfileLibraryPanelController.State.ROBOX);
        
        if (validateProfileName())
        {
            PrintProfileSettings defaultSettings = PRINT_PROFILE_SETTINGS_CONTAINER.getDefaultPrintProfileSettingsForSlicer(getSlicerType());
            PrintProfileSettings settingsForHead = new PrintProfileSettings(defaultSettings);
            RoboxProfile headProfile = ROBOX_PROFILE_SETTINGS_CONTAINER.loadHeadProfileForSlicer(currentHeadType.get(), getSlicerType());
            overwriteSettingsFromProfile(settingsForHead, headProfile);
            
            PrintProfileSettings currentSettings = PRINT_PROFILE_SETTINGS_CONTAINER.getPrintProfileSettingsForSlicer(getSlicerType());
            Map<String, List<PrintProfileSetting>> settingsToWrite = 
                    PRINT_PROFILE_SETTINGS_CONTAINER.compareAndGetDifferencesBetweenSettings(settingsForHead, currentSettings);

            if (state.get() == ProfileLibraryPanelController.State.CUSTOM)
            {
                // We are about to create a new profile and need to delete the old one
                ROBOX_PROFILE_SETTINGS_CONTAINER.deleteCustomProfile(currentProfileName, getSlicerType(), currentHeadType.get());
            }
            
            RoboxProfile savedProfile = ROBOX_PROFILE_SETTINGS_CONTAINER.saveCustomProfile(settingsToWrite, cmbPrintProfile.getEditor().getText(), 
                    currentHeadType.get(), getSlicerType());
            
            isDirty.set(false);
            
            repopulateCmbPrintProfile();
            cmbPrintProfile.setValue(savedProfile.getName());
            selectPrintProfile();
            state.set(ProfileLibraryPanelController.State.CUSTOM);
        }
    }

    void whenSaveAsPressed() 
    {
        isNameValid.set(false);
        state.set(ProfileLibraryPanelController.State.NEW);

        currentProfileName = "";
        cmbPrintProfile.setEditable(true);
        cmbPrintProfile.getEditor().requestFocus();
        String newProfileName = "";
        cmbPrintProfile.getItems().add(newProfileName);
        cmbPrintProfile.setValue(newProfileName);
        cmbPrintProfile.pseudoClassStateChanged(ERROR, true);
    }

    void whenDeletePressed() 
    {
        if (state.get() != ProfileLibraryPanelController.State.NEW) 
        {
            ROBOX_PROFILE_SETTINGS_CONTAINER.deleteCustomProfile(currentProfileName, getSlicerType(), currentHeadType.get());
        }
        repopulateCmbPrintProfile();
        selectDefaultPrintProfile();
    }
    
    @Override
    public void panelSelected()
    {
        if (regenerateSettings)
        {
            regenerateSettings(getSlicerType(), true);
            regenerateSettings = false;
        }
    }

    @Override
    public String getMenuTitle() 
    {
        return "extrasMenu.printProfile";
    }

    @Override
    public List<MenuInnerPanel.OperationButton> getOperationButtons()
    {
        List<MenuInnerPanel.OperationButton> operationButtons = new ArrayList<>();
        MenuInnerPanel.OperationButton saveButton = new MenuInnerPanel.OperationButton()
        {
            @Override
            public String getTextId()
            {
                return "genericFirstLetterCapitalised.Save";
            }

            @Override
            public String getFXMLName()
            {
                return "saveButton";
            }

            @Override
            public String getTooltipTextId()
            {
                return "genericFirstLetterCapitalised.Save";
            }

            @Override
            public void whenClicked()
            {
                whenSavePressed();
            }

            @Override
            public BooleanProperty whenEnabled()
            {
                return canSave;
            }

        };
        operationButtons.add(saveButton);
        MenuInnerPanel.OperationButton saveAsButton = new MenuInnerPanel.OperationButton()
        {
            @Override
            public String getTextId()
            {
                return "genericFirstLetterCapitalised.SaveAs";
            }

            @Override
            public String getFXMLName()
            {
                return "saveAsButton";
            }

            @Override
            public String getTooltipTextId()
            {
                return "genericFirstLetterCapitalised.SaveAs";
            }

            @Override
            public void whenClicked()
            {
                whenSaveAsPressed();
            }

            @Override
            public BooleanProperty whenEnabled()
            {
                return canSaveAs;
            }

        };
        operationButtons.add(saveAsButton);
        MenuInnerPanel.OperationButton deleteButton = new MenuInnerPanel.OperationButton()
        {
            @Override
            public String getTextId()
            {
                return "genericFirstLetterCapitalised.Delete";
            }

            @Override
            public String getFXMLName()
            {
                return "deleteButton";
            }

            @Override
            public String getTooltipTextId()
            {
                return "genericFirstLetterCapitalised.Delete";
            }

            @Override
            public void whenClicked()
            {
                whenDeletePressed();
            }

            @Override
            public BooleanProperty whenEnabled()
            {
                return canDelete;
            }

        };
        operationButtons.add(deleteButton);
        return operationButtons;
    }
    
    private SlicerType getSlicerType() 
    {
        return Lookup.getUserPreferences().getSlicerType();
    }
}
