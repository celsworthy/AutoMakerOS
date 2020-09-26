package celtech.roboxbase.comms.remote.rx;

import celtech.roboxbase.comms.rx.PrinterIDResponse;
import celtech.roboxbase.comms.rx.RoboxRxPacket;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.paint.Color;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author ianhudson
 */
public class PrinterIDResponseTest
{

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String jsonifiedClass = "{\"@class\":\"celtech.roboxbase.comms.rx.PrinterIDResponse\",\"packetType\":\"PRINTER_ID_RESPONSE\",\"messagePayload\":null,\"sequenceNumber\":44,\"includeSequenceNumber\":false,\"includeCharsOfDataInOutput\":false,\"model\":\"RBX01\",\"edition\":\"KS\",\"weekOfManufacture\":null,\"yearOfManufacture\":null,\"poNumber\":null,\"serialNumber\":null,\"checkByte\":null,\"electronicsVersion\":null,\"printerFriendlyName\":null,\"printerColour\":\"0xf0f8ffff\"}";
    private static String testColourString = Color.ALICEBLUE.toString();

    public PrinterIDResponseTest()
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
        final PrinterIDResponse packet = getTestPacket();

        String mappedValue = mapper.writeValueAsString(packet);
        assertEquals(jsonifiedClass, mappedValue);
    }

    @Test
    public void deserializesFromJSON() throws Exception
    {
        final PrinterIDResponse packet = getTestPacket();

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

    private PrinterIDResponse getTestPacket()
    {
        PrinterIDResponse packet = new PrinterIDResponse();

        packet.setSequenceNumber(44);
        packet.setEdition("KS");
        packet.setModel("RBX01");
        packet.setPrinterColour(testColourString);

        return packet;
    }

}
