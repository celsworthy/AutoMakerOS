package celuk.gcodeviewer.engine;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.BufferUtils;

public class ModelLoader {
    
    private final List<Integer> vaos = new ArrayList<>();
    private final List<Integer> vbos = new ArrayList<>();
    
    public RawModel loadToVAO(float[] positions, float[] normals, int[] indices) {
        int vaoId = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(0, positions);
        storeDataInAttributeList(1, normals);
        unbindVAO();
        return new RawModel(vaoId, indices.length);
    }
    
    public RawModel loadToVAO(float[] positions) {
        int vaoId = createVAO();
        storeDataInAttributeList(0, positions);
        unbindVAO();
        return new RawModel(vaoId, positions.length / 3);
    }
    
    public void cleanUp() {
        vaos.stream().forEach(vao -> glDeleteVertexArrays(vao));
        vbos.stream().forEach(vbo -> glDeleteBuffers(vbo));
    }
    
    private int createVAO() {
        int vaoId = glGenVertexArrays();
        vaos.add(vaoId);
        glBindVertexArray(vaoId);
        return vaoId;
    }
    
    private void storeDataInAttributeList(int attributeNumber, float[] data) {
        int vboId = glGenBuffers();
        vbos.add(vboId);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        FloatBuffer floatBufferData = storeDataInFloatBuffer(data);
        glBufferData(GL_ARRAY_BUFFER, floatBufferData, GL_STATIC_DRAW);
        glVertexAttribPointer(attributeNumber, 3, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
    
    private void unbindVAO() {
        glBindVertexArray(0);
    }
    
    private void bindIndicesBuffer(int[] indices) {
        int vboId = glGenBuffers();
        vbos.add(vboId);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
        IntBuffer intBufferData = storeDataInIntBuffer(indices);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, intBufferData, GL_STATIC_DRAW);
    }
    
    private IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer intBuffer = BufferUtils.createIntBuffer(data.length);
        intBuffer.put(data);
        intBuffer.flip();
        return intBuffer;
    }
    
    private FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(data.length);
        floatBuffer.put(data);
        floatBuffer.flip();
        return floatBuffer;
    }
}
