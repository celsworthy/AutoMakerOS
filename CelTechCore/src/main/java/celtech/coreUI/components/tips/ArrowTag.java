package celtech.coreUI.components.tips;

import celtech.Lookup;
import celtech.coreUI.DisplayManager;
import celtech.roboxbase.BaseLookup;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

/**
 *
 * @author Ian
 */
public class ArrowTag extends HBox
{

    Label title = new Label();
    
    @FXML
    VBox labelContainer;

    @FXML
    Label label;

    @FXML
    SVGPath arrow;

    private TaggablePane attachedTo;
    private String i18nTitle = null;
    private final List<ConditionalText> conditionalTextElements = new ArrayList<>();
    private final ChangeListener<Boolean> conditionChangeListener = new ChangeListener<Boolean>()
    {
        @Override
        public void changed(
            ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
        {
            calculateVisibility();
        }
    };

    public ArrowTag()
    {
        this.getStyleClass().add("arrow-tag");

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
            "/celtech/resources/fxml/components/ArrowTag.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        fxmlLoader.setClassLoader(this.getClass().getClassLoader());
        
        title.setAlignment(Pos.CENTER_LEFT);
        AnchorPane.setLeftAnchor(this, 0.0);
        title.setId("title");

        try
        {
            fxmlLoader.load();
        } catch (IOException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    public String getLabelText()
    {
        return label.getText();
    }

    public void setLabelText(String text)
    {
        label.setText(text);
    }

    public StringProperty labelTextProperty()
    {
        return label.textProperty();
    }

    public String getTitleText()
    {
        return title.getText();
    }

    public void setTitleText(String text)
    {
        title.setText(text);
    }

    public StringProperty titleTextProperty()
    {
        return title.textProperty();
    }

    private void bindPosition()
    {
        DisplayManager.getInstance().nodesMayHaveMovedProperty().addListener(
            (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) ->
            {
                repositionText();
            });
    }

    private void repositionText()
    {
        if (attachedTo != null)
        {
            Point2D tagOffsetInScene = attachedTo.getTagPosition();

            //TODO this will need to change as different orientations are supported
            setTranslateX(-getLayoutBounds().getMaxX() + tagOffsetInScene.getX());
            setTranslateY(-5);
        }
    }

    public void addConditionalText(String i18nText, ObservableValue<Boolean> whenToAddText)
    {
        conditionalTextElements.add(new ConditionalText(i18nText, whenToAddText));
        whenToAddText.addListener(conditionChangeListener);
        calculateVisibility();
    }

    public void removeAllConditionalText()
    {
        for (ConditionalText conditionalText : conditionalTextElements)
        {
            conditionalText.getAppearanceCondition().removeListener(conditionChangeListener);
        }
        conditionalTextElements.clear();
        calculateVisibility();
    }

    public void destroy()
    {
        attachedTo.getChildren().remove(this);
        removeAllConditionalText();
    }

    private void constructString()
    {
        StringBuilder labelText = new StringBuilder();

        if (i18nTitle != null)
        {
            title.setText(Lookup.i18n(i18nTitle));
        }

        boolean addedFirst = false;

        for (ConditionalText conditionalText : conditionalTextElements)
        {
            if (conditionalText.getAppearanceCondition().getValue())
            {
                if (addedFirst)
                {
                    labelText.append("\n");
                }

                labelText.append(Lookup.i18n(conditionalText.getI18nText()));

                addedFirst = true;
            }
        }

        label.setText(labelText.toString());
    }

    public void initialise(TaggablePane node, String i18nTitle)
    {
        this.i18nTitle = i18nTitle;
        labelContainer.getChildren().add(0, title);
        initialise(node);
    }

    public void initialise(TaggablePane node)
    {
        node.getChildren().add(this);

        this.attachedTo = node;

        attachedTo.visibleProperty().addListener(conditionChangeListener);

        this.layoutBoundsProperty().addListener(new ChangeListener<Bounds>()
        {
            @Override
            public void changed(
                ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue)
            {
                calculateVisibility();
            }
        });

        calculateVisibility();

        bindPosition();
    }

    private void calculateVisibility()
    {
        boolean visible = false;

        if (attachedTo.isVisible())
        {
            for (ConditionalText conditionalText : conditionalTextElements)
            {
                visible |= conditionalText.getAppearanceCondition().getValue();
            }
        }

        setVisible(visible);

        if (visible)
        {
            constructString();
            repositionText();
        }
    }
}
