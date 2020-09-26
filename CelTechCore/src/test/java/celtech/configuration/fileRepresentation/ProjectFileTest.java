package celtech.configuration.fileRepresentation;

import celtech.ConfiguredTest;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Ian
 */
public class ProjectFileTest extends ConfiguredTest
{

    private static final String jsonifiedClass = "{\"projectType\":\"MODEL\",\"version\":5,\"projectName\":null,\"lastModifiedDate\":null,\"lastPrintJobID\":\"\",\"projectNameModified\":false,\"timelapseTriggerEnabled\":false,\"timelapseProfileName\":\"\",\"timelapseCameraID\":\"\",\"subVersion\":1,\"brimOverride\":0,\"fillDensityOverride\":0.0,\"fillDensityOverridenByUser\":false,\"printSupportOverride\":false,\"printSupportTypeOverride\":\"MATERIAL_2\",\"printRaft\":false,\"spiralPrint\":false,\"extruder0FilamentID\":null,\"extruder1FilamentID\":null,\"settingsName\":\"Draft\",\"printQuality\":\"NORMAL\",\"groupStructure\":{},\"groupState\":{}}";
    private static final String jsonifiedClass_2_03_01 = "{\"version\":3,\"projectName\":null,\"lastModifiedDate\":null,\"lastPrintJobID\":\"\",\"subVersion\":3,\"brimOverride\":0,\"fillDensityOverride\":0.0,\"printSupportOverride\":false,\"printSupportTypeOverride\":\"MATERIAL_2\",\"printRaft\":false,\"spiralPrint\":false,\"extruder0FilamentID\":null,\"extruder1FilamentID\":null,\"settingsName\":\"Draft\",\"printQuality\":\"DRAFT\",\"groupStructure\":{},\"groupState\":{}}";

    public ProjectFileTest()
    {
    }

    @Test
    public void serializesToJSON() throws Exception
    {
        final ModelContainerProjectFile projectFile = createTestProjectFile();

        ObjectMapper mapper = new ObjectMapper();
        String mappedValue = mapper.writeValueAsString(projectFile);
        assertEquals(jsonifiedClass, mappedValue);
    }

    @Test
    public void deserializesFromJSON() throws Exception
    {
        try
        {
            ProjectFileDeserialiser deserializer
                    = new ProjectFileDeserialiser();
            SimpleModule module
                    = new SimpleModule("LegacyProjectFileDeserialiserModule",
                            new Version(1, 0, 0, null));
            module.addDeserializer(ProjectFile.class, deserializer);

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(module);

            ProjectFile projectFileReceived = mapper.readValue(jsonifiedClass, ProjectFile.class);
            assertNotNull(projectFileReceived);
            assertTrue(projectFileReceived instanceof ModelContainerProjectFile);
        } catch (Exception e)
        {
            System.out.println(e);
            fail();
        }
    }

    @Test
    public void deserializes_2_03_01_FromJSON() throws Exception
    {
        ProjectFileDeserialiser deserializer
                = new ProjectFileDeserialiser();
        SimpleModule module
                = new SimpleModule("LegacyProjectFileDeserialiserModule",
                        new Version(1, 0, 0, null));
        module.addDeserializer(ProjectFile.class, deserializer);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(module);

        ProjectFile projectFileReceived
                = mapper.readValue(jsonifiedClass_2_03_01, ProjectFile.class);
        assertNotNull(projectFileReceived);
        assertTrue(projectFileReceived instanceof ModelContainerProjectFile);
    }

    private ModelContainerProjectFile createTestProjectFile()
    {
        ModelContainerProjectFile projectFile = new ModelContainerProjectFile();
        return projectFile;
    }
}
