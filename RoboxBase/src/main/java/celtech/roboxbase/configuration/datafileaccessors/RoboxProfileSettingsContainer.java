package celtech.roboxbase.configuration.datafileaccessors;

import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.configuration.RoboxProfile;
import celtech.roboxbase.configuration.SlicerType;
import celtech.roboxbase.configuration.profilesettings.PrintProfileSetting;
import celtech.roboxbase.utils.FileUtilities;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author George Salter
 */
public class RoboxProfileSettingsContainer {
    
    private static final Stenographer STENO = StenographerFactory.getStenographer(
            RoboxProfileSettingsContainer.class.getName());
    
    private static RoboxProfileSettingsContainer instance;
    
    private static final String TITLE_BORDER = "//==============";
    private static final String METADATA = "Metadata";
    private static final String PROFILE_NAME = "profileName";
    private static final String HEAD_TYPE = "headType";
    
    private static Map<String, List<RoboxProfile>> curaRoboxProfiles;
    private static Map<String, List<RoboxProfile>> cura4RoboxProfiles;
    private static Map<String, List<RoboxProfile>> slic3rRoboxProfiles;
    
    private static Map<String, ObservableList<RoboxProfile>> curaCustomRoboxProfiles;
    private static Map<String, ObservableList<RoboxProfile>> cura4CustomRoboxProfiles;
    private static Map<String, ObservableList<RoboxProfile>> slic3rCustomRoboxProfiles;
    
    
    public RoboxProfileSettingsContainer() {
        curaRoboxProfiles = new HashMap<>();
        cura4RoboxProfiles = new HashMap<>();
        slic3rRoboxProfiles = new HashMap<>();
        curaCustomRoboxProfiles = new HashMap<>();
        cura4CustomRoboxProfiles = new HashMap<>();
        slic3rCustomRoboxProfiles = new HashMap<>();
        loadRoboxProfiles(SlicerType.Cura);
        loadRoboxProfiles(SlicerType.Cura4);
    }
    
    public static RoboxProfileSettingsContainer getInstance() {
        if(instance == null) {
            instance = new RoboxProfileSettingsContainer();
        }
        return instance;
    }
    
    public Map<String, List<RoboxProfile>> getRoboxProfilesForSlicer(SlicerType slicerType) {
        switch (slicerType) {
            case Cura:
                return curaRoboxProfiles;
            case Cura4:
                return cura4RoboxProfiles;
            case Slic3r:
                return slic3rRoboxProfiles;
            default:
                return new HashMap<>();
        }
    }
    
    public Map<String, ObservableList<RoboxProfile>> getCustomRoboxProfilesForSlicer(SlicerType slicerType) {
        switch (slicerType) {
            case Cura:
                return curaCustomRoboxProfiles;
            case Cura4:
                return cura4CustomRoboxProfiles;
            case Slic3r:
                return slic3rCustomRoboxProfiles;
            default:
                return new HashMap<>();
        }
    }
    
    public Optional<RoboxProfile> getRoboxProfileWithName(String profileName, SlicerType slicerType, String headType) {
        Optional<RoboxProfile> roboxProfile = Optional.empty();
        try {
            List<RoboxProfile> profilesForHead = getRoboxProfilesForSlicer(slicerType).get(headType);
            if (profilesForHead != null) {
                roboxProfile = profilesForHead.stream()
                        .filter(profile -> profile.getName().equals(profileName))
                        .findAny();
            }
        }
        catch (Exception ex) {
            STENO.exception("Failed to get profile with name", ex);
        }
            
        return roboxProfile;
    }
    
    public RoboxProfile loadHeadProfileForSlicer(String headType, SlicerType slicerType) {
        File slicerApplicationProfileDirectory = new File(BaseConfiguration.getApplicationPrintProfileDirectoryForSlicer(slicerType));
        for(File headDir : slicerApplicationProfileDirectory.listFiles()) {
            if(headDir.getName().equals(headType)) {
                Map<String, String> settingsMap = loadHeadSettingsIntoMap(headType, slicerType);
                RoboxProfile headProfile = new RoboxProfile(headType, headType, true, settingsMap);
                return headProfile;
            }
        }
        
        return null;
    }
    
