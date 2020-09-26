package celtech.roboxbase.postprocessor.nouveau;

import celtech.roboxbase.postprocessor.nouveau.nodes.ObjectDelineationNode;
import org.parboiled.Action;
import org.parboiled.Context;

/**
 *
 * @author Ian
 */
public class ObjectSectionActionClass implements Action
{
    private ObjectDelineationNode node = null;
    
    @Override
    public boolean run(Context context)
    {
        node = new ObjectDelineationNode();
        return true;
    }

    public ObjectDelineationNode getNode()
    {
        return node;
    }
}
