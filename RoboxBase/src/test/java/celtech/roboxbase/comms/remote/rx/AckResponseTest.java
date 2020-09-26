package celtech.roboxbase.comms.remote.rx;

import celtech.roboxbase.comms.rx.FirmwareError;
import celtech.roboxbase.comms.rx.RoboxRxPacket;
import celtech.roboxbase.comms.rx.AckResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ianhudson
 */
public class AckResponseTest
{

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String jsonifiedClass = "{\"@class\":\"celtech.roboxbase.comms.rx.AckResponse\",\"packetType\":\"ACK_WITH_ERRORS\",\"messagePayload\":null,\"sequenceNumber\":44,\"includeSequenceNumber\":false,\"includeCharsOfDataInOutput\":false,\"firmwareErrors\":[\"USB_RX\"]}";

    public AckResponseTest()
    {
    }

    @BeforeClass
    public static void setUpClass()
    {
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
        final AckResponse packet = getTestPacket();

        String mappedValue = mapper.writeValueAsString(packet);
        assertEquals(jsonifiedClass, mappedValue);
    }

    @Test
    public void deserializesFromJSON() throws Exception
    {
        final AckResponse packet = getTestPacket();

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

    private AckResponse getTestPacket()
    {
        AckResponse packet = new AckResponse();

        packet.setSequenceNumber(44);
        List<FirmwareError> firmwareErrors = new ArrayList<>();
        firmwareErrors.add(FirmwareError.USB_RX);
        packet.setFirmwareErrors(firmwareErrors);

        return packet;
    }
}
