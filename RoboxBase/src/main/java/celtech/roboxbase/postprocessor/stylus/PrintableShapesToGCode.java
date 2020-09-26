package celtech.roboxbase.postprocessor.stylus;

import celtech.roboxbase.configuration.datafileaccessors.HeadContainer;
import celtech.roboxbase.configuration.hardwarevariants.PrinterType;
import celtech.roboxbase.importers.twod.svg.SVGConverterConfiguration;
import celtech.roboxbase.importers.twod.svg.metadata.dragknife.PathHelper;
import celtech.roboxbase.postprocessor.nouveau.nodes.GCodeEventNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.StylusScribeNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.TravelNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.Movement;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.MovementProvider;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.Renderable;
import celtech.roboxbase.printerControl.comms.commands.GCodeMacros;
import celtech.roboxbase.printerControl.comms.commands.MacroLoadException;
import celtech.roboxbase.printerControl.model.Head;
import celtech.roboxbase.printerControl.model.Printer;
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
import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
    private static final Stenographer steno = StenographerFactory.getStenographer(PrintableShapesToGCode.class.getName());
    private static final double MINIMUM_OFFSET = 0.0001;
    private static final double X_SET_POS = 20.0;
    private static final double Y_SET_POS = 20.0;
    private static final double Z_SET_POS = 10.0;

    public static List<GCodeEventNode> parsePrintableShapes(PrintableShapes shapes)
    {
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
            float lastX = 0.0f;
            float lastY = 0.0f;
            float firstX = 0.0f;
            float firstY = 0.0f;
            boolean first = true;
            
            //steno.info("renderShapeToGCode");
            while (!pathIterator.isDone())
            {
                int elementType = pathIterator.currentSegment(pathData);

                switch (elementType)
                {
                    case PathIterator.SEG_MOVETO:
                        Point2D currentPoint_moveto = shapeToWorldTransformer.transformShapeToRealWorldCoordinates(pathData[0], pathData[1]);
                        gcodeEvents.add(createTravelNode("Travel to start of path segment",
                                SVGConverterConfiguration.getInstance().getTravelFeedrate(),
                                currentPoint_moveto.getX(),
                                currentPoint_moveto.getY()));
                        lastX = pathData[0];
                        lastY = pathData[1];
                        //steno.info(String.format("SEG_MOVETO (%.2f, %.2f)", currentPoint_moveto.getX(), currentPoint_moveto.getY()) );
                        break;
                    case PathIterator.SEG_LINETO:
                        Point2D currentPoint_lineto = shapeToWorldTransformer.transformShapeToRealWorldCoordinates(pathData[0], pathData[1]);
                        gcodeEvents.add(createStylusScribeNode("Straight cut",
                                SVGConverterConfiguration.getInstance().getCuttingFeedrate(),
                                currentPoint_lineto.getX(),
                                currentPoint_lineto.getY()));
                        if (first)
                        {
                            first = false;
                            firstX = lastX;
                            firstY = lastY;
                        }
                        lastX = pathData[0];
                        lastY = pathData[1];
                        //steno.info(String.format("SEG_LINETO (%.2f, %.2f)", currentPoint_lineto.getX(), currentPoint_lineto.getY()) );
                        break;
                    case PathIterator.SEG_QUADTO:
                        QuadCurve newQuadCurve = new QuadCurve();
                        newQuadCurve.setStartX(lastX);
                        newQuadCurve.setStartY(lastY);
                        newQuadCurve.setControlX(pathData[0]);
                        newQuadCurve.setControlY(pathData[1]);
                        newQuadCurve.setEndX(pathData[2]);
                        newQuadCurve.setEndY(pathData[3]);
                        List<GCodeEventNode> quadCurveParts = renderCurveToGCodeNode(newQuadCurve, shapeToWorldTransformer);
                        gcodeEvents.addAll(quadCurveParts);
                        if (first)
                        {
                            first = false;
                            firstX = lastX;
                            firstY = lastY;
                        }
                        lastX = pathData[2];
                        lastY = pathData[3];
                        //steno.info(String.format("SEG_QUADTO (%.2f, %.2f), (%.2f, %.2f), (%.2f, %.2f)",
                        //                         newQuadCurve.getStartX(), newQuadCurve.getStartY(),
                        //                         newQuadCurve.getControlX(), newQuadCurve.getControlY(),
                        //                         newQuadCurve.getEndX(), newQuadCurve.getEndY()));
                        break;
                    case PathIterator.SEG_CUBICTO:
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
                        if (first)
                        {
                            first = false;
                            firstX = lastX;
                            firstY = lastY;
                        }
                        lastX = pathData[4];
                        lastY = pathData[5];
                        //steno.info(String.format("SEG_CUBICTO (%.2f, %.2f), (%.2f, %.2f), (%.2f, %.2f), (%.2f, %.2f)",
                        //                         newCubicCurve.getStartX(), newCubicCurve.getStartY(),
                        //                         newCubicCurve.getControlX1(), newCubicCurve.getControlY1(),
                        //                         newCubicCurve.getControlX2(), newCubicCurve.getControlY2(),
                        //                         newCubicCurve.getEndX(), newCubicCurve.getEndY()));
                        break;
                    case PathIterator.SEG_CLOSE:
                        if (!first)
                        {
                            Point2D currentPoint_close = shapeToWorldTransformer.transformShapeToRealWorldCoordinates(firstX, firstY);
                            gcodeEvents.add(createStylusScribeNode("Close segment - straight cut",
                                    SVGConverterConfiguration.getInstance().getCuttingFeedrate(),
                                    currentPoint_close.getX(),
                                    currentPoint_close.getY()));
                            lastX = firstX;
                            lastY = firstY;
                            //steno.info(String.format("SEG_CLOSE (%.2f, %.2f)", currentPoint_close.getX(), currentPoint_close.getY()) );
                        }
                        else
                        {
                            //steno.info("SEG_CLOSE - empty");
                        }

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

    public static void writeGCodeToFile(String outputFilename, List<GCodeEventNode> gcodeNodes, String headTypeID, double xOffset, double yOffset, double zOffset, Optional<PrinterType> printerTypeOpt)
    {
        PrintWriter out = null;
        try
        {
            out = new PrintWriter(new BufferedWriter(new FileWriter(outputFilename)));

            //Add a macro header
            try
            {
                
                List<String> startMacro = GCodeMacros.getMacroContents("Stylus_Start",
                        printerTypeOpt, headTypeID, false, false, false);
                for (String macroLine : startMacro)
                {
                    out.println(macroLine);
                }
            } catch (MacroLoadException ex)
            {
                steno.exception("Unable to load stylus cut start macro.", ex);
            }
            
            if (abs(xOffset) > MINIMUM_OFFSET)
            {
                // Adjust x offset.
                out.println(String.format("G0 X %.2f", X_SET_POS + xOffset, Locale.UK));
                out.println(String.format("G92 X %.2f", X_SET_POS, Locale.UK));
            }
            if (abs(yOffset) > MINIMUM_OFFSET)
            {
                // Adjust y offset.
                out.println(String.format("G0 Y %.2f", Y_SET_POS + yOffset, Locale.UK));
                out.println(String.format("G92 Y %.2f", Y_SET_POS, Locale.UK));
            }
            if (abs(zOffset) > MINIMUM_OFFSET)
            {
                // Adjust Z offset.
                out.println(String.format("G0 Z %.2f", Z_SET_POS + zOffset, Locale.UK));
                out.println(String.format("G92 Z %.2f", Z_SET_POS, Locale.UK));
            }

            for (GCodeEventNode gcodeEventNode : gcodeNodes)
            {
                if (gcodeEventNode instanceof Renderable)
                {
                    out.println(((Renderable) gcodeEventNode).renderForOutput());
                }
            }
            
            // Raise the head.
            out.println(String.format("G0 Z %.2f", Z_SET_POS + zOffset, Locale.UK));
            
            if (abs(xOffset) > MINIMUM_OFFSET)
            {
                // Reset x offset.
                out.println(String.format("G0 X %.2f", X_SET_POS, Locale.UK));
                out.println(String.format("G92 X %.2f", X_SET_POS + xOffset, Locale.UK));
            }
            if (abs(yOffset) > MINIMUM_OFFSET)
            {
                // Reset y offset.
                out.println(String.format("G0 Y %.2f", Y_SET_POS, Locale.UK));
                out.println(String.format("G92 Y %.2f", Y_SET_POS + yOffset, Locale.UK));
            }
            if (abs(zOffset) > MINIMUM_OFFSET)
            {
                // Reset Z offset.
                out.println(String.format("G0 Z %.2f", Z_SET_POS, Locale.UK));
                out.println(String.format("G92 Z %.2f", Z_SET_POS + zOffset, Locale.UK));
            }

            //Add a macro footer
            try
            {
                List<String> startMacro = GCodeMacros.getMacroContents("Stylus_End",
                        printerTypeOpt, headTypeID, false, false, false);
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

    public static void offsetGCode(List<GCodeEventNode> gcodeNodes, double xOffset, double yOffset, double zOffset)
    {
        // Usually the GCode for shapes does not contain Z moves, which are added later. The zOffset
        // is here just for completeness.
        boolean hasXOffset = (abs(xOffset) > MINIMUM_OFFSET);
        boolean hasYOffset = (abs(yOffset) > MINIMUM_OFFSET);
        boolean hasZOffset = (abs(zOffset) > MINIMUM_OFFSET);
        
        if (hasXOffset || hasYOffset || hasZOffset)
        {
            for (GCodeEventNode gNode : gcodeNodes)
            {
                if (gNode instanceof MovementProvider)
                {
                    Movement m = ((MovementProvider)gNode).getMovement();
                    if (hasXOffset && m.isXSet())
                        m.setX(m.getX() + xOffset);
                    if (hasYOffset &&m.isYSet())
                        m.setY(m.getY() + yOffset);
                    if (hasZOffset && m.isZSet())
                        m.setZ(m.getZ() + zOffset);
                }
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
