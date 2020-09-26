package celtech.roboxbase.postprocessor.nouveau.nodes.providers;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author Ian
 */
public final class Extrusion implements Renderable
{

    private boolean isESet = false;
    private double e = 0;
    private boolean isDSet = false;
    private double d = 0;

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
        if (value == 0)
        {
            isESet = false;
            this.e = 0;
        } else
        {
            isESet = true;
            this.e = value;
        }
    }

    public boolean isEInUse()
    {
        return isESet;
    }

    public void eNotInUse()
    {
        this.e = 0;
        isESet = false;
    }

    public boolean isDInUse()
    {
        return isDSet;
    }

    public void dNotInUse()
    {
        this.d = 0;
        isDSet = false;
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
        if (value == 0)
        {
            isDSet = false;
            this.d = 0;
        } else
        {
            isDSet = true;
            this.d = value;
        }
    }

    public void extrudeUsingEOnly()
    {
        setE(getE() + getD());
        dNotInUse();
    }

    public void extrudeUsingDOnly()
    {
        setD(getE() + getD());
        eNotInUse();
    }

    @Override
    public String renderForOutput()
    {
        NumberFormat fiveDPformatter = DecimalFormat.getNumberInstance(Locale.UK);
        fiveDPformatter.setMaximumFractionDigits(5);
        fiveDPformatter.setMinimumFractionDigits(5);
        fiveDPformatter.setGroupingUsed(false);

        StringBuilder stringToReturn = new StringBuilder();

        if (isDSet)
        {
            stringToReturn.append('D');
            stringToReturn.append(fiveDPformatter.format(d));
        }

        if (isESet)
        {
            if (isDSet)
            {
                stringToReturn.append(' ');
            }
            stringToReturn.append('E');
            stringToReturn.append(fiveDPformatter.format(e));
        }

        return stringToReturn.toString().trim();
    }

    public Extrusion clone()
    {
        Extrusion newNode = new Extrusion();

        newNode.d = this.d;
        newNode.isDSet = this.isDSet;
        newNode.e = this.e;
        newNode.isESet = this.isESet;

        return newNode;
    }
}
