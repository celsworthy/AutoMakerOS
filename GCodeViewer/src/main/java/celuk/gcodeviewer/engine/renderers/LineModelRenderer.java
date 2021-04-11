package celuk.gcodeviewer.engine.renderers;

import celuk.gcodeviewer.engine.RawModel;
import static org.lwjgl.opengl.GL11.*;
import celuk.gcodeviewer.entities.LineEntity;
import celuk.gcodeviewer.shaders.LineModelShader;
import celuk.gcodeviewer.utils.MatrixUtils;
import java.util.List;
import org.joml.Matrix4f;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

/**
 *
 * @author George Salter
 */
public class LineModelRenderer {
    
    private final LineModelShader shader;

    LineModelRenderer(LineModelShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;
        loadProjectionMatrix(projectionMatrix);
    }
    
    public final void loadProjectionMatrix(Matrix4f projectionMatrix) {
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }
    
    public void render(List<LineEntity> lineEntities) {
        RawModel lineModel = lineEntities.get(0).getLineModel();
        prepareRawModel(lineModel);
        lineEntities.forEach(lineEntity -> {
            prepareInstance(lineEntity);
            glDrawArrays(GL_LINES, 0, 2);
        });
        unbindRawModel();
    }
    
    public void prepareRawModel(RawModel model) {
        glBindVertexArray(model.getVaoId());
        glEnableVertexAttribArray(0);
    }
    
    public void unbindRawModel() {
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
    }
    
    public void prepareInstance(LineEntity lineEntity) {
        Matrix4f transformationMatrix = MatrixUtils.createTransformationMatrixForLine(
                lineEntity.getPosition(), 
                lineEntity.getRotationX(), 
                lineEntity.getRotationY(), 
                lineEntity.getRotationZ(),
                lineEntity.getLength());
        shader.loadTransformationMatrix(transformationMatrix);
        shader.loadColour(lineEntity.getColour());
    }
}
