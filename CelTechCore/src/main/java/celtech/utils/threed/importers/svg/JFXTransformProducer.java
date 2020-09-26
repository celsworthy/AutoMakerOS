package celtech.utils.threed.importers.svg;

import javafx.scene.transform.Affine;
import java.io.Reader;
import java.text.ParseException;
import org.apache.batik.parser.TransformListHandler;
import org.apache.batik.parser.TransformListParser;

/**
 * This class provides an implementation of the PathHandler that initializes
 * an Affine transform from the value of a 'transform' attribute.
 *
 * @author Tony Aldhous
 * 
 */
public class JFXTransformProducer implements TransformListHandler {
    /**
     * The value of the current affine transform.
     */
    protected Affine affineTransform;

    /**
     * Utility method for creating an AffineTransform.
     * @param r The reader used to read the transform specification.
     */
    public static Affine createAffineTransform(Reader r)
        throws ParseException {
        TransformListParser p = new TransformListParser();
        JFXTransformProducer th = new JFXTransformProducer();

        p.setTransformListHandler(th);
        p.parse(r);

        return th.getAffineTransform();
    }

    /**
     * Utility method for creating an AffineTransform.
     * @param s The transform specification.
     */
    public static Affine createAffineTransform(String s)
        throws ParseException {
        TransformListParser p = new TransformListParser();
        JFXTransformProducer th = new JFXTransformProducer();

        p.setTransformListHandler(th);
        p.parse(s);

        return th.getAffineTransform();
    }

    /**
     * Returns the AffineTransform object initialized during the last parsing.
     * @return the transform or null if this handler has not been used by
     *         a parser.
     */
    public Affine getAffineTransform() {
        return affineTransform;
    }

    /**
     * Implements {@link TransformListHandler#startTransformList()}.
     */
    public void startTransformList() {
        affineTransform = new Affine();
    }

    /**
     * Implements {@link
     * TransformListHandler#matrix(float,float,float,float,float,float)}.
     */
    public void matrix(float mxx, float mxy, float myx, float myy, float tx, float ty)
    {
        affineTransform.append(mxx, mxy, tx, myx, myy, ty);
    }

    /**
     * Implements {@link TransformListHandler#rotate(float)}.
     */
    public void rotate(float theta)
    {
        affineTransform.appendRotation(theta);
    }

    /**
     * Implements {@link TransformListHandler#rotate(float,float,float)}.
     */
    public void rotate(float theta, float cx, float cy)
    {
        affineTransform.appendRotation(theta, cx, cy);
    }

    /**
     * Implements {@link TransformListHandler#translate(float)}.
     */
    public void translate(float tx)
    {
        affineTransform.appendTranslation(tx, 0.0f);
    }

    /**
     * Implements {@link TransformListHandler#translate(float,float)}.
     */
    public void translate(float tx, float ty)
    {
        affineTransform.appendTranslation(tx, ty);
    }

    /**
     * Implements {@link TransformListHandler#scale(float)}.
     */
    public void scale(float sx)
    {
        affineTransform.appendScale(sx, sx);
    }

    /**
     * Implements {@link TransformListHandler#scale(float,float)}.
     */
    public void scale(float sx, float sy)
    {
        affineTransform.appendScale(sx, sy);
    }

    /**
     * Implements {@link TransformListHandler#skewX(float)}.
     */
    public void skewX(float skx)
    {
        affineTransform.appendShear(skx, 0.0f);
    }

    /**
     * Implements {@link TransformListHandler#skewY(float)}.
     */
    public void skewY(float sky)
    {
        affineTransform.appendShear(0.0f, sky);
    }

    /**
     * Implements {@link TransformListHandler#endTransformList()}.
     */
    public void endTransformList()
    {
    }
}
