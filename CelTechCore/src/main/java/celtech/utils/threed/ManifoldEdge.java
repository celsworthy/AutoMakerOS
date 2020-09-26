/*
 * Copyright 2015 CEL UK
 */
package celtech.utils.threed;

import celtech.utils.threed.NonManifoldLoopDetector.Direction;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;


final class ManifoldEdge
{

    final int v0;
    final int v1;
    final Point3D point0;
    final Point3D point1;
    final int faceIndex;
    boolean visitedForwards = false;
    boolean visitedBackwards = false;

    public ManifoldEdge(int v0, int v1, Point3D point0, Point3D point1, int faceIndex)
    {
        this.v0 = v0;
        this.v1 = v1;
        this.point0 = point0;
        this.point1 = point1;
        this.faceIndex = faceIndex;
    }
    
    public boolean isVisited(Direction direction) {
        if (direction == Direction.FORWARDS) {
            return isVisitedForwards();
        } else {
            return isVisitedBackwards();
        }
    }
    
    public void setVisited(Direction direction) {
        if (direction == Direction.FORWARDS) {
            setVisitedForwards(true);
        } else {
            setVisitedBackwards(true);
        }
    }
    
    public Point2D getVectorForDirection(Direction direction) {
        double x = point1.getX() - point0.getX();
        double z = point1.getZ() - point0.getZ();
        if (direction == Direction.FORWARDS) {
            return new Point2D(x, z);
        } else {
            return new Point2D(-x, -z);
        }
    }

    public boolean isVisitedForwards()
    {
        return visitedForwards;
    }

    public void setVisitedForwards(boolean visitedForwards)
    {
        this.visitedForwards = visitedForwards;
    }

    public boolean isVisitedBackwards()
    {
        return visitedBackwards;
    }

    public void setVisitedBackwards(boolean visitedBackwards)
    {
        this.visitedBackwards = visitedBackwards;
    }

    @Override
    public String toString()
    {
        return "ManifoldEdge{faceIndex " + faceIndex + ",v0=" + v0 + ",v1=" + v1 + ", point0=" + point0 + ", point1=" + point1 + '}';
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof ManifoldEdge))
        {
            return false;
        }
        if (obj == this)
        {
            return true;
        }

        ManifoldEdge other = (ManifoldEdge) obj;
        if (other.faceIndex == faceIndex && 
            ((other.v0 == v0 && other.v1 == v1) || (other.v1 == v0 && other.v0 == v1)))
        {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        // hash code must be symmetrical in v0/v1 
        return v0 + v1;
    }
}


