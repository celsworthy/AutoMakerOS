package celuk.gcodeviewer.engine.renderers;

import celuk.gcodeviewer.engine.RawEntity;
import celuk.gcodeviewer.engine.RawModel;
import celuk.gcodeviewer.engine.RenderParameters;
import celuk.gcodeviewer.engine.RenderingEngine;
import celuk.gcodeviewer.shaders.AngleShader;
import celuk.gcodeviewer.entities.Camera;
import celuk.gcodeviewer.entities.CenterPoint;
import celuk.gcodeviewer.entities.Entity;
import celuk.gcodeviewer.entities.Floor;
import celuk.gcodeviewer.entities.Light;
import celuk.gcodeviewer.entities.LineEntity;
import celuk.gcodeviewer.entities.PrintVolume;
import celuk.gcodeviewer.shaders.SegmentShader;
import celuk.gcodeviewer.shaders.FloorShader;
import celuk.gcodeviewer.shaders.LineModelShader;
import celuk.gcodeviewer.shaders.LineShader;
import celuk.gcodeviewer.shaders.MoveShader;
import celuk.gcodeviewer.utils.MatrixUtils;
import java.text.DecimalFormat;
//import celuk.gcodeviewer.shaders.StaticShader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import static org.lwjgl.opengl.GL11.*;

public class MasterRenderer {

    private static final float FOV = 70f;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 2500.0f;
    
//    private final StaticShader staticShader = new StaticShader();
//    private final StaticRenderer staticEntityRenderer;
    
    private final AngleShader angleShader = new AngleShader();
    private final AngleRenderer angleRenderer;

    private final SegmentShader segmentShader = new SegmentShader();
    private final SegmentRenderer segmentRenderer;

    private final MoveShader moveShader = new MoveShader();
    private final MoveRenderer moveRenderer;

    private final LineModelShader lineModelShader = new LineModelShader();
    private final LineModelRenderer lineModelRenderer;
    
    private final LineShader lineShader = new LineShader();
    private final LineRenderer lineRenderer;

    private final FloorShader floorShader = new FloorShader();
    private final FloorRenderer floorRenderer;
    
    private RawEntity segmentEntity = null;
    private RawEntity moveEntity = null;
    private RawEntity printVolumeEntity = null;
    private final Map<RawModel, List<Entity>> entities = new HashMap<>();
    private final List<LineEntity> lineEntities = new ArrayList<>();
    private Floor floor;
    private CenterPoint centrePoint;
    
    private Matrix4f projectionMatrix;

    private RenderParameters renderParameters;
    
    public MasterRenderer(RenderParameters renderParameters) {
        createProjectionMatrix(renderParameters.getWindowWidth(), renderParameters.getWindowHeight());
//        this.staticEntityRenderer = new StaticRenderer(staticShader, projectionMatrix);
        this.angleRenderer = new AngleRenderer(angleShader, projectionMatrix);
        this.segmentRenderer = new SegmentRenderer(segmentShader, projectionMatrix);
        this.moveRenderer = new MoveRenderer(moveShader, projectionMatrix);
        this.lineModelRenderer = new LineModelRenderer(lineModelShader, projectionMatrix);
        this.lineRenderer = new LineRenderer(lineShader, projectionMatrix);
        this.floorRenderer = new FloorRenderer(floorShader, projectionMatrix);
        this.renderParameters = renderParameters;
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
    }
    
    public RenderParameters getRenderParameters() {
        return renderParameters;
    }

    public void setRenderParameters(RenderParameters renderParameters) {
        this.renderParameters = renderParameters;
    }

    public void render(Camera camera, Light light) {   
        prepare();
        
//        staticShader.start();
//        staticShader.loadLight(light);
//        staticShader.loadViewMatrix(camera);
//        staticEntityRenderer.render(entities, showMovesFlag, topLayerToShow, bottomLayerToShow, firstLineToShow, lastLineToShow);
//        staticShader.stop();
        
        if (!lineEntities.isEmpty() ||
            (centrePoint != null &&  centrePoint.isRendered())) {
            lineModelShader.start();
            lineModelShader.loadViewMatrix(camera);
            if(!lineEntities.isEmpty()) {
                lineModelRenderer.render(lineEntities);
            }
            if (centrePoint != null && centrePoint.isRendered()) {
                lineModelRenderer.render(centrePoint.getLineEntities());
            }
            lineModelShader.stop();
        }

        if (printVolumeEntity != null) {
            lineRenderer.prepare(camera, light, renderParameters);
            lineRenderer.render(printVolumeEntity);
            lineRenderer.finish();
        }

        if (segmentEntity != null) {
            segmentRenderer.render(segmentEntity, camera, light, renderParameters);
            if (renderParameters.getShowAngles())
                angleRenderer.render(segmentEntity, camera, light, renderParameters);
        }
        
        if (moveEntity != null && (renderParameters.getShowMoves() || renderParameters.getShowStylus())) {
            moveRenderer.render(moveEntity, camera, light, renderParameters);
        }

        if (floor != null) {
            floorShader.start();
            floorShader.loadLight(light);
            floorShader.loadViewMatrix(camera);
            floorRenderer.render(floor);
            floorShader.stop();
        }
        //System.out.println("Camera distance from centre = " + Float.toString(camera.getDistanceFromCenter()));
        //debugPV(new Vector4f(0.0f, 0.0f, 0.0f, 1.0f), camera);
        //debugPV(new Vector4f(210.0f, 0.0f, 0.0f, 1.0f), camera);
        //debugPV(new Vector4f(105.0f, 75.0f, 50.0f, 1.0f), camera);
        //debugPV(new Vector4f(210.0f, 150.0f, 0.0f, 1.0f), camera);
        //debugPV(new Vector4f(0.0f, 150.0f, 0.0f, 1.0f), camera);
        //debugPV(new Vector4f(105.0f, 75.0f, 50.0f, 1.0f), camera);
        checkErrors();
    }
    
