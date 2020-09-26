package celtech.roboxbase.importers.twod.svg.metadata;

import celtech.roboxbase.importers.twod.svg.metadata.dragknife.DragKnifeMetaCut;

/**
 *
 * @author ianhudson
 */
public class SVGMetaArc extends SVGMetaPart
{
    //rx ry x-axis-rotation large-arc-flag sweep-flag dx dy

    private final double rx, ry, x_axis_rotation, x, y;
    private final boolean largeArc, sweep;
    private final boolean isAbsolute;

    public SVGMetaArc(double rx,
            double ry,
            double x_axis_rotation,
            boolean largeArc,
            boolean sweep,
            double x,
            double y,
            boolean isAbsolute)
    {
        this.rx = rx;
        this.ry = ry;
        this.x_axis_rotation = x_axis_rotation;
        this.largeArc = largeArc;
        this.sweep = sweep;
        this.x = x;
        this.y = y;
        this.isAbsolute = isAbsolute;
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public boolean isIsAbsolute()
    {
        return isAbsolute;
    }

    @Override
    public RenderSVGToStylusMetaResult renderToDragKnifeMetaParts(double currentX, double currentY)
    {
        double resultantX, resultantY;

        String comment = " Arc " + ((isAbsolute == true) ? "ABS" : "REL");

        if (isAbsolute)
        {
            resultantX = x;
            resultantY = y;
        } else
        {
            resultantX = currentX + x;
            resultantY = currentY + y;
        }
        
        
        DragKnifeMetaCut cut = new DragKnifeMetaCut(currentX, currentY, resultantX, resultantY, comment);

        RenderSVGToStylusMetaResult result = new RenderSVGToStylusMetaResult(resultantX, resultantY, cut);
        return result;
    }
}
