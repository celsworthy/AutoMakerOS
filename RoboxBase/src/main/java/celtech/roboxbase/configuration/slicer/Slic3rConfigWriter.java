package celtech.roboxbase.configuration.slicer;

import celtech.roboxbase.configuration.RoboxProfile;
import celtech.roboxbase.configuration.SlicerType;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

/**
 *
 * @author Ian
 */
public class Slic3rConfigWriter extends SlicerConfigWriter
{

    public Slic3rConfigWriter() {
        super();
        slicerType = SlicerType.Slic3r;
        PRINT_PROFILE_SETTINGS_CONTAINER.getDefaultPrintProfileSettingsForSlicer(slicerType).getAllSettings()
                .forEach(setting -> printProfileSettingsMap.put(setting.getId(), setting));
    }

    @Override
    void bringDataInBounds(RoboxProfile profileData) {
        if (profileData.getSpecificSettingAsString("fillPattern").equals("line")
            && profileData.getSpecificFloatSetting("fillDensity_normalised") >= 0.99f) {
            profileData.addOrOverride("fillDensity_normalised", "0.99");
        }
    }

    @Override
    protected void outputLine(FileWriter writer, String variableName, boolean value) throws IOException
    {
        int valueToWrite = (value) ? 1 : 0;
        writer.append(variableName + " = " + valueToWrite + "\n");
    }

    @Override
    protected void outputLine(FileWriter writer, String variableName, int value) throws IOException
    {
        writer.append(variableName + " = " + value + "\n");
    }

    @Override
    protected void outputLine(FileWriter writer, String variableName, float value) throws IOException
    {
        writer.append(variableName + " = " + threeDPformatter.format(value) + "\n");
    }

    @Override
    protected void outputLine(FileWriter writer, String variableName, String value) throws IOException
    {
        writer.append(variableName + " = " + value + "\n");
    }

    @Override
    protected void outputLine(FileWriter writer, String variableName, SlicerType value) throws IOException
    {
        writer.append(variableName + " = " + value + "\n");
    }

    @Override
    protected void outputLine(FileWriter writer, String variableName, Enum value) throws IOException
    {
        writer.append(variableName + " = " + value.name().toLowerCase() + "\n");
    }

    @Override
    protected void outputPrintCentre(FileWriter writer, float centreX, float centreY) throws IOException
    {
        //As of 1.2.9 slic3r doesn't seem to take any notice of this variable in the config file
        //print-center must be set on the command line
    }

    @Override
    protected void outputFilamentDiameter(FileWriter writer, float diameter) throws IOException
    {
        outputLine(writer, "filament_diameter", String.format(Locale.UK, "%f", diameter));
    }
}
