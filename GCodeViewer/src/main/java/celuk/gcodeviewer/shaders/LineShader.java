package celuk.gcodeviewer.shaders;

import celuk.gcodeviewer.entities.Camera;
import static celuk.gcodeviewer.shaders.ShaderProgram.SHADER_DIRECTORY;
import celuk.gcodeviewer.utils.MatrixUtils;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 *
 * @author Tony Aldhous
 */
public class LineShader  extends ShaderProgram {
    private static final String VERTEX_FILE = SHADER_DIRECTORY + "lineVertexShader.txt";
    private static final String FRAGMENT_FILE = SHADER_DIRECTORY + "lineFragmentShader.txt";
    
    private int location_compositeMatrix;
    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;
            
    public LineShader() {
        super(VERTEX_FILE, null, FRAGMENT_FILE);
    }
            
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "colour");
    }

    @Override
    protected void getAllUniformLocations() {
        location_compositeMatrix = super.getUniformLocation("compositeMatrix");
    }
    
    public void setViewMatrix(Camera camera) {
        this.viewMatrix = MatrixUtils.createViewMatrix(camera);
    }
    
    public void setProjectionMatrix(Matrix4f projectionMatrix) {
        this.projectionMatrix = projectionMatrix;
    }
    
    public void loadCompositeMatrix() {
        Matrix4f composite = new Matrix4f(projectionMatrix);
        composite.mul(viewMatrix);
        super.loadMatrix(location_compositeMatrix, composite);
    }
}
