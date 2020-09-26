/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.coreUI.components;

import celtech.roboxbase.utils.AxisSpecifier;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;

/**
 *
 * @author Ian
 */
public class JogButton extends Button
{

    private final ObjectProperty<AxisSpecifier> axis = new SimpleObjectProperty<>();
    private final FloatProperty distance = new SimpleFloatProperty();
    private final BooleanProperty useG1 = new SimpleBooleanProperty(false);
    private final IntegerProperty feedRate = new SimpleIntegerProperty(0);

    /**
     *
     * @param value
     */
    public void setAxis(AxisSpecifier value)
    {
        axis.set(value);
    }

    /**
     *
     * @return
     */
    public AxisSpecifier getAxis()
    {
        return axis.get();
    }

    /**
     *
     * @return
     */
    public ObjectProperty<AxisSpecifier> getAxisProperty()
    {
        return axis;
    }

    /**
     *
     * @param value
     */
    public void setDistance(float value)
    {
        distance.set(value);
    }

    /**
     *
     * @return
     */
    public float getDistance()
    {
        return distance.get();
    }

    /**
     *
     * @return
     */
    public FloatProperty getDistanceProperty()
    {
        return distance;
    }

    /**
     *
     * @param value
     */
    public void setUseG1(boolean value)
    {
        useG1.set(value);
    }

    /**
     *
     * @return
     */
    public boolean getUseG1()
    {
        return useG1.get();
    }

    /**
     *
     * @return
     */
    public BooleanProperty getUseG1Property()
    {
        return useG1;
    }

    /**
     *
     * @param value
     */
    public void setFeedRate(int value)
    {
        feedRate.set(value);
    }

    /**
     *
     * @return
     */
    public int getFeedRate()
    {
        return feedRate.get();
    }

    /**
     *
     * @return
     */
    public IntegerProperty getFeedRateProperty()
    {
        return feedRate;
    }

    /**
     *
     */
    public JogButton()
    {
        getStyleClass().add("jog-button");
        setPickOnBounds(false);
    }
}
