package celtech.roboxbase.printerControl.model;

import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.comms.rx.HeadEEPROMDataResponse;
import celtech.roboxbase.configuration.datafileaccessors.HeadContainer;
import celtech.roboxbase.configuration.fileRepresentation.HeadFile;
import celtech.roboxbase.configuration.fileRepresentation.NozzleData;
import celtech.roboxbase.configuration.fileRepresentation.NozzleHeaterData;
import celtech.roboxbase.utils.InvalidChecksumException;
import celtech.roboxbase.utils.Math.MathUtils;
import celtech.roboxbase.utils.SystemUtils;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyFloatProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author ianhudson
 */
public class Head implements Cloneable, RepairableComponent
{

    public enum HeadType
    {

        STYLUS_HEAD("stylusHead"),
        SINGLE_MATERIAL_HEAD("singleMaterialHead"),
        DUAL_MATERIAL_HEAD("dualMaterialHead");

        private final String helpText;

        HeadType(String helpText)
        {
            this.helpText = helpText;
        }

        @Override
        public String toString()
        {
            return BaseLookup.i18n("headType." + helpText);
        }

    }

    public enum ValveType
    {

        FITTED,
        NOT_FITTED;
    }

    private static final Stenographer steno = StenographerFactory.getStenographer(Head.class.
            getName());

    protected ObjectProperty<HeadType> headType = new SimpleObjectProperty<>(
            HeadType.SINGLE_MATERIAL_HEAD);
    protected ObjectProperty<ValveType> valveType = new SimpleObjectProperty<>(
            ValveType.FITTED);

    protected final FloatProperty zReductionProperty = new SimpleFloatProperty(0);
    
    protected final FloatProperty headXPosition = new SimpleFloatProperty(0);
    protected final FloatProperty headYPosition = new SimpleFloatProperty(0);
    protected final FloatProperty headZPosition = new SimpleFloatProperty(0);
    protected final FloatProperty BPosition = new SimpleFloatProperty(0);
    protected final IntegerProperty nozzleInUse = new SimpleIntegerProperty(0);

    protected final StringProperty typeCode = new SimpleStringProperty("");
    protected final StringProperty name = new SimpleStringProperty("");
    protected final StringProperty uniqueID = new SimpleStringProperty("");
    protected final FloatProperty headHours = new SimpleFloatProperty(0);

    protected final StringProperty weekNumber = new SimpleStringProperty("");
    protected final StringProperty yearNumber = new SimpleStringProperty("");
    protected final StringProperty PONumber = new SimpleStringProperty("");
    protected final StringProperty serialNumber = new SimpleStringProperty("");
    protected final StringProperty checksum = new SimpleStringProperty("");

    protected final ObservableList<NozzleHeater> nozzleHeaters = FXCollections.observableArrayList();
    protected final ObservableList<Nozzle> nozzles = FXCollections.observableArrayList();

    protected final BooleanProperty dataChanged = new SimpleBooleanProperty();

    public Head()
    {
    }

    public Head(HeadFile headData)
    {
        updateFromHeadFileData(headData);
    }

    protected static Head createHead(HeadEEPROMDataResponse headResponse)
    {
        Head createdHead = null;

        HeadFile headData = HeadContainer.getHeadByID(headResponse.getHeadTypeCode());
        if (headData != null)
        {
            createdHead = new Head(headData);
            createdHead.updateFromEEPROMData(headResponse);
        } else
        {
            steno.error("Attempt to create head with invalid or absent type code");
        }

        return createdHead;
    }

    protected NozzleHeater makeNozzleHeater(NozzleHeaterData nozzleHeaterData)
    {
        return new NozzleHeater(nozzleHeaterData.getMaximum_temperature_C(),
                nozzleHeaterData.getBeta(),
                nozzleHeaterData.getTcal(),
                0, 0, 0, 0, "");
    }

    private void updateFromHeadFileData(HeadFile headData)
    {
        updateFromHeadFileData(headData, true);
    }

