package celtech.roboxbase.postprocessor.nouveau.nodes;

import celtech.roboxbase.postprocessor.nouveau.nodes.GCodeEventNode;

/**
 *
 * @author Ian
 */
public class GCodeEventNodeTestImpl extends GCodeEventNode
{

    private String name;

    public GCodeEventNodeTestImpl()
    {
    }

    public GCodeEventNodeTestImpl(String name)
    {
        this.name = name;
    }
}
