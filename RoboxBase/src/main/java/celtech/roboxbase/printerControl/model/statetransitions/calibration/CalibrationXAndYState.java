package celtech.roboxbase.printerControl.model.statetransitions.calibration;

import celtech.roboxbase.BaseLookup;
import java.net.URL;
import java.util.Optional;

/**
 *
 * @author Ian
 */
public enum CalibrationXAndYState
{

    IDLE("calibrationPanel.xAndYIntroduction", "Nozzle Alignment Illustrations_Step 1.fxml"),
    //    HEATING("calibrationPanel.heating"),

    PRINT_PATTERN("calibrationPanel.xAndYPrintPattern", ""),
    GET_Y_OFFSET("calibrationPanel.xAndYGetOffsets", "Nozzle Alignment Illustrations_Step 4.fxml"),
    PRINT_CIRCLE("calibrationPanel.xAndYPrintingCircle", ""),
    PRINT_CIRCLE_CHECK("calibrationPanel.xAndYPrintCircleCheck",
                       "Nozzle Alignment Illustrations_Step 5.fxml"),
    FINISHED("calibrationPanel.calibrationSucceededMessage",
             "Nozzle Alignment Illustrations_Step 6.fxml"),
    CANCELLING("misc.resettingPrinter", ""),
    CANCELLED("", ""),
    DONE("", ""),
    FAILED("calibrationPanel.nozzleCalibrationFailed", "Nozzle Height Illustrations_Failure.fxml");

    private final String stepTitleResource;
    private final String diagramName;

    private CalibrationXAndYState(String stepTitleResource, String diagramName)
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

    public Optional<String> getDiagramName()
    {
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
