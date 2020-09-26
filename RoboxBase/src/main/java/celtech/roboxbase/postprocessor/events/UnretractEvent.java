package celtech.roboxbase.postprocessor.events;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author Ian
 */
public class UnretractEvent extends GCodeParseEvent
{

    private double e = 0;
    private double d = 0;

    /**
     *
     * @return
     */
    public double getE()
    {
        return e;
    }

    /**
     *
     * @param value
     */
    public void setE(double value)
    {
        this.e = value;
    }

    /**
     *
     * @return
     */
    public double getD()
    {
        return d;
    }

    /**
     *
     * @param value
     */
    public void setD(double value)
    {
        this.d = value;
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

        String stringToReturn = "G1 E" + fiveDPformatter.format(e) + " D" + fiveDPformatter.format(d);

        if (getFeedRate() > 0)
        {
            stringToReturn += " F" + threeDPformatter.format(getFeedRate());
        }

        if (getComment() != null)
        {
            stringToReturn += " ; " + getComment();
        }

        return stringToReturn + "\n";
    }
}
