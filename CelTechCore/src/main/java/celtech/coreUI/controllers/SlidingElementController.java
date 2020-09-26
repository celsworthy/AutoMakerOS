/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.coreUI.controllers;

import celtech.coreUI.components.SlidingComponentDirection;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class SlidingElementController
{

    private Animation hideSidebar = null;
    private Animation showSidebar = null;
    private boolean hidden = false;
    private final double minimumToShow = 0.1;
    private final double maximumToShow = 1.0;
    private boolean slidIn = false;
    private SlidingComponentDirection directionToSlide = SlidingComponentDirection.IN_FROM_RIGHT;
    private double panelWidth = 0;
    private double panelHeight = 0;
    private double panelLayoutMinX = 0;
    private double panelLayoutMinY = 0;
    private final Rectangle clippingRectangle = new Rectangle();
    private Pane paneToSlide = null;

    /**
     *
     */
    public SlidingElementController()
    {
        hideSidebar = new Transition()
        {

            
            {
                setCycleDuration(Duration.millis(250));
            }

            @Override
            public void interpolate(double frac)
            {
                slideMenuPanel(1.0 - frac);
            }
        };

        // create an animation to show a sidebar.
        showSidebar = new Transition()
        {

            
            {
                setCycleDuration(Duration.millis(250));
            }

            @Override
            public void interpolate(double frac)
            {
                slideMenuPanel(frac);
            }
        };
    }

    /**
     *
     * @param paneToSlide
     * @param directionToSlide
     */
    public void configurePanel(Pane paneToSlide, SlidingComponentDirection directionToSlide)
    {
        this.paneToSlide = paneToSlide;
        this.directionToSlide = directionToSlide;
    }

    /**
     *
     */
    public void toggleSlide()
    {
        if (slidIn)
        {
            startSlidingOut();
        } else
        {
            startSlidingIn();
        }
    }

    /**
     *
     */
    public void slideIn()
    {
        slideMenuPanel(0.0);
        hidden = true;
    }

    /**
     *
     */
    public void slideOut()
    {
        slideMenuPanel(1.0);
        hidden = false;
    }

    /**
     *
     * @param amountToShow
     */
    public void slideMenuPanel(double amountToShow)
    {
        if (amountToShow < minimumToShow)
        {
            amountToShow = minimumToShow;
        } else if (amountToShow > maximumToShow)
        {
            amountToShow = maximumToShow;
        }

        if (amountToShow == minimumToShow)
        {
            slidIn = true;
        } else
        {
            slidIn = false;
        }

        if (directionToSlide == SlidingComponentDirection.IN_FROM_LEFT
                || directionToSlide == SlidingComponentDirection.IN_FROM_RIGHT)
        {
            double targetPanelWidth = panelWidth * amountToShow;
            double widthToHide = panelWidth - targetPanelWidth;
            double translateByX = 0;

            if (directionToSlide == SlidingComponentDirection.IN_FROM_LEFT)
            {
                translateByX = -panelWidth + targetPanelWidth;
                clippingRectangle.setX(-translateByX);
            } else
            {
                translateByX = panelWidth - targetPanelWidth;
            }

            clippingRectangle.setHeight(panelHeight);
            clippingRectangle.setWidth(targetPanelWidth);

            paneToSlide.setClip(clippingRectangle);
            paneToSlide.setTranslateX(translateByX);
        } else if (directionToSlide == SlidingComponentDirection.DOWN_FROM_TOP
                || directionToSlide == SlidingComponentDirection.UP_FROM_BOTTOM)
        {
            double targetPanelHeight = panelHeight * amountToShow;
            double heightToHide = panelHeight - targetPanelHeight;
            double translateByY = 0;

            if (directionToSlide == SlidingComponentDirection.DOWN_FROM_TOP)
            {
                translateByY = -panelHeight + targetPanelHeight;
                clippingRectangle.setY(panelLayoutMinY + heightToHide);

            } else
            {
                translateByY = panelHeight - targetPanelHeight;
            }

            clippingRectangle.setHeight(targetPanelHeight);
            clippingRectangle.setWidth(panelWidth);

            paneToSlide.setClip(clippingRectangle);
            paneToSlide.setTranslateY(translateByY);
        }
    }

    /**
     *
     * @return
     */
    public boolean isHidden()
    {
        return hidden;
    }

    /**
     *
     * @return
     */
    public boolean isSliding()
    {
        return showSidebar.statusProperty().get() != Animation.Status.STOPPED
                || hideSidebar.statusProperty().get() != Animation.Status.STOPPED;
    }

    /**
     *
     * @return
     */
    public boolean startSlidingOut()
    {
        if (hideSidebar.statusProperty().get() == Animation.Status.STOPPED)
        {
//            steno.info("Pulling out");
            showSidebar.play();
            return true;
        } else
        {
            return false;
        }
    }

    /**
     *
     * @return
     */
    public boolean startSlidingIn()
    {
        if (showSidebar.statusProperty().get() == Animation.Status.STOPPED)
        {
//            steno.info("Hiding");
            hideSidebar.play();
            return true;
        } else
        {
            return false;
        }
    }

    /**
     *
     * @return
     */
    public boolean isSlidIn()
    {
        return slidIn;
    }

    /**
     *
     * @param panelWidth
     * @param panelHeight
     * @param panelLayoutMinX
     * @param panelLayoutMinY
     */
    public void setDimensions(double panelWidth, double panelHeight, double panelLayoutMinX, double panelLayoutMinY)
    {
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
        this.panelLayoutMinX = panelLayoutMinX;
        this.panelLayoutMinY = panelLayoutMinY;
    }
}
