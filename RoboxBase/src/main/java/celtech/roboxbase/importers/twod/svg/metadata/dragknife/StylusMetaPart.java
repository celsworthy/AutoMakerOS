package celtech.roboxbase.importers.twod.svg.metadata.dragknife;

import celtech.roboxbase.importers.twod.svg.SVGConverterConfiguration;
import celtech.roboxbase.postprocessor.nouveau.nodes.GCodeEventNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.TravelNode;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.shape.ShapeHelper;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javafx.geometry.Point2D;
import javafx.scene.shape.Shape;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 *
 * @author ianhudson
 */
public abstract class StylusMetaPart
{

    private Vector2D start;
    private Vector2D end;
    private String comment;

    private final NumberFormat threeDPformatter;

    public StylusMetaPart(double startX, double startY, double endX, double endY, String comment)
    {
        start = new Vector2D(startX, startY);
        end = new Vector2D(endX, endY);

        threeDPformatter = DecimalFormat.getNumberInstance(Locale.UK);
        threeDPformatter.setMaximumFractionDigits(3);
        threeDPformatter.setGroupingUsed(false);

        this.comment = comment;
    }

    public void setStart(Vector2D start)
    {
        this.start = start;
    }

    public Vector2D getStart()
    {
        return start;
    }

    public void setEnd(Vector2D end)
    {
        this.end = end;
    }

    public Vector2D getEnd()
    {
        return end;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public String getComment()
    {
        return comment;
    }

    public abstract String renderToGCode();

    protected String generateXYMove(double xValue, double yValue, int feedrate, String comment)
    {
        String generatedOutput = "G1 X" + threeDPformatter.format(xValue)
                + " Y" + threeDPformatter.format(yValue)
                + " F" + feedrate
                + " ; " + comment;

        return generatedOutput;
    }

    protected String generateXMove(double xValue, int feedrate, String comment)
    {
        String generatedOutput = "G1 X" + threeDPformatter.format(xValue)
                + " F" + feedrate
                + " ; " + comment;

        return generatedOutput;
    }

    protected String generateYMove(double yValue, int feedrate, String comment)
    {
        String generatedOutput = "G1 Y" + threeDPformatter.format(yValue)
                + " F" + SVGConverterConfiguration.getInstance().getTravelFeedrate()
                + " ; " + comment;

        return generatedOutput;
    }

    protected String generatePlunge(String comment)
    {
        String generatedOutput = "G0 Z" + SVGConverterConfiguration.getInstance().getPlungeDepth()
                + " ;Plunge"
                + " ; " + comment;

        return generatedOutput;
    }

    protected String generateLift(String comment)
    {
        String generatedOutput = "G0 Z10;Lift"
                + " ; " + comment;

        return generatedOutput;
    }

    protected List<GCodeEventNode> renderShapeToGCodeNode(Shape shape)
    {
        List<GCodeEventNode> gcodeNodes = new ArrayList<>();

        final Path2D path2D = new Path2D(ShapeHelper.configShape(shape));
        final BaseTransform tx = NodeHelper.getLeafTransform(shape);
        PathHelper pathHelper = new PathHelper(path2D, tx, 1.0);

        int numberOfSteps = 10;
        for (int stepNum = 0; stepNum <= numberOfSteps; stepNum++)
        {
            double fraction = (double) stepNum / (double) numberOfSteps;
            Point2D position = pathHelper.getPosition2D(fraction, false);
            System.out.println("Input " + fraction + " X:" + position.getX() + " Y:" + position.getY());

            TravelNode newTravel = new TravelNode();
            newTravel.getMovement().setX(position.getX());
            newTravel.getMovement().setY(position.getY());
            newTravel.getFeedrate().setFeedRate_mmPerMin(SVGConverterConfiguration.getInstance().getCuttingFeedrate());
            gcodeNodes.add(newTravel);
        }

        return gcodeNodes;
    }

    public abstract List<GCodeEventNode> renderToGCodeNode();

}
