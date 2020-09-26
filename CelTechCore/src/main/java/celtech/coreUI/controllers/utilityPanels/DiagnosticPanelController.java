package celtech.coreUI.controllers.utilityPanels;

import celtech.Lookup;
import celtech.coreUI.controllers.StatusInsetController;
import celtech.roboxbase.printerControl.model.Printer;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;

/**
 * FXML Controller class
 *
 * @author Ian
 */
public class DiagnosticPanelController implements Initializable, StatusInsetController
{

    private Printer connectedPrinter = null;

    @FXML
    private RadioButton rbXLimit;

    @FXML
    private RadioButton rbYLimit;

    @FXML
    private RadioButton rbZLimit;

    @FXML
    private RadioButton rbZPositiveLimit;

    @FXML
    private RadioButton rbDoor;

    @FXML
    private RadioButton rbReel;

    @FXML
    private RadioButton rbLoaded0;

    @FXML
    private RadioButton rbIndex0;

    @FXML
    private Label extruder0Label;

    @FXML
    private Label extruder0LoadedLabel;

    @FXML
    private Label extruder0IndexLabel;

    @FXML
    private Label extruder1Label;

    @FXML
    private Label extruder1LoadedLabel;

    @FXML
    private Label extruder1IndexLabel;

    @FXML
    private RadioButton rbLoaded1;

    @FXML
    private RadioButton rbIndex1;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        Lookup.getSelectedPrinterProperty().addListener(new ChangeListener<Printer>()
        {

            @Override
            public void changed(ObservableValue<? extends Printer> observable, Printer oldValue,
                Printer newValue)
            {
                if (connectedPrinter != null)
                {
                    unbindFromPrinter(connectedPrinter);
                }

                if (newValue != null)
                {
                    bindToPrinter(newValue);
                }
            }
        });

        rbXLimit.setDisable(true);
        rbYLimit.setDisable(true);
        rbZLimit.setDisable(true);
        rbZPositiveLimit.setDisable(true);
        rbDoor.setDisable(true);
        rbReel.setDisable(true);
        rbLoaded0.setDisable(true);
        rbIndex0.setDisable(true);
        rbLoaded1.setDisable(true);
        rbIndex1.setDisable(true);
    }

    private void unbindFromPrinter(Printer printer)
    {
        if (connectedPrinter != null)
        {
            rbXLimit.selectedProperty().unbind();
            rbYLimit.selectedProperty().unbind();
            rbZLimit.selectedProperty().unbind();
            rbZPositiveLimit.selectedProperty().unbind();
            rbDoor.selectedProperty().unbind();
            rbReel.selectedProperty().unbind();
            rbLoaded0.selectedProperty().unbind();
            rbIndex0.selectedProperty().unbind();
            rbLoaded1.selectedProperty().unbind();
            rbIndex1.selectedProperty().unbind();
            
            rbXLimit.setSelected(false);
            rbYLimit.setSelected(false);
            rbZLimit.setSelected(false);
            rbZPositiveLimit.setSelected(false);
            rbDoor.setSelected(false);
            rbReel.setSelected(false);
            rbLoaded0.setSelected(false);
            rbIndex0.setSelected(false);
            rbLoaded1.setSelected(false);
            rbIndex1.setSelected(false);

            extruder1Label.visibleProperty().unbind();
            extruder1Label.setVisible(false);
            extruder1LoadedLabel.visibleProperty().unbind();
            extruder1LoadedLabel.setVisible(false);
            extruder1IndexLabel.visibleProperty().unbind();
            extruder1IndexLabel.setVisible(false);

            connectedPrinter = null;
        }
    }

    private void bindToPrinter(Printer printer)
    {
        if (connectedPrinter == null)
        {
            connectedPrinter = printer;

            rbXLimit.selectedProperty().bind(printer.getPrinterAncillarySystems().
                xStopSwitchProperty());
            rbYLimit.selectedProperty().bind(printer.getPrinterAncillarySystems().
                yStopSwitchProperty());
            rbZLimit.selectedProperty().bind(printer.getPrinterAncillarySystems().
                zStopSwitchProperty());
            rbZPositiveLimit.selectedProperty().bind(printer.getPrinterAncillarySystems().
                zTopStopSwitchProperty());

            rbDoor.selectedProperty().bind(printer.getPrinterAncillarySystems().doorOpenProperty());
            rbReel.selectedProperty().bind(printer.getPrinterAncillarySystems().
                reelButtonProperty());

            rbLoaded0.selectedProperty().bind(
                printer.extrudersProperty().get(0).filamentLoadedProperty());
            rbLoaded0.disableProperty().bind(
                Lookup.getUserPreferences().detectLoadedFilamentProperty());
            rbIndex0.selectedProperty().bind(printer.extrudersProperty().get(0).
                indexWheelStateProperty());

            rbLoaded1.selectedProperty().bind(
                printer.extrudersProperty().get(1).filamentLoadedProperty());
            rbLoaded1.disableProperty().bind(
                Lookup.getUserPreferences().detectLoadedFilamentProperty());
            rbIndex1.selectedProperty().bind(printer.extrudersProperty().get(1).
                indexWheelStateProperty());

            ReadOnlyBooleanProperty extruder0Visible = printer.extrudersProperty().get(0).
                isFittedProperty();
            extruder0Label.visibleProperty().bind(extruder0Visible);
            extruder0LoadedLabel.visibleProperty().bind(extruder0Visible);
            extruder0IndexLabel.visibleProperty().bind(extruder0Visible);
            rbLoaded0.visibleProperty().bind(extruder0Visible);
            rbIndex0.visibleProperty().bind(extruder0Visible);

            ReadOnlyBooleanProperty extruder1Visible = printer.extrudersProperty().get(1).
                isFittedProperty();
            extruder1Label.visibleProperty().bind(extruder1Visible);
            extruder1LoadedLabel.visibleProperty().bind(extruder1Visible);
            extruder1IndexLabel.visibleProperty().bind(extruder1Visible);
            rbLoaded1.visibleProperty().bind(extruder1Visible);
            rbIndex1.visibleProperty().bind(extruder1Visible);

        }
    }

}
