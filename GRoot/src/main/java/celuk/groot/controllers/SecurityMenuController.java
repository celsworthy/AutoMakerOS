package celuk.groot.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;

public class SecurityMenuController extends MenuController {
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        menuPane.getStylesheets().add("styles/SecurityMenu.css");
        translateMenuText("securityMenu.title",
                          "securityMenu.accessPIN");
        rightButton.setVisible(false);
    }

    @Override
    protected void menu1Action(ActionEvent event) {
        if (validButtonAction(event)) {
            //System.out.println("security menu 1");
            rootController.showAccessPINPage(printer);
        }
    }

    @Override
    protected void leftButtonAction(ActionEvent event) {
        if (validButtonAction(event))
            if (printer != null)
                rootController.showSettingsMenu(printer);
            else
                rootController.showServerSettingsMenu();
    }
    
    @Override
    protected void middleButtonAction(ActionEvent event) {
        if (validButtonAction(event)) {
            if (printer != null)
                rootController.showMainMenu(printer);
            else
                rootController.showPrinterSelectPage();
        }
    }
}
