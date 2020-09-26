package celtech.roboxbase.utils.models;

import celtech.roboxbase.utils.twod.ShapeToWorldTransformer;
import javafx.scene.shape.Shape;

/**
 * This class is used to hold models and associated data that are used for slicing or postprocessing activities
 * @author ianhudson
 */
public class ShapeForProcessing
{
    private final Shape shape;
    private final ShapeToWorldTransformer shapeToWorldTransformer;

    public ShapeForProcessing(Shape shape, ShapeToWorldTransformer shapeToWorldTransformer)
    {
        this.shape = shape;
        this.shapeToWorldTransformer = shapeToWorldTransformer;
    }

    public ShapeToWorldTransformer getShapeToWorldTransformer()
    {
        return shapeToWorldTransformer;
    }

    public Shape getShape()
    {
        return shape;
    }
}
