package celtech.coreUI.components.buttons;

import celtech.roboxbase.utils.Math.MathUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 *
 * @author Ian
 */
public class GraphicButtonWithLabel extends Pane
{

    private final VBox container = new VBox();
    private final GraphicButton button = new GraphicButton();
    private final Label label = new Label();

    public GraphicButtonWithLabel()
    {
        this.setPrefWidth(80);
        this.setPrefHeight(80);
        this.setMinWidth(USE_PREF_SIZE);
        this.setMaxWidth(USE_PREF_SIZE);
        this.setMinHeight(USE_PREF_SIZE);
        this.setMaxHeight(USE_PREF_SIZE);

        container.setAlignment(Pos.CENTER);
        container.setPrefWidth(80);
        container.setPrefHeight(80);
        container.setMinWidth(USE_PREF_SIZE);
        container.setMaxWidth(USE_PREF_SIZE);
        container.setMinHeight(USE_PREF_SIZE);
        container.setMaxHeight(USE_PREF_SIZE);

        label.getStyleClass().add("graphic-button-label");

        getChildren().add(container);
        container.getChildren().add(button);
        container.getChildren().add(label);
    }

    public String getFxmlFileName()
    {
        return button.getFxmlFileName();
    }

    public void setFxmlFileName(String fxmlFileName)
    {
        button.setFxmlFileName(fxmlFileName);
    }

    public StringProperty getFxmlFileNameProperty()
    {
        return button.getFxmlFileNameProperty();
    }

    public final ObjectProperty<EventHandler<ActionEvent>> onActionProperty()
    {
        return button.onActionProperty();
    }

    public final void setOnAction(EventHandler<ActionEvent> value)
    {
        button.onActionProperty().set(value);
    }

    public final EventHandler<ActionEvent> getOnAction()
    {
        return button.onActionProperty().get();
    }

    public String getLabelText()
    {
        return label.getText();
    }

    public void setLabelText(String text)
    {
        label.setText(text);
    }

    public StringProperty getLabelTextProperty()
    {
        return label.textProperty();
    }
}
