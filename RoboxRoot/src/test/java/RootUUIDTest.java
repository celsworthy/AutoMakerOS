
import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxremote.RootUUID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.net.URL;
import java.util.Properties;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author Ian
 */
public class RootUUIDTest
{
    static
    {
        // Set the libertySystems config file property..
        // The property is set in this static initializer because the configuration is loaded before the test is run.
        URL applicationURL = LowLevelAPITest.class.getResource("/");
        String configDir = applicationURL.getPath();
        String configFile = configDir + "Root.configFile.xml";
        System.setProperty("libertySystems.configFile", configFile);
    }

    @Before
    public void setup()
    {
        Properties testProperties = new Properties();
        testProperties.setProperty("language", "UK");
        URL applicationURL = LowLevelAPITest.class.getResource("/");
        String testDir = applicationURL.getFile();
        BaseConfiguration.setInstallationProperties(
            testProperties,
            testDir,
            testDir);
    }

    @After
    public void tearDown()
    {
    }

    @Test
    public void testRootUUID()
    {
        String rootUUID = RootUUID.get();
        assertEquals(rootUUID, "94ae029a-c577-52c3-890e-cfe499eaa90f");
    }
}
