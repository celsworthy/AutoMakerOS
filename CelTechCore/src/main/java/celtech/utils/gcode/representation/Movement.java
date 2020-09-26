/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package celtech.utils.gcode.representation;

import javafx.geometry.Point3D;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class Movement
{
    private Point3D targetPosition = null;
    private int gCodeLineNumber = -1;

    /**
     *
     * @param segment
     * @param gcodeLineNumber
     */
    public Movement(Point3D segment, int gcodeLineNumber)
    {
        this.targetPosition = segment;
        this.gCodeLineNumber = gcodeLineNumber;
    }

    /**
     *
     * @return
     */
    public Point3D getTargetPosition()
    {
        return targetPosition;
    }

    /**
     *
     * @return
     */
    public int getGCodeLineNumber()
    {
        return gCodeLineNumber;
    }
    
    /**
     *
     * @return
     */
    @Override
    public String toString()
    {
        return targetPosition.toString() + " : " + gCodeLineNumber;
    }
}
