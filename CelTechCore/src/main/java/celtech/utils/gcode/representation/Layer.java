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
public class Layer
{

    private float zHeight;
    private ArrayList<CompoundMovement> movements = new ArrayList<>();
    private int layerNumber = 0;

    /**
     *
     * @param movement
     */
    public void addMovement(CompoundMovement movement)
    {
        movements.add(movement);
    }

    /**
     *
     * @return
     */
    public ArrayList<CompoundMovement> getMovements()
    {
        return movements;
    }

    /**
     *
     * @param layerNumber
     */
    public void setLayerNumber(int layerNumber)
    {
        this.layerNumber = layerNumber;
    }

    /**
     *
     * @return
     */
    public int getLayerNumber()
    {
        return layerNumber;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString()
    {
        StringBuilder stringOut = new StringBuilder();

        stringOut.append("There are ");
        stringOut.append(movements.size());
        stringOut.append(" moves\n");

        for (CompoundMovement movement : movements)
        {
            stringOut.append(movement.toString());
        }

        stringOut.append("End of layer\n");

        return stringOut.toString();
    }
}
