
package celtech.roboxbase.postprocessor;

import celtech.roboxbase.postprocessor.events.GCodeParseEvent;

/**
 *
 * @author Ian
 */
public interface GCodeTranslationEventHandler
{    

    /**
     *
     * @param event
     * @throws NozzleCloseSettingsError
     */
    public void processEvent(GCodeParseEvent event) throws PostProcessingError;

    /**
     *
     * @param line
     */
    public void unableToParse(String line);
}
