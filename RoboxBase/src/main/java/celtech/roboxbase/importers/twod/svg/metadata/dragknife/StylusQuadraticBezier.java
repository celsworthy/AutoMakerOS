package celtech.roboxbase.importers.twod.svg.metadata.dragknife;

import celtech.roboxbase.postprocessor.nouveau.nodes.GCodeEventNode;
import java.util.List;
import javafx.scene.shape.QuadCurve;

/**
 *
 * @author ianhudson
 */
public class StylusQuadraticBezier extends StylusMetaPart
{

    private double controlX, controlY;

    public StylusQuadraticBezier()
    {
        super(0, 0, 0, 0, null);
    }

    public StylusQuadraticBezier(double startX, double startY, double controlX, double controlY, double endX, double endY, String comment)
    {
        super(startX, startY, endX, endY, comment);
        this.controlX = controlX;
        this.controlY = controlY;
    }

    @Override
    public String renderToGCode()
    {
//        String gcodeLine = generateXYMove(getEnd().getX(), getEnd().getY(), SVGConverterConfiguration.getInstance().getCuttingFeedrate(), "Cut " + getComment());
        return ";Quadratic";
    }

    @Override
    public List<GCodeEventNode> renderToGCodeNode()
    {
        QuadCurve quadCurve = new QuadCurve();
        quadCurve.setStartX(getStart().getX());
        quadCurve.setStartY(getStart().getY());
        quadCurve.setControlX(controlX);
        quadCurve.setControlY(controlY);
        quadCurve.setEndX(getEnd().getX());
        quadCurve.setEndY(getEnd().getY());

        return renderShapeToGCodeNode(quadCurve);
    }
}
