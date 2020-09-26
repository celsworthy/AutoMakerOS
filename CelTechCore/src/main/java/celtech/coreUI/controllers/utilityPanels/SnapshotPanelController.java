package celtech.coreUI.controllers.utilityPanels;

import celtech.Lookup;
import celtech.appManager.ApplicationMode;
import celtech.appManager.ApplicationStatus;
import celtech.roboxbase.camera.CameraInfo;
import celtech.roboxbase.comms.DetectedServer.CameraTag;
import celtech.roboxbase.comms.RemoteDetectedPrinter;
import celtech.roboxbase.comms.remote.RoboxRemoteCommandInterface;
import celtech.roboxbase.configuration.CoreMemory;
import celtech.roboxbase.configuration.fileRepresentation.CameraProfile;
import celtech.roboxbase.printerControl.model.Printer;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * FXML Controller class
 *
 * @author Ian
 */
public class SnapshotPanelController extends SnapshotController
{
    private Printer connectedPrinter = null;

    private final ChangeListener<Boolean> cameraDetectedChangeListener = (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
        controlSnapshotTask();
    };
    
    private final ChangeListener<ApplicationMode> applicationModeChangeListener = (ObservableValue<? extends ApplicationMode> observable, ApplicationMode oldValue, ApplicationMode newValue) -> {
        controlSnapshotTask();
    };

    private final ChangeListener<CameraTag> cameraTagChangeListener = (ObservableValue<? extends CameraTag> observable, CameraTag oldValue, CameraTag newValue) -> {
        selectCameraAndProfile(newValue.getCameraProfileName(), newValue.getCameraName());
    };

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        viewWidthFixed = true;
        super.initialize(url, rb);
        Lookup.getSelectedPrinterProperty().addListener((ObservableValue<? extends Printer> observable, Printer oldValue, Printer newValue) -> {
            if (connectedPrinter != null)
                unbindFromPrinter(connectedPrinter);

            if (newValue != null)
                bindToPrinter(newValue);
        });
        ApplicationStatus.getInstance().modeProperty().addListener(applicationModeChangeListener);
    }
    
    private void unbindFromPrinter(Printer printer)
    {
        if (connectedPrinter != null) {
            connectedPrinter = null;
        }
        
        if (connectedServer != null) {
            connectedServer.cameraDetectedProperty().removeListener(cameraDetectedChangeListener);
            connectedServer.cameraTagProperty().removeListener(cameraTagChangeListener);
            connectedServer = null;
        }
        controlSnapshotTask();
    }

    private void bindToPrinter(Printer printer)
    {
        connectedPrinter = printer;
        if (connectedPrinter != null && 
            connectedPrinter.getCommandInterface() instanceof RoboxRemoteCommandInterface) {
            connectedServer = ((RemoteDetectedPrinter)connectedPrinter.getCommandInterface().getPrinterHandle()).getServerPrinterIsAttachedTo();
            String profileName = "";
            String cameraName = "";
            CameraTag tag = connectedServer.cameraTagProperty().get();
            if (tag != null) {
                profileName = tag.getCameraProfileName();
                cameraName = tag.getCameraName();
            }
            
            populateCameraProfileChooser();
            populateCameraChooser();
            
            if (!profileName.isBlank() && !cameraName.isBlank()) {
                selectCameraAndProfile(profileName, cameraName);
            }
            else if (selectedProfile != null && selectedCamera != null) {
                connectedServer.setCameraTag(selectedProfile.getProfileName(), selectedCamera.getCameraName());
            }
        }
        controlSnapshotTask();
    }
    
    private void controlSnapshotTask()
    {
        if (ApplicationStatus.getInstance().modeProperty().get() == ApplicationMode.STATUS &&
            connectedServer != null &&
            connectedServer.getCameraDetected())
        {
            repopulateCameraProfileChooser();
            repopulateCameraChooser();
            if (snapshotTask == null) {
                takeSnapshot();
            }
        }
        else {
            if (snapshotTask != null) {
                snapshotTask.cancel();
                snapshotTask = null;
            }
        }
    }
    
    @Override
    protected void selectProfile(CameraProfile profile) {
        super.selectProfile(profile);
        if (connectedServer != null && profile != null && selectedCamera != null) {
            connectedServer.setCameraTag(profile.getProfileName(), selectedCamera.getCameraName());
            CoreMemory.getInstance().updateRoboxRoot(connectedServer);
        }
    }
    
    @Override
    protected void selectCamera(CameraInfo camera) {
        super.selectCamera(camera);
        if (connectedServer != null && selectedProfile != null && camera != null) {
            connectedServer.setCameraTag(selectedProfile.getProfileName(), camera.getCameraName());
            CoreMemory.getInstance().updateRoboxRoot(connectedServer);
        }
    }
}
