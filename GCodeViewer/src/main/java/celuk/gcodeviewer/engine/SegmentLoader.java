package celuk.gcodeviewer.engine;

import celuk.gcodeviewer.entities.Entity;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.lwjgl.BufferUtils;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class SegmentLoader {
    
    private static final int POSITION_ATTRIBUTE = 0;
    private static final int DIRECTION_ATTRIBUTE = 1;
    private static final int NORMAL_ATTRIBUTE = 2;
    private static final int DIMENSION_ATTRIBUTE = 3;
    private static final int COLOUR_ATTRIBUTE = 4;
    private static final int ATTRIBUTES_ATTRIBUTE = 5;
    private static final int ANGLE_ATTRIBUTE = 6;

    private final List<RawEntity> segmentEntities = new ArrayList<>();

    public RawEntity loadToVAO(List<Entity> segments) {
        RawEntity segmentEntity = createVAO(segments.size());
        segmentEntities.add(segmentEntity);
        storeVector3InAttributeList(segmentEntity, POSITION_ATTRIBUTE, segments, Entity::getPosition);
        storeVector4InAttributeList(segmentEntity, DIRECTION_ATTRIBUTE, segments, (Entity s) -> {
                Vector3f d = s.getDirection();
                Vector4f d4 = new Vector4f(d.x(), d.y(), d.z(), 1.0f);
                return d4;
            });
        storeVector4InAttributeList(segmentEntity, NORMAL_ATTRIBUTE, segments, (Entity s) -> {
                Vector3f n = s.getNormal();
                Vector4f n4 = new Vector4f(n.x(), n.y(), n.z(), 1.0f);
                return n4;
            });
        storeVector4InAttributeList(segmentEntity, DIMENSION_ATTRIBUTE, segments, (Entity s) -> {
                int layerNumber = s.getLayer();
                if (layerNumber == Entity.NULL_LAYER)
                    layerNumber = s.getLineNumber();
                Vector4f v = new Vector4f(s.getLength(), s.getWidth(), s.getThickness(), 1.0f);
                return v;
            });
        storeVector3InAttributeList(segmentEntity, COLOUR_ATTRIBUTE, segments, Entity::getColour);
        storeVector4InAttributeList(segmentEntity, ATTRIBUTES_ATTRIBUTE, segments, (Entity s) -> {
                int layerNumber = s.getLayer();
                if (layerNumber == Entity.NULL_LAYER)
                    layerNumber = s.getLineNumber();
                Vector4f v = new Vector4f(s.getTypeIndex(), layerNumber, s.getLineNumber(), s.getToolNumber());
                return v;
            });
        storeVector4InAttributeList(segmentEntity, ANGLE_ATTRIBUTE, segments, (Entity s) -> {
                float b = 0.01745329252f * s.getDataValue(Entity.DATA_B); // B in radians.
                float c = 0.01745329252f * s.getDataValue(Entity.DATA_C); // C in radians.
                Vector4f v = new Vector4f((float)Math.cos(b), (float)Math.sin(b), (float)Math.cos(c), (float)Math.sin(c));
                return v;
            });
        unbindVAO();
        return segmentEntity;
    }
    
    public void reloadColours(RawEntity segmentEntity, List<Entity> segments) {
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(3 * segments.size());
        segments.forEach(segment -> {
                Vector3f v = segment.getColour();
                floatBuffer.put(v.x());
                floatBuffer.put(v.y());
                floatBuffer.put(v.z());
            });
        floatBuffer.flip();
        glBindVertexArray(segmentEntity.getVaoId());
        int vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, floatBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(COLOUR_ATTRIBUTE, 3, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        segmentEntity.setVboId(COLOUR_ATTRIBUTE, vboId);
        glBindVertexArray(0);
    }

    public void cleanUp() {
        segmentEntities.stream().forEach(segment -> segment.cleanup());
        segmentEntities.clear();
    }
    
    private RawEntity createVAO(int nVertices) {
        int vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);
        return new RawEntity(vaoId, nVertices);
    }
    
    private void storeVector3InAttributeList(RawEntity segmentEntity, int attributeNumber, List<Entity> segments, Function<Entity, Vector3f> getVector3) {
        int vboId = glGenBuffers();
        segmentEntity.setVboId(attributeNumber, vboId);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(3 * segments.size());
        segments.forEach(segment -> {
                Vector3f v = getVector3.apply(segment);
                floatBuffer.put(v.x());
                floatBuffer.put(v.y());
                floatBuffer.put(v.z());
            });
        floatBuffer.flip();
        glBufferData(GL_ARRAY_BUFFER, floatBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(attributeNumber, 3, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private void storeVector4InAttributeList(RawEntity segmentEntity, int attributeNumber, List<Entity> segments, Function<Entity, Vector4f> getVector4) {
        int vboId = glGenBuffers();
        segmentEntity.setVboId(attributeNumber, vboId);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(4 * segments.size());
        segments.forEach(segment -> {
                Vector4f v = getVector4.apply(segment);
                floatBuffer.put(v.x());
                floatBuffer.put(v.y());
                floatBuffer.put(v.z());
                floatBuffer.put(v.w());
            });
        floatBuffer.flip();
        glBufferData(GL_ARRAY_BUFFER, floatBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(attributeNumber, 4, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private void storeInteger4InAttributeList(RawEntity segmentEntity, int attributeNumber, List<Entity> segments, Function<Entity, Integer[]> getInteger4) {
        int vboId = glGenBuffers();
        segmentEntity.setVboId(attributeNumber, vboId);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(4 * segments.size());
//        IntBuffer intBuffer = BufferUtils.createIntBuffer(4 * entities.size());
        segments.forEach(segment -> {
                Integer[] i4 = getInteger4.apply(segment);                
                floatBuffer.put((float)i4[0]);
                floatBuffer.put((float)i4[1]);
                floatBuffer.put((float)i4[2]);
                floatBuffer.put((float)i4[3]);
//                intBuffer.put(i4[0]);
//                intBuffer.put(i4[1]);
//                intBuffer.put(i4[2]);
//                intBuffer.put(i4[3]);
            });
        floatBuffer.flip();
        glBufferData(GL_ARRAY_BUFFER, floatBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(attributeNumber, 4, GL_FLOAT, false, 0, 0);
//        intBuffer.flip();
//        glBufferData(GL_ARRAY_BUFFER, intBuffer, GL_STATIC_DRAW);
//        glVertexAttribIPointer(attributeNumber, 4, GL_INT, 32, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private void unbindVAO() {
        glBindVertexArray(0);
    }
}
