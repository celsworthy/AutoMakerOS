package celtech.roboxbase.postprocessor.nouveau.nodes.providers;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author Ian
 */
public class NozzlePosition implements Renderable
{

    private boolean isBSet = false;
    private double b;
    private boolean partialOpen = false;

    /**
     *
     * @return
     */
    public boolean isBSet()
    {
        return isBSet;
    }

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
        isBSet = true;
        if (b < 0.01)
        {
            b = 0;
        }
        this.b = b;
    }
    
    public void bNotInUse()
    {
        isBSet = false;
        this.b = 0;
    }

    public void setPartialOpen(boolean partialOpen)
    {
        this.partialOpen = partialOpen;
    }

    public boolean isPartialOpen()
    {
        return partialOpen;
    }
    
    @Override
    public String renderForOutput()
    {
        NumberFormat twoDPformatter = DecimalFormat.getNumberInstance(Locale.UK);
        twoDPformatter.setMaximumFractionDigits(2);
        twoDPformatter.setGroupingUsed(false);
        
        StringBuilder stringToReturn = new StringBuilder();

        if (isBSet)
        {
            stringToReturn.append("B");
            stringToReturn.append(twoDPformatter.format(b));
        }
        
        return stringToReturn.toString().trim();
    }
    
    public NozzlePosition clone()
    {
        NozzlePosition newNode = new NozzlePosition();
        newNode.isBSet = this.isBSet;
        newNode.b = this.b;
        return newNode;
    }
}
