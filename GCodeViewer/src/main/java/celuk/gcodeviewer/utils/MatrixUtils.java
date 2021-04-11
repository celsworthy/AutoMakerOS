package celuk.gcodeviewer.utils;

import celuk.gcodeviewer.entities.Camera;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class MatrixUtils {
    
    public static Matrix4f createTransformationMatrix(Vector3f translation, 
            float rx, float ry, float rz, float scale) {
        return createTransformationMatrix(translation, rx, ry, rz, scale, scale, scale);
    }
    
    public static Matrix4f createTransformationMatrix(Vector3f translation, 
            float rx, float ry, float rz,
            float sx, float sy, float sz) {
        Matrix4f matrix = new Matrix4f().identity();
        matrix.translate(translation);
        matrix.rotate((float) rx, new Vector3f(1.0f, 0.0f, 0.0f));
        matrix.rotate((float) ry, new Vector3f(0.0f, 1.0f, 0.0f));
        matrix.rotate((float) rz, new Vector3f(0.0f, 0.0f, 1.0f));
        matrix.scale(sx, sy, sz);
        
        return matrix;
    }
    
    public static Matrix4f createTransformationMatrixForLine(Vector3f translation, 
            float rx, float ry, float rz, float length) {
        Matrix4f matrix = new Matrix4f().identity();
        matrix.translate(translation);
        matrix.rotate((float) rx, new Vector3f(1.0f, 0.0f, 0.0f));
        matrix.rotate((float) ry, new Vector3f(0.0f, 1.0f, 0.0f));
        matrix.rotate((float) rz, new Vector3f(0.0f, 0.0f, 1.0f));
        matrix.scale(length, 1.0f, 1.0f);

        return matrix;
    }

    public static Matrix4f createViewMatrix(Camera camera) {
        Matrix4f viewMatrix = new Matrix4f().identity(); 
        
        viewMatrix.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1.0f, 0.0f, 0.0f));
        viewMatrix.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0.0f, 0.0f, 1.0f));
        
        Vector3f negativeCameraPos = new Vector3f(camera.getPosition()).negate();
        viewMatrix.translate(negativeCameraPos);

        // Transform to OpenGL coordinates which are left-handed, with Y as the up direction.
        Matrix4f mx = new Matrix4f().identity();
       // Mirror in X.
        mx.m00(-1.0f);
        // Swap Y and Z
        mx.m11(0.0f);
        mx.m12(1.0f);
        mx.m21(1.0f);
        mx.m22(0.0f);
        mx.mul(viewMatrix);
        
        return mx;
    }
}
