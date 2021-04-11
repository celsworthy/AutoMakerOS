package celuk.gcodeviewer.shaders;

import celuk.gcodeviewer.entities.Camera;
import celuk.gcodeviewer.entities.Light;
import celuk.gcodeviewer.utils.MatrixUtils;
import org.joml.Matrix4f;

/**
 *
 * @author George Salter
 */
public class FloorShader extends ShaderProgram {
    
    private static final String VERTEX_FILE = SHADER_DIRECTORY + "floorVertexShader.txt";
    private static final String FRAGMENT_FILE = SHADER_DIRECTORY + "floorFragmentShader.txt";
    
    private int location_transformationMatrix;
    private int location_viewMatrix;
    private int location_projectionMatrix;
    private int location_lightPosition;
    private int location_lightColour;
    
    public FloorShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }
            
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "normal");
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_lightPosition = super.getUniformLocation("lightPosition");
        location_lightColour = super.getUniformLocation("lightColour");
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
    
    public void loadLight(Light light) {
        super.loadVector3(location_lightPosition, light.getPosition());
        super.loadVector3(location_lightColour, light.getColour());
    }
}
