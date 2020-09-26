/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.coreUI.components;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

/**
 *
 * @author Ian
 */
public class InsetPanelWithMenu extends HBox
{

    @FXML
    private InsetPanelMenu menu;

    @FXML
    private HBox contentContainer;

    public InsetPanelWithMenu()
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
            "/celtech/resources/fxml/components/insetPanelWithMenu.fxml"));
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

        getStyleClass().add("inset-panel-with-menu");

        contentContainer.getStyleClass().add("blue-inset-panel");

//        this.widthProperty().addListener(new ChangeListener<Number>()
//        {
//            @Override
//            public void changed(
//                ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
//            {
//                contentContainer.setPrefWidth(newValue.doubleValue());
//            }
//        });
//        contentContainer.setPrefWidth(this.getWidth());
    }

    public void setMenuTitle(String title)
    {
        menu.setTitle(title);
    }

    public void addMenuItem(InsetPanelMenuItem menuItem, Node content)
    {
        menu.addMenuItem(menuItem);

//        contentContainer.getChildren().add(content);
    }
}
