/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.coreUI.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

/**
 * 
 * @author Ian
 */
public class InfoScreenIndicatorController implements Initializable
{

    @FXML
    private Group blueParts;

    private Color blueColour = Color.rgb(38, 166, 217);
    private Color whiteColour = Color.rgb(255, 255, 255);

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
    }

    /**
     *
     * @param selected
     */
    public void setSelected(boolean selected)
    {
        //TODO make this work with CSS
        if (selected)
        {
            for (Node node : blueParts.getChildren())
            {
                if (node instanceof SVGPath)
                {
                    ((SVGPath) node).setFill(whiteColour);
                }
            }
        } else
        {
            for (Node node : blueParts.getChildren())
            {
                if (node instanceof SVGPath)
                {
                    ((SVGPath) node).setFill(blueColour);
                }
            }
        }
    }

}
