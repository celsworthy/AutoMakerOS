package celtech.roboxbase.postprocessor.nouveau;

import celtech.roboxbase.postprocessor.NozzleProxy;
import celtech.roboxbase.postprocessor.nouveau.nodes.RetractNode;

/**
 *
 * @author Ian
 */
public class RetractHolder
{

    private final RetractNode node;
    private final NozzleProxy nozzle;

    public RetractHolder(RetractNode node, NozzleProxy nozzle)
    {
        this.node = node;
        this.nozzle = nozzle;
    }

    public RetractNode getNode()
    {
        return node;
    }

    public NozzleProxy getNozzle()
    {
        return nozzle;
    }

}
