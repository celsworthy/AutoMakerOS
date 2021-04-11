package celuk.gcodeviewer.engine.renderers;

import celuk.gcodeviewer.engine.RawModel;
import celuk.gcodeviewer.entities.Floor;
import celuk.gcodeviewer.shaders.FloorShader;
import celuk.gcodeviewer.utils.MatrixUtils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

/**
 * 
 * @author George Salter
 */
public class FloorRenderer {
    
    private final FloorShader shader;
    
    public FloorRenderer(FloorShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;
        loadProjectionMatrix(projectionMatrix);
    }
    
    public final void loadProjectionMatrix(Matrix4f projectionMatrix) {
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }
    
    public void render(Floor floor) {
        prepareFloor(floor);
        loadModelMatrix(floor);
        glDrawElements(GL_TRIANGLES, floor.getModel().getVertexCount(), GL_UNSIGNED_INT, 0);
        unbindRawModel();
    }
    
    public void prepareFloor(Floor floor) {
        RawModel model = floor.getModel();
        glBindVertexArray(model.getVaoId());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
    }
    
    public void unbindRawModel() {
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
    }
    
    public void loadModelMatrix(Floor floor) {
        Matrix4f transformationMatrix = MatrixUtils.createTransformationMatrix(
                new Vector3f(floor.getXPos(), floor.getYPos(), floor.getZPos()), 
                0, 0, 0, 1);
        shader.loadTransformationMatrix(transformationMatrix);
    }
}
