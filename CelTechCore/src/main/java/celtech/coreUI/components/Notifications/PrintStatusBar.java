/*
 * Copyright 2014 CEL UK
 */
package celtech.coreUI.components.Notifications;

import celtech.Lookup;
import celtech.roboxbase.comms.remote.BusyStatus;
import celtech.roboxbase.comms.remote.PauseStatus;
import celtech.roboxbase.configuration.Macro;
import celtech.roboxbase.printerControl.PrintQueueStatus;
import celtech.roboxbase.printerControl.PrinterStatus;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.printerControl.model.PrinterException;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;

/**
 *
 * @author tony
 */
public class PrintStatusBar extends AppearingProgressBar implements Initializable
{

    private Printer printer = null;

    private final ChangeListener<Number> printerNumberElementListener = (ObservableValue<? extends Number> ov, Number lastState, Number newState) ->
    {
        reassessStatus();
    };

    private final ChangeListener<PrinterStatus> printerStatusChangeListener = (ObservableValue<? extends PrinterStatus> ov, PrinterStatus lastState, PrinterStatus newState) ->
    {
        reassessStatus();
    };

    private final ChangeListener<PrintQueueStatus> printQueueStatusChangeListener = (ObservableValue<? extends PrintQueueStatus> ov, PrintQueueStatus lastState, PrintQueueStatus newState) ->
    {
        reassessStatus();
    };

    private final ChangeListener<PauseStatus> pauseStatusChangeListener = (ObservableValue<? extends PauseStatus> ov, PauseStatus lastState, PauseStatus newState) ->
    {
        reassessStatus();
    };

    private final ChangeListener<BusyStatus> busyStatusChangeListener = (ObservableValue<? extends BusyStatus> ov, BusyStatus lastState, BusyStatus newState) ->
    {
        reassessStatus();
    };

    private final EventHandler<ActionEvent> pauseEventHandler = (ActionEvent t) ->
    {
        try
        {
            printer.pause();
        } catch (PrinterException ex)
        {
            System.out.println("Couldn't pause printer");
        }
    };

    private final EventHandler<ActionEvent> resumeEventHandler = (ActionEvent t) ->
    {
        try
        {
            printer.resume();
        } catch (PrinterException ex)
        {
            System.out.println("Couldn't resume print");
        }
    };

    private final EventHandler<ActionEvent> cancelEventHandler = (ActionEvent t) ->
    {
        try
        {
            printer.cancel(null, Lookup.getUserPreferences().isSafetyFeaturesOn());
        } catch (PrinterException ex)
        {
            System.out.println("Couldn't resume print");
        }
    };

    private final BooleanProperty buttonsAllowed = new SimpleBooleanProperty(false);

    public PrintStatusBar()
    {
        super();
    }

