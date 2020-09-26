/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.utils.gcode.representation;

import java.util.ArrayList;

/**
 *
 * @author ianhudson
 */
public class CompoundMovement
{

    private MovementType type = null;
    private final ArrayList<Movement> segments = new ArrayList<>();
    private int startingGCodeLine = 0;
    private int endingGCodeLine = 0;

    /**
     *
     * @param type
     */
    public CompoundMovement(MovementType type)
    {
        this.type = type;
    }

    /**
     *
     * @return
     */
    public MovementType getMovementType()
    {
        return type;
    }

    /**
     *
     * @param segment
     */
    public void addMove(Movement segment)
    {
        segments.add(segment);
    }

    /**
     *
     * @return
     */
    public ArrayList<Movement> getSegments()
    {
        return segments;
    }

    /**
     *
     * @return
     */
    public int getStartingGCodeLine()
    {
        return startingGCodeLine;
    }

    /**
     *
     * @param startingGCodeLine
     */
    public void setStartingGCodeLine(int startingGCodeLine)
    {
        this.startingGCodeLine = startingGCodeLine;
    }

    /**
     *
     * @return
     */
    public int getEndingGCodeLine()
    {
        return endingGCodeLine;
    }

    /**
     *
     * @param endingGCodeLine
     */
    public void setEndingGCodeLine(int endingGCodeLine)
    {
        this.endingGCodeLine = endingGCodeLine;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString()
    {
        StringBuilder stringOut = new StringBuilder();

        stringOut.append("Movement type ");
        stringOut.append(type.name());
        stringOut.append("\n");

        for (Movement segment : segments)
        {
            stringOut.append(segment.toString());
            stringOut.append("\n");
        }

        stringOut.append("End of movement\n");

        return stringOut.toString();
    }
}
