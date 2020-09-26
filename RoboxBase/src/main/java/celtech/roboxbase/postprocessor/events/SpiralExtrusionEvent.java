package celtech.roboxbase.postprocessor.events;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author Ian
 */
public class SpiralExtrusionEvent extends GCodeParseEvent
{
    private float z;
    private float x;
    private float y;
    private float e;

    /**
     *
     * @return
     */
    public float getZ()
    {
        return z;
    }

    /**
     *
     * @param z
     */
    public void setZ(float z)
    {
        this.z = z;
    }

    /**
     *
     * @return
     */
    public float getX()
    {
        return x;
    }

    /**
     *
     * @param x
     */
    public void setX(float x)
    {
        this.x = x;
    }

    /**
     *
     * @return
     */
    public float getY()
    {
        return y;
    }

    /**
     *
     * @param y
     */
    public void setY(float y)
    {
        this.y = y;
    }

    /**
     *
     * @return
     */
    public float getE()
    {
        return e;
    }

    /**
     *
     * @param e
     */
    public void setE(float e)
    {
        this.e = e;
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
        threeDPformatter.setGroupingUsed(false);

        NumberFormat fiveDPformatter = DecimalFormat.getNumberInstance(Locale.UK);
        fiveDPformatter.setMaximumFractionDigits(5);
        fiveDPformatter.setGroupingUsed(false);

        String stringToReturn = "G1 X" + threeDPformatter.format(x) + " Y" + threeDPformatter.format(y) + " E" + fiveDPformatter.format(e);
        
        if (getFeedRate() > 0)
        {
            stringToReturn += " F" + threeDPformatter.format(getFeedRate());
        }
        
        if (getComment() != null)
        {
            stringToReturn += " ; " +  getComment();
        }
        
        return stringToReturn + "\n";
    }
}
