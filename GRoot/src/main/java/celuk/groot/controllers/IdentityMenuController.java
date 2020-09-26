package celuk.groot.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;

public class IdentityMenuController extends MenuController {
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
        menuPane.getStylesheets().add("styles/IdentityMenu.css");
        translateMenuText("identityMenu.title",
                          "identityMenu.printerName",
                          "identityMenu.printerColour");
        rightButton.setVisible(false);
    }

    @Override
    protected void menu1Action(ActionEvent event) {
        if (validButtonAction(event)) {
            //System.out.println("identity menu 1");
            rootController.showPrinterNamePage(printer);
        }
    }

    @Override
    protected void menu2Action(ActionEvent event) {
        if (validButtonAction(event)) {
            //System.out.println("identity menu 2");
            rootController.showPrinterColourPage(printer);
        }
    }
    
        @Override
    protected void leftButtonAction(ActionEvent event) {
        if (validButtonAction(event))
            rootController.showSettingsMenu(printer);
    }
    
    @Override
    protected void middleButtonAction(ActionEvent event) {
        if (validButtonAction(event))
            rootController.showMainMenu(printer);
    }
}
