package celtech;

import celtech.appManager.TestSystemNotificationManager;
import celtech.configuration.ApplicationConfiguration;
import celtech.postprocessor.TestGCodeOutputWriter;
import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.configuration.datafileaccessors.PrintProfileSettingsContainer;
import celtech.roboxbase.configuration.datafileaccessors.RoboxProfileSettingsContainer;
import celtech.utils.tasks.TestTaskExecutor;
import java.io.File;
import java.net.URL;
import java.util.Properties;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

/**
 *
 * @author George Salter
 */
public class ConfiguredTest 
{
    static
    {
        // Set the libertySystems config file property..
        // The property is set in this static initializer because the configuration is loaded before the test is run.
        URL applicationURL = ConfiguredTest.class.getResource("/");
        String configDir = applicationURL.getPath();
        String configFile = configDir + "AutoMaker.configFile.xml";
        System.setProperty("libertySystems.configFile", configFile);
    }
    
    @Rule
    public TemporaryFolder temporaryUserStorageFolder = new TemporaryFolder();
    public String userStorageFolderPath;

    @Before
    public void setUp()
    {
        Properties testProperties = new Properties();

        testProperties.setProperty("language", "UK");
        URL applicationInstallURL = ConfiguredTest.class.getResource("/InstallDir/AutoMaker/");
        userStorageFolderPath = temporaryUserStorageFolder.getRoot().getAbsolutePath()
            + File.separator;
        BaseConfiguration.setInstallationProperties(
            testProperties,
            applicationInstallURL.getFile(),
            userStorageFolderPath);
        
        File filamentDir = new File(userStorageFolderPath
            + BaseConfiguration.filamentDirectoryPath
            + File.separator);
        filamentDir.mkdirs();
        
        new File(userStorageFolderPath
            + BaseConfiguration.printSpoolStorageDirectoryPath
            + File.separator).mkdirs();

        new File(userStorageFolderPath
            + ApplicationConfiguration.projectFileDirectoryPath
                + File.separator).mkdirs();

        URL configURL = ConfiguredTest.class.getResource("/AutoMaker.configFile.xml");
        System.setProperty("libertySystems.configFile", configURL.getFile());
        
        Lookup.setupDefaultValues();

        // force initialisation
        String installDir = BaseConfiguration.getApplicationInstallDirectory(
            Lookup.class);

        BaseLookup.setTaskExecutor(new TestTaskExecutor());
        BaseLookup.setSystemNotificationHandler(new TestSystemNotificationManager());

        BaseLookup.setPostProcessorOutputWriterFactory(TestGCodeOutputWriter::new);
        
        RoboxProfileSettingsContainer.getInstance();
        PrintProfileSettingsContainer.getInstance();
    }
}
