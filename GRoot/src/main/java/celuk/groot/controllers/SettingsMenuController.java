package celuk.groot.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;

public class SettingsMenuController extends MenuController {
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        menuPane.getStylesheets().add("styles/SettingsMenu.css");
        translateMenuText("settingsMenu.title",
                          "settingsMenu.identity",
                          "settingsMenu.wireless",
                          "settingsMenu.security",
                          "settingsMenu.trash",
                          "settingsMenu.about");
        leftButton.setVisible(false);
        rightButton.setVisible(false);
    }
    
    @Override
    protected void menu1Action(ActionEvent event) {
        if (validButtonAction(event)) {
            //System.out.println("settings menu 1");
            rootController.showIdentityMenu(printer);
        }
    }

    @Override
    protected void menu2Action(ActionEvent event) {
        if (validButtonAction(event)) {
            //System.out.println("settings menu 2");
            rootController.showWirelessPage(printer);
        }
    }

    @Override
    protected void menu3Action(ActionEvent event) {
        if (validButtonAction(event)) {
            //System.out.println("settings menu 3");
            rootController.showSecurityMenu(printer);
        }
    }

    @Override
    protected void menu4Action(ActionEvent event) {
        if (validButtonAction(event)) {
            //System.out.println("settings menu 4");
            printer.runTidyPrintJobDirsTask();
            rootController.showHomePage(printer);
        }
    }

    @Override
    protected void menu5Action(ActionEvent event) {
        if (validButtonAction(event)) {
            //System.out.println("settings menu 5");
            rootController.showAboutPage(printer);
        }
    }
}
