package celuk.gcodeviewer.engine;

public class RawModel {
    
    private final int vaoId;
    private final int vertexCount;
    
    public RawModel(int vaoId, int vertexCount) {
        this.vaoId = vaoId;
        this.vertexCount = vertexCount;
    }

    public int getVaoId() {
        return vaoId;
    }

    public int getVertexCount() {
        return vertexCount;
    }
    
}
