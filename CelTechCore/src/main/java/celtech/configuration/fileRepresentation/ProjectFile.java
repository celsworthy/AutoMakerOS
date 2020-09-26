package celtech.configuration.fileRepresentation;

import celtech.appManager.Project;
import celtech.roboxbase.camera.CameraInfo;
import celtech.roboxbase.configuration.fileRepresentation.CameraProfile;
import java.util.Date;
import java.util.Optional;

//@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
//@JsonDeserialize(using = ProjectFileDeserialiser.class)
public abstract class ProjectFile
{
    private ProjectFileTypeEnum projectType;
    private int version = 5;
    private String projectName;
    private Date lastModifiedDate;
    private String lastPrintJobID = "";
    private boolean projectNameModified = false;
    private boolean timelapseTriggerEnabled = false;
    private String timelapseProfileName = "";
    private String timelapseCameraID = "";

    public ProjectFileTypeEnum getProjectType()
    {
        return projectType;
    }

    public void setProjectType(ProjectFileTypeEnum projectType)
    {
        this.projectType = projectType;
    }
    
    public final Date getLastModifiedDate()
    {
        return lastModifiedDate;
    }

    public final void setLastModifiedDate(Date lastModifiedDate)
    {
        this.lastModifiedDate = lastModifiedDate;
    }

    public final String getLastPrintJobID()
    {
        return lastPrintJobID;
    }

    public final void setLastPrintJobID(String lastPrintJobID)
    {
        this.lastPrintJobID = lastPrintJobID;
    }

    public final String getProjectName()
    {
        return projectName;
    }

    public final void setProjectName(String projectName)
    {
        this.projectName = projectName;
    }

    public final int getVersion()
    {
        return version;
    }

    public final void setVersion(int version)
    {
        this.version = version;
    }

    public boolean isProjectNameModified() 
    {
        return projectNameModified;
    }

    public void setProjectNameModified(boolean projectNameModified) 
    {
        this.projectNameModified = projectNameModified;
    }
    
    public boolean isTimelapseTriggerEnabled() 
    {
        return timelapseTriggerEnabled;
    }

    public void setTimelapseTriggerEnabled(boolean timelapseTriggerEnabled) 
    {
        this.timelapseTriggerEnabled = timelapseTriggerEnabled;
    }
           
    public String getTimelapseProfileName() 
    {
        return timelapseProfileName;
    }

    public void setTimelapseProfileName(String timelapseProfileName) 
    {
        this.timelapseProfileName = timelapseProfileName;
    }

    public String getTimelapseCameraID() 
    {
        return timelapseCameraID;
    }

    public void setTimelapseCameraID(String timelapseCameraID) 
    {
        this.timelapseCameraID = timelapseCameraID;
    }

    public abstract void implementationSpecificPopulate(Project project);
    
    public final void populateFromProject(Project project) {
        projectName = project.getProjectName();
        lastModifiedDate = project.getLastModifiedDate().get();
        lastPrintJobID = project.getLastPrintJobID();
        projectNameModified = project.isProjectNameModified();
        timelapseTriggerEnabled = project.getTimelapseSettings().getTimelapseTriggerEnabled();
        timelapseProfileName = project.getTimelapseSettings()
                                      .getTimelapseProfile()
                                      .map(CameraProfile::getProfileName)
                                      .orElse("");
        timelapseCameraID = project.getTimelapseSettings()
                                      .getTimelapseCamera()
                                      .map((c) -> String.format("%s:%02d", c.getCameraName(), c.getCameraNumber()))
                                      .orElse("");
        
        implementationSpecificPopulate(project);
    }
}