    public static void checkErrors() {
        int i = glGetError ();
        if (i != GL_NO_ERROR) {
            System.out.println("OpenGL error " + Integer.toString(i));
        }
    }
    
    public void processEntity(Entity entity) {
        RawModel entityModel = entity.getModel();
        List<Entity> modelEntities;
        
        if(entities.containsKey(entityModel)) {
            modelEntities = entities.get(entityModel);
            modelEntities.add(entity);
        } else {
            modelEntities = new ArrayList<>();
            modelEntities.add(entity);
            entities.put(entityModel, modelEntities);
        }
    }
    
    public void processSegmentEntity(RawEntity segmentEntity) {
        this.segmentEntity = segmentEntity;
    }

    public RawEntity getSegmentEntity() {
        return segmentEntity;
    }
    
    public void processMoveEntity(RawEntity moveEntity) {
        this.moveEntity = moveEntity;
    }

    public void processFloor(Floor floor) {
        this.floor = floor;
    }
    
    public void processCentrePoint(CenterPoint centrePoint) {
        this.centrePoint = centrePoint;
    }
    
    public void processPrintVolume(PrintVolume printVolume) {
        this.printVolumeEntity = printVolume.getRawEntity();
    }

    public void processLine(LineEntity lineEntity) {
        lineEntities.add(lineEntity);
    }
    
    public void clearEntities() {
        this.segmentEntity = null;
        this.moveEntity = null;
        entities.clear();
        lineEntities.clear();
    }
    
    public void cleanUp() {
        //staticShader.cleanUp();
        floorShader.cleanUp();
        lineModelShader.cleanUp();
        lineShader.cleanUp();
        segmentShader.cleanUp();
    }
    
    private void prepare() {
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(0.6f, 0.6f, 0.6f, 1);
    }
    
    /**
     * Generates the projection matrix for a perspective view.
     * 
     * @param windowWidth
     * @param windowHeight 
     */
    public final void createProjectionMatrix(float windowWidth, float windowHeight) {
        float aspectRatio = windowWidth / windowHeight;
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        projectionMatrix = new Matrix4f().zero();
        projectionMatrix.m00(x_scale);
        projectionMatrix.m11(y_scale);
        projectionMatrix.m22(-((FAR_PLANE + NEAR_PLANE) / frustum_length));
        projectionMatrix.m23(-1.0f);
        projectionMatrix.m32(-((2.0f * NEAR_PLANE * FAR_PLANE) / frustum_length));
        projectionMatrix.m33(0.0f);
    }
    
    /**
     * Re-loads the projection matrix into the renderer's respective shaders.
     */
    public final void reloadProjectionMatrix() {
//        staticEntityRenderer.loadProjectionMatrix(projectionMatrix);
        lineModelRenderer.loadProjectionMatrix(projectionMatrix);
        lineRenderer.setProjectionMatrix(projectionMatrix);
        segmentRenderer.setProjectionMatrix(projectionMatrix);
        angleRenderer.setProjectionMatrix(projectionMatrix);
        moveRenderer.setProjectionMatrix(projectionMatrix);
        floorRenderer.loadProjectionMatrix(projectionMatrix);
    }

    public final void prepareModelEntities(RawModel model) {
        
    }

    public final void prepareEntities() {
        entities.keySet().stream().forEach(model -> prepareModelEntities(model));
    }
    
    private void debugPV( Vector4f c, Camera camera) {
        DecimalFormat f = new DecimalFormat("0.00");
        Matrix4f viewMatrix = MatrixUtils.createViewMatrix(camera);

        Vector4f cv = new Vector4f(c);
        cv.mul(viewMatrix);
        Vector4f cvp = new Vector4f(cv);
        cvp.mul(projectionMatrix);
        Vector2f c2 = new Vector2f(cvp.x / cvp.w, cvp.y / cvp.w);
        System.out.println("(" +
                           f.format(c.x) +
                           ", " +
                           f.format(c.y) +
                           ", " +
                           f.format(c.z) +
                           ", " +
                           f.format(c.w) +
                           ") -> (" +
                           f.format(cv.x) +
                           ", " +
                           f.format(cv.y) +
                           ", " +
                           f.format(cv.z) +
                           ", " +
                           f.format(cv.w) +
                           ") -> (" +
                           f.format(cvp.x) +
                           ", " +
                           f.format(cvp.y) +
                           ", " +
                           f.format(cvp.z) +
                           ", " +
                           f.format(cvp.w) +
                           ") -> (" +
                           f.format(c2.x) +
                           ", " +
                           f.format(c2.y) +
                           ")");
    }
}


