package celtech.roboxbase.importers.twod.svg;

import celtech.roboxbase.postprocessor.nouveau.nodes.GCodeEventNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.StylusLiftNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.StylusPlungeNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.StylusScribeNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.StylusSwivelNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.TravelNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.MovementProvider;
import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 *
 * @author ianhudson
 *
 */
public class DragKnifeCompensatorAlt
{

    private static final Stenographer steno = StenographerFactory.getStenographer(DragKnifeCompensatorAlt.class.getName());
    private static final double EPSILON = 0.0005;
    private static final double MINIMUM_COMPENSATION_ANGLE = 0.001; // 0.1 radians = 5.7 degrees.
    
    public List<GCodeEventNode> doCompensation(List<GCodeEventNode> uncompensatedParts, double dragRadius, Optional<Vector2D> initialDragKnifeOrientation)
    {
        List<GCodeEventNode> compensatedParts = new ArrayList();

        GCodeEventNode lastUncompensatedPart = null;
        GCodeEventNode lastCompensatedPart = null;
        // Initial orientation of drag knife assumed to be along positive y axis.
        // This should be set up in the GCode prolog.
        Vector2D currentDragKnifeOrientation = null;
        Vector2D currentShiftVector = null;
        if (initialDragKnifeOrientation.isPresent())
        {
            currentDragKnifeOrientation = initialDragKnifeOrientation.get();
            currentShiftVector = currentDragKnifeOrientation.scalarMultiply(dragRadius);
        }
        
        for (GCodeEventNode uncompensatedPart : uncompensatedParts)
        {
            GCodeEventNode compensatedPart = null;

            if (uncompensatedPart instanceof TravelNode)
            {
                // Leave the travel alone for the moment.
                compensatedPart = uncompensatedPart;
            } 
            else if (uncompensatedPart instanceof StylusScribeNode)
            {
                StylusScribeNode uncompensatedStylusScribeNode = (StylusScribeNode) uncompensatedPart;
                Vector2D uncompensatedEndPoint = uncompensatedStylusScribeNode.getMovement().toVector2D();
                Vector2D uncompensatedStartPoint = ((MovementProvider) lastUncompensatedPart).getMovement().toVector2D();
                
                Vector2D segmentVector = uncompensatedEndPoint.subtract(uncompensatedStartPoint);
                double segmentLength = segmentVector.getNorm();

                if (segmentLength < EPSILON)
                {
                    steno.info("Discarding zero distance movement");
                } 
                else
                {
                    //Shift along vector
                    //Vector2D resultant_norm = vectorForThisSegment.normalize();
                    Vector2D segmentDirection = segmentVector.scalarMultiply(1.0 / segmentLength);
                    Vector2D shiftVector = segmentDirection.scalarMultiply(dragRadius);

                    compensatedPart = new StylusScribeNode();
                    Vector2D newEnd = uncompensatedStylusScribeNode.getMovement().toVector2D().add(shiftVector);
                    ((MovementProvider) compensatedPart).getMovement().setX(newEnd.getX());
                    ((MovementProvider) compensatedPart).getMovement().setY(newEnd.getY());
                    compensatedPart.appendCommentText(" - shifted");
                    
                    if (lastUncompensatedPart instanceof TravelNode)
                    {
                        if (currentDragKnifeOrientation == null || currentShiftVector == null)
                        {
                            // The last segment was a travel and the current orientation of the drag knife is unknown.
                            // Move to a shift vectors length before the start of the segment. This will
                            // force a 180 degree arc towards the start point, which will move the drag knife tip
                            // to the start of the segment.
                            currentDragKnifeOrientation = segmentDirection.negate();
                            currentShiftVector = currentDragKnifeOrientation.scalarMultiply(dragRadius);
                        }
                        Vector2D shiftedStartOfCut = ((MovementProvider) lastCompensatedPart).getMovement().toVector2D().add(currentShiftVector);
                        ((MovementProvider) lastCompensatedPart).getMovement().setX(shiftedStartOfCut.getX());
                        ((MovementProvider) lastCompensatedPart).getMovement().setY(shiftedStartOfCut.getY());
                        lastCompensatedPart.appendCommentText(" - moved start of cut");
                    }
                    
                    double angle = Vector2D.angle(segmentDirection, currentDragKnifeOrientation);
                    if (abs(angle) > MINIMUM_COMPENSATION_ANGLE)
                    {
                        // The tip of the drag knife is at the end of the uncompensated segment. The
                        // tool point as at the end of the compensated segment. Move the tool point in
                        // an arc from the shift point on to the new segment to orient the drag knife
                        // along the new segment.
                        compensatedParts.addAll(generateSwivel(uncompensatedStartPoint, segmentVector, currentShiftVector, dragRadius));
                    }
                    currentDragKnifeOrientation = segmentDirection;
                    currentShiftVector = shiftVector;
                }
            }

            if (compensatedPart != null)
            {
                compensatedParts.add(compensatedPart);
                lastCompensatedPart = compensatedPart;
                lastUncompensatedPart = uncompensatedPart;
            }
        }

        return compensatedParts;
    }

