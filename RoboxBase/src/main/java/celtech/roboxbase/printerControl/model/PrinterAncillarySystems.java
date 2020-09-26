package celtech.roboxbase.printerControl.model;

import celtech.roboxbase.comms.remote.WhyAreWeWaitingState;
import celtech.roboxbase.configuration.BaseConfiguration;
import java.util.ArrayList;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyFloatProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

/**
 *
 * @author Ian
 */
public class PrinterAncillarySystems
{

    protected final BooleanProperty XStopSwitch = new SimpleBooleanProperty(false);
    protected final BooleanProperty YStopSwitch = new SimpleBooleanProperty(false);
    protected final BooleanProperty ZStopSwitch = new SimpleBooleanProperty(false);
    protected final BooleanProperty ZTopStopSwitch = new SimpleBooleanProperty(false);
    protected final BooleanProperty reelButton = new SimpleBooleanProperty(false);
    protected final BooleanProperty headFanOn = new SimpleBooleanProperty(false);
    protected final BooleanProperty ambientFanOn = new SimpleBooleanProperty(false);
    protected final BooleanProperty bAxisHome = new SimpleBooleanProperty(false);
    protected final BooleanProperty doorOpen = new SimpleBooleanProperty(false);
    protected final BooleanProperty dualReelAdaptorPresent = new SimpleBooleanProperty(false);
    protected final FloatProperty feedRateEMultiplier = new SimpleFloatProperty(0);
    protected final FloatProperty feedRateDMultiplier = new SimpleFloatProperty(0);
    protected final FloatProperty hoursCounter = new SimpleFloatProperty(0);
    protected final BooleanProperty sdCardInserted = new SimpleBooleanProperty(false);

    protected final ObjectProperty<HeaterMode> bedHeaterMode = new SimpleObjectProperty<>(HeaterMode.OFF);
    protected final IntegerProperty ambientTemperature = new SimpleIntegerProperty(0);
    protected final IntegerProperty ambientTargetTemperature = new SimpleIntegerProperty(0);
    protected final IntegerProperty bedTemperature = new SimpleIntegerProperty(0);
    protected final IntegerProperty bedFirstLayerTargetTemperature = new SimpleIntegerProperty(0);
    protected final IntegerProperty bedTargetTemperature = new SimpleIntegerProperty(0);
    private final LineChart.Series<Number, Number> ambientTemperatureHistory = new LineChart.Series<>();
    private final ArrayList<LineChart.Data<Number, Number>> ambientTemperatureDataPoints = new ArrayList<>();
    private final LineChart.Series<Number, Number> bedTemperatureHistory = new LineChart.Series<>();
    private final ArrayList<LineChart.Data<Number, Number>> bedTemperatureDataPoints = new ArrayList<>();
    private final LineChart.Series<Number, Number> ambientTargetTemperatureSeries = new LineChart.Series<>();
    private final LineChart.Series<Number, Number> bedTargetTemperatureSeries = new LineChart.Series<>();
    private final LineChart.Data<Number, Number> ambientTargetPoint = new LineChart.Data<>(
        BaseConfiguration.NUMBER_OF_TEMPERATURE_POINTS_TO_KEEP + 5, 0);
    private final LineChart.Data<Number, Number> bedTargetPoint = new LineChart.Data<>(
        BaseConfiguration.NUMBER_OF_TEMPERATURE_POINTS_TO_KEEP + 5, 0);
    private long lastTemperatureTimestamp = 0;

    protected final ObjectProperty<WhyAreWeWaitingState> whyAreWeWaitingProperty = new SimpleObjectProperty<>(WhyAreWeWaitingState.NOT_WAITING);

    public PrinterAncillarySystems()
    {
        for (int i = 0; i < BaseConfiguration.NUMBER_OF_TEMPERATURE_POINTS_TO_KEEP; i++)
        {
            LineChart.Data<Number, Number> newAmbientPoint = new LineChart.Data<>(i, 0);
            ambientTemperatureDataPoints.add(newAmbientPoint);
            ambientTemperatureHistory.getData().add(newAmbientPoint);
            LineChart.Data<Number, Number> newBedPoint = new LineChart.Data<>(i, 0);
            bedTemperatureDataPoints.add(newBedPoint);
            bedTemperatureHistory.getData().add(newBedPoint);
        }
        ambientTargetTemperatureSeries.getData().add(ambientTargetPoint);
        bedTargetTemperatureSeries.getData().add(bedTargetPoint);
    }

    public ReadOnlyBooleanProperty xStopSwitchProperty()
    {
        return XStopSwitch;
    }

