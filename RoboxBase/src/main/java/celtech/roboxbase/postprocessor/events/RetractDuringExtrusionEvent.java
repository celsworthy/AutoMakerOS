/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.roboxbase.postprocessor.events;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author Ian
 */
public class RetractDuringExtrusionEvent extends TravelEvent
{
    
    private double e;

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
     * @param e
     */
    public void setE(double e)
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
        
        String stringToReturn = "G1 X" + threeDPformatter.format(getX()) + " Y" + threeDPformatter.format(getY()) + " E" + fiveDPformatter.format(e);
        
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
