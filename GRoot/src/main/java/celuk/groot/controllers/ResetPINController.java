package celuk.groot.controllers;

import celuk.groot.remote.RootPrinter;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.robot.Robot;

public class ResetPINController implements Initializable, Page {

    @FXML
    private StackPane resetPINPane;
    @FXML
    private Label resetPINTitle;
    @FXML
    private Label serialLabel;
    @FXML
    private TextField serialField;
    @FXML
    private Label resetPINPrompt;
    @FXML
    private Button zeroKey;
    @FXML
    private Button oneKey;
    @FXML
    private Button twoKey;
    @FXML
    private Button threeKey;
    @FXML
    private Button fourKey;
    @FXML
    private Button fiveKey;
    @FXML
    private Button sixKey;
    @FXML
    private Button sevenKey;
    @FXML
    private Button eightKey;
    @FXML
    private Button nineKey;
    @FXML
    private Button backspaceKey;

    @FXML
    private Button leftButton;
    @FXML
    private Button middleButton;
    @FXML
    private Button rightButton;
    
    @FXML
    void keypadAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button) {
            Button b = (Button)event.getSource();
            if (!serialField.isFocused()) {
                serialField.requestFocus();
                // Setting the caret position clears the selection, so the key
                // does not replace the selected text.
                serialField.positionCaret(serialField.getText().length());
            }
            KeyCode k = (KeyCode)(b.getUserData());
            // Simulate holding down shift key for letter keys,
            // to get capital letters.
            if (k.isLetterKey())
                keyRobot.keyPress(KeyCode.SHIFT);
            keyRobot.keyType(k);
            if (k.isLetterKey())
                keyRobot.keyRelease(KeyCode.SHIFT);
        }
    }

    @FXML
    void clearAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button) {
            Button b = (Button)event.getSource();
                serialField.clear();
        }
    }

    @FXML
    void leftButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button) {
            rootController.showLoginPage(printer);
        }
    }
    
    @FXML
    void middleButtonAction(ActionEvent event) {
        if (rootController != null &&
            event.getSource() instanceof Button &&
            serialField.getText().length() == 6) {
            
            rootController.getRootServer().runResetPINTask(serialField.getText());
            rootController.showLoginPage(printer);
        }
    }

    @FXML
    void rightButtonAction(ActionEvent event) {
    }

    static final UnaryOperator<TextFormatter.Change> NUMERIC_FILTER = (change) -> {
            String newText = change.getControlNewText();
            if (newText.matches("([0-9]{0,4})")) { 
                return change;
            }
            return null;
        };
    private RootStackController rootController = null;
    private RootPrinter printer = null;
    private final Robot keyRobot = new Robot();
    
    
    @Override
    public void setRootStackController(RootStackController rootController) {
        this.rootController = rootController;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        translateLabels(resetPINTitle,
                        serialLabel,
                        resetPINPrompt,
                        backspaceKey);
        serialField.setPromptText("------");
        serialField.setTextFormatter(new TextFormatter <> (NUMERIC_FILTER));
        serialField.textProperty().addListener((observable, oldValue, newValue) -> {
                middleButton.setDisable(serialField.getText().length() != 6); 
            });

        zeroKey.setUserData(KeyCode.DIGIT0);
        oneKey.setUserData(KeyCode.DIGIT1);
        twoKey.setUserData(KeyCode.DIGIT2);
        threeKey.setUserData(KeyCode.DIGIT3);
        fourKey.setUserData(KeyCode.DIGIT4);
        fiveKey.setUserData(KeyCode.DIGIT5);
        sixKey.setUserData(KeyCode.DIGIT6);
        sevenKey.setUserData(KeyCode.DIGIT7);
        eightKey.setUserData(KeyCode.DIGIT8);
        nineKey.setUserData(KeyCode.DIGIT9);
        backspaceKey.setUserData(KeyCode.BACK_SPACE);

        resetPINPane.setVisible(false);
        rightButton.setVisible(false);
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
        if (!resetPINPane.isVisible()) {
            startUpdates();
            resetPINPane.setVisible(true);
        }
    }

    @Override
    public void hidePage() {
        stopUpdates();
        resetPINPane.setVisible(false);
    }
    
    @Override
    public boolean isVisible() {
        return resetPINPane.isVisible();
    }
}
