package celtech.roboxbase.configuration.profilesettings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author George Salter
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrintProfileSettingsTab
{
    @JsonProperty("tabName")
    private String tabName;
    
    @JsonProperty("fxmlIconName")
    private String fxmlIconName;
    
    @JsonProperty("fxmlSelectedIconName")
    private Optional<String> fxmlSelectedIconName = Optional.empty();
    
    @JsonProperty("settings")
    private List<PrintProfileSetting> settings;
    
    /**
     * Default constructor for Jackson
     */
    public PrintProfileSettingsTab() {}

    /**
     * Copy constructor
     * 
     * @param tabToCopy 
     */
    public PrintProfileSettingsTab(PrintProfileSettingsTab tabToCopy)
    {
        tabName = tabToCopy.getTabName();
        fxmlIconName = tabToCopy.getFxmlIconName();
        
        if (tabToCopy.getFxmlSelectedIconName().isPresent())
        {
            fxmlSelectedIconName = Optional.of(tabToCopy.getFxmlSelectedIconName().get());
        }
        
        settings = new ArrayList<>();
        tabToCopy.getSettings().forEach(setting -> settings.add(new PrintProfileSetting(setting)));
    }
    
    /**
     * This method returns all the settings including any children
     * 
     * @return 
     */
    public List<PrintProfileSetting> getAllSettings()
    {
        List<PrintProfileSetting> allPrintProfileSettings = new ArrayList<>();
        settings.forEach(setting -> setting.flattened().forEach(flattenedSetting -> allPrintProfileSettings.add(flattenedSetting)));
        return allPrintProfileSettings;
    }
    
    public String getTabName()
    {
        return tabName;
    }

    public void setTabName(String tabName)
    {
        this.tabName = tabName;
    }

    public String getFxmlIconName() 
    {
        return fxmlIconName;
    }

    public void setFxmlIconName(String fxmlIconName) 
    {
        this.fxmlIconName = fxmlIconName;
    }

    public Optional<String> getFxmlSelectedIconName()
    {
        return fxmlSelectedIconName;
    }

    public void setFxmlSelectedIconName(Optional<String> fxmlSelectedIconName) 
    {
        this.fxmlSelectedIconName = fxmlSelectedIconName;
    }

    public List<PrintProfileSetting> getSettings()
    {
        return settings;
    }

    public void setSettings(List<PrintProfileSetting> settings)
    {
        this.settings = settings;
    }
}
