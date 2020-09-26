package celuk.groot.remote;

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
    
    private float rightNozzleTargetTemp = 0.0F;
    private float leftNozzleTargetTemp = 0.0F;

    // Bed
    private float bedTargetTemp = 0.0F;

    // Material
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
