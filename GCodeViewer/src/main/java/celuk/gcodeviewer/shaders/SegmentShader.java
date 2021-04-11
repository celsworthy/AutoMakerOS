package celuk.gcodeviewer.shaders;

import celuk.gcodeviewer.entities.Camera;
import celuk.gcodeviewer.entities.Light;
import static celuk.gcodeviewer.shaders.ShaderProgram.SHADER_DIRECTORY;
import celuk.gcodeviewer.utils.MatrixUtils;
import java.util.List;
import org.joml.Matrix4f;
import org.joml.Vector3f;
//import org.lwjgl.util.vector.Matrix4f;
//import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author George Salter
 */
public class SegmentShader  extends ShaderProgram {
    private static final String VERTEX_FILE = SHADER_DIRECTORY + "segmentVertexShader.txt";
    private static final String GEOMETRY_FILE = SHADER_DIRECTORY + "segmentGeometryShader.txt";
    private static final String FRAGMENT_FILE = SHADER_DIRECTORY + "segmentFragmentShader.txt";
    
    private int location_lightPosition;
    private int location_lightColour;
    private int location_compositeMatrix;
    private int location_topVisibleLine;
    private int location_bottomVisibleLine;
    private int location_firstSelectedLine;
    private int location_lastSelectedLine;
    private int location_showFlags;
    private int location_showTools;
    private int location_toolColours;
    private int location_showTypes;
    private int location_typeColours;
    private int location_selectColour;
    
    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;
            
    public SegmentShader() {
        super(VERTEX_FILE, GEOMETRY_FILE, FRAGMENT_FILE);
    }
            
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "direction");
        super.bindAttribute(2, "normal");
        super.bindAttribute(3, "dimensions");
        super.bindAttribute(4, "colour");
        super.bindAttribute(5, "attributes");
        super.bindAttribute(6, "angles");
    }

    @Override
    protected void getAllUniformLocations() {
        location_lightPosition = super.getUniformLocation("lightPosition");
        location_lightColour = super.getUniformLocation("lightColour");
        location_compositeMatrix = super.getUniformLocation("compositeMatrix");
        location_topVisibleLine = super.getUniformLocation("topVisibleLine");
        location_bottomVisibleLine = super.getUniformLocation("bottomVisibleLine");
        location_firstSelectedLine = super.getUniformLocation("firstSelectedLine");
        location_lastSelectedLine = super.getUniformLocation("lastSelectedLine");
        location_showFlags = super.getUniformLocation("showFlags");
        location_showTools = super.getUniformLocation("showTools");
        location_showTypes = super.getUniformLocation("showTypes");
        location_toolColours = super.getUniformLocation("toolColours");
        location_typeColours = super.getUniformLocation("typeColours");
        location_selectColour = super.getUniformLocation("selectColour");
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
    
    public void loadLight(Light light) {
        super.loadVector3(location_lightPosition, light.getPosition());
        super.loadVector3(location_lightColour, light.getColour());
    }
    
    public void loadVisibleLimits(int topVisibleLine, int bottomVisibleLine) {
        super.loadInt(location_topVisibleLine, topVisibleLine);
        super.loadInt(location_bottomVisibleLine, bottomVisibleLine);
    }

    public void loadSelectionLimits(int firstSelectedLine, int lastSelectedLine) {
        super.loadInt(location_firstSelectedLine, firstSelectedLine);
        super.loadInt(location_lastSelectedLine, lastSelectedLine);
    }

    public void loadShowFlags(int showFlags) {
        super.loadInt(location_showFlags, showFlags);
    }

    public void loadShowTools(int showTools) {
        super.loadInt(location_showTools, showTools);
    }

    public void loadShowTypes(int showTypes) {
        super.loadInt(location_showTypes, showTypes);
    }

    public void loadToolColours(List<Vector3f> toolColours) {
        super.loadVector3ArraytoUVector4(location_toolColours, toolColours);
    }

    public void loadTypeColours(List<Vector3f> typeColours) {
        super.loadVector3ArraytoUVector4(location_typeColours, typeColours);
    }

    public void loadSelectColour(Vector3f selectColour) {
        super.loadVector3(location_selectColour, selectColour);
    }
}
