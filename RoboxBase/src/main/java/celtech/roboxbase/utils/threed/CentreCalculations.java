package celtech.roboxbase.utils.threed;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 *
 * @author ianhudson
 */
public class CentreCalculations
{

    private boolean dataProvided = false;
    private double minX;
    private double minY;
    private double minZ;
    private double maxX;
    private double maxY;
    private double maxZ;

    public CentreCalculations()
    {
        reset();
    }

    public final void reset()
    {
        minX = 999;
        minY = 999;
        minZ = 999;
        maxX = 0;
        maxY = 0;
        maxZ = 0;

        dataProvided = false;
    }

    public Vector3D getResult()
    {
        if (dataProvided)
        {
            double width = maxX - minX;
            double depth = maxZ - minZ;
            double height = maxY - minY;

            double centreX = minX + (width / 2);
            double centreY = maxY - (height / 2);
            double centreZ = minZ + (depth / 2);

            return new Vector3D(centreX, centreY, centreZ);
        } else
        {
            return new Vector3D(0, 0, 0);
        }
    }

    public void processPoint(Vector3D point)
    {
        processPoint(point.getX(), point.getY(), point.getZ());
    }

    public void processPoint(double x, double y, double z)
    {
        minX = Math.min(x, minX);
        minY = Math.min(y, minY);
        minZ = Math.min(z, minZ);

        maxX = Math.max(x, maxX);
        maxY = Math.max(y, maxY);
        maxZ = Math.max(z, maxZ);

        dataProvided = true;
    }
}
