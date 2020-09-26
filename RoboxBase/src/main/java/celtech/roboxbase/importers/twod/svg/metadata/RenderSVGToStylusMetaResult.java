package celtech.roboxbase.importers.twod.svg.metadata;

import celtech.roboxbase.importers.twod.svg.metadata.dragknife.StylusMetaPart;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ianhudson
 */
public class RenderSVGToStylusMetaResult
{
    private List<StylusMetaPart> dragknifemetaparts = new ArrayList<>();
    private final double resultantX;
    private final double resultantY;

    public RenderSVGToStylusMetaResult(double resultantX, double resultantY, List<StylusMetaPart> dragknifemetaparts)
    {
        this.dragknifemetaparts = dragknifemetaparts;
        this.resultantX = resultantX;
        this.resultantY = resultantY;
    }

    public RenderSVGToStylusMetaResult(double resultantX, double resultantY, StylusMetaPart dragknifemetapart)
    {
        this.dragknifemetaparts = new ArrayList();
        this.dragknifemetaparts.add(dragknifemetapart);
        this.resultantX = resultantX;
        this.resultantY = resultantY;
    }

    public List<StylusMetaPart> getDragKnifeMetaParts()
    {
        return dragknifemetaparts;
    }

    public double getResultantX()
    {
        return resultantX;
    }

    public double getResultantY()
    {
        return resultantY;
    }
}
