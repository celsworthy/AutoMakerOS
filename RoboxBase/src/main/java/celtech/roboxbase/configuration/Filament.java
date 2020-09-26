package celtech.roboxbase.configuration;

import celtech.roboxbase.MaterialType;
import celtech.roboxbase.comms.rx.ReelEEPROMDataResponse;
import celtech.roboxbase.printerControl.model.Reel;
import celtech.roboxbase.utils.SystemUtils;
import java.io.Serializable;
import java.util.Comparator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 *
 * @author ianhudson
 */
public class Filament implements Serializable, Cloneable
{

    private static final Stenographer steno = StenographerFactory.getStenographer(
            Filament.class.getName());

    private final BooleanProperty mutable = new SimpleBooleanProperty(false);
    private final StringProperty friendlyFilamentName = new SimpleStringProperty("");
    private final ObjectProperty<MaterialType> material = new SimpleObjectProperty();
    private final StringProperty filamentID = new SimpleStringProperty();
    private final StringProperty brand = new SimpleStringProperty("");
    private final StringProperty category = new SimpleStringProperty("");

    private final FloatProperty diameter = new SimpleFloatProperty(0);
    private final FloatProperty filamentMultiplier = new SimpleFloatProperty(0);
    private final FloatProperty feedRateMultiplier = new SimpleFloatProperty(0);
    private final IntegerProperty requiredAmbientTemperature = new SimpleIntegerProperty(0);
    private final IntegerProperty requiredFirstLayerBedTemperature = new SimpleIntegerProperty(0);
    private final IntegerProperty requiredBedTemperature = new SimpleIntegerProperty(0);
    private final IntegerProperty requiredFirstLayerNozzleTemperature = new SimpleIntegerProperty(0);
    private final IntegerProperty requiredNozzleTemperature = new SimpleIntegerProperty(0);
    private final ObjectProperty<Color> displayColour = new SimpleObjectProperty<>();
    private final FloatProperty remainingFilament = new SimpleFloatProperty(0);
    private final FloatProperty costGBPPerKG = new SimpleFloatProperty(35f);
    private final IntegerProperty defaultLength_m = new SimpleIntegerProperty(0);
    private final BooleanProperty filled = new SimpleBooleanProperty(false);
    
    public static final Comparator<Filament> BY_MATERIAL
            = (Filament o1, Filament o2) -> o1.getMaterial().name().compareTo(o2.getMaterial().name());

    public static final Comparator<Filament> BY_NAME
            = Comparator.comparing(Filament::getFriendlyFilamentName);

    private static int charsToDiscardInIDComparisonFinal = 8;
    private static int charsToRetainInIDComparisonInitial = 3;

    public static int compareByFilamentID(Filament lhs, Filament rhs)
    {
        String lhsFinalCode = lhs.getFilamentID();
        String rhsFinalCode = rhs.getFilamentID();

        if (lhsFinalCode.length() > charsToDiscardInIDComparisonFinal)
        {
            lhsFinalCode = lhsFinalCode.substring(charsToDiscardInIDComparisonFinal);
        }

        if (rhsFinalCode.length() > charsToDiscardInIDComparisonFinal)
        {
            rhsFinalCode = rhsFinalCode.substring(charsToDiscardInIDComparisonFinal);
        }

        if (lhsFinalCode.equals(rhsFinalCode))
        {
            String lhsInitialCode = lhs.getFilamentID();
            String rhsInitialCode = rhs.getFilamentID();

            if (lhsInitialCode.length() > charsToRetainInIDComparisonInitial)
            {
                lhsInitialCode = lhsInitialCode.substring(charsToRetainInIDComparisonInitial);
            }

            if (rhsInitialCode.length() > charsToRetainInIDComparisonInitial)
            {
                rhsInitialCode = rhsInitialCode.substring(charsToRetainInIDComparisonInitial);
            }

            //Must compare by the first 3 chars of the ID
            return lhsInitialCode.compareTo(rhsInitialCode);
        } else
        {
            return lhsFinalCode.compareTo(rhsFinalCode);
        }
    }

