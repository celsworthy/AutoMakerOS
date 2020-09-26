package celtech.coreUI.controllers.utilityPanels;

import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.camera.CameraInfo;
import celtech.roboxbase.comms.DetectedServer;
import celtech.roboxbase.configuration.datafileaccessors.CameraProfileContainer;
import celtech.roboxbase.configuration.fileRepresentation.CameraProfile;
import celtech.roboxbase.configuration.fileRepresentation.CameraSettings;
import celtech.utils.CameraInfoStringConverter;
import celtech.utils.CameraProfileStringConverter;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 * FXML Controller class
 *
 * @author Ian
 */
public abstract class SnapshotController implements Initializable
{
    private static final Stenographer STENO = StenographerFactory.getStenographer(SnapshotController.class.getName());
    protected static final int SNAPSHOT_INTERVAL = 500;
    
    @FXML
    protected ComboBox<CameraProfile> cameraProfileChooser;
    
    @FXML
    protected ComboBox<CameraInfo> cameraChooser;
    
    @FXML
    protected ImageView snapshotView;
    
    protected boolean viewWidthFixed = true;
    protected CameraProfile selectedProfile = null;
    protected CameraInfo selectedCamera = null;
    protected DetectedServer connectedServer = null;
    protected Task<Void> snapshotTask = null;
    
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        cameraProfileChooser.setConverter(new CameraProfileStringConverter(cameraProfileChooser::getItems));
        cameraProfileChooser.valueProperty().addListener((observable, oldValue, newValue) -> {
            selectProfile(newValue);
        });
        
