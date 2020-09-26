package celuk.groot.controllers;

import celuk.groot.remote.RootPrinter;
import celuk.groot.remote.RootServer;
import celuk.groot.remote.WifiStatusResponse;
import celuk.language.I18n;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Future;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class WirelessController implements Initializable, Page {
    
    @FXML
    private StackPane wirelessPane;
    
    @FXML
    private VBox wirelessVBox;
    
    @FXML
    private Label wirelessTitle;

    @FXML
    private HBox radioHBox;    
    @FXML
    private RadioButton wirelessOff;    
    @FXML
    private RadioButton wirelessOn;    

    @FXML
    private VBox credentialsVBox;
    @FXML
    private Label ssidLabel;    
    @FXML
    private TextField ssidField;
    @FXML
    private Button ssidClear;
    @FXML
    private Label passwordLabel;    
    @FXML
    private TextField passwordField;
    @FXML
    private Button passwordClear;
    @FXML
    private Label savePrompt;    
    
    @FXML
    private Button leftButton;
    @FXML
    private Button middleButton;
    @FXML
    private Button rightButton;

    @FXML
    void clearAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button) {
            TextInputControl f = (TextInputControl)((Button)event.getSource()).getUserData();
            f.clear();
        }
    }

    @FXML
    void wirelessToggleAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof RadioButton) {
            reconfigure();
        }
    }
    
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
        if (rootController != null && event.getSource() instanceof Button) {
            if (printer == null)
                rootController.showPrinterSelectPage();
            else
                rootController.showHomePage(printer);
        }
    }

    @FXML
    void rightButtonAction(ActionEvent event) {
        if (rootController != null &&
            event.getSource() instanceof Button) {
            
            radioHBox.setDisable(true);
            credentialsVBox.setDisable(true);
            keyboardPane.setDisable(true);
            rightButton.setDisable(true);
            String ssidText = ssidField.getText();
            String passwordText = passwordField.getText();
            boolean wifiEnabled = wirelessOn.isSelected();
            
            server.runBackgroundTask(() -> {
                Future<Void> f1 = null;
                if (wifiEnabled)
                    f1 = server.runSetWiFiCredentialsTask(ssidText, passwordText);
                else
                    f1 = server.runEnableDisableWifiTask(false);
                f1.get();
                Future<WifiStatusResponse> f2 = server.runRequestWifiStatusTask();
                f2.get();
                return null;
            });
        }
    }

    private RootStackController rootController = null;
    private KeyboardController keyboardController = null;
    private GridPane keyboardPane = null;
    
    private RootPrinter printer = null;
    private RootServer server = null;
    private WifiStatusResponse wifiStatus = null;
    private boolean wifiEnabled = false;
    private final ChangeListener<String> fieldListener = (ob, ov, nv) -> {
        reconfigure();
    };
    
    private final ChangeListener<WifiStatusResponse> statusListener = (ob, ov, nv) -> {
        wifiStatus = nv;
        
        Platform.runLater(() -> {
            if (wifiStatus != null &&
                wifiStatus.isPoweredOn() &&
                wifiStatus.isAssociated() &&
                !wifiStatus.getSsid().isBlank()) {
                radioHBox.setDisable(false);
                credentialsVBox.setDisable(false);
                wirelessOn.setSelected(true);
                wifiEnabled = true;
                ssidField.setText(wifiStatus.getSsid());
                passwordField.setText("");
                rightButton.setDisable(wifiStatus.getSsid().isBlank());
                keyboardPane.setDisable(false);
            }
            else {
                radioHBox.setDisable(wifiStatus == null);
                wirelessOff.setSelected(true);
                wifiEnabled = false;
                credentialsVBox.setDisable(true);
                ssidField.setText("");
                passwordField.setText("");
                keyboardPane.setDisable(true);
                rightButton.setDisable(true);
            }
        });
    };

    @Override
    public void setRootStackController(RootStackController rootController) {
        this.rootController = rootController;
        this.server = rootController.getRootServer();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        URL keyboardURL = getClass().getResource("/fxml/Keyboard.fxml");
        try
        {
            FXMLLoader keyboardLoader =  new FXMLLoader(keyboardURL, null);
            keyboardPane = keyboardLoader.load();
            keyboardController = (KeyboardController)(keyboardLoader.getController());
            TextInputControl focusFields[] = new TextInputControl[] {
                ssidField,
                passwordField
            };
            keyboardController.setFocusFields(focusFields);
            List<Node> children = wirelessVBox.getChildren();
            children.add(children.size() - 1, keyboardPane);
            VBox.setMargin( keyboardPane, new Insets(5, 5, 5, 5));
            VBox.setVgrow(keyboardPane, Priority.ALWAYS);
        }
        catch (IOException ex) {
            System.err.println(ex);
            ex.printStackTrace(System.err);
            System.exit(1);
        }

        wirelessOff.setText(I18n.t("wireless.offLabel"));
        wirelessOn.setText(I18n.t("wireless.onLabel"));
        ssidLabel.setText(I18n.t("wireless.ssidLabel"));
        passwordLabel.setText(I18n.t("wireless.passwordLabel"));
        wirelessTitle.setText(I18n.t("wireless.title"));
        savePrompt.setText(I18n.t("common.savePrompt"));
        ssidField.setPromptText(I18n.t("wireless.ssidPrompt"));
        passwordField.setPromptText(I18n.t("wireless.passwordPrompt"));
        ssidClear.setUserData(ssidField);
        passwordClear.setUserData(passwordField);
        
        wirelessPane.setVisible(false);        
        middleButton.setVisible(false);

        ssidField.textProperty().addListener(fieldListener);
    }
    
    @Override
    public void startUpdates() {
        clearPage();
        server.getWifiStatusProperty().addListener(statusListener);
        server.runRequestWifiStatusTask();
    }
    
    @Override
    public void stopUpdates() {
        server.getWifiStatusProperty().removeListener(statusListener);
        wifiStatus = null;
        printer = null;
    }

    @Override
    public void displayPage(RootPrinter printer) {
        this.printer = printer;
        if (!wirelessPane.isVisible()) {
            startUpdates();
            wirelessPane.setVisible(true);
        }
    }

    @Override
    public void hidePage() {
        stopUpdates();
        wirelessPane.setVisible(false);
    }
    
    @Override
    public boolean isVisible() {
        return wirelessPane.isVisible();
    }

    private void reconfigure()
    {
        boolean newWifiEnabled = wirelessOn.isSelected();
        String newSSID = ssidField.getText();
        if (wifiEnabled != newWifiEnabled ||
            (newWifiEnabled && newSSID != null && !newSSID.isBlank())) {
            rightButton.setDisable(false);
        }
        else {
            rightButton.setDisable(true);
        }
        credentialsVBox.setDisable(!newWifiEnabled);
    };

    private void clearPage() {
        wifiStatus = null;
        Platform.runLater(() -> {
            radioHBox.setDisable(true);
            wirelessOff.setSelected(true);
            credentialsVBox.setDisable(true);
            ssidField.setText("");
            passwordField.setText("");
            keyboardPane.setDisable(true);
            rightButton.setDisable(true);
        });
    }
}
