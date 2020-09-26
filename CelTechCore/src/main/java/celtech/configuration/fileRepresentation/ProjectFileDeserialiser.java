package celtech.configuration.fileRepresentation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * This deserialiser is used to coerce old project files into the new
 * polymorphic structure In essence anything that doesn't have a type defined in
 * the JSON should be assumed to be an old project file
 *
 * @author Ian
 */
public class ProjectFileDeserialiser extends StdDeserializer<ProjectFile>
{

    public ProjectFileDeserialiser()
    {
        this(null);
    }

    public ProjectFileDeserialiser(Class<?> vc)
    {
        super(vc);
    }

    @Override
    public ProjectFile deserialize(
            JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException
    {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        ObjectNode root = (ObjectNode) mapper.readTree(jp);
        Class<? extends ProjectFile> projectFileClass = null;
        Iterator<Entry<String, JsonNode>> elementsIterator = root.fields();
        int projectFileVersion = -1;

        while (elementsIterator.hasNext() && projectFileClass == null)
        {
            Entry<String, JsonNode> element = elementsIterator.next();
            String name = element.getKey();
            if (name.equals("projectType"))
            {
                ProjectFileTypeEnum projectFileType = ProjectFileTypeEnum.valueOf(element.getValue().asText());
                switch (projectFileType)
                {
                    case MODEL:
                        projectFileClass = ModelContainerProjectFile.class;
                        break;
                    case SHAPE:
                        projectFileClass = ShapeContainerProjectFile.class;
                        break;
                }
            } else if (name.equals("version"))
            {
                projectFileVersion = element.getValue().asInt();
            }
        }

        if (projectFileClass == null)
        {
            if (projectFileVersion > 0 && projectFileVersion < 4)
            {
                //This was a legacy project file - only Model Projects used to exist...
                projectFileClass = ModelContainerProjectFile.class;
            } else
            {
                //We couldn't work out what this was...
                return null;
            }
        }
        return mapper.readValue(root.toString(), projectFileClass);
    }
}
