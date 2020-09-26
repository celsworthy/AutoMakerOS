package celtech.roboxbase.postprocessor;

import celtech.roboxbase.postprocessor.events.ExtrusionEvent;
import celtech.roboxbase.postprocessor.events.GCodeParseEvent;
import celtech.roboxbase.postprocessor.events.NozzleOpenFullyEvent;
import java.io.IOException;
import java.util.ArrayList;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian
 */
public class PostProcessingBuffer extends ArrayList<GCodeParseEvent>
{

    Stenographer steno = StenographerFactory.getStenographer(PostProcessingBuffer.class.getName());
    int indexOfFirstExtrusionEvent = -1;
    double eExtrusionVolume = 0;
    double dExtrusionVolume = 0;

    public void emptyBufferToOutput(GCodeOutputWriter outputWriter) throws IOException
    {
        for (GCodeParseEvent event : this)
        {
            outputWriter.writeOutput(event.renderForOutput());
        }
        outputWriter.flush();
        clear();
    }

    public void closeNozzle(String comment, GCodeOutputWriter outputWriter)
    {

    }

    /**
     * This method inserts a NozzleOpenFully event just before the first
     * ExtrusionEvent
     */
    void openNozzleFullyBeforeExtrusion()
    {
        if (extrusionEventsArePresent())
        {
            add(indexOfFirstExtrusionEvent, new NozzleOpenFullyEvent());
            indexOfFirstExtrusionEvent++;
        }
    }

    @Override
    public boolean add(GCodeParseEvent e)
    {
        boolean added = super.add(e);

        if (e instanceof ExtrusionEvent)
        {
            
            if (added && !extrusionEventsArePresent())
            {
                indexOfFirstExtrusionEvent = size() - 1;
            }
        }

        return added;
    }

    @Override
    public void clear()
    {
        super.clear();
        indexOfFirstExtrusionEvent = -1;
        eExtrusionVolume = 0;
        dExtrusionVolume = 0;
    }

    private boolean extrusionEventsArePresent()
    {
        return indexOfFirstExtrusionEvent >= 0;
    }
}
