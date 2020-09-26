package celtech.roboxbase.postprocessor.nouveau.helpers;

/**
 *
 * @author Ian
 */
public class ToolDefinition
{

    private final int toolNumber;
    private final double duration;
    private final float extrusion;

    public ToolDefinition(int toolNumber,
            double duration)
    {
        this.toolNumber = toolNumber;
        this.duration = duration;
        this.extrusion = 0;
    }

    public ToolDefinition(int toolNumber,
            double duration,
            float extrusion)
    {
        this.toolNumber = toolNumber;
        this.duration = duration;
        this.extrusion = extrusion;
    }

    public int getToolNumber()
    {
        return toolNumber;
    }

    public double getDuration()
    {
        return duration;
    }

    public float getExtrusion()
    {
        return extrusion;
    }
}
