package celuk.groot.controllers;

import celuk.groot.remote.HeadEEPROMData;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class HeadParametersController implements Initializable, Page {
    @FXML
    protected Pane headIcon;
    @FXML
    protected Label headTitleBold;
    @FXML
    protected Label headTitleLight;
    @FXML
    protected Label headTitleEdition;
    @FXML
    protected Label headDescription;
    @FXML
    protected Label headNozzlesDescription;
    @FXML
    protected Label headFeedsDescription;
    @FXML
    protected Button headChangeButton;

    @FXML
    protected StackPane headParametersPane;
    @FXML
    protected Label headParametersTitle;
    @FXML
    protected Label serialNumberLabel;
    @FXML
    protected Label serialNumberField;
    @FXML
    protected Label headHoursField;
    @FXML
    protected Label maxTempField;
    
    @FXML
    protected Label printHoursLabel;
    @FXML
    protected Label maxTempLabel;
    @FXML
    protected Label leftNozzleSubtitle;
    @FXML
    protected Label leftNozzleTitle;
    @FXML
    protected Label rightNozzleSubtitle;
    @FXML
    protected Label rightNozzleTitle;

    @FXML
    protected Button leftXDec;
    @FXML
    protected TextField leftXField;
    @FXML
    protected Button leftXInc;
    @FXML
    protected Button leftYDec;
    @FXML
    protected TextField leftYField;
    @FXML
    protected Button leftYInc;
    @FXML
    protected Button leftZDec;
    @FXML
    protected TextField leftZField;
    @FXML
    protected Button leftZInc;
    @FXML
    protected Button leftBDec;
    @FXML
    protected TextField leftBField;
    @FXML
    protected Button leftBInc;

    @FXML
    protected Button rightXDec;
    @FXML
    protected TextField rightXField;
    @FXML
    protected Button rightXInc;
    @FXML
    protected Button rightYDec;
    @FXML
    protected TextField rightYField;
    @FXML
    protected Button rightYInc;
    @FXML
    protected Button rightZDec;
    @FXML
    protected TextField rightZField;
    @FXML
    protected Button rightZInc;
    @FXML
    protected Button rightBDec;
    @FXML
    protected TextField rightBField;
    @FXML
    protected Button rightBInc;
    @FXML
    protected Button rightButton;

    @FXML
    protected void decAction(ActionEvent event) {
        FloatSpinnerController s = (FloatSpinnerController)(((Node)event.getSource()).getUserData());
        s.decAction(event);
    }

    @FXML
    protected void headChangeAction(ActionEvent event) {
        printer.runRemoveHeadTask();
        rootController.showRemoveHeadPage(printer);
    }

    @FXML
    protected void incAction(ActionEvent event) {
        FloatSpinnerController s = (FloatSpinnerController)(((Node)event.getSource()).getUserData());
        s.incAction(event);
    }

    @FXML
    protected void leftButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button)
            rootController.showMainMenu(printer);
    }
    
    @FXML
    protected void middleButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button)
            rootController.showHomePage(printer);
    }

    @FXML
    protected void rightButtonAction(ActionEvent event) {
        if (rootController != null && event.getSource() instanceof Button) {
            saveHeadData();
        }
    }

    private static final float MAX_SPINNER_VALUE = 20.0F;
    private static final float MIN_SPINNER_VALUE = -20.0F;
    
    protected RootStackController rootController = null;
    protected RootPrinter printer = null;
    private final Map<String, FloatSpinnerController> spinnerMap = new HashMap<>();
    private int nozzleCount = 0;
    private boolean valveFitted = false;
    protected boolean modified = false;
    
    private final ChangeListener<HeadEEPROMData> headEEPROMDataListener = (ob, ov, nv) -> {
        //System.out.println("RemotePrinter \"" + printer.getPrinterId() + "\"headEEPROMDataListener");
        Platform.runLater(() -> {
            updateHeadEEPROMData(nv);
        });
    };
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        translateLabels(headParametersTitle,
                        serialNumberLabel,
                        printHoursLabel,
                        maxTempLabel,
                        leftNozzleSubtitle,
                        leftNozzleTitle,
                        rightNozzleSubtitle,
                        rightNozzleTitle);

        BiConsumer<FloatSpinnerController, Float> updater = (sc, v) -> { setModified(true); };

        spinnerMap.put("leftX",
            new FloatSpinnerController("leftX", leftXField, leftXDec, leftXInc, 0.05F, "%.2f", updater));
        spinnerMap.get("leftX").initialize();
        spinnerMap.put("leftY",
           new FloatSpinnerController("leftY", leftYField, leftYDec, leftYInc, 0.05F, "%.2f", updater));
        spinnerMap.get("leftY").initialize();
        spinnerMap.put("leftZ",
            new FloatSpinnerController("leftZ", leftZField, leftZDec, leftZInc, 0.05F, "%.2f", updater));
        spinnerMap.get("leftZ").initialize();
        spinnerMap.put("leftB",
            new FloatSpinnerController("leftB", leftBField, leftBDec, leftBInc, 0.05F, "%.2f", updater));
        spinnerMap.get("leftB").initialize();
        spinnerMap.put("rightX",
            new FloatSpinnerController("rightX", rightXField, rightXDec, rightXInc, 0.05F, "%.2f", updater));
        spinnerMap.get("rightX").initialize();
        spinnerMap.put("rightY",
            new FloatSpinnerController("rightY", rightYField, rightYDec, rightYInc, 0.05F, "%.2f", updater));
        spinnerMap.get("rightY").initialize();
        spinnerMap.put("rightZ",
            new FloatSpinnerController("rightZ", rightZField, rightZDec, rightZInc, 0.05F, "%.2f", updater));
        spinnerMap.get("rightZ").initialize();
        spinnerMap.put("rightB",
            new FloatSpinnerController("rightB", rightBField, rightBDec, rightBInc, 0.05F, "%.2f", updater));
        spinnerMap.get("rightB").initialize();

        headParametersPane.setVisible(false);
    }
    
    @Override
    public void setRootStackController(RootStackController rootController) {
        this.rootController = rootController;
    }
    

    @Override
    public void startUpdates() {
        Platform.runLater(() -> {
            preparePage();
        });
        printer.getCurrentHeadEEPROMDataProperty().addListener(headEEPROMDataListener);
        printer.runRequestHeadEEPROMDataTask();
    }
    
    @Override
    public void stopUpdates() {
        if (printer != null) {
            printer.getCurrentHeadEEPROMDataProperty().removeListener(headEEPROMDataListener);
            printer = null;
        }
    }

    @Override
    public void displayPage(RootPrinter printer) {
        this.printer = printer;
        if (!headParametersPane.isVisible()) {
            startUpdates();
            headParametersPane.setVisible(true);
        }
    }

    @Override
    public void hidePage() {
        stopUpdates();
        headParametersPane.setVisible(false);
    }

    @Override
    public boolean isVisible() {
        return headParametersPane.isVisible();
    }
    
    public void preparePage()
    {
        headTitleBold.setText("");
        headTitleLight.setText("");
        headTitleEdition.setText("");
        headDescription.setText("");
        headNozzlesDescription.setText("");
        headFeedsDescription.setText("");
        String headIconText = "-fx-background-image: null";
        headIcon.setStyle(headIconText);
        serialNumberField.setText("");
        headHoursField.setText("");
        maxTempField.setText("");

        setSpinnerData("leftX", 0.0F, MIN_SPINNER_VALUE, MAX_SPINNER_VALUE);
        setSpinnerData("leftY", 0.0F, MIN_SPINNER_VALUE, MAX_SPINNER_VALUE);
        setSpinnerData("leftZ", 0.0F, MIN_SPINNER_VALUE, MAX_SPINNER_VALUE);
        setSpinnerData("leftB", 0.0F, MIN_SPINNER_VALUE, MAX_SPINNER_VALUE);
        setSpinnerData("rightX", 0.0F, MIN_SPINNER_VALUE, MAX_SPINNER_VALUE);
        setSpinnerData("rightY", 0.0F, MIN_SPINNER_VALUE, MAX_SPINNER_VALUE);
        setSpinnerData("rightZ", 0.0F, MIN_SPINNER_VALUE, MAX_SPINNER_VALUE);
        setSpinnerData("rightB", 0.0F, MIN_SPINNER_VALUE, MAX_SPINNER_VALUE);
    }

    public void updateHeadEEPROMData(HeadEEPROMData headData)
    {
        String typeCode = headData.getTypeCode();
        nozzleCount = headData.getNozzleCount();
        valveFitted = headData.getValveFitted();
        modified = false;
        rightButton.setDisable(true);

        String prefix;
        if (typeCode == null || typeCode.isEmpty()) {
            typeCode = "";
            prefix = "noHead";
        }
        else {
            prefix = typeCode.toLowerCase().replace("-", "");
        }
        headTitleBold.setText(I18n.t(prefix + ".titleBold"));
        headTitleLight.setText(I18n.t(prefix + ".titleLight"));
        headTitleEdition.setText(I18n.t(prefix + ".titleEdition"));
        headDescription.setText(I18n.t(prefix + ".description"));
        headNozzlesDescription.setText(I18n.t(prefix + ".nozzles"));
        headFeedsDescription.setText(I18n.t(prefix + ".feeds"));
        String headIconText = "-fx-background-image: url(\"/image/" + I18n.t(prefix + ".icon") + "\")";
        headIcon.setStyle(headIconText);
        
        if (!typeCode.isEmpty()) {
            headChangeButton.setDisable(false);
            String serialNumber = headData.getTypeCode()
                                   + "-"
                                   + headData.getWeek()
                                   + headData.getYear()
                                   + "-"
                                   + headData.getPONumber()
                                   + "-"
                                   + headData.getSerialNumber()
                                   + "-"
                                   + headData.getChecksum();
            serialNumberField.setText(serialNumber);
            headHoursField.setText(String.format("%d %s", (int)headData.getHourCount(), I18n.t("unit.hours")));
            maxTempField.setText(String.format("%d %s",(int)headData.getMaxTemp(), I18n.t("unit.temp")));

            setSpinnerData("rightX", headData.getRightNozzleXOffset(), MIN_SPINNER_VALUE, MAX_SPINNER_VALUE);
            setSpinnerData("rightY", headData.getRightNozzleYOffset(), MIN_SPINNER_VALUE, MAX_SPINNER_VALUE);
            setSpinnerData("rightZ", headData.getRightNozzleZOverrun(), MIN_SPINNER_VALUE, MAX_SPINNER_VALUE);
        }
        else
        {
            headChangeButton.setDisable(true);
            serialNumberField.setText("");
            headHoursField.setText("");
            maxTempField.setText("");

            spinnerMap.get("rightX").setVisible(false);
            spinnerMap.get("rightY").setVisible(false);
            spinnerMap.get("rightZ").setVisible(false);
        }
        
        if (valveFitted) {
            setSpinnerData("rightB", headData.getLeftNozzleBOffset(), MIN_SPINNER_VALUE, MAX_SPINNER_VALUE);
        }
        else {
            spinnerMap.get("rightB").setVisible(false);
        }
        
        if (nozzleCount > 1) {
            setSpinnerData("leftX", headData.getLeftNozzleXOffset(), MIN_SPINNER_VALUE, MAX_SPINNER_VALUE);
            setSpinnerData("leftY", headData.getLeftNozzleYOffset(), MIN_SPINNER_VALUE, MAX_SPINNER_VALUE);
            setSpinnerData("leftZ", headData.getLeftNozzleZOverrun(), MIN_SPINNER_VALUE, MAX_SPINNER_VALUE);
            if (valveFitted) {
                setSpinnerData("leftB", headData.getLeftNozzleBOffset(), MIN_SPINNER_VALUE, MAX_SPINNER_VALUE);
            }
            else {
                spinnerMap.get("leftB").setVisible(false);
            }
        }
        else {
            spinnerMap.get("leftX").setVisible(false);
            spinnerMap.get("leftY").setVisible(false);
            spinnerMap.get("leftZ").setVisible(false);
            spinnerMap.get("leftB").setVisible(false);
        }
    }
    
    private void setModified(boolean modified) {
        this.modified = modified;
        rightButton.setDisable(!modified);
    }
    
    private void setSpinnerData(String fieldName, float value, float minValue, float maxValue) {
        FloatSpinnerController spinner = spinnerMap.get(fieldName);
        if (spinner != null) {
            spinner.setVisible(true);
            spinner.updateSpinnerData(value, minValue, maxValue);
        }
    }

    private void saveHeadData() {
        HeadEEPROMData headData = printer.getCurrentHeadEEPROMDataProperty().get().duplicate();
        headData.setRightNozzleXOffset(spinnerMap.get("rightX").value);
        headData.setRightNozzleYOffset(spinnerMap.get("rightY").value);
        headData.setRightNozzleZOverrun(spinnerMap.get("rightZ").value);
        if (valveFitted)
            headData.setRightNozzleBOffset(spinnerMap.get("rightB").value);
        if (nozzleCount > 1)
        {
            headData.setLeftNozzleXOffset(spinnerMap.get("leftX").value);
            headData.setLeftNozzleYOffset(spinnerMap.get("leftY").value);
            headData.setLeftNozzleZOverrun(spinnerMap.get("leftZ").value);
            if (valveFitted)
                headData.setLeftNozzleBOffset(spinnerMap.get("leftB").value);
        }
        printer.runSetHeadEEPROMDataTask(headData);
        setModified(false);
    }
}
