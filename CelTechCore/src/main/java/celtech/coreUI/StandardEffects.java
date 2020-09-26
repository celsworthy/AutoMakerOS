/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.coreUI;

import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.StrokeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

/**
 *
 * @author ianhudson
 */
public class StandardEffects
{

    private static Glow glowEffect = new Glow();
    private static DropShadow dropShadowEffect = new DropShadow();

    /**
     *
     * @return
     */
    public static Effect getMouseEnteredEffect()
    {
        dropShadowEffect.setWidth(30);
        dropShadowEffect.setHeight(30);
        dropShadowEffect.setRadius(13);
        dropShadowEffect.setSpread(0.27);
        return dropShadowEffect;
    }
    
    /**
     *
     * @return
     */
    public static Effect getDropShadowEffect()
    {
        return dropShadowEffect;
    }

    /**
     *
     * @return
     */
    public static Effect getButtonPressEffect()
    {
        return glowEffect;
    }

    /**
     *
     * @param node
     */
    public static void disableEffect(Node node)
    {
        node.setOpacity(0.5);
    }

    /**
     *
     * @param node
     */
    public static void enableEffect(Node node)
    {
        node.setOpacity(1);
    }
    
    /**
     *
     * @return
     */
    public static Effect defaultGlow()
    {
        return glowEffect;
    }

    /**
     *
     * @param throbColour
     * @param objectToThrob
     * @param throbDuration
     * @return
     */
    public static FillTransition getThrobFillEffect(Color throbColour, final Shape objectToThrob, double throbDuration)
    {
        final Color originalColour = (Color) objectToThrob.getFill();

        FillTransition transition = new FillTransition(Duration.seconds(throbDuration), objectToThrob, originalColour, throbColour);
        transition.setAutoReverse(true);
        transition.setCycleCount(StrokeTransition.INDEFINITE);

        return transition;
    }

    /**
     *
     * @param throbColour
     * @param objectToThrob
     * @param throbDuration
     * @return
     */
    public static StrokeTransition getThrobStrokeEffect(Color throbColour, final Shape objectToThrob, double throbDuration)
    {
        final Color originalColour = (Color) objectToThrob.getStroke();

        StrokeTransition transition = new StrokeTransition(Duration.seconds(throbDuration), objectToThrob, originalColour, throbColour);
        transition.setAutoReverse(true);
        transition.setCycleCount(StrokeTransition.INDEFINITE);

        return transition;
    }

    /**
     *
     * @param object
     */
    public static void setDefaultStroke(final Shape object)
    {
        object.setStroke(Color.WHITE);
    }
}
