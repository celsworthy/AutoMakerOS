package celtech.coreUI.controllers.panels;

import celtech.Lookup;
import celtech.appManager.ApplicationMode;
import celtech.appManager.ApplicationStatus;
import celtech.appManager.ModelContainerProject;
import celtech.coreUI.components.Notifications.ConditionalNotificationBar;
import celtech.coreUI.components.RestrictedNumberField;
import celtech.coreUI.components.buttons.GraphicButtonWithLabel;
import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.appManager.NotificationType;
import celtech.roboxbase.configuration.Filament;
import celtech.roboxbase.configuration.datafileaccessors.FilamentContainer;
import celtech.roboxbase.printerControl.PrinterStatus;
import celtech.roboxbase.printerControl.model.Head;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.printerControl.model.PrinterException;
import celtech.roboxbase.printerControl.model.PrinterListChangesAdapter;
import celtech.roboxbase.printerControl.model.Reel;
import celtech.roboxbase.printerControl.model.statetransitions.StateTransition;
import celtech.roboxbase.printerControl.model.statetransitions.StateTransitionManager;
import celtech.roboxbase.printerControl.model.statetransitions.StateTransitionManager.GUIName;
import celtech.roboxbase.printerControl.model.statetransitions.purge.PurgeState;
import static celtech.roboxbase.printerControl.model.statetransitions.purge.PurgeState.CONFIRM_TEMPERATURE;
import static celtech.roboxbase.printerControl.model.statetransitions.purge.PurgeState.FAILED;
import static celtech.roboxbase.printerControl.model.statetransitions.purge.PurgeState.FINISHED;
import static celtech.roboxbase.printerControl.model.statetransitions.purge.PurgeState.HEATING;
import static celtech.roboxbase.printerControl.model.statetransitions.purge.PurgeState.IDLE;
import static celtech.roboxbase.printerControl.model.statetransitions.purge.PurgeState.INITIALISING;
import static celtech.roboxbase.printerControl.model.statetransitions.purge.PurgeState.RUNNING_PURGE;
import celtech.roboxbase.printerControl.model.statetransitions.purge.PurgeStateTransitionManager;
import celtech.roboxbase.utils.PrinterUtils;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javax.print.PrintException;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian
 */
public class PurgeInsetPanelController implements Initializable
{

    private final Stenographer steno = StenographerFactory.getStenographer(
            PurgeInsetPanelController.class.getName());

    private ModelContainerProject project = null;
    private Printer printer = null;
    private Filament currentMaterial0;
    private Filament currentMaterial1;
    private ApplicationStatus applicationStatus = null;
    private ConditionalNotificationBar cantPurgeDoorIsOpenNotificationBar;
    private ConditionalNotificationBar cantPrintNoFilamentNotificationBar;
    private ConditionalNotificationBar cantPrintNoFilament0NotificationBar;
    private ConditionalNotificationBar cantPrintNoFilament1NotificationBar;
    private ConditionalNotificationBar cantPrintFilamentNotSpecifiedNotificationBar;
    private ConditionalNotificationBar cantPrintFilament0NotSpecifiedNotificationBar;
    private ConditionalNotificationBar cantPrintFilament1NotSpecifiedNotificationBar;

    BooleanBinding purgeTwoNozzleHeaters;

    PurgeStateTransitionManager transitionManager;

    Map<StateTransitionManager.GUIName, Region> namesToButtons = new HashMap<>();

    @FXML
    private VBox container;

    @FXML
    private Group diagram;

    @FXML
    private VBox diagramContainer;

    @FXML
    private GraphicButtonWithLabel startPurgeButton;

    @FXML
    private Text purgeStatus;

    @FXML
    private Text resettingPrinter;

    @FXML
    private GraphicButtonWithLabel cancelPurgeButton;

    @FXML
    private GraphicButtonWithLabel proceedButton;

    @FXML
    private GraphicButtonWithLabel okButton;

    @FXML
    private GraphicButtonWithLabel backToStatus;

    @FXML
    private GraphicButtonWithLabel repeatButton;

    @FXML
    private Text lastMaterialTemperature0;

    @FXML
    private GridPane purgeDetailsGrid;

    @FXML
    private RestrictedNumberField purgeTemperature0;

    @FXML
    private Text currentMaterialTemperature0;

    @FXML
    private Text textCurrentMaterial0;

