/*
 * Copyright 2014 CEL UK
 */
package celtech.coreUI.controllers.panels;

import celtech.Lookup;
import celtech.configuration.ApplicationConfiguration;
import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.printerControl.model.Head;
import celtech.roboxbase.printerControl.model.HeaterMode;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.printerControl.model.PrinterAncillarySystems;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

/**
 * ChartManager is an auxiliary class to PrinterStatusSidePanelController and
 * manages the temperature chart.
 *
 * @author tony
 */
class ChartManager
{

    private final LineChart<Number, Number> chart;
    private XYChart.Series<Number, Number> ambientData = new XYChart.Series<>();
    private XYChart.Series<Number, Number> bedData = new XYChart.Series<>();
    private final LineChart.Series<Number, Number> ambientTargetTemperatureSeries = new LineChart.Series<>();
    private final LineChart.Series<Number, Number> bedTargetTemperatureSeries = new LineChart.Series<>();
    private final LineChart.Data<Number, Number> ambientTargetPoint = new LineChart.Data<>(
            BaseConfiguration.NUMBER_OF_TEMPERATURE_POINTS_TO_KEEP - 5, 0);
    private final LineChart.Data<Number, Number> bedTargetPoint = new LineChart.Data<>(
            BaseConfiguration.NUMBER_OF_TEMPERATURE_POINTS_TO_KEEP - 5, 0);

    private ReadOnlyIntegerProperty bedTargetTemperatureProperty;
    private ReadOnlyIntegerProperty bedFirstLayerTargetTemperatureProperty;
    private ReadOnlyIntegerProperty ambientTargetTemperatureProperty;
    private ReadOnlyIntegerProperty bedTemperatureProperty;
    private ReadOnlyIntegerProperty ambientTemperatureProperty;
    private ReadOnlyObjectProperty<HeaterMode> bedHeaterModeProperty;
    private Label legendBed;
    private Label legendAmbient;
    private final List<NozzleChartData> nozzleChartDataSets = new ArrayList<>();
    private final int nozzleTargetTempFirstIndex = 2;

    private final int ambientIndexOffset = 0;
    private final int bedIndexOffset = 1;
    private final int firstNozzleIndexOffset = 2;

    private final String ambientBugColour = "#145019";
    private final String bedBugColour = "#a07800";
    private final List<String> nozzleBugColour = new ArrayList<>();
    private final String ambientLineColour = "#1eb43c";
    private final String bedLineColour = "#ffc800";
    private final List<String> nozzleLineColour = new ArrayList<>();

    private final String rhTriangleBugCSS
            = "-fx-background-radius: 0; "
            + "-fx-shape: \"M0,6 L12,0 L12,12 L0,6 Z\"; "
            + "-fx-scale-x: 2; "
            + "-fx-scale-y: 2; ";

    private final String rhTriangleBugUpperOnlyCSS
            = "-fx-background-radius: 0; "
            + "-fx-shape: \"M0,12 L24,12 L24,24 L0,12 Z\"; "
            + "-fx-alignment: bottom-center; ";

    private final String rhTriangleBugLowerOnlyCSS
            = "-fx-background-radius: 0; "
            + "-fx-shape: \"M0,6 L12,0 L12,6 L0,6 Z\"; "
            + "-fx-padding: 8 0 0 0;";

    private final String graphLineCSS = "-fx-stroke-width: 3; -fx-stroke-type: centered; -fx-stroke-line-cap: butt; ";
    private final String graphLineFatCSS = "-fx-stroke-width: 4; -fx-stroke-type: centered; -fx-stroke-line-cap: butt; ";
    private final String graphLineThinCSS = "-fx-stroke-width: 2; -fx-stroke-type: centered; -fx-stroke-line-cap: butt; ";
    private int dashedLineCounter = 0;

    private Printer currentPrinter = null;

    private enum BugType
    {

        ALL, UPPER, LOWER
    }

    public ChartManager(LineChart<Number, Number> chart)
    {
        this.chart = chart;
        ambientTargetTemperatureSeries.getData().add(ambientTargetPoint);
        bedTargetTemperatureSeries.getData().add(bedTargetPoint);

        nozzleBugColour.add("#009EE3");
        nozzleBugColour.add("#FF661B");

        nozzleLineColour.add("#009EE3");
        nozzleLineColour.add("#FF661B");

    }

