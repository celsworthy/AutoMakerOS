/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.coreUI.controllers;

import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.Lighting;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class ButtonController
{

    /**
     *
     */
    protected Effect notAvailable = null;

    /**
     *
     */
    protected Effect availableOnly = null;

    /**
     *
     */
    protected DropShadow availableAndHover = null;

    /**
     *
     */
    protected InnerShadow pressed = null;

    /**
     *
     */
    protected Lighting selectedOnly = null;

    /**
     *
     */
    protected final double dimmedOpacity = .3;

    /**
     *
     */
    public ButtonController()
    {
        availableAndHover = new DropShadow();
        availableAndHover.setWidth(21);
        availableAndHover.setHeight(21);
        availableAndHover.setSpread(.4);

        pressed = new InnerShadow();
        pressed.setWidth(40);
        pressed.setHeight(40);
        pressed.setRadius(20);

        selectedOnly = new Lighting();
        selectedOnly.setDiffuseConstant(1.3);
        selectedOnly.setSpecularConstant(.6);
        selectedOnly.setSpecularExponent(20);
        selectedOnly.setSurfaceScale(3.6);
    }

    /**
     *
     * @param button
     */
    protected void makeButtonNotAvailable(Node button)
    {
//        button.setMouseTransparent(true);
//        button.setDisable(true);
        button.setOpacity(dimmedOpacity);
        button.setEffect(notAvailable);
    }

    /**
     *
     * @param button
     */
    protected void makeButtonAvailable(Node button)
    {
//        button.setDisable(false);
//        button.setMouseTransparent(false);
        button.setOpacity(1.0);
        button.setEffect(availableOnly);
    }

    /**
     *
     * @param button
     */
    protected void makeButtonSelected(Node button)
    {
//        button.setMouseTransparent(true);
//        button.setDisable(true);
        button.setOpacity(1.0);
        button.setEffect(selectedOnly);
    }

}
