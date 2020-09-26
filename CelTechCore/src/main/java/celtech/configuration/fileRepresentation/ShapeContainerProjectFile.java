package celtech.configuration.fileRepresentation;

import celtech.appManager.Project;
import celtech.appManager.ShapeContainerProject;

public class ShapeContainerProjectFile extends ProjectFile
{

    public ShapeContainerProjectFile()
    {
        setProjectType(ProjectFileTypeEnum.SHAPE);
    }

    private int subVersion = 1;

    public int getSubVersion()
    {
        return subVersion;
    }

    public void setSubVersion(int version)
    {
        this.subVersion = version;
    }

    public void populateFromProject(ShapeContainerProject project)
    {
    }

    @Override
    public void implementationSpecificPopulate(Project project)
    {
    }
}
