package celtech.roboxbase.postprocessor.events;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author Ian
 */
public class NozzlePositionChangeEvent extends ExtrusionEvent
{

    private double b;
    private boolean noExtrusion = false;

    /**
     *
     * @return
     */
    public double getB()
    {
        return b;
    }

    /**
     *
     * @param b
     */
    public void setB(double b)
    {
        this.b = b;
    }

    /**
     *
     * @return
     */
    public boolean getNoExtrusionFlag()
    {
        return noExtrusion;
    }

    /*
     * This method indicates whether E values should be output or not
     */
    /**
     *
     * @param value
     */
    public void setNoExtrusionFlag(boolean value)
    {
        this.noExtrusion = value;
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

        String stringToReturn = "G1 X" + threeDPformatter.format(getX()) + " Y" + threeDPformatter.format(getY()) + " B" + threeDPformatter.format(b);

        if (noExtrusion == false)
        {
            stringToReturn += " E" + fiveDPformatter.format(getE());
        }

        if (getFeedRate() > 0)
        {
            stringToReturn += " F" + threeDPformatter.format(getFeedRate());
        }

        stringToReturn += " ; ->L" + getLength() + " ";

        if (noExtrusion != false)
        {
            stringToReturn += " ; ->E" + getE() + " ";
        }

        if (getComment() != null)
        {
            stringToReturn += " ; " + getComment();
        }

        return stringToReturn + "\n";
    }
}
