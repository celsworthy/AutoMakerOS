package celtech.roboxbase.postprocessor.stylus;

import celtech.roboxbase.configuration.hardwarevariants.PrinterType;
import celtech.roboxbase.importers.twod.svg.SVGConverterConfiguration;
import celtech.roboxbase.importers.twod.svg.metadata.dragknife.PathHelper;
import celtech.roboxbase.postprocessor.nouveau.nodes.GCodeEventNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.StylusScribeNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.TravelNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.Renderable;
import celtech.roboxbase.printerControl.comms.commands.GCodeMacros;
import celtech.roboxbase.printerControl.comms.commands.MacroLoadException;
import celtech.roboxbase.utils.models.PrintableShapes;
import celtech.roboxbase.utils.models.ShapeForProcessing;
import celtech.roboxbase.utils.twod.ShapeToWorldTransformer;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.geom.PathIterator;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.shape.ShapeHelper;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian
 */
public class PrintableShapesToGCode
{

    private static boolean isInContact = false;

    private static final Stenographer steno = StenographerFactory.getStenographer(PrintableShapesToGCode.class.getName());

    public static List<GCodeEventNode> parsePrintableShapes(PrintableShapes shapes)
    {
        isInContact = false;

        List<GCodeEventNode> gcodeEventNodes = new ArrayList<>();

        for (ShapeForProcessing shapeForProcessing : shapes.getShapesForProcessing())
        {
            gcodeEventNodes.addAll(renderShapeToGCode(shapeForProcessing));
        }

        return gcodeEventNodes;
    }

