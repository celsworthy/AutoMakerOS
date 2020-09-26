/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.coreUI.components;

import java.util.List;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class ScalingGroup extends Group
{

    private Stenographer steno = StenographerFactory.getStenographer(ScalingGroup.class.getName());
    private Translate translate = new Translate(0, 0, 0);
    private Scale scale = new Scale(1, 1, 1, 0, 0, 0);
    private double twoSize = 2;

    /**
     *
     */
    public ScalingGroup()
    {
        steno.debug("Creating a scaling group");
    }

    @Override
    protected void layoutChildren()
    {
        List<Node> children = getChildren();
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, minZ = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE, maxZ = Double.MIN_VALUE;
        boolean first = true;
        for (int i = 0, max = children.size(); i < max; i++)
        {
            final Node node = children.get(i);
            if (node.isVisible())
            {
                Bounds bounds = node.getBoundsInLocal();
                // if the bounds of the child are invalid, we don't want
                // to use those in the remaining computations.
                if (bounds.isEmpty())
                {
                    continue;
                }
                if (first)
                {
                    minX = bounds.getMinX();
                    minY = bounds.getMinY();
                    minZ = bounds.getMinZ();
                    maxX = bounds.getMaxX();
                    maxY = bounds.getMaxY();
                    maxZ = bounds.getMaxZ();
                    first = false;
                } else
                {
                    minX = Math.min(bounds.getMinX(), minX);
                    minY = Math.min(bounds.getMinY(), minY);
                    minZ = Math.min(bounds.getMinZ(), minZ);
                    maxX = Math.max(bounds.getMaxX(), maxX);
                    maxY = Math.max(bounds.getMaxY(), maxY);
                    maxZ = Math.max(bounds.getMaxZ(), maxZ);
                }
            }
        }

        final double w = maxX - minX;
        final double h = maxY - minY;
        final double d = maxZ - minZ;

        final double centerX = minX + (w / 2);
        final double centerY = minY + (h / 2);
        final double centerZ = minZ + (d / 2);

        double scaleX = twoSize / w;
        double scaleY = twoSize / h;
        double scaleZ = twoSize / d;

        double scale = Math.min(scaleX, Math.min(scaleY, scaleZ));
        this.scale.setX(scale);
        this.scale.setY(scale);
        this.scale.setZ(scale);

        this.translate.setX(-centerX);
        this.translate.setY(-centerY);

    }
}
