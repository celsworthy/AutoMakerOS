package celtech.roboxbase.postprocessor.nouveau.nodes;

import celtech.roboxbase.postprocessor.nouveau.nodes.providers.Renderable;

/**
 *
 * @author Ian
 */
public class OrphanSectionNode extends SectionNode implements Renderable
{
    @Override
    public String renderForOutput()
    {
        return ";Orphan section";
    }
}
