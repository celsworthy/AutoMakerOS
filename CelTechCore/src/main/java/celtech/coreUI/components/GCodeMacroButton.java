package celtech.coreUI.components;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;

/**
 *
 * @author Ian
 */
public class GCodeMacroButton extends Button
{
    private StringProperty macroName = new SimpleStringProperty("");
    
    /**
     *
     * @param value
     */
    public void setMacroName(String value)
    {
        macroName.set(value);
    }
    
    /**
     *
     * @return
     */
    public String getMacroName()
    {
        return macroName.get();
    }
    
    /**
     *
     * @return
     */
    public StringProperty macroNameProperty()
    {
        return macroName;
    }
}
