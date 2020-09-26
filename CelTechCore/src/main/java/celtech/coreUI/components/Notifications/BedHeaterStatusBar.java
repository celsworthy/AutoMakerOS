/*
 * Copyright 2014 CEL UK
 */
package celtech.coreUI.components.Notifications;

import celtech.Lookup;
import celtech.roboxbase.printerControl.model.HeaterMode;
import celtech.roboxbase.printerControl.model.PrinterAncillarySystems;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;

/**
 *
 * @author tony
 */
public class BedHeaterStatusBar extends AppearingProgressBar implements Initializable
{

    private ReadOnlyObjectProperty<HeaterMode> heaterMode;
    private ReadOnlyIntegerProperty bedTemperature;
    private ReadOnlyIntegerProperty bedTargetTemperature;
    private ReadOnlyIntegerProperty bedFirstLayerTargetTemperature;

    private static final double showBarIfMoreThanXDegreesOut = 5;

    private final ChangeListener<Number> numberChangeListener = (ObservableValue<? extends Number> ov, Number lastState, Number newState) ->
    {
        reassessStatus();
    };

    private final ChangeListener<HeaterMode> heaterModeChangeListener = (ObservableValue<? extends HeaterMode> ov, HeaterMode lastState, HeaterMode newState) ->
    {
        reassessStatus();
    };

    public BedHeaterStatusBar()
    {
        super();
        
        getStyleClass().add("secondaryStatusBar");

        setPickOnBounds(false);
        setMouseTransparent(true);
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

        switch (heaterMode.get())
        {
            case OFF:
                break;
            case FIRST_LAYER:
                if (Math.abs(bedTemperature.get() - bedFirstLayerTargetTemperature.get())
                        > showBarIfMoreThanXDegreesOut)
                {
                    largeProgressDescription.setText(Lookup.i18n("printerStatus.heatingBed"));

                    largeTargetLegend.textProperty().set(Lookup.i18n("progressBar.targetTemperature"));
                    largeTargetValue.textProperty().set(bedFirstLayerTargetTemperature.asString("%d").get()
                            .concat(Lookup.i18n("misc.degreesC")));
                    currentValue.textProperty().set(bedTemperature.asString("%d").get()
                            .concat(Lookup.i18n("misc.degreesC")));

                    if (bedFirstLayerTargetTemperature.doubleValue() > 0)
                    {
                        double normalisedProgress = 0;
                        normalisedProgress = bedTemperature.doubleValue() / bedFirstLayerTargetTemperature.doubleValue();
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
                if (Math.abs(bedTemperature.get() - bedTargetTemperature.get())
                        > showBarIfMoreThanXDegreesOut)
                {
                    largeProgressDescription.setText(Lookup.i18n("printerStatus.heatingBed"));

                    largeTargetLegend.textProperty().set(Lookup.i18n("progressBar.targetTemperature"));
                    largeTargetValue.textProperty().set(bedTargetTemperature.asString("%d").get()
                            .concat(Lookup.i18n("misc.degreesC")));
                    currentValue.textProperty().set(bedTemperature.asString("%d").get()
                            .concat(Lookup.i18n("misc.degreesC")));

                    if (bedTargetTemperature.doubleValue() > 0)
                    {
                        double normalisedProgress = 0;
                        normalisedProgress = bedTemperature.doubleValue() / bedTargetTemperature.doubleValue();
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

    public void bindToPrinterSystems(PrinterAncillarySystems printerSystems) {
        this.heaterMode = printerSystems.bedHeaterModeProperty();
        this.bedTemperature = printerSystems.bedTemperatureProperty();
        this.bedFirstLayerTargetTemperature = printerSystems.bedFirstLayerTargetTemperatureProperty();
        this.bedTargetTemperature = printerSystems.bedTargetTemperatureProperty();

        this.bedTemperature.addListener(numberChangeListener);
        this.bedFirstLayerTargetTemperature.addListener(numberChangeListener);
        this.bedTargetTemperature.addListener(numberChangeListener);
        this.heaterMode.addListener(heaterModeChangeListener);
        
        reassessStatus();
    }
    
    public void unbindAll()
    {
        if (heaterMode != null)
        {
            heaterMode.removeListener(heaterModeChangeListener);
            heaterMode = null;
        }

        if (bedTemperature != null)
        {
            bedTemperature.removeListener(numberChangeListener);
            bedTemperature = null;
        }

        if (bedFirstLayerTargetTemperature != null)
        {
            bedFirstLayerTargetTemperature.removeListener(numberChangeListener);
            bedFirstLayerTargetTemperature = null;
        }

        if (bedTargetTemperature != null)
        {
            bedTargetTemperature.removeListener(numberChangeListener);
            bedTargetTemperature = null;
        }
        // Hide the bar if it is currently shown.
        startSlidingOutOfView();
   }
}
