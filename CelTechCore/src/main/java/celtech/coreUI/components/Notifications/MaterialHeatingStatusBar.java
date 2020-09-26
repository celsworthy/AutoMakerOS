/*
 * Copyright 2014 CEL UK
 */
package celtech.coreUI.components.Notifications;

import celtech.Lookup;
import celtech.roboxbase.printerControl.model.HeaterMode;
import celtech.roboxbase.printerControl.model.NozzleHeater;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;

/**
 *
 * @author tony
 */
public class MaterialHeatingStatusBar extends AppearingProgressBar implements Initializable
{

    private static final double EJECT_TEMPERATURE = 140.0;

    private NozzleHeater heater = null;
    private final int materialNumber;
    private final boolean thisIsTheOnlyNozzle;
    private static final double showBarIfMoreThanXDegreesOut = 3;

    private ChangeListener<Number> numberChangeListener = (ObservableValue<? extends Number> ov, Number lastState, Number newState) ->
    {
        reassessStatus();
    };

    private ChangeListener<HeaterMode> heaterModeChangeListener = (ObservableValue<? extends HeaterMode> ov, HeaterMode lastState, HeaterMode newState) ->
    {
        reassessStatus();
    };

    public MaterialHeatingStatusBar(NozzleHeater heater, int materialNumber, boolean thisIsTheOnlyNozzle)
    {
        super();
        this.heater = heater;
        this.materialNumber = materialNumber;
        this.thisIsTheOnlyNozzle = thisIsTheOnlyNozzle;

        heater.nozzleTemperatureProperty().addListener(numberChangeListener);
        heater.nozzleTargetTemperatureProperty().addListener(numberChangeListener);
        heater.nozzleFirstLayerTargetTemperatureProperty().addListener(numberChangeListener);
        heater.heaterModeProperty().addListener(heaterModeChangeListener);

        getStyleClass().add("secondaryStatusBar");

        setPickOnBounds(false);
        setMouseTransparent(true);

        reassessStatus();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        super.initialize(location, resources);
        targetLegendRequired(true);
        targetValueRequired(true);
        currentValueRequired(true);
        progressRequired(true);
        layerDataRequired(false);
    }

