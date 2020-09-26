package celtech.roboxbase.configuration.utils;

import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.configuration.RoboxProfile;
import celtech.roboxbase.configuration.SlicerType;
import celtech.roboxbase.utils.FileUtilities;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author George Salter
 */
public class RoboxProfileUtils 
{
    private static final Stenographer STENO = StenographerFactory.getStenographer(RoboxProfileUtils.class.getName());
    
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    private static final String NOZZLE_PARAMETERS = "nozzleParameters";
    private static final String EJECTION_VOLUME = "ejectionVolume";
    private static final String PARTIAL_B_MINIMUM = "partialBMinimum";
    private static final String FILL_PATTERN = "fillPattern";
    private static final String SUPPORT_PATTERN = "supportPattern";
    private static final String HEAD_TYPE = "headType";
    private static final String PROFILE_NAME = "profileName";
    private static final String EQUALS = "=";
    private static final String COLON = ":";
    private static final String SINGLE_NOZZLE = "RBXDV-S1";
    private static final String ARCHIVE_FOLDER_NAME = "Archive";
    
    private static final Map<String, String> ENUM_CONVERSION_MAP = new HashMap<>();
    
    static
    {
        ENUM_CONVERSION_MAP.put("LINE", "line");
        ENUM_CONVERSION_MAP.put("RECTILINEAR", "rectlinear");
        ENUM_CONVERSION_MAP.put("RECTILINEAR_GRID", "rectlinear_grid");
    }
    
    /**
     * Calculate the raft offset.
     * 
     * @param profileSettings needed to find the total size of the raft
     * @param slicerType the slicer in use
     * @return the raftOffset as a double
     */
    public static double calculateRaftOffset(RoboxProfile profileSettings, SlicerType slicerType)
    {
        double raftOffset = profileSettings.getSpecificFloatSetting("raftBaseThickness_mm")
                        //Raft interface thickness
                        + 0.28
                        //Raft surface layer thickness * surface layers
                        + (profileSettings.getSpecificIntSetting("raftInterfaceLayers")* 0.27)
                        + profileSettings.getSpecificFloatSetting("raftAirGapLayer0_mm");

        return raftOffset;
    }
    
    /**
     * A method of converting the .roboxprofile files from 3.02.00 and earlier to the new format.
     * The old files are added to an Archive folder and the converted files are added to the correct
     * head folder withing the custom Cura print profile folder.
     * 
     * @param oldProfile Path of the old print profile
     * @param userProfileDirectory
     * @return the path of the new converted file
     * @throws IOException 
     */
    public static String convertOldProfileIntoNewFormat(Path oldProfile, Path userProfileDirectory) throws IOException
    { 
        StringBuilder contentBuilder = new StringBuilder();
        JsonNode nozzleParameters = null;
        String headType = "";
        String profileName = "";
        
        JsonNode profileNode = MAPPER.readTree(oldProfile.toFile());
        Iterator<Map.Entry<String, JsonNode>> settings = profileNode.fields();

        while(settings.hasNext()) 
        {
            Map.Entry<String, JsonNode> setting = settings.next();
            switch (setting.getKey())
            {
                case NOZZLE_PARAMETERS:
                {
                    nozzleParameters = setting.getValue();
                    break;
                }
                case HEAD_TYPE:
                {
                    headType = setting.getValue().asText();
                    break;
                }
                case PROFILE_NAME:
                {
                    profileName = setting.getValue().asText();
                    break;
                }
                case FILL_PATTERN:
                case SUPPORT_PATTERN:
                {
                    String convertedEnum = ENUM_CONVERSION_MAP.get(setting.getValue().asText());
                    setting.setValue(new TextNode(convertedEnum));
                    break;
                }
            }
            
            if (!setting.getKey().equals(NOZZLE_PARAMETERS))
            {
                contentBuilder.append(setting.getKey())
                            .append(EQUALS)
                            .append(setting.getValue().asText())
                            .append(System.lineSeparator());
            }
        }
        
        dealWithNozzleParameters(nozzleParameters, headType, contentBuilder);
        
        // Copy contents to new file
        String headFolderPath = FileUtilities.findOrCreateFileInDir(userProfileDirectory, headType);
        String newProfilePath = headFolderPath + File.separator + profileName + BaseConfiguration.printProfileFileExtension;
        File newProfile = new File(newProfilePath);
        
        if(newProfile.exists())
        {
            // We cannot copy as file already exists with same name
            STENO.error("Problem when trying to convert old profile: " + profileName 
                    + ". Profile with same name already exists at : " + newProfilePath);
        } else
        {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(newProfile)))
            {
                writer.append(contentBuilder);
            }
        }
        
        // Archive old file
        String archiveFolderPath = FileUtilities.findOrCreateFileInDir(userProfileDirectory.getParent(), ARCHIVE_FOLDER_NAME);
        String archivedFilePath = archiveFolderPath + File.separator + oldProfile.getFileName().toString();
        FileUtils.moveFile(oldProfile.toFile(), new File(archivedFilePath));
        
        return newProfilePath;
    }
    
    private static void dealWithNozzleParameters(JsonNode nozzleParameters, String headType, StringBuilder contentBuilder)
    {
        if (nozzleParameters.isArray() && nozzleParameters.size() == 2)
        {
            JsonNode nozzle0 = nozzleParameters.get(0);
            JsonNode nozzle1 = nozzleParameters.get(1);

            addNozzleValue(nozzle0, nozzle1, contentBuilder, EJECTION_VOLUME, headType);
            addNozzleValue(nozzle0, nozzle1, contentBuilder, PARTIAL_B_MINIMUM, headType);
        }
    }
    
    private static void addNozzleValue(JsonNode nozzle0, 
            JsonNode nozzle1, 
            StringBuilder contentBuilder, 
            String settingName, 
            String headType)
    {
        contentBuilder.append(settingName)
                    .append(EQUALS)
                    .append(nozzle0.findValue(settingName).asText());
        
        if (!headType.equalsIgnoreCase(SINGLE_NOZZLE))
        {
            contentBuilder.append(COLON)
                    .append(nozzle1.findValue(settingName).asText());
        }
        
        contentBuilder.append(System.lineSeparator());
    }
}
