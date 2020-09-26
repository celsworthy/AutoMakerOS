package celtech.roboxbase.postprocessor.events;

/**
 *
 * @author Ian
 */
public class NozzleCloseFullyEvent extends GCodeParseEvent
{

    /**
     *
     * @return
     */
    @Override
    public String renderForOutput()
    {
        String stringToReturn = "G1 B0.0";

        if (getComment() != null)
        {
            stringToReturn += " ; " + getComment();
        }

        return stringToReturn + "\n";
    }
}
