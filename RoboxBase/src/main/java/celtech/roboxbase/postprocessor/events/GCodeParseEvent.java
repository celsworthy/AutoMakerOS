
package celtech.roboxbase.postprocessor.events;

/**
 *
 * @author Ian
 */
public abstract class GCodeParseEvent
{
    private String comment = "";
    private double feedRate = -1;
    private double length = 0;
    /**
     * Number of lines processed at the point of this event
     */
    private int linesSoFar = 0;

    /**
     *
     * @return
     */
    public String getComment()
    {
        return comment;
    }

    /**
     *
     * @param comment
     */
    public void setComment(String comment)
    {
        this.comment = comment;
    }

    /**
     *
     * @return
     */
    public double getFeedRate()
    {
        return feedRate;
    }

    /**
     *
     * @param feedRate
     */
    public void setFeedRate(double feedRate)
    {
        this.feedRate = feedRate;
    }

    /**
     *
     * @return
     */
    public double getLength()
    {
        return length;
    }

    /**
     *
     * @param length
     */
    public void setLength(double length)
    {
        this.length = length;
    }
    
    /**
     *
     * @return
     */
    public abstract String renderForOutput();

    /**
     * @return the linesSoFar
     */
    public int getLinesSoFar()
    {
        return linesSoFar;
    }

    /**
     * @param linesSoFar the linesSoFar to set
     */
    public void setLinesSoFar(int linesSoFar)
    {
        this.linesSoFar = linesSoFar;
    }
}
