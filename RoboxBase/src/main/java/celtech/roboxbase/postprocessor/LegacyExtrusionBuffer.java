package celtech.roboxbase.postprocessor;

import celtech.roboxbase.postprocessor.events.ExtrusionEvent;
import celtech.roboxbase.postprocessor.events.GCodeParseEvent;
import celtech.roboxbase.postprocessor.events.LayerChangeEvent;
import celtech.roboxbase.postprocessor.events.MCodeEvent;
import celtech.roboxbase.postprocessor.events.MovementEvent;
import java.util.ArrayList;

/**
 *
 * @author Ian
 */
public class LegacyExtrusionBuffer extends ArrayList<GCodeParseEvent>
{

    public boolean containsExtrusionEvents()
    {
        boolean foundExtrusionEvent = false;
        for (GCodeParseEvent event : this)
        {
            if (event instanceof ExtrusionEvent)
            {
                foundExtrusionEvent = true;
                break;
            }
        }
        return foundExtrusionEvent;
    }

    public int getNextExtrusionEventIndex(int startingIndex)
    {
        int indexOfEvent = -1;

        for (int index = startingIndex; index < this.size(); index++)
        {
            if (this.get(index) instanceof ExtrusionEvent)
            {
                indexOfEvent = index;
                break;
            }
        }
        return indexOfEvent;
    }

    public int getNextEventIndex(int startingIndex, Class<?> eventClass)
    {
        int indexOfEvent = -1;

        for (int index = startingIndex; index < this.size(); index++)
        {
            if (this.get(index).getClass() == eventClass)
            {
                indexOfEvent = index;
                break;
            }
        }
        return indexOfEvent;
    }

    public int getFinalExtrusionEventIndex()
    {
        return getPreviousExtrusionEventIndex(this.size());
    }

    public int getPreviousExtrusionEventIndex(int startingIndex)
    {
        int indexOfEvent = -1;

        if (startingIndex > 0)
        {
            for (int index = startingIndex - 1; index >= 0; index--)
            {
                if (this.get(index) instanceof ExtrusionEvent)
                {
                    indexOfEvent = index;
                    break;
                }
            }
        }
        return indexOfEvent;
    }

    public int getExtrusionEventIndexBackwards(int startingIndex)
    {
        int indexOfEvent = -1;

        if (startingIndex > 0)
        {
            for (int index = startingIndex; index >= 0; index--)
            {
                if (this.get(index) instanceof ExtrusionEvent)
                {
                    indexOfEvent = index;
                    break;
                }
            }
        }
        return indexOfEvent;
    }

    public int getPreviousMovementEventIndex(int startingIndex)
    {
        int indexOfEvent = -1;

        if (startingIndex > 0)
        {
            for (int index = startingIndex - 1; index >= 0; index--)
            {
                if (this.get(index) instanceof MovementEvent)
                {
                    indexOfEvent = index;
                    break;
                }
            }
        }
        return indexOfEvent;
    }

    public int getPreviousEventIndex(int startingIndex, Class<?> eventClass)
    {
        int indexOfEvent = -1;

        for (int index = startingIndex; index >= 0; index--)
        {
            if (this.get(index).getClass() == eventClass
                || this.get(index).getClass().getSuperclass() == eventClass)
            {
                indexOfEvent = index;
                break;
            }
        }
        return indexOfEvent;
    }

    public int getPreviousExtrusionTask(int startingIndex, ExtrusionTask task)
    {
        int lastValidEventBoundaryIndex = -1;

        for (int index = startingIndex; index >= 0; index--)
        {
            if (this.get(index).getClass() == ExtrusionEvent.class)
            {
                ExtrusionEvent extrusionEvent = (ExtrusionEvent) this.get(index);
                if (extrusionEvent.getExtrusionTask() == task)
                {
                    lastValidEventBoundaryIndex = index;
                    break;
                }
            }
        }

        return lastValidEventBoundaryIndex;
    }

    public int getPreviousExtrusionTaskThisLayerOnly(int startingIndex, ExtrusionTask task)
    {
        int lastValidEventBoundaryIndex = -1;

        for (int index = startingIndex; index >= 0; index--)
        {
            if (this.get(index).getClass() == ExtrusionEvent.class)
            {
                ExtrusionEvent extrusionEvent = (ExtrusionEvent) this.get(index);
                if (extrusionEvent.getExtrusionTask() == task)
                {
                    lastValidEventBoundaryIndex = index;
                    break;
                }
            }
            else if (this.get(index) instanceof LayerChangeEvent)
            {
                break;
            }
        }

        return lastValidEventBoundaryIndex;
    }

    public int getStartOfExtrusionEventBoundaryIndex(int startingIndex)
    {
        int lastValidExtrusionIndex = startingIndex;

        if (startingIndex > 0)
        {
            ExtrusionTask currentTask = ((ExtrusionEvent) this.get(startingIndex)).
                getExtrusionTask();

            for (int index = startingIndex; index >= 0; index--)
            {
                if (this.get(index).getClass() == ExtrusionEvent.class)
                {
                    ExtrusionEvent extrusionEvent = (ExtrusionEvent) this.get(index);
                    if (extrusionEvent.getExtrusionTask() == currentTask
                        || (currentTask == ExtrusionTask.ExternalPerimeter && extrusionEvent.
                        getExtrusionTask() == ExtrusionTask.Perimeter))
                    {
                        lastValidExtrusionIndex = index;
                    } else
                    {
                        break;
                    }
                }
            }
        }
        return lastValidExtrusionIndex;
    }

    protected void insertSubsequentLayerTemperatures()
    {
        MCodeEvent subsequentLayerNozzleTemp = new MCodeEvent();
        subsequentLayerNozzleTemp.setMNumber(104);
        subsequentLayerNozzleTemp.setComment(
            "take post layer 1 nozzle temperature from loaded reel - don't wait");
        add(subsequentLayerNozzleTemp);

        MCodeEvent subsequentLayerBedTemp = new MCodeEvent();
        subsequentLayerBedTemp.setMNumber(140);
        subsequentLayerBedTemp.setComment(
            "take post layer 1 bed temperature from loaded reel - don't wait");
        add(subsequentLayerBedTemp);
    }
}
