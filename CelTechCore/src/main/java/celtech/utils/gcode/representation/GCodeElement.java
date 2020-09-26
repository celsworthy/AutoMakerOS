/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.utils.gcode.representation;

import javafx.scene.shape.Shape;
import javafx.scene.shape.Shape3D;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class GCodeElement
{

    private Shape gcodeVisualRepresentation = null;
    private MovementType movementType = null;

    /**
     *
     * @param gcodeVisualRepresentation
     * @param movementType
     */
    public GCodeElement(Shape gcodeVisualRepresentation, MovementType movementType)
    {
        this.gcodeVisualRepresentation = gcodeVisualRepresentation;
        this.movementType = movementType;
    }

    /**
     *
     * @return
     */
    public Shape getGcodeVisualRepresentation()
    {
        return gcodeVisualRepresentation;
    }

    /**
     *
     * @return
     */
    public MovementType getMovementType()
    {
        return movementType;
    }
}
