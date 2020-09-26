package celtech.roboxbase.importers.twod.svg.metadata;

import celtech.roboxbase.importers.twod.svg.metadata.dragknife.DragKnifeMetaCut;

/**
 *
 * @author ianhudson
 */
public class SVGMetaLineHorizontal extends SVGMetaPart
{
    private final double x;
    private final boolean isAbsolute;

    public SVGMetaLineHorizontal(double x, boolean isAbsolute)
    {
        this.x = x;
        this.isAbsolute = isAbsolute;
    }

    public double getX()
    {
        return x;
    }

    public boolean isIsAbsolute()
    {
        return isAbsolute;
    }
    
    @Override
    public RenderSVGToStylusMetaResult renderToDragKnifeMetaParts(double currentX, double currentY)
    {
        double resultantX;

        String comment = " Line H " + ((isAbsolute == true) ? "ABS" : "REL");

        if (isAbsolute)
        {
            resultantX = x;
        } else
        {
            resultantX = currentX + x;
        }
        
        DragKnifeMetaCut cut = new DragKnifeMetaCut(currentX, currentY, resultantX, currentY, comment);
        
        RenderSVGToStylusMetaResult result = new RenderSVGToStylusMetaResult(resultantX, currentY, cut);
        
        return result;
    }
}