    public RoboxProfile saveCustomProfile(Map<String, List<PrintProfileSetting>> settingsToWrite, String nameForProfile, 
            String headType, SlicerType slicerType) {
        List<RoboxProfile> allProfilesForHead = getRoboxProfilesForSlicer(slicerType).get(headType);
        List<RoboxProfile> customProfilesForHead = getCustomRoboxProfilesForSlicer(slicerType).get(headType);
        RoboxProfile roboxProfile;
        
        Optional<RoboxProfile> existingProfile = customProfilesForHead.stream()
                .filter(profile -> profile.getName().equals(nameForProfile))
                .findAny();
        if(existingProfile.isPresent()) {
            customProfilesForHead.remove(existingProfile.get());
            allProfilesForHead.remove(existingProfile.get());
            roboxProfile = saveUserProfile(nameForProfile, slicerType, settingsToWrite, headType);
        } else {
            roboxProfile = saveUserProfile(nameForProfile, slicerType, settingsToWrite, headType);
        }
        customProfilesForHead.add(roboxProfile);
        allProfilesForHead.add(roboxProfile);
        return roboxProfile;
    }
    
    public void deleteCustomProfile(String profileName, SlicerType slicerType, String headType) {
        ObservableList<RoboxProfile> customRoboxProfiles = getCustomRoboxProfilesForSlicer(slicerType).get(headType);
        List<RoboxProfile> allRoboxProfiles = getRoboxProfilesForSlicer(slicerType).get(headType);
        Optional<RoboxProfile> profileToDelete = customRoboxProfiles.stream()
                .filter(profile -> profile.getName().equals(profileName))
                .findAny();
        if(profileToDelete.isPresent()) {
            allRoboxProfiles.remove(profileToDelete.get());
            customRoboxProfiles.remove(profileToDelete.get());
        } else {
            STENO.error("File " + profileName + ", doesn't exist in profiles list for slicer: " + slicerType);
        }
        
        String filePath = BaseConfiguration.getUserPrintProfileDirectoryForSlicer(slicerType) + "/" + headType + "/" 
                + profileName + BaseConfiguration.printProfileFileExtension;
        File fileToDelete = new File(filePath);
        if(fileToDelete.exists()) {
            fileToDelete.delete();
        } else {
            STENO.error("File could not be deleted as it doesn't exist. File path: " + filePath);
        }
    }
    
    public void addProfileChangeListener(ListChangeListener listChangeListener) {
        for(SlicerType slicerType : SlicerType.values()) {
            getCustomRoboxProfilesForSlicer(slicerType).values()
                    .forEach(observableList -> observableList.addListener(listChangeListener));
        }
    }
    
    public void removeProfileChangeListener(ListChangeListener listChangeListener) {
        for(SlicerType slicerType : SlicerType.values()) {
            getCustomRoboxProfilesForSlicer(slicerType).values()
                    .forEach(observableList -> observableList.removeListener(listChangeListener));
        }
    }
    
    private void loadRoboxProfiles(SlicerType slicerType) {
        File applicationProfileDirectory = new File(BaseConfiguration.getApplicationPrintProfileDirectoryForSlicer(slicerType));
        File userProfileDirectory = new File(BaseConfiguration.getUserPrintProfileDirectoryForSlicer(slicerType));
        loadRoboxProfilesIntoMap(applicationProfileDirectory, slicerType, true);
        loadRoboxProfilesIntoMap(userProfileDirectory, slicerType, false);
    }
    
    private void loadRoboxProfilesIntoMap(File profileDirectory, SlicerType slicerType, boolean standardProfile) 
    {
        Map<String, List<RoboxProfile>> allProfilesMap = getRoboxProfilesForSlicer(slicerType);
        Map<String, ObservableList<RoboxProfile>> customProfilesMap = getCustomRoboxProfilesForSlicer(slicerType);
        
        if(profileDirectory.exists())
        {
            for(File headDir : profileDirectory.listFiles()) 
            {
                if(headDir.isDirectory()) 
                {
                    String headType = headDir.getName();
                    List<String> usedNames = allProfilesMap.getOrDefault(headType, new ArrayList<>()).stream()
                            .map(profile -> profile.getName())
                            .collect(Collectors.toList());
                    Map<String, String> settings = loadHeadSettingsIntoMap(headType, slicerType);

                    List<RoboxProfile> allRoboxProfiles = new ArrayList<>();
                    ObservableList<RoboxProfile> customRoboxProfiles = FXCollections.observableArrayList();

                    for(File profile : headDir.listFiles())
                    {
                        String profileName = profile.getName().split("\\.")[0];
                        if(!profileName.equals(headType) && !usedNames.contains(profileName))
                        {
                            Map<String, String> profileSettings = new HashMap<>(settings);
                            addOrOverriteSettings(profile, profileSettings);
                            RoboxProfile roboxProfile = new RoboxProfile(profileName, headType, standardProfile, profileSettings);
                            allRoboxProfiles.add(roboxProfile);
                            if(!standardProfile) 
                            {
                                customRoboxProfiles.add(roboxProfile);
                            }
                            usedNames.add(profileName);
                        }
                    }

                    if(allProfilesMap.containsKey(headType)) 
                    {
                        allProfilesMap.get(headType).addAll(allRoboxProfiles);
                    } else 
                    {
                        allProfilesMap.put(headType, allRoboxProfiles);
                    }

                    if(customProfilesMap.containsKey(headType)) 
                    {
                        customProfilesMap.get(headType).addAll(customRoboxProfiles);
                    } else 
                    {
                        customProfilesMap.put(headType, customRoboxProfiles);
                    }
                }
            }
        }
    }
    