    private static List<GCodeEventNode> renderShapeToGCode(ShapeForProcessing shapeForProcessing)
    {
        List<GCodeEventNode> gcodeEvents = new ArrayList<>();

        Shape shapeToProcess = shapeForProcessing.getShape();
        ShapeToWorldTransformer shapeToWorldTransformer = shapeForProcessing.getShapeToWorldTransformer();

        if (shapeToProcess instanceof SVGPath)
        {
            SVGPath pathToProcess = (SVGPath) shapeForProcessing.getShape();
            final Path2D path2D = new Path2D(ShapeHelper.configShape(pathToProcess));
            final BaseTransform tx = NodeHelper.getLeafTransform(pathToProcess);

            PathIterator pathIterator = path2D.getPathIterator(tx, 0.01f);
            float[] pathData = new float[6];
            float lastX = 0, lastY = 0;

            while (!pathIterator.isDone())
            {
                int elementType = pathIterator.currentSegment(pathData);

                switch (elementType)
                {
                    case PathIterator.SEG_MOVETO:
                        steno.info("Got a SEG_MOVETO");
                        Point2D currentPoint_moveto = shapeToWorldTransformer.transformShapeToRealWorldCoordinates(pathData[0], pathData[1]);
                        gcodeEvents.add(createTravelNode("Travel to start of path segment",
                                SVGConverterConfiguration.getInstance().getTravelFeedrate(),
                                currentPoint_moveto.getX(),
                                currentPoint_moveto.getY()));
                        lastX = pathData[0];
                        lastY = pathData[1];
                        break;
                    case PathIterator.SEG_LINETO:
                        steno.info("Got a SEG_LINETO");
                        Point2D currentPoint_lineto = shapeToWorldTransformer.transformShapeToRealWorldCoordinates(pathData[0], pathData[1]);
                        gcodeEvents.add(createStylusScribeNode("Straight cut",
                                SVGConverterConfiguration.getInstance().getCuttingFeedrate(),
                                currentPoint_lineto.getX(),
                                currentPoint_lineto.getY()));
                        lastX = pathData[0];
                        lastY = pathData[1];
                        break;
                    case PathIterator.SEG_QUADTO:
                        steno.info("Got a SEG_QUADTO");
                        QuadCurve newQuadCurve = new QuadCurve();
                        newQuadCurve.setStartX(lastX);
                        newQuadCurve.setStartY(lastY);
                        newQuadCurve.setControlX(pathData[0]);
                        newQuadCurve.setControlY(pathData[1]);
                        newQuadCurve.setEndX(pathData[2]);
                        newQuadCurve.setEndY(pathData[3]);
                        List<GCodeEventNode> quadCurveParts = renderCurveToGCodeNode(newQuadCurve, shapeToWorldTransformer);
                        gcodeEvents.addAll(quadCurveParts);
                        lastX = pathData[2];
                        lastY = pathData[3];
                        break;
                    case PathIterator.SEG_CUBICTO:
                        steno.info("Got a SEG_CUBICTO");
                        CubicCurve newCubicCurve = new CubicCurve();
                        newCubicCurve.setStartX(lastX);
                        newCubicCurve.setStartY(lastY);
                        newCubicCurve.setControlX1(pathData[0]);
                        newCubicCurve.setControlY1(pathData[1]);
                        newCubicCurve.setControlX2(pathData[2]);
                        newCubicCurve.setControlY2(pathData[3]);
                        newCubicCurve.setEndX(pathData[4]);
                        newCubicCurve.setEndY(pathData[5]);
                        List<GCodeEventNode> cubicCurveParts = renderCurveToGCodeNode(newCubicCurve, shapeToWorldTransformer);
                        gcodeEvents.addAll(cubicCurveParts);
                        lastX = pathData[4];
                        lastY = pathData[5];
                        break;
                    case PathIterator.SEG_CLOSE:
                        steno.info("Got a SEG_CLOSE");
                        break;
                }
                pathIterator.next();
            }

        } else if (shapeToProcess instanceof Rectangle)
        {
            Bounds bounds = shapeToProcess.getBoundsInLocal();
            Point2D bottomLeft = shapeToWorldTransformer.transformShapeToRealWorldCoordinates((float) bounds.getMinX(), (float) bounds.getMinY());
            Point2D topRight = shapeToWorldTransformer.transformShapeToRealWorldCoordinates((float) bounds.getMaxX(), (float) bounds.getMaxY());

            gcodeEvents.add(createTravelNode("Travel to start of Rectangle",
                    SVGConverterConfiguration.getInstance().getTravelFeedrate(),
                    bottomLeft.getX(),
                    bottomLeft.getY()
            ));

            gcodeEvents.add(createStylusScribeNode("Cut 1",
                    SVGConverterConfiguration.getInstance().getCuttingFeedrate(),
                    bottomLeft.getX(),
                    topRight.getY()
            ));

            gcodeEvents.add(createStylusScribeNode("Cut 2",
                    SVGConverterConfiguration.getInstance().getCuttingFeedrate(),
                    topRight.getX(),
                    topRight.getY()
            ));

            gcodeEvents.add(createStylusScribeNode("Cut 3",
                    SVGConverterConfiguration.getInstance().getCuttingFeedrate(),
                    topRight.getX(),
                    bottomLeft.getY()
            ));

            gcodeEvents.add(createStylusScribeNode("Cut 4",
                    SVGConverterConfiguration.getInstance().getCuttingFeedrate(),
                    bottomLeft.getX(),
                    bottomLeft.getY()
            ));
        } else if (shapeToProcess instanceof Circle
                || shapeToProcess instanceof Arc)
        {
            List<GCodeEventNode> circleParts = renderCurveToGCodeNode(shapeToProcess, shapeToWorldTransformer);
            gcodeEvents.addAll(circleParts);
        } else
        {
            steno.warning("Unable to handle shape of type " + shapeToProcess.getClass().getName());
        }

        return gcodeEvents;
    }

    private static List<GCodeEventNode> renderCurveToGCodeNode(Shape shape, ShapeToWorldTransformer shapeToWorldTransformer)
    {
        return renderCurveToGCodeNode(shape, shapeToWorldTransformer, 100);
    }

