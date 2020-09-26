package celtech.roboxbase.comms.remote.rx;

import celtech.roboxbase.comms.remote.clear.WifiStatusResponse;
import com.fasterxml.jackson.databind.MapperFeature;
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
public class WifiStatusResponseTest
{

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String jsonifiedClass = "{\"associated\":false,\"poweredOn\":true,\"ssid\":\"\"}";

    public WifiStatusResponseTest()
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
        WifiStatusResponse packet = new WifiStatusResponse();

        packet.setPoweredOn(true);
        packet.setAssociated(false);
        packet.setSsid("");

        return packet;
    }
}
