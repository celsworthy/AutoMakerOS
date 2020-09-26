package celuk.groot.controllers;

import celuk.groot.remote.FilamentDetails;
import celuk.groot.remote.PrinterStatusResponse;
import celuk.groot.remote.RootPrinter;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ControlController implements Initializable, Page {
    
    static final String NO_HEAD_KEY = "<none>";
    static final String DEFAULT_HEAD_KEY = "<default>";

    @FXML
    private StackPane controlPane;
    @FXML
    private VBox controlVBox;
    @FXML
    private GridPane extruderGridPane;
    @FXML
    private Button e1m50Button;
    @FXML
    private Button e1m20Button;
    @FXML
    private Button e1EjectButton;
    @FXML
    private Button e1p20Button;
    @FXML
    private Button e1p50Button;
    @FXML
    private Button e1HeatButton;
    @FXML
    private Label ejectLabel;
    @FXML
    private Label extrudeLabel;
    @FXML
    private Label heatLabel;
    @FXML
    private Label retractLabel;
    @FXML
    private Button e2m50Button;
    @FXML
    private Button e2m20Button;
    @FXML
    private Button e2EjectButton;
    @FXML
    private Button e2p20Button;
    @FXML
    private Button e2p50Button;
    @FXML
    private Button e2HeatButton;
    @FXML
    private HBox xAxisHBox;
    @FXML
    private Button homeButton;
    @FXML
    private Button xm10Button;
    @FXML
    private Button xHomeButton;
    @FXML
    private Button xp10Button;
    @FXML
    private Button ambientLightButton;
    @FXML
    private Button zp10Button;
    @FXML
    private Button zHomeButton;
    @FXML
    private Button zm10Button;
    @FXML
    private Button yp10Button;
    @FXML
    private Button yHomeButton;
    @FXML
    private Button ym10Button;
    @FXML
    private Button heatBedButton;
    @FXML
    private Button unlockButton;
    @FXML
    private ImageView headImageView;
    @FXML
    private Button selectButton;
    @FXML
    private Button valveButton;
    @FXML
    private Button headlightButton;
    @FXML
    private Button fanButton;
    @FXML
    private Button leftButton;
    @FXML
    private Button middleButton;
    @FXML
    private Button rightButton;

    private final SimpleBooleanProperty headAttachedProperty = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty hasMultipleNozzlesProperty = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty isDualMaterialHeadProperty = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty hasValvesProperty = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty canEject1Property = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty canExtrude1Property = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty canRetract1Property = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty canEject2Property = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty canExtrude2Property = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty canRetract2Property = new SimpleBooleanProperty(false);
    private SimpleBooleanProperty[][] filamentProperties = null;
    private String ambientLightState = "on";
    private final SimpleBooleanProperty bedHeatState = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty fanState = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty heaterSState = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty heaterTState = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty headlightState = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty nozzleSelectState = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty valveState = new SimpleBooleanProperty(false);

    @FXML
    void controlAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button) {
            try {
                Button b = (Button)event.getSource();
                switch(b.getId()) {
                    case "ambientLight":
                        toggleAmbientLight();
                        break;
                    case "e1Heat":
                        if (printer.getCurrentStatusProperty().get().isDualMaterialHead())
                            toggleSwitch(heaterTState, "M104 T", "M104 T0");
                        else
                            toggleSwitch(heaterSState, "M104 S", "M104 S0");
                        break;
                    case "e1Eject":
                        controlEject(1);
                        break;
                    case "e1m20":
                        controlJog("E", -20);
                        break;
                    case "e1m50":
                        controlJog("E", -50);
                        break;
                    case "e1p20":
                        controlJog("E", 20);
                        break;
                    case "e1p50":
                        controlJog("E", 50);
                        break;
                    case "e2Eject":
                        controlEject(2);
                        break;
                    case "e2Heat":
  //                      'heaterS':{'state':false, 'onCode':'M104 S', 'offCode':'M104 S0'},
  //                      'heaterT':{'state':false, 'onCode':'M104 T', 'offCode':'M104 T0'},
                        break;
                    case "e2m20":
                        controlJog("D", -20);
                        break;
                    case "e2m50":
                        controlJog("D", -50);
                        break;
                    case "e2p20":
                        controlJog("D", 20);
                        break;
                    case "e2p50":
                        controlJog("D", 50);
                        break;
                    case "fan":
                        toggleSwitch(fanState, "M106", "M107");
                        break;
                    case "home":
                        home();
                        break;
                    case "headlight":
                        toggleSwitch(headlightState, "M129", "M128");
                        break;
                    case "heatBed":
                        toggleBedHeat();
                        break;
                    case "select":
                        toggleSwitch(nozzleSelectState, "T0", "T1");
                        break;
                    case "unlock":
                        printer.runSendGCodeTask("G37 S");
                        break;
                    case "valve":
                        toggleSwitch(valveState, "G0 B1", "G0 B0");
                        break;
                    case "xHome":
                        controlMove("X", 0);
                        break;
                    case "xm10":
                        controlMove("X", -10);
                        break;
                    case "xp10":
                        controlMove("X", 10);
                        break;
                    case "yHome":
                        controlMove("Y", 0);
                        break;
                    case "ym10":
                        controlMove("Y", -10);
                        break;
                    case "yp10":
                        controlMove("Y", 10);
                        break;
                    case "zHome":
                        controlMove("Z", 0);
                        break;
                    case "zm10":
                        controlMove("Z", -10);
                        break;
                    case "zp10":
                        controlMove("Z", 10);
                        break;
                    default:
                        break;
                }
            }
            catch (Exception ex) {
            }
        }
    }
    
    @FXML
    void leftButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button)
            rootController.showMainMenu(printer);
    }
    
    @FXML
    void middleButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button)
            rootController.showHomePage(printer);
    }

    @FXML
    void rightButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button)
            rootController.showConsolePage(printer, true);
    }

    private RootStackController rootController = null;
    private RootPrinter printer = null;
    private static final Map<String, Image> headImageMap = new HashMap<>();
    
    private final ChangeListener<PrinterStatusResponse> printerStatusListener = (ob, ov, nv) -> {
        //System.out.println("RemotePrinter \"" + printer.getPrinterId() + "\"printerStatusListener");
        updatePrinterStatus(nv);
    };
    
    @Override
    public void setRootStackController(RootStackController rootController) {
        this.rootController = rootController;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        translateLabels(ejectLabel,
                        extrudeLabel,
                        heatLabel,
                        retractLabel,
                        selectButton,
                        valveButton,
                        headlightButton,
                        fanButton);

        filamentProperties = new SimpleBooleanProperty[][] {{canEject1Property, canExtrude1Property, canRetract1Property}, {canEject2Property, canExtrude2Property, canRetract2Property}};
        e1m50Button.disableProperty().bind(canRetract1Property.not());
        e1m20Button.disableProperty().bind(canRetract1Property.not());
        e1EjectButton.disableProperty().bind(canEject1Property.not());
        e1p20Button.disableProperty().bind(canExtrude1Property.not());
        e1p50Button.disableProperty().bind(canExtrude1Property.not());
        e1HeatButton.disableProperty().bind(canEject1Property.not().and(headAttachedProperty.not()));

        e2m50Button.disableProperty().bind(canRetract2Property.not());
        e2m20Button.disableProperty().bind(canRetract2Property.not());
        e2EjectButton.disableProperty().bind(canEject2Property.not());
        e2p20Button.disableProperty().bind(canExtrude2Property.not());
        e2p50Button.disableProperty().bind(canExtrude2Property.not());
        e2HeatButton.disableProperty().bind(canEject2Property.not().and(headAttachedProperty.not()).and(hasMultipleNozzlesProperty.not()));

        selectButton.disableProperty().bind(headAttachedProperty.not().and(hasMultipleNozzlesProperty.not()));
        valveButton.disableProperty().bind(headAttachedProperty.not().and(hasValvesProperty.not()));
        headlightButton.disableProperty().bind(headAttachedProperty.not());
        fanButton.disableProperty().bind(headAttachedProperty.not());
        
        headImageMap.put(NO_HEAD_KEY, new Image(getClass().getResourceAsStream("/image/control-head-none.png")));
        headImageMap.put(DEFAULT_HEAD_KEY, new Image(getClass().getResourceAsStream("/image/control-head-default.png")));

        controlPane.setVisible(false);
    }
    
    @Override
    public void startUpdates() {
        printer.getCurrentStatusProperty().addListener(printerStatusListener);
        updatePrinterStatus(printer.getCurrentStatusProperty().get());
    }
    
    @Override
    public void stopUpdates() {
        // Printer can be null if
        // the home page has never been shown.
        if (printer != null) {
            printer.getCurrentStatusProperty().removeListener(printerStatusListener);
            printer = null;
        }
    }
    
    @Override
    public void displayPage(RootPrinter printer) {
        this.printer = printer;
        if (!controlPane.isVisible()) {
            startUpdates();
            controlPane.setVisible(true);
        }
    }
    
    @Override
    public void hidePage() {
        stopUpdates();
        controlPane.setVisible(false);
    }

    @Override
    public boolean isVisible() {
        return controlPane.isVisible();
    }

    private void updatePrinterStatus(PrinterStatusResponse printerStatus) {
        Platform.runLater(() -> {
            updateHeadStatus(printerStatus);
            updateMaterialStatus(printerStatus);
        });
    }
    
    private void updateHeadStatus(PrinterStatusResponse printerStatus) {
        if (printerStatus.getHeadName().isEmpty()) {
            // No head attached
            headAttachedProperty.set(false);
            isDualMaterialHeadProperty.set(false);
            hasMultipleNozzlesProperty.set(false);
            hasValvesProperty.set(false);
            
            // Update headImageView
            headImageView.setImage(headImageMap.get(NO_HEAD_KEY));
        }
        else {
            // Head attached.
            headAttachedProperty.set(true);
            // Update headImageView
            String headCode = printerStatus.getHeadTypeCode().toLowerCase();
            Image headImage = headImageMap.get(headCode);
            if (headImage == null) {
                try {
                    InputStream imageStream = getClass().getResourceAsStream("/image/control-head-" + headCode + ".png");
                    if (imageStream != null) {
                        headImage = new Image(imageStream);
                        headImageMap.put(headCode, headImage);
                   }
                }
                catch (Exception ex)
                {
                }
            }
            if (headImage == null)
                headImage = headImageMap.get(DEFAULT_HEAD_KEY);
            headImageView.setImage(headImage);
            isDualMaterialHeadProperty.set(printerStatus.isDualMaterialHead());
            // Length of the nozzle temperature array is the number of nozzles.
            hasMultipleNozzlesProperty.set(printerStatus.getNozzleTemperature().length > 1);
            hasValvesProperty.set(printerStatus.areValvesFitted());
        }
    }
    
    private void updateMaterialStatus(PrinterStatusResponse printerStatus) {
        updateFilamentStatus(printerStatus, 0);
        updateFilamentStatus(printerStatus, 1);
    }

    private void updateFilamentStatus(PrinterStatusResponse printerStatus, int mIndex) {
        if (printerStatus.getAttachedFilaments().length > mIndex) {
            FilamentDetails details =  printerStatus.getAttachedFilaments()[mIndex];
            SimpleBooleanProperty[] filamentProps = filamentProperties[mIndex];
            filamentProps[0].set(details.getCanEject());
            filamentProps[1].set(details.getCanExtrude());
            filamentProps[2].set(details.getCanRetract());
        }
    }
    
    private void controlJog(String extruder, int step) {
        if (step > 0) {
            String gCode =  "G91:G1 " + extruder + Integer.toString(step) + " F400:G90";
            printer.runSendGCodeTask(gCode);
        }
    }

    private void controlEject(int filamentNumber) {
        printer.runEjectFilamentTask(filamentNumber);
    }

    private void toggleSwitch(SimpleBooleanProperty state, String onCode, String offCode) {
        if (state.get()) {
            printer.runSendGCodeTask(offCode);
            state.set(false);
        }
        else {
            printer.runSendGCodeTask(onCode);
            state.set(true);
         }
    }
    
    private void controlMove(String axis, int step) {
        String gCode = "";
        if (step == 0)
            gCode = "G90:G28 " + axis;
        else
            gCode = "G91:G0 " + axis + Integer.toString(step) + ":G90";
        printer.runSendGCodeTask(gCode);
    }

    private void home() {
        printer.runMacroTask("HOME_ALL");
    }

    private void toggleAmbientLight() {
        switch(ambientLightState)
        {
            case "on":
                ambientLightState= "white";
                break;
            case "white":
                ambientLightState = "off";
                break;
            case "off":
            default:
                ambientLightState = "on";
                break;
        }
       printer.runSwitchAmbientLightTask(ambientLightState);
    }

    private void toggleBedHeat() {
        
        String gcode = "";
        if (bedHeatState.get()) {
                bedHeatState.set(false);
                gcode = "M140 S0";
        }
        else {
                bedHeatState.set(true);
                gcode = "M140";
                if (canEject1Property.get())
                    gcode = gcode + " E";
                if (canEject2Property.get())
                    gcode = gcode + " D";
                else
                    gcode = gcode + " S80";
        }
        printer.runSendGCodeTask(gcode);
    }
}
