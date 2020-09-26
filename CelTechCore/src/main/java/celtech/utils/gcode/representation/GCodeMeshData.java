/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package celtech.utils.gcode.representation;

import java.util.HashMap;
import javafx.scene.Group;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class GCodeMeshData
{
    private Group allParts = null;
    private HashMap<Integer, GCodeElement> referencedElements = null;
    private HashMap<Integer, Group> referencedLayers = null;

    /**
     *
     * @param allParts
     * @param referencedElements
     * @param referencedLayers
     */
    public GCodeMeshData(Group allParts, HashMap<Integer, GCodeElement> referencedElements, HashMap<Integer, Group> referencedLayers)
    {
        this.allParts = allParts;
        this.referencedElements = referencedElements;
        this.referencedLayers = referencedLayers;
    }

    /**
     *
     * @return
     */
    public Group getAllParts()
    {
        return allParts;
    }

    /**
     *
     * @return
     */
    public HashMap<Integer, GCodeElement> getReferencedElements()
    {
        return referencedElements;
    }
    
    /**
     *
     * @return
     */
    public HashMap<Integer, Group> getReferencedArrays()
    {
        return referencedLayers;
    }
}
