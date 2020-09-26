package celuk.groot.controllers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.paint.Color;

public class MachineDetails
{
    public enum OPACITY {
        PC20,
        PC50,
        PC100
    }
    
    public static final MachineDetails DEFAULT_DETAILS;
    public static final Map<String, MachineDetails> MACHINE_DETAIL_MAP;
    static {
        DEFAULT_DETAILS = new MachineDetails("machine.robox",
                                            "/image/status-robox-text-white.png",
                                            "/image/status-robox-black-20pc.png",
                                            "/image/status-robox-black-50pc.png",
                                            "/image/status-robox-black.png",
                                            "/image/status-robox-white-20pc.png",
                                            "/image/status-robox-white-50pc.png",
                                            "/image/status-robox-white.png",
                                            "/image/machine-robox-black.png",
                                            "/image/machine-robox-white.png");
        
        MACHINE_DETAIL_MAP = new HashMap<>();
        MACHINE_DETAIL_MAP.put("RBX01", DEFAULT_DETAILS);
        MACHINE_DETAIL_MAP.put("RBX02", DEFAULT_DETAILS);
        MACHINE_DETAIL_MAP.put("RBX10",
                              new MachineDetails("machine.roboxPro",
                                                 "/image/status-roboxpro-text-inverse.png",
                                                 "/image/status-roboxpro-black-20pc.png",
                                                 "/image/status-roboxpro-black-50pc.png",
                                                 "/image/status-roboxpro-black.png",
                                                 "/image/status-roboxpro-white-20pc.png",
                                                 "/image/status-roboxpro-white-50pc.png",
                                                 "/image/status-roboxpro-white.png",
                                                 "/image/machine-roboxpro-black.png",
                                                 "/image/machine-roboxpro-white.png"));
    }
    
    public String model;
    public String textIcon;
    public String statusIconDark20;
    public String statusIconDark50;
    public String statusIconDark;
    public String statusIconLight20;
    public String statusIconLight50;
    public String statusIconLight;
    public String machineIconDark;
    public String machineIconLight;

    public MachineDetails()
    {
        // Jackson deserialization
    }

    public MachineDetails(String model,
                          String textIcon,
                          String statusIconDark20,
                          String statusIconDark50,
                          String statusIconDark,
                          String statusIconLight20,
                          String statusIconLight50,
                          String statusIconLight,
                          String machineIconDark,
                          String machineIconLight)
    {
        this.model = model;
        this.textIcon = textIcon;
        this.statusIconDark20 = statusIconDark20;
        this.statusIconDark50 = statusIconDark50;
        this.statusIconDark = statusIconDark;
        this.statusIconLight20 = statusIconLight20;
        this.statusIconLight50 = statusIconLight50;
        this.statusIconLight = statusIconLight;
        this.machineIconDark = machineIconDark;
        this.machineIconLight = machineIconLight;
    }

    @JsonProperty
    public String getModel()
    {
        return model;
    }

    @JsonProperty
    public void setModel(String model)
    {
        this.model = model;
    }

    @JsonProperty
    public String getTextIcon()
    {
        return textIcon;
    }

    @JsonProperty
    public void setTextIcon(String textIcon)
    {
        this.textIcon = textIcon;
    }

    @JsonProperty
    public String getStatusIconDark20()
    {
        return statusIconDark20;
    }

    @JsonProperty
    public void setStatusIconDark20(String statusIconDark20)
    {
        this.statusIconDark20 = statusIconDark20;
    }

    @JsonProperty
    public String getStatusIconDark50()
    {
        return statusIconDark50;
    }

    @JsonProperty
    public void setStatusIconDark50(String statusIconDark50)
    {
        this.statusIconDark50 = statusIconDark50;
    }

    @JsonProperty
    public String getStatusIconDark()
    {
        return statusIconDark;
    }

    @JsonProperty
    public void setStatusIconDark(String statusIconDark)
    {
        this.statusIconDark = statusIconDark;
    }

    @JsonProperty
    public String getStatusIconLight20()
    {
        return statusIconLight20;
    }

    @JsonProperty
    public void setStatusIconLight20(String statusIconLight20)
    {
        this.statusIconLight20 = statusIconLight20;
    }

    @JsonProperty
    public String getStatusIconLight50()
    {
        return statusIconLight50;
    }

    @JsonProperty
    public void setStatusIconLight50(String statusIconLight50)
    {
        this.statusIconLight50 = statusIconLight50;
    }

    @JsonProperty
    public String getStatusIconLight()
    {
        return statusIconLight;
    }

    @JsonProperty
    public void setStatusIconLight(String statusIconLight)
    {
        this.statusIconLight = statusIconLight;
    }

    @JsonIgnore
    public String getStatusIcon20(String webColour)
    {
        return getComplimentaryOption(webColour, statusIconDark20, statusIconLight20);
    }

    @JsonIgnore
    public String getStatusIcon50(String webColour)
    {
        return getComplimentaryOption(webColour, statusIconDark50, statusIconLight50);
    }

    @JsonIgnore
    public String getStatusIcon(String webColour, OPACITY iconOpacity)
    {
        String s = "";
        switch (iconOpacity) {
            case PC20:
                s = getComplimentaryOption(webColour, statusIconDark20, statusIconLight20);
                break;
                
            case PC50:
                s = getComplimentaryOption(webColour, statusIconDark50, statusIconLight50);
                break;
                
            case PC100:
                s = getComplimentaryOption(webColour, statusIconDark, statusIconLight);
                break;
        }
        return s;
    }

    @JsonProperty
    public String getMachineIconDark()
    {
        return machineIconDark;
    }

    @JsonProperty
    public void setMachineIconDark(String machineIconDark)
    {
        this.machineIconDark = machineIconDark;
    }

    @JsonProperty
    public String getMachineIconLight()
    {
        return machineIconLight;
    }

    @JsonProperty
    public void setMachineIconLight(String machineIconLight)
    {
        this.machineIconLight = machineIconLight;
    }
    
    @JsonIgnore
    public String getMachineIcon(String webColour)
    {
        return getComplimentaryOption(webColour, machineIconDark, machineIconLight);
    }

    public static <T> T getComplimentaryOption(String webColour, T darkOption, T lightOption) {
        Color wc = Color.web(webColour);
        // brightness  =  sqrt( .241 R2 + .691 G2 + .068 B2 )
        // Don't know where these numbers came from!

        double brightness = Math.sqrt(0.241 * wc.getRed() * wc.getRed() + 0.691 * wc.getGreen() * wc.getGreen() + 0.068 * wc.getBlue() * wc.getBlue());
        if (brightness > 0.5)
            return darkOption;
        else
            return lightOption;
    }
    
    public static MachineDetails getDetails(String typeCode) {
        return MACHINE_DETAIL_MAP.getOrDefault(typeCode,
                                              MachineDetails.DEFAULT_DETAILS);
    }
}
