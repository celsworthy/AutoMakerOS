package celtech.roboxbase.printerControl.model;

import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.MaterialType;
import celtech.roboxbase.comms.rx.ReelEEPROMDataResponse;
import celtech.roboxbase.configuration.Filament;
import celtech.roboxbase.utils.Math.MathUtils;
import celtech.roboxbase.utils.SystemUtils;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyFloatProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;
import static celtech.roboxbase.utils.ColourStringConverter.colourToString;

/**
 *
 * @author Ian
 */
public class Reel
{

    private final Stenographer steno = StenographerFactory.getStenographer(Reel.class.getName());
//    private final FilamentContainer filamentContainer = Lookup.getFilamentContainer();
    protected final StringProperty friendlyFilamentName = new SimpleStringProperty("");
    protected final ObjectProperty<MaterialType> material = new SimpleObjectProperty();
    protected final StringProperty filamentID = new SimpleStringProperty();
    protected final FloatProperty diameter = new SimpleFloatProperty(0);
    protected final FloatProperty filamentMultiplier = new SimpleFloatProperty(0);
    protected final FloatProperty feedRateMultiplier = new SimpleFloatProperty(0);
    protected final IntegerProperty ambientTemperature = new SimpleIntegerProperty(0);
    protected final IntegerProperty firstLayerBedTemperature = new SimpleIntegerProperty(0);
    protected final IntegerProperty bedTemperature = new SimpleIntegerProperty(0);
    protected final IntegerProperty firstLayerNozzleTemperature = new SimpleIntegerProperty(0);
    protected final IntegerProperty nozzleTemperature = new SimpleIntegerProperty(0);
    protected final ObjectProperty<Color> displayColour = new SimpleObjectProperty<>();
    protected final FloatProperty remainingFilament = new SimpleFloatProperty(0);

    protected final BooleanProperty dataChangedToggle = new SimpleBooleanProperty(false);

