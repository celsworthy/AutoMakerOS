/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.coreUI.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 * FXML Controller class
 *
 * @author ianhudson
 */
public class ModalDialogController implements Initializable
{

    private Stenographer steno = StenographerFactory.getStenographer(ModalDialogController.class.getName());
    @FXML
    private Label dialogTitle;
    @FXML
    private Label dialogMessage;
    @FXML
    private HBox buttonHolder;
    @FXML
    private VBox container;
    @FXML
    private VBox defaultContent;

    private EventHandler<ActionEvent> buttonHandler = null;
    private int buttonValue = -1;
    private Stage myStage = null;
    private Node customContent = null;
    /*
     * 
     */
    private ArrayList<Button> buttons = new ArrayList<>();

    /**
     *
     */
    public ModalDialogController()
    {
        buttonHandler = new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent t)
            {
                buttonValue = buttons.indexOf(t.getSource());
                myStage.close();
            }
        };

    }

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        dialogTitle.setText("");
    }

    /**
     *
     * @param title
     */
    public void setDialogTitle(String title)
    {
        dialogTitle.setText(title);
    }

    /**
     *
     * @param message
     */
    public void setDialogMessage(String message)
    {
        dialogMessage.setText(message);
    }

    /**
     *
     * @param text
     * @return
     */
    public int addButton(String text)
    {
        return addButton(text, null);
    }

    /**
     *
     * @param text
     * @param disabler
     * @return
     */
    public int addButton(String text, ReadOnlyBooleanProperty disabler)
    {
        Button newButton = new Button(text);
        newButton.setOnAction(buttonHandler);
        buttonHolder.getChildren().add(newButton);
        buttons.add(newButton);
        
        if (disabler != null)
        {
            newButton.disableProperty().bind(disabler);
        }
        
        return buttons.indexOf(newButton);
    }

    /**
     *
     * @return
     */
    public int getButtonValue()
    {
        return buttonValue;
    }

    /**
     *
     * @param dialogStage
     */
    public void configure(Stage dialogStage)
    {
        myStage = dialogStage;
    }

    /**
     *
     * @param content
     */
    public void setContent(Node content)
    {
        defaultContent.setVisible(false);
        if (customContent != null)
        {
            container.getChildren().remove(customContent);
        }

        customContent = content;
        container.getChildren().add(0, customContent);
    }
}
