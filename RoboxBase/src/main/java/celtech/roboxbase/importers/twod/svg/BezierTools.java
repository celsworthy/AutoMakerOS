package celtech.roboxbase.importers.twod.svg;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 *
 * @author ianhudson
 */
public class BezierTools
{

    private final int SEGMENTS_PER_CURVE = 10;
    private final float MINIMUM_SQR_DISTANCE = 0.01f;

    // This corresponds to about 172 degrees, 8 degrees from a traight line
    private final float DIVISION_THRESHOLD = -0.99f;

    private List<Vector2D> controlPoints;

    private int curveCount; //how many bezier curves in this path?

    /**
     * Constructs a new empty Bezier curve. Use one of these methods to add
     * points: SetControlPoints, Interpolate, SamplePoints.
     */
    public BezierTools()
    {
        controlPoints = new ArrayList<Vector2D>();
    }

    /**
     * Sets the control points of this Bezier path. Points 0-3 forms the first
     * Bezier curve, points 3-6 forms the second curve, etc.
     */
    public void SetControlPoints(List<Vector2D> newControlPoints)
    {
        controlPoints.clear();
        controlPoints.addAll(newControlPoints);
        curveCount = (controlPoints.size() - 1) / 3;
    }

    /**
     * Returns the control points for this Bezier curve.
     */
    public List<Vector2D> GetControlPoints()
    {
        return controlPoints;
    }

    /**
     * Calculates a Bezier interpolated path for the given points.
     */
//    public void Interpolate(List<Vector3> segmentPoints, float scale)
//    {
//        controlPoints.Clear();
//
//        if (segmentPoints.Count < 2)
//        {
//            return;
//        }
//
//        for (int i = 0; i < segmentPoints.Count; i++)
//        {
//            if (i == 0) // is first
//            {
//                Vector3 p1 = segmentPoints[i];
//                Vector3 p2 = segmentPoints[i + 1];                
//
//                Vector3 tangent = (p2 - p1);
//                Vector3 q1 = p1 + scale * tangent;
//
//                controlPoints.Add(p1);
//                controlPoints.Add(q1);
//            }
//            else if (i == segmentPoints.Count - 1) //last
//            {
//                Vector3 p0 = segmentPoints[i - 1];
//                Vector3 p1 = segmentPoints[i];
//                Vector3 tangent = (p1 - p0);
//                Vector3 q0 = p1 - scale * tangent;
//
//                controlPoints.Add(q0);
//                controlPoints.Add(p1);
//            }
//            else
//            {
//                Vector3 p0 = segmentPoints[i - 1];
//                Vector3 p1 = segmentPoints[i];
//                Vector3 p2 = segmentPoints[i + 1];
//                Vector3 tangent = (p2 - p0).normalized;
//                Vector3 q0 = p1 - scale * tangent * (p1 - p0).magnitude;
//                Vector3 q1 = p1 + scale * tangent * (p2 - p1).magnitude;
//
//                controlPoints.Add(q0);
//                controlPoints.Add(p1);
//                controlPoints.Add(q1);
//            }
//        }
//
//        curveCount = (controlPoints.Count - 1) / 3;
//    }   
    /**
     * Sample the given points as a Bezier path.
     */
//    public void SamplePoints(List<Vector2D> sourcePoints, float minSqrDistance, float maxSqrDistance, float scale)
//    {
//        if(sourcePoints.Count < 2)
//        {
//            return;
//        }
//
//        Stack<Vector3> samplePoints = new Stack<Vector3>();
//        
//        samplePoints.Push(sourcePoints[0]);
//        
//        Vector3 potentialSamplePoint = sourcePoints[1];
//
//        int i = 2;
//
//        for (i = 2; i < sourcePoints.Count; i++ )
//        {
//            if(
//                ((potentialSamplePoint - sourcePoints[i]).sqrMagnitude > minSqrDistance) &&
//                ((samplePoints.Peek() - sourcePoints[i]).sqrMagnitude > maxSqrDistance))
//            {
//                samplePoints.Push(potentialSamplePoint);
//            }
//
//            potentialSamplePoint = sourcePoints[i];
//        }
//
//        //now handle last bit of curve
//        Vector3 p1 = samplePoints.Pop(); //last sample point
//        Vector3 p0 = samplePoints.Peek(); //second last sample point
//        Vector3 tangent = (p0 - potentialSamplePoint).normalized;
//        float d2 = (potentialSamplePoint - p1).magnitude;
//        float d1 = (p1 - p0).magnitude;
//        p1 = p1 + tangent * ((d1 - d2)/2);
//
//        samplePoints.Push(p1);
//        samplePoints.Push(potentialSamplePoint);
//
//
//        Interpolate(new List<Vector3>(samplePoints), scale);
//    }
    /**
     * Calculates a point on the path.
     *
     * @param curveIndex The index of the curve that the point is on. For
     * example, the second curve (index 1) is the curve with controlpoints 3, 4,
     * 5, and 6.
     *
     * @param t The paramater indicating where on the curve the point is. 0
     * corresponds to the "left" point, 1 corresponds to the "right" end point.
     */
    public Vector2D CalculateBezierPoint(int curveIndex, float t)
    {
        int nodeIndex = curveIndex * 3;

        Vector2D p0 = controlPoints.get(nodeIndex);
        Vector2D p1 = controlPoints.get(nodeIndex + 1);
        Vector2D p2 = controlPoints.get(nodeIndex + 2);
        Vector2D p3 = controlPoints.get(nodeIndex + 3);

        return CalculateBezierPoint(t, p0, p1, p2, p3);
    }

