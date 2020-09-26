package celtech.roboxbase.configuration.slicer;

import celtech.roboxbase.configuration.RoboxProfile;
import celtech.roboxbase.configuration.SlicerType;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author Ian
 */
public class CuraConfigWriter extends SlicerConfigWriter
{
    private static final String SUPPORT_TYPE_SETTING = "supportType";
    private static final Map<String, String> OPTION_TO_NUMBER_MAP = new HashMap<>();
    
    public CuraConfigWriter()
    {
        super();
        slicerType = SlicerType.Cura;
        PRINT_PROFILE_SETTINGS_CONTAINER.getDefaultPrintProfileSettingsForSlicer(slicerType).getAllSettings()
                .forEach(setting -> printProfileSettingsMap.put(setting.getId(), setting));
        
        OPTION_TO_NUMBER_MAP.put("rectlinear_grid", "0");
        OPTION_TO_NUMBER_MAP.put("rectlinear", "1");
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
        if (variableName.equals(SUPPORT_TYPE_SETTING))
        {
            value = OPTION_TO_NUMBER_MAP.containsKey(value) ? OPTION_TO_NUMBER_MAP.get(value) : value;
        }
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
        writer.append(variableName + "=" + value + "\n");
    }

    @Override
    protected void outputPrintCentre(FileWriter writer, float centreX, float centreY) throws IOException
    {
    }

    @Override
    protected void outputFilamentDiameter(FileWriter writer, float diameter) throws IOException
    {
        outputLine(writer, "filamentDiameter", String.format(Locale.UK, "%d",
                                                             (int) (diameter * 1000)));
    }

    @Override
    void bringDataInBounds(RoboxProfile profileData)
    {
    }
    
}
