package celtech.roboxbase.comms.remote.clear;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Ian
 */
public class WifiStatusResponseTest
{

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String jsonifiedClass = "{\"poweredOn\":true,\"associated\":true,\"ssid\":\"TestSSID\"}";

    public WifiStatusResponseTest()
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
        final WifiStatusResponse packet = getTestPacket();

        String mappedValue = mapper.writeValueAsString(packet);
        assertEquals(jsonifiedClass, mappedValue);
    }

    @Test
    public void deserializesFromJSON() throws Exception
    {
        final WifiStatusResponse packet = getTestPacket();

        try
        {
            WifiStatusResponse packetRec = mapper.readValue(jsonifiedClass, WifiStatusResponse.class);
            assertEquals(packet, packetRec);
        } catch (Exception e)
        {
            System.out.println(e.getCause().getMessage());
            fail();
        }
    }

    private WifiStatusResponse getTestPacket()
    {
        return new WifiStatusResponse(true, true, "TestSSID");
    }
}
