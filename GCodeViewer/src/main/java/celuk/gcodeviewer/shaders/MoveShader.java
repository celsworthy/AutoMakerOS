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
public class MoveShader  extends ShaderProgram {
    private static final String VERTEX_FILE = SHADER_DIRECTORY + "moveVertexShader.txt";
    private static final String GEOMETRY_FILE = SHADER_DIRECTORY + "moveGeometryShader.txt";
    private static final String FRAGMENT_FILE = SHADER_DIRECTORY + "moveFragmentShader.txt";
    
    private int location_compositeMatrix;
    private int location_topVisibleLine;
    private int location_bottomVisibleLine;
    private int location_firstSelectedLine;
    private int location_lastSelectedLine;
    private int location_moveColour;
    private int location_selectColour;
    private int location_stylusColour;
    private int location_stylusHeight;
    private int location_showFlags;
    
    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;
            
    public MoveShader() {
        super(VERTEX_FILE, GEOMETRY_FILE, FRAGMENT_FILE);
    }
            
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "attributes");
    }

    @Override
    protected void getAllUniformLocations() {
        location_compositeMatrix = super.getUniformLocation("compositeMatrix");
        location_topVisibleLine = super.getUniformLocation("topVisibleLine");
        location_bottomVisibleLine = super.getUniformLocation("bottomVisibleLine");
        location_firstSelectedLine = super.getUniformLocation("firstSelectedLine");
        location_lastSelectedLine = super.getUniformLocation("lastSelectedLine");
        location_moveColour = super.getUniformLocation("moveColour");
        location_selectColour = super.getUniformLocation("selectColour");
        location_stylusColour = super.getUniformLocation("stylusColour");
        location_stylusHeight = super.getUniformLocation("stylusHeight");
        location_showFlags = super.getUniformLocation("showFlags");
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
        
    public void loadVisibleLimits(int topVisibleLine, int bottomVisibleLine) {
        super.loadInt(location_topVisibleLine, topVisibleLine);
        super.loadInt(location_bottomVisibleLine, bottomVisibleLine);
    }

    public void loadSelectionLimits(int firstSelectedLine, int lastSelectedLine) {
        super.loadInt(location_firstSelectedLine, firstSelectedLine);
        super.loadInt(location_lastSelectedLine, lastSelectedLine);
    }

    public void loadMoveColour(Vector3f moveColour) {
        super.loadVector3(location_moveColour, moveColour);
    }

    public void loadSelectColour(Vector3f selectColour) {
        super.loadVector3(location_selectColour, selectColour);
    }

    public void loadStylusColour(Vector3f stylusColour) {
        super.loadVector3(location_stylusColour, stylusColour);
    }

    public void loadStylusHeight(float stylusHeight) {
        super.loadFloat(location_stylusHeight, stylusHeight);
    }

    public void loadShowFlags(int showFlags) {
        super.loadInt(location_showFlags, showFlags);
    }
}
