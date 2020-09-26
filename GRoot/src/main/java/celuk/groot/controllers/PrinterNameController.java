package celuk.groot.controllers;

import celuk.groot.remote.RootPrinter;
import celuk.language.I18n;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class PrinterNameController implements Initializable, Page {
    
    @FXML
    private StackPane namePane;

    @FXML
    private VBox nameVBox;

    @FXML
    private Label nameTitle;

    @FXML
    private Label nameLabel;    
    @FXML
    private TextField nameField;
    @FXML
    private Button nameClear;
    @FXML
    private Label savePrompt;    
    
    @FXML
    private Button leftButton;
    @FXML
    private Button middleButton;
    @FXML
    private Button rightButton;

    @FXML
    void nameClearAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button) {
            nameField.clear();
        }
    }

    @FXML
    void leftButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button) {
            rootController.showMainMenu(printer);
        }
    }
    
    @FXML
    void middleButtonAction(ActionEvent event) {
    }

    @FXML
    void rightButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button) {
            // Save printer name
            String printerName = nameField.getText().trim();
            if (!printerName.isEmpty() && !printerName.equals(currentName)) {
                printer.runRenamePrinterTask(printerName);
                rootController.showHomePage(printer);
            }
        }
    }

    private RootStackController rootController = null;
    private KeyboardController keyboardController = null;
    private RootPrinter printer = null;
    private String currentName = null;
    
    @Override
    public void setRootStackController(RootStackController rootController) {
        this.rootController = rootController;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        URL keyboardURL = getClass().getResource("/fxml/Keyboard.fxml");
        try
        {
            FXMLLoader keyboardLoader =  new FXMLLoader(keyboardURL, null);
            GridPane keyboardPane = keyboardLoader.load();
            keyboardController = (KeyboardController)(keyboardLoader.getController());
            TextInputControl focusFields[] = new TextInputControl[] { nameField };
            keyboardController.setFocusFields(focusFields);
            List<Node> children = nameVBox.getChildren();
            children.add(children.size() - 1, keyboardPane);
            VBox.setMargin( keyboardPane, new Insets(5, 5, 5, 5));
            VBox.setVgrow(keyboardPane, Priority.ALWAYS);
        }
        catch (IOException ex) {
            System.out.println(ex);
            ex.printStackTrace(System.err);
            System.exit(1);
        }
        nameLabel.setText(I18n.t("printerName.nameLabel"));
        nameTitle.setText(I18n.t("printerName.title"));
        savePrompt.setText(I18n.t("common.savePrompt"));
        nameField.setPromptText(I18n.t("printerName.enterName"));
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            String v = newValue.trim();
            rightButton.setDisable(v.isBlank() || v.equals(currentName));
        });
        middleButton.setDisable(true);
        namePane.setVisible(false);
    }
    
    @Override
    public void startUpdates() {
        Platform.runLater(() -> {
            currentName = printer.getPrinterName();
            nameField.setText(currentName);
            keyboardController.setCase(true);
        });
    }
    
    @Override
    public void stopUpdates() {
        printer = null;
    }

    @Override
    public void displayPage(RootPrinter printer) {
        this.printer = printer;
        if (!namePane.isVisible()) {
            startUpdates();
            namePane.setVisible(true);
        }
    }

    @Override
    public void hidePage() {
        stopUpdates();
        namePane.setVisible(false);
    }
    
    @Override
    public boolean isVisible() {
        return namePane.isVisible();
    }
 }