    private final ChangeListener<String> stringChangeListener = new ChangeListener<String>()
    {
        @Override
        public void changed(
            ObservableValue<? extends String> observable, String oldValue, String newValue)
        {
            dataChanged();
        }
    };
    private final ChangeListener<Number> numberChangeListener = new ChangeListener<Number>()
    {
        @Override
        public void changed(
            ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
        {
            dataChanged();
        }
    };

    private void dataChanged()
    {
        dataChangedToggle.set(!dataChangedToggle.get());
    }

    public Reel()
    {
        friendlyFilamentName.addListener(stringChangeListener);
        material.addListener(new ChangeListener<MaterialType>()
        {
            @Override
            public void changed(
                ObservableValue<? extends MaterialType> observable, MaterialType oldValue, MaterialType newValue)
            {
                dataChanged();
            }
        });
        filamentID.addListener(stringChangeListener);
        diameter.addListener(numberChangeListener);

        filamentMultiplier.addListener(numberChangeListener);
        feedRateMultiplier.addListener(numberChangeListener);
        ambientTemperature.addListener(numberChangeListener);
        firstLayerBedTemperature.addListener(numberChangeListener);
        bedTemperature.addListener(numberChangeListener);
        firstLayerNozzleTemperature.addListener(numberChangeListener);
        nozzleTemperature.addListener(numberChangeListener);
        displayColour.addListener(new ChangeListener<Color>()
        {
            @Override
            public void changed(
                ObservableValue<? extends Color> observable, Color oldValue, Color newValue)
            {
                dataChanged();
            }
        });
        remainingFilament.addListener(numberChangeListener);
    }

    public ReadOnlyStringProperty friendlyFilamentNameProperty()
    {
        return friendlyFilamentName;
    }

    public ReadOnlyObjectProperty<MaterialType> materialProperty()
    {
        return material;
    }

    public ReadOnlyStringProperty filamentIDProperty()
    {
        return filamentID;
    }

    public ReadOnlyFloatProperty diameterProperty()
    {
        return diameter;
    }

    public ReadOnlyFloatProperty filamentMultiplierProperty()
    {
        return filamentMultiplier;
    }

    public ReadOnlyFloatProperty feedRateMultiplierProperty()
    {
        return feedRateMultiplier;
    }

    public ReadOnlyIntegerProperty ambientTemperatureProperty()
    {
        return ambientTemperature;
    }

    public ReadOnlyIntegerProperty firstLayerBedTemperatureProperty()
    {
        return firstLayerBedTemperature;
    }

    public ReadOnlyIntegerProperty bedTemperatureProperty()
    {
        return bedTemperature;
    }

    public ReadOnlyIntegerProperty firstLayerNozzleTemperatureProperty()
    {
        return firstLayerNozzleTemperature;
    }

    public ReadOnlyIntegerProperty nozzleTemperatureProperty()
    {
        return nozzleTemperature;
    }

    public ReadOnlyObjectProperty<Color> displayColourProperty()
    {
        return displayColour;
    }

    public ReadOnlyFloatProperty remainingFilamentProperty()
    {
        return remainingFilament;
    }

    public ReadOnlyBooleanProperty dataChangedToggleProperty()
    {
        return dataChangedToggle;
    }

    public void updateFromEEPROMData(ReelEEPROMDataResponse eepromData)
    {
        ambientTemperature.set(eepromData.getAmbientTemperature());
        bedTemperature.set(eepromData.getBedTemperature());
        feedRateMultiplier.set(eepromData.getFeedRateMultiplier());
        diameter.set(eepromData.getFilamentDiameter());
        filamentMultiplier.set(eepromData.getFilamentMultiplier());
        firstLayerBedTemperature.set(eepromData.getFirstLayerBedTemperature());
        firstLayerNozzleTemperature.set(eepromData.getFirstLayerNozzleTemperature());
        nozzleTemperature.set(eepromData.getNozzleTemperature());
        displayColour.set(Color.web(eepromData.getDisplayColourString()));
        filamentID.set(eepromData.getFilamentID());
        friendlyFilamentName.set(eepromData.getFriendlyName());
        material.set(eepromData.getMaterialType());
        remainingFilament.set(eepromData.getRemainingFilament());
    }

    /**
     * Compare the contents of this reel with filament data. Note that the remaining
     *
     * @param filament
     * @return
     */
    public boolean isSameAs(Filament filament)
    {
        boolean same = false;

        if (filament.getAmbientTemperatureProperty().get() == ambientTemperature.intValue()
            && filament.getBedTemperatureProperty().get() == bedTemperature.intValue()
            && filament.getDiameterProperty().get() == diameter.get()
            && filament.getDisplayColourProperty().get().equals(displayColour.get())
            && SystemUtils.isDoubleSame(filament.getFeedRateMultiplierProperty().get(), feedRateMultiplier.get())
            && filament.getFilamentIDProperty().get().equals(filamentID.get())
            && SystemUtils.isDoubleSame(filament.getFilamentMultiplierProperty().get(), filamentMultiplier.get())
            && SystemUtils.isDoubleSame(filament.getFirstLayerBedTemperatureProperty().get(), firstLayerBedTemperature.get())
            && SystemUtils.isDoubleSame(filament.getFirstLayerNozzleTemperatureProperty().get(), firstLayerNozzleTemperature.get())
            && filament.getFriendlyFilamentNameProperty().get().equals(friendlyFilamentName.get())
            && filament.getMaterialProperty().get() == material.get()
            && filament.getNozzleTemperatureProperty().intValue() == nozzleTemperature.get())
        {
            same = true;
        }

        return same;
    }

    protected void noReelLoaded()
    {
        ambientTemperature.set(0);
        bedTemperature.set(0);
        feedRateMultiplier.set(0);
        diameter.set(0);
        filamentMultiplier.set(0);
        firstLayerBedTemperature.set(0);
        firstLayerNozzleTemperature.set(0);
        nozzleTemperature.set(0);
        displayColour.set(Color.ALICEBLUE);
        filamentID.set("");
        friendlyFilamentName.set(BaseLookup.i18n("smartReelProgrammer.noReelLoaded"));
        material.set(MaterialType.ABS);
        remainingFilament.set(0);
    }

    public boolean isUserFilament()
    {
        return filamentID.get().startsWith("U");
    }

    public void updateContents(Filament filament)
    {
        ambientTemperature.set(filament.getAmbientTemperature());
        bedTemperature.set(filament.getBedTemperature());
        feedRateMultiplier.set(filament.getFeedRateMultiplier());
        diameter.set(filament.getDiameter());
        filamentMultiplier.set(filament.getFilamentMultiplier());
        firstLayerBedTemperature.set(filament.getFirstLayerBedTemperature());
        firstLayerNozzleTemperature.set(filament.getFirstLayerNozzleTemperature());
        nozzleTemperature.set(filament.getNozzleTemperature());
        displayColour.set(filament.getDisplayColour());
        filamentID.set(filament.getFilamentID());
        friendlyFilamentName.set(filament.getFriendlyFilamentName());
        material.set(filament.getMaterial());
        remainingFilament.set(filament.getRemainingFilament());
    }

    public RepairResult bringDataInBounds(Filament referenceFilamentData)
    {
        float epsilon = 1e-5f;

        RepairResult result = RepairResult.NO_REPAIR_NECESSARY;

        if (referenceFilamentData != null)
        {
            if (ambientTemperature.get() != referenceFilamentData.getAmbientTemperature())
            {
                ambientTemperature.set(referenceFilamentData.getAmbientTemperature());
                result = RepairResult.REPAIRED_WRITE_ONLY;
            }

            if (bedTemperature.get() != referenceFilamentData.getBedTemperature())
            {
                bedTemperature.set(referenceFilamentData.getBedTemperature());
                result = RepairResult.REPAIRED_WRITE_ONLY;
            }

            if (MathUtils.compareDouble(feedRateMultiplier.get(), referenceFilamentData.getFeedRateMultiplier(), epsilon) != MathUtils.EQUAL)
            {
                feedRateMultiplier.set(referenceFilamentData.getFeedRateMultiplier());
                result = RepairResult.REPAIRED_WRITE_ONLY;
            }

            if (MathUtils.compareDouble(diameter.get(), referenceFilamentData.getDiameter(), epsilon) != MathUtils.EQUAL)
            {
                diameter.set(referenceFilamentData.getDiameter());
                result = RepairResult.REPAIRED_WRITE_ONLY;
            }

            if (MathUtils.compareDouble(filamentMultiplier.get(), referenceFilamentData.getFilamentMultiplier(), epsilon) != MathUtils.EQUAL)
            {
                filamentMultiplier.set(referenceFilamentData.getFilamentMultiplier());
                result = RepairResult.REPAIRED_WRITE_ONLY;
            }

            if (firstLayerBedTemperature.get() != referenceFilamentData.getFirstLayerBedTemperature())
            {
                firstLayerBedTemperature.set(referenceFilamentData.getFirstLayerBedTemperature());
                result = RepairResult.REPAIRED_WRITE_ONLY;
            }

            if (firstLayerNozzleTemperature.get() != referenceFilamentData.getFirstLayerNozzleTemperature())
            {
                firstLayerNozzleTemperature.set(referenceFilamentData.getFirstLayerNozzleTemperature());
                result = RepairResult.REPAIRED_WRITE_ONLY;
            }

            if (nozzleTemperature.get() != referenceFilamentData.getNozzleTemperature())
            {
                nozzleTemperature.set(referenceFilamentData.getNozzleTemperature());
                result = RepairResult.REPAIRED_WRITE_ONLY;
            }

            if (! colourToString(displayColour.get()).equals(colourToString(referenceFilamentData.getDisplayColour())))
            {
                displayColour.set(referenceFilamentData.getDisplayColour());
                result = RepairResult.REPAIRED_WRITE_ONLY;
            }

            if (! filamentID.get().equals(referenceFilamentData.getFilamentID()))
            {
                filamentID.set(referenceFilamentData.getFilamentID());
                result = RepairResult.REPAIRED_WRITE_ONLY;
            }

            if (!friendlyFilamentName.get().trim().equals(referenceFilamentData.getFriendlyFilamentName().trim()))
            {
                friendlyFilamentName.set(referenceFilamentData.getFriendlyFilamentName());
                result = RepairResult.REPAIRED_WRITE_ONLY;
            }

            if (material.get() != referenceFilamentData.getMaterial())
            {
                material.set(referenceFilamentData.getMaterial());
                result = RepairResult.REPAIRED_WRITE_ONLY;
            }

            steno.debug("Reel data bounds check - result is " + result.name());
        } else
        {
            steno.warning("Reel bounds check requested but reference data could not be obtained.");
        }

        return result;

    }
//
//    public void resetToDefaults()
//    {
//        Filament referenceFilament = filamentContainer.getFilamentByID(filamentID.get());
//        if (referenceFilament != null)
//        {
//            updateContents(referenceFilament);
//            steno.info("Reset reel to defaults with data set - " + referenceFilament.getFriendlyFilamentName());
//        } else
//        {
//            steno.warning("Attempt to reset reel to defaults failed - reference data cannot be derived");
//        }
//    }
}
