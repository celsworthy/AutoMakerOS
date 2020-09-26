/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.coreUI.components;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 *
 * @author Ian
 */
public class InsetPanelMenu extends HBox
{
    @FXML
    private Text menuTitle;

    @FXML
    private VBox menuItemContainer;
    
    private ToggleGroup buttonGroup = new ToggleGroup();

    public InsetPanelMenu()
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
            "/celtech/resources/fxml/components/insetPanelMenu.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        fxmlLoader.setClassLoader(this.getClass().getClassLoader());

        try
        {
            fxmlLoader.load();
        } catch (IOException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    public void setTitle(String title)
    {
        menuTitle.setText(title);
    }
    
    public void addMenuItem(InsetPanelMenuItem menuItem)
    {
        menuItemContainer.getChildren().add(menuItem);
        
        menuItem.setToggleGroup(buttonGroup);
    }
}
