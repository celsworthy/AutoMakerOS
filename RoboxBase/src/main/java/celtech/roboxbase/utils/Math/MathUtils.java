package celtech.roboxbase.utils.Math;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.geometry.euclidean.twod.Segment;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class MathUtils
{

    /**
     *
     */
    public static Point3D xAxis = new Point3D(1, 0, 0);

    /**
     *
     */
    public static Point3D yAxis = new Point3D(0, -1, 0);

    /**
     *
     */
    public static Point3D zAxis = new Point3D(0, 0, 1);

    /**
     *
     */
    public static final double HALF_PI = Math.PI / 2;

    /**
     *
     */
    public static final double TWO_PI = Math.PI * 2;

    /**
     *
     */
    public static final double RAD_TO_DEG = 180 / Math.PI;

    /**
     *
     */
    public static final double DEG_TO_RAD = Math.PI / 180;

    /**
     *
     * @param x
     * @return
     */
    public static double invSqrt(double x)
    {
        double xhalf = 0.5d * x;
        long i = Double.doubleToLongBits(x);
        i = 0x5fe6ec85e7de30daL - (i >> 1);
        x = Double.longBitsToDouble(i);
        x = x * (1.5d - xhalf * x * x);
        return x;
    }

    /**
     *
     * @param n
     * @param alf
     * @param bet
     * @param gam
     */
    public static void matrixRotateNode(Node n, double alf, double bet, double gam)
    {
        double A11 = Math.cos(alf) * Math.cos(gam);
        double A12 = Math.cos(bet) * Math.sin(alf) + Math.cos(alf) * Math.sin(bet) * Math.sin(gam);
        double A13 = Math.sin(alf) * Math.sin(bet) - Math.cos(alf) * Math.cos(bet) * Math.sin(gam);
        double A21 = -Math.cos(gam) * Math.sin(alf);
        double A22 = Math.cos(alf) * Math.cos(bet) - Math.sin(alf) * Math.sin(bet) * Math.sin(gam);
        double A23 = Math.cos(alf) * Math.sin(bet) + Math.cos(bet) * Math.sin(alf) * Math.sin(gam);
        double A31 = Math.sin(gam);
        double A32 = -Math.cos(gam) * Math.sin(bet);
        double A33 = Math.cos(bet) * Math.cos(gam);

        double d = Math.acos((A11 + A22 + A33 - 1d) / 2d);
        if (d != 0d)
        {
            double den = 2d * Math.sin(d);
            Point3D p = new Point3D((A32 - A23) / den, (A13 - A31) / den, (A21 - A12) / den);
            n.setRotationAxis(p);
            n.setRotate(Math.toDegrees(d));
        }
    }

    /**
     *
     * @param polarCoordinate
     * @return
     */
    public static Point3D sphericalToCartesianLocalSpaceAdjusted(PolarCoordinate polarCoordinate)
    {
        // This method converts from spherical to cartesian coords with the following special assumptions
        // 1- the cartesian coordinate system is as per JavaFX - x->right, z->away (into screen), y->down
        // 2- an azimuth of 0 represents looking from the front - ie straight down the z axis
        // 3- the output is referenced to 0,0,0

        // Theta - angle from reference plane (elevation) - 0-PI
        // Phi - angle from x axis - counter clockwise - 0-2PI
        // x=r cos(theta) cos(phi)
        // y=r cos(theta) sin(phi)
        // z=r sin(theta)
        // Adjust for javafx axes - x->right, y->down, z->up
        // x=r cos(theta) cos(phi)
        // z=r cos(theta) sin(phi)
        // y=- r sin(theta)
        // 270 is front view - need to adjust so that an input of 0 give this value
        double azimuthRadians = polarCoordinate.getPhi();

        azimuthRadians -= HALF_PI;
        azimuthRadians = boundAzimuthRadians(azimuthRadians);

        double newX = polarCoordinate.getRadius() * Math.cos(polarCoordinate.getTheta()) * Math.cos(azimuthRadians);
        double newZ = polarCoordinate.getRadius() * Math.cos(polarCoordinate.getTheta()) * Math.sin(azimuthRadians);
        double newY = -polarCoordinate.getRadius() * Math.sin(polarCoordinate.getTheta());

        return new Point3D(newX, newY, newZ);
    }

    /**
     *
     * @param cartesianCoordinate
     * @return
     */
    public static PolarCoordinate cartesianToSphericalLocalSpaceAdjusted(Point3D cartesianCoordinate)
    {
        // Convert from cartesian to spherical
        // r= sqrt(x^2 + y^2 + z^2)
        // theta = arcsin(z/r)
        // phi = arctan(y/x)
        //
        //Adjust for javafx coords
        // theta = arcsin(-y / r)
        // phi = arctan(z / x)
        double r = Math.sqrt((cartesianCoordinate.getX() * cartesianCoordinate.getX())
                + (cartesianCoordinate.getY() * cartesianCoordinate.getY())
                + (cartesianCoordinate.getZ() * cartesianCoordinate.getZ()));
        double theta = Math.asin(-cartesianCoordinate.getY() / r);
        double phi = Math.atan2(cartesianCoordinate.getZ(), cartesianCoordinate.getX());

        //Adjust for the 0 deg azimuth = front view
        phi += HALF_PI;
        phi = boundAzimuthRadians(phi);

        double thetaDeg = Math.toDegrees(theta);
        double phiDeg = Math.toDegrees(phi);

        return new PolarCoordinate(theta, phi, r);
    }

    /**
     *
     * @param polarCoordinate
     * @return
     */
    public static Point3D sphericalToCartesianLocalSpaceUnadjusted(PolarCoordinate polarCoordinate)
    {
        // This method converts from spherical to cartesian coords with the following special assumptions
        // 1- the cartesian coordinate system is as per JavaFX - x->right, z->away (into screen), y->down
        // 2- an azimuth of 0 represents looking from the front - ie straight down the z axis
        // 3- the output is referenced to 0,0,0

        // Theta - angle from reference plane (elevation) - 0-PI
        // Phi - angle from x axis - counter clockwise - 0-2PI
        // x=r cos(theta) cos(phi)
        // y=r cos(theta) sin(phi)
        // z=r sin(theta)
        // Adjust for javafx axes - x->right, y->down, z->up
        // x=r cos(theta) cos(phi)
        // z=r cos(theta) sin(phi)
        // y=- r sin(theta)
        // 270 is front view - need to adjust so that an input of 0 give this value
        double azimuthRadians = polarCoordinate.getPhi();

        azimuthRadians = boundAzimuthRadians(azimuthRadians);

        double newX = polarCoordinate.getRadius() * Math.cos(polarCoordinate.getTheta()) * Math.cos(azimuthRadians);
        double newZ = polarCoordinate.getRadius() * Math.cos(polarCoordinate.getTheta()) * Math.sin(azimuthRadians);
        double newY = -polarCoordinate.getRadius() * Math.sin(polarCoordinate.getTheta());

        return new Point3D(newX, newY, newZ);
    }

    /**
     *
     * @param cartesianCoordinate
     * @return
     */
    public static PolarCoordinate cartesianToSphericalLocalSpaceUnadjusted(Point3D cartesianCoordinate)
    {
        // Convert from cartesian to spherical
        // r= sqrt(x^2 + y^2 + z^2)
        // theta = arcsin(z/r)
        // phi = arctan(y/x)
        //
        //Adjust for javafx coords
        // theta = arcsin(-y / r)
        // phi = arctan(z / x)
        double r = Math.sqrt((cartesianCoordinate.getX() * cartesianCoordinate.getX())
                + (cartesianCoordinate.getY() * cartesianCoordinate.getY())
                + (cartesianCoordinate.getZ() * cartesianCoordinate.getZ()));
        double theta = Math.asin(-cartesianCoordinate.getY() / r);
        double phi = Math.atan2(cartesianCoordinate.getZ(), cartesianCoordinate.getX());

        phi = boundAzimuthRadians(phi);

        if (r == 0)
        {
            theta = 0;
            phi = 0;
        }
        double thetaDeg = Math.toDegrees(theta);
        double phiDeg = Math.toDegrees(phi);

        return new PolarCoordinate(theta, phi, r);
    }

    /**
     *
     * @param xPos
     * @param yPos
     * @return
     */
    public static double cartesianToAngleDegreesCWFromTop(double xPos, double yPos)
    {
        // Returns an angle assuming the input is relative to a zero centre point
        // Rotation is clockwise
        double angle = Math.atan2(xPos, yPos);
        angle = boundAzimuthRadians(angle);
        return angle * RAD_TO_DEG;
    }

    /**
     *
     * @param angle
     * @param radius
     * @return
     */
    public static Point2D angleDegreesToCartesianCWFromTop(double angle, double radius)
    {
        // Returns an angle assuming the input is relative to a zero centre point
        // Rotation is clockwise

        double angleToProcess = (DEG_TO_RAD * angle);
        double xPos = Math.sin(angleToProcess) * radius;
        double yPos = Math.cos(angleToProcess) * radius;

        return new Point2D(xPos, yPos);
    }

    /**
     *
     * @param angle
     * @param radius
     * @return
     */
    public static Point2D angleDegreesToCartesianCWFromTop(double angle, double radius, boolean flipY)
    {
        // Returns an angle assuming the input is relative to a zero centre point
        // Rotation is clockwise

        double angleToProcess = DEG_TO_RAD * angle;
        double xPos = Math.sin(angleToProcess) * radius;
        double yPos = Math.cos(angleToProcess) * radius;

        if (flipY)
        {
            yPos = -yPos;
        }

        return new Point2D(xPos, yPos);
    }

    /**
     *
     * @param xPos
     * @param yPos
     * @return
     */
    public static double cartesianToAngleDegreesCCWFromRight(double xPos, double yPos)
    {
        // Returns an angle assuming the input is relative to a zero centre point
        // Rotation is clockwise
        double angle = Math.atan2(xPos, yPos);
        angle -= HALF_PI;
        angle = boundAzimuthRadians(angle);
        return angle * RAD_TO_DEG;
    }

    /**
     *
     * @param azimuth
     * @return
     */
    public static double boundAzimuthRadians(double azimuth)
    {
        double outputAzimuth = 0;
        if (azimuth > TWO_PI)
        {
            outputAzimuth = azimuth - TWO_PI;
        } else if (azimuth < 0)
        {
            outputAzimuth = azimuth + TWO_PI;
        } else
        {
            outputAzimuth = azimuth;
        }

        return outputAzimuth;
    }

    /**
     *
     * @param azimuth
     * @return
     */
    public static double boundAzimuthDegrees(double azimuth)
    {
        double outputAzimuth = 0;
        if (azimuth > 360)
        {
            outputAzimuth = azimuth - 360;
        } else if (azimuth < 0)
        {
            outputAzimuth = azimuth + 360;
        } else
        {
            outputAzimuth = azimuth;
        }

        return outputAzimuth;
    }

    public static Segment getOrthogonalLineToLinePoints(double orthogonalLength, Vector2D startPoint, Vector2D endPoint)
    {
        Vector2D midPoint = findMidPoint(startPoint, endPoint);

        Vector2D originalVector = endPoint.subtract(startPoint);

        Vector2D normalisedOrthogonal = (new Vector2D(-originalVector.getY(), originalVector.getX())).normalize();
        Vector2D scaledOrthogonal = normalisedOrthogonal.scalarMultiply(orthogonalLength);

        Vector2D newStartPoint = midPoint.add(scaledOrthogonal);
        Vector2D newEndPoint = midPoint.subtract(scaledOrthogonal);

        Line resultantLine = new Line(newStartPoint, newEndPoint, 1e-12);

        return new Segment(newStartPoint, newEndPoint, resultantLine);
    }

    public static Vector2D findMidPoint(Vector2D startPoint, Vector2D endPoint)
    {
        double halfXDifference = (Math.max(startPoint.getX(), endPoint.getX()) - Math.min(startPoint.getX(), endPoint.getX())) / 2;
        double halfYDifference = (Math.max(startPoint.getY(), endPoint.getY()) - Math.min(startPoint.getY(), endPoint.getY())) / 2;
        double midX = Math.min(startPoint.getX(), endPoint.getX()) + halfXDifference;
        double midY = Math.min(startPoint.getY(), endPoint.getY()) + halfYDifference;

        Vector2D midPoint = new Vector2D(midX, midY);

        return midPoint;
    }

    public static Vector2D getSegmentIntersection(Segment firstSegment, Segment secondSegment)
    {
        Vector2D intersectionPoint = firstSegment.getLine().intersection(secondSegment.getLine());

        if (intersectionPoint != null)
        {
            boolean withinFirstSegment = doesPointLieWithinSegment(intersectionPoint, firstSegment);
            boolean withinSecondSegment = doesPointLieWithinSegment(intersectionPoint, secondSegment);

            if (!withinFirstSegment || !withinSecondSegment)
            {
                intersectionPoint = null;
            }
        }
        return intersectionPoint;
    }

    public static boolean doesPointLieWithinSegment(Vector2D pointToTest, Segment segmentToTest)
    {
        boolean pointWithinSegment = false;

        int pointXMinTest = compareDouble(pointToTest.getX(), Math.min(segmentToTest.getStart().getX(), segmentToTest.getEnd().getX()), 1e-10);
        int pointXMaxTest = compareDouble(pointToTest.getX(), Math.max(segmentToTest.getStart().getX(), segmentToTest.getEnd().getX()), 1e-10);
        int pointYMinTest = compareDouble(pointToTest.getY(), Math.min(segmentToTest.getStart().getY(), segmentToTest.getEnd().getY()), 1e-10);
        int pointYMaxTest = compareDouble(pointToTest.getY(), Math.max(segmentToTest.getStart().getY(), segmentToTest.getEnd().getY()), 1e-10);

        if ((pointXMinTest == EQUAL || pointXMinTest == MORE_THAN)
                && (pointXMaxTest == EQUAL || pointXMaxTest == LESS_THAN)
                && (pointYMinTest == EQUAL || pointYMinTest == MORE_THAN)
                && (pointYMaxTest == EQUAL || pointYMaxTest == LESS_THAN))
        {
            pointWithinSegment = true;
        }

        return pointWithinSegment;
    }

    public static final int EQUAL = 0;
    public static final int MORE_THAN = 1;
    public static final int LESS_THAN = -1;

    public static int compareDouble(double a, double b, double epsilon)
    {
        double result = a - b;

        if (Math.abs(result) < epsilon)
        {
            return EQUAL;
        } else if (result > 0)
        {
            return MORE_THAN;
        } else
        {
            return LESS_THAN;
        }
    }

    public static int compareFloat(float a, float b, float epsilon)
    {
        float result = a - b;

        if (Math.abs(result) <= epsilon)
        {
            return EQUAL;
        } else if (result > 0)
        {
            return MORE_THAN;
        } else
        {
            return LESS_THAN;
        }
    }
}
