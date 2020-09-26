/*
 * Copyright 2015 CEL UK
 */
package celtech.coreUI.controllers.panels;

import celtech.Lookup;
import celtech.coreUI.components.buttons.GraphicButtonWithLabel;
import celtech.coreUI.controllers.panels.MenuInnerPanel.OperationButton;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

/**
 * The ButtonBox class is a JavaFX HBox that displays a set of operation buttons
 * as defined by a {@code List<OperationButton>}. This list can change according
 * to the active panel that it is tied to.
 *
 * @author tony
 */
public class ButtonBox extends HBox
{

    public ButtonBox(ReadOnlyObjectProperty<MenuInnerPanel> extrasMenuInnerPanelProperty)
    {
        setAlignment(Pos.CENTER);
        extrasMenuInnerPanelProperty.addListener(
                (ObservableValue<? extends MenuInnerPanel> observable, MenuInnerPanel oldValue, MenuInnerPanel newValue) ->
                {
                    setupButtonsForInnerPanel(newValue);
                });
        setupButtonsForInnerPanel(extrasMenuInnerPanelProperty.get());
    }

    /**
     * Set up the buttons according to the given panel.
     */
    private void setupButtonsForInnerPanel(MenuInnerPanel innerPanel)
    {
        if (innerPanel == null)
        {
            return;
        }
        getChildren().clear();
        if (innerPanel.getOperationButtons() != null)
        {
            for (OperationButton operationButton : innerPanel.getOperationButtons())
            {
                GraphicButtonWithLabel button = new GraphicButtonWithLabel();
                button.setLabelText(Lookup.i18n(operationButton.getTextId()));
                button.setFxmlFileName(operationButton.getFXMLName());
                button.setOnAction((ActionEvent event) ->
                {
                    operationButton.whenClicked();
                });
                button.disableProperty().bind(Bindings.not(operationButton.whenEnabled()));

                getChildren().add(button);
            }
        }
    }

}
