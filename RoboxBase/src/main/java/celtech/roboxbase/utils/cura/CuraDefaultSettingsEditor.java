package celtech.roboxbase.utils.cura;

import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.configuration.SlicerType;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 * A class to allow editing of the Cura 4 default settings file.
 * 
 * @author George Salter
 */
public class CuraDefaultSettingsEditor {
    
    private static final Stenographer STENO = StenographerFactory.getStenographer(CuraDefaultSettingsEditor.class.getName());
    
    private static final String JSON_SETTINGS_FILE = BaseConfiguration.getApplicationPrintProfileDirectoryForSlicer(SlicerType.Cura4) + "fdmprinter.def.json";
    private static final String JSON_EXTRUDER_SETTINGS_FILE = BaseConfiguration.getApplicationPrintProfileDirectoryForSlicer(SlicerType.Cura4) + "fdmextruder.def.json";
    private static final String EDITED_FILE_NAME = "fdmprinter_robox.def.json";
    private static final String SETTINGS = "settings";
    private static final String CHILDREN = "children";
    private static final String DEFAULT_VALUE = "default_value";
    private static final String TYPE = "type";
    private static final String METADATA = "metadata";
    private static final String EXTRUDER_TRAINS = "machine_extruder_trains";
    private static final String OVERRIDES = "overrides";
    private static final String EXTRUDER_NUM = "extruder_nr";
    private static final String POSITION = "position";
    
    private final ObjectMapper mapper = new ObjectMapper();
    
    private final Map<String, JsonNodeWrapper> settingsNodes = new HashMap<>();
    private final Map<String, Map<String, JsonNodeWrapper>> extruderSettingsNodesMap = new HashMap<>();

    private boolean singleNozzleHead = false;
    
    private JsonNode settingsRootNode = null;
    private Map<String, JsonNode> extruderRootNodes;
    
    public CuraDefaultSettingsEditor(boolean singleNozzleHead) {
        this.singleNozzleHead = singleNozzleHead;
    }    
    
    /**
     * Read the JSON file into nodes. 
     */
    public void beginEditing() {
        STENO.info("Reading cura settings file for editing.");
        try {
            settingsRootNode = mapper.readTree(new File(JSON_SETTINGS_FILE));
            Iterator<Entry<String, JsonNode>> sections = settingsRootNode.get(SETTINGS).fields();
            while(sections.hasNext()) {
                Entry<String, JsonNode> settingsNode = sections.next();
                addSettingsToMap(settingsNode, null, settingsNodes);
            }
            
            extruderRootNodes = new LinkedHashMap<>();
        } catch (IOException ex) {
            STENO.error("Failed to read json file: " + JSON_SETTINGS_FILE + " in " + this.getClass().getName());
            STENO.error(ex.getMessage());
        }
    }
    
    /**
     * Write the changes back to JSON.
     * @param destinationDirectory
     */
    public void endEditing(String destinationDirectory) {
        String filePath = destinationDirectory + EDITED_FILE_NAME;
        STENO.info("Writing changes made to " + filePath);
        try {
            JsonFactory factory = new JsonFactory();
                        
            writeExtruderFiles(factory, destinationDirectory);
            addExtruders();
            
            JsonGenerator generator = factory.createGenerator(new File(filePath), JsonEncoding.UTF8);
            mapper.writeTree(generator, settingsRootNode);
        } catch (IOException ex) {
            STENO.error("Failed to write json to file: " + filePath + " in " + this.getClass().getName());
            STENO.error(ex.getMessage());
        }
    }
    
    public void editDefaultValue(String settingId, String value) {
        ObjectNode settingNode = (ObjectNode) settingsNodes.get(settingId).getJsonNode();
        editDefaultValue(settingNode, value);
    }
    
