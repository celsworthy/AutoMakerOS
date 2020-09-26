package celuk.groot.controllers;

import celuk.language.I18n;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.robot.Robot;

public class KeyboardController implements Initializable {
    
    @FXML
    private GridPane keyboardPane;
    
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
    private Button aKey;
    @FXML
    private Button bKey;
    @FXML
    private Button cKey;
    @FXML
    private Button dKey;
    @FXML
    private Button eKey;
    @FXML
    private Button fKey;
    @FXML
    private Button gKey;
    @FXML
    private Button hKey;
    @FXML
    private Button iKey;
    @FXML
    private Button jKey;
    @FXML
    private Button kKey;
    @FXML
    private Button lKey;
    @FXML
    private Button mKey;
    @FXML
    private Button nKey;
    @FXML
    private Button oKey;
    @FXML
    private Button pKey;
    @FXML
    private Button qKey;
    @FXML
    private Button rKey;
    @FXML
    private Button sKey;
    @FXML
    private Button tKey;
    @FXML
    private Button uKey;
    @FXML
    private Button vKey;
    @FXML
    private Button wKey;
    @FXML
    private Button xKey;
    @FXML
    private Button yKey;
    @FXML
    private Button zKey;
    @FXML
    private Button pointKey;
    @FXML
    private Button spaceKey;
    @FXML
    private Button dashKey;
    @FXML
    private Button shiftKey;
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
    void keyboardAction(ActionEvent event) {
        if (event.getSource() instanceof Button) {
            Button b = (Button)event.getSource();
            if (focusFields != null && focusFields.length > 0) {
                boolean isFocused = false;
                for (TextInputControl ff : focusFields) {
                    if (ff.isFocused()) {
                        isFocused = true;
                        break;
                    }
                }
                if (!isFocused) {
                    TextInputControl ff = focusFields[0];
                    ff.requestFocus();
                    // Setting the caret position clears the selection, so the key
                    // does not replace the selected text.
                    String text = ff.getText();
                    if (text != null)
                        ff.positionCaret(text.length());
                }
            }
            KeyCode k = (KeyCode)(b.getUserData());
            if (shiftRequired)
                keyRobot.keyPress(KeyCode.SHIFT);
            keyRobot.keyType(k);
            if (shiftRequired)
                keyRobot.keyRelease(KeyCode.SHIFT);
        }
    }
    
    @FXML
    void shiftAction(ActionEvent event) {
        if (event.getSource() instanceof Button) {
            setCase(!shiftRequired);
        }
    }

    @FXML
    void enterAction(ActionEvent event) {
        if (event.getSource() instanceof Button) {
            if (focusFields != null && focusFields.length > 0) {
                TextInputControl ff = null;
                for (int i = 0; i < focusFields.length; ++i) {
                    if (focusFields[i].isFocused()) {
                        // Move focus to the next field, 
                        ff = focusFields[(i + 1) % focusFields.length];
                        break;
                    }
                }
                if (ff == null)
                    ff = focusFields[0];
                
                if (!ff.isFocused()) {
                    ff.requestFocus();
                    // Setting the caret position clears the selection, so the key
                    // does not replace the selected text.
                    ff.positionCaret(ff.getText().length());
                }
            }
        }
    }

    private final Robot keyRobot = new Robot();
    private boolean shiftRequired = false;
    private String keyboardUppercaseText = "keyboard.uppercase";
    private String keyboardLowercaseText = "keyboard.lowercase";
    private TextInputControl focusFields[] = null;
    private Button alphaKeys[] = null;
    private Button digitKeys[] = null;
    private String digitTextLower[] = null;
    private String digitTextUpper[] = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        alphaKeys = new Button[] {
            aKey,
            bKey,
            cKey,
            dKey,
            eKey,
            fKey,
            gKey,
            hKey,
            iKey,
            jKey,
            kKey,
            lKey,
            mKey,
            nKey,
            oKey,
            pKey,
            qKey,
            rKey,
            sKey,
            tKey,
            uKey,
            vKey,
            wKey,
            xKey,
            yKey,
            zKey
        };
        
        digitKeys = new Button[] {
            zeroKey,
            oneKey,
            twoKey,
            threeKey,
            fourKey,
            fiveKey,
            sixKey,
            sevenKey,
            eightKey,
            nineKey
        };

        digitTextLower = new String[] {
            "0",
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9"
        };

        digitTextUpper = new String[] {
            ")",
            "!",
            "@",
            "#",
            "$",
            "%",
            "^",
            "&",
            "*",
            "("
        };

        backspaceKey.setText(I18n.t(backspaceKey.getText()));
        enterKey.setText(I18n.t(enterKey.getText()));
        shiftKey.setText(I18n.t(shiftKey.getText()));
        spaceKey.setText(I18n.t(spaceKey.getText()));
        keyboardLowercaseText = I18n.t(keyboardLowercaseText);
        keyboardUppercaseText = I18n.t(keyboardUppercaseText);
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

        aKey.setUserData(KeyCode.A);
        bKey.setUserData(KeyCode.B);
        cKey.setUserData(KeyCode.C);
        dKey.setUserData(KeyCode.D);
        eKey.setUserData(KeyCode.E);
        fKey.setUserData(KeyCode.F);
        gKey.setUserData(KeyCode.G);
        hKey.setUserData(KeyCode.H);
        iKey.setUserData(KeyCode.I);
        jKey.setUserData(KeyCode.J);
        kKey.setUserData(KeyCode.K);
        lKey.setUserData(KeyCode.L);
        mKey.setUserData(KeyCode.M);
        nKey.setUserData(KeyCode.N);
        oKey.setUserData(KeyCode.O);
        pKey.setUserData(KeyCode.P);
        qKey.setUserData(KeyCode.Q);
        rKey.setUserData(KeyCode.R);
        sKey.setUserData(KeyCode.S);
        tKey.setUserData(KeyCode.T);
        uKey.setUserData(KeyCode.U);
        vKey.setUserData(KeyCode.V);
        wKey.setUserData(KeyCode.W);
        xKey.setUserData(KeyCode.X);
        yKey.setUserData(KeyCode.Y);
        zKey.setUserData(KeyCode.Z);

        backspaceKey.setUserData(KeyCode.BACK_SPACE);
        shiftKey.setUserData(KeyCode.SHIFT);
        dashKey.setUserData(KeyCode.MINUS);
        pointKey.setUserData(KeyCode.PERIOD);
        enterKey.setUserData(KeyCode.ENTER);
        spaceKey.setUserData(KeyCode.SPACE);
    }
    
    public void setFocusFields(TextInputControl focusFields[]) {
        this.focusFields = focusFields;
    }
    
    public TextInputControl[] getFocusFields() {
        return focusFields;
    }

    public void setCase(boolean uppercase) {
        if (uppercase) {
            shiftKey.setText(keyboardLowercaseText);
            shiftRequired = true;
            for (Button key : alphaKeys)
                key.setText(key.getText().toUpperCase());
            for (int i = 0; i < digitKeys.length; ++i) {
                digitKeys[i].setText(digitTextUpper[i]);
            }
            dashKey.setText("_");
            pointKey.setText(">");
        }
        else {
            shiftKey.setText(keyboardUppercaseText);
            shiftRequired = false;
            for (Button key : alphaKeys)
                key.setText(key.getText().toLowerCase());
            for (int i = 0; i < digitKeys.length; ++i) {
                digitKeys[i].setText(digitTextLower[i]);
            }
            dashKey.setText("-");
            pointKey.setText(".");
        }
    }
}
