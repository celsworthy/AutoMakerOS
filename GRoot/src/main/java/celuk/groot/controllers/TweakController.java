package celuk.groot.controllers;

import celuk.groot.remote.PrintAdjustData;
import celuk.groot.remote.RootPrinter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class TweakController implements Initializable, Page {
    private class SpinnerUpdater implements BiConsumer<IntegerSpinnerController, Integer> {
        private final String tagName;
        private final String fieldName;
        
        public SpinnerUpdater(String tagName, String fieldName) {
            this.tagName = tagName;
            this.fieldName = fieldName;
        }
        
        @Override
        public void accept(IntegerSpinnerController spinner, Integer value) {
            String data = String.format("{\"name\":\"%s\",\"tag\":\"%s\",\"value\":%s}",
                                        fieldName,
                                        tagName, Integer.toString(value));
            try {
                printer.runSetPrintAdjustDataTask(data).get();
            } catch (InterruptedException ex) {
                System.err.println("Interrupt exception when running print adjust request.");
            } catch (ExecutionException ex) {
                System.err.println("Execution exception when running print adjust request.");
            }
        }
    };
    
    static final String NO_HEAD_KEY = "<none>";
    static final String DEFAULT_HEAD_KEY = "<default>";

    @FXML
    private StackPane tweakPane;

    @FXML
    private GridPane m1Pane;
    @FXML
    private Label m1Title;
    @FXML
    private Label m1Description;
    @FXML
    private Label m1Material;
    @FXML
    private Label m1PrintSpeedLabel;
    @FXML
    private Button m1PrintSpeedInc;
    @FXML
    private TextField m1PrintSpeedValue;
    @FXML
    private Button m1PrintSpeedDec;
    @FXML
    private Label m1PrintSpeedSuffix;
    @FXML
    private Label m1FlowRateLabel;
    @FXML
    private Button m1FlowRateInc;
    @FXML
    private TextField m1FlowRateValue;
    @FXML
    private Button m1FlowRateDec;
    @FXML
    private Label m1FlowRateSuffix;
    @FXML
    private Label m1TempLabel;
    @FXML
    private Button m1TempInc;
    @FXML
    private TextField m1TempValue;
    @FXML
    private Button m1TempDec;
    @FXML
    private Label m1TempSuffix;

    @FXML
    private GridPane m2Pane;
    @FXML
    private Label m2Title;
    @FXML
    private Label m2Description;
    @FXML
    private Label m2Material;
    @FXML
    private Label m2PrintSpeedLabel;
    @FXML
    private Button m2PrintSpeedInc;
    @FXML
    private TextField m2PrintSpeedValue;
    @FXML
    private Button m2PrintSpeedDec;
    @FXML
    private Label m2PrintSpeedSuffix;
    @FXML
    private Label m2FlowRateLabel;
    @FXML
    private Button m2FlowRateInc;
    @FXML
    private TextField m2FlowRateValue;
    @FXML
    private Button m2FlowRateDec;
    @FXML
    private Label m2FlowRateSuffix;
    @FXML
    private Label m2TempLabel;
    @FXML
    private Button m2TempInc;
    @FXML
    private TextField m2TempValue;
    @FXML
    private Button m2TempDec;
    @FXML
    private Label m2TempSuffix;

    @FXML
    private GridPane bedPane;
    @FXML
    private Label bedTitle;
    @FXML
    private Label bedTempLabel;
    @FXML
    private Button bedTempInc;
    @FXML
    private TextField bedTempValue;
    @FXML
    private Button bedTempDec;
    @FXML
    private Label bedTempSuffix;
    
    @FXML
    private Button leftButton;

    @FXML
    private Button middleButton;

    @FXML
    private Button rightButton;

    @FXML
    void leftButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button)
            rootController.showHomePage(printer);
    }
    
    @FXML
    void middleButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button)
            rootController.showMainMenu(printer);
    }

    @FXML
    void rightButtonAction(ActionEvent event) {
    }

    private RootStackController rootController = null;
    private RootPrinter printer = null;
    private Map<String, IntegerSpinnerController> spinnerMap = new HashMap<>();
    
    private final ChangeListener<PrintAdjustData> printAdjustDataListener = (ob, ov, nv) -> {
        //System.out.println("RemotePrinter \"" + printer.getPrinterId() + "\"printAdjustDataListener");
        updatePrintAdjustData(nv);
    };

    @Override
    public void setRootStackController(RootStackController rootController) {
        this.rootController = rootController;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        translateLabels(m1Title,
                        m1Description,
                        m1PrintSpeedLabel,
                        m1PrintSpeedSuffix,
                        m1FlowRateLabel,
                        m1FlowRateSuffix,
                        m1TempLabel,
                        m1TempSuffix,
                        m2Title,
                        m2Description,
                        m2PrintSpeedLabel,
                        m2PrintSpeedSuffix,
                        m2FlowRateLabel,
                        m2FlowRateSuffix,
                        m2TempLabel,
                        m2TempSuffix,
                        bedTitle,
                        bedTempLabel,
                        bedTempSuffix);
       
        spinnerMap.put("m1PrintSpeed",
            new IntegerSpinnerController("m1PrintSpeed", m1PrintSpeedValue,
                m1PrintSpeedDec, m1PrintSpeedInc, 10,
                new SpinnerUpdater("r", "feedRate")));
        spinnerMap.put("m1FlowRate",
            new IntegerSpinnerController("m1FlowRate", m1FlowRateValue,
                m1FlowRateDec, m1FlowRateInc, 2,
                new SpinnerUpdater("r", "extrusionRate")));
        spinnerMap.put("m1Temp",
            new IntegerSpinnerController("m1Temp", m1TempValue,
                m1TempDec, m1TempInc, 2, 
                new SpinnerUpdater("r", "temp")));
        spinnerMap.put("m2PrintSpeed",
            new IntegerSpinnerController("m2PrintSpeed", m2PrintSpeedValue,
                m2PrintSpeedDec, m2PrintSpeedInc, 10,
                new SpinnerUpdater("l", "feedRate")));
        spinnerMap.put("m2FlowRate",
            new IntegerSpinnerController("m2FlowRate", m2FlowRateValue,
                m2FlowRateDec, m2FlowRateInc, 2,
                new SpinnerUpdater("l", "extrusionRate")));
        spinnerMap.put("m2Temp",
            new IntegerSpinnerController("m2Temp", m2TempValue,
                            m2TempDec, m2TempInc, 2, new SpinnerUpdater("l", "temp")));
        spinnerMap.put("bedTemp",
            new IntegerSpinnerController("bedTemp", bedTempValue,
                bedTempDec, bedTempInc, 5,
                new SpinnerUpdater("bed", "temp")));

        m1Pane.setVisible(false);
        m1Pane.setManaged(false);
        m2Pane.setVisible(false);
        m2Pane.setManaged(false);

        tweakPane.setVisible(false);
        rightButton.setVisible(false);
    }
    
    @Override
    public void startUpdates() {
        printer.getCurrentPrintAdjustDataProperty().addListener(printAdjustDataListener);
        updatePrintAdjustData(printer.getCurrentPrintAdjustDataProperty().get());
    }
    
    @Override
    public void stopUpdates() {
        // Printer can be null if
        // the home page has never been shown.
        if (printer != null) {
            printer.getCurrentPrintAdjustDataProperty().removeListener(printAdjustDataListener);
            printer = null;
        }
    }
    
    @Override
    public void displayPage(RootPrinter printer) {
        this.printer = printer;
        if (!tweakPane.isVisible()) {
            startUpdates();
            tweakPane.setVisible(true);
        }
    }
    
    @Override
    public void hidePage() {
        stopUpdates();
        tweakPane.setVisible(false);
    }

    @Override
    public boolean isVisible() {
        return tweakPane.isVisible();
    }

    private void setSpinnerData(String fieldName, double value, double delta) {
        IntegerSpinnerController spinner = spinnerMap.get(fieldName);
        if (spinner != null)
            spinner.updateSpinnerData((int)Math.round(value), (int)Math.round(value - delta), (int)Math.round(value + delta));
    }

    private void updatePrintAdjustData(PrintAdjustData adjustData) {
        Platform.runLater(() -> {
            if (adjustData == null) {
                // It is possible for this thread to complete after the 
                // thread has been stopped, in which case the printer will be null.
                // A local variable is used to make sure the printer does not
                // disapper between testing it and using it.
                RootPrinter p = printer;
                if (p != null)
                    rootController.showHomePage(p);
            }
            else {
                setSpinnerData("bedTemp", adjustData.getBedTargetTemp(), 15.0);
                
                if (adjustData.getUsingMaterial1()) {
                    m1Pane.setVisible(true);
                    m1Pane.setManaged(true);

                    m1Material.setText(adjustData.getMaterial1Name());
                    setSpinnerData("m1PrintSpeed", adjustData.getRightFeedRateMultiplier(), 100.0);
                    setSpinnerData("m1FlowRate", adjustData.getRightExtrusionRateMultiplier(), 100.0);
                    setSpinnerData("m1Temp", adjustData.getRightNozzleTargetTemp(), 15.0);
                }
                else {
                    m1Pane.setVisible(false);
                    m1Pane.setManaged(false);
                }

                if (adjustData.getUsingMaterial2()) {
                    m2Pane.setVisible(true);
                    m2Pane.setManaged(true);

                    m2Material.setText(adjustData.getMaterial2Name());
                    setSpinnerData("m2PrintSpeed", adjustData.getLeftFeedRateMultiplier(), 100.0);
                    setSpinnerData("m2FlowRate", adjustData.getLeftExtrusionRateMultiplier(), 100.0);
                    setSpinnerData("m2Temp", adjustData.getLeftNozzleTargetTemp(), 15.0);
                }
                else {
                    m2Pane.setVisible(false);
                    m2Pane.setManaged(false);
                }
            }
        });
    }
}
