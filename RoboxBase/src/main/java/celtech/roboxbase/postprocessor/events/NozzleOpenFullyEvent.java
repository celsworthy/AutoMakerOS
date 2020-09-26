package celtech.roboxbase.postprocessor.events;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author Ian
 */
public class NozzleOpenFullyEvent extends GCodeParseEvent
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

        String stringToReturn = "G1 B1.0";

        if (e != 0)
        {
            stringToReturn += " E" + fiveDPformatter.format(e);
        }

        if (d != 0)
        {
            stringToReturn += " D" + fiveDPformatter.format(d);
        }

        if (e != 0 || d != 0)
        {
            stringToReturn += " F" + fiveDPformatter.format(400f);
        }

        if (getComment() != null)
        {
            stringToReturn += " ; " + getComment();
        }

        return stringToReturn + "\n";
    }
}
