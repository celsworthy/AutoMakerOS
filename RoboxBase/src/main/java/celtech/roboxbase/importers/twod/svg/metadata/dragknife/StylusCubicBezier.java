package celtech.roboxbase.importers.twod.svg.metadata.dragknife;

import celtech.roboxbase.postprocessor.nouveau.nodes.GCodeEventNode;
import java.util.List;
import javafx.scene.shape.CubicCurve;

/**
 *
 * @author ianhudson
 */
public class StylusCubicBezier extends StylusMetaPart
{

    private double controlX1, controlY1, controlX2, controlY2;

    public StylusCubicBezier()
    {
        super(0, 0, 0, 0, null);
    }

    public StylusCubicBezier(double startX, double startY,
            double controlX1, double controlY1,
            double controlX2, double controlY2,
            double endX, double endY,
            String comment)
    {
        super(startX, startY, endX, endY, comment);
        this.controlX1 = controlX1;
        this.controlY1 = controlY1;
        this.controlX2 = controlX2;
        this.controlY2 = controlY2;
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
        CubicCurve curve = new CubicCurve();
        curve.setStartX(getStart().getX());
        curve.setStartY(getStart().getY());
        curve.setControlX1(controlX1);
        curve.setControlY1(controlY1);
        curve.setControlX2(controlX2);
        curve.setControlY2(controlY2);
        curve.setEndX(getEnd().getX());
        curve.setEndY(getEnd().getY());

        return renderShapeToGCodeNode(curve);
    }
}
