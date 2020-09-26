package celtech.roboxbase.utils.cura;

import com.fasterxml.jackson.databind.JsonNode;

/**
 *
 * 
 * @author George Salter
 */
public class JsonNodeWrapper {
    
    private final JsonNode jsonNode;
    
    private final String settingId;
    
    private JsonNodeWrapper parent = null;
    
    public JsonNodeWrapper(JsonNode jsonNode, String settingId, JsonNodeWrapper parent) {
        this.jsonNode = jsonNode;
        this.settingId = settingId;
        this.parent = parent;
    }

    public JsonNode getJsonNode() {
        return jsonNode;
    }
    
    public JsonNodeWrapper getParent() {
        return parent;
    }
    
    public String getSettingId() {
        return settingId;
    }
    
    public boolean hasParent() {
        return parent != null;
    }
}