    /**
     * Gets the drawing points. This implementation simply calculates a certain
     * number of points per curve.
     */
    public List<Vector2D> GetDrawingPoints0()
    {
        List<Vector2D> drawingPoints = new ArrayList<Vector2D>();

        for (int curveIndex = 0; curveIndex < curveCount; curveIndex++)
        {
            if (curveIndex == 0) //Only do this for the first end point. 
            //When i != 0, this coincides with the 
            //end point of the previous segment,
            {
                drawingPoints.add(CalculateBezierPoint(curveIndex, 0));
            }

            for (int j = 1; j <= SEGMENTS_PER_CURVE; j++)
            {
                float t = j / (float) SEGMENTS_PER_CURVE;
                drawingPoints.add(CalculateBezierPoint(curveIndex, t));
            }
        }

        return drawingPoints;
    }

    /**
     * Gets the drawing points. This implementation simply calculates a certain
     * number of points per curve.
     *
     * This is a lsightly different inplementation from the one above.
     */
    public List<Vector2D> GetDrawingPoints1()
    {
        List<Vector2D> drawingPoints = new ArrayList<Vector2D>();

        for (int i = 0; i < controlPoints.size() - 3; i += 3)
        {
            Vector2D p0 = controlPoints.get(i);
            Vector2D p1 = controlPoints.get(i + 1);
            Vector2D p2 = controlPoints.get(i + 2);
            Vector2D p3 = controlPoints.get(i + 3);

            if (i == 0) //only do this for the first end point. When i != 0, this coincides with the end point of the previous segment,
            {
                drawingPoints.add(CalculateBezierPoint(0, p0, p1, p2, p3));
            }

            for (int j = 1; j <= SEGMENTS_PER_CURVE; j++)
            {
                float t = j / (float) SEGMENTS_PER_CURVE;
                drawingPoints.add(CalculateBezierPoint(t, p0, p1, p2, p3));
            }
        }

        return drawingPoints;
    }

    /**
     * This gets the drawing points of a bezier curve, using recursive division,
     * which results in less points for the same accuracy as the above
     * implementation.
     */
    public List<Vector2D> GetDrawingPoints2()
    {
        List<Vector2D> drawingPoints = new ArrayList<>();

        for (int curveIndex = 0; curveIndex < curveCount; curveIndex++)
        {
            List<Vector2D> bezierCurveDrawingPoints = FindDrawingPoints(curveIndex);

            if (curveIndex != 0)
            {
                //remove the fist point, as it coincides with the last point of the previous Bezier curve.
                bezierCurveDrawingPoints.remove(0);
            }

            drawingPoints.addAll(bezierCurveDrawingPoints);
        }

        return drawingPoints;
    }

    List<Vector2D> FindDrawingPoints(int curveIndex)
    {
        List<Vector2D> pointList = new ArrayList<Vector2D>();

        Vector2D left = CalculateBezierPoint(curveIndex, 0);
        Vector2D right = CalculateBezierPoint(curveIndex, 1);

        pointList.add(left);
        pointList.add(right);

        FindDrawingPoints(curveIndex, 0, 1, pointList, 1);

        return pointList;
    }

