package celuk.groot.controllers;

import celuk.groot.remote.RootPrinter;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class PurgeIntroController implements Initializable, Page {
    
    @FXML
    private StackPane purgeIntroPane;
    @FXML
    private Label purgeIntroTitle;
    @FXML
    private Label purgeIntro1;
    @FXML
    private Label purgeIntro2;
    @FXML
    private Label purgeIntroContinue;
    
    @FXML
    private Button leftButton;
    @FXML
    private Button middleButton;
    @FXML
    private Button rightButton;

    @FXML
    void leftButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button)
            rootController.showMainMenu(printer);
    }
    
    @FXML
    void middleButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button)
            rootController.showHomePage(printer);
    }

    @FXML
    void rightButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button)
            rootController.showPurgePage(printer);
    }

    private RootStackController rootController = null;
    private RootPrinter printer = null;
    
    @Override
    public void setRootStackController(RootStackController rootController) {
        this.rootController = rootController;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        translateLabels(purgeIntroTitle,
                        purgeIntro1,
                        purgeIntro2,
                        purgeIntroContinue);
        purgeIntroPane.setVisible(false);
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
        if (!purgeIntroPane.isVisible()) {
            startUpdates();
            purgeIntroPane.setVisible(true);
        }
    }

    @Override
    public void hidePage() {
        stopUpdates();
        purgeIntroPane.setVisible(false);
    }
    
    @Override
    public boolean isVisible() {
        return purgeIntroPane.isVisible();
    }
}