    private void updateFromHeadFileData(HeadFile headData, boolean flagDataChanged)
    {
        setTypeCode(headData.getTypeCode());
        valveType.set(headData.getValves());
        
        zReductionProperty.set(headData.getZReduction());
        
        nozzleHeaters.clear();
        headData.getNozzleHeaters().stream().
                map((nozzleHeaterData) -> makeNozzleHeater(nozzleHeaterData))
                .forEach((newNozzleHeater) ->
                        {
                            nozzleHeaters.add(newNozzleHeater);
                });

        nozzles.clear();
        headData.getNozzles().stream().
                map((nozzleData) -> new Nozzle(nozzleData.getDiameter(),
                                nozzleData.getDefaultXOffset(),
                                nozzleData.getDefaultYOffset(),
                                nozzleData.getDefaultZOffset(),
                                nozzleData.getDefaultBOffset())).
                forEach((newNozzle) ->
                        {
                            nozzles.add(newNozzle);
                });

        if (flagDataChanged)
        {
            dataChanged.set(!dataChanged.get());
        }
    }

    private Head(String typeCode,
            String friendlyName,
            String uniqueID,
            float headHours,
            List<NozzleHeater> nozzleHeaters,
            List<Nozzle> nozzles)
    {
        setTypeCode(typeCode);
        this.name.set(friendlyName);
        this.uniqueID.set(uniqueID);
        this.headHours.set(headHours);
        this.nozzleHeaters.addAll(nozzleHeaters);
        this.nozzles.addAll(nozzles);
    }

    public StringProperty typeCodeProperty()
    {
        return typeCode;
    }

    public StringProperty nameProperty()
    {
        return name;
    }

    public ObjectProperty<HeadType> headTypeProperty()
    {
        return headType;
    }
    
     public ObjectProperty<ValveType> valveTypeProperty()
    {
        return valveType;
    }
     
    public ReadOnlyFloatProperty getZReductionProperty() 
    {
        return zReductionProperty;
    }

    public StringProperty uniqueIDProperty()
    {
        return uniqueID;
    }

    public FloatProperty headHoursProperty()
    {
        return headHours;
    }

    public ObservableList<NozzleHeater> getNozzleHeaters()
    {
        return nozzleHeaters;
    }

    public ObservableList<Nozzle> getNozzles()
    {
        return nozzles;
    }

    public ReadOnlyFloatProperty bPositionProperty()
    {
        return BPosition;
    }

    public ReadOnlyIntegerProperty nozzleInUseProperty()
    {
        return nozzleInUse;
    }

    public ReadOnlyFloatProperty headXPositionProperty()
    {
        return headXPosition;
    }

    public ReadOnlyFloatProperty headYPositionProperty()
    {
        return headYPosition;
    }

    public ReadOnlyFloatProperty headZPositionProperty()
    {
        return headZPosition;
    }

    public ReadOnlyBooleanProperty dataChangedProperty()
    {
        return dataChanged;
    }

    public String getWeekNumber()
    {
        return weekNumber.get();
    }

    public void setWeekNumber(String value)
    {
        weekNumber.set(value);
    }

    public String getYearNumber()
    {
        return yearNumber.get();
    }

    public void setYearNumber(String value)
    {
        yearNumber.set(value);
    }

    public String getPONumber()
    {
        return PONumber.get();
    }

    public void setPONumber(String value)
    {
        PONumber.set(value);
    }

    public String getSerialNumber()
    {
        return serialNumber.get();
    }

    public void setSerialNumber(String value)
    {
        serialNumber.set(value);
    }

    public String getChecksum()
    {
        return checksum.get();
    }

    public void setChecksum(String value)
    {
        checksum.set(value);
    }

    @Override
    public String toString()
    {
        return name.get();
    }

    @Override
    public Head clone()
    {
        ArrayList<NozzleHeater> newNozzleHeaters = new ArrayList<>();
        ArrayList<Nozzle> newNozzles = new ArrayList<>();

        nozzleHeaters.stream().
                forEach((nozzleHeater) ->
                        {
                            newNozzleHeaters.add(nozzleHeater.clone());
                });

        nozzles.stream().
                forEach((nozzle) ->
                        {
                            newNozzles.add(nozzle.clone());
                });

        Head clone = new Head(
                typeCode.get(),
                name.get(),
                uniqueID.get(),
                headHours.get(),
                newNozzleHeaters,
                newNozzles
        );

        return clone;
    }

    private void setTypeCode(String typeCode)
    {
        this.typeCode.set(typeCode);

        HeadFile headFile = HeadContainer.getHeadByID(typeCode);
        if (headFile != null)
        {
            headType.set(HeadContainer.getHeadByID(typeCode).getType());
        } else
        {
            headType.set(null);
        }
    }

