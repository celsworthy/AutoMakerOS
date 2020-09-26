package celtech.roboxbase.importers.twod.svg;

import celtech.roboxbase.importers.twod.svg.metadata.SVGMetaPart;
import celtech.roboxbase.importers.twod.svg.metadata.SVGMetaPolygon;
import java.util.List;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PointsHandler;

/**
 *
 * @author ianhudson
 */
public class PointParserThing implements PointsHandler
{

    private final List<SVGMetaPart> metaparts;
    private SVGMetaPolygon tempMetaPoly;

    public PointParserThing(List<SVGMetaPart> metaparts)
    {
        this.metaparts = metaparts;
    }

    @Override
    public void startPoints() throws ParseException
    {
        tempMetaPoly = new SVGMetaPolygon();
    }

    @Override
    public void point(float x, float y) throws ParseException
    {
        tempMetaPoly.addPoint(SVGConverterConfiguration.getInstance().getxPointCoefficient() * x,
                SVGConverterConfiguration.getInstance().getyPointCoefficient() * y);
    }

    @Override
    public void endPoints() throws ParseException
    {
        metaparts.add(tempMetaPoly);
        tempMetaPoly = new SVGMetaPolygon();
    }

}
