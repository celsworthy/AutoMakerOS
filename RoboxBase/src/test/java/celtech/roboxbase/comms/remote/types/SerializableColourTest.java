package celtech.roboxbase.comms.remote.types;

import celtech.roboxbase.comms.remote.types.SerializableColour;
import celtech.roboxbase.comms.remote.types.SerializableColour;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.paint.Color;
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
public class SerializableColourTest
{

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String jsonifiedClass = "{\"webColour\":\"0xf0f8ffff\"}";

    public SerializableColourTest()
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
        final SerializableColour packet = new SerializableColour();

        packet.setWebColour(Color.ALICEBLUE.toString());

        String mappedValue = mapper.writeValueAsString(packet);
        assertEquals(jsonifiedClass, mappedValue);
    }

    @Test
    public void deserializesFromJSON() throws Exception
    {
        final SerializableColour packet = new SerializableColour();
        packet.setWebColour(Color.ALICEBLUE.toString());
        SerializableColour packetRec = mapper.readValue(jsonifiedClass, SerializableColour.class);
        assertEquals(packet, packetRec);
    }
}
