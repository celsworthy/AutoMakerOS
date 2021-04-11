package celuk.gcodeviewer.entities;

import org.joml.Vector3f;

public class Light {
    
    private final Vector3f position;
    private final Vector3f colour;

    public Light(Vector3f position, Vector3f colour) {
        this.position = position;
        this.colour = colour;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getColour() {
        return colour;
    }
}
