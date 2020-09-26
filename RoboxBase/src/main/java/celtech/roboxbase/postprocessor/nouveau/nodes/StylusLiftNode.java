package celtech.roboxbase.postprocessor.nouveau.nodes;

import celtech.roboxbase.postprocessor.nouveau.nodes.providers.Renderable;
import java.util.Locale;

/**
 *
 * @author Ian
 */
public class StylusLiftNode extends GCodeEventNode implements Renderable
{
    private float liftValue = 0;

    public StylusLiftNode(float liftValue)
    {
        this.liftValue = liftValue;
    }

    public float getLiftValue()
    {
        return liftValue;
    }

    public void setLiftValue(float liftValue)
    {
        this.liftValue = liftValue;
    }
    
    @Override
    public String renderForOutput()
    {
        StringBuilder stringToReturn = new StringBuilder();

        stringToReturn.append("G0 ");
        stringToReturn.append(String.format("Z %.2f", liftValue, Locale.UK));
        stringToReturn.append(' ');
        stringToReturn.append(getCommentText());

        return stringToReturn.toString();
    }
}
