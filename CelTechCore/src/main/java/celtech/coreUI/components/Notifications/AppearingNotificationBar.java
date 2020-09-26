/*
 * Copyright 2014 CEL UK
 */
package celtech.coreUI.components.Notifications;

import celtech.Lookup;
import celtech.coreUI.components.HyperlinkedLabel;
import celtech.roboxbase.appManager.NotificationType;
import celtech.roboxbase.utils.Math.MathUtils;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

/**
 *
 * @author tony
 */
public abstract class AppearingNotificationBar extends StackPane implements Initializable
{
    
    @FXML
    private StackPane notificationBar;
    
    @FXML
    protected HyperlinkedLabel notificationDescription;
    
    @FXML
    private Label notificationStepXofY;
    
    @FXML
    private SVGPath noteIndicator;
    
    @FXML
    private Group warningIndicator;
    
    @FXML
    private Group cautionIndicator;
    
    @FXML
    Button actionButton;

    private static final Duration transitionLengthMillis = Duration.millis(200);
    
    NotificationType notificationType;
    
    private Animation hideSidebar = new Transition()
    {
        {
            setCycleDuration(transitionLengthMillis);
        }
        
        @Override
        public void interpolate(double frac)
        {
            slideMenuPanel(1.0 - frac);
        }
    };
    
    private Animation showSidebar = new Transition()
    {
        
        {
            setCycleDuration(transitionLengthMillis);
        }
        
        @Override
        public void interpolate(double frac)
        {
            slideMenuPanel(frac);
        }
    };
    
    private final double minimumToShow = 0.0;
    private final double maximumToShow = 1.0;
    private boolean slidingIntoView = false;
    private boolean slidingOutOfView = false;
    private boolean slidIntoView = false;
    private boolean slidOutOfView = false;
    private double panelHeight = 0;
    private final Rectangle clippingRectangle = new Rectangle();
    
    public AppearingNotificationBar()
    {
        super();
        
        URL fxml = getClass().getResource(
                "/celtech/resources/fxml/components/notifications/appearingNotificationBar.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(fxml);
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.setClassLoader(getClass().getClassLoader());
        
        try
        {
            fxmlLoader.load();
        } catch (IOException exception)
        {
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
        
        showSidebar.setOnFinished((ActionEvent t) ->
        {
            slidingIntoView = false;
            slidIntoView = true;
            finishedSlidingIntoView();
        });
        
        hideSidebar.setOnFinished((ActionEvent t) ->
        {
            slidingOutOfView = false;
            slidOutOfView = true;
            setVisible(false);
            finishedSlidingOutOfView();
        });
        
        notificationStepXofY.setVisible(false);
        actionButton.setVisible(false);
    }

    /**
     *
     * @param amountToShow
     */
    private void slideMenuPanel(double amountToShow)
    {
        if (amountToShow < minimumToShow)
        {
            amountToShow = minimumToShow;
        } else if (amountToShow > maximumToShow)
        {
            amountToShow = maximumToShow;
        }
        
        double targetPanelHeight = panelHeight * amountToShow;
        
        clippingRectangle.setY(panelHeight - targetPanelHeight);
        clippingRectangle.setHeight(targetPanelHeight);
        notificationBar.setPrefHeight(targetPanelHeight);
    }

    /**
     *
     */
    public void startSlidingOutOfView()
    {
        if (!isSlidOutOrSlidingOut())
        {
            slidingIntoView = false;
            slidingOutOfView = true;
            slidIntoView = false;
            slidOutOfView = false;
            showSidebar.stop();
            Duration time = showSidebar.getCurrentTime();
            Duration startFromTime;
            if (time.lessThanOrEqualTo(Duration.ZERO))
            {
                startFromTime = Duration.ZERO;
            } else
            {
                startFromTime = transitionLengthMillis.subtract(time);
            }
            hideSidebar.jumpTo(startFromTime);
            hideSidebar.play();
        } else if (slidOutOfView)
        {
            if (MathUtils.compareDouble(notificationBar.getPrefHeight(), 0.0, 0.01) == MathUtils.MORE_THAN)
            {
                slideMenuPanel(0);
            }
        }
        
    }

    /**
     *
     */
    public void startSlidingInToView()
    {
        if (!isSlidInOrSlidingIn())
        {
            setVisible(true);
            slidingIntoView = true;
            slidingOutOfView = false;
            slidIntoView = false;
            slidOutOfView = false;
            hideSidebar.stop();
            Duration time = hideSidebar.getCurrentTime();
            Duration startFromTime;
            if (time.lessThanOrEqualTo(Duration.ZERO))
            {
                startFromTime = Duration.ZERO;
            } else
            {
                startFromTime = transitionLengthMillis.subtract(time);
                showSidebar.jumpTo(startFromTime);
            }
            showSidebar.play();
        } else if (slidIntoView)
        {
            if (MathUtils.compareDouble(notificationBar.getPrefHeight(), 1.0, 0.01) == MathUtils.LESS_THAN)
            {
                slideMenuPanel(1.0);
            }
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        panelHeight = notificationBar.getPrefHeight();
        
        notificationBar.setMinHeight(0);
        
        slideMenuPanel(0);
        slidIntoView = false;
        slidOutOfView = true;
        slidingIntoView = false;
        slidingOutOfView = false;

//        pauseButton.setVisible(false);
//        resumeButton.setVisible(false);
//        cancelButton.setVisible(false);
        clippingRectangle.setX(0);
        clippingRectangle.setY(0);
        clippingRectangle.setHeight(panelHeight);
        clippingRectangle.setWidth(4000);
        
        setVisible(false);
        notificationBar.setClip(clippingRectangle);
        notificationBar.setPrefHeight(0);
    }
    
    public boolean isSlidInOrSlidingIn()
    {
        return slidIntoView || slidingIntoView;
    }
    
    public boolean isSlidOutOrSlidingOut()
    {
        return slidOutOfView || slidingOutOfView;
    }
    
    public void setMessage(String message)
    {
        notificationDescription.replaceText(message);
    }
    
    public void setType(NotificationType notificationType)
    {
        switch (notificationType)
        {
            case NOTE:
                noteIndicator.setVisible(true);
                warningIndicator.setVisible(false);
                cautionIndicator.setVisible(false);
                break;
            case WARNING:
                noteIndicator.setVisible(false);
                warningIndicator.setVisible(true);
                cautionIndicator.setVisible(false);
                break;
            case CAUTION:
                noteIndicator.setVisible(false);
                warningIndicator.setVisible(false);
                cautionIndicator.setVisible(true);
                break;
            default:
                noteIndicator.setVisible(false);
                warningIndicator.setVisible(false);
                cautionIndicator.setVisible(false);
                break;
        }
        
        this.notificationType = notificationType;
    }
    
    public NotificationType getType()
    {
        return notificationType;
    }
    
    public void setXOfY(int step, int ofSteps)
    {
        notificationStepXofY.setText(step + " " + Lookup.i18n("misc.of") + " " + ofSteps);
        notificationStepXofY.setVisible(true);
    }
    
    public abstract void show();

    public abstract void finishedSlidingIntoView();

    public abstract void finishedSlidingOutOfView();
    
    public abstract boolean isSameAs(AppearingNotificationBar bar);
    
    public abstract void destroyBar();
}
