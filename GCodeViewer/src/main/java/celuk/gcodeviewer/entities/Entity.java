package celuk.gcodeviewer.entities;

import celuk.gcodeviewer.engine.RawModel;
import org.joml.Vector3f;

public class Entity {

public final static int N_DATA_VALUES = 6;
    public final static int DATA_A = 0;
    public final static int DATA_B = 1;
    public final static int DATA_C = 2;
    public final static int DATA_D = 3;
    public final static int DATA_E = 4;
    public final static int DATA_F = 5;
    public final static int NULL_LAYER = -9999;
    
    private RawModel model;
    private String type;
    private Vector3f position;
    private Vector3f direction;
    private Vector3f normal;
    private float length;
    private float width;
    private float thickness;
    private final float dataValues[];
    
    private Vector3f colour = new Vector3f(1, 1, 1);
    private Vector3f typeColour = new Vector3f(1, 1, 1);

    private int layer;
    private int lineNumber;
    private int toolNumber;
    private int typeIndex;
    private boolean isMoveFlag;

    public Entity(RawModel model, Vector3f position, Vector3f direction, Vector3f normal,
            float length, float width, float thickness,
            int layer, int lineNumber, int toolNumber, boolean isMoveFlag, float dataValues[]) {
        this.model = model;
        this.type = "";
        this.position = position;
        this.direction = direction;
        this.normal = normal;
        this.length = length;
        this.width = width;
        this.thickness = thickness;
        this.layer = layer;
        this.lineNumber = lineNumber;
        this.toolNumber = toolNumber;
        this.typeIndex = -1;
        this.isMoveFlag = isMoveFlag;
        this.dataValues = new float[N_DATA_VALUES];
        for (int i = 0; i < N_DATA_VALUES; ++i) {
            if (dataValues != null && i < dataValues.length)
                this.dataValues[i] = dataValues[i];
            else
                this.dataValues[i] = 0.0f;
        }
    }
    
    public RawModel getModel() {
        return model;
    }

    public void setModel(RawModel model) {
        this.model = model;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    public Vector3f getNormal() {
        return normal;
    }

    public void setNormal(Vector3f normal) {
        this.normal = normal;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getThickness() {
        return thickness;
    }

    public void setThickness(float thickness) {
        this.width = thickness;
    }

    public Vector3f getColour() {
        return colour;
    }

    public void setColour(Vector3f colour) {
        this.colour = colour;
    }
    
    public Vector3f getTypeColour() {
        return typeColour;
    }

    public void setTypeColour(Vector3f colour) {
        this.typeColour = colour;
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getToolNumber() {
        return toolNumber;
    }

    public void setToolNumber(int toolNumber) {
        this.toolNumber = toolNumber;
    }

    public int getTypeIndex() {
        return typeIndex;
    }

    public void setTypeIndex(int typeIndex) {
        this.typeIndex = typeIndex;
    }

    public boolean isMove() {
        return isMoveFlag;
    }

    public void setIsMove(boolean isMoveFlag) {
        this.isMoveFlag = isMoveFlag;
    }

    public float getDataValue(int i) {
        return ((i >= 0 && i < N_DATA_VALUES) ? dataValues[i] : 0.0f);
    }

    public void setDataValue(int i, float value) {
        if (i >= 0 && i < N_DATA_VALUES)
            dataValues[i] = value;
    }

    public void setDataValues(float dataValues[]) {
        for (int i = 0; i < N_DATA_VALUES; ++i) {
            if (dataValues != null && i < dataValues.length)
                this.dataValues[i] = dataValues[i];
            else
                this.dataValues[i] = 0.0f;
        }
    }
}
