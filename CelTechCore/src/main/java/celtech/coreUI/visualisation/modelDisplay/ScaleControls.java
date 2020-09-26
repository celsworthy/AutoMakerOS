package celtech.coreUI.visualisation.modelDisplay;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.util.Duration;

/**
 *
 * @author Ian
 */
public class ScaleControls
{

    private final Group parentGroup;

    // Scale handles
    private final PhongMaterial scaleBoxMaterial = new PhongMaterial(Color.YELLOW);
    private final double scaleBoxSize = 1.5;
    private final double scaleBoxGrowth = 1;
    private Node selectedScaleBox = null;
    private final Box scaleBoxTop = new Box(scaleBoxSize, scaleBoxSize, scaleBoxSize);
    private final Box scaleZBoxFront = new Box(scaleBoxSize, scaleBoxSize, scaleBoxSize);
    private final Box scaleXBoxLeft = new Box(scaleBoxSize, scaleBoxSize, scaleBoxSize);
    private final Box scaleXBoxRight = new Box(scaleBoxSize, scaleBoxSize, scaleBoxSize);
    private final Box scaleZBoxBack = new Box(scaleBoxSize, scaleBoxSize, scaleBoxSize);
    private final Box scaleXZBoxLeftFront = new Box(scaleBoxSize, scaleBoxSize, scaleBoxSize);
    private final Box scaleXZBoxRightFront = new Box(scaleBoxSize, scaleBoxSize, scaleBoxSize);
    private final Box scaleXZBoxLeftBack = new Box(scaleBoxSize, scaleBoxSize, scaleBoxSize);
    private final Box scaleXZBoxRightBack = new Box(scaleBoxSize, scaleBoxSize, scaleBoxSize);
    private final Duration scaleGrowShrinkDuration = Duration.millis(500);
    private Animation pulsateScaleBox = new Transition()
    {
        {
            setCycleDuration(scaleGrowShrinkDuration);
            setAutoReverse(true);
        }

        @Override
        public void interpolate(double frac)
        {
            changeScale(selectedScaleBox, frac * scaleBoxGrowth + 1);
        }
    };

    private Animation growScaleBox = new Transition()
    {
        {
            setCycleDuration(scaleGrowShrinkDuration);
            setAutoReverse(false);
        }

        @Override
        public void interpolate(double frac)
        {
            changeScale(selectedScaleBox, frac * scaleBoxGrowth + 1);
        }
    };

    private Animation shrinkScaleBox = new Transition()
    {
        {
            setCycleDuration(scaleGrowShrinkDuration);
            setAutoReverse(false);
        }

        @Override
        public void interpolate(double frac)
        {
            changeScale(selectedScaleBox, (1.0 - frac) * scaleBoxGrowth + 1);
        }
    };

