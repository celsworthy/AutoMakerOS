package celtech.roboxbase.utils.twod;

import javafx.geometry.Point2D;

/**
 *
 * @author ianhudson
 */
public interface ShapeToWorldTransformer
{
    public Point2D transformShapeToRealWorldCoordinates(float vertexX, float vertexY);
}