    public ReadOnlyBooleanProperty yStopSwitchProperty()
    {
        return YStopSwitch;
    }

    public ReadOnlyBooleanProperty zStopSwitchProperty()
    {
        return ZStopSwitch;
    }

    public ReadOnlyBooleanProperty zTopStopSwitchProperty()
    {
        return ZTopStopSwitch;
    }

    public ReadOnlyBooleanProperty reelButtonProperty()
    {
        return reelButton;
    }

    public ReadOnlyBooleanProperty headFanOnProperty()
    {
        return headFanOn;
    }

    public ReadOnlyBooleanProperty ambientFanOnProperty()
    {
        return ambientFanOn;
    }

    public ReadOnlyBooleanProperty bAxisHomeProperty()
    {
        return bAxisHome;
    }

    public ReadOnlyBooleanProperty doorOpenProperty()
    {
        return doorOpen;
    }
    
    public ReadOnlyBooleanProperty dualReelAdaptorPresentProperty()
    {
        return dualReelAdaptorPresent;
    }
      
    public ReadOnlyFloatProperty feedRateEMultiplierProperty()
    {
        return feedRateEMultiplier;
    }
    
    public ReadOnlyFloatProperty feedRateDMultiplierProperty()
    {
        return feedRateDMultiplier;
    }
    
    public ReadOnlyObjectProperty<HeaterMode> bedHeaterModeProperty()
    {
        return bedHeaterMode;
    }

    public ReadOnlyObjectProperty<WhyAreWeWaitingState> whyAreWeWaitingProperty()
    {
        return whyAreWeWaitingProperty;
    }

    public ReadOnlyIntegerProperty ambientTemperatureProperty()
    {
        return ambientTemperature;
    }

    public ReadOnlyIntegerProperty ambientTargetTemperatureProperty()
    {
        return ambientTargetTemperature;
    }

    public ReadOnlyIntegerProperty bedTemperatureProperty()
    {
        return bedTemperature;
    }

    public ReadOnlyIntegerProperty bedFirstLayerTargetTemperatureProperty()
    {
        return bedFirstLayerTargetTemperature;
    }

    public ReadOnlyIntegerProperty bedTargetTemperatureProperty()
    {
        return bedTargetTemperature;
    }

    public ReadOnlyFloatProperty hoursCounterProperty()
    {
        return hoursCounter;
    }

    public ReadOnlyBooleanProperty sdCardInsertedProperty()
    {
        return sdCardInserted;
    }

    public XYChart.Series<Number, Number> getAmbientTemperatureHistory()
    {
        return ambientTemperatureHistory;
    }

    public XYChart.Series<Number, Number> getBedTemperatureHistory()
    {
        return bedTemperatureHistory;
    }

    protected void updateGraphData()
    {
        long now = System.currentTimeMillis();
        if ((now - lastTemperatureTimestamp) >= 999)
        {
            lastTemperatureTimestamp = now;

            for (int pointCounter = 0; pointCounter < BaseConfiguration.NUMBER_OF_TEMPERATURE_POINTS_TO_KEEP
                - 1; pointCounter++)
            {
                ambientTemperatureDataPoints.get(pointCounter).setYValue(
                    ambientTemperatureDataPoints.get(pointCounter + 1).getYValue());
                bedTemperatureDataPoints.get(pointCounter).setYValue(
                    bedTemperatureDataPoints.get(pointCounter + 1).getYValue());
            }

            ambientTemperatureDataPoints
                .get(BaseConfiguration.NUMBER_OF_TEMPERATURE_POINTS_TO_KEEP - 1)
                .setYValue(ambientTemperature.get());

            if (bedTemperature.get() < BaseConfiguration.maxTempToDisplayOnGraph
                && bedTemperature.get() > BaseConfiguration.minTempToDisplayOnGraph)
            {
                bedTemperatureDataPoints
                    .get(BaseConfiguration.NUMBER_OF_TEMPERATURE_POINTS_TO_KEEP - 1)
                    .setYValue(bedTemperature.get());
            }
        }

        ambientTargetPoint.setYValue(ambientTargetTemperature.get());

        switch (bedHeaterMode.get())
        {
            case OFF:
                bedTargetPoint.setYValue(0);
                break;
            case FIRST_LAYER:
                bedTargetPoint.setYValue(bedFirstLayerTargetTemperature.get());
                break;
            case NORMAL:
                bedTargetPoint.setYValue(bedTargetTemperature.get());
                break;
            default:
                break;
        }
    }
}
