package celtech.configuration.fileRepresentation;

import celtech.appManager.Project;
import celtech.appManager.ShapeContainerProject;
import celtech.modelcontrol.ItemState;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ShapeContainerProjectFile extends ProjectFile
{
    private Map<Integer, Set<Integer>> groupStructure = new HashMap<>();
    private Map<Integer, ItemState> groupState = new HashMap<>();

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
        if (project instanceof ShapeContainerProject)
        {
            ShapeContainerProject scp = (ShapeContainerProject) project;
            groupStructure = scp.getGroupStructure();
            groupState = scp.getGroupState();
        }
    }
    
    public Map<Integer, Set<Integer>> getGroupStructure()
    {
        return groupStructure;
    }

    public void setGroupStructure(Map<Integer, Set<Integer>> groupStructure)
    {
        this.groupStructure = groupStructure;
    }

    public Map<Integer, ItemState> getGroupState()
    {
        return groupState;
    }

    public void setGroupState(Map<Integer, ItemState> groupState)
    {
        this.groupState = groupState;
    }
}
