package celtech.coreUI.controllers.panels;

import celtech.Lookup;
import celtech.appManager.ApplicationMode;
import celtech.appManager.ApplicationStatus;
import celtech.coreUI.DisplayManager;
import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.printerControl.model.Head;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.printerControl.model.PrinterIdentity;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 *
 * @author Ian
 */
public class AboutPanelController implements Initializable
{

    private final Clipboard clipboard = Clipboard.getSystemClipboard();
    private final ClipboardContent content = new ClipboardContent();

    @FXML
    private Label roboxSerialNumber;

    @FXML
    private Label roboxElectronicsVersion;

    @FXML
    private Label headSerialNumber;
    
    @FXML
    private Label version;

    @FXML
    private Label infoLabel;

    @FXML
    private Text bdLabel;

    @FXML
    private Text bdNames;

    @FXML
    private Text hwengLabel;

    @FXML
    private Text hwengNames;

    @FXML
    private Text swengLabel;

    @FXML
    private Text swengNames;

    @FXML
    private Text amTitleText1;

    @FXML
    private Text amTitleText2;

    @FXML
    private Text amTitleText3;

    @FXML
    private VBox logoBox;

    private Printer currentPrinter = null;

    @FXML
    private void viewREADME(ActionEvent event)
    {
        ApplicationStatus.getInstance().setMode(ApplicationMode.WELCOME);
    }
    
    @FXML
    private void okPressed(ActionEvent event)
    {
        ApplicationStatus.getInstance().returnToLastMode();
    }

    @FXML
    private void copyPrinterSerialNumber(ActionEvent event)
    {
        content.putString(roboxSerialNumber.getText());
        clipboard.setContent(content);
    }

    @FXML
    private void copyHeadSerialNumber(ActionEvent event)
    {
        content.putString(headSerialNumber.getText());
        clipboard.setContent(content);
    }

    @FXML
    private void systemInformationPressed(ActionEvent event)
    {
        ApplicationStatus.getInstance().setMode(ApplicationMode.SYSTEM_INFORMATION);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        version.setText(BaseConfiguration.getApplicationVersion());

        DisplayManager.getInstance().getDisplayScalingModeProperty().addListener(new ChangeListener<DisplayManager.DisplayScalingMode>()
        {
            @Override
            public void changed(ObservableValue<? extends DisplayManager.DisplayScalingMode> ov, DisplayManager.DisplayScalingMode t, DisplayManager.DisplayScalingMode scalingMode)
            {
                switch (scalingMode)
                {
                    case NORMAL:
                        infoLabel.setStyle("-fx-font-size: 21px");
                        hwengLabel.setStyle("-fx-font-size: 21px");
                        hwengNames.setStyle("-fx-font-size: 21px");
                        swengLabel.setStyle("-fx-font-size: 21px");
                        swengNames.setStyle("-fx-font-size: 21px");
                        bdLabel.setStyle("-fx-font-size: 21px");
                        bdNames.setStyle("-fx-font-size: 21px");
                        amTitleText1.setStyle("-fx-font-size: 100px");
                        amTitleText2.setStyle("-fx-font-size: 100px");
                        amTitleText3.setStyle("-fx-font-size: 14px");
                        logoBox.setScaleX(1);
                        logoBox.setScaleY(1);
                        break;
                    default:
                        infoLabel.setStyle("-fx-font-size: 14px");
                        hwengLabel.setStyle("-fx-font-size: 14px");
                        hwengNames.setStyle("-fx-font-size: 14px");
                        swengLabel.setStyle("-fx-font-size: 14px");
                        swengNames.setStyle("-fx-font-size: 14px");
                        bdLabel.setStyle("-fx-font-size: 14px");
                        bdNames.setStyle("-fx-font-size: 14px");
                        amTitleText1.setStyle("-fx-font-size: 70px");
                        amTitleText2.setStyle("-fx-font-size: 70px");
                        amTitleText3.setStyle("-fx-font-size: 10px");
                        logoBox.setScaleX(0.8);
                        logoBox.setScaleY(0.8);
                        break;

                }
            }
        });

        Lookup.getSelectedPrinterProperty().addListener((ObservableValue<? extends Printer> observable, Printer oldValue, Printer newValue) ->
        {
            bindToPrinter(newValue);
        });
        bindToPrinter(Lookup.getSelectedPrinterProperty().get());
    }

    private void updateHeadData(Head head)
    {
        if (head != null)
        {
            headSerialNumber.setText(head.getFormattedSerial());
        } else
        {
            headSerialNumber.setText("");
        }
    }

    private void updateIDData(PrinterIdentity identity)
    {
        if (identity != null)
        {
            roboxSerialNumber.setText(identity.toString());
            if (!identity.printerelectronicsVersionProperty().get().isEmpty())
                roboxElectronicsVersion.setText("E" + identity.printerelectronicsVersionProperty().get());
            else
                roboxElectronicsVersion.setText("");
        } else
        {
            roboxSerialNumber.setText("");
            roboxElectronicsVersion.setText("");
        }
    }
    
    private ChangeListener<Head> headChangeListener = new ChangeListener<Head>()
    {
        @Override
        public void changed(ObservableValue<? extends Head> observable, Head oldValue, Head newValue)
        {
            updateHeadData(newValue);
        }
    };

    private void bindToPrinter(Printer printer)
    {
        if (currentPrinter != null)
        {
            currentPrinter.headProperty().removeListener(headChangeListener);
        }

        if (printer != null)
        {
            printer.headProperty().addListener(headChangeListener);
            updateHeadData(printer.headProperty().get());
            updateIDData(printer.getPrinterIdentity());
        } else
        {
            updateHeadData(null);
            updateIDData(null);
        }

        currentPrinter = printer;
    }
}