    public void bindPrinter(Printer printer)
    {
        currentPrinter = printer;

        PrinterAncillarySystems ancillarySystems = printer.getPrinterAncillarySystems();
        setAmbientData(ancillarySystems.getAmbientTemperatureHistory());
        setBedData(ancillarySystems.getBedTemperatureHistory());
        setAmbientTemperatureProperty(ancillarySystems.ambientTemperatureProperty());
        setBedTemperatureProperty(ancillarySystems.bedTemperatureProperty());

        setTargetAmbientTemperatureProperty(
                ancillarySystems.ambientTargetTemperatureProperty());
        setBedHeaterModeProperty(ancillarySystems.bedHeaterModeProperty());

        setTargetBedTemperatureProperty(ancillarySystems.bedTargetTemperatureProperty());
        setTargetBedFirstLayerTemperatureProperty(ancillarySystems.
                bedFirstLayerTargetTemperatureProperty());
    }

    public void unbindPrinter()
    {
        clearAmbientData();
        clearBedData();
        clearLegendLabels();
    }

    public void setAmbientData(XYChart.Series<Number, Number> ambientData)
    {
        this.ambientData = ambientData;
        updateChartDataSources();
    }

    public void setBedData(XYChart.Series<Number, Number> bedData)
    {
        this.bedData = bedData;
        updateChartDataSources();
    }

    /**
     * update the chart to display all the correct data.
     */
    private void updateChartDataSources()
    {
        dashedLineCounter = 0;

        chart.getData().clear();

        chart.applyCss();

        //Set up the ambient bug
        setupBug(ambientIndexOffset, ambientTargetTemperatureSeries, ambientBugColour, BugType.ALL);

        //Set up the bed bug
        setupBug(bedIndexOffset, bedTargetTemperatureSeries, bedBugColour, BugType.ALL);

        //Set up the nozzle bugs
        for (int nozzleCounter = 0; nozzleCounter < nozzleChartDataSets.size(); nozzleCounter++)
        {
            BugType bugType;
            if (nozzleCounter % 2 == 0)
            {
                bugType = BugType.UPPER;
            } else
            {
                bugType = BugType.LOWER;
            }

            int nozzleBugColourIndex = 0;
            if (currentPrinter.headProperty().get().headTypeProperty().get() == Head.HeadType.DUAL_MATERIAL_HEAD)
            {
                switch (nozzleCounter)
                {
                    case 0:
                        nozzleBugColourIndex = 1;
                        break;
                    case 1:
                        nozzleBugColourIndex = 0;
                        break;
                }
            } else
            {
                nozzleBugColourIndex = 0;
            }

            setupBug(nozzleTargetTempFirstIndex + nozzleCounter, nozzleChartDataSets.get(
                    nozzleCounter).getTargetTemperatureSeries(), nozzleBugColour.get(nozzleBugColourIndex), BugType.ALL);
        }

        int startOfLineSection = firstNozzleIndexOffset + nozzleChartDataSets.size();
        setupChartLine(startOfLineSection + ambientIndexOffset, ambientData, ambientLineColour, false);
        setupChartLine(startOfLineSection + bedIndexOffset, bedData, bedLineColour, false);

        boolean dashedNozzleLines = nozzleChartDataSets.size() > 1;

        for (int nozzleCounter = 0; nozzleCounter < nozzleChartDataSets.size(); nozzleCounter++)
        {
            int nozzleBugColourIndex = 0;
            if (currentPrinter.headProperty().get().headTypeProperty().get() == Head.HeadType.DUAL_MATERIAL_HEAD)
            {
                switch (nozzleCounter)
                {
                    case 0:
                        nozzleBugColourIndex = 1;
                        break;
                    case 1:
                        nozzleBugColourIndex = 0;
                        break;
                }
            } else
            {
                nozzleBugColourIndex = 0;
            }
            setupChartLine(startOfLineSection + firstNozzleIndexOffset + nozzleCounter,
                    nozzleChartDataSets.get(nozzleCounter).getNozzleTemperatureSeries(),
                    nozzleLineColour.get(nozzleBugColourIndex),
                    dashedNozzleLines);
        }
    }

