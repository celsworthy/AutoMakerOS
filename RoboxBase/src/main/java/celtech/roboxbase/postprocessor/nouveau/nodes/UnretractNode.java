package celtech.roboxbase.postprocessor.nouveau.nodes;

import celtech.roboxbase.postprocessor.nouveau.nodes.providers.Extrusion;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.ExtrusionProvider;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.Feedrate;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.FeedrateProvider;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.Renderable;

/**
 *
 * @author Ian
 */
public class UnretractNode extends GCodeEventNode implements ExtrusionProvider, FeedrateProvider, Renderable
{
    private final Feedrate feedrate = new Feedrate();
    private final Extrusion extrusion = new Extrusion();

    @Override
    public Extrusion getExtrusion()
    {
        return extrusion;
    }

    @Override
    public Feedrate getFeedrate()
    {
        return feedrate;
    }

    @Override
    public String renderForOutput()
    {
        StringBuilder stringToOutput = new StringBuilder();

        stringToOutput.append("G1 ");
        stringToOutput.append(feedrate.renderForOutput());
        stringToOutput.append(' ');
        stringToOutput.append(extrusion.renderForOutput());
        stringToOutput.append(' ');
        stringToOutput.append(getCommentText());
        return stringToOutput.toString().trim();
    }
}