    /**
     * @returns the number of points added.
     */
    int FindDrawingPoints(int curveIndex, float t0, float t1,
            List<Vector2D> pointList, int insertionIndex)
    {
        Vector2D left = CalculateBezierPoint(curveIndex, t0);
        Vector2D right = CalculateBezierPoint(curveIndex, t1);

        Vector2D resultant = left.subtract(right);
        double magnitude_squared = (resultant.getX() * resultant.getX()) + (resultant.getY() * resultant.getY());
        if (magnitude_squared < MINIMUM_SQR_DISTANCE)
        {
            return 0;
        }

        float tMid = (t0 + t1) / 2;
        Vector2D mid = CalculateBezierPoint(curveIndex, tMid);

        Vector2D leftDirection = left.subtract(mid).normalize();
        Vector2D rightDirection = right.subtract(mid).normalize();
        
        if (leftDirection.dotProduct(rightDirection) > DIVISION_THRESHOLD || Math.abs(tMid - 0.5f) < 0.0001f)
        {
            int pointsAddedCount = 0;

            pointsAddedCount += FindDrawingPoints(curveIndex, t0, tMid, pointList, insertionIndex);
            pointList.add(insertionIndex + pointsAddedCount, mid);
            pointsAddedCount++;
            pointsAddedCount += FindDrawingPoints(curveIndex, tMid, t1, pointList, insertionIndex + pointsAddedCount);

            return pointsAddedCount;
        }

        return 0;
    }

    /**
     * Caluclates a point on the Bezier curve represented with the four
     * controlpoints given.
     */
    private Vector2D CalculateBezierPoint(float t, Vector2D p0, Vector2D p1, Vector2D p2, Vector2D p3)
    {
         
        double a = Math.pow((1.0 - t), 3.0);
        double b = 3.0 * t * Math.pow((1.0 - t), 2.0);
        double c = 3.0 * Math.pow(t, 2.0) * (1.0 - t);
        double d = Math.pow(t, 3.0);
 
        double x = a * p0.getX() + b * p1.getX() + c * p2.getX() + d * p3.getX();
        double y = a * p0.getY() + b * p1.getY() + c * p2.getY() + d * p3.getY();
        
//        
//        float u = 1 - t;
//        float tt = t * t;
//        float uu = u * u;
//        float uuu = uu * u;
//        float ttt = tt * t;
//
//        Vector2D p = p0.scalarMultiply(uuu); //first term
//
//        p.add(p1.scalarMultiply(3 * uu * t));
//        p.add(p2.scalarMultiply(3 * u * tt));
//        p.add(p3.scalarMultiply(ttt));

//        p += 3 * uu * t * p1; //second term
//        p += 3 * u * tt * p2; //third term
//        p += ttt * p3; //fourth term
        return new Vector2D(x, y);

    }
}

