package celtech.coreUI.controllers.panels;

import celtech.Lookup;
import celtech.appManager.ApplicationMode;
import celtech.appManager.ApplicationStatus;
import celtech.configuration.ApplicationConfiguration;
import celtech.coreUI.SpinnerControl;
import celtech.coreUI.components.HyperlinkedLabel;
import celtech.coreUI.components.Notifications.ConditionalNotificationBar;
import celtech.coreUI.components.VerticalMenu;
import celtech.coreUI.components.buttons.GraphicButtonWithLabel;
import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.appManager.NotificationType;
import celtech.roboxbase.configuration.Filament;
import celtech.roboxbase.configuration.datafileaccessors.FilamentContainer;
import celtech.roboxbase.printerControl.model.Head;
import celtech.roboxbase.printerControl.model.Head.HeadType;
import celtech.roboxbase.printerControl.model.Head.ValveType;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.printerControl.model.PrinterException;
import celtech.roboxbase.printerControl.model.PrinterListChangesListener;
import celtech.roboxbase.printerControl.model.Reel;
import celtech.roboxbase.printerControl.model.statetransitions.StateTransitionManager;
import celtech.roboxbase.printerControl.model.statetransitions.calibration.CalibrationXAndYState;
import celtech.roboxbase.printerControl.model.statetransitions.calibration.NozzleHeightCalibrationState;
import celtech.roboxbase.printerControl.model.statetransitions.calibration.NozzleOpeningCalibrationState;
import celtech.roboxbase.printerControl.model.statetransitions.calibration.SingleNozzleHeightCalibrationState;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.util.Duration;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian
 */
