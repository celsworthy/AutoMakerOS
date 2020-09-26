package celtech.roboxremote.rootDataStructures;

import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.PrinterColourMap;
import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.configuration.Filament;
import celtech.roboxbase.configuration.datafileaccessors.FilamentContainer;
import celtech.roboxbase.postprocessor.PrintJobStatistics;
import celtech.roboxbase.printerControl.PrinterStatus;
import celtech.roboxbase.printerControl.model.Head;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.utils.ColourStringConverter;
import celtech.roboxremote.PrinterRegistry;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.IOException;
import javafx.scene.paint.Color;

/**
 *
 * @author taldhous
 */
public class HeadStatusData
{

    private String printerID;

    //Head
    private String headName;
    private String headTypeCode;
    private boolean dualMaterialHead;
    private boolean valvesFitted;
    private int nozzleCount;
    private int[] nozzleTemperature;

    private boolean canCalibrateHead;
    private boolean canPurgeHead;
    private boolean canRemoveHead;
 
    //Bed
    private int bedTemperature;

    // Ambient
    private int ambientTemperature;

    public HeadStatusData()
    {
        // Jackson deserialization
    }

    @JsonIgnore
    public void updateFromPrinterData(String printerID)
    {
        this.printerID = printerID;
        Printer printer = PrinterRegistry.getInstance().getRemotePrinters().get(printerID);

        canCalibrateHead = printer.canCalibrateHeadProperty().get();
        canRemoveHead = printer.canRemoveHeadProperty().get();
     
        //Head
        Head printerHead = printer.headProperty().get();
        if (printerHead != null)
        {
            headName = printerHead.nameProperty().get();
            headTypeCode = printerHead.typeCodeProperty().get().trim();
            dualMaterialHead = printerHead.headTypeProperty().get() == Head.HeadType.DUAL_MATERIAL_HEAD;
            valvesFitted = printerHead.valveTypeProperty().get() == Head.ValveType.FITTED;

            if (dualMaterialHead)
            {
                canPurgeHead = printer.reelsProperty().containsKey(0) && printer.reelsProperty().containsKey(1) && printer.canPurgeHeadProperty().get();
            } else
            {
                canPurgeHead = printer.reelsProperty().containsKey(0) && printer.canPurgeHeadProperty().get();
            }

            nozzleCount = printerHead.getNozzles().size();
            nozzleTemperature = new int[printerHead.getNozzleHeaters().size()];
            for (int heaterNumber = 0; heaterNumber < printerHead.getNozzleHeaters().size(); heaterNumber++)
            {
                nozzleTemperature[heaterNumber] = printerHead.getNozzleHeaters().get(heaterNumber).nozzleTemperatureProperty().get();
            }
        } else
        {
            headName = "";
            headTypeCode = "";
            canPurgeHead = false;
            dualMaterialHead = false;
            valvesFitted = false;
            nozzleCount = 0;
            nozzleTemperature = null;
        }

        bedTemperature = printer.getPrinterAncillarySystems().bedTemperatureProperty().get();

        ambientTemperature = printer.getPrinterAncillarySystems().ambientTemperatureProperty().get();
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
    public boolean isCanCalibrateHead()
    {
        return canCalibrateHead;
    }

    @JsonProperty
    public void setCanCalibrateHead(boolean canCalibrateHead)
    {
        this.canCalibrateHead = canCalibrateHead;
    }

    @JsonProperty
    public boolean isCanPurgeHead()
    {
        return canPurgeHead;
    }

    @JsonProperty
    public void setCanPurgeHead(boolean canPurgeHead)
    {
        this.canPurgeHead = canPurgeHead;
    }

    @JsonProperty
    public boolean isCanRemoveHead()
    {
        return canRemoveHead;
    }

    @JsonProperty
    public void setCanRemoveHead(boolean canRemoveHead)
    {
        this.canRemoveHead = canRemoveHead;
    }

    @JsonProperty
    public String getHeadName()
    {
        return headName;
    }

    @JsonProperty
    public void setHeadName(String headName)
    {
        this.headName = headName;
    }

    @JsonProperty
    public String getHeadTypeCode()
    {
        return headTypeCode;
    }

    @JsonProperty
    public void setHeadTypeCode(String headTypeCode)
    {
        this.headTypeCode = headTypeCode;
    }
    
    @JsonProperty
    public boolean isDualMaterialHead()
    {
        return dualMaterialHead;
    }

    @JsonProperty
    public void setDualMaterialHead(boolean dualMaterialHead)
    {
        this.dualMaterialHead = dualMaterialHead;
    }

    @JsonProperty
    public boolean isValvesFitted()
    {
        return valvesFitted;
    }

    @JsonProperty
    public void setValvesFitted(boolean valvesFitted)
    {
        this.valvesFitted = valvesFitted;
    }

    @JsonProperty
    public int getBedTemperature()
    {
        return bedTemperature;
    }

    @JsonProperty
    public void setBedTemperature(int bedTemperature)
    {
        this.bedTemperature = bedTemperature;
    }

    @JsonProperty
    public int getAmbientTemperature()
    {
        return ambientTemperature;
    }

    @JsonProperty
    public void setAmbientTemperature(int ambientTemperature)
    {
        this.ambientTemperature = ambientTemperature;
    }

    @JsonProperty
    public int getNozzleCount()
    {
        return nozzleCount;
    }

    @JsonProperty
    public void getNozzleCount(int nozzleCount)
    {
        this.nozzleCount = nozzleCount;
    }

    @JsonProperty
    public int[] getNozzleTemperature()
    {
        return nozzleTemperature;
    }

    @JsonProperty
    public void setNozzleTemperature(int[] nozzleTemperature)
    {
        this.nozzleTemperature = nozzleTemperature;
    }

    @JsonIgnore
    public void setNozzleTemperature(int nozzleIndex, int newNozzleTemperature)
    {
        if (nozzleIndex >= 0
                && nozzleIndex < nozzleTemperature.length)
        {
            nozzleTemperature[nozzleIndex] = newNozzleTemperature;
        }
    }
}
