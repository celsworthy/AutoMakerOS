package celtech.roboxremote.rootDataStructures;

import celtech.roboxbase.configuration.Filament;
import celtech.roboxbase.configuration.datafileaccessors.FilamentContainer;
import celtech.roboxbase.postprocessor.PrintJobStatistics;
import celtech.roboxbase.printerControl.PrintJob;
import celtech.roboxbase.printerControl.model.Head;
import celtech.roboxbase.printerControl.model.HeaterMode;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxremote.PrinterRegistry;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author ianhudson
 */
public class PrintAdjustData
{
    private String printerID;

    // Head
    private boolean dualMaterialHead = false;
    private boolean printingFirstLayer = false;
    
    private int nNozzleHeaters = 0;
    private float rightNozzleTargetTemp = 0.0F;
    private float leftNozzleTargetTemp = 0.0F;

    // Bed
    private float bedTargetTemp = 0.0F;

    // Material
    private int nMaterials = 0;
    private boolean usingMaterial1 = true;
    private boolean usingMaterial2 = true;
    private String material1Name = "";
    private String material2Name = "";
    private float leftFeedRateMultiplier = 0.0F;
    private float rightFeedRateMultiplier = 0.0F;
    private float leftExtrusionRateMultiplier = 0.0F;
    private float rightExtrusionRateMultiplier = 0.0F;
    
    public PrintAdjustData()
    {
        // Jackson deserialization
    }

    public void updateFromPrinterData(String printerID)
    {
        this.printerID = printerID;
        Printer printer = PrinterRegistry.getInstance().getRemotePrinters().get(printerID);
        PrintJob currentPrintJob = printer.getPrintEngine().printJobProperty().get();
        PrintJobStatistics currentPrintJobStatistics = null;
        Head head = printer.headProperty().get();
        dualMaterialHead = ((head != null) && (head.headTypeProperty().get() == Head.HeadType.DUAL_MATERIAL_HEAD));

        try
        {
            currentPrintJobStatistics = currentPrintJob.getStatistics();
            if (currentPrintJobStatistics != null)
            {
                usingMaterial1 = (currentPrintJobStatistics.geteVolumeUsed() > 0);
                usingMaterial2 = (dualMaterialHead && currentPrintJobStatistics.getdVolumeUsed() > 0);
            }
        } catch (Exception ex)
        {
//            steno.error("Failed to get print job statistics for tweaks page");
        }
        
        // Materials - need to know if the head is a dual material head.
        
        nMaterials = 1;
        if (printer.extrudersProperty().size() == 2
                && printer.extrudersProperty().get(1) != null
                && printer.extrudersProperty().get(1).isFittedProperty().get())
        {
            nMaterials = 2;
        }
        
        if (nMaterials > 0 && usingMaterial1)
        {
            if (printer.effectiveFilamentsProperty().get(0) != FilamentContainer.UNKNOWN_FILAMENT)
            {
                Filament filament = printer.effectiveFilamentsProperty().get(0);
                material1Name = filament.getFriendlyFilamentName();
            }
            if (printer.extrudersProperty().get(0).isFittedProperty().get())
            {
                rightExtrusionRateMultiplier = printer.extrudersProperty().get(0).extrusionMultiplierProperty().floatValue() * 100.0F;
                rightFeedRateMultiplier = printer.getPrinterAncillarySystems().feedRateEMultiplierProperty().floatValue() * 100.0F;
            }
        }
        if (nMaterials > 1 && usingMaterial2)
        {
            if (printer.effectiveFilamentsProperty().get(1) != FilamentContainer.UNKNOWN_FILAMENT)
            {
                Filament filament = printer.effectiveFilamentsProperty().get(1);
                material2Name = filament.getFriendlyFilamentName();
            }
            if (printer.extrudersProperty().get(1).isFittedProperty().get())
            {
                leftExtrusionRateMultiplier = printer.extrudersProperty().get(1).extrusionMultiplierProperty().floatValue() * 100.0F;
                leftFeedRateMultiplier = printer.getPrinterAncillarySystems().feedRateDMultiplierProperty().floatValue() * 100.0F;
            }
        }

        //Head
        if (head != null)
        {
            printingFirstLayer = (head.getNozzleHeaters().get(0).heaterModeProperty().get() == HeaterMode.FIRST_LAYER);

            nNozzleHeaters = head.getNozzleHeaters().size();
            rightNozzleTargetTemp = 0.0F;
            leftNozzleTargetTemp = 0.0F;
            if (nNozzleHeaters == 1)
            {
                if (printingFirstLayer)
                    rightNozzleTargetTemp = head.getNozzleHeaters().get(0).nozzleFirstLayerTargetTemperatureProperty().floatValue();
                else
                    rightNozzleTargetTemp = head.getNozzleHeaters().get(0).nozzleTargetTemperatureProperty().floatValue();
            }
            if (nNozzleHeaters > 1)
            {
                if (printingFirstLayer)
                {
                    rightNozzleTargetTemp = head.getNozzleHeaters().get(1).nozzleFirstLayerTargetTemperatureProperty().floatValue();
                    leftNozzleTargetTemp = head.getNozzleHeaters().get(0).nozzleFirstLayerTargetTemperatureProperty().floatValue();
                }
                else
                {
                    rightNozzleTargetTemp = head.getNozzleHeaters().get(1).nozzleTargetTemperatureProperty().floatValue();
                    leftNozzleTargetTemp = head.getNozzleHeaters().get(0).nozzleTargetTemperatureProperty().floatValue();
                }
            }
        }
        
        bedTargetTemp = printer.getPrinterAncillarySystems().bedTargetTemperatureProperty().get();
    }

