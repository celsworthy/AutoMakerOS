package celtech.roboxbase.importers.twod.svg.metadata;

import celtech.roboxbase.importers.twod.svg.BezierTools;
import celtech.roboxbase.importers.twod.svg.metadata.dragknife.DragKnifeMetaCut;
import celtech.roboxbase.importers.twod.svg.metadata.dragknife.StylusMetaPart;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 *
 * @author ianhudson
 */
public class SVGMetaCubicBezier extends SVGMetaPart
{

    private final double x1, y1, x2, y2, x3, y3;
    private final boolean isAbsolute;

    public SVGMetaCubicBezier(double x1,
            double y1,
            double x2,
            double y2,
            double x3,
            double y3,
            boolean isAbsolute)
    {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.x3 = x3;
        this.y3 = y3;
        this.isAbsolute = isAbsolute;
    }

    public SVGMetaCubicBezier(double x1,
            double y1,
            double x2,
            double y2)
    {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.isAbsolute = false;
        
        this.x3 = 0;
        this.y3 = 0;
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

    public double getX3()
    {
        return x3;
    }

    public double getY3()
    {
        return y3;
    }

    public boolean isIsAbsolute()
    {
        return isAbsolute;
    }

    @Override
    public RenderSVGToStylusMetaResult renderToDragKnifeMetaParts(double currentX, double currentY)
    {
        BezierTools bezierTools = new BezierTools();
        List<Vector2D> controlPoints = new ArrayList<>();

        double resultantX, resultantY;

        if (isAbsolute)
        {
            controlPoints.add(new Vector2D(currentX, currentY));
            controlPoints.add(new Vector2D(x1, y1));
            controlPoints.add(new Vector2D(x2, y2));
            controlPoints.add(new Vector2D(x3, y3));

            resultantX = x3;
            resultantY = y3;
        } else
        {
            controlPoints.add(new Vector2D(currentX, currentY));
            controlPoints.add(new Vector2D(currentX + x1, currentY + y1));
            controlPoints.add(new Vector2D(currentX + x2, currentY + y2));
            controlPoints.add(new Vector2D(currentX + x3, currentY + y3));

            resultantX = currentX + x3;
            resultantY = currentY + y3;
        }

        bezierTools.SetControlPoints(controlPoints);
        List<Vector2D> linePoints = bezierTools.GetDrawingPoints2();
        List<StylusMetaPart> metaparts = new ArrayList<>();
        
        double xInUse = currentX;
        double yInUse = currentY;

        for (int linePointCounter = 1; linePointCounter < linePoints.size(); linePointCounter++)
        {
            DragKnifeMetaCut cut = new DragKnifeMetaCut(xInUse, yInUse, linePoints.get(linePointCounter).getX(),
                    linePoints.get(linePointCounter).getY(), "Bezier point " + linePointCounter);
            metaparts.add(cut);
            xInUse = linePoints.get(linePointCounter).getX();
            yInUse = linePoints.get(linePointCounter).getY();
        }
        
        RenderSVGToStylusMetaResult result = new RenderSVGToStylusMetaResult(resultantX, resultantY, metaparts);
        return result;
    }
}
