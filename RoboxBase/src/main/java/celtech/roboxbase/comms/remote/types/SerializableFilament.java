package celtech.roboxbase.comms.remote.types;

import celtech.roboxbase.MaterialType;
import celtech.roboxbase.configuration.Filament;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.scene.paint.Color;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 *
 * @author Ian
 */
public class SerializableFilament
{

    private String friendlyFilamentName;
    private MaterialType material;
    private String reelID;
    private String brand;
    private String category;
    private float diameter;
    private float filamentMultiplier;
    private float feedRateMultiplier;
    private int requiredAmbientTemperature;
    private int requiredFirstLayerBedTemperature;
    private int requiredBedTemperature;
    private int requiredFirstLayerNozzleTemperature;
    private int requiredNozzleTemperature;
    private String webDisplayColour;
    private float costGBPPerKG;
    private int defaultLength_m;
    private boolean filled;
    private boolean mutable;

    public SerializableFilament()
    {
    }

    public SerializableFilament(Filament filament)
    {
        this.friendlyFilamentName = filament.getFriendlyFilamentName();
        this.material = filament.getMaterial();
        this.reelID = filament.getFilamentID();
        this.brand = filament.getBrand();
        this.category = filament.getCategory();
        this.diameter = filament.getDiameter();
        this.filamentMultiplier = filament.getFilamentMultiplier();
        this.feedRateMultiplier = filament.getFeedRateMultiplier();
        this.requiredAmbientTemperature = filament.getAmbientTemperature();
        this.requiredFirstLayerBedTemperature = filament.getFirstLayerBedTemperature();
        this.requiredBedTemperature = filament.getBedTemperature();
        this.requiredFirstLayerNozzleTemperature = filament.getFirstLayerNozzleTemperature();
        this.requiredNozzleTemperature = filament.getNozzleTemperature();
        this.webDisplayColour = filament.getDisplayColour().toString();
        this.costGBPPerKG = filament.getCostGBPPerKG();
        this.defaultLength_m = filament.getDefaultLength_m();
        this.filled = filament.isFilled();
        this.mutable = filament.isMutable();
    }

    public SerializableFilament(String friendlyFilamentName,
            MaterialType material,
            String reelID,
            String brand,
            String category,
            float diameter,
            float filamentMultiplier,
            float feedRateMultiplier,
            int requiredAmbientTemperature,
            int requiredFirstLayerBedTemperature,
            int requiredBedTemperature,
            int requiredFirstLayerNozzleTemperature,
            int requiredNozzleTemperature,
            String webDisplayColour,
            float costGBPPerKG,
            int defaultLength_m,
            boolean filled,
            boolean mutable)
    {
        this.friendlyFilamentName = friendlyFilamentName;
        this.material = material;
        this.reelID = reelID;
        this.brand = brand;
        this.category = category;
        this.diameter = diameter;
        this.filamentMultiplier = filamentMultiplier;
        this.feedRateMultiplier = feedRateMultiplier;
        this.requiredAmbientTemperature = requiredAmbientTemperature;
        this.requiredFirstLayerBedTemperature = requiredFirstLayerBedTemperature;
        this.requiredBedTemperature = requiredBedTemperature;
        this.requiredFirstLayerNozzleTemperature = requiredFirstLayerNozzleTemperature;
        this.requiredNozzleTemperature = requiredNozzleTemperature;
        this.webDisplayColour = webDisplayColour;
        this.costGBPPerKG = costGBPPerKG;
        this.defaultLength_m = defaultLength_m;
        this.filled = filled;
        this.mutable = mutable;
    }

    public String getFriendlyFilamentName()
    {
        return friendlyFilamentName;
    }

    public void setFriendlyFilamentName(String friendlyFilamentName)
    {
        this.friendlyFilamentName = friendlyFilamentName;
    }

    public MaterialType getMaterial()
    {
        return material;
    }

    public void setMaterial(MaterialType material)
    {
        this.material = material;
    }

    public String getReelID()
    {
        return reelID;
    }

    public void setReelID(String reelID)
    {
        this.reelID = reelID;
    }

    public String getBrand()
    {
        return brand;
    }

