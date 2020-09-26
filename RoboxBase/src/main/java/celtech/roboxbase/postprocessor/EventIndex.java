/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.roboxbase.postprocessor;

/**
 *
 * @author Ian
 */
class EventIndex
{
    private int index = 0;
    private EventType eventType;

    public EventIndex(EventType eventType, int index)
    {
        this.eventType = eventType;
        this.index = index;
    }

    public int getIndex()
    {
        return index;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }

    public EventType getEventType()
    {
        return eventType;
    }

    public void setEventType(EventType eventType)
    {
        this.eventType = eventType;
    }
}
