package celuk.groot.controllers;

import celuk.groot.remote.RootPrinter;
import celuk.language.I18n;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.robot.Robot;

public class ConsoleController implements Initializable, Page {
    private static final int MAX_HISTORY_SIZE = 200;
    
    @FXML
    private StackPane consolePane;
    @FXML
    private ListView<String> gcodeHistory;
    @FXML
    private TextField gcodeField;
    @FXML
    private Button gcodeClear;
    
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
    private Button xKey;
    @FXML
    private Button yKey;
    @FXML
    private Button zKey;
    @FXML
    private Button eKey;
    @FXML
    private Button dKey;
    @FXML
    private Button bKey;
    @FXML
    private Button gKey;
    @FXML
    private Button mKey;
    @FXML
    private Button fKey;
    @FXML
    private Button sKey;
    @FXML
    private Button tKey;
    @FXML
    private Button pointKey;
    @FXML
    private Button spaceKey;
    @FXML
    private Button dashKey;
    @FXML
    private Button backspaceKey;
    @FXML
    private Button enterKey;

    @FXML
    private Button leftButton;
    @FXML
    private Button middleButton;
    @FXML
    private Button rightButton;

    @FXML
    void gcodeHistoryAction(MouseEvent event) {
        if (rootController != null && event.getSource() instanceof ListView) {
            String line = gcodeHistory.getSelectionModel().getSelectedItem();
            if (line != null) {
                String l = line.trim();
                if (!l.isEmpty())
                    gcodeField.setText(l);
            }
        }
    }

    @FXML
    void gcodeClearAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button) {
            gcodeField.clear();
        }
    }

    @FXML
    void keypadAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button) {
            Button b = (Button)event.getSource();
            gcodeHistory.getSelectionModel().clearSelection();
            if (!gcodeField.isFocused()) {
                gcodeField.requestFocus();
                // Setting the caret position clears the selection, so the key
                // does not replace the selected text.
                gcodeField.positionCaret(gcodeField.getText().length());
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
    void enterAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button) {
            gcodeHistory.getSelectionModel().clearSelection();
            String gCode = gcodeField.getText().trim();
            if (!gCode.isEmpty()) {
                try {
                    String response = printer.runSendGCodeTask(gCode).get();
                    gcodeHistory.getItems().add(gCode);
                    gcodeField.clear();
                    if (!response.isEmpty()) {
//                        List<String> lines = new ArrayList<>();
//                        int lastIndex = 0;
//                        int nextIndex = response.indexOf("\r\n");
                        String[] lines = response.split("\r\n");
                        gcodeHistory.getItems().addAll(lines);
                        ObservableList<String> history = gcodeHistory.getItems();
                        if (history.size() > MAX_HISTORY_SIZE)
                            history.remove(0, history.size() - MAX_HISTORY_SIZE);
                    }
                    gcodeHistory.scrollTo(gcodeHistory.getItems().size() - 1);
                } catch (InterruptedException | ExecutionException ex) {
                }
            }
        }
    }

    @FXML
    void leftButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button) {
            if (returnToControl)
                rootController.showControlPage(printer);
            else
                rootController.showMainMenu(printer);
        }
    }
    
    @FXML
    void middleButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button)
            rootController.showHomePage(printer);
    }

    @FXML
    void rightButtonAction(ActionEvent event) {
    }

    private RootStackController rootController = null;
    private RootPrinter printer = null;
    private boolean returnToControl = false;
    private final Robot keyRobot = new Robot();
    private final ChangeListener<String> historyListener = (o, ov, nv) -> {
        if (nv != null) {
            String v = nv.trim();
            if (!v.isEmpty())
                gcodeField.setText(v);
            gcodeHistory.getSelectionModel().clearSelection();
        }
        // System.out.println("historyListener - ov = \"" + ov + "\" nv = \"" + nv + "\"");
    };
    
    @Override
    public void setRootStackController(RootStackController rootController) {
        this.rootController = rootController;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        translateLabels(spaceKey);
        
        gcodeField.setPromptText(I18n.t("console.enterGCode"));
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

        bKey.setUserData(KeyCode.B);
        dKey.setUserData(KeyCode.D);
        eKey.setUserData(KeyCode.E);
        fKey.setUserData(KeyCode.F);
        gKey.setUserData(KeyCode.G);
        mKey.setUserData(KeyCode.M);
        sKey.setUserData(KeyCode.S);
        tKey.setUserData(KeyCode.T);
        xKey.setUserData(KeyCode.X);
        yKey.setUserData(KeyCode.Y);
        zKey.setUserData(KeyCode.Z);

        backspaceKey.setUserData(KeyCode.BACK_SPACE);
        dashKey.setUserData(KeyCode.MINUS);
        pointKey.setUserData(KeyCode.PERIOD);
        enterKey.setUserData(KeyCode.ENTER);
        spaceKey.setUserData(KeyCode.SPACE);
        
        //gcodeHistory.getSelectionModel().selectedItemProperty().addListener(historyListener);
        consolePane.setVisible(false);
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
        if (!consolePane.isVisible()) {
            startUpdates();
            consolePane.setVisible(true);
        }
        gcodeField.requestFocus();
        gcodeField.clear();
    }

    @Override
    public void hidePage() {
        stopUpdates();
        consolePane.setVisible(false);
    }
    
    @Override
    public boolean isVisible() {
        return consolePane.isVisible();
    }

    public void setReturnToControl(boolean returnToControl) {
        this.returnToControl = returnToControl;
    }
}
