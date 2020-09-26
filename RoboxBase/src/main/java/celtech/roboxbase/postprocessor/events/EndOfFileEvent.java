package celtech.roboxbase.postprocessor.events;

/**
 *
 * @author Ian
 */
public class EndOfFileEvent extends GCodeParseEvent
{

    /**
     *
     * @return
     */
    @Override
    public String renderForOutput()
    {
        return "; EOF\n";
    }
}
