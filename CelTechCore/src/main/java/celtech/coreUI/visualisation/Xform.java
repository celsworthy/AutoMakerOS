package celtech.coreUI.visualisation;

import javafx.scene.Group;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

/**
 *
 * @author Ian
 */
public class Xform extends Group
{

    /**
     *
     */
    public enum RotateOrder
    {

        /**
         *
         */
        XYZ,

        /**
         *
         */
        XZY,

        /**
         *
         */
        YXZ,

        /**
         *
         */
        YZX,

        /**
         *
         */
        ZXY,

        /**
         *
         */
        ZYX
    }

    /**
     *
     */
    public Translate t = new Translate();

    /**
     *
     */
    public Translate p = new Translate();

    /**
     *
     */
    public Translate ip = new Translate();

    /**
     *
     */
    public Rotate rx = new Rotate();

    
    {
        rx.setAxis(Rotate.X_AXIS);
    }

    /**
     *
     */
    public Rotate ry = new Rotate();

    
    {
        ry.setAxis(Rotate.Y_AXIS);
    }

    /**
     *
     */
    public Rotate rz = new Rotate();

    
    {
        rz.setAxis(Rotate.Z_AXIS);
    }

    /**
     *
     */
    public Scale s = new Scale();

    /**
     *
     */
    public Xform()
    {
        super();
        getTransforms().addAll(t, rz, ry, rx, s);
    }
    
    public Xform(RotateOrder rotateOrder, String id) {
        this(rotateOrder);
        setId(id);
    }

    /**
     *
     * @param rotateOrder
     */
    public Xform(RotateOrder rotateOrder)
    {
        super();
        // choose the order of rotations based on the rotateOrder
        switch (rotateOrder)
        {
            case XYZ:
                getTransforms().addAll(t, p, rz, ry, rx, s, ip);
                break;
            case XZY:
                getTransforms().addAll(t, p, ry, rz, rx, s, ip);
                break;
            case YXZ:
                getTransforms().addAll(t, p, rz, rx, ry, s, ip);
                break;
            case YZX:
                getTransforms().addAll(t, p, rx, rz, ry, s, ip);  // For Camera
                break;
            case ZXY:
                getTransforms().addAll(t, p, ry, rx, rz, s, ip);
                break;
            case ZYX:
                getTransforms().addAll(t, p, rx, ry, rz, s, ip);
                break;
        }
    }

    /**
     *
     * @param x
     * @param y
     * @param z
     */
    public void setTranslate(double x, double y, double z)
    {
        t.setX(x);
        t.setY(y);
        t.setZ(z);
    }

    /**
     *
     * @param x
     * @param y
     */
    public void setTranslate(double x, double y)
    {
        t.setX(x);
        t.setY(y);
    }

    // Cannot override these methods as they are final:
    // public void setTranslateX(double x) { t.setX(x); }
    // public void setTranslateY(double y) { t.setY(y); }
    // public void setTranslateZ(double z) { t.setZ(z); }
    // Use these methods instead:

    /**
     *
     * @param x
     */
        public void setTx(double x)
    {
        t.setX(x);
    }

    /**
     *
     * @param y
     */
    public void setTy(double y)
    {
        t.setY(y);
    }

    /**
     *
     * @param z
     */
    public void setTz(double z)
    {
        t.setZ(z);
    }

    /**
     *
     * @return
     */
    public double getTx()
    {
        return t.getTx();
    }

    /**
     *
     * @return
     */
    public double getTy()
    {
        return t.getTy();
    }

    /**
     *
     * @return
     */
    public double getTz()
    {
        return t.getTz();
    }

    /**
     *
     * @param x
     * @param y
     * @param z
     */
    public void setRotate(double x, double y, double z)
    {
        rx.setAngle(x);
        ry.setAngle(y);
        rz.setAngle(z);
    }

    /**
     *
     * @param x
     */
    public void setRotateX(double x)
    {
        rx.setAngle(x);
    }

    /**
     *
     * @param y
     */
    public void setRotateY(double y)
    {
        ry.setAngle(y);
    }

    /**
     *
     * @param z
     */
    public void setRotateZ(double z)
    {
        rz.setAngle(z);
    }

    /**
     *
     * @param x
     */
    public void setRx(double x)
    {
        rx.setAngle(x);
    }