    /**
     * 
     * @param settingObjectNode
     * @param value 
     */
    public void editDefaultValue(ObjectNode settingObjectNode, String value) {
        String settingType = settingObjectNode.get(TYPE).asText();
        
        switch (settingType) {
            case "int":
                settingObjectNode.remove(DEFAULT_VALUE);
                settingObjectNode.put(DEFAULT_VALUE, Integer.parseInt(value));
                break;
            case "float":
                settingObjectNode.remove(DEFAULT_VALUE);
                settingObjectNode.put(DEFAULT_VALUE, Float.parseFloat(value));
                break;
            case "bool":
                settingObjectNode.remove(DEFAULT_VALUE);
                settingObjectNode.put(DEFAULT_VALUE, Boolean.parseBoolean(value));
                break;
            case "str":
            case "enum":
            case "[int]":
                settingObjectNode.remove(DEFAULT_VALUE);
                settingObjectNode.put(DEFAULT_VALUE, value);
                break;                
            case "extruder":
            case "optional_extruder":
                // Heads with a single nozzle are anomalous because
                // tool zero uses the "E" extruder, which is usually
                // extruder number 1. So for these kinds of head, the
                // extruder number needs to be reset to 0.
                // This seems a very odd place to do this, but there
                // is no-where else that is obvious. This hack is closely related
                // to the hack in SlicerTask that also sets the extruder number to zero
                // for single nozzle heads.
                settingObjectNode.remove(DEFAULT_VALUE);
                if (singleNozzleHead)
                    settingObjectNode.put(DEFAULT_VALUE, "0");
                else    
                    settingObjectNode.put(DEFAULT_VALUE, value);
                break;
            default:
                STENO.warning("Unknown cura setting type: " + settingType +
                        ". Setting will not be processed!");
                break;
        }
    }
    
    /**
     * Edit the default value of a specified float setting.
     * 
     * @param settingId the id of the setting to be changed.
     * @param value the new default float value for the setting.
     */
    public void editDefaultFloatValue(String settingId, float value) {
        ObjectNode settingObjectNode = (ObjectNode) settingsNodes.get(settingId).getJsonNode();
        String type = settingObjectNode.get(TYPE).asText();
        if (type.equals("float")) {
            settingObjectNode.remove(DEFAULT_VALUE);
            settingObjectNode.put(DEFAULT_VALUE, value);
        } else {
            STENO.error("Setting value is of type: " + type + " is not compatible with float of " + value);
        }
    }
    
    /**
     * Edit the default value of a specified int setting.
     * 
     * @param settingId the id of the setting to be changed.
     * @param value the new default int value for the setting.
     */
    public void editDefaultIntValue(String settingId, int value) {
        ObjectNode settingObjectNode = (ObjectNode) settingsNodes.get(settingId).getJsonNode();
        String type = settingObjectNode.get(TYPE).asText();
        if (type.equals("int")) {
            settingObjectNode.remove(DEFAULT_VALUE);
            settingObjectNode.put(DEFAULT_VALUE, value);
        } else {
            STENO.error("Setting value is of type: " + type + " is not compatible with int of " + value);
        }
    }
    
    public void editExtruderValue(String settingId, String nozzleRef, String value) {
        if(!extruderRootNodes.containsKey(nozzleRef)) {
            STENO.debug("Nozzle - " + nozzleRef + " does not exist. Setting - " + settingId + " not mapped to extruder file");
            return;
        } 
        
        if(extruderSettingsNodesMap.get(nozzleRef).containsKey(settingId)) {
            ObjectNode settingNode = (ObjectNode) extruderSettingsNodesMap.get(nozzleRef).get(settingId).getJsonNode();
            editDefaultValue(settingNode, value);
        } else {
            try {
                ObjectNode overrides = (ObjectNode) extruderRootNodes.get(nozzleRef).get(OVERRIDES);
                String type = settingsNodes.get(settingId).getJsonNode().get(TYPE).textValue();
                String jsonTypeString = String.format("{\"%s\": \"%s\"}", TYPE, type);
                JsonNode newNode;

                newNode = mapper.readTree(jsonTypeString);
                overrides.set(settingId, newNode);
                editDefaultValue((ObjectNode) overrides.get(settingId), value);
            } catch (IOException ex) {
                STENO.error("JSON string format incorrect");
                STENO.error(ex.getMessage());
            }
        }
    }
    