        cameraChooser.setConverter(new CameraInfoStringConverter(cameraChooser::getItems));
        BaseLookup.getConnectedCameras().addListener((ListChangeListener.Change<? extends CameraInfo> c) -> {
            repopulateCameraProfileChooser();
            repopulateCameraChooser();
        });
        cameraChooser.valueProperty().addListener((observable, oldValue, newValue) -> {
            selectCamera(newValue);
        });
    }
    
    public void selectCameraAndProfile(String profileName, String cameraName) {
        // Find the profile.
        cameraProfileChooser.getItems()
                            .stream()
                            .filter(cp -> cp.getProfileName().equalsIgnoreCase(profileName))
                            .findFirst()
                            .ifPresent((p) -> {
                                selectedProfile = p;
                                BaseLookup.getTaskExecutor().runOnGUIThread(() -> { cameraProfileChooser.setValue(p); });
                            });
        cameraChooser.getItems()
                     .stream()
                     .filter(ci -> ci.getCameraName().equalsIgnoreCase(cameraName))
                     .findFirst()
                     .ifPresent((c) -> {
                         selectedCamera = c;
                         BaseLookup.getTaskExecutor().runOnGUIThread(() -> { cameraChooser.setValue(c); });
                     });
    }
    
    protected void selectProfile(CameraProfile profile) {
        selectedProfile = profile;
        if (connectedServer != null) {
            populateCameraChooser();
            if (profile != null) {
                if (viewWidthFixed) {
                    double aspectRatio = profile.getCaptureHeight() / (double)profile.getCaptureWidth();
                    double fitHeight = (aspectRatio * snapshotView.getFitWidth());
                    snapshotView.setFitHeight(fitHeight);
                }
                else {
                    double aspectRatio = profile.getCaptureWidth() / (double)profile.getCaptureHeight();
                    double fitWidth = (aspectRatio * snapshotView.getFitHeight());
                    snapshotView.setFitWidth(fitWidth);
                }
                cameraChooser.getItems()
                             .stream()
                             .filter(ci -> profile.getCameraName().isBlank() ||
                                           ci.getCameraName().equalsIgnoreCase(profile.getCameraName()))
                             .findFirst()
                             .ifPresentOrElse(cameraChooser::setValue, 
                                              () -> { cameraChooser.setValue(cameraChooser.getItems().size() > 0 ? cameraChooser.getItems().get(0) : null); });
            }
            else
                cameraChooser.setValue(cameraChooser.getItems().size() > 0 ? cameraChooser.getItems().get(0) : null);
        }
}
    
    protected void repopulateCameraProfileChooser() {
        BaseLookup.getTaskExecutor().runOnGUIThread(() -> {
            STENO.debug("Repopulating profile chooser");
            CameraProfile currentProfile = cameraProfileChooser.getValue();
            populateCameraProfileChooser();
            // Interesting mix of programming styles.
            CameraProfile profileToSelect;
            if (currentProfile != null) {
                String currentProfileName = currentProfile.getProfileName();
                List<CameraProfile> itemList = cameraProfileChooser.getItems();
                profileToSelect = itemList.stream()
                                          .filter(p -> p.getProfileName().equals(currentProfileName))
                                          .findAny()
                                          .orElse(CameraProfileContainer.getInstance().getDefaultProfile());
            }
            else
                profileToSelect = CameraProfileContainer.getInstance().getDefaultProfile();
            cameraProfileChooser.setValue(profileToSelect);
        });
    }
    
    protected void populateCameraProfileChooser() {
        // Get the names of all the cameras on the current server.
        if (connectedServer != null) {
            List<String> cameraNames = BaseLookup.getConnectedCameras()
                                                 .stream()
                                                 .filter(cc -> cc.getServer() == connectedServer)
                                                 .map(CameraInfo::getCameraName)
                                                 .distinct()
                                                 .collect(Collectors.toList());
            Map<String, CameraProfile> cameraProfilesMap = CameraProfileContainer.getInstance().getCameraProfilesMap();
            ObservableList<CameraProfile> items = cameraProfilesMap.values()
                                        .stream()
                                        .filter(pp -> pp.getCameraName().isBlank() ||
                                                      cameraNames.contains(pp.getCameraName()))
                                        .collect(Collectors.toCollection(FXCollections::observableArrayList));
            cameraProfileChooser.setItems(items);
        }
        else
            cameraProfileChooser.setItems(FXCollections.emptyObservableList());
    }
    
    protected void selectCamera(CameraInfo camera) {
        selectedCamera = camera;
        if (connectedServer != null) {
            snapshotView.setImage(null);
            if (selectedCamera != null)
                takeSnapshot();
        }
    }

    protected void repopulateCameraChooser() {
        BaseLookup.getTaskExecutor().runOnGUIThread(() -> {
            STENO.debug("Repopulating camera chooser");
            CameraInfo currentCamera = cameraChooser.getValue();
            populateCameraChooser();
            CameraInfo cameraToSelect = cameraChooser.getItems()
                                                     .stream()
                                                     .filter(ci -> ci.equals(currentCamera))
                                                     .findAny()
                                                     .orElse(cameraChooser.getItems().size() > 0 ? cameraChooser.getItems().get(0) : null);
            cameraChooser.setValue(cameraToSelect);
        });
    }

    protected void populateCameraChooser() {
        String cameraName = (cameraProfileChooser.getValue() != null ? cameraProfileChooser.getValue().getCameraName()
                                                                     : "");
        if (connectedServer != null) {
            ObservableList<CameraInfo> itemList = BaseLookup.getConnectedCameras().stream()
                    .filter(cc -> cc.getServer() == connectedServer &&
                                  (cameraName.isBlank() ||
                                   cameraName.equalsIgnoreCase(cc.getCameraName())))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList))
                    .sorted();
            cameraChooser.setItems(itemList);
        }
        else
        {
            cameraChooser.setItems(FXCollections.emptyObservableList());
        }
    }

    protected void takeSnapshot()
    {
        if (snapshotTask != null) {
            snapshotTask.cancel();
            snapshotTask = null;
        }
        if (selectedProfile != null && selectedCamera != null) {
            CameraSettings snapshotSettings = new CameraSettings(selectedProfile, selectedCamera);
            snapshotTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    DetectedServer server = snapshotSettings.getCamera().getServer();
                    while (!isCancelled()) {
                        Image snapshotImage = server.takeCameraSnapshot(snapshotSettings);
                        BaseLookup.getTaskExecutor().runOnGUIThread(() -> {
                            if (selectedCamera == snapshotSettings.getCamera()) {
                                snapshotView.setImage(snapshotImage);
                            }
                        });
                        if (!isCancelled()) {
                            Thread.sleep(SNAPSHOT_INTERVAL);
                        }
                    }
                    return null;
                }
            };
            Thread snapshotThread = new Thread(snapshotTask);
            snapshotThread.start();
        }
    }
}
