package celtech.roboxbase.postprocessor.events;

/**
 *
 * @author Ian
 */
public class GCodeEvent extends GCodeParseEvent
{

    private int gNumber;
    private String gString = null;

    /**
     *
     * @return
     */
    public int getGNumber()
    {
        return gNumber;
    }

    /**
     *
     * @param value
     */
    public void setGNumber(int value)
    {
        this.gNumber = value;
    }

    /**
     *
     * @return
     */
    public String getGString()
    {
        return gString;
    }

    /**
     *
     * @param value
     */
    public void setGString(String value)
    {
        this.gString = value;
    }

    /**
     *
     * @return
     */
    @Override
    public String renderForOutput()
    {
        String stringToReturn = "G" + getGNumber();
        
        if (gString != null)
        {
            stringToReturn += " " + gString;
        }
        
        if (getComment() != null)
        {
            stringToReturn += " ; " + getComment();
        }

        return stringToReturn + "\n";
    }
}
