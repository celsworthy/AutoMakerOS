package celtech.roboxbase.utils;

/**
 *
 * @author ianhudson
 */
public class RectangularBounds
{

    double minX = 0;
    double maxX = 0;
    double minY = 0;
    double maxY = 0;
    double minZ = 0;
    double maxZ = 0;

    double width = 0;
    double height = 0;
    double depth = 0;

    double centreX = 0;
    double centreY = 0;
    double centreZ = 0;
    


    /**
     *
     */
    public RectangularBounds()
    {
    }

    public RectangularBounds(double minX,
        double maxX,
        double minY,
        double maxY,
        double minZ,
        double maxZ,
        double width,
        double height,
        double depth,
        double centreX,
        double centreY,
        double centreZ)
    {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.minZ = minZ;
        this.maxZ = maxZ;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.centreX = centreX;
        this.centreY = centreY;
        this.centreZ = centreZ;
    }

    public double getMinX()
    {
        return minX;
    }

    public void setMinX(double minX)
    {
        this.minX = minX;
    }

    public double getMaxX()
    {
        return maxX;
    }

    public void setMaxX(double maxX)
    {
        this.maxX = maxX;
    }

    public double getMinY()
    {
        return minY;
    }

    public void setMinY(double minY)
    {
        this.minY = minY;
    }

    public double getMaxY()
    {
        return maxY;
    }

    public void setMaxY(double maxY)
    {
        this.maxY = maxY;
    }

    public double getMinZ()
    {
        return minZ;
    }

    public void setMinZ(double minZ)
    {
        this.minZ = minZ;
    }

    public double getMaxZ()
    {
        return maxZ;
    }

    public void setMaxZ(double maxZ)
    {
        this.maxZ = maxZ;
    }

    public double getWidth()
    {
        return width;
    }

    public void setWidth(double width)
    {
        this.width = width;
    }

    public double getHeight()
    {
        return height;
    }

    public void setHeight(double height)
    {
        this.height = height;
    }

    public double getDepth()
    {
        return depth;
    }

    public void setDepth(double depth)
    {
        this.depth = depth;
    }

    public double getCentreX()
    {
        return centreX;
    }

    public void setCentreX(double centreX)
    {
        this.centreX = centreX;
    }

    public double getCentreY()
    {
        return centreY;
    }

    public void setCentreY(double centreY)
    {
        this.centreY = centreY;
    }

    public double getCentreZ()
    {
        return centreZ;
    }

    public void setCentreZ(double centreZ)
    {
        this.centreZ = centreZ;
    }

    @Override
    public String toString()
    {
        return String.format("Model bounds {MinX: %.2f "
            + " MaxX: %.2f \n"
            + " MinY: %.2f "
            + " MaxY: %.2f \n"
            + " MinZ: %.2f "
            + " MaxZ: %.2f \n"
            + " W: %.2f "
            + " H: %.2f "
            + " D: %.2f \n"
            + " Centre X: %.2f"
            + " Y: %.2f"
            + " Z: %.2f }", minX, maxX, minY, maxY, minZ, maxZ, width, height, depth, centreX,
                             centreY, centreZ);
    }

    public void translateX(double deltaCentreX)
    {
        minX += deltaCentreX;
        maxX += deltaCentreX;
        centreX += deltaCentreX;
    }

    public void translateY(double deltaCentreY)
    {
        minY += deltaCentreY;
        maxY += deltaCentreY;
        centreY += deltaCentreY;
    }

    public void translateZ(double deltaCentreZ)
    {
        minZ += deltaCentreZ;
        maxZ += deltaCentreZ;
        centreZ += deltaCentreZ;
    }

}
