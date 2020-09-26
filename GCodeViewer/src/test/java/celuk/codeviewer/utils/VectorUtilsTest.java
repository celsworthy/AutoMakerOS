/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celuk.codeviewer.utils;

import celuk.gcodeviewer.utils.VectorUtils;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.joml.Vector3f;

/**
 *
 * @author Tony
 */
public class VectorUtilsTest {
    @Test
    public void testVectorUtils() {
        Vector3f v1 = new Vector3f(1.0f, 2.0f, 3.0f);
        Vector3f v2 = new Vector3f(5.0f, -2.0f, 3.0f);
        
        Vector3f v3 = VectorUtils.calculateCenterBetweenVectors(v1, v2);
        assertEquals(v3, new Vector3f(3.0f, 0.0f, 3.0f));
 
        float l = VectorUtils.calculateLengthBetweenVectors(v1, v2);
        assertEquals(l, 5.656854f, 0.0005f);
        
        float a = VectorUtils.calculateRotationAroundYOfVectors(v1, v2);
        assertEquals(a, 0.0f, 0.0005f);

        Vector3f v4 = new Vector3f(2.0f, 1.0f, 8.0f);
        a = VectorUtils.calculateRotationAroundYOfVectors(v1, v4);
        assertEquals(a, -1.2951536f, 0.0005f);

         a = VectorUtils.calculateRotationAroundZOfVectors(v1, v2);
        assertEquals(a, -0.7853981f, 0.0005f);

        a = VectorUtils.calculateRotationAroundZOfVectors(v1, v4);
        assertEquals(a, -0.19365823f, 0.0005f);
    }
}
