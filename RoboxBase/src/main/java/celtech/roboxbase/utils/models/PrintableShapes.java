package celtech.roboxbase.utils.models;

import java.util.List;

/**
 *
 * @author Ian
 */
public class PrintableShapes
{
    private final List<ShapeForProcessing> shapesForProcessing;
    private final String projectName;
    private final String requiredPrintJobID;

    public PrintableShapes(List<ShapeForProcessing> shapesForProcessing,
            String projectName,
            String requiredPrintJobID)
    {
        this.shapesForProcessing = shapesForProcessing;
        this.projectName = projectName;
        this.requiredPrintJobID = requiredPrintJobID;
    }

    public List<ShapeForProcessing> getShapesForProcessing()
    {
        return shapesForProcessing;
    }

    public String getProjectName()
    {
        return projectName;
    }

    public String getRequiredPrintJobID()
    {
        return requiredPrintJobID;
    }
}