    public final void updateFromEEPROMData(HeadEEPROMDataResponse eepromData)
    {
        if (!eepromData.getHeadTypeCode().equals(typeCode.get()))
        {
            updateFromHeadFileData(HeadContainer.getHeadByID(eepromData.getHeadTypeCode()), false);
        }
        uniqueID.set(eepromData.getUniqueID());
        weekNumber.set(eepromData.getWeekNumber());
        yearNumber.set(eepromData.getYearNumber());
        PONumber.set(eepromData.getPONumber());
        serialNumber.set(eepromData.getSerialNumber());
        checksum.set(eepromData.getChecksum());
        headHours.set(eepromData.getHeadHours());

        for (int i = 0; i < nozzleHeaters.size(); i++)
        {
            nozzleHeaters.get(i).beta.set(eepromData.getThermistorBeta());
            nozzleHeaters.get(i).tcal.set(eepromData.getThermistorTCal());
            nozzleHeaters.get(i).lastFilamentTemperature.
                    set(eepromData.getLastFilamentTemperature(i));
            nozzleHeaters.get(i).maximumTemperature.set(eepromData.getMaximumTemperature());
            nozzleHeaters.get(i).filamentID.set(eepromData.getFilamentID(i));
        }

        if (nozzles.size() > 0)
        {
            nozzles.get(0).xOffset.set(eepromData.getNozzle1XOffset());
            nozzles.get(0).yOffset.set(eepromData.getNozzle1YOffset());
            nozzles.get(0).zOffset.set(eepromData.getNozzle1ZOffset());
            nozzles.get(0).bOffset.set(eepromData.getNozzle1BOffset());
        }

        if (nozzles.size() == 2)
        {
            nozzles.get(1).xOffset.set(eepromData.getNozzle2XOffset());
            nozzles.get(1).yOffset.set(eepromData.getNozzle2YOffset());
            nozzles.get(1).zOffset.set(eepromData.getNozzle2ZOffset());
            nozzles.get(1).bOffset.set(eepromData.getNozzle2BOffset());
        }

        dataChanged.set(!dataChanged.get());
    }

    public boolean matchesEEPROMData(HeadEEPROMData response)
    {
        boolean matches = false;

        if (response.getHeadTypeCode().equals(typeCodeProperty().get()))
        {
            matches = response.getHeadHours() == headHoursProperty().get()
                    && response.getUniqueID().equals(uniqueIDProperty().get());

            if (getNozzleHeaters().size() > 0)
            {
                matches &= response.getMaximumTemperature() == getNozzleHeaters().get(0).maximumTemperatureProperty().get()
                        && response.getThermistorBeta() == getNozzleHeaters().get(0).betaProperty().get()
                        && response.getThermistorTCal() == getNozzleHeaters().get(0).tCalProperty().get();
            }

            if (getNozzles().size() > 0)
            {
                matches &= response.getNozzle1BOffset() == getNozzles().get(0).bOffsetProperty().get()
                        && response.getNozzle1XOffset() == getNozzles().get(0).xOffsetProperty().get()
                        && response.getNozzle1YOffset() == getNozzles().get(0).yOffsetProperty().get()
                        && response.getNozzle1ZOffset() == getNozzles().get(0).zOffsetProperty().get();
            }

            if (getNozzles().size() > 1)
            {
                matches &= response.getNozzle2BOffset() == getNozzles().get(1).bOffsetProperty().get()
                        && response.getNozzle2XOffset() == getNozzles().get(1).xOffsetProperty().get()
                        && response.getNozzle2YOffset() == getNozzles().get(1).yOffsetProperty().get()
                        && response.getNozzle2ZOffset() == getNozzles().get(1).zOffsetProperty().get();
            }
        }
        return matches;
    }

