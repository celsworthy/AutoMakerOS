package celtech.roboxbase.utils.models;

import celtech.roboxbase.camera.CameraInfo;
import celtech.roboxbase.configuration.fileRepresentation.CameraSettings;
import celtech.roboxbase.services.camera.CameraTriggerData;
import celtech.roboxbase.services.slicer.PrintQualityEnumeration;
import java.util.List;
import java.util.Optional;

/**
 * Object to represent a project that can be printed.
 * This is the intermediate stage between a project and print job.
 * 
 * @author George Salter
 */
public class PrintableProject {
    
    private String projectName;
    private String jobUUID;
    private PrintQualityEnumeration printQuality;
    private String projectLocation;
    private List<Boolean> usedExtruders;
    private CameraTriggerData cameraTriggerData; // Used by post processor to set trigger points in GCode.
    private Optional<CameraSettings> cameraData; // Sent to Root so it can use the specified camera.
    private boolean cameraEnabled;
    
    public PrintableProject(String projectName, 
							PrintQualityEnumeration printQuality,
							String projectLocation) {
		this.projectName = projectName;
        this.printQuality = printQuality;
        this.projectLocation = projectLocation;
        this.usedExtruders = null;
        this.jobUUID = "";
        this.cameraTriggerData = null;
        this.cameraData = Optional.empty();
        this.cameraEnabled = false;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    
    public PrintQualityEnumeration getPrintQuality() {
        return printQuality;
    }

    public void setPrintQuality(PrintQualityEnumeration printQuality) {
        this.printQuality = printQuality;
    }

    public String getProjectLocation() {
        return projectLocation;
    }

    public void setProjectLocation(String projectLocation) {
        this.projectLocation = projectLocation;
    }

    public String getJobUUID() {
        return jobUUID;
    }

    public void setJobUUID(String jobUUID) {
        this.jobUUID = jobUUID;
    }

    public List<Boolean> getUsedExtruders() {
        return usedExtruders;
    }

    public void setUsedExtruders(List<Boolean> usedExtruders) {
        this.usedExtruders = usedExtruders;
    }

    public CameraTriggerData getCameraTriggerData() {
        return cameraTriggerData;
    }

    public void setCameraTriggerData(CameraTriggerData cameraTriggerData) {
        this.cameraTriggerData = cameraTriggerData;
    }

    public Optional<CameraSettings> getCameraData() {
        return cameraData;
    }

    public void setCameraData(Optional<CameraSettings> cameraData) {
        this.cameraData = cameraData;
    }

    public boolean isCameraEnabled() {
        return cameraEnabled;
    }

    public void setCameraEnabled(boolean cameraEnabled) {
        this.cameraEnabled = cameraEnabled;
    }
}
