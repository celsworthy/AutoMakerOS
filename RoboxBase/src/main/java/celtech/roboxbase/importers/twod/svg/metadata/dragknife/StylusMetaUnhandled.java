package celtech.roboxbase.importers.twod.svg.metadata.dragknife;

import celtech.roboxbase.postprocessor.nouveau.nodes.CommentNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.GCodeEventNode;
import java.util.List;

/**
 *
 * @author ianhudson
 */
public class StylusMetaUnhandled extends StylusMetaPart
{
    private final String comment;

    public StylusMetaUnhandled()
    {
        super(0, 0, 0, 0, null);
        this.comment = null;
    }
    
    public StylusMetaUnhandled(double startX, double startY, double endX, double endY, String comment)
    {
        super(startX, startY, endX, endY, comment);
        this.comment = comment;
    }
    
    @Override
    public String getComment()
    {
        return comment;
    }
    
    @Override
    public String renderToGCode()
    {
        return "; Unhandled - " + comment;
    }

    @Override
    public List<GCodeEventNode> renderToGCodeNode()
    {
        return null;
    }
}
