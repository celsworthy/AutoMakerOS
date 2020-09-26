import celtech.roboxremote.PublicPrinterControlAPI;
import io.dropwizard.jackson.Jackson;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.Test;

/**
 *
 * @author Ian
 */
public class HighLevelAPITest
{
//    @ClassRule
//    public static final ResourceTestRule resources = ResourceTestRule.builder()
//            .setMapper(Jackson.newObjectMapper())
//            .addResource(new PublicPrinterControlAPI())
//            .build();

    @Before
    public void setup()
    {
//        when(dao.fetchPerson(eq("blah"))).thenReturn(person);
    }

    @After
    public void tearDown()
    {
        // we have to reset the mock after each test because of the
        // @ClassRule, or use a @Rule as mentioned below.
//        reset(dao);
    }

    @Test
    public void testOpenDoor()
    {
////        WebTarget target = resources.client().target("/123456/remoteControl/openDoor");
//
////        QueryFirmwareVersion fw = (QueryFirmwareVersion)RoboxTxPacketFactory.createPacket(TxPacketTypeEnum.QUERY_FIRMWARE_VERSION);
////        fw.setSequenceNumber(33);
//        boolean boolVal = true;
//        Entity requestEntity = Entity.entity(boolVal, MediaType.APPLICATION_JSON_TYPE);
//        System.out.println(requestEntity.getEntity());
//        Invocation invocation = resources.client().target("/12345678/remoteControl/openDoor")
//                .request().buildPost(requestEntity);
//        try
//        {
//
//            Response response = invocation.invoke();
//        } catch (Exception e)
//        {
//            System.out.println("Arrg");
//        }
//        System.out.println("hello");
//        
//        
//        QueryFirmwareVersion fw = (QueryFirmwareVersion) RoboxTxPacketFactory.createPacket(TxPacketTypeEnum.QUERY_FIRMWARE_VERSION);
//        fw.setSequenceNumber(33);
//
//        Entity requestEntity = Entity.entity(fw, MediaType.APPLICATION_JSON_TYPE);
//        Invocation invocation = resources.client().target("/12345678/printerControl/writeData")
//                .request().buildPost(requestEntity);
//        Response response = invocation.invoke();
    }
}
