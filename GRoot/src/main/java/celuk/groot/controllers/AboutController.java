package celuk.groot.controllers;

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

public class AboutController implements Initializable, Page {

    @FXML
    private StackPane aboutPane;
    @FXML
    private Pane aboutHeader;
    @FXML
    private Label aboutVersion;
    @FXML
    private Label aboutPara1;
    @FXML
    private Label aboutPara2;
    @FXML
    private Label aboutPara3;
    @FXML
    private Label aboutPara4;
    @FXML
    private Label aboutPara5;
    @FXML
    private Label aboutPara6;
    @FXML
    private Label aboutPara7;
    @FXML
    private Pane aboutFooter;

    @FXML
    private Button leftButton;
    @FXML
    private Button middleButton;
    @FXML
    private Button rightButton;

    @FXML
    void leftButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button) {
            if (printer == null)
                rootController.showServerSettingsMenu();
            else
                rootController.showSettingsMenu(printer);
        }
    }
    
    @FXML
    void middleButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button)
            if (printer == null)
                rootController.showPrinterSelectPage();
            else
                rootController.showMainMenu(printer);
    }

    @FXML
    void rightButtonAction(ActionEvent event) {
    }

    private RootStackController rootController = null;
    private RootPrinter printer = null;
    private String versionFormat = "about.version";
    
    @Override
    public void setRootStackController(RootStackController rootController) {
        this.rootController = rootController;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        translateLabels(aboutPara1,
                        aboutPara2,
                        aboutPara3,
                        aboutPara4,
                        aboutPara5,
                        aboutPara6,
                        aboutPara7);
        versionFormat = I18n.t(versionFormat);
        aboutPane.setVisible(false);
        rightButton.setVisible(false);
    }
    
    @Override
    public void startUpdates() {
        String version = rootController.getRootServer().getServerVersion();
        String versionText = versionFormat.replaceAll("#1", version);
        aboutVersion.setText(versionText);
    }
    
    @Override
    public void stopUpdates() {
        printer = null;
    }

    @Override
    public void displayPage(RootPrinter printer) {
        this.printer = printer;
        if (!aboutPane.isVisible()) {
            startUpdates();
            aboutPane.setVisible(true);
        }
    }

    @Override
    public void hidePage() {
        stopUpdates();
        aboutPane.setVisible(false);
    }
    
    @Override
    public boolean isVisible() {
        return aboutPane.isVisible();
    }
}
