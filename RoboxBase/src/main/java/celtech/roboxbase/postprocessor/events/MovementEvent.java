package celtech.roboxbase.postprocessor.events;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author Ian
 */
public class MovementEvent extends GCodeParseEvent
{

    private double x;
    private double y;

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
        this.x = x;
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
        this.y = y;
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

        String stringToReturn = "G1 X" + threeDPformatter.format(x) + " Y" + threeDPformatter.format(y);

        if (getFeedRate() > 0)
        {
            stringToReturn += " F" + threeDPformatter.format(getFeedRate());
        }

        stringToReturn += " ; ->" + getLength() + " ";

        if (getComment() != null)
        {
            stringToReturn += " ; " + getComment();
        }

        return stringToReturn + "\n";
    }
}