    @Override
    public RepairResult bringDataInBounds()
    {
        float epsilon = 1e-5f;

        RepairResult result = RepairResult.NO_REPAIR_NECESSARY;

        HeadFile referenceHeadData = HeadContainer.getHeadByID(typeCode.get());
        if (referenceHeadData != null)
        {
            // Iterate through the nozzle heaters and check for differences
            for (int i = 0; i < getNozzleHeaters().size(); i++)
            {
                NozzleHeater nozzleHeater = getNozzleHeaters().get(i);
                NozzleHeaterData nozzleHeaterData = referenceHeadData.getNozzleHeaters().get(i);

                if (MathUtils.compareDouble(nozzleHeater.maximumTemperatureProperty().get(),
                        nozzleHeaterData.getMaximum_temperature_C(), epsilon)
                        != MathUtils.EQUAL)
                {
                    nozzleHeater.maximumTemperature.set(nozzleHeaterData.getMaximum_temperature_C());
                    result = RepairResult.REPAIRED_WRITE_ONLY;
                }

                if (Math.abs(nozzleHeater.tCalProperty().get() - nozzleHeaterData.getTcal())
                        > epsilon)
                {
                    nozzleHeater.tcal.set(nozzleHeaterData.getTcal());
                    result = RepairResult.REPAIRED_WRITE_ONLY;
                }

                if (Math.abs(nozzleHeater.betaProperty().get() - nozzleHeaterData.getBeta())
                        > epsilon)
                {
                    nozzleHeater.beta.set(nozzleHeaterData.getBeta());
                    result = RepairResult.REPAIRED_WRITE_ONLY;
                }
            }

            // Now for the nozzles...
            for (int i = 0; i < getNozzles().size(); i++)
            {
                Nozzle nozzle = getNozzles().get(i);
                NozzleData nozzleData = referenceHeadData.getNozzles().get(i);

                if (nozzle.xOffsetProperty().get() < nozzleData.getMinXOffset() || nozzle.
                        xOffsetProperty().get() > nozzleData.getMaxXOffset())
                {
                    nozzle.xOffset.set(nozzleData.getDefaultXOffset());
                    result = RepairResult.REPAIRED_WRITE_AND_RECALIBRATE;
                }

                if (nozzle.yOffsetProperty().get() < nozzleData.getMinYOffset() || nozzle.
                        yOffsetProperty().get() > nozzleData.getMaxYOffset())
                {
                    nozzle.yOffset.set(nozzleData.getDefaultYOffset());
                    result = RepairResult.REPAIRED_WRITE_AND_RECALIBRATE;
                }

                if (nozzle.zOffsetProperty().get() < nozzleData.getMinZOffset() || nozzle.
                        zOffsetProperty().get() > nozzleData.getMaxZOffset())
                {
                    nozzle.zOffset.set(nozzleData.getDefaultZOffset());
                    result = RepairResult.REPAIRED_WRITE_AND_RECALIBRATE;
                }

                if (nozzle.bOffsetProperty().get() < nozzleData.getMinBOffset() || nozzle.
                        bOffsetProperty().get() > nozzleData.getMaxBOffset())
                {
                    nozzle.bOffset.set(nozzleData.getDefaultBOffset());
                    result = RepairResult.REPAIRED_WRITE_AND_RECALIBRATE;
                }
            }

            steno.debug("Head data bounds check - result is " + result.name());
        } else
        {
            steno.warning("Head bounds check requested but reference data could not be obtained.");
        }

        if (result != RepairResult.NO_REPAIR_NECESSARY)
        {
            dataChanged.set(!dataChanged.get());
        }

        return result;
    }

    @Override
    public void resetToDefaults()
    {
        HeadFile referenceHeadData = HeadContainer.getHeadByID(typeCode.get());
        if (referenceHeadData != null)
        {
            typeCode.set(referenceHeadData.getTypeCode());

            int nozzleHeaterIndex = 0;
            for (NozzleHeater heater : nozzleHeaters)
            {
                NozzleHeaterData heaterData
                        = referenceHeadData.getNozzleHeaters().get(nozzleHeaterIndex);
                heater.maximumTemperature.set(heaterData.getMaximum_temperature_C());
                heater.beta.set(heaterData.getBeta());
                heater.tcal.set(heaterData.getTcal());
                heater.lastFilamentTemperature.set(0);
                heater.nozzleFirstLayerTargetTemperature.set(0);
                heater.nozzleTargetTemperature.set(0);
                heater.nozzleTemperature.set(0);
                heater.filamentID.set("");

                nozzleHeaterIndex++;
            }

            int nozzleIndex = 0;
            for (Nozzle nozzle : nozzles)
            {
                NozzleData nozzleData
                        = referenceHeadData.getNozzles().get(nozzleIndex);

                nozzle.diameter.set(nozzleData.getDiameter());
                nozzle.xOffset.set(nozzleData.getDefaultXOffset());
                nozzle.yOffset.set(nozzleData.getDefaultYOffset());
                nozzle.zOffset.set(nozzleData.getDefaultZOffset());
                nozzle.bOffset.set(nozzleData.getDefaultBOffset());
                nozzle.BPosition.set(0);

                nozzleIndex++;
            }

            dataChanged.set(!dataChanged.get());

            steno.info("Reset head to defaults with data set - " + referenceHeadData.getTypeCode());
        } else
        {
            steno.warning(
                    "Attempt to reset head to defaults failed - reference data cannot be derived");
        }
    }

