package celtech.roboxbase.importers.twod.svg;

import celtech.roboxbase.importers.twod.svg.metadata.SVGClosePath;
import celtech.roboxbase.importers.twod.svg.metadata.SVGEndPath;
import celtech.roboxbase.importers.twod.svg.metadata.SVGMetaCubicBezier;
import celtech.roboxbase.importers.twod.svg.metadata.SVGMetaLine;
import celtech.roboxbase.importers.twod.svg.metadata.SVGMetaLineHorizontal;
import celtech.roboxbase.importers.twod.svg.metadata.SVGMetaLineVertical;
import celtech.roboxbase.importers.twod.svg.metadata.SVGMetaMove;
import celtech.roboxbase.importers.twod.svg.metadata.SVGMetaPart;
import celtech.roboxbase.importers.twod.svg.metadata.SVGMetaQuadraticBezier;
import celtech.roboxbase.importers.twod.svg.metadata.SVGStartPath;
import java.util.List;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PathHandler;

/**
 *
 * @author ianhudson
 */
public class PathParserThing implements PathHandler
{

    private final Stenographer steno = StenographerFactory.getStenographer(PathParserThing.class.getName());
    private final List<SVGMetaPart> metaparts;

    public PathParserThing(List<SVGMetaPart> metaparts)
    {
        this.metaparts = metaparts;
    }

    @Override
    public void startPath() throws ParseException
    {
        metaparts.add(new SVGStartPath());
    }

    @Override
    public void endPath() throws ParseException
    {
        metaparts.add(new SVGEndPath());
    }

    @Override
    public void movetoRel(float x, float y) throws ParseException
    {
        metaparts.add(new SVGMetaMove(SVGConverterConfiguration.getInstance().getxPointCoefficient() * x,
                SVGConverterConfiguration.getInstance().getyPointCoefficient() * y, false));
    }

    @Override
    public void movetoAbs(float x, float y) throws ParseException
    {
        metaparts.add(new SVGMetaMove(x, y, true));
    }

    @Override
    public void closePath() throws ParseException
    {
        metaparts.add(new SVGClosePath());
    }

    @Override
    public void linetoRel(float x, float y) throws ParseException
    {
        if (x == 0 && y == 0)
        {
            //Ignore this case
            steno.warning("Discarding relative line with zero length");
        } else
        {
            metaparts.add(new SVGMetaLine(x, y, false));
        }
    }

    @Override
    public void linetoAbs(float x, float y) throws ParseException
    {
        metaparts.add(new SVGMetaLine(x, y, true));
    }

    @Override
    public void linetoHorizontalRel(float x) throws ParseException
    {
        if (x == 0)
        {
            //Ignore this case
            steno.warning("Discarding relative line to horizontal with 0 length");
        } else
        {
            metaparts.add(new SVGMetaLineHorizontal(x, false));
        }
    }

    @Override
    public void linetoHorizontalAbs(float x) throws ParseException
    {
        metaparts.add(new SVGMetaLineHorizontal(x, true));
    }

    @Override
    public void linetoVerticalRel(float y) throws ParseException
    {
        if (y == 0)
        {
            //Ignore this case
            steno.warning("Discarding relative line to vertical with 0 length");
        } else
        {
            metaparts.add(new SVGMetaLineVertical(y, false));
        }
    }

    @Override
    public void linetoVerticalAbs(float y) throws ParseException
    {
        metaparts.add(new SVGMetaLineVertical(y, true));
    }

    @Override
    public void curvetoCubicRel(float x1, float y1, float x2, float y2, float x3, float y3) throws ParseException
    {
        if (x1 == 0 && y1 == 0
                && x2 == 0 && y2 == 0
                && x3 == 0 && y3 == 0)
        {
            //Ignore this case
            steno.warning("Discarding relative cubic bezier with zero components");
        } else
        {
            metaparts.add(new SVGMetaCubicBezier(x1, y1, x2, y2, x3, y3, false));
        }
    }

    @Override
    public void curvetoCubicAbs(float x1, float y1, float x2, float y2, float x3, float y3) throws ParseException
    {
        metaparts.add(new SVGMetaCubicBezier(x1, y1, x2, y2, x3, y3, true));
    }

    @Override
    public void curvetoCubicSmoothRel(float x1, float y1, float x2, float y2) throws ParseException
    {
        throw new RuntimeException("Curve to Quadratic Smooth Rel");
//        metaparts.add(new SVGMetaCubicBezier(x1, y1, x2, y2));
    }

    @Override
    public void curvetoCubicSmoothAbs(float x1, float y1, float x2, float y2) throws ParseException
    {
        throw new RuntimeException("Curve to Cubic Smooth Abs");
//        metaparts.add(new SVGMetaCubicBezier(x1, y1, x2, y2, true));
    }

    @Override
    public void curvetoQuadraticRel(float x1, float y1, float x2, float y2) throws ParseException
    {
        metaparts.add(new SVGMetaQuadraticBezier(x1, y1, x2, y2, false));
    }

    @Override
    public void curvetoQuadraticAbs(float x1, float y1, float x2, float y2) throws ParseException
    {
        metaparts.add(new SVGMetaQuadraticBezier(x1, y1, x2, y2, true));
    }

    @Override
    public void curvetoQuadraticSmoothRel(float x1, float y1) throws ParseException
    {
        throw new RuntimeException("Curve to Quadratic Smooth Rel");
//        metaparts.add(new SVGMetaUnhandled("Curve to Quadratic Smooth Rel"));
    }

    @Override
    public void curvetoQuadraticSmoothAbs(float x1, float y1) throws ParseException
    {
        throw new RuntimeException("Curve to Quadratic Smooth Abs");
//        metaparts.add(new SVGMetaUnhandled("Curve to Quadratic Smooth Abs"));
    }

    @Override
    public void arcRel(float f, float f1, float f2, boolean bln, boolean bln1, float f3, float f4) throws ParseException
    {
        throw new RuntimeException("Arc Rel");
//        metaparts.add(new SVGMetaArc(f, f, bln1));
    }

    @Override
    public void arcAbs(float f, float f1, float f2, boolean bln, boolean bln1, float f3, float f4) throws ParseException
    {
        throw new RuntimeException("Arc Abs");
//        metaparts.add(new SVGMetaUnhandled("Arc Abs"));
    }
}
