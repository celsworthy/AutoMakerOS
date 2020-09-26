package celtech.roboxbase.printerControl.model.statetransitions.calibration;

import celtech.roboxbase.BaseLookup;
import java.net.URL;
import java.util.Optional;

/**
 *
 * @author Ian
 */
public enum NozzleOpeningCalibrationState 
{   
    IDLE("calibrationPanel.readyToBeginNozzleOpeningCalibration", "Nozzle Opening Illustrations_Step 1.fxml"),

    HEATING("calibrationPanel.heating", ""),
    
    HEAD_CLEAN_CHECK_BEFORE_LEAK_TEST("calibrationPanel.ensureHeadIsCleanBMessage", "Nozzle Opening Illustrations_Step 5 and 7.fxml"),

    NO_MATERIAL_CHECK_NO_YES_NO_BUTTONS("calibrationPanel.valvesClosedNoMaterial", "Nozzle Opening Illustrations_Step 3.fxml"),

    NO_MATERIAL_CHECK("calibrationPanel.valvesClosedNoMaterial", "Nozzle Opening Illustrations_Step 3.fxml"),
    
    T0_EXTRUDING("calibrationPanel.isMaterialExtrudingNozzle0", "Nozzle Opening Illustrations_Step 4.fxml"),
    
    T1_EXTRUDING("calibrationPanel.isMaterialExtrudingNozzle1", "Nozzle Opening Illustrations_Step 6.fxml"),
    
    HEAD_CLEAN_CHECK_AFTER_EXTRUDE("calibrationPanel.ensureHeadIsCleanBMessage", "Nozzle Opening Illustrations_Step 5 and 7.fxml"),

    PRE_CALIBRATION_PRIMING_FINE("calibrationPanel.primingNozzle", ""),

    CALIBRATE_FINE_NOZZLE("calibrationPanel.calibrationCommencedMessageFine", "Nozzle Opening Illustrations_Step 4A.fxml"),
    
    INCREMENT_FINE_NOZZLE_POSITION_NO_BUTTONS("calibrationPanel.calibrationCommencedMessageFine", "Nozzle Opening Illustrations_Step 4A.fxml"),
    
    INCREMENT_FINE_NOZZLE_POSITION("", ""),

    PRE_CALIBRATION_PRIMING_FILL("calibrationPanel.primingNozzle", ""),

    CALIBRATE_FILL_NOZZLE("calibrationPanel.calibrationCommencedMessageFill", "Nozzle Opening Illustrations_Step 6A.fxml"),
    
    INCREMENT_FILL_NOZZLE_POSITION_NO_BUTTONS("calibrationPanel.calibrationCommencedMessageFill", "Nozzle Opening Illustrations_Step 6A.fxml"),
    
    INCREMENT_FILL_NOZZLE_POSITION("", ""),

    HEAD_CLEAN_CHECK_FILL_NOZZLE("calibrationPanel.ensureHeadIsCleanBMessage", "Nozzle Opening Illustrations_Step 5 and 7.fxml"),

    CONFIRM_NO_MATERIAL_NO_YESNO_BUTTONS("calibrationPanel.valvesClosedNoMaterialPostCalibration", "Nozzle Opening Illustrations_Step 8.fxml"),
    
    CONFIRM_NO_MATERIAL("calibrationPanel.valvesClosedNoMaterialPostCalibration", "Nozzle Opening Illustrations_Step 8.fxml"),

//    CONFIRM_MATERIAL_EXTRUDING("calibrationPanel.valvesOpenMaterialExtruding", ""),

    FINISHED("calibrationPanel.calibrationSucceededMessage", "Nozzle Opening Illustrations_Step 10.fxml"),
    
    CANCELLING("misc.resettingPrinter", ""),
    
    CANCELLED("", ""),
    
    DONE("", ""),

    FAILED("calibrationPanel.nozzleCalibrationFailed", "Nozzle Opening Illustrations_Failure.fxml");

    private String stepTitleResource = null;
    private final String diagramName;

    private NozzleOpeningCalibrationState(String stepTitleResource, String diagramName)
    {
        this.stepTitleResource = stepTitleResource;
        this.diagramName = diagramName;
    }
    
     /**
     * Return if the cancel button should be shown for this state.
     */
    public boolean showCancelButton() {
        return (this != IDLE && this != FAILED && this != FINISHED && this != CANCELLING && this != DONE);
    }    
    
    public Optional<String> getDiagramName() {
        if (diagramName.equals(""))
        {
            return Optional.empty();
        }
        return Optional.of(diagramName);
    }

    public String getStepTitle()
    {
        if (stepTitleResource == null || stepTitleResource.equals(""))
        {
            return "";
        } else
        {
            return BaseLookup.i18n(stepTitleResource);
        }
    }
}
