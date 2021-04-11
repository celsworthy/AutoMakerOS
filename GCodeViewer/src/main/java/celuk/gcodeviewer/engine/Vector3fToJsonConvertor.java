/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celuk.gcodeviewer.engine;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import org.joml.Vector3f;

/**
 *
 * @author Tony
 */
public class Vector3fToJsonConvertor {
    // Jackson serializer for Vector3f.
    public static class Vector3fSerializer extends StdSerializer<Vector3f> {
     
        public Vector3fSerializer() {
            this(null);
        }

        public Vector3fSerializer(Class<Vector3f> t) {
            super(t);
        }

        @Override
        public void serialize(Vector3f value,
                              JsonGenerator jgen,
                              SerializerProvider provider) 
                    throws IOException, JsonProcessingException {
            jgen.writeStartArray();
            jgen.writeNumber(value.x);
            jgen.writeNumber(value.y);
            jgen.writeNumber(value.z);
            jgen.writeEndArray();
        }
    }

    // Jackson deserializer for Vector3f.
    public static class Vector3fDeserializer extends StdDeserializer<Vector3f> {
     
        public Vector3fDeserializer() {
            this(null);
        }

        public Vector3fDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public Vector3f deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException
        {
            ObjectCodec codec = jp.getCodec();
            JsonNode node = codec.readTree(jp);
            Vector3f v3f = new Vector3f(node.get(0).floatValue(),
                                        node.get(1).floatValue(),
                                        node.get(2).floatValue());
            return v3f;
        }
    }

}
