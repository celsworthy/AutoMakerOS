/*
 * Copyright 2014 CEL UK
 */
package celtech.roboxbase.printerControl.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * ETCCalculator calculates the ETC (estimated time to complete) of print jobs
 * for a given line number being processed.
 *
 * @author tony
 */
public class ETCCalculator
{

    /**
     * The line number at the top of the given layer. The first element should
     * have a value of 0.
     */
    private final List<Integer> layerNumberToLineNumber;
    private final Map<Integer, Double> layerNumberToPredictedDuration_E;
    private final Map<Integer, Double> layerNumberToPredictedDuration_D;
    private final Map<Integer, Double> layerNumberToPredictedDuration_feedrateIndependent;
    /**
     * The time taken to get to the start of the given layer. The first element
     * should have a value of 0.
     */
    private final List<Double> layerNumberToTotalPredictedDuration = new ArrayList<>();
    /**
     * The time to print all layers
     */
    double totalPredictedDurationAllLayers;

    private double currentFeedrateMultiplierE = 1.0;
    private double currentFeedrateMultiplierD = 1.0;

    /**
     * The estimated number of seconds it takes to heat the bed up by one degree
     */
    protected static int PREDICTED_BED_HEAT_RATE = 2;
    private final Printer printer;

    public ETCCalculator(Printer printer,
            Map<Integer, Double> layerNumberToPredictedDuration_E,
            Map<Integer, Double> layerNumberToPredictedDuration_D,
            Map<Integer, Double> layerNumberToPredictedDuration_feedrateIndependent,
            List<Integer> layerNumberToLineNumber)
    {
        this.printer = printer;
        this.layerNumberToLineNumber = layerNumberToLineNumber;
        this.layerNumberToPredictedDuration_E = layerNumberToPredictedDuration_E;
        this.layerNumberToPredictedDuration_D = layerNumberToPredictedDuration_D;
        this.layerNumberToPredictedDuration_feedrateIndependent = layerNumberToPredictedDuration_feedrateIndependent;

        assert (layerNumberToPredictedDuration_E.get(0) == 0);
        assert (layerNumberToPredictedDuration_D.get(0) == 0);
        assert (layerNumberToPredictedDuration_feedrateIndependent.get(0) == 0);
        assert (layerNumberToLineNumber.get(0) == 0);

        updateFromFeedrateChange();

        printer.getPrinterAncillarySystems().feedRateEMultiplier.addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1)
            {
                updateFromFeedrateChange();
            }
        });

        printer.getPrinterAncillarySystems().feedRateDMultiplier.addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1)
            {
                updateFromFeedrateChange();
            }
        });
    }

    private void updateFromFeedrateChange()
    {
        double totalPredictedDuration = 0;

        currentFeedrateMultiplierE = 1 / printer.getPrinterAncillarySystems().feedRateEMultiplier.doubleValue();
        currentFeedrateMultiplierD = 1 / printer.getPrinterAncillarySystems().feedRateDMultiplier.doubleValue();

        if (layerNumberToPredictedDuration_E != null && layerNumberToPredictedDuration_D != null)
        {
            for (int i = 0; i < layerNumberToPredictedDuration_E.size(); i++)
            {
                totalPredictedDuration += layerNumberToPredictedDuration_E.get(i) * currentFeedrateMultiplierE;
                totalPredictedDuration += layerNumberToPredictedDuration_D.get(i) * currentFeedrateMultiplierD;
                totalPredictedDuration += layerNumberToPredictedDuration_feedrateIndependent.get(i);
                layerNumberToTotalPredictedDuration.add(i, totalPredictedDuration);
            }
        }
        totalPredictedDurationAllLayers = totalPredictedDuration;
    }

    /**
     * Calculate the ETC based on predicted durations.
     *
     * @return the number of seconds
     */
    public int getETCPredicted(int lineNumber)
    {
        int remainingTimeSeconds = getPredictedRemainingPrintTime(lineNumber);
        remainingTimeSeconds += getBedHeatingTime();
        return remainingTimeSeconds;
    }

    /**
     * Estimate the time to heat the bed up to the target temperature
     *
     * @return the time in seconds
     */
    private int getBedHeatingTime()
    {
        int bedTargetTemperature = printer.getPrinterAncillarySystems().bedTargetTemperatureProperty().get();
        int bedTemperature = printer.getPrinterAncillarySystems().bedTemperatureProperty().get();
        if (bedTemperature < bedTargetTemperature)
        {
            return (bedTargetTemperature - bedTemperature)
                    * PREDICTED_BED_HEAT_RATE;
        } else
        {
            return 0;
        }
    }

    /**
     * Return the predicted remaining print time by calculating the predicted
     * time to reach the current line, and subtract from the predicted total
     * time.
     */
    private int getPredictedRemainingPrintTime(int lineNumber)
    {
        int layerNumber = getCurrentLayerNumberForLineNumber(lineNumber);
        double totalPredictedDurationAtEndOfPreviousLayer = 0;

        if (layerNumberToTotalPredictedDuration.size() > 0 && layerNumber != 1)
        {
            totalPredictedDurationAtEndOfPreviousLayer = layerNumberToTotalPredictedDuration.get(layerNumber - 2);
        }
        double elapsedTimeInThisLayer = getPartialDurationInLayer(
                layerNumber, lineNumber);
        double totalDurationSoFar = totalPredictedDurationAtEndOfPreviousLayer
                + elapsedTimeInThisLayer;
        int remainingTimeSeconds = (int) ((totalPredictedDurationAllLayers
                - totalDurationSoFar));
        return remainingTimeSeconds;
    }

    /**
     * Get the completed layer number for the given line number.
     */
    public int getCurrentLayerNumberForLineNumber(int lineNumber)
    {
        if (layerNumberToLineNumber != null)
        {
            for (int layerNumber = 0; layerNumber < layerNumberToLineNumber.size(); layerNumber++)
            {
                Integer lineNumberForLayer = layerNumberToLineNumber.get(layerNumber);
                if (lineNumberForLayer >= lineNumber)
                {
                    return layerNumber + 1;
                }
            }
            throw new RuntimeException(
                    "Did not calculate layer number - line number greater"
                    + " than total number of lines");
        }
        return 0;
    }

    /**
     * Return the estimated duration to partially print a layer up to the given
     * line number.
     */
    protected double getPartialDurationInLayer(int layerNumber, int lineNumber)
    {
        double numLinesAtStartOfLayer = 0;
        if (layerNumber > 1)
        {
            numLinesAtStartOfLayer = layerNumberToLineNumber.get(layerNumber - 2);
        }
        double progressInThisLayer = lineNumber
                - numLinesAtStartOfLayer;
        if (progressInThisLayer == 0)
        {
            return 0;
        }
        if (layerNumberToLineNumber != null)
        {
            double numLinesAtEndOfLayer = layerNumberToLineNumber.get(layerNumber - 1);
            double durationInLayer = layerNumberToPredictedDuration_E.get(layerNumber - 1) * currentFeedrateMultiplierE;
            durationInLayer += layerNumberToPredictedDuration_D.get(layerNumber - 1) * currentFeedrateMultiplierD;
            durationInLayer += layerNumberToPredictedDuration_feedrateIndependent.get(layerNumber - 1);
            double totalLinesInNextLayer = numLinesAtEndOfLayer
                    - numLinesAtStartOfLayer;
            return (progressInThisLayer / totalLinesInNextLayer)
                    * durationInLayer;
        } else
        {
            return 0;
        }
    }

    /**
     * Return the percentage complete based on the line number reached.
     */
    public double getPercentCompleteAtLine(int lineNumber)
    {
        double percent = (totalPredictedDurationAllLayers
                - getPredictedRemainingPrintTime(lineNumber))
                / totalPredictedDurationAllLayers;

        if (percent < 0)
        {
            percent = 0;
        }

        return percent;
    }

}
