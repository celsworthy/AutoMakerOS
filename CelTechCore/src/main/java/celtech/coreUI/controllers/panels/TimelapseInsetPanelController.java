package celtech.coreUI.controllers.panels;

import celtech.Lookup;
import celtech.appManager.ApplicationMode;
import celtech.appManager.ApplicationStatus;
import celtech.appManager.ModelContainerProject;
import celtech.appManager.Project;
import celtech.appManager.TimelapseSettingsData;
import celtech.coreUI.DisplayManager;
import celtech.coreUI.controllers.ProjectAwareController;
import celtech.coreUI.controllers.utilityPanels.SnapshotController;
import celtech.modelcontrol.ProjectifiableThing;
import celtech.roboxbase.camera.CameraInfo;
import celtech.roboxbase.comms.RemoteDetectedPrinter;
import celtech.roboxbase.comms.remote.RoboxRemoteCommandInterface;
import celtech.roboxbase.configuration.datafileaccessors.CameraProfileContainer;
import celtech.roboxbase.configuration.datafileaccessors.RoboxProfileSettingsContainer;
import celtech.roboxbase.configuration.fileRepresentation.CameraProfile;
import celtech.roboxbase.configuration.fileRepresentation.PrinterSettingsOverrides;
import celtech.roboxbase.printerControl.model.Printer;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 * FXML Controller class
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class TimelapseInsetPanelController extends SnapshotController implements Initializable, ProjectAwareController, ModelContainerProject.ProjectChangesListener
{
    private final Stenographer STENO = StenographerFactory.getStenographer(TimelapseInsetPanelController.class.getName());
    
    @FXML
    private GridPane timelapseInsetRoot;

    @FXML
    private CheckBox timelapseEnableButton;

    @FXML
    private Button editCameraProfileButton;

    private Printer currentPrinter = null;
    private Project currentProject = null;
    
    private final ChangeListener<Printer> selectedPrinterChangeListener = 
            (ObservableValue<? extends Printer> observable, Printer oldValue, Printer newValue) -> {
        whenPrinterChanged(newValue);
    };

    private final ChangeListener<ApplicationMode> applicationModeChangeListener = (ObservableValue<? extends ApplicationMode> observable, ApplicationMode oldValue, ApplicationMode newValue) -> {
        setPanelVisibility();
    };

    private final ChangeListener<Boolean> cameraDetectedChangeListener = (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
        setPanelVisibility();
        if (currentProject != null &&
            currentProject.getTimelapseSettings().getTimelapseTriggerEnabled()) {
            currentProject.getTimelapseSettings().toggleDataChanged();
        }
    };

    @FXML
    void editCameraProfile(ActionEvent event)
    {
        DisplayManager.getInstance().showAndSelectCameraProfile(cameraProfileChooser.getValue());
    }

    @FXML
    void timelapseEnableAction(ActionEvent event)
    {
        if (currentProject != null) {
            currentProject.getTimelapseSettings().setTimelapseTriggerEnabled(timelapseEnableButton.isSelected());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        viewWidthFixed = false;
        super.initialize(url,rb);
        try {
            timelapseEnableButton.setSelected(false);
            
            DisplayManager.getInstance().libraryModeEnteredProperty().addListener((observable, oldValue, enteredLibraryMode) -> {
                if (!enteredLibraryMode)
                    repopulateCameraProfileChooser();
            });

            Lookup.getSelectedPrinterProperty().addListener(selectedPrinterChangeListener);

            ApplicationStatus.getInstance().modeProperty().addListener(applicationModeChangeListener);


            whenPrinterChanged(Lookup.getSelectedPrinterProperty().get());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void selectProfile(CameraProfile profile) {
        super.selectProfile(profile);
        // If repopulating, don't update the project as it will be 
        // reset to the original value.
//        if (!repopulatingProfileChooser && currentProject != null)
        if (currentProject != null)
            currentProject.getTimelapseSettings().setTimelapseProfile(Optional.ofNullable(profile));
    }

    @Override
    protected void selectCamera(CameraInfo camera) {
        super.selectCamera(camera);
        // If repopulating, don't update the project as it will be 
        // reset to the original value.
        //if (!repopulatingProfileChooser && !repopulatingCameraChooser && currentProject != null)
        if (currentProject != null)
            currentProject.getTimelapseSettings().setTimelapseCamera(Optional.ofNullable(camera));
    }

    private void whenPrinterChanged(Printer printer)
    {
        if (currentPrinter != null && connectedServer != null)
        {
            connectedServer.cameraDetectedProperty().removeListener(cameraDetectedChangeListener);
        }

        currentPrinter = printer;
        if (currentPrinter != null && 
            currentPrinter.getCommandInterface() instanceof RoboxRemoteCommandInterface) {
            connectedServer = ((RemoteDetectedPrinter)currentPrinter.getCommandInterface().getPrinterHandle()).getServerPrinterIsAttachedTo();
            connectedServer.cameraDetectedProperty().addListener(cameraDetectedChangeListener);
        }
        else
            connectedServer = null;
        
        repopulateCameraProfileChooser();
        repopulateCameraChooser();
        setPanelVisibility();
    }

    private void setPanelVisibility()
    {
        if (ApplicationStatus.getInstance().modeProperty().get() == ApplicationMode.SETTINGS &&
            connectedServer != null &&
            connectedServer.getCameraDetected())
        {
            timelapseInsetRoot.setVisible(true);
            timelapseInsetRoot.setManaged(true);
            timelapseInsetRoot.setMouseTransparent(false);
            if (snapshotTask == null) {
                takeSnapshot();
            }
        }
        else {
            timelapseInsetRoot.setVisible(false);
            timelapseInsetRoot.setManaged(false);
            timelapseInsetRoot.setMouseTransparent(true);
            if (snapshotTask != null) {
                snapshotTask.cancel();
                snapshotTask = null;
            }
        }
    }

    @Override
    public void setProject(Project project)
    {
        if (snapshotTask != null) {
            snapshotTask.cancel();
            snapshotTask = null;
        }

        if (currentProject != null)
        {
            currentProject.removeProjectChangesListener(this);
        }

        currentProject = project;
        if (project != null)
        {
            project.addProjectChangesListener(this);
            whenProjectChanged(project);
        }
    }

    private void whenProjectChanged(Project project)
    {
        whenTimelapseSettingsChanged(project.getTimelapseSettings());
    }

    @Override
    public void whenModelAdded(ProjectifiableThing modelContainer)
    {
    }

    @Override
    public void whenModelsRemoved(Set<ProjectifiableThing> modelContainers)
    {
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
    }

    @Override
    public void whenPrinterSettingsChanged(PrinterSettingsOverrides printerSettings)
    {
    }

    @Override
    public void whenTimelapseSettingsChanged(TimelapseSettingsData timelapseSettings)
    {
        timelapseEnableButton.setSelected(timelapseSettings.getTimelapseTriggerEnabled());
        timelapseSettings.getTimelapseProfile()
                         .ifPresentOrElse(cameraProfileChooser::setValue, 
                                          () -> { cameraProfileChooser.setValue(CameraProfileContainer.getInstance().getDefaultProfile()); });
        
        timelapseSettings.getTimelapseCamera()
                         .ifPresentOrElse(cameraChooser::setValue, 
                                          () -> { 
                                            if (cameraChooser.getItems().size() > 0) {
                                                // This will recursively call whenTimeLapseSettingsChanged and set the cameraChooser value.
                                                timelapseSettings.setTimelapseCamera(Optional.ofNullable(cameraChooser.getItems().get(0)));
                                            }
                                            else
                                                cameraChooser.setValue(null); 
                                          });
    }

    @Override
    public void shutdownController()
    {
        if (snapshotTask != null)
            snapshotTask.cancel();
            
        if (currentPrinter != null)
        {
        }

        if (currentProject != null)
        {
            currentProject.removeProjectChangesListener(this);
        }
        currentProject = null;

        Lookup.getSelectedPrinterProperty().removeListener(selectedPrinterChangeListener);

        ApplicationStatus.getInstance().modeProperty().removeListener(applicationModeChangeListener);
    }
}