    @JsonProperty
    public String getPrinterID()
    {
        return printerID;
    }

    @JsonProperty
    public void setPrinterID(String printerID)
    {
        this.printerID = printerID;
    }

    @JsonProperty
    public boolean getDualMaterialHead()
    {
        return dualMaterialHead;
    }

    @JsonProperty
    public void setDualMaterialHead(boolean dualMaterialHead)
    {
        this.dualMaterialHead = dualMaterialHead;
    }

    @JsonProperty
    public boolean getUsingMaterial1()
    {
        return usingMaterial1;
    }

    @JsonProperty
    public void getUsingMaterial1(boolean usingMaterial)
    {
        this.usingMaterial1 = usingMaterial;
    }

    @JsonProperty
    public boolean getUsingMaterial2()
    {
        return usingMaterial2;
    }

    @JsonProperty
    public void getUsingMaterial2(boolean usingMaterial)
    {
        this.usingMaterial2 = usingMaterial;
    }

    @JsonProperty
    public boolean getPrintingFirstLayer()
    {
        return printingFirstLayer;
    }

    @JsonProperty
    public void setPrintingFirstLayer(boolean printingFirstLayer)
    {
        this.printingFirstLayer = printingFirstLayer;
    }

    @JsonProperty
    public float getBedTargetTemp()
    {
        return bedTargetTemp;
    }

    @JsonProperty
    public void setBedTargetTemp(float bedTargetTemp)
    {
        this.bedTargetTemp = bedTargetTemp;
    }

    @JsonProperty
    public float getRightNozzleTargetTemp()
    {
        return rightNozzleTargetTemp;
    }

    @JsonProperty
    public void setRightNozzleTargetTemp(float targetTemp)
    {
        this.rightNozzleTargetTemp = targetTemp;
    }
    
    @JsonProperty
    public float getLeftNozzleTargetTemp()
    {
        return leftNozzleTargetTemp;
    }

    @JsonProperty
    public void setLeftNozzleTargetTemp(float targetTemp)
    {
        this.leftNozzleTargetTemp = targetTemp;
    }

    @JsonProperty
    public String getMaterial1Name()
    {
        return material1Name;
    }

    @JsonProperty
    public void setMaterial1Name(String materialName)
    {
        this.material1Name = materialName;
    }

    @JsonProperty
    public String getMaterial2Name()
    {
        return material2Name;
    }

    @JsonProperty
    public void setMaterial2Name(String materialName)
    {
        this.material2Name = materialName;
    }
    
    @JsonProperty
    public float getLeftExtrusionRateMultiplier()
    {
        return leftExtrusionRateMultiplier;
    }

    @JsonProperty
    public void setLeftExtrusionRateMultiplier(float extrusionRateMultiplier)
    {
        this.leftExtrusionRateMultiplier = extrusionRateMultiplier;
    }

    @JsonProperty
    public float getRightExtrusionRateMultiplier()
    {
        return rightExtrusionRateMultiplier;
    }

    @JsonProperty
    public void setRightExtrusionRateMultiplier(float extrusionRateMultiplier)
    {
        this.rightExtrusionRateMultiplier = extrusionRateMultiplier;
    }

    @JsonProperty
    public float getLeftFeedRateMultiplier()
    {
        return leftFeedRateMultiplier;
    }

    @JsonProperty
    public void setLeftFeedRateMultiplier(float feedRateMultiplier)
    {
        this.leftFeedRateMultiplier = feedRateMultiplier;
    }

    @JsonProperty
    public float getRightFeedRateMultiplier()
    {
        return rightFeedRateMultiplier;
    }

    @JsonProperty
    public void setRightFeedRateMultiplier(float feedRateMultiplier)
    {
        this.rightFeedRateMultiplier = feedRateMultiplier;
    }
}
