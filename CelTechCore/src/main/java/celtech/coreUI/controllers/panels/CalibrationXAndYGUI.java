/*
 * Copyright 2014 CEL UK
 */
package celtech.coreUI.controllers.panels;

import celtech.Lookup;
import celtech.configuration.ApplicationConfiguration;
import celtech.roboxbase.printerControl.model.statetransitions.StateTransitionManager;
import celtech.roboxbase.printerControl.model.statetransitions.StateTransitionManager.GUIName;
import celtech.roboxbase.printerControl.model.statetransitions.StateTransition;
import celtech.roboxbase.printerControl.model.statetransitions.calibration.CalibrationXAndYState;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.Region;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author tony
 */
public class CalibrationXAndYGUI
{

    private final Stenographer steno = StenographerFactory.getStenographer(
            CalibrationXAndYGUI.class.getName());

    private CalibrationInsetPanelController controller;
    StateTransitionManager<CalibrationXAndYState> stateManager;
    Map<GUIName, Region> namesToButtons = new HashMap<>();

    public CalibrationXAndYGUI(CalibrationInsetPanelController controller,
            StateTransitionManager stateManager)
    {
        this.controller = controller;
        this.stateManager = stateManager;

        stateManager.stateGUITProperty().addListener(new ChangeListener()
        {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue)
            {
                setState((CalibrationXAndYState) newValue);
            }
        });
        populateNamesToButtons(controller);
    }

    private void showAppropriateButtons(CalibrationXAndYState state)
    {
        controller.hideAllInputControlsExceptStepNumber();
        if (state.showCancelButton())
        {
            controller.cancelCalibrationButton.setVisible(true);
        }
        for (StateTransition<CalibrationXAndYState> allowedTransition : this.stateManager.getTransitions())
        {
            if (namesToButtons.containsKey(allowedTransition.getGUIName()))
            {
                namesToButtons.get(allowedTransition.getGUIName()).setVisible(true);
            }
        }
    }

    public void setState(CalibrationXAndYState state)
    {
        steno.debug("GUI going to state " + state);
        controller.calibrationStatus.replaceText(state.getStepTitle());
        showAppropriateButtons(state);
        if (state.getDiagramName().isPresent())
        {
            URL fxmlURL = getClass().getResource(
                    ApplicationConfiguration.fxmlDiagramsResourcePath
                    + "nozzlealignment" + "/" + state.getDiagramName().get());

            controller.showDiagram(fxmlURL);
        }
        int stepNo = 0;
        switch (state)
        {
            case IDLE:
                break;
            case PRINT_PATTERN:
                controller.calibrationMenu.disableNonSelectedItems();
                controller.showSpinner();
                stepNo = 1;
                break;
            case GET_Y_OFFSET:
                stepNo = 2;
                break;
//            case PRINT_CIRCLE:
//                controller.showSpinner();
//                controller.setCalibrationProgressVisible(
//                    CalibrationInsetPanelController.ProgressVisibility.PRINT);
//                stepNo = 3;
//                break;
//            case PRINT_CIRCLE_CHECK:
//                stepNo = 3;
//                break;
            case CANCELLED:
                controller.resetMenuAndGoToChoiceMode();
                break;
            case FINISHED:
                controller.calibrationMenu.reset();
                break;
            case FAILED:
                controller.calibrationMenu.enableNonSelectedItems();
                break;
        }
        if (stepNo != 0)
        {
            controller.stepNumber.setText(String.format(Lookup.i18n("calibrationPanel.stepXOf2"), stepNo));
        }
    }

    private void populateNamesToButtons(CalibrationInsetPanelController controller)
    {
        namesToButtons.put(GUIName.YES, controller.buttonA);
        namesToButtons.put(GUIName.NO, controller.buttonB);
        namesToButtons.put(GUIName.NEXT, controller.nextButton);
        namesToButtons.put(GUIName.RETRY, controller.retryPrintButton);
        namesToButtons.put(GUIName.START, controller.startCalibrationButton);
        namesToButtons.put(GUIName.BACK, controller.backToStatus);
    }

}
