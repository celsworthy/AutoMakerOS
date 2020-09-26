package celtech.roboxbase.configuration.profilesettings;

import celtech.roboxbase.BaseLookup;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;

/**
 *
 * @author George Salter
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrintProfileSettings
{
    /**
     * The settings generated along side head type and quality selections
     */
    @JsonProperty("headerSettings")
    private List<PrintProfileSetting> headerSettings;
    
    /**
     * The tabs to be generated each containing their own settings
     */
    @JsonProperty("tabs")
    private List<PrintProfileSettingsTab> tabs;
    
    /**
     * Any hidden settings that need to be set later, usually just before printing
     */
    @JsonProperty("hiddenSettings")
    private List<PrintProfileSetting> hiddenSettings;

    /**
     * Default constructor for Jackson
     */
    public PrintProfileSettings() {}
    
    /**
     * Copy constructor
     * 
     * @param settingsToCopy 
     */
    public PrintProfileSettings(PrintProfileSettings settingsToCopy)
    {
        headerSettings = new ArrayList<>();
        tabs = new ArrayList<>();
        hiddenSettings = new ArrayList<>();
        
        settingsToCopy.getHeaderSettings().forEach(setting -> headerSettings.add(new PrintProfileSetting(setting)));
        settingsToCopy.getTabs().forEach(tab -> tabs.add(new PrintProfileSettingsTab(tab)));
        settingsToCopy.getHiddenSettings().forEach(setting -> hiddenSettings.add(new PrintProfileSetting(setting)));
    }
    
    /**
     * Return a list of all the {@link PrintProfileSetting}s
     * 
     * @return all the settings
     */
    @JsonIgnore
    public List<PrintProfileSetting> getAllSettings() 
    {
        List<PrintProfileSetting> allSettings = new ArrayList<>();
        allSettings.addAll(headerSettings);
        allSettings.addAll(hiddenSettings);
        tabs.forEach(tab -> allSettings.addAll(tab.getAllSettings()));     
        return allSettings;
    }

    /**
     * Return a list of pairs of {@link PrintProfileSetting}s and their sections.
     * This is used when saving profile settings and checking for overriden settings.
     * We don't need to return the hidden settings as these are not edited within
     * the print profiles.
     * 
     * @return 
     */
    @JsonIgnore
    public List<Pair<PrintProfileSetting, String>> getAllEditableSettingsWithSections()
    {
        List<Pair<PrintProfileSetting, String>> settingsAndSections = new ArrayList<>();
        
        headerSettings.forEach(setting ->
        {
            Pair settingSectionPair = new Pair(setting, BaseLookup.i18n("printProfileSettings.qualitySettings"));
            settingsAndSections.add(settingSectionPair);
        });
        
        tabs.forEach(tab -> 
        {
            tab.getAllSettings().forEach(setting -> 
            {
                Pair settingSectionPair = new Pair(setting, tab.getTabName());
                settingsAndSections.add(settingSectionPair);
            });
        });
        
        return settingsAndSections;
    }
    
    public List<PrintProfileSetting> getHeaderSettings()
    {
        return headerSettings;
    }

    public void setHeaderSettings(List<PrintProfileSetting> headerSettings) 
    {
        this.headerSettings = headerSettings;
    }

    public List<PrintProfileSettingsTab> getTabs() 
    {
        return tabs;
    }

    public void setTabs(List<PrintProfileSettingsTab> tabs)
    {
        this.tabs = tabs;
    }

    public List<PrintProfileSetting> getHiddenSettings() 
    {
        return hiddenSettings;
    }

    public void setHiddenSettings(List<PrintProfileSetting> hiddenSettings)
    {
        this.hiddenSettings = hiddenSettings;
    }
}
