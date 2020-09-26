package celtech.coreUI.visualisation;

import javafx.geometry.Point2D;

/**
 *
 * @author Ian
 */
public class Edge
{
    private final Point2D firstPoint;
    private final Point2D secondPoint;

    public Edge(Point2D firstPoint, Point2D secondPoint)
    {
        this.firstPoint = firstPoint;
        this.secondPoint = secondPoint;
    }

    public Point2D getFirstPoint()
    {
        return firstPoint;
    }

    public Point2D getSecondPoint()
    {
        return secondPoint;
    }
}
