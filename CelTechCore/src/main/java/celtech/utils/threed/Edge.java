/*
 * Copyright 2015 CEL UK
 */
package celtech.utils.threed;


final class Edge
{

    final int v0;
    final int v1;

    public Edge(int v0, int v1)
    {
        this.v0 = v0;
        this.v1 = v1;
    }

    @Override
    public String toString()
    {
        return "Edge{" + "v0=" + v0 + ", v1=" + v1 + '}';
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof Edge))
        {
            return false;
        }
        if (obj == this)
        {
            return true;
        }

        Edge other = (Edge) obj;
        if ((other.v0 == v0 && other.v1 == v1) || (other.v1 == v0 && other.v0 == v1))
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

