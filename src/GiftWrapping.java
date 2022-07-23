package Project1;

import java.util.ArrayList;

/**
 *
 * @author 958026292
 */
public class GiftWrapping {
    /**
     * Returns ArrayList of a convex hull among input points by Gift-Wrapping 
     * (also, called Jarvis march) algorithm.<br>
     * This algorithm has O(hn) of efficiency.
     * @param points An ArrayList which contains points for getting a convex hull
     * @return ArrayList that contains the points on the convex hull
     */
    public static ArrayList<Point> getConvexHull(ArrayList<Point> points)
    {
        boolean foundAll = false;
        //create an arraylist which is going to contain points on a convex hull.
        ArrayList<Point> convexHull = new ArrayList<>();
        //copy the input points into another array
        ArrayList<Point> copyPoints = new ArrayList<>();
        copyPoints.addAll(points);
        //Step1: find the rightmost lowest point
        Point h = findH(copyPoints);
        //assign the rightmost lowest point to t0.
        Point t0 = h;
        //create t1.
        Point t1;
        //create a test point.
        Point testP;
        //start do-while loop -- loop through all points and find convexHull
        do
        {
//            //delete t0 point from the ArrayList so that looping process 
//            //doesn't duplicate comparing a point which is already in convexHull
//            copyPoints.remove(t0);
            //add t0 into the ArrayList named 'convexHull'.
            convexHull.add(t0);
            //assign the first point into t1 for comparison with respect to t0
            t1 = copyPoints.get(0);
            //start for loop -- find the next t1.
            for (int i = 0; i < copyPoints.size(); i++)
            {
                testP = copyPoints.get(i);
                //if the test point is located on the right side than t1.
                if (direction(t0, t1, testP) > 0)
                    // *set testP to be t1*
                    t1 = testP;
                //if the test point is located on the same line on which t0 and t1 is
                else if (direction(t0, t1, testP) == 0)
                    //and if the test point is further to the t0 than t1 is.
                    /*(This if statement also prevents t1 from being the same point)
                        case 1: i = copyPoints.get(0) and t1 = copyPoints.get(0)
                        case 2: t0 = t1 = testP = h                           */
                    if (distance(t0, t1) < distance(t0, testP))
                        // *set testP to be t1*
                        t1 = testP;
            }
            //reset t0 to be t1 for finding a new point of convex hull
            t0 = t1;
//            //if the found t1 already exists in the convexHull.
//            if (convexHull.get(convexHull.size() - 1) == t1)
//                //exit this do-while loop
//                foundAll = true;
            //if the found t1 is h point.
            if (h == t0)
                //exit this do-while loop
                foundAll = true;
        } while(!foundAll);
        //end loop
        return convexHull;
    }
    /**
     * Returns a point which is the rightmost lowest point.
     * @param points a set of points
     * @return Point object that is the rightmost lowest point.
     */
    public static Point findH(ArrayList<Point> points)
    {
        Point h = points.get(0);
        
        for (Point point : points) {
            if ( point.getY() < h.getY())
                h = point;
            else if ( point.getY() == h.getY())
                if (point.getX() > h.getX())
                    h = point;
        }
        return h;
    }
    /**
     * Returns a numeric value of how much a point is winding from a line starting<br>
     * at a and ending at b.
     * @param a Point which is the starting point on the line
     * @param b Point which is the ending point on the line
     * @param p Point to test
     * @return double that represents the degree between the line <br>
     * from a to b and the line from a to p.
     */
    public static double direction(Point a, Point b, Point p)
    {
        return (p.getX() - a.getX()) * (b.getY() - a.getY())
                - (p.getY() - a.getY()) * (b.getX() - a.getX());
    }
    /**
     * Returns a distance between two points.
     * @param p1 Point1 on (x, y) coordinate
     * @param p2 Point2 on (x, y) coordinate
     * @return double that represents the value of a distance.
     */
    public static double distance(Point p1, Point p2)
    {
        double a = Math.pow((p2.getX() - p1.getX()), 2) 
                   + Math.pow((p2.getY() - p1.getY()), 2);
        return Math.sqrt(a);
    }
}
