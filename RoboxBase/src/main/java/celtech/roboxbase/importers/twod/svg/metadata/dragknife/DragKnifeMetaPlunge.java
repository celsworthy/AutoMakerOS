package celtech.roboxbase.importers.twod.svg.metadata.dragknife;

import celtech.roboxbase.importers.twod.svg.SVGConverterConfiguration;
import celtech.roboxbase.postprocessor.nouveau.nodes.GCodeEventNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.TravelNode;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ianhudson
 */
public class DragKnifeMetaPlunge extends StylusMetaPart
{

    public DragKnifeMetaPlunge()
    {
        super(0, 0, 0, 0, null);
    }

    public DragKnifeMetaPlunge(double startX, double startY, String comment)
    {
        super(startX, startY, 0, 0, comment);
    }

    @Override
    public String renderToGCode()
    {
        String gcodeLine = generatePlunge(getComment());
        return gcodeLine;
    }

    @Override
    public List<GCodeEventNode> renderToGCodeNode()
    {
        List<GCodeEventNode> gcodeNodes = new ArrayList<>();

        TravelNode travelNode = new TravelNode();
        travelNode.setCommentText("Lift " + getComment());
        travelNode.getFeedrate().setFeedRate_mmPerMin(SVGConverterConfiguration.getInstance().getPlungeFeedrate());
        travelNode.getMovement().setZ(SVGConverterConfiguration.getInstance().getLiftDepth());

        gcodeNodes.add(travelNode);
        return gcodeNodes;
    }
}