    private void setupBug(int offset, XYChart.Series<Number, Number> series, String webColour, BugType bugType)
    {
        chart.getData().add(offset, series);
        Set<Node> symbolNodes = chart.lookupAll(".default-color" + offset + ".chart-line-symbol");

        symbolNodes.forEach(symbolNode ->
        {
            switch (bugType)
            {
                case ALL:
                    symbolNode.setStyle(rhTriangleBugCSS + "-fx-background-color: " + webColour + "; ");
                    break;
                case LOWER:
                    symbolNode.setStyle(rhTriangleBugLowerOnlyCSS + "-fx-background-color: " + webColour + "; ");
                    break;
                case UPPER:
                    symbolNode.setStyle(rhTriangleBugUpperOnlyCSS + "-fx-background-color: " + webColour + "; ");
                    break;
            }
        });
    }

    private void setupChartLine(int offset, XYChart.Series<Number, Number> series, String webColour,
            boolean dashed)
    {
        chart.getData().add(offset, series);

        Set<Node> symbolNodes = chart.lookupAll(".default-color" + offset + ".chart-line-symbol");

        symbolNodes.forEach(symbolNode ->
        {
            symbolNode.setStyle("visibility: hidden;");
        });

        Node lineNode = chart.lookup(".default-color" + offset + ".chart-series-line");

        if (lineNode != null)
        {
            if (dashed)
            {
                if (dashedLineCounter > 0)
                {
                    lineNode.setStyle(graphLineThinCSS + "-fx-stroke: " + webColour);
                } else
                {
                    lineNode.setStyle(graphLineFatCSS + "-fx-stroke: " + webColour);
                }
                dashedLineCounter++;
            } else
            {
                lineNode.setStyle(graphLineCSS + "-fx-stroke: " + webColour + "; ");
            }
        }
    }

    void clearBedData()
    {
        bedData = new XYChart.Series<>();
        bedTemperatureProperty = null;
    }

    void clearAmbientData()
    {
        ambientData = new XYChart.Series<>();
        ambientTemperatureProperty = null;
    }

    ChangeListener<Number> ambientTargetTemperatureListener = (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) ->
    {
        ambientTargetPoint.setYValue(newValue);
    };

    void setTargetAmbientTemperatureProperty(
            ReadOnlyIntegerProperty ambientTargetTemperatureProperty)
    {
        if (this.ambientTargetTemperatureProperty != null)
        {
            ambientTargetTemperatureProperty.removeListener(ambientTargetTemperatureListener);
        }
        ambientTargetPoint.setYValue(ambientTargetTemperatureProperty.get());
        this.ambientTargetTemperatureProperty = ambientTargetTemperatureProperty;
        ambientTargetTemperatureProperty.addListener(ambientTargetTemperatureListener);
    }

    ChangeListener<Number> bedTargetTemperatureListener = (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) ->
    {
        updateBedTargetPoint();
    };

    void setTargetBedTemperatureProperty(
            ReadOnlyIntegerProperty bedTargetTemperatureProperty)
    {
        if (this.bedTargetTemperatureProperty != null)
        {
            this.bedTargetTemperatureProperty.removeListener(bedTargetTemperatureListener);
        }
        this.bedTargetTemperatureProperty = bedTargetTemperatureProperty;

        bedTargetTemperatureProperty.addListener(bedTargetTemperatureListener);
        updateBedTargetPoint();
    }

    ChangeListener<Number> bedFirstLayerTargetTemperatureListener = (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) ->
    {
        updateBedTargetPoint();
    };

    void setTargetBedFirstLayerTemperatureProperty(
            ReadOnlyIntegerProperty bedFirstLayerTargetTemperatureProperty)
    {
        if (this.bedFirstLayerTargetTemperatureProperty != null)
        {
            this.bedFirstLayerTargetTemperatureProperty.removeListener(bedTargetTemperatureListener);
        }
        this.bedFirstLayerTargetTemperatureProperty = bedFirstLayerTargetTemperatureProperty;

        bedFirstLayerTargetTemperatureProperty.addListener(bedFirstLayerTargetTemperatureListener);
        updateBedTargetPoint();
    }

    ChangeListener<HeaterMode> bedHeaterModeListener = (ObservableValue<? extends HeaterMode> observable, HeaterMode oldValue, HeaterMode newValue) ->
    {
        updateBedTargetPoint();
    };

