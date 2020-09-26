package celtech.coreUI.controllers.panels;

import celtech.Lookup;
import celtech.coreUI.components.RestrictedTextField;
import celtech.coreUI.controllers.StatusInsetController;
import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.printerControl.comms.commands.GCodeMacros;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.printerControl.model.PrinterException;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class GCodePanelController implements Initializable, StatusInsetController
{

    private final Stenographer steno = StenographerFactory.getStenographer(
            GCodePanelController.class.getName());
    private ListChangeListener<String> gcodeTranscriptListener = null;
    private Printer currentPrinter = null;

    @FXML
    private VBox gcodeEditParent;

    @FXML
    private RestrictedTextField gcodeEntryField;

    @FXML
    private ListView<String> gcodeTranscript;

    @FXML
    private Button sendGCodeButton;

    @FXML
    private HBox gcodePanel;

    @FXML
    void sendGCodeM(MouseEvent event)
    {
        fireGCodeAtPrinter();
    }

    @FXML
    void sendGCodeA(ActionEvent event)
    {
        fireGCodeAtPrinter();
    }

    Optional<String> getGCodeFileToUse(String text) {
        String macroFilename;
        String gcodeFileWithPathApp;
        if (text.startsWith("!!"))
        {
            // with !! use scoring technique. Use current printer type
            // and head type. Optionally add #N0 or #N1 to macro name to specify a nozzle
            macroFilename = text.substring(2);

            GCodeMacros.NozzleUseIndicator nozzleUse = GCodeMacros.NozzleUseIndicator.DONT_CARE;

            int hashIx = macroFilename.indexOf('#');
            if (hashIx != -1)
            {
                String nozzleSelect = macroFilename.substring(hashIx + 2);
                if ("0".equals(nozzleSelect))
                {
                    nozzleUse = GCodeMacros.NozzleUseIndicator.NOZZLE_0;
                } else if ("1".equals(nozzleSelect))
                {
                    nozzleUse = GCodeMacros.NozzleUseIndicator.NOZZLE_1;
                }
                macroFilename = macroFilename.substring(0, hashIx);
            }

            try
            {
                gcodeFileWithPathApp = GCodeMacros.getFilename(macroFilename,
                        Optional.of(currentPrinter.findPrinterType()),
                        currentPrinter.headProperty().get().typeCodeProperty().get(),
                        nozzleUse,
                        GCodeMacros.SafetyIndicator.DONT_CARE);
            } catch (FileNotFoundException ex)
            {
                gcodeFileWithPathApp = "";
            }
        } else
        {
            macroFilename = text.substring(1);
            gcodeFileWithPathApp = BaseConfiguration.getCommonApplicationDirectory() + BaseConfiguration.macroFileSubpath + macroFilename + ".gcode";
        }
        String gcodeFileWithPathUser = BaseConfiguration.getUserStorageDirectory() + BaseConfiguration.macroFileSubpath + macroFilename + ".gcode";

        if (new File(gcodeFileWithPathUser).exists())
        {
            return Optional.of(gcodeFileWithPathUser);
        } else if (new File(gcodeFileWithPathApp).exists())
        {
            return Optional.of(gcodeFileWithPathApp);
        } else
        {
            steno.error("Failed to find macro: " + macroFilename);
            return Optional.empty();
        }
    }

    protected void fireGCodeAtPrinter() {
        gcodeEntryField.selectAll();
        String text = gcodeEntryField.getText();

        if (text.startsWith("!"))
        {
            Optional<String> gcodeFileToUse = getGCodeFileToUse(text);

            //See if we can run a macro
            if (currentPrinter != null && gcodeFileToUse.isPresent())
            {
                try
                {
                    currentPrinter.executeGCodeFile(gcodeFileToUse.get(), false);
                    currentPrinter.gcodeTranscriptProperty().add(text);
                } catch (PrinterException ex)
                {
                    steno.exception("Failed to run macro: " + text, ex);
                }
            } else
            {
                steno.error("Can't run requested macro: " + text);
            }
        } else if (!text.equals(""))
        {
            Lookup.getSelectedPrinterProperty().get().sendRawGCode(text.toUpperCase(), true);
        }
    }

    private void selectLastItemInTranscript()
    {
        gcodeTranscript.getSelectionModel().selectLast();
        gcodeTranscript.scrollTo(gcodeTranscript.getSelectionModel().getSelectedIndex());
    }

    private boolean suppressReactionToGCodeEntryChange = false;

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        gcodeEntryField.disableProperty().bind(
                Lookup.getUserPreferences().advancedModeProperty().not());
        sendGCodeButton.disableProperty().bind(
                Lookup.getUserPreferences().advancedModeProperty().not());

        gcodeTranscriptListener = (ListChangeListener.Change<? extends String> change) ->
        {
            while (change.next())
            {
            }

            suppressReactionToGCodeEntryChange = true;
            selectLastItemInTranscript();
            suppressReactionToGCodeEntryChange = false;
        };

        gcodeTranscript.selectionModelProperty().get().selectedItemProperty().addListener(new ChangeListener<String>()
        {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
            {
                if (!suppressReactionToGCodeEntryChange)
                {
                    gcodeEntryField.setText(gcodeTranscript.getSelectionModel().getSelectedItem());
                }
            }
        });

        Lookup.getSelectedPrinterProperty().addListener(
                (ObservableValue<? extends Printer> ov, Printer t, Printer t1) ->
        {
            if (currentPrinter != null)
            {
                currentPrinter.gcodeTranscriptProperty().removeListener(gcodeTranscriptListener);
            }

            if (t1 != null)
            {
                gcodeTranscript.setItems(t1.gcodeTranscriptProperty());
                t1.gcodeTranscriptProperty().addListener(gcodeTranscriptListener);
            } else
            {
                gcodeTranscript.setItems(null);
            }
            currentPrinter = t1;
        });

        gcodeEditParent.visibleProperty().bind(Lookup.getSelectedPrinterProperty().isNotNull());

        gcodeEntryField.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent t) ->
        {
            if (t.getCode() == KeyCode.UP)
            {
                gcodeTranscript.getSelectionModel().selectPrevious();
                t.consume();
            } else if (t.getCode() == KeyCode.DOWN)
            {
                gcodeTranscript.getSelectionModel().selectNext();
                t.consume();
            }
        });

        gcodeTranscript.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent t) ->
        {
            if (t.getCode() == KeyCode.ENTER)
            {
                fireGCodeAtPrinter();
            }
        });

        gcodeTranscript.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() > 1)
                {
                    fireGCodeAtPrinter();
                }
            }
        });
    }
}
