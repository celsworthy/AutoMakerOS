/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.utils;

import celtech.roboxbase.camera.CameraInfo;
import celtech.roboxbase.configuration.fileRepresentation.CameraProfile;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author tonya
 */
public class CameraProfileStringConverterTest {
        @Test
    public void testStringConverter()
    {
        // creating a Stream of strings 
        List<CameraProfile> l = new ArrayList<>();
        CameraProfile cp = new CameraProfile();
        cp.setAmbientLightOff(true);
        cp.setCaptureHeight(1080);
        cp.setCaptureWidth(1920);
        cp.setHeadLightOff(true);
        cp.setMoveBeforeCapture(false);
        cp.setMoveToX(0);
        cp.setMoveToY(0);
        cp.setMoveBeforeCapture(false);
        cp.setProfileName("Default");
        l.add(cp);
        cp = new CameraProfile();
        cp.setAmbientLightOff(true);
        cp.setCaptureHeight(1080);
        cp.setCaptureWidth(1920);
        cp.setHeadLightOff(true);
        cp.setMoveBeforeCapture(false);
        cp.setMoveToX(0);
        cp.setMoveToY(0);
        cp.setCameraName("Default");
        cp.setProfileName("Logitech 920");
        l.add(cp);
        cp = new CameraProfile();
        cp.setAmbientLightOff(true);
        cp.setCaptureHeight(1080);
        cp.setCaptureWidth(1920);
        cp.setHeadLightOff(true);
        cp.setCameraName("Default");
        cp.setProfileName("Logitech StreamCam");
        l.add(cp);
        
        CameraProfileStringConverter cisc = new CameraProfileStringConverter(() -> { return l; });
        
        String cs0 = cisc.toString(l.get(0));
        CameraProfile cp1 = cisc.fromString("Logitech 920");
        assertEquals(cs0, "Default");
        assertEquals(cp1, l.get(1));
    }
}
