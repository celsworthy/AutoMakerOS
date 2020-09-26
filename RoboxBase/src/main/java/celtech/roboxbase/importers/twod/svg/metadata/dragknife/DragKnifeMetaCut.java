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
public class DragKnifeMetaCut extends StylusMetaPart
{

    public DragKnifeMetaCut()
    {
        super(0, 0, 0, 0, null);
    }

    public DragKnifeMetaCut(double startX, double startY, double endX, double endY, String comment)
    {
        super(startX, startY, endX, endY, comment);
    }

    @Override
    public String renderToGCode()
    {
        String gcodeLine = generateXYMove(getEnd().getX(), getEnd().getY(), SVGConverterConfiguration.getInstance().getCuttingFeedrate(), "Cut " + getComment());
        return gcodeLine;
    }

    @Override
    public List<GCodeEventNode> renderToGCodeNode()
    {
        List<GCodeEventNode> gcodeNodes = new ArrayList<>();
        TravelNode travelNode = new TravelNode();
        travelNode.setCommentText("Cut " + getComment());
        travelNode.getFeedrate().setFeedRate_mmPerMin(SVGConverterConfiguration.getInstance().getCuttingFeedrate());
        travelNode.getMovement().setX(getEnd().getX());
        travelNode.getMovement().setY(getEnd().getY());

        gcodeNodes.add(travelNode);
        return gcodeNodes;
    }
}