    @FXML
    private GridPane purgeDetailsGrid0;

    @FXML
    private Text lastMaterialTemperature1;

    @FXML
    private RestrictedNumberField purgeTemperature1;

    @FXML
    private Text currentMaterialTemperature1;

    @FXML
    private Text textCurrentMaterial1;

    @FXML
    private GridPane purgeDetailsGrid1;

    @FXML
    private CheckBox purgeMaterial0;

    @FXML
    private CheckBox purgeMaterial1;

    @FXML
    private HBox topMenuStrip;

    private boolean backToStatusInhibitWhenAtTop = false;

    @FXML
    void start(ActionEvent event)
    {
        transitionManager.followTransition(GUIName.START);
    }

    @FXML
    void proceed(ActionEvent event)
    {
        transitionManager.followTransition(GUIName.NEXT);
    }

    @FXML
    void cancel(ActionEvent event)
    {
        transitionManager.cancel();
    }

    @FXML
    void repeat(ActionEvent event)
    {
        transitionManager.followTransition(GUIName.RETRY);
    }

    @FXML
    void okPressed(ActionEvent event)
    {
        closeWindow(null);
    }

    @FXML
    void closeWindow(ActionEvent event)
    {

        ApplicationStatus.getInstance().returnToLastMode();

        if (project != null)
        {
            BaseLookup.getSystemNotificationHandler().askUserToClearBed();

            // Need to go to settings page for this project
            ApplicationStatus.getInstance().setMode(ApplicationMode.SETTINGS);
            project = null;
        }
    }

    private final ChangeListener<Boolean> purgeTempEntryListener0
            = (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) ->
            {
                if (printer.headProperty().get() != null
                && printer.headProperty().get().headTypeProperty().get() == Head.HeadType.DUAL_MATERIAL_HEAD)
                {
                    transitionManager.setPurgeTemperature(1, purgeTemperature0.getAsInt());
                } else
                {
                    transitionManager.setPurgeTemperature(0, purgeTemperature0.getAsInt());
                }
            };

    private final ChangeListener<Boolean> purgeTempEntryListener1
            = (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) ->
            {
                transitionManager.setPurgeTemperature(0, purgeTemperature1.getAsInt());
            };

    private final ChangeListener<Boolean> purgeMaterial0Listener
            = (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) ->
            {
                if (printer.headProperty().get().headTypeProperty().get() == Head.HeadType.SINGLE_MATERIAL_HEAD)
                {
                    transitionManager.setPurgeNozzleHeater0(purgeMaterial0.isSelected());
                } else
                {
                    transitionManager.setPurgeNozzleHeater1(purgeMaterial0.isSelected());
                }
            };

    private final ChangeListener<Boolean> purgeMaterial1Listener
            = (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) ->
            {
                if (printer.headProperty().get().headTypeProperty().get() == Head.HeadType.SINGLE_MATERIAL_HEAD)
                {
                    transitionManager.setPurgeNozzleHeater1(purgeMaterial1.isSelected());
                } else
                {
                    transitionManager.setPurgeNozzleHeater0(purgeMaterial1.isSelected());
                }
            };


    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        populateNamesToButtons();

        applicationStatus = ApplicationStatus.getInstance();

        cantPurgeDoorIsOpenNotificationBar = new ConditionalNotificationBar("dialogs.cantPurgeDoorIsOpenMessage", NotificationType.CAUTION);
        cantPrintNoFilamentNotificationBar = new ConditionalNotificationBar("dialogs.cantPrintNoFilamentMessage", NotificationType.CAUTION);
        cantPrintNoFilament0NotificationBar = new ConditionalNotificationBar("dialogs.cantPrintNoFilamentMessage0", NotificationType.CAUTION);
        cantPrintNoFilament1NotificationBar = new ConditionalNotificationBar("dialogs.cantPrintNoFilamentMessage1", NotificationType.CAUTION);
        cantPrintFilamentNotSpecifiedNotificationBar = new ConditionalNotificationBar("dialogs.cantPrintNoFilamentSelectedMessage", NotificationType.CAUTION);
        cantPrintFilament0NotSpecifiedNotificationBar = new ConditionalNotificationBar("dialogs.cantPrintNoFilamentSelectedMessage0", NotificationType.CAUTION);
        cantPrintFilament1NotSpecifiedNotificationBar = new ConditionalNotificationBar("dialogs.cantPrintNoFilamentSelectedMessage1", NotificationType.CAUTION);

