package celtech.roboxbase.printerControl.model;

import celtech.roboxbase.configuration.BaseConfiguration;
import java.util.ArrayList;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyFloatProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

/**
 *
 * @author ianhudson
 */
public class NozzleHeater implements Cloneable
{

    protected final ObjectProperty<HeaterMode> heaterMode = new SimpleObjectProperty<>(HeaterMode.OFF);

    protected final FloatProperty maximumTemperature = new SimpleFloatProperty(0);
    protected final FloatProperty beta = new SimpleFloatProperty(0);
    protected final FloatProperty tcal = new SimpleFloatProperty(0);
    protected final FloatProperty lastFilamentTemperature = new SimpleFloatProperty(0);

    protected final IntegerProperty nozzleTemperature = new SimpleIntegerProperty(0);
    protected final IntegerProperty nozzleFirstLayerTargetTemperature = new SimpleIntegerProperty(0);
    protected final IntegerProperty nozzleTargetTemperature = new SimpleIntegerProperty(0);
    
    protected final StringProperty filamentID = new SimpleStringProperty("");

    private final LineChart.Series<Number, Number> nozzleTemperatureHistory = new LineChart.Series<>();
    private final ArrayList<LineChart.Data<Number, Number>> nozzleTemperatureDataPoints = new ArrayList<>();
    private final LineChart.Series<Number, Number> nozzleTargetTemperatureSeries = new LineChart.Series<>();
    private final LineChart.Data<Number, Number> nozzleTargetPoint = new LineChart.Data<>(
        BaseConfiguration.NUMBER_OF_TEMPERATURE_POINTS_TO_KEEP + 5, 0);
    private long lastTemperatureTimestamp = 0;

    public NozzleHeater()
    {
        initialiseTemperatureGraph();
    }
    
    public NozzleHeater(float maximumTemperature,
        float beta,
        float tcal,
        float lastFilamentTemperature,
        int nozzleTemperature,
        int nozzleFirstLayerTargetTemperature,
        int nozzleTargetTemperature,
        String filamentID)
    {
        this.maximumTemperature.set(maximumTemperature);
        this.beta.set(beta);
        this.tcal.set(tcal);
        this.lastFilamentTemperature.set(lastFilamentTemperature);
        this.nozzleTemperature.set(nozzleTemperature);
        this.nozzleFirstLayerTargetTemperature.set(nozzleFirstLayerTargetTemperature);
        this.nozzleTargetTemperature.set(nozzleTargetTemperature);
        this.filamentID.set(filamentID);

        initialiseTemperatureGraph();
    }

    private void initialiseTemperatureGraph()
    {
        for (int i = 0; i < BaseConfiguration.NUMBER_OF_TEMPERATURE_POINTS_TO_KEEP; i++)
        {
            LineChart.Data<Number, Number> newNozzlePoint = new LineChart.Data<>(i, 0);
            nozzleTemperatureDataPoints.add(newNozzlePoint);
            nozzleTemperatureHistory.getData().add(newNozzlePoint);
        }
        nozzleTargetTemperatureSeries.getData().add(nozzleTargetPoint);
    }

    public final ReadOnlyObjectProperty<HeaterMode> heaterModeProperty()
    {
        return heaterMode;
    }

    public ReadOnlyFloatProperty maximumTemperatureProperty()
    {
        return maximumTemperature;
    }

    public ReadOnlyFloatProperty betaProperty()
    {
        return beta;
    }

    public ReadOnlyFloatProperty tCalProperty()
    {
        return tcal;
    }

    public FloatProperty lastFilamentTemperatureProperty()
    {
        return lastFilamentTemperature;
    }

    public ReadOnlyIntegerProperty nozzleTemperatureProperty()
    {
        return nozzleTemperature;
    }

    public ReadOnlyIntegerProperty nozzleFirstLayerTargetTemperatureProperty()
    {
        return nozzleFirstLayerTargetTemperature;
    }

    public ReadOnlyIntegerProperty nozzleTargetTemperatureProperty()
    {
        return nozzleTargetTemperature;
    }
    
    public ReadOnlyStringProperty filamentIDProperty()
    {
        return filamentID;
    }    
    
    public XYChart.Series<Number, Number> getNozzleTemperatureHistory()
    {
        return nozzleTemperatureHistory;
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
                nozzleTemperatureDataPoints.get(pointCounter).setYValue(
                    nozzleTemperatureDataPoints.get(pointCounter + 1).getYValue());
            }

//            nozzleTemperatureDataPoints.add(bedTargetPoint);

            if (nozzleTemperature.get() < BaseConfiguration.maxTempToDisplayOnGraph
                && nozzleTemperature.get() > BaseConfiguration.minTempToDisplayOnGraph)
            {
                nozzleTemperatureDataPoints
                    .get(BaseConfiguration.NUMBER_OF_TEMPERATURE_POINTS_TO_KEEP - 1)
                    .setYValue(nozzleTemperature.get());
            }
        }

        switch (heaterMode.get())
        {
            case OFF:
                nozzleTargetPoint.setYValue(0);
                break;
            case FIRST_LAYER:
                nozzleTargetPoint.setYValue(nozzleFirstLayerTargetTemperature.get());
                break;
            case NORMAL:
                nozzleTargetPoint.setYValue(nozzleTargetTemperature.get());
                break;
            default:
                break;
        }

    }

    @Override
    public NozzleHeater clone()
    {
        NozzleHeater clone = new NozzleHeater(
            maximumTemperature.floatValue(),
            beta.floatValue(),
            tcal.floatValue(),
            lastFilamentTemperature.floatValue(),
            nozzleTemperature.intValue(),
            nozzleFirstLayerTargetTemperature.intValue(),
            nozzleTargetTemperature.intValue(),
            filamentID.getValue()
        );

        return clone;
    }
}
