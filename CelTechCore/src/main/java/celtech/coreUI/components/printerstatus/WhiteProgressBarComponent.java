/*
 * Copyright 2014 CEL UK
 */
package celtech.coreUI.components.printerstatus;

import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

/**
 *
 * @author tony
 */
public class WhiteProgressBarComponent extends Pane
{

    @FXML
    private Polygon solidBar;

    @FXML
    private Polygon clearBar;

    private double width;
    private double height;
    private double progress;

    public WhiteProgressBarComponent()
    {
        super();
        URL fxml = getClass().getResource(
                "/celtech/resources/fxml/printerstatus/whiteprogressbar.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(fxml);
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try
        {
            fxmlLoader.load();
        } catch (IOException exception)
        {
            throw new RuntimeException(exception);
        }

        solidBar.setFill(Color.WHITE);
        clearBar.setFill(Color.WHITE);
        clearBar.setOpacity(0.5);

        width = 50;
        height = 10;
        redraw();
    }

    public void setProgress(double progress)
    {
        if (progress != this.progress)
        {
            this.progress = progress;
            redraw();
        }
    }

    void setControlWidth(double width)
    {
        this.width = width;
        redraw();
    }

    void setControlHeight(double height)
    {
        this.height = height;
        redraw();
    }

    private void redraw()
    {
        double barWidth = width * progress;
        solidBar.getPoints().clear();
        solidBar.getPoints().addAll(new Double[]
        {
            0.0, 0.0,
            barWidth, 0.0,
            barWidth, height,
            0.0, height,
        });
        clearBar.getPoints().clear();
        clearBar.getPoints().addAll(new Double[]
        {
            barWidth, height,
            width, height,
            width, 0.0,
            barWidth, 0.0,
        });
    }
}
