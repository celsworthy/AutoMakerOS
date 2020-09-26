package celtech.roboxbase.configuration.fileRepresentation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 *
 * @author tony aldhous
 */

public class StylusSettings 
{
    private String name = "";
    private boolean hasDragKnife = false;
    private double dragKnifeRadius = 0.2;
    private double overcut = 0.0;
    private int passes = 1;
    private double xOffset = 0.0;
    private double yOffset = 0.0;
    private double zOffset = 0.0;
    private final BooleanProperty dataChanged = new SimpleBooleanProperty(false);
    private boolean modified = false;
    private boolean readOnly = false;

    public StylusSettings()
    {
    }

    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public boolean getHasDragKnife()
    {
        return hasDragKnife;
    }

    public void setHasDragKnife(boolean hasDragKnife)
    {
        if (this.hasDragKnife != hasDragKnife)
        {
            this.hasDragKnife = hasDragKnife;
            toggleDataChanged();
        }
    }

    public double getDragKnifeRadius()
    {
        return dragKnifeRadius;
    }

    public void setDragKnifeRadius(double dragKnifeRadius)
    {
        if (this.dragKnifeRadius != dragKnifeRadius)
        {
            this.dragKnifeRadius = dragKnifeRadius;
            toggleDataChanged();
        }
    }

    public double getOvercut()
    {
        return overcut;
    }

    public void setOvercut(double overcut)
    {
        if (this.overcut != overcut)
        {
            this.overcut = overcut;
            toggleDataChanged();
        }
    }

    public int getPasses()
    {
        return passes;
    }

    public void setPasses(int passes)
    {
        if (this.passes != passes)
        {
            this.passes = passes;
            toggleDataChanged();
        }
    }

    @JsonProperty("xOffset")
    public double getXOffset()
    {
        return xOffset;
    }

    @JsonProperty("xOffset")
    public void setXOffset(double xOffset)
    {
        if (this.xOffset != xOffset)
        {
            this.xOffset = xOffset;
            toggleDataChanged();
        }
    }

    @JsonProperty("yOffset")
    public double getYOffset()
    {
        return yOffset;
    }

    @JsonProperty("yOffset")
    public void setYOffset(double yOffset)
    {
        if (this.yOffset != yOffset)
        {
            this.yOffset = yOffset;
            toggleDataChanged();
        }
    }

    @JsonProperty("zOffset")
    public double getZOffset()
    {
        return zOffset;
    }

    @JsonProperty("zOffset")
    public void setZOffset(double zOffset)
    {
        if (this.zOffset != zOffset)
        {
            this.zOffset = zOffset;
            toggleDataChanged();
        }
    }

    @JsonIgnore
    public void setOffsets(double xOffset, double yOffset, double zOffset)
    {
        if (this.xOffset != xOffset ||
            this.yOffset != yOffset ||
            this.zOffset != zOffset)
        {
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            this.zOffset = zOffset;
            toggleDataChanged();
        }
    }
    
    @JsonIgnore
    public void setFrom(StylusSettings source)
    {
        this.name = source.name;
        this.hasDragKnife = source.hasDragKnife;
        this.dragKnifeRadius = source.dragKnifeRadius;
        this.overcut = source.overcut;
        this.passes = source.passes;
        this.xOffset = source.xOffset;
        this.yOffset = source.yOffset;
        this.zOffset = source.zOffset;
        toggleDataChanged();
    }
    
    @Override
    public String toString()
    {
        return name;
    }
    
        @JsonIgnore
    public boolean isModified()
    {
        return modified;
    }

    @JsonIgnore
    public void setReadOnly(boolean readOnly)
    {
        this.readOnly = readOnly;
    }

    @JsonIgnore
    public boolean isReadOnly()
    {
        return readOnly;
    }

    @JsonIgnore
    public void clearModified()
    {
        modified = false;
    }

    @JsonIgnore
    public void setModified()
    {
        modified = true;
    }

    @JsonIgnore
    public ReadOnlyBooleanProperty getDataChanged()
    {
        return dataChanged;
    }

    private void toggleDataChanged()
    {
        modified = true;
        dataChanged.set(dataChanged.not().get());
    }
}
