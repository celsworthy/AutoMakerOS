package celuk.groot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author Tony
 */
public class GRootPIN {
    @JsonIgnore
    private static final String CONFIG_FILE_NAME = "GRoot.json";
    
    @JsonIgnore
    private String pin = "";

    public GRootPIN() {
    }

    @JsonIgnore
    public static GRootPIN loadFromJSON(String configDirectory) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Path configPath = Paths.get(configDirectory).resolve(CONFIG_FILE_NAME);
            
        GRootPIN configuration = null;
        try {
            configuration = objectMapper.readValue(configPath.toFile(), GRootPIN.class);
        }
        catch (IOException ex) {
            configuration = new GRootPIN();
        }
        return configuration;
    }
    
    @JsonIgnore
    public void saveToJSON(String configDirectory) {
        ObjectMapper objectMapper = new ObjectMapper();
        String configPath = configDirectory + File.separator + CONFIG_FILE_NAME;
            
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(configPath), this);
        }
        catch (IOException ex) {
        }
    }

    @JsonProperty
    public String getPIN() {
        return pin;
    }

    @JsonProperty
    public void setPIN(String pin) {
        this.pin = pin;
    }
}
