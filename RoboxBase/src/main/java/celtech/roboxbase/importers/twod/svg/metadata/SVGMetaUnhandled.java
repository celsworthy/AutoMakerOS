package celtech.roboxbase.importers.twod.svg.metadata;

import celtech.roboxbase.importers.twod.svg.metadata.dragknife.StylusMetaUnhandled;

/**
 *
 * @author ianhudson
 */
public class SVGMetaUnhandled extends SVGMetaPart
{
    private final String message;

    public SVGMetaUnhandled(String message)
    {
        this.message = message;
    }

    @Override
    public RenderSVGToStylusMetaResult renderToDragKnifeMetaParts(double currentX, double currentY)
    {
        StylusMetaUnhandled unhandled = new StylusMetaUnhandled(currentX, currentY, currentX, currentY, message);
        RenderSVGToStylusMetaResult result = new RenderSVGToStylusMetaResult(currentX, currentY, unhandled);
        
        return result;
    }
    
}
