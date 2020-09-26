package celtech.coreUI.controllers;

import celtech.roboxbase.PrinterColourMap;
import celtech.coreUI.components.ColourChooserButton;
import celtech.coreUI.components.RestrictedTextField;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.printerControl.model.PrinterException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 * FXML Controller class
 *
 * @author ianhudson
 */
public class PrinterIDDialogController implements Initializable
{
    
    private Stenographer steno = StenographerFactory.getStenographer(PrinterIDDialogController.class.getName());
    
    private boolean okPressed = false;
    
    @FXML
    private VBox container;
    
    @FXML
    private Label dialogMessage;
    
    @FXML
    private Label dialogTitle;
    
    @FXML
    private Button okButton;
    
    @FXML
    private RestrictedTextField roboxNameField;
    
    @FXML
    private ToggleGroup colourButtonGroup;
    
    @FXML
    void okButtonPressed(MouseEvent event)
    {
        okPressed = true;
        myStage.close();
    }
    
    @FXML
    void cancelButtonPressed(MouseEvent event)
    {
        try
        {
            printerToUse.readPrinterID();
        } catch (PrinterException ex)
        {
            steno.error("Error reading printer ID");
        }
        okPressed = false;
        myStage.close();
    }
    
    private int buttonValue = -1;
    private Stage myStage = null;
    
    private ArrayList<Button> buttons = new ArrayList<>();
    
    private EventHandler<KeyEvent> textInputHandler = null;
    
    private Printer printerToUse = null;
    
    private PrinterColourMap colourMap = PrinterColourMap.getInstance();

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        
        textInputHandler = new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent event)
            {
                if (event.getCode() == KeyCode.ENTER)
                {
                    okButtonPressed(null);
                }
            }
        };
        
        container.addEventHandler(KeyEvent.KEY_PRESSED, textInputHandler);
        
        colourButtonGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>()
        {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue)
            {
                //The first time this gets set it will be to match the picked colour against the printer, so don't send anything through...
                if (newValue != null)
                {
                    try
                    {
                        printerToUse.setAmbientLEDColour(colourMap.displayToPrinterColour(((ColourChooserButton) newValue).getDisplayColour()));
                    } catch (PrinterException ex)
                    {
                        steno.error("Error writing printer ID");
                    }
                }
            }
        });
    }

    /**
     *
     * @param dialogStage
     */
    public void configure(Stage dialogStage)
    {
        myStage = dialogStage;
    }

    /**
     *
     * @return
     */
    public Color getChosenDisplayColour()
    {
        if (colourButtonGroup.getSelectedToggle() != null)
        {
            return ((ColourChooserButton) colourButtonGroup.getSelectedToggle()).getDisplayColour();
        } else
        {
            return null;
        }
    }

    /**
     *
     * @return
     */
    public String getChosenPrinterName()
    {
        return roboxNameField.getText();
    }

    /**
     *
     * @param printerToUse
     */
    public void setPrinterToUse(Printer printerToUse)
    {
        this.printerToUse = printerToUse;
    }

    /**
     *
     * @param colour
     */
    public void setChosenColour(Color colour)
    {
        for (Toggle toggle : colourButtonGroup.getToggles())
        {
            ColourChooserButton button = (ColourChooserButton) toggle;
            
            if (button.getDisplayColour().equals(colour))
            {
                colourButtonGroup.selectToggle(button);
            }
        }
    }

    /**
     *
     * @param printerName
     */
    public void setChosenPrinterName(String printerName)
    {
        roboxNameField.setText(printerName);
    }

    /**
     *
     * @return
     */
    public boolean okPressed()
    {
        return okPressed;
    }
}
