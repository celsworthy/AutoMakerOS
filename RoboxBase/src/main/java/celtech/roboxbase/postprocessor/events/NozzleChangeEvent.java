package celtech.roboxbase.postprocessor.events;

/**
 *
 * @author Ian
 */
public class NozzleChangeEvent extends GCodeParseEvent
{

    private int nozzleNumber;

    /**
     *
     * @return
     */
    public int getNozzleNumber()
    {
        return nozzleNumber;
    }

    /**
     *
     * @param value
     */
    public void setNozzleNumber(int value)
    {
        this.nozzleNumber = value;
    }

    /**
     *
     * @return
     */
    @Override
    public String renderForOutput()
    {
        String stringToReturn = "T" + nozzleNumber;

        if (getComment() != null)
        {
            stringToReturn += " ; " + getComment();
        }

        return stringToReturn + "\n";
    }
}
