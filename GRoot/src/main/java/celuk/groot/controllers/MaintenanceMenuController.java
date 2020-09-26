package celuk.groot.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;

public class MaintenanceMenuController extends MenuController {
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        menuPane.getStylesheets().add("styles/MaintenanceMenu.css");
        translateMenuText("maintenanceMenu.title",
                          "maintenanceMenu.purge",
                          "maintenanceMenu.ejectStuck",
                          "maintenanceMenu.cleanNozzles",
                          "maintenanceMenu.removeHead",
                          "maintenanceMenu.levelGantry",
                          "maintenanceMenu.testAxisSpeed");
        rightButton.setVisible(false);
    }

    @Override
    protected void menu1Action(ActionEvent event) {
        if (validButtonAction(event)) {
            //System.out.println("maintenance menu 1");
            rootController.showPurgeIntroPage(printer);
        }
    }

    @Override
    protected void menu2Action(ActionEvent event) {
        if (validButtonAction(event)) {
            //System.out.println("maintenance menu 2");
            rootController.showEjectStuckMenu(printer);
        }
    }

    @Override
    protected void menu3Action(ActionEvent event) {
        if (validButtonAction(event)) {
            //System.out.println("maintenance menu 3");
            rootController.showCleanNozzlesMenu(printer);
        }
    }

    @Override
    protected void menu4Action(ActionEvent event) {
        if (validButtonAction(event)) {
            // System.out.println("maintenance menu 4");
            printer.runRemoveHeadTask();
            rootController.showHomePage(printer);
        }
    }

    @Override
    protected void menu5Action(ActionEvent event) {
        if (validButtonAction(event)) {
            printer.runMacroTask("LEVEL_GANTRY");
            rootController.showHomePage(printer);
        }
    }

    @Override
    protected void menu6Action(ActionEvent event) {
        if (validButtonAction(event)) {
            rootController.showTestAxisSpeedMenu(printer);
        }
    }
}
