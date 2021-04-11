package celuk.gcodeviewer.entities;

import celuk.gcodeviewer.engine.ModelLoader;
import celuk.gcodeviewer.engine.RawModel;

/**
 * 
 * @author George Salter
 */
public class Floor {
    
    private static final int VERTEX_COUNT = 2;
    
    private final float sizeX;
    private final float sizeY;
    
    private final float xPos;
    private final float yPos;
    private final float zPos;
    private final RawModel model;
    
    public Floor(float sizeX, float sizeY, float offsetX, float offsetY, float offsetZ, ModelLoader modelLoader) {
        this.xPos = offsetX;
        this.yPos = offsetY;
        this.zPos = offsetZ;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.model = generateFloor(modelLoader);
    }

    public float getXPos() {
        return xPos;
    }

    public float getYPos() {
        return yPos;
    }

    public float getZPos() {
        return zPos;
    }

    public RawModel getModel() {
        return model;
    }
    
    private RawModel generateFloor(ModelLoader modelLoader) {
        int count = VERTEX_COUNT * VERTEX_COUNT;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        //float[] textureCoords = new float[count*2];
        int[] indices = new int[6 * (VERTEX_COUNT-1) * (VERTEX_COUNT - 1)];
        int vertexPointer = 0;
        for(int i = 0; i<VERTEX_COUNT; i++){
            for(int j = 0; j < VERTEX_COUNT; j++){
                vertices[vertexPointer * 3] = sizeX * (float)(j / ((float) VERTEX_COUNT - 1));
                vertices[vertexPointer * 3 + 1] = sizeY * (float)(i / ((float) VERTEX_COUNT - 1));
                vertices[vertexPointer * 3 + 2] = -0.1f; // Lower the floor slightly to reduce z buffer fighting artifacts.
                normals[vertexPointer * 3] = 0.0f;
                normals[vertexPointer * 3 + 1] = 0.0f;
                normals[vertexPointer * 3 + 2] = 1.0f;
                //textureCoords[vertexPointer*2] = (float)j/((float)VERTEX_COUNT - 1);
                //textureCoords[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT - 1);
                vertexPointer++;
            }
        }
        int pointer = 0;
        for(int gz = 0; gz < VERTEX_COUNT - 1; gz++){
            for(int gx = 0; gx < VERTEX_COUNT - 1; gx++){
                int topLeft = (gz * VERTEX_COUNT) + gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz + 1) * VERTEX_COUNT) + gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomRight;
                indices[pointer++] = bottomLeft;
            }
        }
        return modelLoader.loadToVAO(vertices, normals, indices);
    }
}