    private static List<GCodeEventNode> renderCurveToGCodeNode(Shape shape, ShapeToWorldTransformer shapeToWorldTransformer, int numberOfSegmentsToCreate)
    {
        List<GCodeEventNode> gcodeNodes = new ArrayList<>();

        final Path2D path2D = new Path2D(ShapeHelper.configShape(shape));
        final BaseTransform tx = NodeHelper.getLeafTransform(shape);
        PathHelper pathHelper = new PathHelper(path2D, tx, 1.0);

        int numberOfSteps = numberOfSegmentsToCreate;
        for (int stepNum = 0; stepNum <= numberOfSteps; stepNum++)
        {
            double fraction = (double) stepNum / (double) numberOfSteps;
            Point2D position = pathHelper.getPosition2D(fraction, false);
            Point2D transformedPosition = shapeToWorldTransformer.transformShapeToRealWorldCoordinates((float) position.getX(), (float) position.getY());
            System.out.println("Input " + fraction + " X:" + position.getX() + " Y:" + position.getY());
            System.out.println("Transformed X:" + transformedPosition.getX() + " Y:" + transformedPosition.getY());

            String comment;
            if (stepNum == 0)
            {
                comment = "Move to start of curve";
                gcodeNodes.add(createTravelNode(comment,
                        SVGConverterConfiguration.getInstance().getCuttingFeedrate(),
                        transformedPosition.getX(),
                        transformedPosition.getY()));
            } else
            {
                comment = "Curve cut";
                gcodeNodes.add(createStylusScribeNode(comment,
                        SVGConverterConfiguration.getInstance().getCuttingFeedrate(),
                        transformedPosition.getX(),
                        transformedPosition.getY()));
            }
        }

        return gcodeNodes;
    }

    public static void writeGCodeToFile(String outputFilename, List<GCodeEventNode> gcodeNodes)
    {
        PrintWriter out = null;
        try
        {
            out = new PrintWriter(new BufferedWriter(new FileWriter(outputFilename)));

            //Add a macro header
            try
            {
                List<String> startMacro = GCodeMacros.getMacroContents("stylus_start",
                        Optional.<PrinterType>empty(), null, false, false, false);
                for (String macroLine : startMacro)
                {
                    out.println(macroLine);
                }
            } catch (MacroLoadException ex)
            {
                steno.exception("Unable to load stylus cut start macro.", ex);
            }

            for (GCodeEventNode gcodeEventNode : gcodeNodes)
            {
                if (gcodeEventNode instanceof Renderable)
                {
                    out.println(((Renderable) gcodeEventNode).renderForOutput());
                }
            }

            //Add a macro footer
            try
            {
                List<String> startMacro = GCodeMacros.getMacroContents("stylus_end",
                        Optional.<PrinterType>empty(), null, false, false, false);
                for (String macroLine : startMacro)
                {
                    out.println(macroLine);
                }
            } catch (MacroLoadException ex)
            {
                steno.exception("Unable to load stylus cut start macro.", ex);
            }
        } catch (IOException ex)
        {
            steno.error("Unable to output SVG GCode to " + outputFilename);
        } finally
        {
            if (out != null)
            {
                out.flush();
                out.close();
            }
        }
    }

    private static TravelNode createTravelNode(String comment, int travelFeedrate_mmPerMin, double x, double y)
    {
        TravelNode travel = new TravelNode();
        travel.setCommentText(comment);
        travel.getFeedrate().setFeedRate_mmPerMin(travelFeedrate_mmPerMin);
        travel.getMovement().setX(x);
        travel.getMovement().setY(y);
        return travel;
    }

    private static StylusScribeNode createStylusScribeNode(String comment, int travelFeedrate_mmPerMin, double x, double y)
    {
        StylusScribeNode travel = new StylusScribeNode();
        travel.setCommentText(comment);
        travel.getFeedrate().setFeedRate_mmPerMin(travelFeedrate_mmPerMin);
        travel.getMovement().setX(x);
        travel.getMovement().setY(y);
        return travel;
    }
}
