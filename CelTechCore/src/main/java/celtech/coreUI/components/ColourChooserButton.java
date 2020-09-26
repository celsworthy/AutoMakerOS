/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.coreUI.components;

import celtech.roboxbase.utils.AxisSpecifier;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Ian
 */
public class ColourChooserButton extends ToggleButton
{
    private final ObjectProperty<AxisSpecifier> axis = new SimpleObjectProperty<>();
    private final ObjectProperty<Color> displayColour = new SimpleObjectProperty<>(Color.WHITE);
    private final Rectangle colourSwatch = new Rectangle(30, 30);

    /**
     *
     * @param value
     */
    public void setDisplayColour(Color value)
    {
        displayColour.set(value);
    }

    /**
     *
     * @return
     */
    public Color getDisplayColour()
    {
        return displayColour.get();
    }

    /**
     *
     * @return
     */
    public ObjectProperty<Color> getDisplayColourProperty()
    {
        return displayColour;
    }

    /**
     *
     */
    public ColourChooserButton()
    {
        getStyleClass().add("printer-colour-button");
        setPickOnBounds(false);
        this.setGraphic(colourSwatch);
        colourSwatch.fillProperty().bind(displayColour);
    }
}
