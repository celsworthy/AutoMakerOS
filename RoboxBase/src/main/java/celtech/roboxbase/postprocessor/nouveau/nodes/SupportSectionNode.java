package celtech.roboxbase.postprocessor.nouveau.nodes;

import celtech.roboxbase.postprocessor.nouveau.nodes.providers.Renderable;

/**
 *
 * @author Ian
 */
public class SupportSectionNode extends SectionNode implements Renderable
{
    public static final String designator =";TYPE:SUPPORT";

    @Override
    public String renderForOutput()
    {
        StringBuilder stringToOutput = new StringBuilder();
        stringToOutput.append(designator);
        stringToOutput.append(getCommentText());
        return designator;
    }
}
