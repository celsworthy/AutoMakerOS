package celuk.gcodeviewer.engine;

import celuk.gcodeviewer.entities.Entity;
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

public class MoveLoader {
    
    private final List<RawEntity> moveEntities = new ArrayList<>();
    
    public RawEntity loadToVAO(List<Entity> moves) {
        RawEntity moveEntity = createVAO(2 * moves.size());
        moveEntities.add(moveEntity);
        storeSegmentInAttributeList(moveEntity, 0, moves);
        store2xInteger2InAttributeList(moveEntity, 1, moves, (Entity m) -> {
                Integer[] i2 = new Integer[2];
                int layerNumber = m.getLayer();
                if (layerNumber == Entity.NULL_LAYER)
                    layerNumber = m.getLineNumber();
                i2[0] = layerNumber;
                i2[1] = m.getLineNumber();
                return i2;
            });
        unbindVAO();
        return moveEntity;
    }
    
    public void cleanUp() {
        moveEntities.stream().forEach(moveEntity -> moveEntity.cleanup());
        moveEntities.clear();
    }
    
    private RawEntity createVAO(int nVertices) {
        int vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);
        return new RawEntity(vaoId, nVertices);
    }
    
    private void storeSegmentInAttributeList(RawEntity moveEntity, int attributeNumber, List<Entity> moves) {
        int vboId = glGenBuffers();
        moveEntity.setVboId(attributeNumber, vboId);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(6 * moves.size());
        moves.forEach(move -> {
                Vector3f p = move.getPosition();
                Vector3f d = move.getDirection();
                d.mul(0.5f * move.getLength());
                float halfLength = 0.5f * move.getLength();
                Vector3f p1 = new Vector3f(p).sub(d);
                Vector3f p2 = new Vector3f(p).add(d);
                floatBuffer.put(p1.x());
                floatBuffer.put(p1.y());
                floatBuffer.put(p1.z());
                floatBuffer.put(p2.x());
                floatBuffer.put(p2.y());
                floatBuffer.put(p2.z());
            });
        floatBuffer.flip();
        glBufferData(GL_ARRAY_BUFFER, floatBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(attributeNumber, 3, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private void store2xInteger2InAttributeList(RawEntity moveEntity, int attributeNumber, List<Entity> moves, Function<Entity, Integer[]> getInteger2) {
        int vboId = glGenBuffers();
        moveEntity.setVboId(attributeNumber, vboId);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(4 * moves.size());
        moves.forEach(move -> {
                Integer[] i2 = getInteger2.apply(move);
                // Store values twice - one for each vertex in the segment.
                floatBuffer.put((float)i2[0]);
                floatBuffer.put((float)i2[1]);
                floatBuffer.put((float)i2[0]);
                floatBuffer.put((float)i2[1]);
            });
        floatBuffer.flip();
        glBufferData(GL_ARRAY_BUFFER, floatBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(attributeNumber, 2, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private void unbindVAO() {
        glBindVertexArray(0);
    }
}