    private static Map<String, String> loadHeadSettingsIntoMap(String headType, SlicerType slicerType) 
    {
        File headDirectory = new File(BaseConfiguration.getApplicationPrintProfileDirectoryForSlicer(slicerType) + headType);

        File[] headFilesFiltered = headDirectory.listFiles((File dir, String name) -> {
            return name.split("\\.")[0].equals(headType);
        });
           
        if(headFilesFiltered.length == 0) 
        {
            STENO.warning("No head profile exists in folder: " + headDirectory.getPath());
            STENO.warning("Creating empty map for settings");
            return new HashMap<>();
        }
        
        File headFile = headFilesFiltered[0];
        Map<String, String> settingsMap = new HashMap<>();
        
        addOrOverriteSettings(headFile, settingsMap);
        
        return settingsMap;
    }
    
    private static void addOrOverriteSettings(File settings, Map<String, String> settingsMap) {
         try(BufferedReader br = new BufferedReader(new FileReader(settings))) {
            String line;
            while((line = br.readLine()) != null) {
                if(!(line.trim().startsWith("//") || line.trim().equals(""))) {
                    String[] keyValuePair = line.split("=");
                    settingsMap.put(keyValuePair[0], keyValuePair[1]);
                }
            }
        } catch (FileNotFoundException ex) {
            STENO.error(ex.getMessage());
        } catch (IOException ex) {
            STENO.error("Error when reading file: " + settings.getPath());
            STENO.error(ex.getMessage());
        }
    }
    
    private RoboxProfile saveUserProfile(String profileName, SlicerType slicerType, 
            Map<String, List<PrintProfileSetting>> settingsToWrite, String headType) 
    {
        String headDirPath = FileUtilities.findOrCreateFileInDir(Paths.get(BaseConfiguration.getUserPrintProfileDirectoryForSlicer(slicerType)), headType);
        
        String profileFilePath = headDirPath + "/" + profileName + BaseConfiguration.printProfileFileExtension;
        File file = new File(profileFilePath);
        if(file.exists()) 
        {
            file.delete();
        }

        List<String> metaData = new ArrayList<>();
        metaData.add(PROFILE_NAME + "=" + profileName);
        metaData.add(HEAD_TYPE + "=" + headType);
        writeRoboxProfile(profileFilePath, settingsToWrite, metaData);
        
        Map<String, String> settingsMap = loadHeadSettingsIntoMap(headType, slicerType);
        addOrOverriteSettings(new File(profileFilePath), settingsMap);
        RoboxProfile roboxProfile = new RoboxProfile(profileName, headType, false, settingsMap);
        return roboxProfile;
    }
    
    private void writeRoboxProfile(String profileFilePath, Map<String, List<PrintProfileSetting>> settingsToWrite, List<String> metaData) {
        try (PrintWriter printWriter = new PrintWriter(new FileWriter(profileFilePath))) {
            
            printWriter.println(TITLE_BORDER);
            printWriter.println("//" + METADATA);
            printWriter.println(TITLE_BORDER);
            metaData.forEach(data -> printWriter.println(data));
            printWriter.println("");
            
            for(Entry<String, List<PrintProfileSetting>> entry : settingsToWrite.entrySet()) {
                String settingsSection = entry.getKey();
                printWriter.println(TITLE_BORDER);
                printWriter.println("//" + settingsSection);
                printWriter.println(TITLE_BORDER);

                entry.getValue().forEach(setting -> printWriter.println(setting.getId() + "=" + setting.getValue()));

                printWriter.println("");
            }
            
            printWriter.close();
        } catch (IOException ex) {
            STENO.error(ex.getMessage());
        }
    }
    
}
