package celuk.groot.controllers;

import celuk.groot.remote.FilamentDetails;
import celuk.groot.remote.HeadEEPROMData;
import celuk.groot.remote.PurgeData;
import celuk.groot.remote.PurgeTarget;
import celuk.groot.remote.RootPrinter;
import celuk.language.I18n;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class PurgeController implements Initializable, Page {
    
    static final String NO_HEAD_KEY = "<none>";
    static final String DEFAULT_HEAD_KEY = "<default>";

    @FXML
    private StackPane purgePane;

    @FXML
    private GridPane m1Pane;
    @FXML
    private Label purgeTitle;
    @FXML
    private Label purgePrompt;
    @FXML
    private Label m1Title;
    @FXML
    private Label m1Description;
    @FXML
    private Label m1Material;
    @FXML
    private CheckBox m1CheckBox;
    @FXML
    private Label m1PrevTempLabel;
    @FXML
    private Label m1PrevTempValue;
    @FXML
    private Label m1PrevTempSuffix;

    @FXML
    private Label m1NewTempLabel;
    @FXML
    private Label m1NewTempValue;
    @FXML
    private Label m1NewTempSuffix;
    @FXML
    private Label m1PurgeTempLabel;
    @FXML
    private Button m1PurgeTempInc;
    @FXML
    private TextField m1PurgeTempValue;
    @FXML
    private Button m1PurgeTempDec;
    @FXML
    private Label m1PurgeTempSuffix;

    @FXML
    private GridPane m2Pane;
    @FXML
    private Label m2Title;
    @FXML
    private Label m2Description;
    @FXML
    private Label m2Material;
    @FXML
    private CheckBox m2CheckBox;
    @FXML
    private Label m2PrevTempLabel;
    @FXML
    private Label m2PrevTempValue;
    @FXML
    private Label m2PrevTempSuffix;
    @FXML
    private Label m2NewTempLabel;
    @FXML
    private Label m2NewTempValue;
    @FXML
    private Label m2NewTempSuffix;
    @FXML
    private Label m2PurgeTempLabel;
    @FXML
    private Button m2PurgeTempInc;
    @FXML
    private TextField m2PurgeTempValue;
    @FXML
    private Button m2PurgeTempDec;
    @FXML
    private Label m2PurgeTempSuffix;

    @FXML
    private Button leftButton;

    @FXML
    private Button middleButton;

    @FXML
    private Button rightButton;

    @FXML
    void checkAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof CheckBox) {
            CheckBox box = (CheckBox)event.getSource();
            if (box.getId().equalsIgnoreCase("m1CheckBox")) {
                disableNodes(panel1Nodes, !box.isSelected());
            }
            else if (box.getId().equalsIgnoreCase("m2CheckBox")) {
                disableNodes(panel2Nodes, !box.isSelected());
            }
            rightButton.setDisable(!(m1CheckBox.isSelected() || m2CheckBox.isSelected()));
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
        if (rootController != null && event.getSource() instanceof Button) {
            initiatePurge();
        }
    }

    private RootStackController rootController = null;
    private RootPrinter printer = null;
    private Map<String, IntegerSpinnerController> spinnerMap = new HashMap<>();
    private boolean showPanel1 = false;
    private boolean showPanel2 = false;
    private int rightNozzleIndex = 0;    
    private String purgePromptText = "purge.prompt";
    private String purgeUnavailableText = "purge.unavailable";
    private Node[] panel1Nodes = null;
    private Node[] panel2Nodes = null;
    private int m1PreviousTemp = -1;
    private int m1NewTemp = -1;
    private int m2PreviousTemp = -1;
    private int m2NewTemp = -1;
    
    private final ChangeListener<PurgeData> purgeDataListener = (ob, ov, nv) -> {
        //System.out.println("RemotePrinter \"" + printer.getPrinterId() + "\"purgeDataListener");
        updatePurgeData(nv);
    };

    @Override
    public void setRootStackController(RootStackController rootController) {
        this.rootController = rootController;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        translateLabels(m1Title,
                        purgeTitle,
                        m1Description,
                        m1PrevTempLabel,
                        m1PrevTempSuffix,
                        m1NewTempLabel,
                        m1NewTempSuffix,
                        m1PurgeTempLabel,
                        m1PurgeTempSuffix,
                        m2Title,
                        m2Description,
                        m2PrevTempLabel,
                        m2PrevTempSuffix,
                        m2NewTempLabel,
                        m2NewTempSuffix,
                        m2PurgeTempLabel,
                        m2PurgeTempSuffix);
        purgePromptText = I18n.t(purgePromptText);
        purgeUnavailableText = I18n.t(purgeUnavailableText);

        BiConsumer<IntegerSpinnerController, Integer> updater = (sc, v) -> { sc.setValue(v); };
        spinnerMap.put("m1PurgeTemp",
            new IntegerSpinnerController("m1PurgeTemp", m1PurgeTempValue,
                            m1PurgeTempDec, m1PurgeTempInc, 1, updater));
        spinnerMap.put("m2PurgeTemp",
            new IntegerSpinnerController("m2PurgeTemp", m2PurgeTempValue,
                            m2PurgeTempDec, m2PurgeTempInc, 1, updater));
        
        panel1Nodes = new Node[] {
            m1PurgeTempDec,
            m1PurgeTempInc,
//            m1CheckBox,
//            m1Description,
            m1Material,
            m1NewTempLabel,
            m1NewTempSuffix,
            m1NewTempValue,
            m1PrevTempLabel,
            m1PrevTempSuffix,
            m1PrevTempValue,
            m1PurgeTempLabel,
            m1PurgeTempSuffix,
//            m1Title,
            m1PurgeTempValue
        };
        panel2Nodes = new Node[] {
            m2PurgeTempDec,
            m2PurgeTempInc,
//            m2CheckBox,
//            m2Description,
            m2Material,
            m2NewTempLabel,
            m2NewTempSuffix,
            m2NewTempValue,
            m2PrevTempLabel,
            m2PrevTempSuffix,
            m2PrevTempValue,
            m2PurgeTempLabel,
            m2PurgeTempSuffix,
//            m2Title,
            m2PurgeTempValue
        };
        
        showPanel1 = false;
        showPanel1 = false;
        m1Pane.setVisible(false);
        m1Pane.setManaged(false);
        m2Pane.setVisible(false);
        m2Pane.setManaged(false);
        rightButton.setDisable(true);
        purgePane.setVisible(false);
    }
    
    @Override
    public void startUpdates() {
        printer.getCurrentPurgeDataProperty().addListener(purgeDataListener);
        printer.runRequestPurgeDataTask();
//        updatePurgeData(printer.getCurrentPurgeDataProperty().get());
    }
    
    @Override
    public void stopUpdates() {
        // Printer can be null if
        // the home page has never been shown.
        if (printer != null) {
            printer.getCurrentPurgeDataProperty().removeListener(purgeDataListener);
            printer = null;
        }
    }
    
    @Override
    public void displayPage(RootPrinter printer) {
        this.printer = printer;
        if (!purgePane.isVisible()) {
            startUpdates();
            purgePane.setVisible(true);
        }
    }
    
    @Override
    public void hidePage() {
        stopUpdates();
        purgePane.setVisible(false);
    }
    
    @Override
    public boolean isVisible() {
        return purgePane.isVisible();
    }
    
    private void setSpinnerData(String fieldName, double value, double minValue, double maxValue) {
        IntegerSpinnerController spinner = spinnerMap.get(fieldName);
        if (spinner != null)
            spinner.updateSpinnerData((int)Math.round(value), (int)Math.round(minValue), (int)Math.round(maxValue));
    }
    
    private void disableNodes(Node[] nodes, boolean disable) {
        for(Node n : nodes) {
            n.setDisable(disable);
        }
    }

    private void updatePurgeData(PurgeData pData) {
        Platform.runLater(() -> {
            showPanel1 = false;
            showPanel2 = false;
            rightNozzleIndex = 0;
            HeadEEPROMData headData = null;
            FilamentDetails[] attachedFilaments = null;
            if (pData != null) {
                headData = pData.getHeadData();
                attachedFilaments = pData.getMaterialStatus().getAttachedFilaments();

                showPanel1 = attachedFilaments.length > 0 &&
                             attachedFilaments[0] != null &&
                             attachedFilaments[0].getMaterialLoaded() &&
                             attachedFilaments[0].getCanEject();
                showPanel2 = headData.getDualMaterialHead() &&
                             headData.getNozzleCount() > 1 &&
                             attachedFilaments.length > 1 &&
                             attachedFilaments[1] != null &&
                             attachedFilaments[1].getMaterialLoaded() &&
                             attachedFilaments[1].getCanEject();
                rightNozzleIndex = headData.getNozzleCount() - 1;
            }
            
            if (showPanel1 || showPanel2) {
                purgePrompt.setText(purgePromptText);
                rightButton.setDisable(false);
            }
            else {
                purgePrompt.setText(purgeUnavailableText);
                rightButton.setDisable(true);
            }
                
            if (showPanel1) {
                m1Pane.setVisible(true);
                m1Pane.setManaged(true);
                m1CheckBox.selectedProperty().set(true);
                disableNodes(panel1Nodes, false);
                m1PreviousTemp = (int)Math.round(headData.getRightNozzleLastFTemp());
                if (m1PreviousTemp > -1)
                    m1PrevTempValue.setText(Integer.toString(m1PreviousTemp));
                else
                    m1PrevTempValue.setText("-");
                m1Material.setText(attachedFilaments[0].getFilamentName());
                m1NewTemp = (int)Math.round(attachedFilaments[0].getFilamentTemperature());
                m1NewTempValue.setText(Integer.toString(m1NewTemp));
                double t = attachedFilaments[0].getFilamentTemperature();
                if (headData.getRightNozzleLastFTemp() > 0)
                    t = 0.5F * (headData.getRightNozzleLastFTemp() + t);
                setSpinnerData("m1PurgeTemp", t, Math.max(0.5 * t, 50.0), Math.min(2.0 * t, 300.0));
            }
            else {
                m1Pane.setVisible(false);
                m1Pane.setManaged(false);
                m1Material.setText("");
                m1CheckBox.selectedProperty().set(false);
                m1PrevTempValue.setText("");
                m1PreviousTemp = -1;
                m1NewTempValue.setText("");
                m1NewTemp = -1;
            }                       

            if (showPanel2) {
                m2Pane.setVisible(true);
                m2Pane.setManaged(true);
                m2Pane.setDisable(false);
                disableNodes(panel2Nodes, false);
                m2CheckBox.selectedProperty().set(true);
                m2PreviousTemp = (int)Math.round(headData.getLeftNozzleLastFTemp());
                if (m2PreviousTemp > -1)
                    m2PrevTempValue.setText(Integer.toString(m2PreviousTemp));
                else
                    m2PrevTempValue.setText("-");
                m2Material.setText(attachedFilaments[1].getFilamentName());
                m2NewTemp = (int)Math.round(attachedFilaments[1].getFilamentTemperature());
                m2NewTempValue.setText(Integer.toString(m2NewTemp));
                double t = attachedFilaments[1].getFilamentTemperature();
                if (headData.getLeftNozzleLastFTemp() > 0)
                    t = 0.5F * (headData.getLeftNozzleLastFTemp() + t);
                setSpinnerData("m2PurgeTemp", t, Math.max(0.5 * t, 50.0), Math.min(2.0 * t, 300.0));
            }
            else {
                m2Pane.setVisible(false);
                m2Pane.setManaged(false);
                m2Material.setText("");
                m2CheckBox.selectedProperty().set(false);
                m2PrevTempValue.setText("");
                m2PreviousTemp = -1;
                m2NewTempValue.setText("");
                m2NewTemp = -1;
            }
        });
    }
    
    private void initiatePurge() {
        PurgeTarget targetData = new PurgeTarget();
        targetData.setSafetyOn(printer.getSafetiesOnProperty().get());

        if (showPanel1 && m1CheckBox.isSelected()) {
            targetData.getLastTemperature()[rightNozzleIndex] = m1PreviousTemp;
            targetData.getNewTemperature()[rightNozzleIndex] = m1NewTemp;
            targetData.getTargetTemperature()[rightNozzleIndex] = spinnerMap.get("m1PurgeTemp").value;
        }
        
        if (showPanel2 && m2CheckBox.isSelected()) {
            targetData.getLastTemperature()[0] = m2PreviousTemp;
            targetData.getNewTemperature()[0] = m2NewTemp;
            targetData.getTargetTemperature()[0] = spinnerMap.get("m2PurgeTemp").value;
        }
        
        printer.runPurgeTask(targetData);
        rootController.showHomePage(printer);
    }
}
