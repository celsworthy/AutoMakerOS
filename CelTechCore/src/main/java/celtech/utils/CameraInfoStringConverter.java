/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.utils;

import celtech.roboxbase.camera.CameraInfo;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.util.StringConverter;

/**
 *
 * @author tonya
 */
public class CameraInfoStringConverter extends StringConverter<CameraInfo> 
{    
    static final Pattern r = Pattern.compile("\\[(\\d+)\\] (.*)");
    Supplier<List<CameraInfo>> listSupplier;

    public CameraInfoStringConverter(Supplier<List<CameraInfo>> ls)
    {
        listSupplier = ls;
    }

    @Override
    public String toString(CameraInfo camera) 
    {
        if (camera == null)
        {
            return "";
        }
        return "[" + camera.getCameraNumber() + "] " + camera.getCameraName();
    }

    @Override
    public CameraInfo fromString(String string) 
    {
        CameraInfo camera = null;
        Matcher m = r.matcher(string);
        if (m.find())
        {
            int cameraNumber = Integer.parseInt(m.group(1));
            String cameraName = m.group(2).trim();
            Optional<CameraInfo> cOpt = listSupplier.get()
                .stream()
                .filter(c -> (c.getCameraName().equals(cameraName) && c.getCameraNumber() == cameraNumber))
                .findFirst();
            // Unwrap optional as return type is "bare".
            if (cOpt.isPresent())
                camera = cOpt.get();
        }
        return camera;
    }
}