    public static boolean isTypeCodeValid(String typeCode)
    {
        boolean typeCodeIsValid = false;

        if (typeCode != null && typeCode.length() == 8)
            typeCodeIsValid = typeCode.matches("R[BX][0-9a-zA-Z]{3}-[0-9a-zA-Z]{2}");
        return typeCodeIsValid;
    }

    public static boolean isTypeCodeInDatabase(String typeCode)
    {
        boolean typeCodeIsInDatabase = false;

        if (typeCode != null
                && HeadContainer.getHeadByID(typeCode) != null)
        {
            typeCodeIsInDatabase = true;
        }

        return typeCodeIsInDatabase;
    }

    @Override
    public void allocateRandomID()
    {
        String idToCreate = typeCode.get() + SystemUtils.generate16DigitID().substring(typeCode.
                get().length());
        uniqueID.set(idToCreate);

        dataChanged.set(!dataChanged.get());
    }

    public void setUniqueID(String typeCodeInput,
            String weekNumberInput,
            String yearNumberInput,
            String poNumberInput,
            String serialNumberInput,
            String checksumInput)
    {
        typeCode.set(typeCodeInput);
        weekNumber.set(weekNumberInput);
        yearNumber.set(yearNumberInput);
        PONumber.set(poNumberInput);
        serialNumber.set(serialNumberInput);
        checksum.set(checksumInput);
        
        uniqueID.set(typeCodeInput + weekNumberInput + yearNumberInput + poNumberInput + serialNumberInput + checksumInput);
        
        dataChanged.set(!dataChanged.get());
    }

    public static String getFormattedSerial(String typeCode,
            String weekNumber,
            String yearNumber,
            String poNumber,
            String serialNumber,
            String checksum)
    {
        StringBuilder formattedHeadSerial = new StringBuilder();
        formattedHeadSerial.append(typeCode);
        formattedHeadSerial.append("-");
        formattedHeadSerial.append(weekNumber);
        formattedHeadSerial.append(yearNumber);
        formattedHeadSerial.append("-");
        formattedHeadSerial.append(poNumber);
        formattedHeadSerial.append("-");
        formattedHeadSerial.append(serialNumber);
        formattedHeadSerial.append("-");
        formattedHeadSerial.append(checksum);

        return formattedHeadSerial.toString();
    }

    public String getFormattedSerial()
    {
        return getFormattedSerial(typeCodeProperty().get(),
                getWeekNumber(),
                getYearNumber(),
                getPONumber(),
                getSerialNumber(),
                getChecksum());
}

    public static boolean validateSerial(String typeCodeInput,
            String weekNumberInput,
            String yearNumberInput,
            String poNumberInput,
            String serialNumberInput,
            String checksumInput)
    {
        boolean everythingIsGroovy = true;

        if (typeCodeInput != null
                && weekNumberInput != null
                && yearNumberInput != null
                && poNumberInput != null
                && serialNumberInput != null
                && checksumInput != null)
        {
            everythingIsGroovy = isTypeCodeValid(typeCodeInput);

            try
            {
                everythingIsGroovy &= weekNumberInput.length() == 2;
                int weekVal = Integer.valueOf(weekNumberInput);

                everythingIsGroovy &= yearNumberInput.length() == 2;
                int yearVal = Integer.valueOf(yearNumberInput);

                everythingIsGroovy &= poNumberInput.length() == 7;
                int poVal = Integer.valueOf(poNumberInput);

                everythingIsGroovy &= serialNumberInput.length() == 4;
                int serialVal = Integer.valueOf(serialNumberInput);

                everythingIsGroovy &= checksumInput.length() == 1;
                int checksumVal = Integer.valueOf(checksumInput);

                String stringToChecksum = typeCodeInput
                        + weekNumberInput
                        + yearNumberInput
                        + poNumberInput
                        + serialNumberInput;
                try
                {
                    char checkDigit = SystemUtils.generateUPSModulo10Checksum(stringToChecksum.replaceAll("-", ""));
                    everythingIsGroovy &= checksumInput.charAt(0) == checkDigit;
                } catch (InvalidChecksumException ex)
                {
                    everythingIsGroovy = false;
                }

            } catch (NumberFormatException ex)
            {
                everythingIsGroovy = false;
            }
        } else
        {
            everythingIsGroovy = false;
        }

        return everythingIsGroovy;
    }
}