public class CalibrationInsetPanelController implements Initializable,
        PrinterListChangesListener
{

    final CalibrationMenuConfiguration calibrationMenuConfiguration;
    ObjectProperty<CalibrationMode> calibrationMode = new SimpleObjectProperty<CalibrationMode>(null);
    CalibrationXAndYGUI calibrationXAndYGUI;
    CalibrationNozzleHeightGUI calibrationNozzleHeightGUI;
    CalibrationSingleNozzleHeightGUI calibrationSingleNozzleHeightGUI;
    CalibrationNozzleOpeningGUI calibrationNozzleOpeningGUI;
    
    StateTransitionManager stateManager;
    SpinnerControl spinnerControl;
    private ApplicationStatus applicationStatus = null;

    private ResourceBundle resources;

    private void resizeTopBorderPane()
    {
        topBorderPane.setPrefWidth(topPane.getWidth());
        topBorderPane.setPrefHeight(topPane.getHeight());
        topBorderPane.setMaxWidth(topPane.getWidth());
        topBorderPane.setMaxHeight(topPane.getHeight());
    }

    void resetMenuAndGoToChoiceMode()
    {
        calibrationMenu.reset();
        setCalibrationMode(CalibrationMode.CHOICE);
    }

    protected static enum ProgressVisibility
    {

        TEMP, PRINT, NONE;
    };

    private final Stenographer steno = StenographerFactory.getStenographer(
            CalibrationInsetPanelController.class.getName());

    @FXML
    protected VerticalMenu calibrationMenu;

    @FXML
    protected StackPane calibrateBottomMenu;

    @FXML
    protected Pane calibrationBottomArea;

    @FXML
    protected Pane altButtonContainer;

    @FXML
    protected Text stepNumber;

    @FXML
    protected Button buttonA;

    @FXML
    protected Button buttonB;

    @FXML
    protected GraphicButtonWithLabel nextButton;

    @FXML
    protected GraphicButtonWithLabel retryPrintButton;

    @FXML
    protected GraphicButtonWithLabel backToStatus;

    @FXML
    protected GraphicButtonWithLabel startCalibrationButton;

    @FXML
    protected GraphicButtonWithLabel cancelCalibrationButton;

    @FXML
    protected HyperlinkedLabel calibrationStatus;

    @FXML
    private VBox informationCentre;

    @FXML
    private BorderPane topBorderPane;

    @FXML
    private VBox topPane;

    @FXML
    private VBox diagramContainer;

    @FXML
    private HBox topMenuStrip;

    private Printer currentPrinter;
    private Pane diagramNode;
    DiagramController diagramController;
    private final Map<Node, Bounds> nodeToBoundsCache = new HashMap<>();
    private boolean backToStatusInhibitWhenAtTop = false;
    private Line lineToAnimate;
    private double originalAnimatedLineLength = 0;
    private Transition animatedFilamentTransition = new Transition()
    {
        {
            setCycleDuration(Duration.millis(2000));
        }

        @Override
        public void interpolate(double frac)
        {
            lineToAnimate.setEndY(frac * originalAnimatedLineLength);
        }
    };

    private ConditionalNotificationBar oneExtruderNoFilamentSelectedNotificationBar;
    private ConditionalNotificationBar oneExtruderNoFilamentNotificationBar;
    private ConditionalNotificationBar twoExtrudersNoFilament1SelectedNotificationBar;
    private ConditionalNotificationBar twoExtrudersNoFilament1NotificationBar;
    private ConditionalNotificationBar twoExtrudersNoFilament2SelectedNotificationBar;
    private ConditionalNotificationBar twoExtrudersNoFilament2NotificationBar;

    private ConditionalNotificationBar cantCalibrateHeadIsDetachedNotificationBar;
    private boolean notRequiredMessagesShown = false;
    
    @FXML
    void buttonAAction(ActionEvent event)
    {
        stateManager.followTransition(StateTransitionManager.GUIName.A_BUTTON);
    }

    @FXML
    void buttonBAction(ActionEvent event)
    {
        stateManager.followTransition(StateTransitionManager.GUIName.B_BUTTON);
    }

    @FXML
    void nextButtonAction(ActionEvent event)
    {
        stateManager.followTransition(StateTransitionManager.GUIName.NEXT);
    }

    @FXML
    void backToStatusAction(ActionEvent event)
    {
        if (calibrationMode.get() == CalibrationMode.CHOICE
                || stateManager == null)
        {
            ApplicationStatus.getInstance().setMode(ApplicationMode.STATUS);
        } else
        {
            try
            {
                stateManager.followTransition(StateTransitionManager.GUIName.BACK);
            }
            catch (RuntimeException ex)
            {
            }
            ApplicationStatus.getInstance().setMode(ApplicationMode.STATUS);
            calibrationMenu.reset();
        }
    }

    @FXML
    void startCalibration(ActionEvent event)
    {
        stateManager.followTransition(StateTransitionManager.GUIName.START);
    }

    @FXML
    void cancelCalibration(ActionEvent event)
    {
        try
        {
            stateManager.cancel();
        } catch (Exception ex)
        {
            steno.error("Error cancelling calibration: " + ex);
        }
    }

    @FXML
    void retryCalibration(ActionEvent event)
    {
        stateManager.followTransition(StateTransitionManager.GUIName.RETRY);
    }
    
    private final ChangeListener<ApplicationMode> applicationModeChangeListener = new ChangeListener<ApplicationMode>()
    {
        @Override
        public void changed(ObservableValue<? extends ApplicationMode> observable, ApplicationMode oldValue, ApplicationMode newValue)
        {
            if (newValue == ApplicationMode.CALIBRATION_CHOICE)
                resetMenuAndGoToChoiceMode();
        }
    };


    protected void hideAllInputControlsExceptStepNumber()
    {
        backToStatus.setVisible(false);
        retryPrintButton.setVisible(false);
        startCalibrationButton.setVisible(false);
        cancelCalibrationButton.setVisible(false);
        nextButton.setVisible(false);
        buttonB.setVisible(false);
        buttonA.setVisible(false);
        stepNumber.setVisible(true);
        hideSpinner();
        if (diagramNode != null)
        {
            diagramNode.setVisible(false);
        }
        stepNumber.setText("");
    }

    public CalibrationInsetPanelController()
    {
        this.calibrationMenuConfiguration = new CalibrationMenuConfiguration(true, true, true);
    }

    public CalibrationInsetPanelController(boolean displayOpening,
            boolean displayHeight,
            boolean displayAlignment)
    {
        this.calibrationMenuConfiguration = new CalibrationMenuConfiguration(displayOpening, displayHeight, displayAlignment);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        applicationStatus = ApplicationStatus.getInstance();

        oneExtruderNoFilamentSelectedNotificationBar = new ConditionalNotificationBar("dialogs.cantPrintAttachRoboxReelMessage", NotificationType.CAUTION);
        oneExtruderNoFilamentNotificationBar = new ConditionalNotificationBar("dialogs.cantPrintNoFilamentMessage", NotificationType.CAUTION);
        twoExtrudersNoFilament1SelectedNotificationBar = new ConditionalNotificationBar("dialogs.cantPrintAttachRoboxReelMessage0", NotificationType.CAUTION);
        twoExtrudersNoFilament1NotificationBar = new ConditionalNotificationBar("dialogs.cantPrintNoFilamentMessage0", NotificationType.CAUTION);
        twoExtrudersNoFilament2SelectedNotificationBar = new ConditionalNotificationBar("dialogs.cantPrintAttachRoboxReelMessage1", NotificationType.CAUTION);
        twoExtrudersNoFilament2NotificationBar = new ConditionalNotificationBar("dialogs.cantPrintNoFilamentMessage1", NotificationType.CAUTION);

        cantCalibrateHeadIsDetachedNotificationBar = new ConditionalNotificationBar("dialogs.cantCalibrateHeadIsDetached", NotificationType.CAUTION);

        this.resources = resources;
        spinnerControl = Lookup.getSpinnerControl();

        setCalibrationMode(CalibrationMode.CHOICE);

        BaseLookup.getPrinterListChangesNotifier().addListener(this);

        calibrationMenuConfiguration.configureCalibrationMenu(calibrationMenu, this);

        animatedFilamentTransition.setCycleCount(Timeline.INDEFINITE);
        animatedFilamentTransition.setAutoReverse(false);

        addDiagramMoveScaleListeners();
        
        // If the application mode changes, or a different printer is selected, then the calibration page should reset to a known state.
        ApplicationStatus.getInstance().modeProperty().addListener(
                (ObservableValue<? extends ApplicationMode> observable, ApplicationMode oldValue, ApplicationMode newValue) ->
                {
                    if (newValue == ApplicationMode.CALIBRATION_CHOICE)
                        resetMenuAndGoToChoiceMode();
                });
        
        Lookup.getSelectedPrinterProperty().addListener(
                (ObservableValue<? extends Printer> observable, Printer oldValue, Printer newValue) ->
                {
                    if (ApplicationStatus.getInstance().getMode() == ApplicationMode.CALIBRATION_CHOICE &&
                        currentPrinter != newValue)
                    {
                        resetMenuAndGoToChoiceMode();
                    }
                });
     }

    private void addDiagramMoveScaleListeners()
    {
        topPane.widthProperty().addListener(
                (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) ->
                {
                    resizeTopBorderPane();
                });

        topPane.heightProperty().addListener(
                (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) ->
                {
                    resizeTopBorderPane();
                });

        diagramContainer.widthProperty().addListener(
                (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) ->
                {
                    resizeDiagram();
                });

        diagramContainer.heightProperty().addListener(
                (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) ->
                {
                    resizeDiagram();
                });

    }

    private void resizeDiagram()
    {
        if (diagramNode == null)
        {
            return;
        }

        double diagramWidth = nodeToBoundsCache.get(diagramNode).getWidth();
        double diagramHeight = nodeToBoundsCache.get(diagramNode).getHeight();

        double availableWidth = diagramContainer.getWidth();
        double availableHeight = diagramContainer.getHeight();

        double requiredScaleHeight = availableHeight / diagramHeight * 0.95;
        double requiredScaleWidth = availableWidth / diagramWidth * 0.95;
        double requiredScale = Math.min(requiredScaleHeight, requiredScaleWidth);
        requiredScale = Math.min(requiredScale, 1.3d);
        diagramController.setScale(requiredScale, diagramNode);

        diagramNode.setPrefWidth(0);
        diagramNode.setPrefHeight(0);

        double scaledDiagramWidth = diagramWidth * requiredScale;

        double xTranslate = 0;
        double yTranslate = 0;

        xTranslate = -scaledDiagramWidth / 2;
        yTranslate -= availableHeight / 2.0;

        diagramNode.setTranslateX(xTranslate);
        diagramNode.setTranslateY(yTranslate);

    }

    /**
     * Create and return the diagram node for the given fxml file.
     *
     * @param diagramName
     * @return
     */
    private Node getDiagramNode(URL fxmlFileName)
    {
        Pane loadedDiagramNode = null;
        animatedFilamentTransition.stop();
        try
        {
            FXMLLoader loader = new FXMLLoader(fxmlFileName, resources);
            diagramController = new DiagramController();
            loader.setController(diagramController);
            loadedDiagramNode = loader.load();
            lineToAnimate = (Line) loadedDiagramNode.lookup("#animatedFilament");
            if (lineToAnimate != null)
            {
                originalAnimatedLineLength = lineToAnimate.getEndY();
                animatedFilamentTransition.playFrom(Duration.ZERO);
            }

            Bounds bounds = getBoundsOfNotYetDisplayedNode(loadedDiagramNode);
            steno.debug("diagram bounds are " + bounds);
            nodeToBoundsCache.put(loadedDiagramNode, bounds);
            diagramController.setStateTransitionManager(stateManager);

        } catch (IOException ex)
        {
            steno.exception("Could not load diagram: " + fxmlFileName, ex);
        }
        return loadedDiagramNode;

    }

    private Bounds getBoundsOfNotYetDisplayedNode(Pane loadedDiagramNode)
    {
        Group group = new Group(loadedDiagramNode);
        Scene scene = new Scene(group);
        scene.getStylesheets().add(ApplicationConfiguration.getMainCSSFile());
        group.applyCss();
        group.layout();
        Bounds bounds = loadedDiagramNode.getLayoutBounds();
        return bounds;
    }

    protected void showDiagram(URL fxmlLocation)
    {
        diagramNode = (Pane) getDiagramNode(fxmlLocation);
        if (diagramNode == null)
        {
            return;
        }

        diagramContainer.getChildren().clear();
        diagramContainer.getChildren().add(diagramNode);

        resizeDiagram();

        diagramNode.setVisible(true);
    }

    private void switchToPrinter(Printer printer)
    {
       if (printer == null || printer != currentPrinter)
           notRequiredMessagesShown = false;
       if (printer != null)
        {
            bindPrinter(printer);
        }
        currentPrinter = printer;
    }

    private void bindPrinter(Printer printer)
    {
        configureStartButtonForMode(printer);
    }

    private void configureStartButtonForMode(Printer printer)
    {
        if (printer == null)
        {
            return;
        }

        cantCalibrateHeadIsDetachedNotificationBar.setAppearanceCondition(Bindings.isNull(printer.headProperty())
                .and(applicationStatus.modeProperty().isEqualTo(ApplicationMode.CALIBRATION_CHOICE)));

        BooleanBinding oneExtruderPrinter = printer.extrudersProperty().get(0).isFittedProperty().not();
        BooleanBinding twoExtruderPrinter = printer.extrudersProperty().get(1).isFittedProperty().not().not();
        BooleanBinding noFilament1Selected = Bindings.valueAt(printer.reelsProperty(), 0).isNull();
        BooleanBinding noFilament2Selected = Bindings.valueAt(printer.reelsProperty(), 1).isNull();
        noFilament1Selected.get();
        noFilament2Selected.get();

        oneExtruderNoFilamentSelectedNotificationBar.setAppearanceCondition(oneExtruderPrinter.and(
                noFilament1Selected).and(applicationStatus.modeProperty().isEqualTo(ApplicationMode.CALIBRATION_CHOICE)));

        oneExtruderNoFilamentNotificationBar.setAppearanceCondition(oneExtruderPrinter.and(
                printer.extrudersProperty().get(0).
                filamentLoadedProperty().not()).and(applicationStatus.modeProperty().isEqualTo(ApplicationMode.CALIBRATION_CHOICE)));

        twoExtrudersNoFilament1SelectedNotificationBar.setAppearanceCondition(twoExtruderPrinter.and(
                noFilament1Selected).and(applicationStatus.modeProperty().isEqualTo(ApplicationMode.CALIBRATION_CHOICE)));

        twoExtrudersNoFilament1NotificationBar.setAppearanceCondition(twoExtruderPrinter.and(
                printer.extrudersProperty().get(0).
                filamentLoadedProperty().not()).and(applicationStatus.modeProperty().isEqualTo(ApplicationMode.CALIBRATION_CHOICE)));

        if (printer.headProperty().get() != null)
        {
            twoExtrudersNoFilament2SelectedNotificationBar.setAppearanceCondition(twoExtruderPrinter
                    .and(printer.headProperty().get().headTypeProperty().isEqualTo(HeadType.DUAL_MATERIAL_HEAD))
                    .and(noFilament2Selected)
                    .and(applicationStatus.modeProperty().isEqualTo(ApplicationMode.CALIBRATION_CHOICE)));

            twoExtrudersNoFilament2NotificationBar.setAppearanceCondition(twoExtruderPrinter
                    .and(printer.headProperty().get().headTypeProperty().isEqualTo(HeadType.DUAL_MATERIAL_HEAD))
                    .and(printer.extrudersProperty().get(1).
                            filamentLoadedProperty().not()).and(applicationStatus.modeProperty().isEqualTo(ApplicationMode.CALIBRATION_CHOICE)));
        }
        else
        {
            twoExtrudersNoFilament2SelectedNotificationBar.clearAppearanceCondition();
            twoExtrudersNoFilament2NotificationBar.clearAppearanceCondition();
        }

        switch (calibrationMode.get())
        {
            case NOZZLE_OPENING:
                startCalibrationButton.disableProperty().bind(
                        printer.canCalibrateNozzleOpeningProperty().not());
                break;
            case NOZZLE_HEIGHT:
                startCalibrationButton.disableProperty().bind(
                        printer.canCalibrateNozzleHeightProperty().not());
                break;
            case X_AND_Y_OFFSET:
                startCalibrationButton.disableProperty().bind(
                        printer.canCalibrateXYAlignmentProperty().not());
                break;
            case CHOICE:
                if (!notRequiredMessagesShown &&
                    printer.headProperty().get() != null &&
                    printer.headProperty().get().valveTypeProperty().get() == ValveType.NOT_FITTED)
                {
                    notRequiredMessagesShown = true;
                    BaseLookup.getSystemNotificationHandler().showInformationNotification(Lookup.i18n("openNozzleCalibrationNotRequired.title"),
                                                                                          Lookup.i18n("openNozzleCalibrationNotRequired.message"));
                    BaseLookup.getSystemNotificationHandler().showInformationNotification(Lookup.i18n("xyAlignmentNotRequired.title"),
                                                                                          Lookup.i18n("xyAlignmentNotRequired.message"));
                }
                break;
        }
    }

    public void setCalibrationMode(CalibrationMode calibrationMode)
    {
        this.calibrationMode.set(calibrationMode);
        switchToPrinter(Lookup.getSelectedPrinterProperty().get());
        configureStartButtonForMode(currentPrinter);
        switch (calibrationMode)
        {
            case NOZZLE_OPENING:
            {
                try
                {
                    stateManager = currentPrinter.startCalibrateNozzleOpening(Lookup.getUserPreferences().isSafetyFeaturesOn());
                } catch (PrinterException ex)
                {
                    steno.warning("Can't switch to calibration: " + ex);
                    return;
                }
            }
            calibrationNozzleOpeningGUI = new CalibrationNozzleOpeningGUI(this, stateManager);
            calibrationNozzleOpeningGUI.setState(NozzleOpeningCalibrationState.IDLE);
            break;

            case NOZZLE_HEIGHT:
            if (currentPrinter.headProperty().get() != null &&
                currentPrinter.headProperty().get().getNozzles().size() == 1)
            {
                try
                {
                    stateManager = currentPrinter.startCalibrateSingleNozzleHeight(Lookup.getUserPreferences().isSafetyFeaturesOn());
                } catch (PrinterException ex)
                {
                    steno.warning("Can't switch to calibration: " + ex);
                    return;
                }
            
                calibrationSingleNozzleHeightGUI = new CalibrationSingleNozzleHeightGUI(this, stateManager);
                calibrationSingleNozzleHeightGUI.setState(SingleNozzleHeightCalibrationState.IDLE);
            }
            else
            {
                try
                {
                    stateManager = currentPrinter.startCalibrateNozzleHeight(Lookup.getUserPreferences().isSafetyFeaturesOn());
                } catch (PrinterException ex)
                {
                    steno.warning("Can't switch to calibration: " + ex);
                    return;
                }
            
                calibrationNozzleHeightGUI = new CalibrationNozzleHeightGUI(this, stateManager);
                calibrationNozzleHeightGUI.setState(NozzleHeightCalibrationState.IDLE);
            }

            break;

            case X_AND_Y_OFFSET:
            {
                try
                {
                    stateManager = currentPrinter.startCalibrateXAndY(Lookup.getUserPreferences().isSafetyFeaturesOn());
                } catch (PrinterException ex)
                {
                    steno.warning("Can't switch to calibration: " + ex);
                    return;
                }
            }
            calibrationXAndYGUI = new CalibrationXAndYGUI(this, stateManager);
            calibrationXAndYGUI.setState(CalibrationXAndYState.IDLE);
            break;

            case CHOICE:
                setupChoice();
        }
    }

    private void setupChoice()
    {
        calibrationStatus.replaceText(Lookup.i18n("calibrationPanel.chooseCalibration"));
        calibrationMenu.reset();
        hideAllInputControlsExceptStepNumber();
        stepNumber.setVisible(false);
        if (!backToStatusInhibitWhenAtTop)
        {
            backToStatus.setVisible(true);
        } else
        {
            backToStatus.setVisible(false);
        }
        cancelCalibrationButton.setVisible(false);
    }

    protected void showSpinner()
    {
        if (spinnerControl != null)
        {
            spinnerControl.startSpinning(informationCentre);
        }
    }

    protected void hideSpinner()
    {
        if (spinnerControl != null)
        {
            spinnerControl.stopSpinning();
        }
    }

    public void hideCommonBordersAndBackButton()
    {
        topMenuStrip.setMinHeight(0);
        topMenuStrip.setPrefHeight(0);
        topMenuStrip.setVisible(false);
        backToStatus.setVisible(false);
        backToStatusInhibitWhenAtTop = true;
    }

    @Override
    public void whenPrinterAdded(Printer printer)
    {
    }

    @Override
    public void whenPrinterRemoved(Printer printer)
    {
        if (printer == currentPrinter)
        {
            resetMenuAndGoToChoiceMode();
            backToStatusAction(null);
        }
    }

    @Override
    public void whenHeadAdded(Printer printer)
    {
        if (printer == currentPrinter)
        {
            bindPrinter(printer);
        }
    }

    @Override
    public void whenHeadRemoved(Printer printer, Head head)
    {
        if (printer == currentPrinter)
        {
            setCalibrationMode(CalibrationMode.CHOICE);
        }
    }

    @Override
    public void whenReelAdded(Printer printer, int reelIndex)
    {
    }

    @Override
    public void whenReelRemoved(Printer printer, Reel reel, int reelIndex)
    {
    }

    @Override
    public void whenReelChanged(Printer printer, Reel reel)
    {
    }

    @Override
    public void whenExtruderAdded(Printer printer, int extruderIndex)
    {
    }

    @Override
    public void whenExtruderRemoved(Printer printer, int extruderIndex)
    {
    }
}