    public void setBrand(String brand)
    {
        this.brand = brand;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public float getDiameter()
    {
        return diameter;
    }

    public void setDiameter(float diameter)
    {
        this.diameter = diameter;
    }

    public float getFilamentMultiplier()
    {
        return filamentMultiplier;
    }

    public void setFilamentMultiplier(float filamentMultiplier)
    {
        this.filamentMultiplier = filamentMultiplier;
    }

    public float getFeedRateMultiplier()
    {
        return feedRateMultiplier;
    }

    public void setFeedRateMultiplier(float feedRateMultiplier)
    {
        this.feedRateMultiplier = feedRateMultiplier;
    }

    public int getRequiredAmbientTemperature()
    {
        return requiredAmbientTemperature;
    }

    public void setRequiredAmbientTemperature(int requiredAmbientTemperature)
    {
        this.requiredAmbientTemperature = requiredAmbientTemperature;
    }

    public int getRequiredFirstLayerBedTemperature()
    {
        return requiredFirstLayerBedTemperature;
    }

    public void setRequiredFirstLayerBedTemperature(int requiredFirstLayerBedTemperature)
    {
        this.requiredFirstLayerBedTemperature = requiredFirstLayerBedTemperature;
    }

    public int getRequiredBedTemperature()
    {
        return requiredBedTemperature;
    }

    public void setRequiredBedTemperature(int requiredBedTemperature)
    {
        this.requiredBedTemperature = requiredBedTemperature;
    }

    public int getRequiredFirstLayerNozzleTemperature()
    {
        return requiredFirstLayerNozzleTemperature;
    }

    public void setRequiredFirstLayerNozzleTemperature(int requiredFirstLayerNozzleTemperature)
    {
        this.requiredFirstLayerNozzleTemperature = requiredFirstLayerNozzleTemperature;
    }

    public int getRequiredNozzleTemperature()
    {
        return requiredNozzleTemperature;
    }

    public void setRequiredNozzleTemperature(int requiredNozzleTemperature)
    {
        this.requiredNozzleTemperature = requiredNozzleTemperature;
    }

    public String getWebDisplayColour()
    {
        return webDisplayColour;
    }

    public void setWebDisplayColour(String webDisplayColour)
    {
        this.webDisplayColour = webDisplayColour;
    }

    public float getCostGBPPerKG()
    {
        return costGBPPerKG;
    }

    public void setCostGBPPerKG(float costGBPPerKG)
    {
        this.costGBPPerKG = costGBPPerKG;
    }

    public int getDefaultLength_m()
    {
        return defaultLength_m;
    }

    public void setDefaultLength_m(int defaultLength_m)
    {
        this.defaultLength_m = defaultLength_m;
    }

    public boolean isMutable()
    {
        return mutable;
    }

    public void setMutable(boolean mutable)
    {
        this.mutable = mutable;
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(23, 31).
                append(friendlyFilamentName)
                .append(material)
                .append(reelID)
                .append(brand)
                .append(category)
                .append(diameter)
                .append(filamentMultiplier)
                .append(feedRateMultiplier)
                .append(requiredAmbientTemperature)
                .append(requiredFirstLayerBedTemperature)
                .append(requiredBedTemperature)
                .append(requiredFirstLayerNozzleTemperature)
                .append(requiredNozzleTemperature)
                .append(webDisplayColour)
                .append(costGBPPerKG)
                .append(defaultLength_m)
                .append(mutable)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof SerializableFilament))
        {
            return false;
        }
        if (obj == this)
        {
            return true;
        }

        SerializableFilament rhs = (SerializableFilament) obj;
        return new EqualsBuilder()
                .append(friendlyFilamentName, rhs.friendlyFilamentName)
                .append(material, rhs.material)
                .append(reelID, rhs.reelID)
                .append(brand, rhs.brand)
                .append(category, rhs.category)
                .append(diameter, rhs.diameter)
                .append(filamentMultiplier, rhs.filamentMultiplier)
                .append(feedRateMultiplier, rhs.feedRateMultiplier)
                .append(requiredAmbientTemperature, rhs.requiredAmbientTemperature)
                .append(requiredFirstLayerBedTemperature, rhs.requiredFirstLayerBedTemperature)
                .append(requiredBedTemperature, rhs.requiredBedTemperature)
                .append(requiredFirstLayerNozzleTemperature, rhs.requiredFirstLayerNozzleTemperature)
                .append(requiredNozzleTemperature, rhs.requiredNozzleTemperature)
                .append(webDisplayColour, rhs.webDisplayColour)
                .append(costGBPPerKG, rhs.costGBPPerKG)
                .append(defaultLength_m, rhs.defaultLength_m)
                .append(mutable, rhs.mutable)
                .isEquals();
    }

    @JsonIgnore
    public Filament getFilament()
    {
        Filament filament = new Filament(
                this.friendlyFilamentName,
                this.material,
                this.reelID,
                this.brand,
                this.category,
                this.diameter,
                this.filamentMultiplier,
                this.feedRateMultiplier,
                this.requiredAmbientTemperature,
                this.requiredFirstLayerBedTemperature,
                this.requiredBedTemperature,
                this.requiredFirstLayerNozzleTemperature,
                this.requiredNozzleTemperature,
                Color.web(this.webDisplayColour),
                this.costGBPPerKG,
                this.defaultLength_m,
                this.filled,
                this.mutable);

        return filament;
    }
}
