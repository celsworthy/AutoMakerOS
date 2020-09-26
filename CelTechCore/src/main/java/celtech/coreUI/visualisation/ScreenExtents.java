package celtech.coreUI.visualisation;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point2D;

/**
 *
 * @author Ian
 */
public class ScreenExtents
{

    public List<Edge> heightEdges = new ArrayList<>();
    public List<Edge> widthEdges = new ArrayList<>();
    public List<Edge> depthEdges = new ArrayList<>();
    public int minX;
    public int maxX;
    public int minY;
    public int maxY;

    @Override
    public String toString()
    {
        StringBuilder outputString = new StringBuilder();

        outputString.append("Height Edges");
        for (Edge edge : heightEdges)
        {
            outputString.append(edge.getFirstPoint().toString());
            outputString.append(',');
            outputString.append(edge.getSecondPoint().toString());
            outputString.append("\n");
        }
        outputString.append("Width Edges");
        for (Edge edge : widthEdges)
        {
            outputString.append(edge.getFirstPoint().toString());
            outputString.append(',');
            outputString.append(edge.getSecondPoint().toString());
            outputString.append("\n");
        }
        outputString.append("Depth Edges");
        for (Edge edge : depthEdges)
        {
            outputString.append(edge.getFirstPoint().toString());
            outputString.append(',');
            outputString.append(edge.getSecondPoint().toString());
            outputString.append("\n");
        }

        outputString.append("X - min:" + minX + " max:" + maxX);
        outputString.append("\n");
        outputString.append("Y - min:" + minY + " max:" + maxY);
        outputString.append("\n");
        return outputString.toString();
    }

    public void recalculateMaxMin()
    {
        int tempMinX = 9999999;
        int tempMaxX = 0;
        int tempMinY = 9999999;
        int tempMaxY = 0;

        for (Edge edge : heightEdges)
        {
            Point2D firstPoint = edge.getFirstPoint();
            Point2D secondPoint = edge.getSecondPoint();

            tempMaxX = Math.max(tempMaxX, (int) firstPoint.getX());
            tempMinX = Math.min(tempMinX, (int) firstPoint.getX());
            tempMaxY = Math.max(tempMaxY, (int) secondPoint.getY());
            tempMinY = Math.min(tempMinY, (int) secondPoint.getY());
        }

        for (Edge edge : widthEdges)
        {
            Point2D firstPoint = edge.getFirstPoint();
            Point2D secondPoint = edge.getSecondPoint();

            tempMaxX = Math.max(tempMaxX, (int) firstPoint.getX());
            tempMinX = Math.min(tempMinX, (int) firstPoint.getX());
            tempMaxY = Math.max(tempMaxY, (int) secondPoint.getY());
            tempMinY = Math.min(tempMinY, (int) secondPoint.getY());
        }

        for (Edge edge : depthEdges)
        {
            Point2D firstPoint = edge.getFirstPoint();
            Point2D secondPoint = edge.getSecondPoint();

            tempMaxX = Math.max(tempMaxX, (int) firstPoint.getX());
            tempMinX = Math.min(tempMinX, (int) firstPoint.getX());
            tempMaxY = Math.max(tempMaxY, (int) secondPoint.getY());
            tempMinY = Math.min(tempMinY, (int) secondPoint.getY());
        }

        minX = tempMinX;
        maxX = tempMaxX;
        minY = tempMinY;
        maxY = tempMaxY;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj != null && obj instanceof ScreenExtents)
        {
            ScreenExtents extentsToCompare = (ScreenExtents) obj;
            if (extentsToCompare.maxX == maxX
                    && extentsToCompare.maxY == maxY
                    && extentsToCompare.minX == minY
                    && extentsToCompare.minY == minY)
            {
                return true;
            }
        }
        return false;
    }
}
