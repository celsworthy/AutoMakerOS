package celtech.roboxbase.comms.remote.rx;

import celtech.roboxbase.comms.remote.EEPROMState;
import celtech.roboxbase.comms.rx.RoboxRxPacket;
import celtech.roboxbase.comms.rx.StatusResponse;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author ianhudson
 */
public class StatusResponseTest
{

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String jsonifiedClass = "{\"@class\":\"celtech.roboxbase.comms.rx.StatusResponse\",\"ambientFanOn\":false,\"ambientTargetTemperature\":65,\"ambientTemperature\":0,\"bedFirstLayerTargetTemperature\":0,\"bedHeaterMode\":\"OFF\",\"bedTargetTemperature\":0,\"bedTemperature\":0,\"bposition\":0.0,\"busyStatus\":\"NOT_BUSY\",\"dataIsValid\":false,\"dfilamentDiameter\":0.0,\"dfilamentMultiplier\":0.0,\"dindexStatus\":false,\"doorOpen\":false,\"dualReelAdaptorPresent\":false,\"efilamentDiameter\":0.0,\"efilamentMultiplier\":0.0,\"eindexStatus\":false,\"extruderDPresent\":false,\"extruderEPresent\":false,\"feedRateDMultiplier\":0.0,\"feedRateEMultiplier\":0.0,\"filament1SwitchStatus\":true,\"filament2SwitchStatus\":false,\"hardwareRev\":0,\"headEEPROMState\":\"NOT_PRESENT\",\"headFanOn\":false,\"headPowerOn\":false,\"headXPosition\":0.0,\"headYPosition\":0.0,\"headZPosition\":0.0,\"includeCharsOfDataInOutput\":false,\"includeSequenceNumber\":false,\"messagePayload\":null,\"nozzle0FirstLayerTargetTemperature\":0,\"nozzle0HeaterMode\":\"OFF\",\"nozzle0TargetTemperature\":0,\"nozzle0Temperature\":0,\"nozzle1FirstLayerTargetTemperature\":0,\"nozzle1HeaterMode\":\"OFF\",\"nozzle1TargetTemperature\":144,\"nozzle1Temperature\":0,\"nozzleInUse\":0,\"nozzleSwitchStatus\":false,\"packetType\":\"STATUS_RESPONSE\",\"pauseStatus\":\"NOT_PAUSED\",\"printJobLineNumber\":0,\"reel0EEPROMState\":\"PROGRAMMED\",\"reel1EEPROMState\":\"NOT_PRESENT\",\"reelButtonPressed\":false,\"runningPrintJobID\":null,\"sdCardPresent\":false,\"sequenceNumber\":44,\"topZSwitchStatus\":false,\"whyAreWeWaitingState\":\"NOT_WAITING\",\"xSwitchStatus\":false,\"ySwitchStatus\":false,\"zSwitchStatus\":false}";

    public StatusResponseTest()
    {
    }

    @BeforeClass
    public static void setUpClass()
    {
        mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
    }

    @AfterClass
    public static void tearDownClass()
    {
    }

    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }

    @Test
    public void serializesToJSON() throws Exception
    {
        final StatusResponse packet = getTestPacket();

        String mappedValue = mapper.writeValueAsString(packet);
        assertEquals(jsonifiedClass, mappedValue);
    }

    @Test
    public void deserializesFromJSON() throws Exception
    {
        final StatusResponse packet = getTestPacket();

        try
        {
            RoboxRxPacket packetRec = mapper.readValue(jsonifiedClass, RoboxRxPacket.class);
            assertEquals(packet, packetRec);
        } catch (Exception e)
        {
            System.out.println(e.getCause().getMessage());
            fail();
        }
    }

    private StatusResponse getTestPacket()
    {
        StatusResponse packet = new StatusResponse();

        packet.setSequenceNumber(44);
        packet.setAmbientTargetTemperature(65);
        packet.setNozzle1TargetTemperature(144);
        packet.setFilament1SwitchStatus(true);
        packet.setReel0EEPROMState(EEPROMState.PROGRAMMED);

        return packet;
    }
}
