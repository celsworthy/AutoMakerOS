package celuk.gcodeviewer.engine;

import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;

public class RawEntity {
    public static final int N_VBO_ATTRIBUTES = 8;
    private int vaoId;
    private final int vboAttributes[] = new int[N_VBO_ATTRIBUTES];
    private int vertexCount;
    
    public RawEntity(int vaoId, int vertexCount) {
        this.vaoId = vaoId;
        this.vertexCount = vertexCount;
        for (int attributeNumber = 0; attributeNumber < N_VBO_ATTRIBUTES; ++attributeNumber)
            vboAttributes[attributeNumber] = 0;
    }

    public int getVaoId() {
        return vaoId;
    }

    public int getVboId(int attributeNumber) {
        if (attributeNumber >= 0 && attributeNumber < N_VBO_ATTRIBUTES)
            return vboAttributes[attributeNumber];
        else
           return 0;
    }

    public void setVboId(int attributeNumber, int vboId) {
        if (attributeNumber >= 0 && attributeNumber < N_VBO_ATTRIBUTES)
        {
            if (vboAttributes[attributeNumber] != 0 && vboAttributes[attributeNumber] != vboId)
                glDeleteBuffers(vboAttributes[attributeNumber]);
            vboAttributes[attributeNumber] = vboId;
        }
    }

    public int getVertexCount() {
        return vertexCount;
    }
    
    public void cleanup() {
        glDeleteVertexArrays(vaoId);
        for (int attributeNumber = 0; attributeNumber < N_VBO_ATTRIBUTES; ++attributeNumber) {
            if (vboAttributes[attributeNumber] != 0)
            {
                glDeleteBuffers(vboAttributes[attributeNumber]);
                vboAttributes[attributeNumber] = 0;
            }
        }
        // OpenGL errors occur without this, although I don't understand why.
        vaoId = 0;
        vertexCount = 0;
    }
}