    enum StylusPosition
    {

        UNKNOWN,
        TRAVEL,
        CUT,
        SWIVEL
    }

    public List<GCodeEventNode> addZMoves(List<GCodeEventNode> parts, double zOffset)
    {
        StylusPosition position = StylusPosition.UNKNOWN;

        List<GCodeEventNode> partsWithZMoves = new ArrayList<>();

        for (GCodeEventNode part : parts)
        {
            if (part instanceof TravelNode && position != StylusPosition.TRAVEL)
            {
                partsWithZMoves.add(new StylusLiftNode(SVGConverterConfiguration.getInstance().getTravelHeight() + (float)zOffset));
                position = StylusPosition.TRAVEL;
            } else if (part instanceof StylusScribeNode && position != StylusPosition.CUT)
            {
                partsWithZMoves.add(new StylusPlungeNode(SVGConverterConfiguration.getInstance().getContactHeight() + (float)zOffset));
                position = StylusPosition.CUT;
            } else if (part instanceof StylusSwivelNode && position != StylusPosition.SWIVEL)
            {
                partsWithZMoves.add(new StylusPlungeNode(SVGConverterConfiguration.getInstance().getSwivelHeight() + (float)zOffset));
                position = StylusPosition.SWIVEL;
            }

            partsWithZMoves.add(part);
        }

        return partsWithZMoves;
    }

    private double normaliseAngle(double angle)
    {
        double outputAngle = 0;
        //Make a +/- pi angle into a 0-2pi (clockwise) angle
        if (angle < 0)
        {
            outputAngle = angle + (Math.PI * 2);
        } else
        {
            outputAngle = angle;
        }

        return outputAngle;
    }

    private List<GCodeEventNode> generateSwivel(Vector2D arcCentre,
            Vector2D newVector,
            Vector2D lastVector,
            double bladeOffset)
    {
        List<GCodeEventNode> swivelEvents = new ArrayList<>();
        double thisSegmentAngle = Math.atan2(newVector.getY(), newVector.getX());
        double lastSegmentAngle = Math.atan2(lastVector.getY(), lastVector.getX());

        ShortestArc shortestArc = new ShortestArc(lastSegmentAngle, thisSegmentAngle);

        if (Math.abs(shortestArc.getAngularDifference()) > 0.3)
        {
            double arcPointAngle = shortestArc.getCurrentAngle();
            while (Math.abs(arcPointAngle - shortestArc.getTargetAngle()) >= Math.abs(shortestArc.getStepValue()))
            {
                arcPointAngle += shortestArc.getStepValue();
                double newX = arcCentre.getX() + Math.cos(arcPointAngle) * bladeOffset;
                double newY = arcCentre.getY() + Math.sin(arcPointAngle) * bladeOffset;
                StylusSwivelNode swivelCut = new StylusSwivelNode();
                swivelCut.setCommentText("Swivel");
                swivelCut.getMovement().setX(newX);
                swivelCut.getMovement().setY(newY);
                swivelEvents.add(swivelCut);
            }
        }
        return swivelEvents;
    }
}
