package celtech.roboxbase.postprocessor.nouveau.helpers;

/**
 *
 * @author Ian
 */
public class LayerDefinition
{
    private final int layerNumber;
    private final ToolDefinition[] tools;

    public LayerDefinition(int layerNumber, ToolDefinition[] tools)
    {
        this.layerNumber = layerNumber;
        this.tools = tools;
    }

    public int getLayerNumber()
    {
        return layerNumber;
    }

    public ToolDefinition[] getTools()
    {
        return tools;
    }
}
