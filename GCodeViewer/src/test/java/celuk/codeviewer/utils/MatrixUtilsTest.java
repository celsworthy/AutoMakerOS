/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celuk.codeviewer.utils;

import celuk.gcodeviewer.entities.Camera;
import celuk.gcodeviewer.utils.MatrixUtils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Tony
 */
public class MatrixUtilsTest {
    @Test
    public void testMatrixUtils() {


        Matrix4f m1 = MatrixUtils.createTransformationMatrix(new Vector3f(1.0f, 2.0f, 3.0f), 2.0f, 1.0f, 3.0f, 2.0f); 
        assertEquals(m1.m00(), -1.0697904f, 0.0005f);	
        assertEquals(m1.m01(), -1.6324337f, 0.0005f);
        assertEquals(m1.m02(), -0.43670204f, 0.0005f);
        assertEquals(m1.m03(), 0.0f, 0.0005f);
        assertEquals(m1.m10(), -0.15249492f, 0.0005f);
        assertEquals(m1.m11(), 0.60800934f, 0.0005f);
        assertEquals(m1.m13(), 0.0f, 0.0005f);
        assertEquals(m1.m20(), 1.6829419f, 0.0005f);
        assertEquals(m1.m21(), -0.9825909f, 0.0005f);
        assertEquals(m1.m22(), -0.44969016f, 0.0005f);
        assertEquals(m1.m23(), 0.0f, 0.0005f);
        assertEquals(m1.m30(), 1.0f, 0.0005f);
        assertEquals(m1.m31(), 2.0f, 0.0005f);
        assertEquals(m1.m32(), 3.0f, 0.0005f);
        assertEquals(m1.m33(), 1.0f, 0.0005f);

        Matrix4f m2 = MatrixUtils.createTransformationMatrix(new Vector3f(6.0f, 3.0f, 5.0f), 1.0f, 0.5f, 0.2f, 2.0f, 3.0f, 4.0f);
        assertEquals(m2.m00(), 1.7201787f, 0.0005f);
        assertEquals(m2.m01(), 1.0054451f, 0.0005f);
        assertEquals(m2.m02(), -0.17339364f, 0.0005f);
        assertEquals(m2.m03(), 0.0f, 0.0005f);
        assertEquals(m2.m10(), -0.52304626f, 0.0005f);
        assertEquals(m2.m11(), 1.3481535f, 0.0005f);
        assertEquals(m2.m12(), 2.6284795f, 0.0005f);
        assertEquals(m2.m13(), 0.0f, 0.0005f);
        assertEquals(m2.m20(), 1.9177022f, 0.0005f);
        assertEquals(m2.m21(), -2.953841f, 0.0005f);
        assertEquals(m2.m22(), 1.8966393f, 0.0005f);
        assertEquals(m2.m23(), 0.0f, 0.0005f);
        assertEquals(m2.m30(), 6.0f, 0.0005f);
        assertEquals(m2.m31(), 3.0f, 0.0005f);
        assertEquals(m2.m32(), 5.0f, 0.0005f);
        assertEquals(m2.m33(), 1.0f, 0.0005f);

        Matrix4f m3 = MatrixUtils.createTransformationMatrixForLine(new Vector3f(1.0f, 2.0f, 3.0f), 0.0f, 0.0f, 0.0f, 2.0f);
        assertEquals(m3.m00(), 2.0f, 0.0005f);
        assertEquals(m3.m01(), 0.0f, 0.0005f);
        assertEquals(m3.m02(), 0.0f, 0.0005f);
        assertEquals(m3.m03(), 0.0f, 0.0005f);
        assertEquals(m3.m10(), 0.0f, 0.0005f);
        assertEquals(m3.m11(), 1.0f, 0.0005f);
        assertEquals(m3.m12(), 0.0f, 0.0005f);
        assertEquals(m3.m13(), 0.0f, 0.0005f);
        assertEquals(m3.m20(), 0.0f, 0.0005f);
        assertEquals(m3.m21(), 0.0f, 0.0005f);
        assertEquals(m3.m22(), 1.0f, 0.0005f);
        assertEquals(m3.m23(), 0.0f, 0.0005f);
        assertEquals(m3.m30(), 1.0f, 0.0005f);
        assertEquals(m3.m31(), 2.0f, 0.0005f);
        assertEquals(m3.m32(), 3.0f, 0.0005f);
        assertEquals(m3.m33(), 1.0f, 0.0005f);

        //Camera = new Camera();
        //Matrix4f m4 =  MatrixUtils.createViewMatrix(camera);
        assert(true);
    }
}
