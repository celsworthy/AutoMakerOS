package celtech.coreUI.controllers.panels;

import celtech.Lookup;
import celtech.appManager.ApplicationMode;
import celtech.appManager.ApplicationStatus;
import celtech.appManager.Project;
import celtech.appManager.ProjectMode;
import celtech.appManager.ShapeContainerProject;
import celtech.coreUI.components.RestrictedNumberField;
import celtech.coreUI.controllers.ProjectAwareController;
import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.configuration.datafileaccessors.HeadContainer;
import celtech.roboxbase.configuration.datafileaccessors.StylusSettingsContainer;
import celtech.roboxbase.configuration.fileRepresentation.StylusSettings;
import celtech.roboxbase.printerControl.model.Head;
import celtech.roboxbase.printerControl.model.Printer;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 * FXML Controller class
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class StylusSettingsInsetPanelController implements Initializable, ProjectAwareController
{

    private final static Stenographer steno = StenographerFactory.getStenographer(StylusSettingsInsetPanelController.class.getName());
    private final static double REAL_EPSILON = 0.005;
    
    @FXML
    private HBox stylusSettingsInsetRoot;
    
    @FXML
    private GridPane stylusSettingsGridPane;
    
    @FXML
    private ComboBox<String> stylusSettingsCBox;
     
    @FXML
    private Label headTypeLabel;
    @FXML
    private Label dragKnifeLabel;
    @FXML
    private Label dragKnifeRadiusLabel;
    @FXML
    private Label overcutLabel;
    @FXML
    private Label passesLabel;
    @FXML
    private Label xOffsetLabel;
    @FXML
    private Label yOffsetLabel;
    @FXML
    private Label zOffsetLabel;
    @FXML
    private Button resetStylusSettingsButton;
    @FXML
    private Button saveStylusSettingsButton;
    @FXML
    private CheckBox dragKnifeCheckbox;
    @FXML
    private RestrictedNumberField dragKnifeRadiusEntry;
    @FXML
    private RestrictedNumberField overcutEntry;
    @FXML
    private RestrictedNumberField passesEntry;
    @FXML
    private RestrictedNumberField xOffsetEntry;
    @FXML
    private RestrictedNumberField yOffsetEntry;
    @FXML
    private RestrictedNumberField zOffsetEntry;
    
    private Project currentProject;
    private Printer currentPrinter;
    private String currentHeadType = "";
    private final SimpleBooleanProperty headTypeIsStylus = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty settingsModified = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty settingsResettable = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty settingsReadOnly = new SimpleBooleanProperty(false);
    private boolean suppressUpdates = false;
    
    private final Runnable stylusSettingsListener = () -> 
    {
        ObservableList<String> settingsNameList = StylusSettingsContainer.getInstance().getCompleteSettingsList()
                                                                                       .stream()
                                                                                       .map(StylusSettings::getName)
                                                                                       .collect(Collectors.toCollection(FXCollections::observableArrayList));
        String name = stylusSettingsCBox.getEditor().getText();
        stylusSettingsCBox.setItems(settingsNameList);
        stylusSettingsCBox.getEditor().setText(name);
        checkIfSettingsModified();
    };

    private final ChangeListener<Boolean> gCodePrepChangeListener = (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
        currentPrinter = Lookup.getSelectedPrinterProperty().get();
        updateHeadType(currentPrinter);
    };

    private final ChangeListener<ApplicationMode> applicationModeChangeListener = new ChangeListener<ApplicationMode>()
    {
        @Override
        public void changed(ObservableValue<? extends ApplicationMode> observable, ApplicationMode oldValue, ApplicationMode newValue)
        {
            if (newValue == ApplicationMode.SETTINGS &&
                currentProject != null &&
                Lookup.getSelectedProjectProperty().get() == currentProject &&
                currentProject.getMode() == ProjectMode.SVG)
            {
                stylusSettingsInsetRoot.setVisible(true);
                stylusSettingsInsetRoot.setMouseTransparent(false);
                synchronizeProjectSettings();
            } else
            {
                stylusSettingsInsetRoot.setVisible(false);
                stylusSettingsInsetRoot.setMouseTransparent(true);
            }
        }
    };

    @FXML
    void resetStylusSettings(ActionEvent event)
    {
        String name = stylusSettingsCBox.getEditor().getText();
        StylusSettingsContainer.getInstance()
                               .getSettingsByName(name)
                               .ifPresentOrElse(ss -> updateSettingsFromData(ss), () -> checkIfSettingsModified());
    }
    
    @FXML
    void saveStylusSettings(ActionEvent event)
    {
        if (currentProject != null && settingsModified.get())
        {
            String name = stylusSettingsCBox.getEditor().getText();
            Optional<StylusSettings> ssOpt = StylusSettingsContainer.getInstance().getSettingsByName(name);
            if (ssOpt.isEmpty() || !ssOpt.get().isReadOnly())
            {
                StylusSettingsContainer.getInstance().saveSettings(((ShapeContainerProject)currentProject).getStylusSettings());
            }
            else
            {
                steno.warning("Setttings not save - \"" + name + "\" are read only");
            }
        }
    }

    /**
     * Initialises the controller class.
     * 
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        try
        {
            currentPrinter = Lookup.getSelectedPrinterProperty().get();
            updateHeadType(Lookup.getSelectedPrinterProperty().get());

            ApplicationStatus.getInstance()
                    .modeProperty().addListener(applicationModeChangeListener);
        } catch (Exception ex)
        {
            steno.exception("Exception when initializing TimeCostInsetPanel", ex);
        }

        //stylusSettingsGridPane.disableProperty().bind(headTypeIsStylus.not());
        dragKnifeRadiusEntry.disableProperty().bind(dragKnifeCheckbox.selectedProperty().not());
        dragKnifeRadiusLabel.disableProperty().bind(dragKnifeCheckbox.selectedProperty().not());
        
        dragKnifeCheckbox.selectedProperty().addListener(
            (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean selected) -> {
            if (currentProject != null)
                doActionSuppressedUpdates(() -> ((ShapeContainerProject)currentProject).getStylusSettings().setHasDragKnife(selected));
        });
        
        dragKnifeRadiusEntry.valueChangedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            double newValue = dragKnifeRadiusEntry.getAsDouble();
            if (currentProject != null)
                doActionSuppressedUpdates(() -> ((ShapeContainerProject)currentProject).getStylusSettings().setDragKnifeRadius(newValue));
        });
        
        overcutEntry.valueChangedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            double newValue = overcutEntry.getAsDouble();
            if (currentProject != null)
                doActionSuppressedUpdates(() -> ((ShapeContainerProject)currentProject).getStylusSettings().setOvercut(newValue));
        });

        passesEntry.valueChangedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            int newValue = passesEntry.getAsInt();
            if (currentProject != null)
                doActionSuppressedUpdates(() -> ((ShapeContainerProject)currentProject).getStylusSettings().setPasses(newValue));
        });

        xOffsetEntry.valueChangedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            double newValue = xOffsetEntry.getAsDouble();
            if (currentProject != null)
                doActionSuppressedUpdates(() -> ((ShapeContainerProject)currentProject).getStylusSettings().setXOffset(newValue));
        });
        
        yOffsetEntry.valueChangedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            double newValue = yOffsetEntry.getAsDouble();
            if (currentProject != null)
                doActionSuppressedUpdates(() -> ((ShapeContainerProject)currentProject).getStylusSettings().setYOffset(newValue));
        });
        
        zOffsetEntry.valueChangedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            double newValue = zOffsetEntry.getAsDouble();
            if (currentProject != null) 
                doActionSuppressedUpdates(() -> ((ShapeContainerProject)currentProject).getStylusSettings().setZOffset(newValue));
        });
        
        // Initiliase stylus settings combo box.
        stylusSettingsCBox.setEditable(true);
        ObservableList<String> settingsNameList = StylusSettingsContainer.getInstance()
                                                                         .getCompleteSettingsList()
                                                                         .stream()
                                                                         .map(StylusSettings::getName)
                                                                         .collect(Collectors.toCollection(FXCollections::observableArrayList));
        stylusSettingsCBox.setItems(settingsNameList);
        stylusSettingsCBox.getSelectionModel().selectedItemProperty().addListener(
            (o, ov, nv) ->
            {
                if (!suppressUpdates)
                {
                    Optional<StylusSettings> ssOpt = StylusSettingsContainer.getInstance().getSettingsByName(nv.trim());
                    ssOpt.ifPresentOrElse(ss -> updateSettingsFromData(ss), () -> 
                    {
                        // Update the name.
                        if (currentProject != null && nv != null && !nv.isBlank())
                        {
                            ((ShapeContainerProject)currentProject).getStylusSettings()
                                                                   .setName(nv);
                            settingsModified.set(true);
                            settingsResettable.set(false);
                            settingsReadOnly.set(false);
                            //steno.info("stylusSettingsCBox listener -  settingsResettable set to false");
                        }
                    });
                }
            });
        if (!settingsNameList.isEmpty())
        {
            Optional<StylusSettings> ssOpt = StylusSettingsContainer.getInstance().getSettingsByName(settingsNameList.get(0));
            ssOpt.ifPresent(ss ->
            {
                stylusSettingsCBox.setValue(settingsNameList.get(0)); 
                updateSettingsFromData(ss);
            });
        }
        StylusSettingsContainer.getInstance().addListener(stylusSettingsListener);
        resetStylusSettingsButton.disableProperty().bind(settingsResettable.and(settingsModified).not());
        saveStylusSettingsButton.disableProperty().bind(settingsModified.not().or(settingsReadOnly));
    }

    private void updateSettingsFromData(StylusSettings settingsData)
    {
        if (currentProject instanceof ShapeContainerProject)
        {
            ShapeContainerProject stylusProject = (ShapeContainerProject)currentProject;
            StylusSettings projectSettings = stylusProject.getStylusSettings();
            projectSettings.setFrom(settingsData);
            updateFields(settingsData);
            checkIfSettingsModified();
        }
    }    

    private void updateHeadType(Printer printer)
    {
        String headTypeBefore = currentHeadType;
        Head.HeadType headEType = HeadContainer.defaultHeadType;
        
        if (printer != null && printer.headProperty().get() != null)
        {
            currentHeadType = printer.headProperty().get().typeCodeProperty().get();
            headEType = printer.headProperty().get().headTypeProperty().get();
        } else
        {
            currentHeadType = HeadContainer.defaultHeadID;
        }
        if (!headTypeBefore.equals(currentHeadType))
        {
            headTypeIsStylus.set(headEType == Head.HeadType.STYLUS_HEAD);
            BaseLookup.getTaskExecutor().runOnGUIThread(() ->
            {
                headTypeLabel.setText(currentHeadType);
            });
        }
    }
    
    @Override
    public void setProject(Project project)
    {
        if (currentProject != null)
            currentProject.getGCodeGenManager().getDataChangedProperty().removeListener(this.gCodePrepChangeListener);
        
        currentProject = project;

        if (currentProject != null) {
            currentProject.getGCodeGenManager().getDataChangedProperty().addListener(this.gCodePrepChangeListener);
            synchronizeProjectSettings();
        }
    }
    
    private void synchronizeProjectSettings()
    {
        if (currentProject != null)
        {
            StylusSettings projectSettings = ((ShapeContainerProject)currentProject).getStylusSettings();
            if (!projectSettings.getName().isEmpty())
                updateFields(projectSettings);    
            checkIfSettingsModified();
        }
    }

    private void updateFields(StylusSettings stylusSettings)
    {
        if (!suppressUpdates)
        {
            String name = stylusSettings.getName();
            suppressUpdates = true;
            Optional<StylusSettings> settingsOpt = StylusSettingsContainer.getInstance().getSettingsByName(name);
            settingsOpt.ifPresentOrElse(ss -> stylusSettingsCBox.setValue(ss.getName()),
                                        () -> stylusSettingsCBox.getEditor().setText(name));
            dragKnifeCheckbox.selectedProperty().set(stylusSettings.getHasDragKnife());
            dragKnifeRadiusEntry.setValue(stylusSettings.getDragKnifeRadius());
            overcutEntry.setValue(stylusSettings.getOvercut());
            passesEntry.setValue(stylusSettings.getPasses());
            xOffsetEntry.setValue(stylusSettings.getXOffset());
            yOffsetEntry.setValue(stylusSettings.getYOffset());
            zOffsetEntry.setValue(stylusSettings.getZOffset());
            suppressUpdates = false;
        }
    }
    
    private boolean realValuesEqual(double a, double b)
    {
        return (Math.abs(a - b) < REAL_EPSILON);
    }

    private boolean settingsMatch(StylusSettings ss)
    {
        return  (dragKnifeCheckbox.selectedProperty().get() == ss.getHasDragKnife() &&
                 realValuesEqual(dragKnifeRadiusEntry.getAsDouble(), ss.getDragKnifeRadius()) &&
                 realValuesEqual(overcutEntry.getAsDouble(), ss.getOvercut()) &&
                 passesEntry.getAsInt() == ss.getPasses() &&
                 realValuesEqual(xOffsetEntry.getAsDouble(), ss.getXOffset()) &&
                 realValuesEqual(yOffsetEntry.getAsDouble(), ss.getYOffset()) &&
                 realValuesEqual(zOffsetEntry.getAsDouble(), ss.getZOffset()));
    }
    
    private void checkIfSettingsModified()
    {
        String name = stylusSettingsCBox.getEditor().getText();
        StylusSettingsContainer.getInstance()
                               .getSettingsByName(name)
                               .ifPresentOrElse(ss -> 
                                                {
                                                    settingsResettable.set(true);
                                                    settingsReadOnly.set(ss.isReadOnly());
                                                    settingsModified.set(!settingsMatch(ss));
                                                },
                                                () ->
                                                {
                                                    settingsResettable.set(false);
                                                    settingsReadOnly.set(false);
                                                    settingsModified.set(true);
                                                });
        steno.info("checkIfSettingsModified -  settingsResettable set to " + settingsResettable.get());
        steno.info("checkIfSettingsModified -  settingsReadOnly set to " + settingsReadOnly.get());
        steno.info("checkIfSettingsModified -  settingsModified set to " + settingsModified.get());
    }
    
    private void doActionSuppressedUpdates(Runnable action)
    {
        try {
            suppressUpdates = true;
            action.run();
            checkIfSettingsModified();
        }
        finally {
            suppressUpdates = false;
        }
    }
    
    @Override
    public void shutdownController()
    {

        if (currentProject != null)
            currentProject.getGCodeGenManager().getDataChangedProperty().removeListener(this.gCodePrepChangeListener);
        currentProject = null;

        ApplicationStatus.getInstance()
                .modeProperty().removeListener(applicationModeChangeListener);
        
        StylusSettingsContainer.getInstance().removeListener(stylusSettingsListener);
    }
}
