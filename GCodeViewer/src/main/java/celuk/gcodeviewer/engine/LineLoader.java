package celuk.gcodeviewer.engine;

import celuk.gcodeviewer.entities.LineEntity;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

public class LineLoader {
    
    private final List<RawEntity> lineEntities = new ArrayList<>();
    
    public RawEntity loadToVAO(List<LineEntity> lines) {
        RawEntity lineEntity = createVAO(2 * lines.size());
        lineEntities.add(lineEntity);
        storeLineInAttributeList(lineEntity, 0, lines);
        storeColourInAttributeList(lineEntity, 1, lines, LineEntity::getColour);

        unbindVAO();
        return lineEntity;
    }
    
    public void cleanUp() {
        lineEntities.stream().forEach(lineEntity -> lineEntity.cleanup());
        lineEntities.clear();
    }
    
    private RawEntity createVAO(int nVertices) {
        int vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);
        return new RawEntity(vaoId, nVertices);
    }

    private void storeLineInAttributeList(RawEntity lineEntity, int attributeNumber, List<LineEntity> lines) {
        int vboId = glGenBuffers();
        lineEntity.setVboId(attributeNumber, vboId);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(8 * lines.size());
        lines.forEach(line -> {
                Vector3f p1 = line.getStart();
                Vector3f p2 = line.getEnd();
                floatBuffer.put(p1.x());
                floatBuffer.put(p1.y());
                floatBuffer.put(p1.z());
                floatBuffer.put(1.0f);
                floatBuffer.put(p2.x());
                floatBuffer.put(p2.y());
                floatBuffer.put(p2.z());
                floatBuffer.put(1.0f);
                
            });
        floatBuffer.flip();
        glBufferData(GL_ARRAY_BUFFER, floatBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(attributeNumber, 4, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private void storeColourInAttributeList(RawEntity lineEntity, int attributeNumber, List<LineEntity> lines, Function<LineEntity, Vector3f> getColour) {
        int vboId = glGenBuffers();
        lineEntity.setVboId(attributeNumber, vboId);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(8 * lines.size());
        lines.forEach(segment -> {
                Vector3f v = getColour.apply(segment);
                // Store 2 colours - one for each vertex of the line.
                floatBuffer.put(v.x());
                floatBuffer.put(v.y());
                floatBuffer.put(v.z());
                floatBuffer.put(1.0f);
                floatBuffer.put(v.x());
                floatBuffer.put(v.y());
                floatBuffer.put(v.z());
                floatBuffer.put(1.0f);
            });
        floatBuffer.flip();
        glBufferData(GL_ARRAY_BUFFER, floatBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(attributeNumber, 4, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private void unbindVAO() {
        glBindVertexArray(0);
    }
}
