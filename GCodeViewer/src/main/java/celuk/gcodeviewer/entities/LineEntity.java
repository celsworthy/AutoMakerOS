package celuk.gcodeviewer.entities;

import celuk.gcodeviewer.engine.RawModel;
import celuk.gcodeviewer.utils.VectorUtils;
import org.joml.Vector3f;

/**
 *
 * @author George Salter
 */
public class LineEntity {
    
    private RawModel lineModel;
    
    private Vector3f start;
    private Vector3f end;
    
    private Vector3f position;
    
    private float rotationX;
    private float rotationY;
    private float rotationZ;
    
    private float length;
    
    private Vector3f colour = new Vector3f(1, 1, 1);
    
    public LineEntity(RawModel lineModel, Vector3f start, Vector3f end) {
        this.lineModel = lineModel;
        this.start = start;
        this.end = end;
        position = calculatePosition();
        rotationX = 0;
        rotationY = calculateRotationAroundY();
        rotationZ = caculateRotationAroundZ();
        length = calculateLength();
    }
    
    private Vector3f calculatePosition() {
        return VectorUtils.calculateCenterBetweenVectors(start, end);
    }
    
    private float calculateRotationAroundY() {
        return VectorUtils.calculateRotationAroundYOfVectors(start, end);
    }
    
    private float caculateRotationAroundZ() {
        return VectorUtils.calculateRotationAroundZOfVectors(start, end);
    }
    
    private float calculateLength() {
        return VectorUtils.calculateLengthBetweenVectors(start, end);
    }

    public RawModel getLineModel() {
        return lineModel;
    }

    public void setLineModel(RawModel lineModel) {
        this.lineModel = lineModel;
    }

    public Vector3f getStart() {
        return start;
    }

    public void setStart(Vector3f start) {
        this.start = start;
    }

    public Vector3f getEnd() {
        return end;
    }

    public void setEnd(Vector3f end) {
        this.end = end;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public float getRotationX() {
        return rotationX;
    }

    public void setRotationX(float rotationX) {
        this.rotationX = rotationX;
    }

    public float getRotationY() {
        return rotationY;
    }

    public void setRotationY(float rotationY) {
        this.rotationY = rotationY;
    }

    public float getRotationZ() {
        return rotationZ;
    }

    public void setRotationZ(float rotationZ) {
        this.rotationZ = rotationZ;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public Vector3f getColour() {
        return colour;
    }

    public void setColour(Vector3f colour) {
        this.colour = colour;
    }
}
