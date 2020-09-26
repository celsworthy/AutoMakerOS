package celtech.roboxbase.configuration.datafileaccessors;

import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.configuration.fileRepresentation.SlicerMappings;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author ianhudson
 */
public final class SlicerMappingsContainer
{

    private static final Stenographer steno = StenographerFactory.getStenographer(SlicerMappingsContainer.class.getName());
    private static SlicerMappingsContainer instance = null;
    private static SlicerMappings slicerMappingsFile = null;
    private static final ObjectMapper mapper = new ObjectMapper();

    public static final String defaultSlicerMappingsFileName = "slicermapping.dat";

    private SlicerMappingsContainer()
    {
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        loadSlicerMappingsFile();
    }
    
    public void loadSlicerMappingsFile() 
    {        
        File slicerMappingsInputFile = new File(BaseConfiguration.getApplicationPrintProfileDirectory() + defaultSlicerMappingsFileName);
        if (!slicerMappingsInputFile.exists())
        {
            slicerMappingsFile = new SlicerMappings();
            try
            {
                mapper.writeValue(slicerMappingsInputFile, slicerMappingsFile);
            } catch (IOException ex)
            {
                steno.error("Error trying to load slicer mapping file");
            }
        } else
        {
            try
            {
                slicerMappingsFile = mapper.readValue(slicerMappingsInputFile, SlicerMappings.class);

            } catch (IOException ex)
            {
                steno.exception("Error loading slicer mapping file " + slicerMappingsInputFile.getAbsolutePath(), ex);
            }
        }
    }

    /**
     *
     * @return
     */
    public static SlicerMappingsContainer getInstance()
    {
        if (instance == null)
        {
            instance = new SlicerMappingsContainer();
        }

        return instance;
    }

    /**
     *
     * @return
     */
    public static SlicerMappings getSlicerMappings()
    {
        if (instance == null)
        {
            instance = new SlicerMappingsContainer();
        }

        return slicerMappingsFile;
    }
}
