package celtech.roboxbase.importers.twod.svg.metadata;

import javafx.scene.Node;

/**
 *
 * @author ianhudson
 */
public abstract class SVGMetaPart
{
    /**
     *
     * @param currentX
     * @param currentY
     * @return 
     */
    public abstract RenderSVGToStylusMetaResult renderToDragKnifeMetaParts(double currentX, double currentY);
}
