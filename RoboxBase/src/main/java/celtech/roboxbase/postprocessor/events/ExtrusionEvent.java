/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.roboxbase.postprocessor.events;

import celtech.roboxbase.postprocessor.ExtrusionTask;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author Ian
 */
public class ExtrusionEvent extends MovementEvent
{

    private ExtrusionTask extrusionTask = null;
    private double e;
    private double d;
    private boolean dontOutputExtrusion = false;

    /**
     *
     * @return
     */
    public ExtrusionTask getExtrusionTask()
    {
        return extrusionTask;
    }

    /**
     *
     * @param extrusionTask
     */
    public void setExtrusionTask(ExtrusionTask extrusionTask)
    {
        this.extrusionTask = extrusionTask;
    }

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
    public double getD()
    {
        return d;
    }

    /**
     *
     * @param d
     */
    public void setD(double d)
    {
        this.d = d;
    }

    public void setDontOutputExtrusion(boolean value)
    {
        dontOutputExtrusion = value;
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

        String stringToReturn = "G1 X" + threeDPformatter.format(getX()) + " Y" + threeDPformatter.format(getY());

        if (!dontOutputExtrusion)
        {
            stringToReturn += " E" + fiveDPformatter.format(e) + " D" + fiveDPformatter.format(d);
        }

        if (getFeedRate() > 0)
        {
            stringToReturn += " F" + threeDPformatter.format(getFeedRate());
        }

        stringToReturn += " ; ->L" + getLength() + " ->E" + getE() + " ->D" + getD();

        if (dontOutputExtrusion)
        {
            stringToReturn += " - extrusion suppressed";
        }

        if (extrusionTask != null)
        {
            stringToReturn += "; " + extrusionTask.name();
        }

        if (getComment() != null)
        {
            stringToReturn += " ; " + getComment();
        }

        return stringToReturn + "\n";
    }
}