    public Filament(
            String friendlyFilamentName,
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
            Color displayColour,
            float costGBPPerKG,
            int defaultLength_m,
            boolean filled,
            boolean mutable)
    {
        this.friendlyFilamentName.set(friendlyFilamentName);
        this.material.set(material);
        this.filamentID.set(reelID);
        this.brand.set(brand);
        this.category.set(category);
        this.diameter.set(diameter);
        this.filamentMultiplier.set(filamentMultiplier);
        this.feedRateMultiplier.set(feedRateMultiplier);
        this.requiredAmbientTemperature.set(requiredAmbientTemperature);
        this.requiredFirstLayerBedTemperature.set(requiredFirstLayerBedTemperature);
        this.requiredBedTemperature.set(requiredBedTemperature);
        this.requiredFirstLayerNozzleTemperature.set(requiredFirstLayerNozzleTemperature);
        this.requiredNozzleTemperature.set(requiredNozzleTemperature);
        this.displayColour.set(displayColour);
        this.costGBPPerKG.set(costGBPPerKG);
        this.defaultLength_m.set(defaultLength_m);
        //Remaining filament is in mm on the reel
        this.remainingFilament.set(defaultLength_m * 1000);
        this.filled.set(filled);
        this.mutable.set(mutable);
    }

    public Filament(ReelEEPROMDataResponse response)
    {
        this.filamentID.set(response.getFilamentID());
        this.friendlyFilamentName.set(response.getFriendlyName());
        this.material.set(response.getMaterialType());
        this.displayColour.set(Color.web(response.getDisplayColourString()));
        this.diameter.set(response.getFilamentDiameter());
        this.filamentMultiplier.set(response.getFilamentMultiplier());
        this.feedRateMultiplier.set(response.getFeedRateMultiplier());
        this.requiredAmbientTemperature.set(response.getAmbientTemperature());
        this.requiredFirstLayerBedTemperature.set(response.getFirstLayerBedTemperature());
        this.requiredBedTemperature.set(response.getBedTemperature());
        this.requiredFirstLayerNozzleTemperature.set(response.getFirstLayerNozzleTemperature());
        this.requiredNozzleTemperature.set(response.getNozzleTemperature());
        detectAndSetMutable();
    }

