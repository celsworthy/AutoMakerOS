package celuk.groot.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;

public class ServerSettingsMenuController extends MenuController {
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        menuPane.getStylesheets().add("styles/ServerSettingsMenu.css");
        translateMenuText("serverSettingsMenu.title",
                          "serverSettingsMenu.serverName",
                          "serverSettingsMenu.wireless",
                          "serverSettingsMenu.security",
                          "serverSettingsMenu.about");
        middleButton.setVisible(false);
        rightButton.setVisible(false);
    }
    
    @Override
    protected void menu1Action(ActionEvent event) {
        if (validButtonAction(event)) {
            //System.out.println("server settings menu 1");
            rootController.showServerNamePage();
        }
    }

    @Override
    protected void menu2Action(ActionEvent event) {
        if (validButtonAction(event)) {
            //System.out.println("server settings menu 2");
            rootController.showWirelessPage(printer);
        }
    }

    @Override
    protected void menu3Action(ActionEvent event) {
        if (validButtonAction(event)) {
            //System.out.println("server settings menu 3");
            rootController.showSecurityMenu(printer);
        }
    }

    @Override
    protected void menu4Action(ActionEvent event) {
        if (validButtonAction(event)) {
            //System.out.println("server settings menu 4");
            rootController.showAboutPage(printer);
        }
    }
    
    @Override
    protected void leftButtonAction(ActionEvent event) {
        if (validButtonAction(event))
            rootController.showPrinterSelectPage();
    }
}