        FXMLUtilities.addColonsToLabels(purgeDetailsGrid);

        BaseLookup.getPrinterListChangesNotifier().addListener(new PrinterListChangesAdapter()
        {

            @Override
            public void whenReelAdded(Printer printer, int reelIndex)
            {
                if (PurgeInsetPanelController.this.printer == printer)
                {
                    PurgeInsetPanelController.this.showCurrentMaterial0();
                    PurgeInsetPanelController.this.showCurrentMaterial1();
                }
            }

            @Override
            public void whenReelRemoved(Printer printer, Reel reel, int reelIndex)
            {
                if (PurgeInsetPanelController.this.printer == printer)
                {
                    PurgeInsetPanelController.this.showCurrentMaterial0();
                    PurgeInsetPanelController.this.showCurrentMaterial1();
                }
            }

            @Override
            public void whenReelChanged(Printer printer, Reel reel)
            {
                if (PurgeInsetPanelController.this.printer == printer)
                {
                    PurgeInsetPanelController.this.showCurrentMaterial0();
                    PurgeInsetPanelController.this.showCurrentMaterial1();
                }
            }
        });

        diagramContainer.widthProperty().addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1)
            {
                resizePurgeDisplay(diagramContainer.getWidth(), diagramContainer.getHeight());
            }
        });

        diagramContainer.heightProperty().addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1)
            {
                resizePurgeDisplay(diagramContainer.getWidth(), diagramContainer.getHeight());
            }
        });

        diagramContainer.visibleProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1)
            {
                resizePurgeDisplay(diagramContainer.getWidth(), diagramContainer.getHeight());
            }
        });

        resizePurgeDisplay(diagramContainer.getWidth(), diagramContainer.getHeight());
    }

    public void hideCommonBordersAndBackButton()
    {
        topMenuStrip.setMinHeight(0);
        topMenuStrip.setPrefHeight(0);
        topMenuStrip.setVisible(false);
        backToStatus.setVisible(false);
        backToStatusInhibitWhenAtTop = true;
    }

    private void resizePurgeDisplay(double displayWidth, double displayHeight)
    {
        final double beginWidth = 804;
        final double beginHeight = 345;
        final double aspect = beginWidth / beginHeight;

        displayWidth = displayWidth * 0.9;
        displayHeight = displayHeight * 0.9;

        double displayAspect = displayWidth / displayHeight;

        double newWidth = 0;
        double newHeight = 0;

        if (displayAspect >= aspect)
        {
            // Drive from height
            newWidth = displayHeight * aspect;
            newHeight = displayHeight;
        } else
        {
            //Drive from width
            newHeight = displayWidth / aspect;
            newWidth = displayWidth;
        }

        double xScale = newWidth / beginWidth;
        double yScale = newHeight / beginHeight;

        diagram.setScaleX(xScale);
        diagram.setScaleY(yScale);
    }

    private void populateNamesToButtons()
    {
        namesToButtons.put(StateTransitionManager.GUIName.NEXT, proceedButton);
        namesToButtons.put(StateTransitionManager.GUIName.RETRY, repeatButton);
        namesToButtons.put(StateTransitionManager.GUIName.START, startPurgeButton);
        namesToButtons.put(StateTransitionManager.GUIName.BACK, backToStatus);
        namesToButtons.put(StateTransitionManager.GUIName.COMPLETE, okButton);
    }

    /**
     * According to the available transitions, show the appropriate buttons.
     */
    private void showAppropriateButtons(PurgeState state)
    {
        if (state.showCancelButton())
        {
            cancelPurgeButton.setVisible(true);
        }
        for (StateTransition<PurgeState> allowedTransition : transitionManager.getTransitions())
        {
            if (namesToButtons.containsKey(allowedTransition.getGUIName()))
            {
                namesToButtons.get(allowedTransition.getGUIName()).setVisible(true);
            }
        }
    }

    private void hideAllButtons()
    {
        cancelPurgeButton.setVisible(false);
        proceedButton.setVisible(false);
        repeatButton.setVisible(false);
        startPurgeButton.setVisible(false);
        backToStatus.setVisible(false);
        okButton.setVisible(false);
        purgeDetailsGrid0.setVisible(false);
        purgeDetailsGrid1.setVisible(false);
        diagramContainer.setVisible(false);
    }

    public void setState(PurgeState state)
    {
        steno.debug("go to state " + state);
        steno.debug("printer status is " + printer.printerStatusProperty().get());
        hideAllButtons();
        showAppropriateButtons(state);
        resettingPrinter.setVisible(false);
        purgeStatus.setVisible(true);
        purgeStatus.setText(state.getStepTitle());
        purgeTemperature0.valueChangedProperty().removeListener(purgeTempEntryListener0);
        if (purgeTwoNozzleHeaters.get())
        {
            purgeTemperature1.valueChangedProperty().removeListener(purgeTempEntryListener1);
        }
        switch (state)
        {
            case IDLE:
                break;
            case INITIALISING:
                break;
            case CONFIRM_TEMPERATURE:
                showCurrentMaterial0();
                purgeTemperature0.valueChangedProperty().addListener(purgeTempEntryListener0);
                purgeDetailsGrid0.setVisible(true);

                if (purgeTwoNozzleHeaters.get())
                {
                    showCurrentMaterial1();
                    purgeTemperature1.valueChangedProperty().addListener(purgeTempEntryListener1);
                    purgeMaterial0.setVisible(true);
                } else
                {
                    purgeMaterial0.setVisible(false);
                }
                purgeDetailsGrid1.setVisible(purgeTwoNozzleHeaters.get());
                break;
            case HEATING:
                break;
            case RUNNING_PURGE:
                diagramContainer.setVisible(true);
                break;
            case FINISHED:
                diagramContainer.setVisible(true);
                break;
            case DONE:
                closeWindow(null);
                break;
            case FAILED:
                break;
            case CANCELLING:
                resettingPrinter.setVisible(true);
                purgeStatus.setVisible(false);
                break;
            case CANCELLED:
                closeWindow(null);
                break;
        }
    }

    private MapChangeListener<Integer, Filament> effectiveFilamentListener = (MapChangeListener.Change<? extends Integer, ? extends Filament> change) ->
    {
        showCurrentMaterial0();
        showCurrentMaterial1();
    };

    /**
     * Bind to the given printer. This is only called once for a given purge and
     * should not be called again during the purge.
     */
    private void bindPrinter(Printer printer)
    {
        if (this.printer != null)
        {
            cantPurgeDoorIsOpenNotificationBar.clearAppearanceCondition();
            cantPrintNoFilamentNotificationBar.clearAppearanceCondition();
            cantPrintNoFilament0NotificationBar.clearAppearanceCondition();
            cantPrintNoFilament1NotificationBar.clearAppearanceCondition();
            cantPrintFilamentNotSpecifiedNotificationBar.clearAppearanceCondition();
            cantPrintFilament0NotSpecifiedNotificationBar.clearAppearanceCondition();
            cantPrintFilament1NotSpecifiedNotificationBar.clearAppearanceCondition();
            textCurrentMaterial0.visibleProperty().unbind();
            textCurrentMaterial1.visibleProperty().unbind();
            startPurgeButton.disableProperty().unbind();
            proceedButton.disableProperty().unbind();
            purgeMaterial0.selectedProperty().removeListener(purgeMaterial0Listener);
            purgeMaterial1.selectedProperty().removeListener(purgeMaterial1Listener);
            printer.effectiveFilamentsProperty().removeListener(effectiveFilamentListener);
        }

        this.printer = printer;

        printer.effectiveFilamentsProperty().addListener(effectiveFilamentListener);

        purgeTwoNozzleHeaters = Bindings.size(
                printer.headProperty().get().getNozzleHeaters()).greaterThan(1);

        textCurrentMaterial0.visibleProperty().bind(Bindings.valueAt(printer.effectiveFilamentsProperty(), 0).isNotNull());
        textCurrentMaterial1.visibleProperty().bind(Bindings.valueAt(printer.effectiveFilamentsProperty(), 1).isNotNull());
    }

    /**
     * If a reel is loaded then show and select its material, else show and
     * select the material from the combo box. This should be called whenever a
     * reel is loaded/unloaded or changed.
     */
    private void showCurrentMaterial0()
    {
        currentMaterial0 = printer.effectiveFilamentsProperty().get(0);

        if (currentMaterial0 != null
                && currentMaterial0 != FilamentContainer.UNKNOWN_FILAMENT)
        {
            selectMaterial0(currentMaterial0);
        } else
        {
            if (printer.headProperty().get() != null
                    && printer.headProperty().get().headTypeProperty().get() == Head.HeadType.DUAL_MATERIAL_HEAD)
            {
                transitionManager.setPurgeTemperature(1, 0);
            } else
            {
                transitionManager.setPurgeTemperature(0, 0);
            }
            purgeTemperature0.textProperty().set("0");
            textCurrentMaterial0.textProperty().set("Unknown");
        }
    }

    /**
     * If a reel is loaded then show and select its material, else show and
     * select the material from the combo box. This should be called whenever a
     * reel is loaded/unloaded or changed.
     */
    private void showCurrentMaterial1()
    {
        if (!purgeTwoNozzleHeaters.get())
        {
            return;
        }

        currentMaterial1 = printer.effectiveFilamentsProperty().get(1);

        if (currentMaterial1 != null
                && currentMaterial1 != FilamentContainer.UNKNOWN_FILAMENT)
        {
            selectMaterial1(currentMaterial1);
        } else
        {
            transitionManager.setPurgeTemperature(0, 0);
            purgeTemperature1.textProperty().set("0");
            textCurrentMaterial1.textProperty().set("Unknown");
        }
    }

    private boolean headHasTwoNozzleHeaters(Printer printer)
    {
        return printer.headProperty().get().getNozzleHeaters().size() == 2;
    }

    private void installTagAndDisabledStatusForButton(PurgeStateTransitionManager transitionManager,
            Printer printer, GraphicButtonWithLabel button)
    {
        BooleanBinding doorIsOpen = printer.getPrinterAncillarySystems().doorOpenProperty().and(
                Lookup.getUserPreferences().safetyFeaturesOnProperty());

        BooleanBinding extruder0NotLoaded = printer.extrudersProperty().get(0).
                filamentLoadedProperty().not();

        BooleanBinding notPurgingAndNotIdle = Bindings.and(
                printer.printerStatusProperty().isNotEqualTo(PrinterStatus.PURGING_HEAD),
                printer.printerStatusProperty().isNotEqualTo(PrinterStatus.IDLE));

        cantPurgeDoorIsOpenNotificationBar.setAppearanceCondition(doorIsOpen
                .and(applicationStatus.modeProperty().isEqualTo(ApplicationMode.PURGE)));

        
        if (!headHasTwoNozzleHeaters(printer))
        {
            cantPrintNoFilamentNotificationBar.setAppearanceCondition(applicationStatus.modeProperty().isEqualTo(ApplicationMode.PURGE)
                    .and(extruder0NotLoaded));

            cantPrintFilamentNotSpecifiedNotificationBar.setAppearanceCondition(applicationStatus.modeProperty().isEqualTo(ApplicationMode.PURGE)
                    .and(Bindings.valueAt(printer.effectiveFilamentsProperty(), 0).isNull()
                            .or(Bindings.valueAt(printer.effectiveFilamentsProperty(), 0).isEqualTo(FilamentContainer.UNKNOWN_FILAMENT))));

            BooleanBinding isDisabled = notPurgingAndNotIdle.or(doorIsOpen).or(extruder0NotLoaded);
            button.disableProperty().bind(isDisabled);
        }
        else
        {

            BooleanBinding extruder1NotLoaded = printer.extrudersProperty().get(1).
                    filamentLoadedProperty().not();

            if (button == proceedButton)
            {
                BooleanBinding purgingNozzleHeater0 = new BooleanBinding()
                {
                    {
                        super.bind(transitionManager.getPurgeNozzleHeater0());
                    }

                    @Override
                    protected boolean computeValue()
                    {
                        return transitionManager.getPurgeNozzleHeater0().get();
                    }
                };

                BooleanBinding purgingNozzleHeater1 = new BooleanBinding()
                {
                    {
                        super.bind(transitionManager.getPurgeNozzleHeater1());
                    }

                    @Override
                    protected boolean computeValue()
                    {
                        return transitionManager.getPurgeNozzleHeater1().get();
                    }
                };
                
                cantPrintNoFilament0NotificationBar.setAppearanceCondition(applicationStatus.modeProperty().isEqualTo(ApplicationMode.PURGE)
                        .and(purgingNozzleHeater1.and(extruder0NotLoaded)));

                cantPrintNoFilament1NotificationBar.setAppearanceCondition(applicationStatus.modeProperty().isEqualTo(ApplicationMode.PURGE)
                        .and(purgingNozzleHeater0.and(extruder1NotLoaded)));

                cantPrintFilament0NotSpecifiedNotificationBar.setAppearanceCondition(applicationStatus.modeProperty().isEqualTo(ApplicationMode.PURGE)
                        .and(purgingNozzleHeater1.and(Bindings.valueAt(printer.effectiveFilamentsProperty(), 0).isNull()
                                        .or(Bindings.valueAt(printer.effectiveFilamentsProperty(), 0).isEqualTo(FilamentContainer.UNKNOWN_FILAMENT)))));

                cantPrintFilament1NotSpecifiedNotificationBar.setAppearanceCondition(applicationStatus.modeProperty().isEqualTo(ApplicationMode.PURGE)
                        .and(purgingNozzleHeater0.and(Bindings.valueAt(printer.effectiveFilamentsProperty(), 1).isNull()
                                        .or(Bindings.valueAt(printer.effectiveFilamentsProperty(), 1).isEqualTo(FilamentContainer.UNKNOWN_FILAMENT)))));

                BooleanBinding filament0Invalid = Bindings.valueAt(printer.effectiveFilamentsProperty(), 0).isNull()
                                                .or(Bindings.valueAt(printer.effectiveFilamentsProperty(), 0).isEqualTo(FilamentContainer.UNKNOWN_FILAMENT));
                BooleanBinding filament1Invalid = Bindings.valueAt(printer.effectiveFilamentsProperty(), 1).isNull()
                                        .or(Bindings.valueAt(printer.effectiveFilamentsProperty(), 1).isEqualTo(FilamentContainer.UNKNOWN_FILAMENT));

                BooleanBinding isDisabled = notPurgingAndNotIdle
                        .or(doorIsOpen)
                        .or(purgingNozzleHeater0.not().and(purgingNozzleHeater1.not()))
                        .or(purgingNozzleHeater1.and(extruder0NotLoaded.or(filament0Invalid)))
                        .or(purgingNozzleHeater0.and(extruder1NotLoaded.or(filament1Invalid)));
                button.disableProperty().bind(isDisabled);
            }
            else
            {
                BooleanBinding neitherExtruderLoaded = extruder0NotLoaded.and(extruder1NotLoaded);
                BooleanBinding isDisabled = notPurgingAndNotIdle.or(doorIsOpen).or(neitherExtruderLoaded);
                button.disableProperty().bind(isDisabled);
            }
        }
    }

    /**
     * This is called when the user wants to print and the system has detected
     * that a purge is required.
     */
    public void purgeAndPrint(ModelContainerProject project, Printer printerToUse)
    {
        this.project = project;
        bindPrinter(printerToUse);
        selectMaterial0(printer.effectiveFilamentsProperty().get(0));
        selectMaterial1(printer.effectiveFilamentsProperty().get(1));

        setPurgeForRequiredNozzles();

        startPurge();
    }

    /**
     * If a nozzle needs purging then set the appropriate flag to true.
     */
    private void setPurgeForRequiredNozzles()
    {
        if (!headHasTwoNozzleHeaters(printer))
        {
            purgeMaterial0.setSelected(true);
            purgeMaterial1.setSelected(false);
        } else
        {
            purgeMaterial0.setSelected(false);
            purgeMaterial1.setSelected(false);

            if (PrinterUtils.isPurgeNecessaryForExtruder(printer, 0))
            {
                purgeMaterial0.setSelected(true);
            }
            if (PrinterUtils.isPurgeNecessaryForExtruder(printer, 1))
            {
                purgeMaterial1.setSelected(true);
            }
        }
    }

    public void purge(Printer printer)
    {
        bindPrinter(printer);

        // default to purging both nozzles.
        purgeMaterial0.setSelected(true);
        purgeMaterial1.setSelected(true);

        startPurge();
    }

    private void startPurge()
    {
        ApplicationStatus.getInstance().setMode(ApplicationMode.PURGE);

        try
        {
            transitionManager = printer.startPurge(Lookup.getUserPreferences().isSafetyFeaturesOn());

            currentMaterialTemperature0.textProperty().unbind();
            lastMaterialTemperature0.textProperty().unbind();

            currentMaterialTemperature1.textProperty().unbind();
            lastMaterialTemperature1.textProperty().unbind();

            purgeMaterial0.selectedProperty().addListener(purgeMaterial0Listener);
            purgeMaterial1.selectedProperty().addListener(purgeMaterial1Listener);

            setPurgeForRequiredNozzles();
            
            if (purgeTwoNozzleHeaters.get())
            {
                currentMaterialTemperature1.textProperty().unbind();
                lastMaterialTemperature1.textProperty().unbind();
                currentMaterialTemperature1.textProperty().bind(
                        transitionManager.getCurrentMaterialTemperature(0).asString());

                lastMaterialTemperature1.textProperty().bind(
                        transitionManager.getLastMaterialTemperature(0).asString());

                currentMaterialTemperature0.textProperty().bind(
                        transitionManager.getCurrentMaterialTemperature(1).asString());

                lastMaterialTemperature0.textProperty().bind(
                        transitionManager.getLastMaterialTemperature(1).asString());
            } else
            {
                currentMaterialTemperature0.textProperty().bind(
                        transitionManager.getCurrentMaterialTemperature(0).asString());

                lastMaterialTemperature0.textProperty().bind(
                        transitionManager.getLastMaterialTemperature(0).asString());
            }

            transitionManager.stateGUITProperty().addListener(new ChangeListener()
            {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue)
                {
                    setState((PurgeState) newValue);
                }
            });

            transitionManager.start();

            
            if (printer.headProperty().get().headTypeProperty().get() == Head.HeadType.SINGLE_MATERIAL_HEAD)
            {
                transitionManager.setPurgeNozzleHeater0(purgeMaterial0.isSelected());
            } else
            {
                transitionManager.setPurgeNozzleHeater0(purgeMaterial1.isSelected());
                transitionManager.setPurgeNozzleHeater1(purgeMaterial0.isSelected());
            }

            installTagAndDisabledStatusForButton(transitionManager, printer, startPurgeButton);
            installTagAndDisabledStatusForButton(transitionManager, printer, proceedButton);

            setState(PurgeState.IDLE);
        } catch (PrinterException ex)
        {
            steno.error("Error starting purge: " + ex);
        }
    }

    /**
     * Tell the purge state machine about the (changed) current material, and
     * update the relevant text fields.
     */
    private void selectMaterial0(Filament filament)
    {
        currentMaterial0 = filament;
        if (transitionManager != null)
        {
            try
            {
                if (printer.headProperty().get() != null
                        && printer.headProperty().get().headTypeProperty().get() == Head.HeadType.DUAL_MATERIAL_HEAD)
                {
                    transitionManager.setPurgeFilament(1, filament);
                    purgeTemperature0.setText(transitionManager.getPurgeTemperature(1).asString().get());
                } else
                {
                    transitionManager.setPurgeFilament(0, filament);
                    purgeTemperature0.setText(transitionManager.getPurgeTemperature(0).asString().get());
                }
            } catch (PrintException ex)
            {
                steno.exception("Error setting purge filament", ex);
            }
            textCurrentMaterial0.setText(currentMaterial0.getLongFriendlyName() + " "
                    + currentMaterial0.getMaterial().getFriendlyName());
        }
    }

    /**
     * Tell the purge state machine about the (changed) current material, and
     * update the relevant text fields.
     */
    private void selectMaterial1(Filament filament)
    {
        currentMaterial1 = filament;
        if (transitionManager != null)
        {
            try
            {
                transitionManager.setPurgeFilament(0, filament);
            } catch (PrintException ex)
            {
                steno.exception("Error setting purge filament", ex);
            }
            if (currentMaterial1.getMaterial() != null)
            {
                textCurrentMaterial1.setText(currentMaterial1.getLongFriendlyName() + " "
                        + currentMaterial1.getMaterial().getFriendlyName());
            } else
            {
                textCurrentMaterial1.setText(currentMaterial1.getLongFriendlyName());
            }
            purgeTemperature1.setText(transitionManager.getPurgeTemperature(0).asString().get());
        }
    }

    public ReadOnlyObjectProperty<PurgeState> purgeStateProperty()
    {
        return transitionManager.stateGUITProperty();
    }
}
