package celuk.gcodeviewer.entities;

import celuk.gcodeviewer.gui.GUIManager;
import java.nio.DoubleBuffer;
import org.lwjgl.BufferUtils;
import static org.lwjgl.glfw.GLFW.*;
import org.joml.Vector3f;

/**
 * 
 * @author George Salter
 */
public class Camera {

    private static final float MOUSE_CONTROL_SENSITIVITY = 5;
    private static final float MOUSE_ZOOM_SENSITIVITY = 4;
    private static final float MAXIMUM_CAMERA_PITCH = 89;
    private static final float MINIMUM_CAMERA_PITCH = -89;

    private final long window;
    
    private final CenterPoint centerPoint;
    
    private float distanceFromCenter = 300;
    private float angleAroundCenter = 180;
    
    private final Vector3f position = new Vector3f(0, 0, 0);
    private float pitch = -20;
    private float yaw = 0;
    private float roll = 0;
    
    private double previousXPosition = 0;
    private double previousYPosition = 0;
    private boolean dragging = false;
    
    private GUIManager guiManager;
    
    public Camera(long window, CenterPoint centerPoint, float distanceFromCenter, GUIManager guiManager) {
        this.window = window;
        this.centerPoint = centerPoint;
        this.distanceFromCenter = distanceFromCenter;
        this.guiManager = guiManager;
        setUpMovementCallbacks();
    }
    
    public void move() {
        float horizontalDistance = calculateHorizontalDifference();
        float verticalDistance = calculateVerticalDifference();
        calculateCameraPosition(horizontalDistance, verticalDistance);
        this.yaw = angleAroundCenter;
    }
    
    public void reset(Vector3f centerPosition, float distanceFromCenter) {
        this.distanceFromCenter = distanceFromCenter;
        this.angleAroundCenter = 180.0f;
        this.pitch = -20.0f;
        this.yaw = 0.0f;
        centerPoint.getPosition().set(centerPosition);
    }

    private float calculateHorizontalDifference() {
        return (float) (distanceFromCenter * Math.cos(Math.toRadians(pitch)));
    }
    
    private float calculateVerticalDifference() {
        return (float) (distanceFromCenter * Math.sin(Math.toRadians(pitch)));
    }
    
    private void calculateCameraPosition(float horizontalDistance, float verticalDistance) {
        float theta = angleAroundCenter;
        float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
        float offsetY = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
        position.x = centerPoint.getPosition().x + offsetX;
        position.y = centerPoint.getPosition().y + offsetY;
        position.z = centerPoint.getPosition().z - verticalDistance;
    }

