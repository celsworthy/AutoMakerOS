/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.coreUI.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class SlideOutHandleController implements Initializable
{

    @FXML
    VBox handleContainer;

    @FXML
    Button handle;

    @FXML
    void toggleSlide(ActionEvent event)
    {
        toggleSlide();
    }

    private HBox paneToSlide = null;
    private double handleWidth = 0;

    private Animation hideSidebar = null;
    private Animation showSidebar = null;
    private boolean slidIn = false;
    private boolean sliding = false;
    private double panelWidth = 0;
    private int delayTime = 250;

    /**
     *
     */
    public SlideOutHandleController()
    {
        hideSidebar = new Transition()
        {
            {
                setCycleDuration(Duration.millis(delayTime));
            }

            @Override
            public void interpolate(double frac)
            {
                slideMenuPanel(1.0 - frac);
            }
        };

        hideSidebar.onFinishedProperty().set(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                slidIn = true;
            }
        });

        // create an animation to show a sidebar.
        showSidebar = new Transition()
        {
            {
                setCycleDuration(Duration.millis(delayTime));
            }

            @Override
            public void interpolate(double frac)
            {
                slideMenuPanel(frac);
            }
        };

        showSidebar.onFinishedProperty().set(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                slidIn = false;
            }
        });
    }

    private void setup()
    {
        if (paneToSlide == null)
        {
            paneToSlide = (HBox) (handleContainer.getParent());
            handleWidth = handleContainer.getWidth();
            paneToSlide.setMinWidth(handleWidth);
            paneToSlide.setPrefWidth(handleWidth);
            panelWidth = paneToSlide.getWidth();
        }
    }

    /**
     *
     */
    public void toggleSlide()
    {
        setup();
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
        setup();
        slideMenuPanel(0.0);
        slidIn = true;
    }

    /**
     *
     * @param amountToShow
     */
    public void slideMenuPanel(double amountToShow)
    {
        double adjustedWidth = (panelWidth * amountToShow) + handleWidth;
        paneToSlide.setMinWidth(adjustedWidth);
        paneToSlide.setPrefWidth(adjustedWidth);
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
            panelWidth = paneToSlide.getWidth();
            paneToSlide.setMaxWidth(panelWidth);
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

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
    }

}
