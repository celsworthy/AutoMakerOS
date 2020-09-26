package celtech.roboxbase.importers.twod.svg.metadata;

import celtech.roboxbase.importers.twod.svg.BezierTools;
import celtech.roboxbase.importers.twod.svg.metadata.dragknife.DragKnifeMetaCut;
import celtech.roboxbase.importers.twod.svg.metadata.dragknife.DragKnifeMetaTravel;
import celtech.roboxbase.importers.twod.svg.metadata.dragknife.StylusMetaPart;
import celtech.roboxbase.importers.twod.svg.metadata.dragknife.StylusQuadraticBezier;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 *
 * @author ianhudson
 */
public class SVGMetaQuadraticBezier extends SVGMetaPart
{

    private final double x1, y1, x2, y2;
    private final boolean isAbsolute;

    public SVGMetaQuadraticBezier(double x1,
            double y1,
            double x2,
            double y2,
            boolean isAbsolute)
    {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.isAbsolute = isAbsolute;
    }

    public SVGMetaQuadraticBezier(double x1,
            double y1,
            double x2,
            double y2)
    {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.isAbsolute = false;        
    }

    public double getX1()
    {
        return x1;
    }

    public double getY1()
    {
        return y1;
    }

    public double getX2()
    {
        return x2;
    }

    public double getY2()
    {
        return y2;
    }

    public boolean isIsAbsolute()
    {
        return isAbsolute;
    }

    @Override
    public RenderSVGToStylusMetaResult renderToDragKnifeMetaParts(double currentX, double currentY)
    {
//                double resultantX, resultantY;
//
//        String comment = " Move " + ((isAbsolute == true) ? "ABS" : "REL");
//
//        if (isAbsolute)
//        {
//            resultantX = x;
//            resultantY = y;
//        } else
//        {
//            resultantX = currentX + x;
//            resultantY = currentY + y;
//        }
//        
//        StylusQuadraticBezier curve = new StylusQuadraticBezier(currentX, currentY, resultantX, resultantY, comment);
//
//        RenderSVGToStylusMetaResult result = new RenderSVGToStylusMetaResult(resultantX, resultantY, travel);
//        return result;
//        
//        List<
//        RenderSVGToStylusMetaResult result = new RenderSVGToStylusMetaResult(resultantX, resultantY, metaparts);
//        return result;
        return null;
    }
}
