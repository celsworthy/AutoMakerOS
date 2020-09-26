package celtech.roboxbase.configuration;

import celtech.roboxbase.comms.DetectedServer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian
 */
public class CoreMemory
{
    private final Stenographer steno = StenographerFactory.getStenographer(CoreMemory.class.getName());
    private static final String ACTIVE_ROBOX_ROOT_KEY = "ActiveRoots";
    private static final String LAST_PRINTER_SERIAL_KEY = "LastPrinterSerial";
    private static final String LAST_PRINTER_FIRMWARE_VERSION_KEY = "LastPrinterFirmwareVersion";

    private List<DetectedServer> cachedActiveRoboxRoots = null;

    private static CoreMemory instance = null;

    private final ObjectMapper mapper = new ObjectMapper();

    private CoreMemory()
    {
        SimpleModule module = new SimpleModule("DetectedServerDeserializer", new Version(1, 0, 0, null, null, null));
        module.addDeserializer(DetectedServer.class, new DetectedServer.DetectedServerDeserializer());
        module.addSerializer(DetectedServer.class, new DetectedServer.DetectedServerSerializer());
        mapper.registerModule(module);
    }

    public static CoreMemory getInstance()
    {
        if (instance == null)
        {
            instance = new CoreMemory();
        }
        return instance;
    }

    public List<DetectedServer> getActiveRoboxRoots()
    {
        if (cachedActiveRoboxRoots == null)
        {
            String activeRootsJSON = BaseConfiguration.getApplicationMemory(ACTIVE_ROBOX_ROOT_KEY);
            if (activeRootsJSON != null)
            {
                try
                {
                    cachedActiveRoboxRoots = mapper.readValue(activeRootsJSON, new TypeReference<List<DetectedServer>>()
                    {
                    });
                } catch (IOException ex)
                {
                    steno.warning("Unable to map data for active robox roots");
                }
            }
            
            if (cachedActiveRoboxRoots == null)
            {
                cachedActiveRoboxRoots = new ArrayList<>();
            }
        }
        return cachedActiveRoboxRoots;
    }

    public void clearActiveRoboxRoots()
    {
        cachedActiveRoboxRoots.clear();
        BaseConfiguration.setApplicationMemory(ACTIVE_ROBOX_ROOT_KEY, "");
    }

    private void writeRoboxRootData()
    {
        try
        {
            BaseConfiguration.setApplicationMemory(ACTIVE_ROBOX_ROOT_KEY, mapper.writeValueAsString(cachedActiveRoboxRoots));
        } catch (JsonProcessingException ex)
        {
            steno.warning("Unable to write active root data:" + ex.getMessage());
        }
    }

    public void activateRoboxRoot(DetectedServer server)
    {
        if (!cachedActiveRoboxRoots.contains(server))
        {
            cachedActiveRoboxRoots.add(server);
            writeRoboxRootData();
        } else
        {
            //steno.warning("Root " + server.getName() + " is already active");
        }
    }

    public void deactivateRoboxRoot(DetectedServer server)
    {
        if (cachedActiveRoboxRoots.contains(server))
        {
            cachedActiveRoboxRoots.remove(server);
            writeRoboxRootData();
        }
    }
    
    public void updateRoboxRoot(DetectedServer server)
    {
        if (cachedActiveRoboxRoots.contains(server))
            writeRoboxRootData();
    }

    public float getLastPrinterFirmwareVersion()
    {
        float firmwareVersionFloat = 0;

        String firmwareString = BaseConfiguration.getApplicationMemory(LAST_PRINTER_FIRMWARE_VERSION_KEY);
        if (firmwareString != null)
        {
            try
            {
                firmwareVersionFloat = Float.valueOf(firmwareString);
            } catch (NumberFormatException ex)
            {
                steno.warning("Unable to read firmware version from application memory");
            }
        }

        return firmwareVersionFloat;
    }

    public void setLastPrinterFirmwareVersion(float firmwareVersionInUse)
    {
        BaseConfiguration.setApplicationMemory(LAST_PRINTER_FIRMWARE_VERSION_KEY, String.format(Locale.UK, "%f", firmwareVersionInUse));
    }

    public String getLastPrinterSerial()
    {
        return BaseConfiguration.getApplicationMemory(LAST_PRINTER_SERIAL_KEY);
    }

    public void setLastPrinterSerial(String printerIDToUse)
    {
        BaseConfiguration.setApplicationMemory(LAST_PRINTER_SERIAL_KEY, printerIDToUse);
    }
}
