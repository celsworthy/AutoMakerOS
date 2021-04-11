package celuk.gcodeviewer.entities;

import celuk.gcodeviewer.engine.LineLoader;
import celuk.gcodeviewer.engine.RawEntity;
import celuk.gcodeviewer.engine.RawModel;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector3f;

/**
 *
 * @author George Salter
 */
public class PrintVolume {
    
    private final List<LineEntity> lineEntities = new ArrayList<>();
    
    private final RawModel lineModel = null;
    private final LineLoader lineLoader;
    private RawEntity rawEntity;
    
    private final float printVolumeWidth;
    private final float printVolumeHeight;
    private final float printVolumeDepth;
    private final float xOffset;
    private final float yOffset;
    private final float zOffset;
    
    public PrintVolume(LineLoader lineLoader, float printVolumeWidth, 
            float printVolumeDepth, float printVolumeHeight, 
            float xOffset, float yOffset, float zOffset) {
        this.lineLoader = lineLoader;
        this.printVolumeWidth = printVolumeWidth;
        this.printVolumeDepth = printVolumeDepth;
        this.printVolumeHeight = printVolumeHeight;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;

        createPrintVolumeLines();
    }
    
    private void createPrintVolumeLines() {
        LineEntity frontBottom = new LineEntity(lineModel,
                new Vector3f(xOffset, yOffset, zOffset),
                new Vector3f(xOffset + printVolumeWidth, yOffset, zOffset));
        LineEntity backBottom = new LineEntity(lineModel,
                new Vector3f(xOffset, yOffset + printVolumeDepth, zOffset),
                new Vector3f(xOffset + printVolumeWidth, yOffset + printVolumeDepth, zOffset));
        LineEntity leftBottom = new LineEntity(lineModel,
                new Vector3f(xOffset, yOffset, zOffset),
                new Vector3f(xOffset, yOffset + printVolumeDepth, zOffset));
        LineEntity rightBottom = new LineEntity(lineModel,
                new Vector3f(xOffset + printVolumeWidth, yOffset, zOffset),
                new Vector3f(xOffset + printVolumeWidth, yOffset + printVolumeDepth, zOffset));
        
        LineEntity frontTop = new LineEntity(lineModel,
                new Vector3f(xOffset, yOffset, zOffset + printVolumeHeight),
                new Vector3f(xOffset + printVolumeWidth, yOffset, zOffset + printVolumeHeight));
        LineEntity backTop = new LineEntity(lineModel,
                new Vector3f(xOffset, yOffset + printVolumeDepth, zOffset + printVolumeHeight),
                new Vector3f(xOffset + printVolumeWidth, yOffset + printVolumeDepth, zOffset + printVolumeHeight));
        LineEntity leftTop = new LineEntity(lineModel,
                new Vector3f(xOffset, yOffset, zOffset + printVolumeHeight),
                new Vector3f(xOffset, yOffset + printVolumeDepth, zOffset + printVolumeHeight));
        LineEntity rightTop = new LineEntity(lineModel,
                new Vector3f(xOffset + printVolumeWidth, yOffset, zOffset + printVolumeHeight),
                new Vector3f(xOffset + printVolumeWidth, yOffset + printVolumeDepth, zOffset + printVolumeHeight));
        
        LineEntity frontLeft = new LineEntity(lineModel,
                new Vector3f(xOffset, yOffset, zOffset),
                new Vector3f(xOffset, yOffset, zOffset + printVolumeHeight));
        LineEntity backLeft = new LineEntity(lineModel,
                new Vector3f(xOffset, yOffset + printVolumeDepth, zOffset),
                new Vector3f(xOffset, yOffset + printVolumeDepth, zOffset + printVolumeHeight));
        LineEntity frontRight = new LineEntity(lineModel,
                new Vector3f(xOffset + printVolumeWidth, yOffset, zOffset),
                new Vector3f(xOffset + printVolumeWidth, yOffset, zOffset + printVolumeHeight));
        LineEntity backRight = new LineEntity(lineModel,
                new Vector3f(xOffset + printVolumeWidth, yOffset + printVolumeDepth, zOffset),
                new Vector3f(xOffset + printVolumeWidth, yOffset + printVolumeDepth, zOffset + printVolumeHeight));
        
        lineEntities.add(frontBottom);
        lineEntities.add(backBottom);
        lineEntities.add(leftBottom);
        lineEntities.add(rightBottom);
        lineEntities.add(frontTop);
        lineEntities.add(backTop);
        lineEntities.add(leftTop);
        lineEntities.add(rightTop);
        lineEntities.add(frontLeft);
        lineEntities.add(backLeft);
        lineEntities.add(frontRight);
        lineEntities.add(backRight);
        lineEntities.forEach(lineEntity -> lineEntity.setColour(new Vector3f(0, 0, 0)));
        rawEntity = lineLoader.loadToVAO(lineEntities);
    }
    
    public List<LineEntity> getLineEntities() {
        return lineEntities;
    }
    
    public RawEntity getRawEntity() {
        return rawEntity;
    }
}