    private void reassessStatus()
    {
        boolean showHeaterBar = false;

        switch (heater.heaterModeProperty().get())
        {
            case OFF:
                break;
            case FIRST_LAYER:
                if (Math.abs(heater.nozzleTemperatureProperty().get() - heater.nozzleFirstLayerTargetTemperatureProperty().get())
                        > showBarIfMoreThanXDegreesOut)
                {
                    if (heater.nozzleFirstLayerTargetTemperatureProperty().get() > heater.nozzleTemperatureProperty().get())
                    {
                        if (thisIsTheOnlyNozzle)
                        {
                            largeProgressDescription.setText(Lookup.i18n("printerStatus.heatingNozzle"));
                        } else
                        {
                            switch (materialNumber)
                            {
                                case 1:
                                    largeProgressDescription.setText(Lookup.i18n("printerStatus.heating") + " " + Lookup.i18n("printerStatus.Material1Label"));
                                    break;
                                case 2:
                                    largeProgressDescription.setText(Lookup.i18n("printerStatus.heating") + " " + Lookup.i18n("printerStatus.Material2Label"));
                                    break;
                            }
                        }
                    } else
                    {
                        if (thisIsTheOnlyNozzle)
                        {
                            largeProgressDescription.setText(Lookup.i18n("printerStatus.coolingNozzle"));
                        } else
                        {
                            switch (materialNumber)
                            {
                                case 1:
                                    largeProgressDescription.setText(Lookup.i18n("printerStatus.cooling") + " " + Lookup.i18n("printerStatus.Material1Label"));
                                    break;
                                case 2:
                                    largeProgressDescription.setText(Lookup.i18n("printerStatus.cooling") + " " + Lookup.i18n("printerStatus.Material2Label"));
                                    break;
                            }
                        }
                    }

                    largeTargetLegend.textProperty().set(Lookup.i18n("progressBar.targetTemperature"));
                    largeTargetValue.textProperty().set(heater.nozzleFirstLayerTargetTemperatureProperty().asString("%d").get()
                            .concat(Lookup.i18n("misc.degreesC")));
                    currentValue.textProperty().set(heater.nozzleTemperatureProperty().asString("%d").get()
                            .concat(Lookup.i18n("misc.degreesC")));

                    if (heater.nozzleFirstLayerTargetTemperatureProperty().doubleValue() > 0)
                    {
                        double normalisedProgress = 0;
                        normalisedProgress = heater.nozzleTemperatureProperty().doubleValue() / heater.nozzleFirstLayerTargetTemperatureProperty().doubleValue();
                        normalisedProgress = Math.max(0, normalisedProgress);
                        normalisedProgress = Math.min(1, normalisedProgress);

                        progressBar.setProgress(normalisedProgress);
                    } else
                    {
                        progressBar.setProgress(0);
                    }
                    showHeaterBar = true;
                }
                break;

            case NORMAL:
                if (Math.abs(heater.nozzleTemperatureProperty().get() - heater.nozzleTargetTemperatureProperty().get())
                        > showBarIfMoreThanXDegreesOut)
                {
                    if (heater.nozzleTargetTemperatureProperty().get() > heater.nozzleTemperatureProperty().get())
                    {
                        if (thisIsTheOnlyNozzle)
                        {
                            largeProgressDescription.setText(Lookup.i18n("printerStatus.heatingNozzle"));
                        } else
                        {
                            switch (materialNumber)
                            {
                                case 1:
                                    largeProgressDescription.setText(Lookup.i18n("printerStatus.heating") + " " + Lookup.i18n("printerStatus.Material1Label"));
                                    break;
                                case 2:
                                    largeProgressDescription.setText(Lookup.i18n("printerStatus.heating") + " " + Lookup.i18n("printerStatus.Material2Label"));
                                    break;
                            }
                        }
                    } else
                    {
                        if (thisIsTheOnlyNozzle)
                        {
                            largeProgressDescription.setText(Lookup.i18n("printerStatus.coolingNozzle"));
                        } else
                        {
                            switch (materialNumber)
                            {
                                case 1:
                                    largeProgressDescription.setText(Lookup.i18n("printerStatus.cooling") + " " + Lookup.i18n("printerStatus.Material1Label"));
                                    break;
                                case 2:
                                    largeProgressDescription.setText(Lookup.i18n("printerStatus.cooling") + " " + Lookup.i18n("printerStatus.Material2Label"));
                                    break;
                            }
                        }
                    }

                    largeTargetLegend.textProperty().set(Lookup.i18n("progressBar.targetTemperature"));
                    largeTargetValue.textProperty().set(heater.nozzleTargetTemperatureProperty().asString("%d").get()
                            .concat(Lookup.i18n("misc.degreesC")));
                    currentValue.textProperty().set(heater.nozzleTemperatureProperty().asString("%d").get()
                            .concat(Lookup.i18n("misc.degreesC")));

                    if (heater.nozzleFirstLayerTargetTemperatureProperty().doubleValue() > 0)
                    {
                        double normalisedProgress = 0;
                        normalisedProgress = heater.nozzleTemperatureProperty().doubleValue() / heater.nozzleTargetTemperatureProperty().doubleValue();
                        normalisedProgress = Math.max(0, normalisedProgress);
                        normalisedProgress = Math.min(1, normalisedProgress);

                        progressBar.setProgress(normalisedProgress);
                    } else
                    {
                        progressBar.setProgress(0);
                    }
                    showHeaterBar = true;
                }
                break;
            case FILAMENT_EJECT:
                if (Math.abs(heater.nozzleTemperatureProperty().get() - EJECT_TEMPERATURE)
                        > showBarIfMoreThanXDegreesOut)
                {
                    if (thisIsTheOnlyNozzle)
                    {
                        largeProgressDescription.setText(Lookup.i18n("printerStatus.heatingNozzle"));
                    } else
                    {
                        switch (materialNumber)
                        {
                            case 1:
                                largeProgressDescription.setText(Lookup.i18n("printerStatus.heating") + " " + Lookup.i18n("printerStatus.Material1Label"));
                                break;
                            case 2:
                                largeProgressDescription.setText(Lookup.i18n("printerStatus.heating") + " " + Lookup.i18n("printerStatus.Material2Label"));
                                break;
                        }
                    }

                    largeTargetLegend.textProperty().set(Lookup.i18n("progressBar.targetTemperature"));
                    largeTargetValue.textProperty().set(String.format("%.0f", EJECT_TEMPERATURE)
                            + Lookup.i18n("misc.degreesC"));
                    currentValue.textProperty().set(heater.nozzleTemperatureProperty().asString("%d").get()
                            .concat(Lookup.i18n("misc.degreesC")));

                    double normalisedProgress = 0;
                    normalisedProgress = heater.nozzleTemperatureProperty().doubleValue() / EJECT_TEMPERATURE;
                    normalisedProgress = Math.max(0, normalisedProgress);
                    normalisedProgress = Math.min(1, normalisedProgress);

                    progressBar.setProgress(normalisedProgress);

                    showHeaterBar = true;
                }
                break;
            default:
                break;
        }

        if (showHeaterBar)
        {
            startSlidingInToView();
        } else
        {
            startSlidingOutOfView();
        }
    }

    public void unbindAll()
    {
        if (heater != null)
        {
            heater.nozzleTemperatureProperty().removeListener(numberChangeListener);
            heater.nozzleTargetTemperatureProperty().removeListener(numberChangeListener);
            heater.nozzleFirstLayerTargetTemperatureProperty().removeListener(numberChangeListener);
            heater.heaterModeProperty().removeListener(heaterModeChangeListener);
            heater = null;
        }
        // Hide the bar if it is currently shown.
        startSlidingOutOfView();
    }
}
