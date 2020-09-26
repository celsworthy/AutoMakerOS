/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.roboxbase.utils.Math;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 *
 */
public class PolarCoordinate
{

    private double theta; //Elevation in radians
    private double phi; //Azimuth in radians
    private double radius;

    /**
     *
     * @param theta
     * @param phi
     * @param radius
     */
    public PolarCoordinate(double theta, double phi, double radius)
    {
        this.theta = theta;
        this.phi = phi;
        this.radius = radius;
    }

    /**
     *
     * @return
     */
    public double getTheta()
    {
        return theta;
    }

    /**
     *
     * @param theta
     */
    public void setTheta(double theta)
    {
        this.theta = theta;
    }

    /**
     *
     * @param thetaInDegrees
     */
    public void setThetaDegrees(double thetaInDegrees)
    {
        this.theta = Math.toRadians(thetaInDegrees);
    }

    /**
     *
     * @return
     */
    public double getPhi()
    {
        return phi;
    }

    /**
     *
     * @param phi
     */
    public void setPhi(double phi)
    {
        this.phi = phi;
    }

    /**
     *
     * @param phiInDegrees
     */
    public void setPhiDegrees(double phiInDegrees)
    {
        this.phi = Math.toRadians(phiInDegrees);
    }

    /**
     *
     * @return
     */
    public double getRadius()
    {
        return radius;
    }

    /**
     *
     * @param radius
     */
    public void setRadius(double radius)
    {
        this.radius = radius;
    }

    /**
     *
     * @return
     */
    public String toString()
    {
        return "Theta:" + getTheta() + " Phi:" + getPhi() + " Radius:" + getRadius();
    }

}
