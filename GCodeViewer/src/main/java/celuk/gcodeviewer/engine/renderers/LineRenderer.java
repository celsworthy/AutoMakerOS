package celuk.gcodeviewer.engine.renderers;

import celuk.gcodeviewer.engine.RawEntity;
import celuk.gcodeviewer.engine.RenderParameters;
import celuk.gcodeviewer.entities.Camera;
import celuk.gcodeviewer.entities.Light;
import celuk.gcodeviewer.shaders.LineShader;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class LineRenderer {
   
    private final LineShader shader;
    private Matrix4f projectionMatrix;
    
    public LineRenderer(LineShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;
        this.projectionMatrix = projectionMatrix;
    }
    
    public void prepare(Camera camera,
                        Light light,
                        RenderParameters renderParameters) {
        MasterRenderer.checkErrors();
        shader.start();
        MasterRenderer.checkErrors();
        shader.setProjectionMatrix(projectionMatrix);
        MasterRenderer.checkErrors();
        shader.setViewMatrix(camera);
        MasterRenderer.checkErrors();
        shader.loadCompositeMatrix();
        MasterRenderer.checkErrors();
    }
    
    public void render(RawEntity rawEntity) {
        if (rawEntity != null) {
            bindRawModel(rawEntity);
            MasterRenderer.checkErrors();
            glDrawArrays(GL_LINES, 0, rawEntity.getVertexCount());
            MasterRenderer.checkErrors();
            unbindRawModel();
            MasterRenderer.checkErrors();
        }
    }

    public void finish() {
        shader.stop();
        MasterRenderer.checkErrors();
    }

    public void setProjectionMatrix(Matrix4f projectionMatrix) {
        this.projectionMatrix = projectionMatrix;
    }
    
    public void bindRawModel(RawEntity rawEntity) {
        glBindVertexArray(rawEntity.getVaoId());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
    }
    
    public void unbindRawModel() {
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
    }
}