    private void reassessStatus()
    {
        boolean statusProcessed = false;
        boolean barShouldBeDisplayed = false;

        //Now busy status
        switch (printer.busyStatusProperty().get())
        {
            case NOT_BUSY:
                break;
            case BUSY:
                break;
            case LOADING_FILAMENT_E:
            case UNLOADING_FILAMENT_E:
            case LOADING_FILAMENT_D:
            case UNLOADING_FILAMENT_D:
                statusProcessed = true;
                barShouldBeDisplayed = true;
                largeProgressDescription.setText(Lookup.i18n(printer.busyStatusProperty().get().getI18nString()));
                progressRequired(false);
                targetLegendRequired(false);
                targetValueRequired(false);
                currentValueRequired(false);
                layerDataRequired(false);
                buttonsAllowed.set(false);
                break;
            default:
                break;
        }

        //Pause status takes precedence
        if (!statusProcessed)
        {
            switch (printer.pauseStatusProperty().get())
            {
                case NOT_PAUSED:
                    break;
                case PAUSED:
                case PAUSE_PENDING:
                case RESUME_PENDING:
                case SELFIE_PAUSE:
                    statusProcessed = true;
                    barShouldBeDisplayed = true;
                    largeProgressDescription.setText(Lookup.i18n(printer.pauseStatusProperty().get().getI18nString()));
                    progressRequired(false);
                    targetLegendRequired(false);
                    targetValueRequired(false);
                    currentValueRequired(false);
                    layerDataRequired(false);
                    buttonsAllowed.set(true);
                    break;
                default:
                    break;
            }
        }

        //Now print status
        if (!statusProcessed)
        {
            switch (printer.printerStatusProperty().get())
            {
                case IDLE:
                    break;
                case PRINTING_PROJECT:
                    statusProcessed = true;
                    barShouldBeDisplayed = true;
                    largeProgressDescription.setText(printer.printerStatusProperty().get().getI18nString());

                    if (printer.getPrintEngine().etcAvailableProperty().get())
                    {
                        int secondsRemaining = printer.getPrintEngine().progressETCProperty().intValue();
                        secondsRemaining += 30;
                        if (secondsRemaining > 60)
                        {
                            String hoursMinutes = convertToHoursMinutes(
                                    secondsRemaining);
                            currentValue.setText(hoursMinutes);
                        } else
                        {
                            currentValue.setText(Lookup.i18n("dialogs.lessThanOneMinute"));
                        }

                        largeTargetLegend.setText(Lookup.i18n("dialogs.progressETCLabel"));
                        
                        layerN.setText(String.format("%d",printer.getPrintEngine().progressCurrentLayerProperty().get()));
                        layerTotal.setText(String.format("%d",printer.getPrintEngine().progressNumLayersProperty().get()));
                        targetLegendRequired(true);
                        targetValueRequired(false);
                        currentValueRequired(true);
                        layerDataRequired(true);
                    } else
                    {
                        targetLegendRequired(false);
                        targetValueRequired(false);
                        currentValueRequired(false);
                        layerDataRequired(false);
                    }

                    if (progressBar.progressProperty().isBound())
                    {
                        progressBar.progressProperty().unbind();
                    }

                    progressBar.progressProperty().bind(printer.getPrintEngine().progressProperty());
                    progressRequired(true);
                    buttonsAllowed.set(true);
                    break;
                case RUNNING_MACRO_FILE:
                    statusProcessed = true;
                    barShouldBeDisplayed = true;
                    largeProgressDescription.setText(printer.getPrintEngine().macroBeingRun.get().getFriendlyName());

                    targetLegendRequired(false);
                    targetValueRequired(false);
                    currentValueRequired(false);
                    layerDataRequired(false);

                    if (progressBar.progressProperty().isBound())
                    {
                        progressBar.progressProperty().unbind();
                    }

                    if (printer.getPrintEngine().macroBeingRun.get() != Macro.CANCEL_PRINT)
                    {
                        if (printer.getPrintEngine().linesInPrintingFileProperty().get() > 0)
                        {
                            progressBar.progressProperty().bind(printer.getPrintEngine().progressProperty());
                            progressRequired(true);
                        } else
                        {
                            progressRequired(false);
                        }
                    } else
                    {
                        progressRequired(false);
                    }
                    buttonsAllowed.set(true);
                    break;
                case CALIBRATING_NOZZLE_ALIGNMENT:
                    statusProcessed = true;
                    barShouldBeDisplayed = true;
                    largeProgressDescription.setText(printer.printerStatusProperty().get().getI18nString());

                    targetLegendRequired(false);
                    targetValueRequired(false);
                    currentValueRequired(false);
                    layerDataRequired(false);

                    if (progressBar.progressProperty().isBound())
                    {
                        progressBar.progressProperty().unbind();
                    }

                    if (printer.getPrintEngine().macroBeingRun.get() != Macro.CANCEL_PRINT)
                    {
                        if (printer.getPrintEngine().linesInPrintingFileProperty().get() > 0)
                        {
                            progressBar.progressProperty().bind(printer.getPrintEngine().progressProperty());
                            progressRequired(true);
                        } else
                        {
                            progressRequired(false);
                        }
                    } else
                    {
                        progressRequired(false);
                    }
                    buttonsAllowed.set(false);
                    break;
                case PURGING_HEAD:
                    statusProcessed = true;
                    barShouldBeDisplayed = true;
                    largeProgressDescription.setText(printer.printerStatusProperty().get().getI18nString());

                    targetLegendRequired(false);
                    targetValueRequired(false);
                    currentValueRequired(false);
                    layerDataRequired(false);

                    if (progressBar.progressProperty().isBound())
                    {
                        progressBar.progressProperty().unbind();
                    }

                    if (printer.getPrintEngine().macroBeingRun.get() != Macro.CANCEL_PRINT)
                    {
                        if (printer.getPrintEngine().linesInPrintingFileProperty().get() > 0)
                        {
                            progressBar.progressProperty().bind(printer.getPrintEngine().progressProperty());
                            progressRequired(true);
                        } else
                        {
                            progressRequired(false);
                        }
                    } else
                    {
                        progressRequired(false);
                    }
                    buttonsAllowed.set(false);
                    break;
                default:
                    statusProcessed = true;
                    barShouldBeDisplayed = true;
                    targetLegendRequired(false);
                    targetValueRequired(false);
                    currentValueRequired(false);
                    progressRequired(false);
                    layerDataRequired(false);
                    largeProgressDescription.setText(printer.printerStatusProperty().get().getI18nString());
                    buttonsAllowed.set(false);
                    break;
            }
        }

        if (barShouldBeDisplayed)
        {
            startSlidingInToView();
        } else
        {
            startSlidingOutOfView();
        }
    }

