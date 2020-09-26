package celtech.roboxbase.configuration.profilesettings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author George
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrintProfileSetting 
{
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("settingName")
    private String settingName;
    
    @JsonProperty("value")
    private String value;
    
    @JsonProperty("valueType")
    private String valueType;
    
    @JsonProperty("tooltip")
    private String tooltip;
    
    @JsonProperty("unit")
    private Optional<String> unit = Optional.empty();
    
    @JsonProperty("perExtruder")
    private boolean perExtruder;
    
    @JsonProperty("minimumValue")
    private Optional<String> minimumValue = Optional.empty();
    
    @JsonProperty("maximumValue")
    private Optional<String> maximumValue = Optional.empty();
    
    @JsonProperty("nonOverrideAllowed")
    private Optional<Boolean> nonOverrideAllowed = Optional.empty();
    
    @JsonProperty("options")
    private Optional<Map<String, String>> options = Optional.empty();
    
    @JsonProperty("children")
    private Optional<List<PrintProfileSetting>> children = Optional.empty();
    
    /**
     * Default constructor for Jackson
     */
    public PrintProfileSetting() {}
    
    /**
     * Copy constructor
     * 
     * @param settingToCopy 
     */
    public PrintProfileSetting(PrintProfileSetting settingToCopy) 
    {
        id = settingToCopy.getId();
        settingName = settingToCopy.getSettingName();
        value = settingToCopy.getValue();
        valueType = settingToCopy.getValueType();
        tooltip = settingToCopy.getTooltip();
        unit = settingToCopy.getUnit();
        perExtruder = settingToCopy.isPerExtruder();
        
        if(settingToCopy.getUnit().isPresent()) 
        {
            unit = Optional.of(settingToCopy.getUnit().get());
        }
        
        if(settingToCopy.getMaximumValue().isPresent())
        {
            minimumValue = Optional.of(settingToCopy.getMinimumValue().get());
        }
        
        if(settingToCopy.getMaximumValue().isPresent())
        {
            maximumValue = Optional.of(settingToCopy.getMaximumValue().get());
        }
        
        if(settingToCopy.getNonOverrideAllowed().isPresent()) 
        {
            nonOverrideAllowed = Optional.of(settingToCopy.getNonOverrideAllowed().get());
        }
        
        if(settingToCopy.getOptions().isPresent()) 
        {
            options = Optional.of(settingToCopy.getOptions().get());
        }
        
        if(settingToCopy.getChildren().isPresent()) 
        {
            List<PrintProfileSetting> copiedChildren = settingToCopy.getChildren().get().stream()
                    .map(profile -> new PrintProfileSetting(profile))
                    .collect(Collectors.toList());
            children = Optional.of(copiedChildren);
        }
    }
    
    @JsonIgnore
    public Stream<PrintProfileSetting> flattened()
    {
        if(children.isPresent())
        {
            return Stream.concat(Stream.of(this),
                children.get().stream().flatMap(PrintProfileSetting::flattened));
        } else
        {
            return Stream.of(this);
        }
    }
    
    public String getId()
    {
        return id;
    }

    public void setId(String id) 
    {
        this.id = id;
    }

    public String getSettingName() 
    {
        return settingName;
    }

    public void setSettingName(String settingName) 
    {
        this.settingName = settingName;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String defaultValue) 
    {
        this.value = defaultValue;
    }

    public String getValueType() 
    {
        return valueType;
    }

    public void setValueType(String valueType) 
    {
        this.valueType = valueType;
    }
    
    public String getTooltip()
    {
        return tooltip;
    }

    public void setTooltip(String tooltip) 
    {
        this.tooltip = tooltip;
    }

    public Optional<String> getUnit() 
    {
        return unit;
    }

    public void setUnit(Optional<String> unit) 
    {
        this.unit = unit;
    }

    public boolean isPerExtruder() 
    {
        return perExtruder;
    }

    public void setPerExtruder(boolean perExtruder) 
    {
        this.perExtruder = perExtruder;
    }

    public Optional<String> getMinimumValue() 
    {
        return minimumValue;
    }

    public void setMinimumValue(Optional<String> minimumValue) 
    {
        this.minimumValue = minimumValue;
    }

    public Optional<String> getMaximumValue() 
    {
        return maximumValue;
    }

    public void setMaximumValue(Optional<String> maximumValue)
    {
        this.maximumValue = maximumValue;
    }
    
    public Optional<Boolean> getNonOverrideAllowed() 
    {
        return nonOverrideAllowed;
    }
    
    public void setNonOverrideAllowed(Optional<Boolean> nonOverrideAllowed)
    {
        this.nonOverrideAllowed = nonOverrideAllowed;
    }
    
    public Optional<Map<String, String>> getOptions() 
    {
        return options;
    }

    public void setOptions(Optional<Map<String, String>> options) 
    {
        this.options = options;
    }

    public Optional<List<PrintProfileSetting>> getChildren() 
    {
        return children;
    }

    public void setChildren(Optional<List<PrintProfileSetting>> children) 
    {
        this.children = children;
    }
}