    /**
     * Take the default extruder JSON file and read it into a new root node.
     * Expand all the nodes into a new map and add it to the extruder settings nodes map.
     * 
     * @param nozzleRef the nozzle reference for this extruder file.
     */
    public void beginNewExtruderFile(String nozzleRef) {
        try {
            JsonNode extruderRootNode = mapper.readTree(new File(JSON_EXTRUDER_SETTINGS_FILE));
            ObjectNode extruderRootObjectNode = (ObjectNode) extruderRootNode;
            extruderRootObjectNode.put("id", "extruder_robox_" + nozzleRef);
            extruderRootNodes.put(nozzleRef, extruderRootNode);
            
            Map<String, JsonNodeWrapper> extruderSettingsNodes = new HashMap<>();
            
            Iterator<Entry<String, JsonNode>> sections = extruderRootNode.get(SETTINGS).fields();
            while(sections.hasNext()) {
                Entry<String, JsonNode> settingsNode = sections.next();
                addSettingsToMap(settingsNode, null, extruderSettingsNodes);
            }
            
            setupExtruderDefaults(extruderRootNode, extruderSettingsNodes, extruderSettingsNodesMap.size());
            extruderSettingsNodesMap.put(nozzleRef, extruderSettingsNodes);
        } catch (IOException ex) {
            STENO.error("Failed to read json file: " + JSON_EXTRUDER_SETTINGS_FILE + " in " + this.getClass().getName());
            STENO.error(ex.getMessage());
        }
    }
    
    private void setupExtruderDefaults(JsonNode extruderRootNode, Map<String, JsonNodeWrapper> extruderSettingsNodes, int extruderNumber) {
        
        // Set the extruder number
        ObjectNode metadataNode = (ObjectNode) extruderRootNode.get(METADATA);
        metadataNode.remove(POSITION);
        metadataNode.put(POSITION, String.valueOf(extruderNumber));
        
        JsonNodeWrapper extruderNumberNodeWrapper = extruderSettingsNodes.get(EXTRUDER_NUM);
        ObjectNode extruderNumberNode = (ObjectNode) extruderNumberNodeWrapper.getJsonNode();
        editDefaultValue(extruderNumberNode, String.valueOf(extruderNumber));
    }
    
    /**
     * Write changes to all extruder files to new JSON files.
     * 
     * @param factory JsonFactory to create the JsonGenerator.
     */
    private void writeExtruderFiles(JsonFactory factory, String destinationDirectory) {
        extruderRootNodes.entrySet().forEach(nodeEntry -> {
            String fileName = destinationDirectory + "/extruder_robox_" + nodeEntry.getKey() + ".def.json";
            try {
                JsonGenerator generator;
                generator = factory.createGenerator(new File(fileName), JsonEncoding.UTF8);
                mapper.writeTree(generator, nodeEntry.getValue());
            } catch (IOException ex) {
                STENO.error("Failed to write json to file: " + fileName + " in " + this.getClass().getName());
                STENO.error(ex.getMessage());
            }
        });
    }
    
    /**
     * 
     * @param settingsNode
     * @param parent 
     */
    private void addSettingsToMap(Entry<String, JsonNode> settingsNode, JsonNodeWrapper parent, Map<String, JsonNodeWrapper> settingsMap) {
        JsonNodeWrapper nodeToAdd = new JsonNodeWrapper(settingsNode.getValue(), settingsNode.getKey(), parent);
        
        if(settingsNode.getValue().has(CHILDREN)) {
            Iterator<Entry<String, JsonNode>> children = settingsNode.getValue().get(CHILDREN).fields();
            while(children.hasNext()) {
                Entry<String, JsonNode> childNode = children.next();
                addSettingsToMap(childNode, nodeToAdd, settingsMap);
            }
        }
        
        settingsMap.put(settingsNode.getKey(), nodeToAdd);
    }
    
    /**
     * Add the extruder file references to the main settings.
     */
    private void addExtruders() {
        ObjectNode extruders = (ObjectNode) settingsRootNode.get(METADATA).get(EXTRUDER_TRAINS);
        
        int nozzleCount = 0;
        for(String nozzleRef : extruderRootNodes.keySet()) {
            extruders.put(String.valueOf(nozzleCount), "extruder_robox_" + nozzleRef);
            nozzleCount++;
        }
    }
}
