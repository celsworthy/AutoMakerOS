package celtech.roboxbase.postprocessor.nouveau;

import celtech.roboxbase.postprocessor.nouveau.nodes.OrphanObjectDelineationNode;
import org.parboiled.Action;
import org.parboiled.Context;

/**
 *
 * @author Ian
 */
public class OrphanObjectSectionActionClass implements Action
{
    private OrphanObjectDelineationNode node = null;
    
    @Override
    public boolean run(Context context)
    {
        node = new OrphanObjectDelineationNode();
        return true;
    }

    public OrphanObjectDelineationNode getNode()
    {
        return node;
    }
}
