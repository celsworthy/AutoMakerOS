package celtech.roboxbase.postprocessor.nouveau.nodes;

import celtech.roboxbase.postprocessor.nouveau.nodes.providers.Renderable;

/**
 *
 * @author Ian
 */
public class ObjectDelineationNode extends GCodeEventNode implements Renderable
{

    private int objectNumber = -1;

    public int getObjectNumber()
    {
        return objectNumber;
    }

    public void setObjectNumber(int objectNumber)
    {
        this.objectNumber = objectNumber;
    }

    @Override
    public String renderForOutput()
    {
        return ";Object " + objectNumber;
    }
}
