package celuk.gcodeviewer.entities;

import celuk.gcodeviewer.engine.RawModel;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

/**
 *
 * @author George Salter
 */
public class CenterPoint{
    
    private final List<LineEntity> lineEntities = new ArrayList<>();
    
    private final Vector3f position;
    
    private final RawModel lineModel;
    
    private boolean rendered = false;
    
    public CenterPoint(Vector3f position, RawModel lineModel) {
        this.position = position;
        this.lineModel = lineModel;
        createEntities();
    }
    
    private void createEntities() {
        LineEntity xAxis = new LineEntity(lineModel, new Vector3f(-4, 0, 0), new Vector3f(4, 0, 0));
        LineEntity yAxis = new LineEntity(lineModel, new Vector3f(0, -4, 0), new Vector3f(0, 4, 0));
        LineEntity zAxis = new LineEntity(lineModel, new Vector3f(0, 0, -4), new Vector3f(0, 0, 4));
        
        xAxis.setColour(new Vector3f(0, 0, 0));
        yAxis.setColour(new Vector3f(0, 0, 0));
        zAxis.setColour(new Vector3f(0, 0, 0));
        
        lineEntities.add(xAxis);
        lineEntities.add(yAxis);
        lineEntities.add(zAxis);
    }
    
    public List<LineEntity> getLineEntities() {
        lineEntities.forEach(lineEntity -> lineEntity.setPosition(position));
        return lineEntities;
    }

    public Vector3f getPosition() {
        return position;
    }
    
    public boolean isRendered() {
        return this.rendered;
    }
    
    public void setRendered(boolean rendered) {
        this.rendered = rendered;
    }
}
