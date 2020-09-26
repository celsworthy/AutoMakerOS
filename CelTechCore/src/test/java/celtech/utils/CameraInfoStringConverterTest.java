/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.utils;

import celtech.roboxbase.camera.CameraInfo;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author tonya
 */
public class CameraInfoStringConverterTest {
    @Test
    public void testStringConverter()
    {
        // creating a Stream of strings 
        List<CameraInfo> l = new ArrayList<>();
        CameraInfo ci = new CameraInfo();
        ci.setCameraName("Logitech C920");
        ci.setCameraNumber(0);
        ci.setServerIP("1.1.1.1");
        ci.setUdevName("/dev/video0");
        l.add(ci);
        ci = new CameraInfo();
        ci.setCameraName("Logitech C920");
        ci.setCameraNumber(1);
        ci.setServerIP("1.1.1.1");
        ci.setUdevName("/dev/video1");
        l.add(ci);
        ci = new CameraInfo();
        ci.setCameraName("Logitech StreamCam");
        ci.setCameraNumber(3);
        ci.setServerIP("1.1.1.1");
        ci.setUdevName("/dev/video3");
        l.add(ci);
        
        CameraInfoStringConverter cisc = new CameraInfoStringConverter(() -> { return l; });
        
        String cs0 = cisc.toString(l.get(0));
        CameraInfo ci1 = cisc.fromString("[1] Logitech C920");
        assertEquals(cs0, "[0] Logitech C920");
        assertEquals(ci1, l.get(1));
    }
}
