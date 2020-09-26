package celtech.roboxbase;

import celtech.roboxbase.comms.tx.RoboxTxPacket;
import celtech.roboxbase.comms.tx.AbortPrint;
import celtech.roboxbase.comms.tx.PausePrint;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
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
public class RoboxTxPacketTest
{

    private final ObjectMapper mapper = new ObjectMapper();

    public RoboxTxPacketTest()
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

    /**
     * Test of getPacketType method, of class RoboxTxPacket.
     */
    @Test
    public void testJSONifyAbortPrint()
    {
        int sequenceNumber = 333;
        String message = "Some data";

        RoboxTxPacket packet = new AbortPrint();
        packet.setIncludeSequenceNumber(true);
        packet.setSequenceNumber(sequenceNumber);
        packet.setIncludeCharsOfDataInOutput(true);
        packet.setMessagePayload(message);

        try
        {
//            mapper.enableDefaultTyping();
            String jsonifiedString = mapper.writeValueAsString(packet);
            RoboxTxPacket dejson = mapper.readValue(jsonifiedString, RoboxTxPacket.class);
            assertEquals(packet, dejson);
        } catch (IOException ex)
        {
            fail("IO Exception whilst jsonifying");
        }
    }

    /**
     * Test of getPacketType method, of class RoboxTxPacket.
     */
    @Test
    public void testJSONifyPausePrint()
    {
        int sequenceNumber = 333;
        String message = "Some data";

        PausePrint packet = new PausePrint();
        packet.setIncludeSequenceNumber(true);
        packet.setSequenceNumber(sequenceNumber);
        packet.setIncludeCharsOfDataInOutput(true);
        packet.setMessagePayload(message);
        packet.setPause();

        try
        {
            String jsonifiedString = mapper.writeValueAsString(packet);
            RoboxTxPacket dejson = mapper.readValue(jsonifiedString, RoboxTxPacket.class);
            assertEquals(packet, dejson);
            assertTrue(dejson instanceof PausePrint);
            assertEquals("1", ((PausePrint) dejson).getMessagePayload());
        } catch (IOException ex)
        {
            fail("IO Exception whilst jsonifying");
        }
    }
}
