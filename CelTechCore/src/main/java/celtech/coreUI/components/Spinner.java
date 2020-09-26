package celtech.coreUI.components;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.AnimationTimer;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;

/**
 *
 * @author Ian
 */
public class Spinner extends StackPane implements Initializable
{
    
    @FXML
    private SVGPath outerArcs;

    @FXML
    private SVGPath innerArcs;

    private AnimationTimer timer = null;

    public Spinner()
    {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
            "/celtech/resources/fxml/components/spinner.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        fxmlLoader.setClassLoader(this.getClass().getClassLoader());

        try
        {
            fxmlLoader.load();
            scaleXProperty().set(0.5);
            scaleYProperty().set(0.5);

        } catch (IOException exception)
        {
            throw new RuntimeException(exception);
        }

        this.getStyleClass().add("spinner");
    }

    public void startSpinning()
    {
        timer.start();
    }

    public void stopSpinning()
    {
        timer.stop();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        timer = new AnimationTimer()
        {
            @Override
            public void handle(long now)
            {
                if (outerArcs.isVisible())
                {
                    long milliseconds = (int) (now / 1e6);
                    double outerAngle = milliseconds * 120d / 1000d;
                    double index = (outerAngle % 360);
                    double opacity = Math.abs(index - 180) / 180d;
                    outerArcs.rotateProperty().set(outerAngle);
                    innerArcs.rotateProperty().set(-outerAngle);
                    outerArcs.opacityProperty().set(opacity);
                }
            }
        };
    }
    
    private void recentreSpinner(Region region) {
        Bounds nodeBounds = region.getBoundsInLocal();
        double centreX = nodeBounds.getMinX() + nodeBounds.getWidth() / 2.0;
        double centreY = nodeBounds.getMinY() + nodeBounds.getHeight()/ 2.0;
        Point2D nodeCentreInScene = region.localToScene(centreX, centreY);
        Point2D spinnerCentreInScene = localToScene(getWidth() / 2.0, getHeight() / 2.0);
        setTranslateX(getTranslateX() + nodeCentreInScene.getX() - spinnerCentreInScene.getX());
        setTranslateY(getTranslateY() + nodeCentreInScene.getY() - spinnerCentreInScene.getY());
    }

    public void setCentreNode(Region centreNode)
    {
        centreNode.widthProperty().addListener(
            (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) ->
            {
                recentreSpinner(centreNode);
            });

        centreNode.heightProperty().addListener(
            (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) ->
            {
                recentreSpinner(centreNode);
            });    
        
        recentreSpinner(centreNode);
    }
}
