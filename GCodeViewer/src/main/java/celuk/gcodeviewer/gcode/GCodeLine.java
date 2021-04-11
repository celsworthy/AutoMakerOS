
package celuk.gcodeviewer.gcode;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author micro
 */
public class GCodeLine {
    public static int NULL_NUMBER = -9999;
    public char commandLetter = '!';
    public int commandNumber = -1;
    public int lineNumber = NULL_NUMBER;
    public int layerNumber = NULL_NUMBER;
    public double layerHeight = -Double.MAX_VALUE;
    public String type = "";
    public String comment = "";
    public Map<Character, Double> valueMap = new HashMap<>();
    
    public void reset()
    {
        commandLetter = '!';
        commandNumber = -1;
        lineNumber = NULL_NUMBER;
        layerNumber = NULL_NUMBER;
        layerHeight = -Double.MAX_VALUE;
        comment = "";
        valueMap.clear();
    }
    
    public void setValue(char c, double v)
    {
        valueMap.put(c, v);
    }

    public boolean isValueSet(char c)
    {
        return valueMap.containsKey(c);
    }

    public double getValue(char c, double defaultValue)
    {
        return valueMap.getOrDefault(c, defaultValue);
    }
    
    public boolean hasNoValues()
    {
        return valueMap.isEmpty();
    }
    
    public Map<Character, Double> getValueMap()
    {
        return valueMap;
    }
}
