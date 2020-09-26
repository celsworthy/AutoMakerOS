package celtech.roboxbase.importers.twod.svg.metadata;

import celtech.roboxbase.importers.twod.svg.metadata.dragknife.DragKnifeMetaCut;

/**
 *
 * @author ianhudson
 */
public class SVGMetaLineVertical extends SVGMetaPart
{

    private final double y;
    private final boolean isAbsolute;

    public SVGMetaLineVertical(double y, boolean isAbsolute)
    {
        this.y = y;
        this.isAbsolute = isAbsolute;
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
        double resultantY;

        String comment = " Line V " + ((isAbsolute == true) ? "ABS" : "REL");

        if (isAbsolute)
        {
            resultantY = y;
        } else
        {
            resultantY = currentY + y;
        }
        
        DragKnifeMetaCut cut = new DragKnifeMetaCut(currentX, currentY, currentX, resultantY, comment);
        RenderSVGToStylusMetaResult result = new RenderSVGToStylusMetaResult(currentX, resultantY, cut);
        return result;
    }
}
