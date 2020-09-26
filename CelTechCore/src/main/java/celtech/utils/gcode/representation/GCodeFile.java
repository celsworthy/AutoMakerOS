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
public class GCodeFile
{
    private ArrayList<Layer> layers = new ArrayList<>();
    private ExtrusionMode extrusionMode = ExtrusionMode.RELATIVE;
    private MovementMode movementMode = MovementMode.ABSOLUTE;
    
    /**
     *
     * @param layer
     */
    public void addLayer(Layer layer)
    {
        layers.add(layer);
    }
    
    /**
     *
     * @return
     */
    public ArrayList<Layer> getLayers()
    {
        return layers;
    }

    /**
     *
     * @return
     */
    public ExtrusionMode getExtrusionMode()
    {
        return extrusionMode;
    }

    /**
     *
     * @param extrusionMode
     */
    public void setExtrusionMode(ExtrusionMode extrusionMode)
    {
        this.extrusionMode = extrusionMode;
    }

    /**
     *
     * @return
     */
    public MovementMode getMovementMode()
    {
        return movementMode;
    }

    /**
     *
     * @param movementMode
     */
    public void setMovementMode(MovementMode movementMode)
    {
        this.movementMode = movementMode;
    }
    
    /**
     *
     * @return
     */
    @Override
    public String toString()
    {
        StringBuilder stringOutput = new StringBuilder();
        int layerNumber = 1;
        
        stringOutput.append("There are ");
        stringOutput.append(layers.size());
        stringOutput.append(" layers\n");
        
        for (Layer layer: layers)
        {
            stringOutput.append("Layer ");
            stringOutput.append(layerNumber);
            stringOutput.append("\n");
            stringOutput.append(layer.toString());
            layerNumber++;
        }
        
        return stringOutput.toString();
    }
}
