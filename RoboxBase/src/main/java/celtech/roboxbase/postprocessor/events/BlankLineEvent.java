package celtech.roboxbase.postprocessor.events;

/**
 *
 * @author Ian
 */
public class BlankLineEvent extends GCodeParseEvent
{

    /**
     *
     * @return
     */
    @Override
    public String renderForOutput()
    {
        return "\n";
    }
}
