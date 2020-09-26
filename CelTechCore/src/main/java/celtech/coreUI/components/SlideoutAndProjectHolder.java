package celtech.coreUI.components;

import celtech.configuration.ApplicationConfiguration;
import java.net.URL;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 *
 * @author Ian
 */
public class SlideoutAndProjectHolder extends HBox
{

    private final VBox slideOutHolder = new VBox();
    private Button slideButton = null;
    private final VBox projectTabPaneHolder = new VBox();

    private ObjectProperty<HBox> panelToSlide = new SimpleObjectProperty<>();

    private BooleanProperty slidIn = new SimpleBooleanProperty(false);
    private boolean sliding = false;
    private final int slideMs = 250;
    private double lastAmountShown = 0;
    private final double minimumToShow = 0.0;
    private final double maximumToShow = 1.0;
    private double panelWidth = 0;
    private double panelHeight = 0;
    private Rectangle clippingRectangle = new Rectangle();

    private final Animation hideSidebar = new Transition()
    {
        {
            setCycleDuration(Duration.millis(slideMs));
        }

        @Override
        public void interpolate(double frac)
        {
            slideMenuPanel(1.0 - frac);
        }
    };
    private final Animation showSidebar = new Transition()
    {
        {
            setCycleDuration(Duration.millis(slideMs));
        }

        @Override
        public void interpolate(double frac)
        {
            slideMenuPanel(frac);
        }
    };

    /**
     *
     */
    public SlideoutAndProjectHolder()
    {
        hideSidebar.onFinishedProperty().set(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                slidIn.set(true);
            }
        });

        // create an animation to show a sidebar.
        showSidebar.onFinishedProperty().set(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                slidIn.set(false);
            }
        });

        try
        {
            URL fxmlFileName = getClass().getResource(ApplicationConfiguration.fxmlResourcePath + "slideHandleButton.fxml");
            FXMLLoader buttonLoader = new FXMLLoader(fxmlFileName);
            slideButton = (Button) buttonLoader.load();
        } catch (Exception ex)
        {
            System.out.println("Exception: " + ex.getMessage());
        }

        getStyleClass().add("slideout-and-project-holder");

        slideButton.getStyleClass().add("slideout-control-button");
        slideButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                toggleSlide();
            }
        });
        slideButton.disableProperty().bind(panelToSlide.isNull());

        getChildren().addAll(slideOutHolder, slideButton, projectTabPaneHolder);

        HBox.setHgrow(projectTabPaneHolder, Priority.ALWAYS);

        panelWidth = 380;
        heightProperty().addListener((ObservableValue<? extends Number> ov, Number t, Number newHeight) ->
        {
            panelHeight = newHeight.doubleValue();
            slideMenuPanel(lastAmountShown);
        });
    }

    /**
     *
     */
    public void toggleSlide()
    {
        if (slidIn.get())
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
        slidIn.set(true);
    }

    /**
     *
     * @param amountToShow
     */
    public void slideMenuPanel(double amountToShow)
    {
        lastAmountShown = amountToShow;

        if (amountToShow < minimumToShow)
        {
            amountToShow = minimumToShow;
        } else if (amountToShow > maximumToShow)
        {
            amountToShow = maximumToShow;
        }

        double targetPanelWidth = panelWidth * amountToShow;
        double widthToHide = panelWidth - targetPanelWidth;
        double translateByX = 0;

        translateByX = -panelWidth + targetPanelWidth;
        clippingRectangle.setX(-translateByX);

        clippingRectangle.setHeight(panelHeight);
        clippingRectangle.setWidth(targetPanelWidth);

        panelToSlide.get().setClip(clippingRectangle);
        panelToSlide.get().setTranslateX(translateByX);
        panelToSlide.get().setMinWidth(targetPanelWidth);
        panelToSlide.get().setPrefWidth(targetPanelWidth);
    }

    /**
     *
     * @return
     */
    public boolean startSlidingOut()
    {
        if (hideSidebar.statusProperty().get() == Animation.Status.STOPPED)
        {
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
        return slidIn.get();
    }

    /**
     *
     * @return
     */
    public boolean isSliding()
    {
        return showSidebar.statusProperty().get() != Animation.Status.STOPPED || hideSidebar.statusProperty().get() != Animation.Status.STOPPED;
    }

    /**
     *
     * @param slideout
     */
    public void switchInSlideout(HBox slideout)
    {
        if (slideOutHolder.getChildren().isEmpty() == false)
        {
            slideOutHolder.getChildren().remove(0);
        }

        panelToSlide.set(slideout);

        clippingRectangle = new Rectangle();
        
        if (slideout != null)
        {
            slideOutHolder.getChildren().add(slideout);
            slideIn();
        }
    }

    /**
     *
     * @return
     */
    public VBox getProjectTabPaneHolder()
    {
        return projectTabPaneHolder;
    }

    /**
     *
     * @param node
     */
    public void populateProjectDisplay(Node node)
    {
        projectTabPaneHolder.getChildren().add(node);
    }
}
