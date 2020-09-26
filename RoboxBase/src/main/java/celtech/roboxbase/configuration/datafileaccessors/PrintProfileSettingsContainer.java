package celtech.roboxbase.configuration.datafileaccessors;

import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.configuration.SlicerType;
import celtech.roboxbase.configuration.profilesettings.PrintProfileSetting;
import celtech.roboxbase.configuration.profilesettings.PrintProfileSettings;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.util.Pair;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author George Salter
 */
public class PrintProfileSettingsContainer {
    
    private static final Stenographer STENO = StenographerFactory.getStenographer(
            PrintProfileSettingsContainer.class.getName());
    
    private static PrintProfileSettingsContainer instance;
    
    private static Map<SlicerType, PrintProfileSettings> printProfileSettings;
    private static Map<SlicerType, PrintProfileSettings> defaultPrintProfileSettings;
    
    private PrintProfileSettingsContainer() {
        printProfileSettings = new HashMap<>();
        defaultPrintProfileSettings = new HashMap<>();
        loadPrintProfileSettingsFile();
    }
    
    public static PrintProfileSettingsContainer getInstance() {
        if(instance == null) {
            instance = new PrintProfileSettingsContainer();
        }
        return instance;
    }
    
    public PrintProfileSettings getPrintProfileSettingsForSlicer(SlicerType slicerType) {
        return printProfileSettings.get(slicerType);
    }
    
    public PrintProfileSettings getDefaultPrintProfileSettingsForSlicer(SlicerType slicerType) {
        return defaultPrintProfileSettings.get(slicerType);
    }
    
    public Map<String, List<PrintProfileSetting>> compareAndGetDifferencesBetweenSettings(PrintProfileSettings originalSettings, PrintProfileSettings newSettings) {
        Map<String, List<PrintProfileSetting>> changedValuesMap = new HashMap<>();
        
        List<Pair<PrintProfileSetting, String>> originalSettingsList = originalSettings.getAllEditableSettingsWithSections();
        List<Pair<PrintProfileSetting, String>> newSettingsList = newSettings.getAllEditableSettingsWithSections();

        originalSettingsList.forEach(settingToSection ->
        {      
            String sectionTitle = BaseLookup.i18n(settingToSection.getValue());
            PrintProfileSetting originalSetting = settingToSection.getKey();
            
            // From the new settings find one with the same id and different vaue from the old setting
            Optional<PrintProfileSetting> possibleChangedSetting = newSettingsList.stream()
                    .map(newSettingToSection -> { return newSettingToSection.getKey(); })
                    .filter(newSetting -> originalSetting.getId().equals(newSetting.getId()))
                    .filter(newSetting -> !originalSetting.getValue().equals(newSetting.getValue()))
                    .findFirst();

            // If we have a changed value, add the setting to the map in the correct section
            if(possibleChangedSetting.isPresent()) 
            {
                if (changedValuesMap.containsKey(sectionTitle))
                {
                    changedValuesMap.get(sectionTitle).add(possibleChangedSetting.get());
                } else
                {
                    changedValuesMap.put(sectionTitle, new ArrayList(Arrays.asList(possibleChangedSetting.get())));
                }
            }
        });

        return changedValuesMap;
    }
    
    public static void loadPrintProfileSettingsFile() 
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());
        
        File curaPrintProfileSettingsFile = new File(BaseConfiguration.getPrintProfileSettingsFileLocation(SlicerType.Cura));
        File cura4PrintProfileSettingsFile = new File(BaseConfiguration.getPrintProfileSettingsFileLocation(SlicerType.Cura4));
        
        STENO.debug("File path for cura print profile settings file: " + curaPrintProfileSettingsFile.getAbsolutePath());
        STENO.debug("File path for cura4 print profile settings file: " + cura4PrintProfileSettingsFile.getAbsolutePath());
        
        try 
        {
            PrintProfileSettings curaPrintProfileSettings = objectMapper.readValue(curaPrintProfileSettingsFile, PrintProfileSettings.class);
            PrintProfileSettings cura4PrintProfileSettings = objectMapper.readValue(cura4PrintProfileSettingsFile, PrintProfileSettings.class);
            
            printProfileSettings.put(SlicerType.Cura, curaPrintProfileSettings);
            printProfileSettings.put(SlicerType.Cura4, cura4PrintProfileSettings);
            
            defaultPrintProfileSettings.put(SlicerType.Cura, new PrintProfileSettings(curaPrintProfileSettings));
            defaultPrintProfileSettings.put(SlicerType.Cura4, new PrintProfileSettings(cura4PrintProfileSettings));
        } catch (IOException ex)
        {
            STENO.error(ex.getMessage());
        }
    }
}
