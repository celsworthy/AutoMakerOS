package celuk.groot.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;

public class TestAxisSpeedMenuController extends MenuController {
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        menuPane.getStylesheets().add("styles/TestAxisSpeedMenu.css");
        translateMenuText("testAxisSpeedMenu.title",
                          "testAxisSpeedMenu.testSpeed",
                          "testAxisSpeedMenu.testXAxis",
                          "testAxisSpeedMenu.testYAxis",
                          "testAxisSpeedMenu.testZAxis");
        rightButton.setVisible(false);
    }

    @Override
    protected void menu1Action(ActionEvent event) {
        if (validButtonAction(event)) {
            // System.out.println("test axis speed  menu 1");
            printer.runMacroTask("SPEED_TEST");
            rootController.showHomePage(printer);
        }
    }

    @Override
    protected void menu2Action(ActionEvent event) {
        if (validButtonAction(event)) {
            // System.out.println("test axis speed menu 2");
            printer.runMacroTask("TEST_X");
            rootController.showHomePage(printer);
        }
    }

    @Override
    protected void menu3Action(ActionEvent event) {
        if (validButtonAction(event)) {
            //System.out.println("test axis speed menu 3");
            printer.runMacroTask("TEST_Y");
            rootController.showHomePage(printer);
        }
    }

    @Override
    protected void menu4Action(ActionEvent event) {
        if (validButtonAction(event)) {
            //System.out.println("test axis speed menu 4");
            printer.runMacroTask("TEST_Z");
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
