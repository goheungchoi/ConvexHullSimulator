package Project1;

import GiftWrappingAlgorithm.*;

/**
 * This Point class defines the location of a point on (x, y) coordinate.
 * @author Goheung Choi
 */
public class Point implements Comparable<Point> {
    private float x;
    private float y;
    private Point h;
    /**
     * Default constructor of Point class.<br>
     * This constructor does not instantiate x and y values.
     * @author Goheung Choi
     */
    public Point() {}
    /**
     * This constructor instantiates x and y values.
     * @param x x coordinate of this point
     * @param y y coordinate of this point
     */
    public Point(float x, float y)
    {
        this.x = x;
        this.y = y;
    }
    /**
     * Copy constructor.<\n>
     * This constructor copies the x and y values from another point.
     * @param point a point to be copied
     */
    public Point(Point point)
    {
        this.x = point.getX();
        this.y = point.getY();
    }
    /**
     * Returns the value of x.
     * @return x value of a point
     */
    public float getX() {
        return x;
    }
    /**
     * Sets x coordinate.
     * @param x new x value to set up
     */
    public void setX(float x) {
        this.x = x;
    }
    /**
     * Returns the value of y.
     * @return x value of a point
     */
    public float getY() {
        return y;
    }
    /**
     * Sets y coordinate.
     * @param y new y value to set up
     */
    public void setY(float y) {
        this.y = y;
    }

    public boolean equals(Point o) {
        return (this.x == o.x && this.y == o.y);
    }

    @Override
    public int compareTo(Point o) {
        if(angle(this, h) > angle(o, h))
            return 1;
        else if (angle(this, h) > angle(o, h))
            return 0;
        else
            return -1;
    }
    
    /**
     * Returns the degree of an angle between x-axis and a line which passes 
     * through Point a and Point h.
     * @param a point to test
     * @param h the rightmost lowest point
     * @return double--the angle between y = h.getY() and a line passing Point a
     * and Point h
     */
    private double angle(Point a, Point h)
    {
        //in case, the slope of a line is not defined
        if (a.getY() != h.getY() && a.getX() == h.getX())
        {
            return Math.PI / 2.0;
        }
        else if (a.getY() == h.getY() && a.getX() == h.getX())
            return 0;
        else
        {
            //get the slope of the line from h to a
            double slope = (a.getY() - h.getY()) / (a.getX() - h.getX());
            //if the slope is greater than 0
            if (slope > 0)
                return Math.atan(slope);
            /* Since h is the rightmost lowest point, once the slope is 0,
             * the point a is always on the left side of h.
             * Thus, the degree between x-axis and the line h to a is always PI.
             * Also, the less the negetive slope is, the less the value of
             * arctan(slope) we get, so we add this up with PI value.
             */
            else
                return Math.PI + Math.atan(slope);
        }
    }
}
