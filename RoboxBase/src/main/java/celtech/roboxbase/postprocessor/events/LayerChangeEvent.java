package celtech.roboxbase.postprocessor.events;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author Ian
 */
public class LayerChangeEvent extends GCodeParseEvent
{
    private double z;

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
     * @param value
     */
    public void setZ(double value)
    {
        this.z = value;
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

        String stringToReturn = "G1 Z" + threeDPformatter.format(z);

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
