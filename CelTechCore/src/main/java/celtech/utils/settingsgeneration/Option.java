package celtech.utils.settingsgeneration;

/**
 *
 * @author George Salter
 */
public class Option {
    
    private String optionId;
    private String optionValue;

    public Option(String optionName, String optionValue) {
        this.optionId = optionName;
        this.optionValue = optionValue;
    }
    
    public String getOptionId() {
        return optionId;
    }

    public void setOptionID(String optionId) {
        this.optionId = optionId;
    }

    public String getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(String optionValue) {
        this.optionValue = optionValue;
    }

    @Override
    public String toString() {
        return optionValue;
    }
    
}
