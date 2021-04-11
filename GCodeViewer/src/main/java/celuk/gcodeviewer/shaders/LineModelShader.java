package celuk.gcodeviewer.shaders;

import celuk.gcodeviewer.entities.Camera;
import static celuk.gcodeviewer.shaders.ShaderProgram.SHADER_DIRECTORY;
import celuk.gcodeviewer.utils.MatrixUtils;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 *
 * @author George Salter
 */
public class LineModelShader  extends ShaderProgram {
    private static final String VERTEX_FILE = SHADER_DIRECTORY + "lineModelVertexShader.txt";
    private static final String FRAGMENT_FILE = SHADER_DIRECTORY + "lineModelFragmentShader.txt";
    
    private int location_transformationMatrix;
    private int location_viewMatrix;
    private int location_projectionMatrix;
    private int location_modelColour;
    
    public LineModelShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }
            
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_modelColour = super.getUniformLocation("modelColour");
    }
    
    public void loadTransformationMatrix(Matrix4f transformation) {
        super.loadMatrix(location_transformationMatrix, transformation);
    }
    
    public void loadViewMatrix(Camera camera) {
        Matrix4f viewMatrix = MatrixUtils.createViewMatrix(camera);
        super.loadMatrix(location_viewMatrix, viewMatrix);
    }
    
    public void loadProjectionMatrix(Matrix4f projection) {
        super.loadMatrix(location_projectionMatrix, projection);
    }
    
    public void loadColour(Vector3f colour) {
        super.loadVector3(location_modelColour, colour);
    }
}