    private final EventHandler<MouseEvent> scaleEventHandler = (MouseEvent event) ->
    {
        Node selectedNode = (Node) event.getSource();

        if (event.getEventType() == MouseEvent.MOUSE_PRESSED)
        {
//            if (growScaleBox.getStatus() == Animation.Status.RUNNING)
//            {
//                growScaleBox.pause();
//                Duration elapsedTime = growScaleBox.getCurrentTime();
//                growScaleBox.stop();
//                pulsateScaleBox.playFrom(scaleGrowShrinkDuration.subtract(elapsedTime));
//            } else
//            {
//                pulsateScaleBox.playFromStart();
//            }
        } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED)
        {
//            if (selectedScaleBox != null)
//            {
//                if (pulsateScaleBox.getStatus() == Animation.Status.RUNNING)
//                {
//                    pulsateScaleBox.pause();
//                    Duration elapsedTime = pulsateScaleBox.getCurrentTime();
//                    pulsateScaleBox.stop();
//                    shrinkScaleBox.playFrom(scaleGrowShrinkDuration.subtract(elapsedTime));
//                } else
//                {
//                    shrinkScaleBox.playFromStart();
//                }
//            }
        } else if (event.getEventType() == MouseEvent.MOUSE_ENTERED)
        {
            if (selectedScaleBox == null)
            {
                selectedScaleBox = selectedNode;

                pulsateScaleBox.stop();
                if (shrinkScaleBox.getStatus() == Animation.Status.RUNNING)
                {
                    shrinkScaleBox.pause();
                    Duration elapsedTime = shrinkScaleBox.getCurrentTime();
                    shrinkScaleBox.stop();
                    growScaleBox.playFrom(scaleGrowShrinkDuration.subtract(elapsedTime));
                } else
                {
                    growScaleBox.playFromStart();
                }
            }
        } else if (event.getEventType() == MouseEvent.MOUSE_EXITED)
        {
            if (selectedScaleBox != null)
            {
                pulsateScaleBox.stop();
                if (growScaleBox.getStatus() == Animation.Status.RUNNING)
                {
                    growScaleBox.pause();
                    Duration elapsedTime = growScaleBox.getCurrentTime();
                    growScaleBox.stop();
                    shrinkScaleBox.playFrom(scaleGrowShrinkDuration.subtract(elapsedTime));
                } else
                {
                    shrinkScaleBox.playFromStart();
                }
            }
        }
    };

    public ScaleControls(Group parentGroup)
    {
        this.parentGroup = parentGroup;

        attachScalingHandles();

        shrinkScaleBox.setOnFinished((ActionEvent event) ->
        {
            selectedScaleBox = null;
        });
    }

    private void changeScale(Node nodeToScale, double amount)
    {
        nodeToScale.setScaleX(amount);
        nodeToScale.setScaleY(amount);
        nodeToScale.setScaleZ(amount);
    }

    private void attachScalingHandles()
    {
        scaleBoxTop.setMaterial(scaleBoxMaterial);
        scaleBoxTop.addEventHandler(MouseEvent.ANY, scaleEventHandler);

        scaleZBoxBack.setMaterial(scaleBoxMaterial);
        scaleZBoxBack.addEventHandler(MouseEvent.ANY, scaleEventHandler);

        scaleZBoxFront.setMaterial(scaleBoxMaterial);
        scaleZBoxFront.addEventHandler(MouseEvent.ANY, scaleEventHandler);

        scaleXBoxLeft.setMaterial(scaleBoxMaterial);
        scaleXBoxLeft.addEventHandler(MouseEvent.ANY, scaleEventHandler);

        scaleXBoxRight.setMaterial(scaleBoxMaterial);
        scaleXBoxRight.addEventHandler(MouseEvent.ANY, scaleEventHandler);

        scaleXZBoxLeftBack.setMaterial(scaleBoxMaterial);
        scaleXZBoxLeftBack.addEventHandler(MouseEvent.ANY, scaleEventHandler);

        scaleXZBoxLeftFront.setMaterial(scaleBoxMaterial);
        scaleXZBoxLeftFront.addEventHandler(MouseEvent.ANY, scaleEventHandler);

        scaleXZBoxRightBack.setMaterial(scaleBoxMaterial);
        scaleXZBoxRightBack.addEventHandler(MouseEvent.ANY, scaleEventHandler);

        scaleXZBoxRightFront.setMaterial(scaleBoxMaterial);
        scaleXZBoxRightFront.addEventHandler(MouseEvent.ANY, scaleEventHandler);

        parentGroup.getChildren().addAll(scaleBoxTop,
                                         scaleZBoxFront,
                                         scaleXBoxLeft,
                                         scaleXBoxRight,
                                         scaleZBoxBack,
                                         scaleXZBoxLeftBack,
                                         scaleXZBoxLeftFront,
                                         scaleXZBoxRightBack,
                                         scaleXZBoxRightFront);
    }

    void place(double minX, double maxX, double minY, double maxY, double minZ, double maxZ)
    {
        double halfWidth = (maxX - minX) / 2;
        double halfHeight = (minY - maxY) / 2;
        double halfDepth = (maxZ - minZ) / 2;
        scaleBoxTop.setTranslateY(minY);
        scaleBoxTop.setTranslateX(minX + halfWidth);
        scaleBoxTop.setTranslateZ(minZ + halfDepth);

        //Bottom edge scale handles
        scaleZBoxFront.setTranslateY(maxY - scaleBoxSize / 2);
        scaleZBoxFront.setTranslateX(minX + halfWidth);
        scaleZBoxFront.setTranslateZ(minZ);

        scaleZBoxBack.setTranslateY(maxY - scaleBoxSize / 2);
        scaleZBoxBack.setTranslateX(minX + halfWidth);
        scaleZBoxBack.setTranslateZ(maxZ);

        scaleXBoxLeft.setTranslateY(maxY - scaleBoxSize / 2);
        scaleXBoxLeft.setTranslateX(minX);
        scaleXBoxLeft.setTranslateZ(minZ + halfDepth);

        scaleXBoxRight.setTranslateY(maxY - scaleBoxSize / 2);
        scaleXBoxRight.setTranslateX(maxX);
        scaleXBoxRight.setTranslateZ(minZ + halfDepth);

        //Corner XZ scale handles
        scaleXZBoxLeftBack.setTranslateY(maxY - scaleBoxSize / 2);
        scaleXZBoxLeftBack.setTranslateX(minX);
        scaleXZBoxLeftBack.setTranslateZ(maxZ);

        scaleXZBoxLeftFront.setTranslateY(maxY - scaleBoxSize / 2);
        scaleXZBoxLeftFront.setTranslateX(minX);
        scaleXZBoxLeftFront.setTranslateZ(minZ);

        scaleXZBoxRightBack.setTranslateY(maxY - scaleBoxSize / 2);
        scaleXZBoxRightBack.setTranslateX(maxX);
        scaleXZBoxRightBack.setTranslateZ(maxZ);

        scaleXZBoxRightFront.setTranslateY(maxY - scaleBoxSize / 2);
        scaleXZBoxRightFront.setTranslateX(maxX);
        scaleXZBoxRightFront.setTranslateZ(minZ);
    }

}