    /**
     *
     * @param y
     */
    public void setRy(double y)
    {
        ry.setAngle(y);
    }

    /**
     *
     * @param z
     */
    public void setRz(double z)
    {
        rz.setAngle(z);
    }

    /**
     *
     * @return
     */
    public double getRotateX()
    {
        return rx.getAngle();
    }

    /**
     *
     * @return
     */
    public double getRotateY()
    {
        return ry.getAngle();
    }

    /**
     *
     * @return
     */
    public double getRotateZ()
    {
        return rz.getAngle();
    }

    /**
     *
     * @param scaleFactor
     */
    public void setScale(double scaleFactor)
    {
        s.setX(scaleFactor);
        s.setY(scaleFactor);
        s.setZ(scaleFactor);
    }

    /**
     *
     * @param x
     * @param y
     * @param z
     */
    public void setScale(double x, double y, double z)
    {
        s.setX(x);
        s.setY(y);
        s.setZ(z);
    }

    // Cannot override these methods as they are final:
    // public void setScaleX(double x) { s.setX(x); }
    // public void setScaleY(double y) { s.setY(y); }
    // public void setScaleZ(double z) { s.setZ(z); }
    // Use these methods instead:

    /**
     *
     * @param x
     */
        public void setSx(double x)
    {
        s.setX(x);
    }

    /**
     *
     * @param y
     */
    public void setSy(double y)
    {
        s.setY(y);
    }

    /**
     *
     * @param z
     */
    public void setSz(double z)
    {
        s.setZ(z);
    }

    /**
     *
     * @param x
     * @param y
     * @param z
     */
    public void setPivot(double x, double y, double z)
    {
        p.setX(x);
        p.setY(y);
        p.setZ(z);
        ip.setX(-x);
        ip.setY(-y);
        ip.setZ(-z);
    }
    
    /**
     *
     * @return
     */
    public Translate getPivot()
    {
        return p;
    }

    /**
     *
     */
    public void reset()
    {
        t.setX(0.0);
        t.setY(0.0);
        t.setZ(0.0);
        rx.setAngle(0.0);
        ry.setAngle(0.0);
        rz.setAngle(0.0);
        s.setX(1.0);
        s.setY(1.0);
        s.setZ(1.0);
        p.setX(0.0);
        p.setY(0.0);
        p.setZ(0.0);
        ip.setX(0.0);
        ip.setY(0.0);
        ip.setZ(0.0);
    }

    /**
     *
     */
    public void resetTSP()
    {
        t.setX(0.0);
        t.setY(0.0);
        t.setZ(0.0);
        s.setX(1.0);
        s.setY(1.0);
        s.setZ(1.0);
        p.setX(0.0);
        p.setY(0.0);
        p.setZ(0.0);
        ip.setX(0.0);
        ip.setY(0.0);
        ip.setZ(0.0);
    }

    /**
     *
     */
    public void debug()
    {
        System.out.println("t = ("
                + t.getX() + ", "
                + t.getY() + ", "
                + t.getZ() + ")  "
                + "r = ("
                + rx.getAngle() + ", "
                + ry.getAngle() + ", "
                + rz.getAngle() + ")  "
                + "s = ("
                + s.getX() + ", "
                + s.getY() + ", "
                + s.getZ() + ")  "
                + "p = ("
                + p.getX() + ", "
                + p.getY() + ", "
                + p.getZ() + ")  "
                + "ip = ("
                + ip.getX() + ", "
                + ip.getY() + ", "
                + ip.getZ() + ")");
    }

    @Override
    public String toString()
    {
        return "XForm t = ("
                + t.getX() + ", "
                + t.getY() + ", "
                + t.getZ() + ")  "
                + "r = ("
                + rx.getAngle() + ", "
                + ry.getAngle() + ", "
                + rz.getAngle() + ")  "
                + "s = ("
                + s.getX() + ", "
                + s.getY() + ", "
                + s.getZ() + ")  "
                + "p = ("
                + p.getX() + ", "
                + p.getY() + ", "
                + p.getZ() + ")  "
                + "ip = ("
                + ip.getX() + ", "
                + ip.getY() + ", "
                + ip.getZ() + ")";
    }
}
