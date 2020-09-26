package celuk.groot.controllers;

import celuk.groot.remote.RootPrinter;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class PrinterColourController implements Initializable, Page {
    
    @FXML
    private StackPane printerColourPane;
    @FXML
    private Label printerColourTitle;

    @FXML
    private Label colourPrompt;    

    @FXML
    private Label savePrompt;    
    
    @FXML
    private Button colour1Button;
    @FXML
    private Button colour2Button;
    @FXML
    private Button colour3Button;
    @FXML
    private Button colour4Button;
    @FXML
    private Button colour5Button;
    @FXML
    private Button colour6Button;
    @FXML
    private Button colour7Button;
    @FXML
    private Button colour8Button;
    @FXML
    private Button colour9Button;
    @FXML
    private Button colour10Button;
    @FXML
    private Button colour11Button;
    @FXML
    private Button colour12Button;
    @FXML
    private Button colour13Button;
    @FXML
    private Button colour14Button;
    @FXML
    private Button colour15Button;
    @FXML
    private Button colour16Button;
    @FXML
    private Button colour17Button;
    @FXML
    private Button colour18Button; // This one will be used as the custom colour button.

    @FXML
    private Button leftButton;
    @FXML
    private Button middleButton;
    @FXML
    private Button rightButton;

    @FXML
    void colourAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button) {
            Button b = (Button)event.getSource();
            if (b != selectedButton) {
                String selectedColour = (String)b.getUserData();
                deselectColourButton();
                selectColourButton(b, selectedColour);
                printer.runSwitchAmbientLightTask(selectedColour);
            }
        }
    }
    
    @FXML
    void leftButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button) {
            printer.runSwitchAmbientLightTask("on");
            rootController.showMainMenu(printer);
        }
    }
    
    @FXML
    void middleButtonAction(ActionEvent event) {
    }

    @FXML
    void rightButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button) {
            if (selectedButton != null && selectedButton != currentButton) {
                printer.runChangePrinterColourTask((String)selectedButton.getUserData());
                rootController.showHomePage(printer);
            }
        }
    }

    private final static PseudoClass DARK_PS = PseudoClass.getPseudoClass("dark");
    private final static PseudoClass SELECTED_PS = PseudoClass.getPseudoClass("selected");
    private RootStackController rootController = null;
    private RootPrinter printer = null;
    Button colourButtons[] = null;
    Button selectedButton = null;
    Button currentButton = null;
    
    @Override
    public void setRootStackController(RootStackController rootController) {
        this.rootController = rootController;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        translateLabels(colourPrompt, printerColourTitle, savePrompt);
        colourButtons = new Button[] {
            colour1Button,
            colour2Button,
            colour3Button,
            colour4Button,
            colour5Button,
            colour6Button,
            colour7Button,
            colour8Button,
            colour9Button,
            colour10Button,
            colour11Button,
            colour12Button,
            colour13Button,
            colour14Button,
            colour15Button,
            colour16Button,
            colour17Button,
            colour18Button,
        };
       String colours[] = new String[] {
            "#7F7F7F", "#FFFFFF", "#000000",
            "#00007F", "#007F00", "#7F0000",
            "#FF7F7F", "#7FFF7F", "#7F7FFF",
            "#7FFF00", "#00FF00", "#00FF96",
            "#00FFFF", "#0000FF", "#7F00FF",
            "#FF00FF", "#FF0000", "#FF7F00",
            "#FFFF00"
        };
        for (int i = 0; i < colourButtons.length; ++i) {
            setButtonColour(colourButtons[i], colours[i]);
        }
        printerColourPane.setVisible(false);
        middleButton.setVisible(false);
    }
    
    @Override
    public void startUpdates() {
        Platform.runLater( () -> {
            String webColour = printer.getCurrentStatusProperty().get().getPrinterWebColourString();
            selectPrinterColour(webColour);
        });
    }
    
    @Override
    public void stopUpdates() {
        printer = null;
    }

    @Override
    public void displayPage(RootPrinter printer) {
        this.printer = printer;
        if (!printerColourPane.isVisible()) {
            startUpdates();
            printerColourPane.setVisible(true);
        }
    }

    @Override
    public void hidePage() {
        stopUpdates();
        printerColourPane.setVisible(false);
    }
    
    @Override
    public boolean isVisible() {
        return printerColourPane.isVisible();
    }
    
    private void setButtonColour(Button b, String colour) {
            b.setStyle("-fx-background-color: " + colour + ";");
            b.setUserData(colour);
            boolean isDark = MachineDetails.getComplimentaryOption(colour,
                                                                   true,
                                                                   false);
            b.pseudoClassStateChanged(DARK_PS, isDark);
    }
    
    private void deselectColourButton() {
        if (selectedButton != null) {
            selectedButton.pseudoClassStateChanged(SELECTED_PS, false);
            selectedButton = null;
        }
    }

    private void selectColourButton(Button buttonToSelect, String buttonColour) {
        buttonToSelect.pseudoClassStateChanged(SELECTED_PS, true);
        selectedButton = buttonToSelect;
        rightButton.setDisable(selectedButton == null || selectedButton == currentButton);
    }

    private void selectPrinterColour(String webColour) {
        deselectColourButton();        
        for (Button b : colourButtons) {
            String buttonColour = (String)b.getUserData();
            if (buttonColour.equalsIgnoreCase(webColour)) {
                selectedButton = b;
                break;
            }
        }
        
        if (selectedButton == null) {
            selectedButton = colour18Button;
            setButtonColour(colour18Button, webColour);
        }
        currentButton = selectedButton;
        selectColourButton(selectedButton, webColour);
    }
}
