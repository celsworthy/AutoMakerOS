package celtech.automaker;

import celtech.roboxbase.comms.interapp.InterAppRequest;
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
public class AutoMakerInterAppRequestTest
{
    static
    {
        // Set the libertySystems config file property to inidicate it is a test request.
        // The property is set in this static initializer because the configuration is loaded before the test is run.
        System.setProperty("libertySystems.configFile", "$test$");
    }
    
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String jsonifiedClass = "{\"@class\":\"celtech.automaker.AutoMakerInterAppRequest\",\"command\":\"LOAD_MESH_INTO_LAYOUT_VIEW\",\"urlEncodedParameters\":[{\"type\":\"PROJECT_NAME\",\"urlEncodedParameter\":\"A project\"},{\"type\":\"MODEL_NAME\",\"urlEncodedParameter\":\"A model with spaces\"},{\"type\":\"MODEL_NAME\",\"urlEncodedParameter\":\"Another model with spaces\"}]}";

    public AutoMakerInterAppRequestTest()
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
        final AutoMakerInterAppRequest packet = getTestPacket();

        String mappedValue = mapper.writeValueAsString(packet);
        assertEquals(jsonifiedClass, mappedValue);
    }

    @Test
    public void deserializesFromJSON() throws Exception
    {
        final AutoMakerInterAppRequest packet = getTestPacket();

        try
        {
            InterAppRequest packetRec = mapper.readValue(jsonifiedClass, InterAppRequest.class);
            assertEquals(packet, packetRec);
        } catch (Exception e)
        {
            System.out.println(e.getCause().getMessage());
            fail();
        }
    }

    private AutoMakerInterAppRequest getTestPacket()
    {
        AutoMakerInterAppRequest packet = new AutoMakerInterAppRequest();

        packet.setCommand(AutoMakerInterAppRequestCommands.LOAD_MESH_INTO_LAYOUT_VIEW);
        packet.addSeparatedURLEncodedParameter(InterAppParameterType.PROJECT_NAME, "A project");
        packet.addSeparatedURLEncodedParameter(InterAppParameterType.MODEL_NAME, "A model with spaces");
        packet.addSeparatedURLEncodedParameter(InterAppParameterType.MODEL_NAME, "Another model with spaces");

        return packet;
    }

    @Test
    public void paramsInOut()
    {
        AutoMakerInterAppRequest packet = new AutoMakerInterAppRequest();
        packet.addSeparatedURLEncodedParameter(InterAppParameterType.MODEL_NAME, "fred&jim");
        assertEquals(2, packet.getUnencodedParameters().size());
        assertEquals(InterAppParameterType.MODEL_NAME, packet.getUnencodedParameters().get(0).getType());
        assertEquals("fred", packet.getUnencodedParameters().get(0).getUnencodedParameter());
        assertEquals(InterAppParameterType.MODEL_NAME, packet.getUnencodedParameters().get(1).getType());
        assertEquals("jim", packet.getUnencodedParameters().get(1).getUnencodedParameter());
    }
}
