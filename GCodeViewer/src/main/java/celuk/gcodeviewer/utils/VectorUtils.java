package celuk.gcodeviewer.utils;

import org.joml.Vector3f;

/**
 *
 * @author George Salter
 */
public class VectorUtils {
    
    public static Vector3f calculateCenterBetweenVectors(Vector3f from, Vector3f to) {
        return new Vector3f(to).sub(from)
                               .mul(0.5f)
                               .add(from);
    }
    
    public static float calculateLengthBetweenVectors(Vector3f v1, Vector3f v2) {
        return new Vector3f(v2).sub(v1)
                               .length();
    }
    
    public static float calculateRotationAroundYOfVectors(Vector3f from, Vector3f to) {
        Vector3f positionDiff = new Vector3f(to).sub(from);
        if (positionDiff.x == 0.0f && positionDiff.z == 0.0f) {
            return 0.0f;
        }
        float angle = positionDiff.angle(new Vector3f(0.0f, 0.0f, 1.0f)) - (float) Math.toRadians(90);
        if(from.x > to.x) {
            angle = -angle;
        }
        return angle;
    }
    
    public static float calculateRotationAroundZOfVectors(Vector3f from, Vector3f to) {
        Vector3f positionDiff = new Vector3f(to).sub(from);
        if(positionDiff.y == 0) {
            return 0;
        }
        float angle = positionDiff.angle(new Vector3f(0.0f, 1.0f, 0.0f)) - (float) Math.toRadians(90);
        if(from.y > to.y) {
            angle = -angle;
        }
        return angle;
    }
}
