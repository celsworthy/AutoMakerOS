package celtech.roboxbase.postprocessor.nouveau.nodes;

import celtech.roboxbase.postprocessor.nouveau.nodes.providers.Renderable;

/**
 *
 * @author Ian
 */
public class ToolReselectNode extends GCodeEventNode implements Renderable
{
    //For DM head
    //Tool 0 is extruder D
    //Tool 1 is extruder E
    private int toolNumber = -1;

    public int getToolNumber()
    {
        return toolNumber;
    }

    public void setToolNumber(int toolNumber)
    {
        this.toolNumber = toolNumber;
    }

    @Override
    public String renderForOutput()
    {
        String stringToReturn = "";

        stringToReturn += "T" + getToolNumber();
        stringToReturn += getCommentText();

        return stringToReturn;
    }
}
