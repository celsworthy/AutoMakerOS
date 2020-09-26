package celtech.roboxbase.postprocessor.nouveau.nodes;

import celtech.roboxbase.postprocessor.nouveau.nodes.providers.Renderable;

/**
 *
 * @author Ian
 */
public class UnrecognisedLineNode extends GCodeEventNode implements Renderable
{

    private final String discardedLine;

    public UnrecognisedLineNode(String discardedLine)
    {
        this.discardedLine = discardedLine;
    }

    public String getDiscardedLine()
    {
        return discardedLine;
    }

    @Override
    public String renderForOutput()
    {
        return "; " + discardedLine;
    }

    @Override
    public String toString()
    {
        return "Unrecognised: " + discardedLine;
    }
}
