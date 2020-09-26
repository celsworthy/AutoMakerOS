package celtech.coreUI.visualisation.metaparts;

import celtech.modelcontrol.ProjectifiableThing;
import java.util.Set;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class ModelLoadResult
{

    private final ModelLoadResultType type;
    private final String filename;
    private final String fullFilename;
    private final Set<ProjectifiableThing> projectifiableThings;

    public ModelLoadResult(ModelLoadResultType type,
            String fullFilename,
            String filename,
            Set<ProjectifiableThing> projectifiableThings)
    {
        this.type = type;
        this.fullFilename = fullFilename;
        this.filename = filename;
        this.projectifiableThings = projectifiableThings;
    }

    public ModelLoadResultType getType()
    {
        return type;
    }

    public String getModelFilename()
    {
        return filename;
    }

    public String getFullFilename()
    {
        return fullFilename;
    }

    public Set<ProjectifiableThing> getProjectifiableThings()
    {
        return projectifiableThings;
    }
}
