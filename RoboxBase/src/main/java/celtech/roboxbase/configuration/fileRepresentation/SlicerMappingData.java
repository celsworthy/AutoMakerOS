package celtech.roboxbase.configuration.fileRepresentation;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Ian
 */
public class SlicerMappingData
{
    ArrayList<String> defaults;
    HashMap<String,String> mappingData;

    public ArrayList<String> getDefaults()
    {
        return defaults;
    }

    public void setDefaults(ArrayList<String> defaults)
    {
        this.defaults = defaults;
    }

    public HashMap<String, String> getMappingData()
    {
        return mappingData;
    }

    public void setMappingData(HashMap<String, String> mappingData)
    {
        this.mappingData = mappingData;
    }
}
