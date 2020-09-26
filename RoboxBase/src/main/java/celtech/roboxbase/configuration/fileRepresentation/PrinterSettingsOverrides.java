package celtech.roboxbase.configuration.fileRepresentation;

import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.configuration.RoboxProfile;
import celtech.roboxbase.configuration.SlicerType;
import celtech.roboxbase.configuration.datafileaccessors.HeadContainer;
import celtech.roboxbase.configuration.datafileaccessors.RoboxProfileSettingsContainer;
import celtech.roboxbase.printerControl.model.Head;
import celtech.roboxbase.services.slicer.PrintQualityEnumeration;
import java.util.Optional;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 * PrinterSettings represents the choices made by the user for a project on the
 * Settings panel. It is serialised with the project.
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class PrinterSettingsOverrides
{

    private static final Stenographer STENO = StenographerFactory.getStenographer(PrinterSettingsOverrides.class.getName());

    private static final RoboxProfileSettingsContainer ROBOX_PROFILE_SETTINGS_CONTAINER = RoboxProfileSettingsContainer.getInstance();
    
    private final StringProperty customSettingsName = new SimpleStringProperty();
    private final ObjectProperty<PrintQualityEnumeration> printQuality
            = new SimpleObjectProperty<>(PrintQualityEnumeration.NORMAL);
    private final BooleanProperty dataChanged = new SimpleBooleanProperty(false);

    private int brimOverride = 0;
    private float fillDensityOverride = 0;
    private final BooleanProperty printSupportOverride = new SimpleBooleanProperty(false);
    private final BooleanProperty printSupportGapEnabledOverride = new SimpleBooleanProperty(false);
    private final ObjectProperty<SupportType> printSupportTypeOverride = new SimpleObjectProperty<>(SupportType.AS_PROFILE);
    private boolean raftOverride = false;
    private boolean spiralPrintOverride = false;
    
    private boolean fillDensityChangedByUser = false;
    
    public PrinterSettingsOverrides()
    {
        customSettingsName.set("");
        Optional<RoboxProfile> initialRoboxProfile = ROBOX_PROFILE_SETTINGS_CONTAINER
                .getRoboxProfileWithName(printQuality.get().getFriendlyName(), SlicerType.Cura, HeadContainer.defaultHeadID);
        if(initialRoboxProfile.isPresent()) 
        {
            brimOverride = initialRoboxProfile.get().getSpecificIntSetting("brimWidth_mm");
            fillDensityOverride = initialRoboxProfile.get().getSpecificFloatSetting("fillDensity_normalised");
        }
        printSupportTypeOverride.set(SupportType.AS_PROFILE);
    }

    // A clone without copying the dataChanged property.
    public PrinterSettingsOverrides duplicate()
    {
        PrinterSettingsOverrides copy = new PrinterSettingsOverrides();
        copy.customSettingsName.set(this.customSettingsName.get());
        copy.printQuality.set(this.printQuality.get());
        copy.printSupportOverride.set(this.printSupportOverride.get());
        copy.printSupportGapEnabledOverride.set(this.printSupportGapEnabledOverride.get());
        copy.printSupportTypeOverride.set(this.printSupportTypeOverride.get());
        copy.brimOverride = this.brimOverride;
        copy.fillDensityOverride = this.fillDensityOverride;
        copy.raftOverride = this.raftOverride;
        copy.spiralPrintOverride = this.spiralPrintOverride;
        copy.fillDensityChangedByUser = this.fillDensityChangedByUser;

        return copy;
    }

    private void toggleDataChanged()
    {
        dataChanged.set(dataChanged.not().get());
    }

    public ReadOnlyBooleanProperty getDataChanged()
    {
        return dataChanged;
    }

    public void setPrintQuality(PrintQualityEnumeration value)
    {
        if (printQuality.get() != value)
        {
            printQuality.set(value);
            toggleDataChanged();
        }
    }

    public PrintQualityEnumeration getPrintQuality()
    {
        return printQuality.get();
    }

    public ObjectProperty<PrintQualityEnumeration> printQualityProperty()
    {
        return printQuality;
    }

    public void setSettingsName(String settingsName)
    {
        if (!customSettingsName.get().equals(settingsName))
        {
            customSettingsName.set(settingsName);
            toggleDataChanged();
        }
    }

    public String getSettingsName()
    {
        return customSettingsName.get();
    }

    public StringProperty getSettingsNameProperty()
    {
        return customSettingsName;
    }

    public Optional<RoboxProfile> getBaseProfile(String headType, SlicerType slicerType, PrintQualityEnumeration printQuality)
    {
        Optional<RoboxProfile> profileOption = Optional.empty();
        switch (printQuality) {
            case DRAFT:
                profileOption = ROBOX_PROFILE_SETTINGS_CONTAINER
                        .getRoboxProfileWithName(BaseConfiguration.draftSettingsProfileName, slicerType, headType);
                break;
            case NORMAL:
                profileOption = ROBOX_PROFILE_SETTINGS_CONTAINER
                        .getRoboxProfileWithName(BaseConfiguration.normalSettingsProfileName, slicerType, headType);
                break;
            case FINE:
                profileOption = ROBOX_PROFILE_SETTINGS_CONTAINER
                        .getRoboxProfileWithName(BaseConfiguration.fineSettingsProfileName, slicerType, headType);
                break;
            case CUSTOM:
                profileOption = ROBOX_PROFILE_SETTINGS_CONTAINER
                        .getRoboxProfileWithName(customSettingsName.get(), slicerType, headType);
                break;

        }
        
        return profileOption;
    }

    public RoboxProfile getSettings(String headType, SlicerType slicerType)
    {
        Optional<RoboxProfile> settings = getBaseProfile(headType, slicerType, printQuality.get());
        return applyOverrides(settings, headType);
    }
    
    public RoboxProfile getSettings(String headType, SlicerType slicerType, PrintQualityEnumeration printQuality)
    {
        Optional<RoboxProfile> settings = getBaseProfile(headType, slicerType, printQuality);
        return applyOverrides(settings, headType);
    }

    /**
     * Standard profiles must have the overrides applied.
     *
     * @param roboxProfile
     * @return
     */
    public RoboxProfile applyOverrides(Optional<RoboxProfile> roboxProfile, String headType)
    {
        if(!roboxProfile.isPresent()) 
        {
            return null;
        }
        RoboxProfile profileCopy = new RoboxProfile(roboxProfile.get());
        profileCopy.addOrOverride("brimWidth_mm", String.valueOf(brimOverride));
        profileCopy.addOrOverride("generateSupportMaterial", String.valueOf(printSupportOverride.get()));
        profileCopy.addOrOverride("supportGapEnabled", String.valueOf(printSupportGapEnabledOverride.get()));
        profileCopy.addOrOverride("printRaft", String.valueOf(raftOverride));
        profileCopy.addOrOverride("spiralPrint", String.valueOf(spiralPrintOverride));
        if (fillDensityChangedByUser)
        {
            // Only override the profiles fill density if the value has been changed by the user.
            profileCopy.addOrOverride("fillDensity_normalised", String.valueOf(fillDensityOverride));
        }
        
        if (raftOverride)
        {
            profileCopy.addOrOverride("adhesionType", "raft");
        }
        
        if (brimOverride > 0) 
        {
            profileCopy.addOrOverride("adhesionType", "brim");
        }

        if (spiralPrintOverride)
        {
            profileCopy.addOrOverride("numberOfPerimeters", "1");
        }

        if (HeadContainer.getHeadByID(headType).getType() == Head.HeadType.DUAL_MATERIAL_HEAD)
        {
            // Overrides what is on profile. Unless AS_PROFILE is selected.
            if(printSupportTypeOverride.get().equals(SupportType.MATERIAL_1)) 
            {
                profileCopy.addOrOverride("supportNozzle", "1");
                profileCopy.addOrOverride("supportInterfaceNozzle", "1");
                profileCopy.addOrOverride("raftBrimNozzle", "1");
            } else if (printSupportTypeOverride.get().equals(SupportType.MATERIAL_2)) 
            {
                profileCopy.addOrOverride("supportNozzle", "0");
                profileCopy.addOrOverride("supportInterfaceNozzle", "0");
                profileCopy.addOrOverride("raftBrimNozzle", "0");
            }
        }
        
        return profileCopy;
    }

    public int getBrimOverride()
    {
        return brimOverride;
    }

    public void setBrimOverride(int brimOverride)
    {
        if (this.brimOverride != brimOverride)
        {
            this.brimOverride = brimOverride;
            toggleDataChanged();
        }
    }

    public float getFillDensityOverride()
    {
        return fillDensityOverride;
    }

    public void setFillDensityOverride(float fillDensityOverride)
    {
        if (this.fillDensityOverride != fillDensityOverride)
        {
            this.fillDensityOverride = fillDensityOverride;
            toggleDataChanged();
        }
    }

    public boolean getPrintSupportOverride()
    {
        return printSupportOverride.get();
    }

    public BooleanProperty getPrintSupportOverrideProperty()
    {
        return printSupportOverride;
    }

    public void setPrintSupportOverride(boolean printSupportOverride)
    {
        this.printSupportOverride.set(printSupportOverride);
        toggleDataChanged();
    }

    public boolean getPrintSupportGapEnabledOverride()
    {
        return printSupportGapEnabledOverride.get();
    }

    public BooleanProperty getPrintSupportGapEnabledOverrideProperty()
    {
        return printSupportGapEnabledOverride;
    }

    public void setPrintSupportGapEnabledOverride(boolean printSupportGapEnabledOverride)
    {
        this.printSupportGapEnabledOverride.set(printSupportGapEnabledOverride);
        toggleDataChanged();
    }

    public SupportType getPrintSupportTypeOverride()
    {
        return printSupportTypeOverride.get();
    }

    public ObjectProperty<SupportType> getPrintSupportTypeOverrideProperty()
    {
        return printSupportTypeOverride;
    }

    public void setPrintSupportTypeOverride(SupportType printSupportTypeOverride)
    {
        if (this.printSupportTypeOverride.get() != printSupportTypeOverride)
        {
            this.printSupportTypeOverride.set(printSupportTypeOverride);
            toggleDataChanged();
        }
    }

    public boolean getRaftOverride()
    {
        return raftOverride;
    }

    public void setRaftOverride(boolean raftOverride)
    {
        if (this.raftOverride != raftOverride)
        {
            this.raftOverride = raftOverride;
            toggleDataChanged();
        }
    }

    public boolean getSpiralPrintOverride()
    {
        return spiralPrintOverride;
    }

    public void setSpiralPrintOverride(boolean spiralPrintOverride)
    {
        if (this.spiralPrintOverride != spiralPrintOverride)
        {
            this.spiralPrintOverride = spiralPrintOverride;
            toggleDataChanged();
        }
    }

    public boolean isFillDensityChangedByUser() 
    {
        return fillDensityChangedByUser;
    }

    public void setFillDensityChangedByUser(boolean fillDensityChangedByUser) 
    {
        if (this.fillDensityChangedByUser != fillDensityChangedByUser)
        {
            this.fillDensityChangedByUser = fillDensityChangedByUser;
            toggleDataChanged();
        }
    }
}
