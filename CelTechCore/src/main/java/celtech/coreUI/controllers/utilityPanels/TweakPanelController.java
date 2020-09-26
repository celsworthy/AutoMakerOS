package celtech.coreUI.controllers.utilityPanels;

import celtech.Lookup;
import celtech.roboxbase.printerControl.model.HeaterMode;
import celtech.coreUI.controllers.StatusInsetController;
import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.postprocessor.PrintJobStatistics;
import celtech.roboxbase.printerControl.PrintJob;
import celtech.roboxbase.printerControl.PrintQueueStatus;
import celtech.roboxbase.printerControl.model.Head;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.printerControl.model.PrinterException;
import celtech.roboxbase.printerControl.model.PrinterListChangesListener;
import celtech.roboxbase.printerControl.model.Reel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 * FXML Controller class
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class TweakPanelController implements Initializable, StatusInsetController, PrinterListChangesListener
{

    private final Stenographer steno = StenographerFactory.getStenographer(TweakPanelController.class.getName());

    @FXML
    private VBox container;

    @FXML
    private Label printSpeed1Display;

    @FXML
    private Label printSpeed2Display;

    @FXML
    private VBox printSpeed1Box;

    @FXML
    private VBox printSpeed2Box;

    @FXML
    private Slider speedMultiplier1Slider;

    @FXML
    private Slider speedMultiplier2Slider;

    @FXML
    private VBox material1VBox;

    @FXML
    private VBox extrusionMultiplier1Box;

    @FXML
    private Label extrusionMultiplier1Text;

    @FXML
    private Label extrusionMultiplier1Display;

    @FXML
    private Slider extrusionMultiplier1Slider;

    @FXML
    private VBox material2VBox;

    @FXML
    private VBox extrusionMultiplier2Box;

    @FXML
    private Label extrusionMultiplier2Display;

    @FXML
    private Slider extrusionMultiplier2Slider;

    @FXML
    private Label material1Text;

    @FXML
    private Label material1Display;

    @FXML
    private Slider material1TemperatureSlider;

    @FXML
    private Label material2Display;

    @FXML
    private Slider material2TemperatureSlider;

    @FXML
    private Label bedDisplay;

    @FXML
    private Slider bedTemperatureSlider;

    private Printer currentPrinter = null;

    private final ChangeListener<PrintQueueStatus> printQueueStatusListener = new ChangeListener<PrintQueueStatus>()
    {
        @Override
        public void changed(ObservableValue<? extends PrintQueueStatus> ov, PrintQueueStatus t, PrintQueueStatus newStatus)
        {
            if (newStatus == PrintQueueStatus.PRINTING)
            {
                unbind();
                bind();
            } else
            {
                unbind();
            }
        }
    };

    private PrintJob currentPrintJob = null;
    private PrintJobStatistics currentPrintJobStatistics = null;

    private boolean inhibitFeedrate1 = false;
    private final ChangeListener<Number> feedRate1ChangeListener
            = (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) ->
            {
                inhibitFeedrate1 = true;
                speedMultiplier1Slider.setValue(newValue.doubleValue() * 100.0);
                printSpeed1Display.setText(String.format("%d%%", (int) (newValue.doubleValue() * 100.0)));
                inhibitFeedrate1 = false;
            };

    private boolean inhibitFeedrate2 = false;
    private final ChangeListener<Number> feedRate2ChangeListener
            = (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) ->
            {
                inhibitFeedrate2 = true;
                speedMultiplier2Slider.setValue(newValue.doubleValue() * 100.0);
                printSpeed2Display.setText(String.format("%d%%", (int) (newValue.doubleValue() * 100.0)));
                inhibitFeedrate2 = false;
            };

    private final ChangeListener<Number> speedMultiplier1SliderListener
            = (ObservableValue<? extends Number> observable, Number was, Number now) ->
            {
                if (!inhibitFeedrate1
                && (!speedMultiplier1Slider.isValueChanging()
                || now.doubleValue() >= speedMultiplier1Slider.getMax()
                || now.doubleValue() <= speedMultiplier1Slider.getMin()))
                {
                    try
                    {
                        currentPrinter.changeEFeedRateMultiplier(now.doubleValue() / 100.0);
                    } catch (PrinterException ex)
                    {
                        steno.error("Error setting feed rate multiplier - " + ex.getMessage());
                    }
                }
            };

    private final ChangeListener<Number> speedMultiplier2SliderListener
            = (ObservableValue<? extends Number> observable, Number was, Number now) ->
            {
                if (!inhibitFeedrate2
                && (!speedMultiplier2Slider.isValueChanging()
                || now.doubleValue() >= speedMultiplier2Slider.getMax()
                || now.doubleValue() <= speedMultiplier2Slider.getMin()))
                {
                    try
                    {
                        currentPrinter.changeDFeedRateMultiplier(now.doubleValue() / 100.0);
                    } catch (PrinterException ex)
                    {
                        steno.error("Error setting feed rate multiplier - " + ex.getMessage());
                    }
                }
            };

    private boolean inhibitBed = false;
    private final ChangeListener<Number> bedTargetTemperatureChangeListener
            = (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) ->
            {
                inhibitBed = true;
                bedTemperatureSlider.setValue(newValue.doubleValue());
                bedDisplay.setText(String.format("%d°C", (int) newValue.doubleValue()));
                inhibitBed = false;
            };

    private final ChangeListener<Number> bedTempSliderListener
            = (ObservableValue<? extends Number> observable, Number was, Number now) ->
            {
                if (!inhibitBed
                && (!bedTemperatureSlider.isValueChanging()
                || now.doubleValue() >= bedTemperatureSlider.getMax()
                || now.doubleValue() <= bedTemperatureSlider.getMin()))
                {
                    currentPrinter.setBedTargetTemperature(now.intValue());
                }
            };

    private boolean inhibitExtrusion1 = false;
    private final ChangeListener<Number> extrusionMultiplier1ChangeListener
            = (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) ->
            {
                inhibitExtrusion1 = true;
                extrusionMultiplier1Slider.setValue(newValue.doubleValue() * 100.0);
                extrusionMultiplier1Display.setText(String.format("%d%%", (int) (newValue.doubleValue() * 100.0)));
                inhibitExtrusion1 = false;
            };

    private final ChangeListener<Number> extrusionMultiplier1SliderListener
            = (ObservableValue<? extends Number> observable, Number was, Number now) ->
            {
                if (!inhibitExtrusion1
                && (!extrusionMultiplier1Slider.isValueChanging()
                || now.doubleValue() >= extrusionMultiplier1Slider.getMax()
                || now.doubleValue() <= extrusionMultiplier1Slider.getMin()))
                {
                    try
                    {
                        currentPrinter.changeFilamentInfo("E", currentPrinter.extrudersProperty().get(0).filamentDiameterProperty().get(), extrusionMultiplier1Slider.valueProperty().doubleValue() / 100.0);
                    } catch (PrinterException ex)
                    {
                        steno.error("Failed to set extrusion multiplier");
                    }
                }
            };

    private boolean inhibitExtrusion2 = false;
    private final ChangeListener<Number> extrusionMultiplier2ChangeListener
            = (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) ->
            {
                inhibitExtrusion2 = true;
                extrusionMultiplier2Slider.setValue(newValue.doubleValue() * 100.0);
                extrusionMultiplier2Display.setText(String.format("%d%%", (int) (newValue.doubleValue() * 100.0)));
                inhibitExtrusion2 = false;
            };

    private final ChangeListener<Number> extrusionMultiplier2SliderListener
            = (ObservableValue<? extends Number> observable, Number was, Number now) ->
            {
                if (!inhibitExtrusion2
                && (!extrusionMultiplier2Slider.isValueChanging()
                || now.doubleValue() >= extrusionMultiplier2Slider.getMax()
                || now.doubleValue() <= extrusionMultiplier2Slider.getMin()))
                {
                    try
                    {
                        currentPrinter.changeFilamentInfo("D", currentPrinter.extrudersProperty().get(1).filamentDiameterProperty().get(), extrusionMultiplier2Slider.valueProperty().doubleValue() / 100.0);
                    } catch (PrinterException ex)
                    {
                        steno.error("Failed to set extrusion multiplier");
                    }
                }
            };

    private boolean inhibitMaterial1Temp = false;
    private final ChangeListener<Number> material1TargetTempChangeListener
            = (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) ->
            {
                inhibitMaterial1Temp = true;
                material1TemperatureSlider.setValue(newValue.intValue());
                material1Display.setText(String.format("%d°C", (int) newValue.doubleValue()));
                inhibitMaterial1Temp = false;
            };

    private final ChangeListener<Number> lastFilamentTempChangeListener
            = (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) ->
            {
                updateTemperatureSliderBounds();
            };

    private final ChangeListener<Number> material1TempSliderListener
            = (ObservableValue<? extends Number> observable, Number was, Number now) ->
            {
                if (!inhibitMaterial1Temp
                && (!material1TemperatureSlider.isValueChanging()
                || now.doubleValue() >= material1TemperatureSlider.getMax()
                || now.doubleValue() <= material1TemperatureSlider.getMin()))
                {
                    if (currentPrinter.headProperty().get().headTypeProperty().get() == Head.HeadType.DUAL_MATERIAL_HEAD)
                    {
                        currentPrinter.setNozzleHeaterTargetTemperature(1, now.intValue());
                    } else
                    {
                        currentPrinter.setNozzleHeaterTargetTemperature(0, now.intValue());
                    }
                }
            };

    private boolean inhibitMaterial2Temp = false;
    private final ChangeListener<Number> nozzleTargetTemp2ChangeListener
            = (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) ->
            {
                inhibitMaterial2Temp = true;
                material2TemperatureSlider.setValue(newValue.intValue());
                material2Display.setText(String.format("%d°C", (int) newValue.doubleValue()));
                inhibitMaterial2Temp = false;
            };

    private final ChangeListener<Number> material2TempSliderListener
            = (ObservableValue<? extends Number> observable, Number was, Number now) ->
            {
                if (!inhibitMaterial2Temp
                && (!material2TemperatureSlider.isValueChanging()
                || now.doubleValue() >= material2TemperatureSlider.getMax()
                || now.doubleValue() <= material2TemperatureSlider.getMin()))
                {
                    currentPrinter.setNozzleHeaterTargetTemperature(0, material2TemperatureSlider.valueProperty().intValue());
                }
            };

    private final ChangeListener<HeaterMode> heaterModeListener
            = (ObservableValue<? extends HeaterMode> observable, HeaterMode oldValue, HeaterMode newValue) ->
            {
                updateMaterialTemperatureDisplay();
            };

    /**
     * Initialises the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        container.setVisible(false);
        Lookup.getSelectedPrinterProperty().addListener(
                (ObservableValue<? extends Printer> observable, Printer oldValue, Printer newPrinter) ->
                {
                    bindToPrintEngineStatus(newPrinter);

                    if (newPrinter == null)
                    {
                        currentPrinter = null;
                    } else
                    {
                        currentPrinter = newPrinter;
                    }
                });

        BaseLookup.getPrinterListChangesNotifier().addListener(this);

        if (Lookup.getSelectedPrinterProperty().get() != null)
        {
            bindToPrintEngineStatus(Lookup.getSelectedPrinterProperty().get());
        }
    }

    private void bindToPrintEngineStatus(Printer printer)
    {
        if (currentPrinter != null)
        {
            currentPrinter.getPrintEngine().printQueueStatusProperty().removeListener(printQueueStatusListener);
            unbind();
        }

        currentPrinter = printer;
        if (currentPrinter != null)
        {
            printer.getPrintEngine().printQueueStatusProperty().addListener(printQueueStatusListener);
            if (printer.getPrintEngine().printQueueStatusProperty().get() == PrintQueueStatus.PRINTING)
            {
                bind();
            }
            BaseLookup.getPrinterListChangesNotifier().addListener(this);

        }
    }

    private boolean eUsedForThisPrintJob()
    {
        boolean eUsed = false;

        if (currentPrintJobStatistics != null)
        {
            eUsed = currentPrintJobStatistics.geteVolumeUsed() > 0;
        }

        return eUsed;
    }

    private boolean dUsedForThisPrintJob()
    {
        boolean dUsed = false;

        if (currentPrintJobStatistics != null)
        {
            dUsed = currentPrintJobStatistics.getdVolumeUsed() > 0;
        }

        return dUsed;
    }

    private void bind()
    {
        updateTemperatureSliderBounds();

        currentPrintJob = currentPrinter.getPrintEngine().printJobProperty().get();
        try
        {
            currentPrintJobStatistics = currentPrintJob.getStatistics();
        } catch (Exception ex)
        {
            steno.debug("Failed to get print job statistics for tweaks page");
        }

        container.setVisible(true);

        if (currentPrinter.getPrinterAncillarySystems().bedHeaterModeProperty().get() == HeaterMode.FIRST_LAYER)
        {
            bedDisplay.setText(String.format("%d°C", (int) currentPrinter.getPrinterAncillarySystems().
                    bedFirstLayerTargetTemperatureProperty().get()));
            bedTemperatureSlider.setValue(currentPrinter.getPrinterAncillarySystems().
                    bedFirstLayerTargetTemperatureProperty().get());
        } else
        {
            bedDisplay.setText(String.format("%d°C", (int) currentPrinter.getPrinterAncillarySystems().
                    bedTargetTemperatureProperty().get()));
            bedTemperatureSlider.setValue(currentPrinter.getPrinterAncillarySystems().
                    bedTargetTemperatureProperty().get());
        }
        bedTemperatureSlider.valueProperty().addListener(bedTempSliderListener);
        currentPrinter.getPrinterAncillarySystems().bedTargetTemperatureProperty().addListener(
                bedTargetTemperatureChangeListener);

        if (currentPrinter.extrudersProperty().get(0).isFittedProperty().get() && eUsedForThisPrintJob())
        {
            //Both sets are visible
            extrusionMultiplier1Display.setText(String.format("%d%%", (int) (currentPrinter.extrudersProperty().get(0).extrusionMultiplierProperty().doubleValue() * 100.0)));
            extrusionMultiplier1Slider.setValue(currentPrinter.extrudersProperty().get(0).extrusionMultiplierProperty().doubleValue() * 100.0);
            extrusionMultiplier1Slider.valueProperty().addListener(extrusionMultiplier1SliderListener);
            currentPrinter.extrudersProperty().get(0).extrusionMultiplierProperty().addListener(extrusionMultiplier1ChangeListener);
            material1VBox.setVisible(true);
            material1VBox.setMaxHeight(1000);
            material1VBox.setMinHeight(0);

            printSpeed1Display.setText(String.format("%d%%", (int) (currentPrinter.getPrinterAncillarySystems().feedRateEMultiplierProperty().get() * 100.0)));
            speedMultiplier1Slider.setValue(currentPrinter.getPrinterAncillarySystems().feedRateEMultiplierProperty().get() * 100.0);
            speedMultiplier1Slider.valueProperty().addListener(speedMultiplier1SliderListener);
            currentPrinter.getPrinterAncillarySystems().feedRateEMultiplierProperty().addListener(
                    feedRate1ChangeListener);
        } else
        {
            material1VBox.setVisible(false);
            material1VBox.setMaxHeight(0);
            material1VBox.setMinHeight(0);
        }

        if (currentPrinter.extrudersProperty().get(1).isFittedProperty().get() && dUsedForThisPrintJob())
        {
            extrusionMultiplier2Display.setText(String.format("%d%%", (int) (currentPrinter.extrudersProperty().get(1).extrusionMultiplierProperty().doubleValue() * 100.0)));
            extrusionMultiplier2Slider.setValue(currentPrinter.extrudersProperty().get(1).extrusionMultiplierProperty().doubleValue() * 100.0);
            extrusionMultiplier2Slider.valueProperty().addListener(extrusionMultiplier2SliderListener);
            currentPrinter.extrudersProperty().get(1).extrusionMultiplierProperty().addListener(extrusionMultiplier2ChangeListener);
            material2VBox.setVisible(true);
            material2VBox.setMaxHeight(1000);
            material2VBox.setMinHeight(0);

            printSpeed2Display.setText(String.format("%d%%", (int) (currentPrinter.getPrinterAncillarySystems().feedRateDMultiplierProperty().get() * 100.0)));
            speedMultiplier2Slider.setValue(currentPrinter.getPrinterAncillarySystems().feedRateDMultiplierProperty().get() * 100.0);
            speedMultiplier2Slider.valueProperty().addListener(speedMultiplier2SliderListener);
            currentPrinter.getPrinterAncillarySystems().feedRateDMultiplierProperty().addListener(
                    feedRate2ChangeListener);
        } else
        {
            material2VBox.setVisible(false);
            material2VBox.setMaxHeight(0);
            material2VBox.setMinHeight(0);
        }

        bindNozzleTemperatureDisplay();
        updateMaterialTemperatureDisplay();
        container.setVisible(true);
    }

    private void unbind()
    {
        container.setVisible(false);

        speedMultiplier1Slider.valueProperty().removeListener(speedMultiplier1SliderListener);
        speedMultiplier2Slider.valueProperty().removeListener(speedMultiplier2SliderListener);
        bedTemperatureSlider.valueProperty().removeListener(bedTempSliderListener);
        extrusionMultiplier1Slider.valueProperty().removeListener(extrusionMultiplier1SliderListener);
        extrusionMultiplier2Slider.valueProperty().removeListener(extrusionMultiplier2SliderListener);

        currentPrinter.getPrinterAncillarySystems().feedRateEMultiplierProperty().removeListener(
                feedRate1ChangeListener);
        currentPrinter.getPrinterAncillarySystems().feedRateDMultiplierProperty().removeListener(
                feedRate2ChangeListener);

        currentPrinter.getPrinterAncillarySystems().bedTargetTemperatureProperty().removeListener(
                bedTargetTemperatureChangeListener);
        if (currentPrinter.extrudersProperty().get(0).isFittedProperty().get())
        {
            currentPrinter.extrudersProperty().get(0).extrusionMultiplierProperty().removeListener(
                    extrusionMultiplier1ChangeListener);
        }
        if (currentPrinter.extrudersProperty().get(1).isFittedProperty().get())
        {
            currentPrinter.extrudersProperty().get(1).extrusionMultiplierProperty().removeListener(
                    extrusionMultiplier2ChangeListener);
        }

        unbindMaterialTemperatureDisplay();
        updateMaterialTemperatureDisplay();
    }

    private void updateMaterialTemperatureDisplay()
    {
        if (currentPrinter != null
                && currentPrinter.headProperty().get() != null)
        {
            if (currentPrinter.headProperty().get().headTypeProperty().get() == Head.HeadType.SINGLE_MATERIAL_HEAD)
            {
                if (currentPrinter.headProperty().get().getNozzleHeaters().get(0).heaterModeProperty().get() == HeaterMode.FIRST_LAYER)
                {
                    material1Display.setText(String.format("%d°C", (int) currentPrinter.headProperty().get().getNozzleHeaters().get(0).nozzleFirstLayerTargetTemperatureProperty().doubleValue()));
                    material1TemperatureSlider.setValue(currentPrinter.headProperty().get().getNozzleHeaters().get(0).nozzleFirstLayerTargetTemperatureProperty().get());
                } else
                {
                    material1Display.setText(String.format("%d°C", (int) currentPrinter.headProperty().get().getNozzleHeaters().get(0).nozzleTargetTemperatureProperty().doubleValue()));
                    inhibitMaterial1Temp = true;
                    material1TemperatureSlider.setValue(currentPrinter.headProperty().get().getNozzleHeaters().get(0).nozzleTargetTemperatureProperty().get());
                    inhibitMaterial1Temp = false;
                }
            } else
            {
                if (dUsedForThisPrintJob())
                {
                    if (currentPrinter.headProperty().get().getNozzleHeaters().get(0).heaterModeProperty().get() == HeaterMode.FIRST_LAYER)
                    {
                        material2Display.setText(String.format("%d°C", (int) currentPrinter.headProperty().get().getNozzleHeaters().get(0).nozzleFirstLayerTargetTemperatureProperty().doubleValue()));
                        inhibitMaterial2Temp = true;
                        material2TemperatureSlider.setValue(currentPrinter.headProperty().get().getNozzleHeaters().get(0).nozzleFirstLayerTargetTemperatureProperty().get());
                        inhibitMaterial2Temp = false;
                    } else
                    {
                        material2Display.setText(String.format("%d°C", (int) currentPrinter.headProperty().get().getNozzleHeaters().get(0).nozzleTargetTemperatureProperty().doubleValue()));
                        inhibitMaterial2Temp = true;
                        material2TemperatureSlider.setValue(currentPrinter.headProperty().get().getNozzleHeaters().get(0).nozzleTargetTemperatureProperty().get());
                        inhibitMaterial2Temp = false;
                    }
                }

                if (eUsedForThisPrintJob())
                {
                    if (currentPrinter.headProperty().get().getNozzleHeaters().get(1).heaterModeProperty().get() == HeaterMode.FIRST_LAYER)
                    {
                        material1Display.setText(String.format("%d°C", (int) currentPrinter.headProperty().get().getNozzleHeaters().get(1).nozzleFirstLayerTargetTemperatureProperty().doubleValue()));
                        inhibitMaterial1Temp = true;
                        material1TemperatureSlider.setValue(currentPrinter.headProperty().get().getNozzleHeaters().get(1).nozzleFirstLayerTargetTemperatureProperty().get());
                        inhibitMaterial1Temp = false;
                    } else
                    {
                        material1Display.setText(String.format("%d°C", (int) currentPrinter.headProperty().get().getNozzleHeaters().get(1).nozzleTargetTemperatureProperty().doubleValue()));
                        inhibitMaterial1Temp = true;
                        material1TemperatureSlider.setValue(currentPrinter.headProperty().get().getNozzleHeaters().get(1).nozzleTargetTemperatureProperty().get());
                        inhibitMaterial1Temp = false;
                    }
                }
            }
        }
    }

    private void bindNozzleTemperatureDisplay()
    {
        unbindMaterialTemperatureDisplay();

        if (currentPrinter != null
                && currentPrinter.headProperty().get() != null)
        {
            material1TemperatureSlider.valueProperty().addListener(material1TempSliderListener);
            if (currentPrinter.headProperty().get().headTypeProperty().get() == Head.HeadType.DUAL_MATERIAL_HEAD)
            {
                currentPrinter.headProperty().get().getNozzleHeaters().get(1).nozzleFirstLayerTargetTemperatureProperty().addListener(material1TargetTempChangeListener);
                currentPrinter.headProperty().get().getNozzleHeaters().get(1).nozzleTargetTemperatureProperty().addListener(material1TargetTempChangeListener);
                currentPrinter.headProperty().get().getNozzleHeaters().get(1).heaterModeProperty().addListener(heaterModeListener);
                currentPrinter.headProperty().get().getNozzleHeaters().get(1).lastFilamentTemperatureProperty().addListener(lastFilamentTempChangeListener);
            } else if (currentPrinter.headProperty().get().getNozzleHeaters().size() > 0)
            {
                currentPrinter.headProperty().get().getNozzleHeaters().get(0).nozzleFirstLayerTargetTemperatureProperty().addListener(material1TargetTempChangeListener);
                currentPrinter.headProperty().get().getNozzleHeaters().get(0).nozzleTargetTemperatureProperty().addListener(material1TargetTempChangeListener);
                currentPrinter.headProperty().get().getNozzleHeaters().get(0).heaterModeProperty().addListener(heaterModeListener);
                currentPrinter.headProperty().get().getNozzleHeaters().get(0).lastFilamentTemperatureProperty().addListener(lastFilamentTempChangeListener);
            }

            if (currentPrinter.headProperty().get().getNozzleHeaters().size() > 1)
            {
                material2TemperatureSlider.valueProperty().addListener(material2TempSliderListener);
                currentPrinter.headProperty().get().getNozzleHeaters().get(0).nozzleFirstLayerTargetTemperatureProperty().addListener(nozzleTargetTemp2ChangeListener);
                currentPrinter.headProperty().get().getNozzleHeaters().get(0).nozzleTargetTemperatureProperty().addListener(nozzleTargetTemp2ChangeListener);
                currentPrinter.headProperty().get().getNozzleHeaters().get(0).heaterModeProperty().addListener(heaterModeListener);
                currentPrinter.headProperty().get().getNozzleHeaters().get(0).lastFilamentTemperatureProperty().addListener(lastFilamentTempChangeListener);
            }
            material1Text.setText(Lookup.i18n("printAdjustments.nozzleTemperature"));
        }
    }

    private void unbindMaterialTemperatureDisplay()
    {
        material1TemperatureSlider.valueProperty().removeListener(material1TempSliderListener);
        material2TemperatureSlider.valueProperty().removeListener(material2TempSliderListener);

        if (currentPrinter != null
                && currentPrinter.headProperty().get() != null)
        {
            if (currentPrinter.headProperty().get().headTypeProperty().get() == Head.HeadType.DUAL_MATERIAL_HEAD)
            {
                currentPrinter.headProperty().get().getNozzleHeaters().get(1).nozzleFirstLayerTargetTemperatureProperty().removeListener(material1TargetTempChangeListener);
                currentPrinter.headProperty().get().getNozzleHeaters().get(1).nozzleTargetTemperatureProperty().removeListener(material1TargetTempChangeListener);
                currentPrinter.headProperty().get().getNozzleHeaters().get(1).heaterModeProperty().removeListener(heaterModeListener);
                currentPrinter.headProperty().get().getNozzleHeaters().get(1).lastFilamentTemperatureProperty().removeListener(lastFilamentTempChangeListener);
            } else if (currentPrinter.headProperty().get().getNozzleHeaters().size() > 0)
            {
                currentPrinter.headProperty().get().getNozzleHeaters().get(0).nozzleFirstLayerTargetTemperatureProperty().removeListener(material1TargetTempChangeListener);
                currentPrinter.headProperty().get().getNozzleHeaters().get(0).nozzleTargetTemperatureProperty().removeListener(material1TargetTempChangeListener);
                currentPrinter.headProperty().get().getNozzleHeaters().get(0).heaterModeProperty().removeListener(heaterModeListener);
                currentPrinter.headProperty().get().getNozzleHeaters().get(0).lastFilamentTemperatureProperty().removeListener(lastFilamentTempChangeListener);
            }

            if (currentPrinter.headProperty().get().getNozzleHeaters().size() > 1)
            {
                currentPrinter.headProperty().get().getNozzleHeaters().get(0).nozzleFirstLayerTargetTemperatureProperty().removeListener(nozzleTargetTemp2ChangeListener);
                currentPrinter.headProperty().get().getNozzleHeaters().get(0).nozzleTargetTemperatureProperty().removeListener(nozzleTargetTemp2ChangeListener);
                currentPrinter.headProperty().get().getNozzleHeaters().get(0).heaterModeProperty().removeListener(heaterModeListener);
                currentPrinter.headProperty().get().getNozzleHeaters().get(0).lastFilamentTemperatureProperty().removeListener(lastFilamentTempChangeListener);
            }
        }
    }

    private final int tempSliderTolerance = 15;

    private void updateTemperatureSliderBounds()
    {
        if (currentPrinter != null
                && currentPrinter.headProperty().get() != null)
        {
            for (int nozzleNumber = 0; nozzleNumber < currentPrinter.headProperty().get().getNozzleHeaters().size(); nozzleNumber++)
            {
                float lastFilamentTemp = currentPrinter.headProperty().get().getNozzleHeaters().get(nozzleNumber).lastFilamentTemperatureProperty().get();

                if (nozzleNumber == 0
                        && currentPrinter.headProperty().get().headTypeProperty().get() == Head.HeadType.DUAL_MATERIAL_HEAD)
                {
                    inhibitMaterial2Temp = true;
                    material2TemperatureSlider.setMax(lastFilamentTemp + tempSliderTolerance);
                    material2TemperatureSlider.setMin(lastFilamentTemp - tempSliderTolerance);
                    inhibitMaterial2Temp = false;
                } else
                {
                    inhibitMaterial1Temp = true;
                    material1TemperatureSlider.setMax(lastFilamentTemp + tempSliderTolerance);
                    material1TemperatureSlider.setMin(lastFilamentTemp - tempSliderTolerance);
                    inhibitMaterial1Temp = false;
                }
            }
        }
    }

    @Override
    public void whenPrinterAdded(Printer printer)
    {
    }

    @Override
    public void whenPrinterRemoved(Printer printer)
    {
    }

    @Override
    public void whenHeadAdded(Printer printer)
    {
        bindNozzleTemperatureDisplay();
        updateMaterialTemperatureDisplay();
        updateTemperatureSliderBounds();
    }

    @Override
    public void whenHeadRemoved(Printer printer, Head head)
    {
        unbindMaterialTemperatureDisplay();
        updateMaterialTemperatureDisplay();
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
