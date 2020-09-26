package celtech.roboxbase.comms.interapp;

import com.fasterxml.jackson.databind.ObjectMapper;
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
public class InterAppResponseTest
{

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String jsonifiedClass = "{\"@class\":\"celtech.roboxbase.comms.interapp.InterAppResponse\",\"responseStatus\":\"REJECTED_PRINTER_NOT_READY\"}";

    public InterAppResponseTest()
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
        final InterAppResponse packet = getTestPacket();

        String mappedValue = mapper.writeValueAsString(packet);
        assertEquals(jsonifiedClass, mappedValue);
    }

    @Test
    public void deserializesFromJSON() throws Exception
    {
        final InterAppResponse packet = getTestPacket();

        try
        {
            InterAppResponse packetRec = mapper.readValue(jsonifiedClass, InterAppResponse.class);
            assertEquals(packet, packetRec);
        } catch (Exception e)
        {
            System.out.println(e.getCause().getMessage());
            fail();
        }
    }

    private InterAppResponse getTestPacket()
    {
        InterAppResponse packet = new InterAppResponse();

        packet.setResponseStatus(InterAppResponseStatus.REJECTED_PRINTER_NOT_READY);

        return packet;
    }
}
