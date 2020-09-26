package celtech.roboxbase.postprocessor.nouveau.nodes.providers;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 *
 * @author Ian
 */
public final class Movement implements Renderable
{
    private boolean isXSet = false;
    private double x;

    private boolean isYSet = false;
    private double y;

    private boolean isZSet = false;
    private double z;

    public boolean isXSet()
    {
        return isXSet;
    }

    /**
     *
     * @return
     */
    public double getX()
    {
        return x;
    }

    /**
     *
     * @param x
     */
    public void setX(double x)
    {
        isXSet = true;
        this.x = x;
    }

    public void xNotInUse()
    {
        isXSet = false;
        x = 0.0;
    }

    public boolean isYSet()
    {
        return isYSet;
    }

    /**
     *
     * @return
     */
    public double getY()
    {
        return y;
    }

    /**
     *
     * @param y
     */
    public void setY(double y)
    {
        isYSet = true;
        this.y = y;
    }

    public void yNotInUse()
    {
        isYSet = false;
        y = 0.0;
    }

    public boolean isZSet()
    {
        return isZSet;
    }

    /**
     *
     * @return
     */
    public double getZ()
    {
        return z;
    }

    /**
     *
     * @param z
     */
    public void setZ(double z)
    {
        isZSet = true;
        this.z = z;
    }
    
    public void zNotInUse()
    {
        isZSet = false;
        z = 0.0;
    }

    /**
     *
     * @return
     */
    @Override
    public String renderForOutput()
    {
        NumberFormat threeDPformatter = DecimalFormat.getNumberInstance(Locale.UK);
        threeDPformatter.setMaximumFractionDigits(3);
        threeDPformatter.setMinimumFractionDigits(3);
        threeDPformatter.setGroupingUsed(false);

        StringBuilder stringToReturn = new StringBuilder();

        if (isXSet)
        {
            stringToReturn.append('X');
            stringToReturn.append(threeDPformatter.format(x));
            stringToReturn.append(' ');
        }

        if (isYSet)
        {
            stringToReturn.append('Y');
            stringToReturn.append(threeDPformatter.format(y));
            stringToReturn.append(' ');
        }

        if (isZSet)
        {
            stringToReturn.append('Z');
            stringToReturn.append(threeDPformatter.format(z));
            stringToReturn.append(' ');
        }

        return stringToReturn.toString().trim();
    }

    public Vector2D toVector2D()
    {
        return new Vector2D(x, y);
    }
    
    public Vector3D toVector3D()
    {
        return new Vector3D(x, y, z);
    }
    
    public Movement clone()
    {
        Movement newNode = new Movement();
        
        newNode.isXSet = this.isXSet;
        newNode.x = this.x;
        newNode.isYSet = this.isYSet;
        newNode.y = this.y;
        newNode.isZSet = this.isZSet;
        newNode.z = this.z;
        
        return newNode;
    }
}
