package celtech.roboxbase.configuration.datafileaccessors;

import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.configuration.HeadFileFilter;
import celtech.roboxbase.configuration.fileRepresentation.HeadFile;
import celtech.roboxbase.configuration.fileRepresentation.StylusSettings;
import celtech.roboxbase.printerControl.model.Head.HeadType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author ianhudson
 */
public class StylusSettingsContainer
{

    private static final Stenographer steno = StenographerFactory.getStenographer(StylusSettingsContainer.class.getName());
    private static StylusSettingsContainer instance = null;
    private final ObservableList<StylusSettings> completeStylusSettingsList;
    private final ObservableMap<String, StylusSettings> completeStylusSettingsMap = FXCollections.observableHashMap();
    private final ObjectMapper mapper = new ObjectMapper();
    private List<Runnable> listeners = new ArrayList<>();
    
    private StylusSettingsContainer()
    {
        FileFilter filter = (p -> p.getName().endsWith(BaseConfiguration.stylusSettingsFileExtension));
        File settingsDirHandle = new File(BaseConfiguration.getApplicationStylusSettingsDirectory());
        File[] stylusSettingFiles = settingsDirHandle.listFiles(filter);
        ingestSettingsFiles(stylusSettingFiles, true);
        settingsDirHandle = new File(BaseConfiguration.getUserStylusSettingsDirectory());
        stylusSettingFiles = settingsDirHandle.listFiles(filter);
        ingestSettingsFiles(stylusSettingFiles, false);
        completeStylusSettingsList = FXCollections.observableArrayList(completeStylusSettingsMap.values());
        Collections.sort(completeStylusSettingsList, (s1, s2) -> s1.getName().compareToIgnoreCase(s2.getName()));
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    }

    private void ingestSettingsFiles(File[] settingsFiles, boolean readOnly)
    {
        if (settingsFiles != null)
        {
            for (File settingsFile : settingsFiles)
            {
                try
                {
                    StylusSettings settingsData = mapper.readValue(settingsFile, StylusSettings.class);
                    settingsData.setReadOnly(readOnly);
                    if (readOnly && completeStylusSettingsMap.containsKey(settingsData.getName()))
                    {
                        steno.warning("Discarding stylus settings from " + settingsFile.getAbsolutePath());
                    }
                    else
                    {
                        completeStylusSettingsMap.put(settingsData.getName(), settingsData);
                    }
                }
                catch (IOException ex)
                {
                    steno.error("Error loading stylus settings from " + settingsFile.getAbsolutePath());
                }
            }
        }
    }

    public static StylusSettingsContainer getInstance()
    {
        if (instance == null)
            instance = new StylusSettingsContainer();
        return instance;
    }

    public Optional<StylusSettings> getSettingsByName(String settingsName)
    {
        StylusSettings namedSettings = instance.completeStylusSettingsMap.getOrDefault(settingsName, null);
        return Optional.ofNullable(namedSettings);
    }

    public ObservableList<StylusSettings> getCompleteSettingsList()
    {
        return instance.completeStylusSettingsList;
    }
    
    public void saveSettings(StylusSettings settingsData)
    {
        StylusSettings freshSettings = new StylusSettings();
        freshSettings.setFrom(settingsData);
        freshSettings.setName(freshSettings.getName().trim());
        saveSettingsToFile(freshSettings);
    }
    
    private String sanitizeFilename(String inputName)
    {
        return inputName.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
    }
    
    private void saveSettingsToFile(StylusSettings settingsData)
    {
        StylusSettings existingSettings = completeStylusSettingsMap.getOrDefault(settingsData.getName(), null);
        if (existingSettings != null && existingSettings.isReadOnly())
        {
            steno.error("Can't overwrite existing read-only settings \"" +  settingsData.getName() + "\"");
        }
        else
        {
            checkUserDirectory();
            writeSettingsToFile(settingsData);
            String sName = settingsData.getName();
            completeStylusSettingsMap.put(sName, settingsData);
            int comparison = 1;
            int sIndex = -1;
            for (sIndex = 0; sIndex < completeStylusSettingsList.size(); ++sIndex)
            {
                comparison = sName.compareToIgnoreCase(completeStylusSettingsList.get(sIndex).getName());
                if (comparison <= 0)
                    break;
            }
            if (comparison == 0)
                completeStylusSettingsList.set(sIndex, settingsData);
            else
                completeStylusSettingsList.add(sIndex, settingsData);
           fireWhenSettingsChanged();
        }
    }

    public void saveAllSettings()
    {
        checkUserDirectory();
        completeStylusSettingsList.stream()
                                  .filter(StylusSettings::isModified)
                                  .forEach(ss -> writeSettingsToFile(ss));
    }

    private void checkUserDirectory()
    {
        String userDirectory = BaseConfiguration.getUserStylusSettingsDirectory();
        File dirHandle = new File(userDirectory);
        if (!dirHandle.exists())
            dirHandle.mkdirs();
    }

    private void writeSettingsToFile(StylusSettings settingsData)
    {
        String userFilePath = BaseConfiguration.getUserStylusSettingsDirectory()
                                + sanitizeFilename(settingsData.getName())
                                + ".stylussettings";
        try
        {
            File userFile = new File(userFilePath);
            mapper.writeValue(userFile, settingsData);
        }
        catch (IOException ex)
        {
            steno.error("Error trying to user stylus settings to \"" + userFilePath + "\"");
        }
    }
 
    public void addListener(Runnable listener)
    {
        listeners.add(listener);
    }

    public void removeListener(Runnable listener)
    {
        listeners.remove(listener);
    }

    private void fireWhenSettingsChanged()
    {
        List<Runnable> listToIterateThrough = listeners.stream().collect(Collectors.toList());
        listToIterateThrough.forEach(l -> l.run());
    }
}
