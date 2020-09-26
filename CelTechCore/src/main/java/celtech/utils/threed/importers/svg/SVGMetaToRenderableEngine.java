package celtech.utils.threed.importers.svg;

import celtech.roboxbase.importers.twod.svg.metadata.SVGMetaPart;
import celtech.roboxbase.importers.twod.svg.metadata.SVGMetaPolygon;
import celtech.roboxbase.importers.twod.svg.metadata.SVGStartPath;
import java.util.List;
import javafx.scene.Group;
import javafx.scene.shape.Polygon;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 *
 * @author ianhudson
 */
public class SVGMetaToRenderableEngine
{

    public static Group renderSVG(List<SVGMetaPart> metaparts)
    {
        Group createdGroup = new Group();

        for (SVGMetaPart part : metaparts)
        {
            if (part instanceof SVGMetaPolygon)
            {
                Polygon polygon = new Polygon();
                List<Vector2D> points = ((SVGMetaPolygon) part).getPoints();
                for (Vector2D point : points)
                {
                    polygon.getPoints().add(point.getX());
                    polygon.getPoints().add(point.getY());
                }
                
                createdGroup.getChildren().add(polygon);
            }
            else if (part instanceof SVGStartPath)
            {
            }
        }

        return createdGroup;
    }
}
