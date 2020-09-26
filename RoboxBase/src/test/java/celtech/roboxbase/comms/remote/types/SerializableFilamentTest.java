package celtech.roboxbase.comms.remote.types;

import celtech.roboxbase.MaterialType;
import celtech.roboxbase.configuration.Filament;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.paint.Color;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Ian
 */
public class SerializableFilamentTest
{

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String jsonifiedClass = "{\"friendlyFilamentName\":\"A Filament\",\"material\":\"ABS\",\"reelID\":\"U123456\",\"brand\":\"\",\"category\":\"\",\"diameter\":0.4,\"filamentMultiplier\":0.0,\"feedRateMultiplier\":0.0,\"requiredAmbientTemperature\":0,\"requiredFirstLayerBedTemperature\":0,\"requiredBedTemperature\":0,\"requiredFirstLayerNozzleTemperature\":0,\"requiredNozzleTemperature\":0,\"webDisplayColour\":\"0x00ffffff\",\"costGBPPerKG\":0.0,\"defaultLength_m\":0,\"mutable\":true}";
           
    @Test
    public void serializesToJSON() throws Exception
    {
        final SerializableFilament packet = getTestFilament();

        String mappedValue = mapper.writeValueAsString(packet);
        assertEquals(jsonifiedClass, mappedValue);
    }

    @Test
    public void deserializesFromJSON() throws Exception
    {
        final SerializableFilament packet = getTestFilament();

        try
        {
            SerializableFilament packetRec = mapper.readValue(jsonifiedClass, SerializableFilament.class);
            assertEquals(packet, packetRec);
        } catch (Exception e)
        {
            System.out.println(e.getCause().getMessage());
            fail();
        }
    }

    private SerializableFilament getTestFilament()
    {
        SerializableFilament packet = new SerializableFilament("A Filament", MaterialType.ABS, "U123456", "", "", 0.4f, 0, 0, 0, 0, 0, 0, 0, Color.AQUA.toString(), 0, 0, false, true);

        return packet;
    }
}
