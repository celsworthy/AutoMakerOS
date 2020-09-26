package celuk.groot.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;

public class EjectStuckMenuController extends MenuController {
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        menuPane.getStylesheets().add("styles/EjectStuckMenu.css");
        translateMenuText("ejectStuckMenu.title",
                          "ejectStuckMenu.material1",
                          "ejectStuckMenu.material2");
        rightButton.setVisible(false);
    }

    @Override
    protected void menu1Action(ActionEvent event) {
        if (validButtonAction(event)) {
            //System.out.println("eject stuck menu 1");
            printer.runEjectStuckMaterialTask(1);
            rootController.showHomePage(printer);
        }
    }

    @Override
    protected void menu2Action(ActionEvent event) {
        if (validButtonAction(event)) {
            //System.out.println("eject stuck menu 2");
            printer.runEjectStuckMaterialTask(2);
            rootController.showHomePage(printer);
        }
    }

    @Override
    protected void leftButtonAction(ActionEvent event) {
        if (validButtonAction(event))
            rootController.showMaintenanceMenu(printer);
    }
    
    @Override
    protected void middleButtonAction(ActionEvent event) {
        if (validButtonAction(event))
            rootController.showMainMenu(printer);
    }
}
