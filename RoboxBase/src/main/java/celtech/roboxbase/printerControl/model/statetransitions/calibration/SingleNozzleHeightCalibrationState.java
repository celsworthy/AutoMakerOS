package celtech.roboxbase.printerControl.model.statetransitions.calibration;

import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.configuration.BaseConfiguration;
import java.net.URL;
import java.util.Optional;

/**
 *
 * @author Ian
 */
public enum SingleNozzleHeightCalibrationState
{

    IDLE("calibrationPanel.readyToBeginNozzleOffsetCalibration",
         "Single Nozzle Height Illustrations_Step 1.fxml"),
    INITIALISING("calibrationPanel.initialisingOffset", "Single Nozzle Height Illustrations_Step 2.fxml"),
    HEATING("calibrationPanel.heating", ""),
    HEAD_CLEAN_CHECK("calibrationPanel.headCleanCheck", "Single Nozzle Height Illustrations_Step 4.fxml"),
	INSERT_PAPER("calibrationPanel.insertPieceOfPaper", "Single Nozzle Height Illustrations_Step 5.fxml"),
    PROBING("calibrationPanel.moveThePaperInstruction", "Single Nozzle Height Illustrations_Step 6.fxml"),
    INCREMENT_Z("", ""),
    DECREMENT_Z("", ""),
    BRING_BED_FORWARD("", ""),
    REPLACE_PEI_BED("calibrationPanel.replacePEIBed", "Single Nozzle Height Illustrations_Step 7.fxml"),
    FINISHED("calibrationPanel.calibrationSucceededMessage",
             "Single Nozzle Height Illustrations_Step 8.fxml"),
    FAILED("calibrationPanel.nozzleCalibrationFailed", "Single Nozzle Height Illustrations_Failure.fxml"),
    CANCELLING("misc.resettingPrinter", ""),
    CANCELLED("", ""),
    DONE("", "");

    private final String stepTitleResource;
    private final String diagramName;

    private SingleNozzleHeightCalibrationState(String stepTitleResource, String diagramName)
    {
        this.stepTitleResource = stepTitleResource;
        this.diagramName = diagramName;
    }
    
     /**
     * Return if the cancel button should be show for this state.
     */
    public boolean showCancelButton() {
        return (this != IDLE && this != FAILED && this != FINISHED && this != CANCELLED && this != DONE);
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
