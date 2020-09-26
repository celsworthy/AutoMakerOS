package celtech.roboxbase.postprocessor.nouveau;

import celtech.roboxbase.postprocessor.nouveau.nodes.GCodeEventNode;
import java.util.List;

/**
 *
 * @author Ian
 */
public class InScopeEvents
{

    private final List<GCodeEventNode> inScopeEvents;
    private final double availableExtrusion;

    public InScopeEvents(List<GCodeEventNode> inScopeEvents, double availableExtrusion)
    {
        this.inScopeEvents = inScopeEvents;
        this.availableExtrusion = availableExtrusion;
    }

    public double getAvailableExtrusion()
    {
        return availableExtrusion;
    }

    public List<GCodeEventNode> getInScopeEvents()
    {
        return inScopeEvents;
    }
}