    private float getSensitivityModifier() {
        float s = 1.0f;
        if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) != GLFW_RELEASE || 
            glfwGetKey(window, GLFW_KEY_RIGHT_SHIFT) != GLFW_RELEASE) {
            
                s = 5.0f;
        }
        return s;
    }

    private void setUpMovementCallbacks() {
        glfwSetScrollCallback(window, (window, xoffset, yoffset) -> {
            DoubleBuffer xposdb = BufferUtils.createDoubleBuffer(1);
            DoubleBuffer yposdb = BufferUtils.createDoubleBuffer(1);
            glfwGetCursorPos(window, xposdb, yposdb);
            double xpos = xposdb.get();
            double ypos = yposdb.get();
            //System.out.println("mouse[" + Integer.toString(mouseButton) + " = (" + Double.toString(xpos) + ", " + Double.toString(ypos) + ")");
            //System.out.println("action = " + (action == GLFW_PRESS ? "GLFW_PRESS" : "GLFW_RELEASE"));
            if (guiManager.overGuiPanel((int)xpos, (int)ypos))
                guiManager.onScroll(window, xoffset, yoffset);
            else {
                distanceFromCenter -= yoffset * getSensitivityModifier() * MOUSE_ZOOM_SENSITIVITY;
            }
            guiManager.setRenderRequired();
        });
        
        glfwSetMouseButtonCallback(window, (window, mouseButton, action, mods) -> {
            DoubleBuffer xposdb = BufferUtils.createDoubleBuffer(1);
            DoubleBuffer yposdb = BufferUtils.createDoubleBuffer(1);
            glfwGetCursorPos(window, xposdb, yposdb);
            double xpos = xposdb.get();
            double ypos = yposdb.get();
            //System.out.println("mouse[" + Integer.toString(mouseButton) + " = (" + Double.toString(xpos) + ", " + Double.toString(ypos) + ")");
            //System.out.println("action = " + (action == GLFW_PRESS ? "GLFW_PRESS" : "GLFW_RELEASE"));
            if (!dragging)
                guiManager.onMouseButton(window, xpos, ypos, mouseButton, action, mods);
            
            if (!guiManager.overGuiPanel((int)xpos, (int)ypos))
            {
                if((mouseButton == GLFW_MOUSE_BUTTON_1 ||
                    mouseButton == GLFW_MOUSE_BUTTON_2) &&
                   action == GLFW_PRESS) {
                    dragging = true;
                    previousXPosition = xpos;
                    previousYPosition = ypos;
                    centerPoint.setRendered(true);
                    guiManager.setRenderRequired();
                }
                if((mouseButton == GLFW_MOUSE_BUTTON_1 ||
                    mouseButton == GLFW_MOUSE_BUTTON_2) &&
                    action == GLFW_RELEASE) {
                    dragging = false;
                    centerPoint.setRendered(false);
                    guiManager.setRenderRequired();
                }
            }
        });
        
        glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
            guiManager.onCursorPos(window, xpos, ypos);                    
            if (dragging) {
                double xPositionDiff = previousXPosition - xpos;
                double yPositionDiff = previousYPosition - ypos;
                float sensitivity = MOUSE_CONTROL_SENSITIVITY / getSensitivityModifier();

                boolean controlPressed = glfwGetKey(window, GLFW_KEY_LEFT_CONTROL) != GLFW_RELEASE || 
                                             glfwGetKey(window, GLFW_KEY_RIGHT_CONTROL) != GLFW_RELEASE ||
                                             glfwGetKey(window, GLFW_KEY_LEFT_SUPER) != GLFW_RELEASE || 
                                             glfwGetKey(window, GLFW_KEY_RIGHT_SUPER) != GLFW_RELEASE;
                boolean altPressed = glfwGetKey(window, GLFW_KEY_LEFT_ALT) != GLFW_RELEASE || 
                                         glfwGetKey(window, GLFW_KEY_RIGHT_ALT) !=GLFW_RELEASE;
                boolean mousePressed = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) != GLFW_RELEASE ||
                                            glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_2) != GLFW_RELEASE;
                
                if(mousePressed && !controlPressed && !altPressed) {
                    // Spin around control point.
                    angleAroundCenter -= xPositionDiff / sensitivity;
                    pitch += yPositionDiff / sensitivity;
                    if(pitch >= MAXIMUM_CAMERA_PITCH) {
                        pitch = MAXIMUM_CAMERA_PITCH;
                    }
                    if (pitch <= MINIMUM_CAMERA_PITCH) {
                        pitch = MINIMUM_CAMERA_PITCH;
                    }
                }

                if(mousePressed && controlPressed && !altPressed) {
                   // Pan.
                   Vector3f viewVector = calculateNormalisedViewVector();
                   Vector3f leftRightVect = new Vector3f(viewVector.y, -viewVector.x, 0);
                   Vector3f upDownVect = new Vector3f(leftRightVect).cross(viewVector);

                   leftRightVect.normalize();
                   upDownVect.normalize();

                   // Deal with left right pan
                   centerPoint.getPosition().x -= (leftRightVect.x * xPositionDiff) / sensitivity;
                   centerPoint.getPosition().y -= (leftRightVect.y * xPositionDiff) / sensitivity;

                   // Deal with up down pan
                   centerPoint.getPosition().x -= (upDownVect.x * yPositionDiff) / sensitivity;
                   centerPoint.getPosition().y -= (upDownVect.y * yPositionDiff) / sensitivity;
                   centerPoint.getPosition().z -= (upDownVect.z * yPositionDiff) / sensitivity;
                }

                if(mousePressed && altPressed && !controlPressed) {
                    // Zoom.
                    distanceFromCenter -= yPositionDiff / sensitivity;
                }

                previousXPosition = xpos;
                previousYPosition = ypos;
                guiManager.setRenderRequired();
            }
        });
    }
    
    private Vector3f calculateNormalisedViewVector() {
        Vector3f viewVector = new Vector3f(position).sub(centerPoint.getPosition());
        viewVector.normalize();
        return viewVector;
    }
    
    public float getDistanceFromCenter() {
        return distanceFromCenter;
    }
    
    public Vector3f getCenterPosition() {
        return centerPoint.getPosition();
    }
    
    public Vector3f getPosition() {
        return position;
    }
    
    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }
}