    private String convertToHoursMinutes(int seconds)
    {
        int minutes = (int) (seconds / 60);
        int hours = minutes / 60;
        minutes = minutes - (60 * hours);
        return String.format("%02d:%02d", hours, minutes);
    }

    public void bindToPrinter(Printer printer) {
        this.printer = printer;

        printer.printerStatusProperty().addListener(printerStatusChangeListener);
        printer.pauseStatusProperty().addListener(pauseStatusChangeListener);
        printer.busyStatusProperty().addListener(busyStatusChangeListener);
        printer.getPrintEngine().printQueueStatusProperty().addListener(printQueueStatusChangeListener);
        printer.getPrintEngine().progressETCProperty().addListener(printerNumberElementListener);
        printer.getPrintEngine().progressProperty().addListener(printerNumberElementListener);
        printer.getPrintEngine().progressNumLayersProperty().addListener(printerNumberElementListener);

        pauseButton.visibleProperty().bind(printer.canPauseProperty().and(buttonsAllowed));
        pauseButton.setOnAction(pauseEventHandler);
        resumeButton.visibleProperty().bind(printer.canResumeProperty().and(buttonsAllowed));
        resumeButton.setOnAction(resumeEventHandler);
        cancelButton.visibleProperty().bind(printer.canCancelProperty().and(buttonsAllowed));
        cancelButton.setOnAction(cancelEventHandler);

        reassessStatus();
    }
    
    public void unbindAll()
    {
        if (printer != null)
        {
            printer.printerStatusProperty().removeListener(printerStatusChangeListener);
            printer.pauseStatusProperty().removeListener(pauseStatusChangeListener);
            printer.busyStatusProperty().removeListener(busyStatusChangeListener);
            printer.getPrintEngine().printQueueStatusProperty().removeListener(printQueueStatusChangeListener);
            printer.getPrintEngine().progressETCProperty().removeListener(printerNumberElementListener);
            printer.getPrintEngine().progressProperty().removeListener(printerNumberElementListener);
            printer.getPrintEngine().progressNumLayersProperty().removeListener(printerNumberElementListener);
            pauseButton.visibleProperty().unbind();
            pauseButton.setOnAction(null);
            resumeButton.visibleProperty().unbind();
            resumeButton.setOnAction(null);
            cancelButton.visibleProperty().unbind();
            cancelButton.setOnAction(null);
            // Hide the bar if it is currently shown.
            startSlidingOutOfView();
            printer = null;
        }
    }
}
