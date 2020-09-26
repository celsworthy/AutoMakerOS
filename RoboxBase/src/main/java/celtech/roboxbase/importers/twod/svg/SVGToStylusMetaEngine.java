package celtech.roboxbase.importers.twod.svg;

import celtech.roboxbase.importers.twod.svg.metadata.dragknife.StylusMetaPart;
import celtech.roboxbase.importers.twod.svg.metadata.RenderSVGToStylusMetaResult;
import celtech.roboxbase.importers.twod.svg.metadata.SVGClosePath;
import celtech.roboxbase.importers.twod.svg.metadata.SVGEndPath;
import celtech.roboxbase.importers.twod.svg.metadata.SVGMetaLine;
import celtech.roboxbase.importers.twod.svg.metadata.SVGMetaPart;
import celtech.roboxbase.importers.twod.svg.metadata.SVGStartPath;
import java.util.ArrayList;
import java.util.List;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author ianhudson
 */
public class SVGToStylusMetaEngine
{

    private final Stenographer steno = StenographerFactory.getStenographer(SVGToStylusMetaEngine.class.getName());

    public static List<StylusMetaPart> convertToStylusMetaParts(List<SVGMetaPart> metaparts)
    {
        List<StylusMetaPart> dragknifemetaparts = new ArrayList<>();

        double currentX = 0;
        double currentY = 0;
        dragknifemetaparts.clear();

        double startX = 0, startY = 0;
        RenderSVGToStylusMetaResult renderResult = null;

        for (SVGMetaPart part : metaparts)
        {
            if (part instanceof SVGStartPath)
            {
                startX = currentX;
                startY = currentY;
            } else if (part instanceof SVGClosePath)
            {
                SVGMetaLine closeLine = new SVGMetaLine(startX, startY, true);
                renderResult = closeLine.renderToDragKnifeMetaParts(currentX, currentY);
            } else if (!(part instanceof SVGEndPath))
            {
                renderResult = part.renderToDragKnifeMetaParts(currentX, currentY);
            }

            if (renderResult != null)
            {
                currentX = renderResult.getResultantX();
                currentY = renderResult.getResultantY();
                for (StylusMetaPart dragknifemetapart : renderResult.getDragKnifeMetaParts())
                {
                    dragknifemetaparts.add(dragknifemetapart);
                }

                renderResult = null;
            }
        }
        
        return dragknifemetaparts;
    }
}
