package celtech.roboxbase.postprocessor.events;

/**
 *
 * @author Ian
 */
public class MCodeEvent extends GCodeParseEvent
{
    private int mNumber;
    private boolean sNumberPresent = false;
    private int sNumber;

    /**
     *
     * @return
     */
    public int getMNumber()
    {
        return mNumber;
    }

    /**
     *
     * @param value
     */
    public void setMNumber(int value)
    {
        this.mNumber = value;
    }

    /**
     *
     * @return
     */
    public int getSNumber()
    {
        return sNumber;
    }

    /**
     *
     * @param value
     */
    public void setSNumber(int value)
    {
        sNumberPresent = true;
        this.sNumber = value;
    }

    /**
     *
     * @return
     */
    @Override
    public String renderForOutput()
    {
        String stringToReturn = "M" + getMNumber();

        if (sNumberPresent)
        {
            stringToReturn += " S" + sNumber;
        }
        
        if (getComment() != null)
        {
            stringToReturn += " ; " + getComment();
        }

        return stringToReturn + "\n";
    }
}