// //------------------------------------------------------------------------
//    void curve4_div::init(double x1, double y1, 
//                          double x2, double y2, 
//                          double x3, double y3,
//                          double x4, double y4)
//    {
//        m_points.remove_all();
//        m_distance_tolerance = 0.5 / m_approximation_scale;
//        m_distance_tolerance *= m_distance_tolerance;
//        bezier(x1, y1, x2, y2, x3, y3, x4, y4);
//        m_count = 0;
//    }
//
//    //------------------------------------------------------------------------
//    void curve4_div::recursive_bezier(double x1, double y1, 
//                                      double x2, double y2, 
//                                      double x3, double y3, 
//                                      double x4, double y4,
//                                      unsigned level)
//    {
//        if(level > curve_recursion_limit) 
//        {
//            return;
//        }
//
//        // Calculate all the mid-points of the line segments
//        //----------------------
//        double x12   = (x1 + x2) / 2;
//        double y12   = (y1 + y2) / 2;
//        double x23   = (x2 + x3) / 2;
//        double y23   = (y2 + y3) / 2;
//        double x34   = (x3 + x4) / 2;
//        double y34   = (y3 + y4) / 2;
//        double x123  = (x12 + x23) / 2;
//        double y123  = (y12 + y23) / 2;
//        double x234  = (x23 + x34) / 2;
//        double y234  = (y23 + y34) / 2;
//        double x1234 = (x123 + x234) / 2;
//        double y1234 = (y123 + y234) / 2;
//
//        if(level > 0) // Enforce subdivision first time
//        {
//            // Try to approximate the full cubic curve by a single straight line
//            //------------------
//            double dx = x4-x1;
//            double dy = y4-y1;
//
//            double d2 = fabs(((x2 - x4) * dy - (y2 - y4) * dx));
//            double d3 = fabs(((x3 - x4) * dy - (y3 - y4) * dx));
//
//            double da1, da2;
//
//            if(d2 > curve_collinearity_epsilon && d3 > curve_collinearity_epsilon)
//            { 
//                // Regular care
//                //-----------------
//                if((d2 + d3)*(d2 + d3) <= m_distance_tolerance * (dx*dx + dy*dy))
//                {
//                    // If the curvature doesn't exceed the distance_tolerance value
//                    // we tend to finish subdivisions.
//                    //----------------------
//                    if(m_angle_tolerance < curve_angle_tolerance_epsilon)
//                    {
//                        m_points.add(point_type(x1234, y1234));
//                        return;
//                    }
//
//                    // Angle & Cusp Condition
//                    //----------------------
//                    double a23 = atan2(y3 - y2, x3 - x2);
//                    da1 = fabs(a23 - atan2(y2 - y1, x2 - x1));
//                    da2 = fabs(atan2(y4 - y3, x4 - x3) - a23);
//                    if(da1 >= pi) da1 = 2*pi - da1;
//                    if(da2 >= pi) da2 = 2*pi - da2;
//
//                    if(da1 + da2 < m_angle_tolerance)
//                    {
//                        // Finally we can stop the recursion
//                        //----------------------
//                        m_points.add(point_type(x1234, y1234));
//                        return;
//                    }
//
//                    if(m_cusp_limit != 0.0)
//                    {
//                        if(da1 > m_cusp_limit)
//                        {
//                            m_points.add(point_type(x2, y2));
//                            return;
//                        }
//
//                        if(da2 > m_cusp_limit)
//                        {
//                            m_points.add(point_type(x3, y3));
//                            return;
//                        }
//                    }
//                }
//            }
//            else
//            {
//                if(d2 > curve_collinearity_epsilon)
//                {
//                    // p1,p3,p4 are collinear, p2 is considerable
//                    //----------------------
//                    if(d2 * d2 <= m_distance_tolerance * (dx*dx + dy*dy))
//                    {
//                        if(m_angle_tolerance < curve_angle_tolerance_epsilon)
//                        {
//                            m_points.add(point_type(x1234, y1234));
//                            return;
//                        }
//
//                        // Angle Condition
//                        //----------------------
//                        da1 = fabs(atan2(y3 - y2, x3 - x2) - atan2(y2 - y1, x2 - x1));
//                        if(da1 >= pi) da1 = 2*pi - da1;
//
//                        if(da1 < m_angle_tolerance)
//                        {
//                            m_points.add(point_type(x2, y2));
//                            m_points.add(point_type(x3, y3));
//                            return;
//                        }
//
//                        if(m_cusp_limit != 0.0)
//                        {
//                            if(da1 > m_cusp_limit)
//                            {
//                                m_points.add(point_type(x2, y2));
//                                return;
//                            }
//                        }
//                    }
//                }
//                else
//                if(d3 > curve_collinearity_epsilon)
//                {
//                    // p1,p2,p4 are collinear, p3 is considerable
//                    //----------------------
//                    if(d3 * d3 <= m_distance_tolerance * (dx*dx + dy*dy))
//                    {
//                        if(m_angle_tolerance < curve_angle_tolerance_epsilon)
//                        {
//                            m_points.add(point_type(x1234, y1234));
//                            return;
//                        }
//
//                        // Angle Condition
//                        //----------------------
//                        da1 = fabs(atan2(y4 - y3, x4 - x3) - atan2(y3 - y2, x3 - x2));
//                        if(da1 >= pi) da1 = 2*pi - da1;
//
//                        if(da1 < m_angle_tolerance)
//                        {
//                            m_points.add(point_type(x2, y2));
//                            m_points.add(point_type(x3, y3));
//                            return;
//                        }
//
//                        if(m_cusp_limit != 0.0)
//                        {
//                            if(da1 > m_cusp_limit)
//                            {
//                                m_points.add(point_type(x3, y3));
//                                return;
//                            }
//                        }
//                    }
//                }
//                else
//                {
//                    // Collinear case
//                    //-----------------
//                    dx = x1234 - (x1 + x4) / 2;
//                    dy = y1234 - (y1 + y4) / 2;
//                    if(dx*dx + dy*dy <= m_distance_tolerance)
//                    {
//                        m_points.add(point_type(x1234, y1234));
//                        return;
//                    }
//                }
//            }
//        }
//
//        // Continue subdivision
//        //----------------------
//        recursive_bezier(x1, y1, x12, y12, x123, y123, x1234, y1234, level + 1); 
//        recursive_bezier(x1234, y1234, x234, y234, x34, y34, x4, y4, level + 1); 
//    }
//
//    //------------------------------------------------------------------------
//    void curve4_div::bezier(double x1, double y1, 
//                            double x2, double y2, 
//                            double x3, double y3, 
//                            double x4, double y4)
//    {
//        m_points.add(point_type(x1, y1));
//        recursive_bezier(x1, y1, x2, y2, x3, y3, x4, y4, 0);
//        m_points.add(point_type(x4, y4));
//    }
