package celtech.coreUI.keycommands;

/**
 *
 * @author Ian
 */
public interface KeyCommandListener
{

    /**
     * Trigger should return true if the event was consumed
     * @param commandSequence
     * @param capturedParameter
     * @return
     */
    public boolean trigger(String commandSequence, String capturedParameter);
}
