package celtech.roboxbase.configuration.slicer;

import celtech.roboxbase.configuration.RoboxProfile;
import celtech.roboxbase.configuration.SlicerType;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

/**
 *
 * @author George Salter
 */
public class Cura4ConfigWriter extends SlicerConfigWriter {

    public Cura4ConfigWriter()
    {
        super();
        slicerType = SlicerType.Cura4;
        PRINT_PROFILE_SETTINGS_CONTAINER.getDefaultPrintProfileSettingsForSlicer(slicerType).getAllSettings()
                .forEach(setting -> printProfileSettingsMap.put(setting.getId(), setting));
    }
    
    @Override
    protected void outputLine(FileWriter writer, String variableName, boolean value) throws IOException
    {
        writer.append(variableName + "=" + value + "\n");
    }

    @Override
    protected void outputLine(FileWriter writer, String variableName, int value) throws IOException
    {
        writer.append(variableName + "=" + value + "\n");
    }

    @Override
    protected void outputLine(FileWriter writer, String variableName, float value) throws IOException
    {
        writer.append(variableName + "=" + threeDPformatter.format(value) + "\n");
    }

    @Override
    protected void outputLine(FileWriter writer, String variableName, String value) throws IOException
    {
        writer.append(variableName + "=" + value + "\n");
    }

    @Override
    protected void outputLine(FileWriter writer, String variableName, SlicerType value) throws IOException
    {
        writer.append(variableName + "=" + value + "\n");
    }

    @Override
    protected void outputLine(FileWriter writer, String variableName, Enum value) throws IOException
    {
        writer.append(variableName + "=" + value.name().toLowerCase() + "\n");
    }

    @Override
    protected void outputPrintCentre(FileWriter writer, float centreX, float centreY) throws IOException
    {
    }

    @Override
    protected void outputFilamentDiameter(FileWriter writer, float diameter) throws IOException
    {
        outputLine(writer, "material_diameter", String.format(Locale.UK, "%f", diameter));
    }

    @Override
    void bringDataInBounds(RoboxProfile profileData) {
    }
    
}
