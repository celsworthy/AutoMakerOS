package celtech.roboxbase.importers.twod.svg;

import celtech.roboxbase.postprocessor.nouveau.nodes.GCodeEventNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.StylusLiftNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.StylusPlungeNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.StylusScribeNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.StylusSwivelNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.TravelNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.MovementProvider;
import java.util.ArrayList;
import java.util.List;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 *
 * @author ianhudson
 *
 */
public class DragKnifeCompensator
{

    private final Stenographer steno = StenographerFactory.getStenographer(DragKnifeCompensator.class.getName());

    public List<GCodeEventNode> doCompensation(List<GCodeEventNode> uncompensatedParts, double forwards_value)
    {
        List<GCodeEventNode> compensatedParts = new ArrayList();

        GCodeEventNode lastUncompensatedPart = null;
        GCodeEventNode lastCompensatedPart = null;
        Vector2D lastVector = null;

        for (GCodeEventNode uncompensatedPart : uncompensatedParts)
        {
            GCodeEventNode compensatedPart = null;

            if (uncompensatedPart instanceof TravelNode)
            {
                // Leave the travel alone
                compensatedPart = uncompensatedPart;

            } else if (uncompensatedPart instanceof StylusScribeNode)
            {
                StylusScribeNode uncompensatedStylusScribeNode = (StylusScribeNode) uncompensatedPart;

                if (uncompensatedStylusScribeNode.getMovement().toVector2D().equals(((MovementProvider) lastUncompensatedPart).getMovement().toVector2D()))
                {
                    steno.info("Discarding duplicate movement");
                } else
                {
                    //Shift along vector
                    Vector2D vectorForThisSegment = uncompensatedStylusScribeNode.getMovement().toVector2D().subtract(((MovementProvider) lastUncompensatedPart).getMovement().toVector2D());
                    double vectorMagnitude = Math.sqrt(Math.pow(vectorForThisSegment.getX(), 2.0) + Math.pow(vectorForThisSegment.getY(), 2.0));
                    if (vectorForThisSegment.equals(Vector2D.ZERO))
                    {
                        steno.error("Zero vector");
                    }
                    Vector2D resultant_norm = vectorForThisSegment.normalize();
                    Vector2D shiftVector = resultant_norm.scalarMultiply(forwards_value);

                    compensatedPart = new StylusScribeNode();
                    Vector2D newEnd = uncompensatedStylusScribeNode.getMovement().toVector2D().add(shiftVector);
                    ((MovementProvider) compensatedPart).getMovement().setX(newEnd.getX());
                    ((MovementProvider) compensatedPart).getMovement().setY(newEnd.getY());
                    compensatedPart.appendCommentText(" - shifted");

                    if (lastUncompensatedPart != null)
                    {
                        if (lastUncompensatedPart instanceof StylusScribeNode)
                        {
                            if (vectorMagnitude > forwards_value)
                            {
                                // We need to make an arc from the end of the last line to the start of this one
                                // The radius will be the offset and the arc centre will be the start of the uncompensated line
                                Vector2D arcCentre = ((MovementProvider) lastUncompensatedPart).getMovement().toVector2D();
                                compensatedParts.addAll(generateSwivel(arcCentre, vectorForThisSegment, lastVector, forwards_value));
                            } else
                            {
                                //Move the last segment
                                Vector2D newPosition = ((MovementProvider) lastCompensatedPart).getMovement().toVector2D().add(shiftVector);
                                ((MovementProvider) lastCompensatedPart).getMovement().setX(newPosition.getX());
                                ((MovementProvider) lastCompensatedPart).getMovement().setY(newPosition.getY());
                                lastCompensatedPart.appendCommentText(" - small vector moved last segment");
                            }
                        } else
                        {
                            //The last segment was a travel
                            //Move the travel to the new start
                            //Inject a 180 degree arc towards the start point
                            Vector2D originalStartOfCut = ((MovementProvider) lastUncompensatedPart).getMovement().toVector2D();
                            
                            Vector2D newStartOfCut = ((MovementProvider) lastCompensatedPart).getMovement().toVector2D().subtract(shiftVector);
                            ((MovementProvider) lastCompensatedPart).getMovement().setX(newStartOfCut.getX());
                            ((MovementProvider) lastCompensatedPart).getMovement().setY(newStartOfCut.getY());
                            lastCompensatedPart.appendCommentText(" - moved start of cut");

                            compensatedParts.addAll(generateSwivel(originalStartOfCut, vectorForThisSegment, vectorForThisSegment.negate(), forwards_value));
                        }
                    }
                    lastVector = vectorForThisSegment;
                }
            }

            if (compensatedPart != null)
            {
                compensatedParts.add(compensatedPart);
                lastCompensatedPart = compensatedPart;
                lastUncompensatedPart = uncompensatedPart;
            }
        }

        return addZMoves(compensatedParts);
    }

    enum StylusPosition
    {

        UNKNOWN,
        TRAVEL,
        CUT,
        SWIVEL
    }

    private List<GCodeEventNode> addZMoves(List<GCodeEventNode> parts)
    {
        StylusPosition position = StylusPosition.UNKNOWN;

        List<GCodeEventNode> partsWithZMoves = new ArrayList<>();

        for (GCodeEventNode part : parts)
        {
            if (part instanceof TravelNode && position != StylusPosition.TRAVEL)
            {
                partsWithZMoves.add(new StylusLiftNode(SVGConverterConfiguration.getInstance().getTravelHeight()));
                position = StylusPosition.TRAVEL;
            } else if (part instanceof StylusScribeNode && position != StylusPosition.CUT)
            {
                partsWithZMoves.add(new StylusPlungeNode(SVGConverterConfiguration.getInstance().getContactHeight()));
                position = StylusPosition.CUT;
            } else if (part instanceof StylusSwivelNode && position != StylusPosition.SWIVEL)
            {
                partsWithZMoves.add(new StylusPlungeNode(SVGConverterConfiguration.getInstance().getSwivelHeight()));
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