    public Filament(Reel reel)
    {
        this.filamentID.set(reel.filamentIDProperty().get());
        this.friendlyFilamentName.set(reel.friendlyFilamentNameProperty().get());
        this.material.set(reel.materialProperty().get());
        this.displayColour.set(reel.displayColourProperty().get());
        this.diameter.set(reel.diameterProperty().get());
        this.filamentMultiplier.set(reel.filamentMultiplierProperty().get());
        this.feedRateMultiplier.set(reel.feedRateMultiplierProperty().get());
        this.requiredAmbientTemperature.set(reel.ambientTemperatureProperty().get());
        this.requiredFirstLayerBedTemperature.set(reel.firstLayerBedTemperatureProperty().get());
        this.requiredBedTemperature.set(reel.bedTemperatureProperty().get());
        this.requiredFirstLayerNozzleTemperature.set(reel.firstLayerNozzleTemperatureProperty().get());
        this.requiredNozzleTemperature.set(reel.nozzleTemperatureProperty().get());
        detectAndSetMutable();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 31).
                append(filamentID.get()).
                append(friendlyFilamentName.get()).
                append(material.get()).
                append(diameter.get()).
                append(filamentMultiplier.get()).
                append(feedRateMultiplier.get()).
                append(requiredAmbientTemperature.get()).
                append(requiredFirstLayerBedTemperature.get()).
                append(requiredBedTemperature.get()).
                append(requiredFirstLayerNozzleTemperature.get()).
                append(requiredNozzleTemperature.get()).
                append(displayColour.get()).
                append(costGBPPerKG.get()).
                append(defaultLength_m.get()).
                append(filled.get()).
                toHashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof Filament))
        {
            return false;
        }
        if (obj == this)
        {
            return true;
        }

        Filament rhs = (Filament) obj;
        return new EqualsBuilder().
                append(filamentID.get(), rhs.filamentID.get()).
                append(friendlyFilamentName.get(), rhs.friendlyFilamentName.get()).
                append(material.get(), rhs.material.get()).
                append(diameter.get(), rhs.diameter.get()).
                append(filamentMultiplier.get(), rhs.filamentMultiplier.get()).
                append(feedRateMultiplier.get(), rhs.feedRateMultiplier.get()).
                append(requiredAmbientTemperature.get(), rhs.requiredAmbientTemperature.get()).
                append(requiredFirstLayerBedTemperature.get(), rhs.requiredFirstLayerBedTemperature.get()).
                append(requiredBedTemperature.get(), rhs.requiredBedTemperature.get()).
                append(requiredFirstLayerNozzleTemperature.get(), rhs.requiredFirstLayerNozzleTemperature.get()).
                append(requiredNozzleTemperature.get(), rhs.requiredNozzleTemperature.get()).
                append(displayColour.get(), rhs.displayColour.get()).
                append(costGBPPerKG.get(), rhs.costGBPPerKG.get()).
                append(defaultLength_m.get(), rhs.defaultLength_m.get()).
                append(filled.get(), rhs.filled.get()).
                isEquals();
    }

    public String getFileName()
    {
        return friendlyFilamentName.get() + "_" + material.toString();
    }

    public StringProperty getFriendlyFilamentNameProperty()
    {
        return friendlyFilamentName;
    }

    public String getFriendlyFilamentName()
    {
        return friendlyFilamentName.get();
    }

    public String getFilamentID()
    {
        return filamentID.get();
    }

    public StringProperty getFilamentIDProperty()
    {
        return filamentID;
    }

    public String getBrand()
    {
        return brand.get();
    }

    public StringProperty getBrandProperty()
    {
        return brand;
    }

    public String getCategory()
    {
        return category.get();
    }

    public StringProperty getCategoryProperty()
    {
        return category;
    }

    public ObjectProperty<MaterialType> getMaterialProperty()
    {
        return material;
    }

    public MaterialType getMaterial()
    {
        return material.get();
    }

    public FloatProperty getDiameterProperty()
    {
        return diameter;
    }

    public float getDiameter()
    {
        return diameter.get();
    }

    public FloatProperty getFilamentMultiplierProperty()
    {
        return filamentMultiplier;
    }

    public float getFilamentMultiplier()
    {
        return filamentMultiplier.get();
    }

    public FloatProperty getFeedRateMultiplierProperty()
    {
        return feedRateMultiplier;
    }

    public float getFeedRateMultiplier()
    {
        return feedRateMultiplier.get();
    }

    public IntegerProperty getAmbientTemperatureProperty()
    {
        return requiredAmbientTemperature;
    }

    public int getAmbientTemperature()
    {
        return requiredAmbientTemperature.get();
    }

    public IntegerProperty getFirstLayerBedTemperatureProperty()
    {
        return requiredFirstLayerBedTemperature;
    }

    public int getFirstLayerBedTemperature()
    {
        return requiredFirstLayerBedTemperature.get();
    }

    public IntegerProperty getBedTemperatureProperty()
    {
        return requiredBedTemperature;
    }

    public int getBedTemperature()
    {
        return requiredBedTemperature.get();
    }

    public IntegerProperty getFirstLayerNozzleTemperatureProperty()
    {
        return requiredFirstLayerNozzleTemperature;
    }

    public int getFirstLayerNozzleTemperature()
    {
        return requiredFirstLayerNozzleTemperature.get();
    }

    public IntegerProperty getNozzleTemperatureProperty()
    {
        return requiredNozzleTemperature;
    }

    public int getNozzleTemperature()
    {
        return requiredNozzleTemperature.get();
    }

    public ObjectProperty<Color> getDisplayColourProperty()
    {
        return displayColour;
    }

    public Color getDisplayColour()
    {
        return displayColour.get();
    }

    public FloatProperty getRemainingFilamentProperty()
    {
        return remainingFilament;
    }

    public float getRemainingFilament()
    {
        return remainingFilament.get();
    }

    public boolean isFilled()
    {
        return filled.get();
    }

    public BooleanProperty getFilledProperty()
    {
        return filled;
    }

    public void setFriendlyFilamentName(String friendlyColourName)
    {
        this.friendlyFilamentName.set(friendlyColourName);
    }

    public void setFilamentID(String value)
    {
        this.filamentID.set(value);
        detectAndSetMutable();
    }

    public void setBrand(String value)
    {
        this.brand.set(value);
    }
    
    public void setCategory(String value)
    {
        this.category.set(value);
    }

    public void setMaterial(MaterialType material)
    {
        this.material.set(material);
    }

    public void setFilamentDiameter(float diameter)
    {
        this.diameter.set(diameter);
    }

    public void setFilamentMultiplier(float filamentMultiplier)
    {
        this.filamentMultiplier.set(filamentMultiplier);
    }

    public void setFeedRateMultiplier(float feedRateMultiplier)
    {
        this.feedRateMultiplier.set(feedRateMultiplier);
    }

    public void setAmbientTemperature(int requiredAmbientTemperature)
    {
        this.requiredAmbientTemperature.set(requiredAmbientTemperature);
    }

    public void setFirstLayerBedTemperature(int requiredFirstLayerBedTemperature)
    {
        this.requiredFirstLayerBedTemperature.set(requiredFirstLayerBedTemperature);
    }

    public void setBedTemperature(int requiredBedTemperature)
    {
        this.requiredBedTemperature.set(requiredBedTemperature);
    }

    public void setFirstLayerNozzleTemperature(int requiredFirstLayerNozzleTemperature)
    {
        this.requiredFirstLayerNozzleTemperature.set(requiredFirstLayerNozzleTemperature);
    }

    public void setNozzleTemperature(int requiredNozzleTemperature)
    {
        this.requiredNozzleTemperature.set(requiredNozzleTemperature);
    }

    public void setDisplayColour(Color displayColour)
    {
        this.displayColour.set(displayColour);
    }

    public void setRemainingFilament(float value)
    {
        this.remainingFilament.set(value);
    }

    public void setFilled(boolean value)
    {
        this.filled.set(value);
    }

    public boolean isMutable()
    {
        return mutable.get();
    }

    public void setMutable(boolean value)
    {
        this.mutable.set(value);
    }

    public BooleanProperty getMutableProperty()
    {
        return mutable;
    }

    public IntegerProperty getDefaultLength_mProperty()
    {
        return defaultLength_m;
    }

    public int getDefaultLength_m()
    {
        return defaultLength_m.get();
    }

    public void setDefaultLength_m(int value)
    {
        defaultLength_m.set(value);
    }

    /**
     * Return the friendlyName
     */
    public String getLongFriendlyName()
    {
        if (filamentID.get() != null)
        {
            return friendlyFilamentName.get();
        }

        return friendlyFilamentName.get();
    }

    /**
     * Return the weight of a given length of this material, in grammes.
     */
    public double getWeightForLength(double lengthMetres)
    {
        double densityKGM3 = material.get().getDensity() * 1000d;
        double crossSectionM2 = Math.PI * diameter.get() * diameter.get() / 4d * 1e-6;
        return lengthMetres * crossSectionM2 * densityKGM3 * 1000d;
    }

    /**
     * Return the weight of a given volume of this material, in grammes.
     */
    public double getWeightForVolume(double volumeCubicMetres)
    {
        double densityKGM3 = material.get().getDensity() * 1000d;
        return volumeCubicMetres * densityKGM3 * 1000d;
    }

    /**
     * Return the cost in GBP of a given volume of this material.
     */
    public double getCostForVolume(double volumeCubicMetres)
    {
        double densityKGM3 = material.get().getDensity() * 1000d;
        double weight = volumeCubicMetres * densityKGM3;
        return weight * costGBPPerKG.get();
    }

    @Override
    public String toString()
    {
        StringBuilder stringToReturn = new StringBuilder();
        stringToReturn.append(getLongFriendlyName());
        if (material.get() != null)
        {
            stringToReturn.append(' ');
            stringToReturn.append(material.get());
        }
        return stringToReturn.toString();
    }

    public static String generateUserFilamentID()
    {
        String fullID = SystemUtils.generate16DigitID();
        StringBuilder id = new StringBuilder();
        id.append('U');
        id.append(fullID.substring(1, fullID.length() - 1));
        return id.toString();
    }

    @Override
    public Filament clone()
    {
        Filament clone = new Filament(this.getFriendlyFilamentName(),
                this.getMaterial(),
                this.getFilamentID(),
                this.getBrand(),
                this.getCategory(),
                this.getDiameter(),
                this.getFilamentMultiplier(),
                this.getFeedRateMultiplier(),
                this.getAmbientTemperature(),
                this.getFirstLayerBedTemperature(),
                this.getBedTemperature(),
                this.getFirstLayerNozzleTemperature(),
                this.getNozzleTemperature(),
                this.getDisplayColour(),
                this.getCostGBPPerKG(),
                this.getDefaultLength_m(),
                this.isFilled(),
                this.isMutable()
        );

        return clone;
    }

    public float getCostGBPPerKG()
    {
        return costGBPPerKG.get();
    }

    public void setCostGBPPerKG(float cost)
    {
        costGBPPerKG.set(cost);
    }

    /**
     * Based on whether the first character of the ID is "U" or not, set the
     * mutable field. All user created filaments should start with U, Robox
     * reels do not.
     */
    private void detectAndSetMutable()
    {
        if (filamentID.get().startsWith("U"))
        {
            mutable.set(true);
        } else
        {
            mutable.set(false);
        }

    }
}
