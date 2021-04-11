/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celuk.gcodeviewer.engine;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joml.Vector3f;

/**
 *
 * @author Tony
 */
public class PrintVolumeDetails {
        
    @JsonIgnore
    private Vector3f dimensions;
    @JsonIgnore
    private Vector3f offset;
    @JsonIgnore
    private float defaultCameraDistance;

    public PrintVolumeDetails() {
        dimensions = new Vector3f(100.0f, 100.0f, 100.0f);
        offset = new Vector3f(0.0f, 0.0f, 0.0f);;
        defaultCameraDistance = 0.0f;
    }

    public PrintVolumeDetails(Vector3f d, Vector3f o) {
        dimensions = d;
        offset = o;
    }

    @JsonProperty
    public Vector3f getDimensions() {
        return dimensions;
    }

    @JsonProperty
    public void setDimensions(Vector3f d) {
        dimensions = d;
    }

    @JsonProperty
    public Vector3f getOffset() {
        return offset;
    }

    @JsonProperty
    public void setOffset(Vector3f o) {
        offset = o;
    }

    @JsonProperty
    public float getDefaultCameraDistance() {
        return defaultCameraDistance;
    }

    @JsonProperty
    public void setDefaultCameraDistance(float d) {
        defaultCameraDistance = d;
    }
}