    void setBedHeaterModeProperty(ReadOnlyObjectProperty<HeaterMode> bedHeaterModeProperty)
    {
        if (this.bedHeaterModeProperty != null)
        {
            this.bedHeaterModeProperty.removeListener(bedHeaterModeListener);
        }
        bedHeaterModeProperty.addListener(bedHeaterModeListener);
        this.bedHeaterModeProperty = bedHeaterModeProperty;
        updateBedTargetPoint();
    }

    void updateBedTargetPoint()
    {
        if (bedHeaterModeProperty.get() == HeaterMode.OFF)
        {
            bedTargetPoint.setYValue(0);
        } else if (bedHeaterModeProperty.get() == HeaterMode.FIRST_LAYER)
        {
            bedTargetPoint.setYValue(bedFirstLayerTargetTemperatureProperty.get());
        } else if (bedHeaterModeProperty.get() == HeaterMode.NORMAL)
        {
            bedTargetPoint.setYValue(bedTargetTemperatureProperty.get());
        }
    }

    void setLegendLabels(Label legendBed, Label legendAmbient)
    {
        this.legendBed = legendBed;
        this.legendAmbient = legendAmbient;
        updateLegend();
    }

    void clearLegendLabels()
    {
        this.legendBed = null;
        this.legendAmbient = null;
        updateLegend();
    }

    private void updateLegend()
    {
        String degreesC = Lookup.i18n("misc.degreesC");
        String legendBedText = Lookup.i18n("printerStatus.temperatureGraphBedLabel");
        String legendAmbientText = Lookup.i18n("printerStatus.temperatureGraphAmbientLabel");

        if (legendBed != null && bedTemperatureProperty != null)
        {
            legendBedText += String.format(" %s%s", bedTemperatureProperty.get(), degreesC);
        }

        if (legendAmbient != null && ambientTemperatureProperty != null)
        {
            legendAmbientText += String.format(" %s%s", ambientTemperatureProperty.get(), degreesC);
        }

        if (legendBed != null)
        {
            legendBed.setText(legendBedText);
        }
        if (legendAmbient != null)
        {
            legendAmbient.setText(legendAmbientText);
        }
    }

    InvalidationListener bedTemperatureListener = (Observable observable) ->
    {
        updateLegend();
    };

    void setBedTemperatureProperty(ReadOnlyIntegerProperty bedTemperatureProperty)
    {
        if (this.bedTemperatureProperty != null)
        {
            this.bedTemperatureProperty.removeListener(bedTemperatureListener);
        }
        this.bedTemperatureProperty = bedTemperatureProperty;
        bedTemperatureProperty.addListener(bedTemperatureListener);
        updateLegend();
    }

    InvalidationListener ambientTemperatureListener = (Observable observable) ->
    {
        updateLegend();
    };

    void setAmbientTemperatureProperty(ReadOnlyIntegerProperty ambientTemperatureProperty)
    {
        if (this.ambientTemperatureProperty != null)
        {
            this.ambientTemperatureProperty.removeListener(ambientTemperatureListener);
        }
        this.ambientTemperatureProperty = ambientTemperatureProperty;
        ambientTemperatureProperty.addListener(ambientTemperatureListener);
        updateLegend();
    }

    void addNozzle(int nozzleNumber, XYChart.Series<Number, Number> nozzleTemperatureData,
            ReadOnlyObjectProperty<HeaterMode> nozzleHeaterModeProperty,
            ReadOnlyIntegerProperty nozzleTargetTemperatureProperty,
            ReadOnlyIntegerProperty nozzleFirstLayerTargetTemperatureProperty,
            ReadOnlyIntegerProperty nozzleTemperatureProperty)
    {
        NozzleChartData nozzleChartData = new NozzleChartData(nozzleNumber, nozzleTemperatureData,
                nozzleHeaterModeProperty,
                nozzleTargetTemperatureProperty,
                nozzleFirstLayerTargetTemperatureProperty,
                nozzleTemperatureProperty);

        nozzleChartDataSets.add(nozzleChartData);

        updateChartDataSources();
    }

    void removeNozzle(int nozzleNumber)
    {
        nozzleChartDataSets.get(nozzleNumber).destroy();
        nozzleChartDataSets.remove(nozzleNumber);

        updateChartDataSources();
    }

    void removeAllNozzles()
    {
        nozzleChartDataSets.forEach(dataSet ->
        {
            dataSet.destroy();
        });

        nozzleChartDataSets.clear();

        updateChartDataSources();
    }
}
