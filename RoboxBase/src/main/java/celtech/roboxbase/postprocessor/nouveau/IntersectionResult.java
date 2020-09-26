package celtech.roboxbase.postprocessor.nouveau;

import celtech.roboxbase.postprocessor.nouveau.nodes.GCodeEventNode;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 *
 * @author Ian
 */
public class IntersectionResult
{
    private final GCodeEventNode closestNode;
    private final Vector2D intersectionPoint;
    private final int nodeIndex;

    public IntersectionResult(GCodeEventNode closestNode, Vector2D intersectionPoint, int nodeIndex)
    {
        this.closestNode = closestNode;
        this.intersectionPoint = intersectionPoint;
        this.nodeIndex = nodeIndex;
    }

    public GCodeEventNode getClosestNode()
    {
        return closestNode;
    }

    public Vector2D getIntersectionPoint()
    {
        return intersectionPoint;
    }

    public int getNodeIndex()
    {
        return nodeIndex;
    }
}
