package celtech.roboxbase.postprocessor.nouveau;

import celtech.roboxbase.configuration.RoboxProfile;
import celtech.roboxbase.postprocessor.nouveau.nodes.GCodeEventNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.MovementProvider;
import celtech.roboxbase.utils.Math.MathUtils;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.geometry.euclidean.twod.Segment;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 *
 * @author Ian
 */
public class CloseUtilities
{

    private final float maxDistanceFromEndPoint;
    private final int maxNumberOfIntersectionsToConsider;

    public CloseUtilities(RoboxProfile settings, String headType)
    {
        maxNumberOfIntersectionsToConsider = settings.getSpecificIntSetting("numberOfPerimeters");
        maxDistanceFromEndPoint = settings.getSpecificFloatSetting("perimeterExtrusionWidth_mm")
                * 1.01f * maxNumberOfIntersectionsToConsider;
    }

    protected Optional<IntersectionResult> findClosestMovementNode(
            SearchSegment finalSegment,
            List<GCodeEventNode> inScopeEvents,
            boolean intersectOrthogonally
    )
    {
        GCodeEventNode closestNode = null;
        Vector2D intersectionPoint = null;
        Optional<IntersectionResult> result = Optional.empty();

        if (finalSegment != null)
        {
            // We can work out how to split this extrusion
            //Get an orthogonal to the extrusion we're considering
            Vector2D startPoint = finalSegment.getStartNode().getMovement().toVector2D();
            Vector2D endPoint = finalSegment.getEndNode().getMovement().toVector2D();
            // We want the orthogonal line to be closer to the specified end point rather than the prior point
            Vector2D vectorFromPriorToThis = endPoint.subtract(startPoint);
            Vector2D halfwayBetweenPriorAndThisPoint = startPoint.add(vectorFromPriorToThis.scalarMultiply(0.5));

            Segment segmentToIntersectWith = null;
            Vector2D segmentToIntersectWithMeasurementPoint = null;

            if (intersectOrthogonally)
            {
                segmentToIntersectWith = MathUtils.getOrthogonalLineToLinePoints(maxDistanceFromEndPoint, halfwayBetweenPriorAndThisPoint, endPoint);

                segmentToIntersectWithMeasurementPoint = MathUtils.findMidPoint(segmentToIntersectWith.getStart(),
                        segmentToIntersectWith.getEnd());
            } else
            {
                Vector2D normalisedVectorToEndOfExtrusion = endPoint.subtract(startPoint).normalize();
                Vector2D scaledVectorToEndOfExtrusion = normalisedVectorToEndOfExtrusion.scalarMultiply(maxDistanceFromEndPoint);

                Vector2D segmentEndPoint = endPoint.add(scaledVectorToEndOfExtrusion);

                Line intersectionLine = new Line(endPoint, segmentEndPoint, 1e-12);
                segmentToIntersectWith = new Segment(endPoint, segmentEndPoint, intersectionLine);

                segmentToIntersectWithMeasurementPoint = endPoint;
            }

            GCodeEventNode lastNodeConsidered = null;

            double closestDistanceSoFar = 999;

            Iterator<GCodeEventNode> inScopeEventIterator = inScopeEvents.iterator();

            while (inScopeEventIterator.hasNext())
            {
                GCodeEventNode inScopeEvent = inScopeEventIterator.next();

                if (inScopeEvent instanceof MovementProvider)
                {
                    MovementProvider movementProvider = (MovementProvider) inScopeEvent;
                    Vector2D extrusionPoint = movementProvider.getMovement().toVector2D();

                    if (lastNodeConsidered != null)
                    {
                        Vector2D lastPoint = ((MovementProvider) lastNodeConsidered).getMovement().toVector2D();
                        Segment segmentUnderConsideration = new Segment(lastPoint,
                                extrusionPoint,
                                new Line(lastPoint, extrusionPoint, 1e-12));

                        Vector2D tempIntersectionPoint = MathUtils.getSegmentIntersection(
                                segmentToIntersectWith, segmentUnderConsideration);

                        if (tempIntersectionPoint != null)
                        {
                            double distanceFromMidPoint = tempIntersectionPoint.distance(
                                    segmentToIntersectWithMeasurementPoint);

                            if (distanceFromMidPoint < closestDistanceSoFar)
                            {
                                //Which node was closest - the last one or this one?
                                if (tempIntersectionPoint.distance(lastPoint)
                                        < tempIntersectionPoint.distance(extrusionPoint)
                                        && lastNodeConsidered != finalSegment.getStartNode()
                                        && lastNodeConsidered != finalSegment.getEndNode())
                                {
                                    closestNode = lastNodeConsidered;
                                } else
                                {
                                    closestNode = inScopeEvent;
                                }
                                closestDistanceSoFar = distanceFromMidPoint;
                                intersectionPoint = tempIntersectionPoint;
                            }
                        }
                    }

                    lastNodeConsidered = inScopeEvent;
                }
            }
        }

        if (closestNode != null
                && intersectionPoint != null)
        {
            result = Optional.of(new IntersectionResult(closestNode, intersectionPoint, 0));
        }

        return result;
    }
}
