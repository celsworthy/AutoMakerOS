/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.coreUI.visualisation;

import celtech.configuration.ApplicationConfiguration;
import celtech.coreUI.StandardColours;
import celtech.coreUI.visualisation.modelDisplay.SelectionHighlighter;
import celtech.utils.gcode.representation.MovementType;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class ApplicationMaterials
{

    public static final Color roboxBlue = Color.rgb(38, 125, 216);
    private static final PhongMaterial defaultModelMaterial = new PhongMaterial(roboxBlue);
    private static final PhongMaterial selectedModelMaterial = new PhongMaterial(Color.LAWNGREEN);
    private static final PhongMaterial collidedModelMaterial = new PhongMaterial(Color.DARKORANGE);
    private static final PhongMaterial collidedSelectedModelMaterial = new PhongMaterial(Color.PERU);
    private static final PhongMaterial offBedModelMaterial = new PhongMaterial(Color.CRIMSON);

    private static PhongMaterial selectionBoxMaterial = null;

    //GCode-related materials
    private static final PhongMaterial extrusionMaterial = new PhongMaterial(Color.GREEN);
    private static final PhongMaterial retractMaterial = new PhongMaterial(Color.RED);
    private static final PhongMaterial unretractMaterial = new PhongMaterial(Color.BLUE);
    private static final PhongMaterial supportMaterial = new PhongMaterial(Color.BROWN);
    private static final PhongMaterial travelMaterial = new PhongMaterial(Color.LIGHTGREEN);
    public static final PhongMaterial pickedGCodeMaterial = new PhongMaterial(Color.GOLDENROD);

    /**
     *
     * @return
     */
    public static PhongMaterial getDefaultModelMaterial()
    {
        defaultModelMaterial.setSpecularColor(roboxBlue);
        defaultModelMaterial.setSpecularPower(1.0);
        return defaultModelMaterial;
    }

    /**
     *
     * @return
     */
    public static PhongMaterial getSelectedModelMaterial()
    {
        return selectedModelMaterial;
    }

    /**
     *
     * @return
     */
    public static PhongMaterial getCollidedModelMaterial()
    {
        return collidedModelMaterial;
    }

    /**
     *
     * @return
     */
    public static PhongMaterial getCollidedSelectedModelMaterial()
    {
        return collidedSelectedModelMaterial;
    }

    /**
     *
     * @return
     */
    public static PhongMaterial getOffBedModelMaterial()
    {
        return offBedModelMaterial;
    }

    /**
     *
     * @return
     */
    public static PhongMaterial getSelectionBoxMaterial()
    {
        if (selectionBoxMaterial == null)
        {
            selectionBoxMaterial = new PhongMaterial(StandardColours.SELECTION_HIGHLIGHTER_GREEN);
            Image illuminationMap = new Image(SelectionHighlighter.class.getResource(ApplicationConfiguration.imageResourcePath + "greenIlluminationMap.png").toExternalForm());
            selectionBoxMaterial.setSelfIlluminationMap(illuminationMap);
        }
        return selectionBoxMaterial;
    }

    /**
     *
     * @return
     */
    public static Material getExtrusionMaterial()
    {
        return extrusionMaterial;
    }

    /**
     *
     * @return
     */
    public static Material getRetractMaterial()
    {
        return retractMaterial;
    }

    /**
     *
     * @return
     */
    public static Material getUnretractMaterial()
    {
        return unretractMaterial;
    }

    /**
     *
     * @return
     */
    public static Material getSupportMaterial()
    {
        return supportMaterial;
    }

    /**
     *
     * @return
     */
    public static Material getTravelMaterial()
    {
        return travelMaterial;
    }

    /**
     *
     * @param movementType
     * @param selected
     * @return
     */
    public static Material getGCodeMaterial(MovementType movementType, boolean selected)
    {
        Material returnVal = extrusionMaterial;

        if (selected == false)
        {
            switch (movementType)
            {
                case EXTRUDE:
                    returnVal = extrusionMaterial;
                    break;
                case EXTRUDE_SUPPORT:
                    returnVal = supportMaterial;
                    break;
                case RETRACT:
                    returnVal = retractMaterial;
                    break;
                case TRAVEL:
                    returnVal = travelMaterial;
                    break;
                case UNRETRACT:
                    returnVal = unretractMaterial;
                    break;
                default:
                    break;
            }
        } else
        {
            switch (movementType)
            {
                default:
                    returnVal = pickedGCodeMaterial;
                    break;
            }
        }

        return returnVal;
    }
}
