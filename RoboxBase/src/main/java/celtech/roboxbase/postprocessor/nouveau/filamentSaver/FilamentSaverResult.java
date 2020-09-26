package celtech.roboxbase.postprocessor.nouveau.filamentSaver;

import celtech.roboxbase.postprocessor.nouveau.nodes.ToolSelectNode;

/**
 *
 * @author Ian
 */
public class FilamentSaverResult
{

    private final boolean n1HeaterOn;
    private final double[] toolLastRequiredAt;
    private final boolean n2HeaterOn;
    private final ToolSelectNode lastToolSelectNode;

    public FilamentSaverResult(
            boolean n1HeaterOn,
            boolean n2HeaterOn,
            double[] toolLastRequiredAt,
            ToolSelectNode lastSelectNode)
    {
        this.n1HeaterOn = n1HeaterOn;
        this.n2HeaterOn = n2HeaterOn;
        this.toolLastRequiredAt = toolLastRequiredAt;
        this.lastToolSelectNode = lastSelectNode;
    }

    public double[] getToolLastRequiredAt()
    {
        return toolLastRequiredAt;
    }

    public ToolSelectNode getLastToolSelectNode()
    {
        return lastToolSelectNode;
    }

    public boolean isN1HeaterOn()
    {
        return n1HeaterOn;
    }

    public boolean isN2HeaterOn()
    {
        return n2HeaterOn;
    }
}
