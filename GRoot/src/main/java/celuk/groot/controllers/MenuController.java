package celuk.groot.controllers;

import celuk.groot.remote.PrinterStatusResponse;
import celuk.groot.remote.RootPrinter;
import celuk.language.I18n;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

abstract public class MenuController implements Initializable, Page {
    
    @FXML
    protected StackPane menuPane;
    @FXML
    protected Pane menuLogo;
    @FXML
    protected Label menuTitle;
    @FXML
    protected Button menuButton1;
    @FXML
    protected Button menuButton2;
    @FXML
    protected Button menuButton3;
    @FXML
    protected Button menuButton4;
    @FXML
    protected Button menuButton5;
    @FXML
    protected Button menuButton6;
    @FXML
    protected Button leftButton;
    @FXML
    protected Button middleButton;
    @FXML
    protected Button rightButton;
    
    @FXML
    protected void menu1Action(ActionEvent event) {
        //System.out.println("menu 1");
    }

    @FXML
    protected void menu2Action(ActionEvent event) {
        //System.out.println("menu 2");
    }

    @FXML
    protected void menu3Action(ActionEvent event) {
        //System.out.println("menu 3");
    }

    @FXML
    protected void menu4Action(ActionEvent event) {
        //System.out.println("menu 4");
    }

    @FXML
    protected void menu5Action(ActionEvent event) {
        //System.out.println("menu 5");
    }

    @FXML
    protected void menu6Action(ActionEvent event) {
        //System.out.println("menu 6");
    }
    
    @FXML
    protected void leftButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button)
            rootController.showMainMenu(printer);
    }
    
    @FXML
    protected void middleButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button)
            rootController.showHomePage(printer);
    }

    @FXML
    protected void rightButtonAction(ActionEvent event) {
    }

    protected RootStackController rootController = null;
    protected RootPrinter printer = null;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        menuPane.setVisible(false);
    }
    
    @Override
    public void setRootStackController(RootStackController rootController) {
        this.rootController = rootController;
    }
    
    @Override
    public void startUpdates() {
    }
    
    @Override
    public void stopUpdates() {
        printer = null;
    }

    @Override
    public void displayPage(RootPrinter printer) {
        this.printer = printer;
        String logoIcon = "/image/logo-root-text-white.png";
        if (printer != null) {
            PrinterStatusResponse s = printer.getCurrentStatusProperty().get();
            if (s != null) {
                logoIcon = MachineDetails.getDetails(s.getPrinterTypeCode())
                                         .getTextIcon();
            }
        }
        menuLogo.setStyle("-fx-background-image: url(\""
                          + logoIcon
                          + "\");");
        if (!menuPane.isVisible()) {
            startUpdates();
            menuPane.setVisible(true);
        }
    }

    @Override
    public void hidePage() {
        stopUpdates();
        menuPane.setVisible(false);
    }
    
    @Override
    public boolean isVisible() {
        return menuPane.isVisible();
    }

    protected void translateMenuText(String titleText, String ... buttonTexts) {
        Button[] buttons = {menuButton1, menuButton2, menuButton3,
                            menuButton4, menuButton5, menuButton6};
        menuTitle.setText(I18n.t(titleText));
        for (int i = 0; i < 6; ++i) {
            Button b = buttons[i];
            if (i < buttonTexts.length) {
                b.setText(I18n.t(buttonTexts[i]));
                b.setVisible(true);
            }
            else {
                b.setText("");
                b.setVisible(false);
            }
        }
    }
    
    protected boolean validButtonAction(ActionEvent event) {
        return (rootController != null && event.getSource() instanceof Button);
    }
}
