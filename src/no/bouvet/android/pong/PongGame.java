package no.bouvet.android.pong;

import android.util.Log;

// holds state and logic for the game
public class PongGame implements Runnable {
    private static final long serialVersionUID = 1L;
    static final int BAR_MOVING_LEFT = 0;
    static final int BAR_STOPPED = 1;
    static final int BAR_MOVING_RIGHT = 2;

    private long mLastTime;
    private double mBarX;
    private double mDX = 50.;
    private double mDY = 50.;
    private double mX;
    private double mY;
    private boolean mPaused = true;
    public static final int PX_PER_SEC = 120;
    private int mBarState = BAR_STOPPED;
    private int mGameHeight = 1;
    private int mGameWidth = 1;
    int mBarWidth;
    int mFixedHeight = 50;
    boolean lost = false;
    
    public double getX() {
        return mX;
    }
    
    public int getMFixedHeight() {
        return mFixedHeight;
    }
    
    public double getY() {
        return mY;
    }
    
    public double getBarX() {
        return mBarX;
    }

    void setPaused(boolean paused) {
        mPaused = paused;
    }
    
    void stopBar() {
        mBarState = BAR_STOPPED;
    }
    
    void moveBarLeft() {
        mBarState = BAR_MOVING_LEFT;
    }
    
    void moveBarRight() {
        mBarState = BAR_MOVING_RIGHT;
    }
    
    public void run() {
        if (!mPaused) {
            doUpdatePhysics();
        }
    }
    
    public boolean isLost() {
        return lost;
    }

    int count = 0;
    private void doUpdatePhysics() {
        long now = System.currentTimeMillis();

        if (mLastTime == 0) {
            mLastTime = now;
            return;
        }
        if (mLastTime > now) {
            return;
        }

        double elapsed = (now - mLastTime) / 1000.0;

        if (mBarState == BAR_MOVING_LEFT && mBarX > 0) {
            mBarX -= elapsed * PX_PER_SEC;
        }
        if (mBarState == BAR_MOVING_RIGHT && mBarX < mGameWidth - mBarWidth) {
            mBarX += elapsed * PX_PER_SEC;
        }
        double newMX = mX + elapsed * mDX;
        double newMY = mY + elapsed * mDY;
        
        // detect collision with bar
        double mBarXstart = mBarX;
        double mBarXend = mBarX + mBarWidth;
        
        if (linesIntersect(mX, mY, newMX, newMY, mBarXstart, mFixedHeight, mBarXend, mFixedHeight)) {
            Log.e("game", "ball collided with paddle");
            mDY = -mDY;
        } else {
            mY = newMY;
        }
        mX = newMX;
        
        count++;
        if (count > 50) {
            Log.i("game", String.format("Distance to paddle: %s", ptLineDist(mBarXstart, mFixedHeight, mBarXend, mFixedHeight, mX, mY)));
            count = 0;
        }
        
        if (mY < 0) {
            //lost = true;
            //mPaused = true;
            Log.e("game", "You lost!");
            mY = 1;
            mDY = -mDY;
            mDX += Math.random();
        }
        if (mY > mGameHeight) {
            //TODO send ball back to opponent
            mY = mGameHeight - 1;
            mDY = -mDY;
            mDX += Math.random();
        }
        if (mX < 0) {
            mX = 1;
            mDX = -mDX;
            mDY += Math.random();
        }
        if (mX > mGameWidth) {
            mX = mGameWidth - 1;
            mDX = -mDX;
            mDY += Math.random();
        }

        mLastTime = now;
    }

    void sizeChanged(int width, int height) {
        mGameWidth = width;
        mGameHeight = height;
    }

    public void setBarWidth(int barWidth) {
        mBarWidth = barWidth;
    }
    
    /**
     * Tests if the line segment from (X1,&nbsp;Y1) to 
     * (X2,&nbsp;Y2) intersects the line segment from (X3,&nbsp;Y3) 
     * to (X4,&nbsp;Y4).
     * @param X1,&nbsp;Y1 the coordinates of the beginning of the first 
     *          specified line segment
     * @param X2,&nbsp;Y2 the coordinates of the end of the first 
     *          specified line segment
     * @param X3,&nbsp;Y3 the coordinates of the beginning of the second
     *           specified line segment
     * @param X4,&nbsp;Y4 the coordinates of the end of the second 
     *          specified line segment
     * @return <code>true</code> if the first specified line segment 
     *          and the second specified line segment intersect  
     *          each other; <code>false</code> otherwise.  
     */
    public static boolean linesIntersect(double X1, double Y1,
                     double X2, double Y2,
                     double X3, double Y3,
                     double X4, double Y4) {
    return ((relativeCCW(X1, Y1, X2, Y2, X3, Y3) *
         relativeCCW(X1, Y1, X2, Y2, X4, Y4) <= 0)
        && (relativeCCW(X3, Y3, X4, Y4, X1, Y1) *
            relativeCCW(X3, Y3, X4, Y4, X2, Y2) <= 0));
    }
    
    /**
     * Returns an indicator of where the specified point 
     * (PX,&nbsp;PY) lies with respect to the line segment from 
     * (X1,&nbsp;Y1) to (X2,&nbsp;Y2).
     * The return value can be either 1, -1, or 0 and indicates
     * in which direction the specified line must pivot around its
     * first endpoint, (X1,&nbsp;Y1), in order to point at the
     * specified point (PX,&nbsp;PY).
     * <p>A return value of 1 indicates that the line segment must
     * turn in the direction that takes the positive X axis towards
     * the negative Y axis.  In the default coordinate system used by
     * Java 2D, this direction is counterclockwise.  
     * <p>A return value of -1 indicates that the line segment must
     * turn in the direction that takes the positive X axis towards
     * the positive Y axis.  In the default coordinate system, this 
     * direction is clockwise.
     * <p>A return value of 0 indicates that the point lies
     * exactly on the line segment.  Note that an indicator value 
     * of 0 is rare and not useful for determining colinearity 
     * because of floating point rounding issues. 
     * <p>If the point is colinear with the line segment, but 
     * not between the endpoints, then the value will be -1 if the point
     * lies "beyond (X1,&nbsp;Y1)" or 1 if the point lies 
     * "beyond (X2,&nbsp;Y2)".
     * @param X1,&nbsp;Y1 the coordinates of the beginning of the
     *      specified line segment
     * @param X2,&nbsp;Y2 the coordinates of the end of the specified
     *      line segment
     * @param PX,&nbsp;PY the coordinates of the specified point to be
     *      compared with the specified line segment
     * @return an integer that indicates the position of the third specified
     *          coordinates with respect to the line segment formed
     *          by the first two specified coordinates.
     */
    public static int relativeCCW(double X1, double Y1,
                  double X2, double Y2,
                  double PX, double PY) {
    X2 -= X1;
    Y2 -= Y1;
    PX -= X1;
    PY -= Y1;
    double ccw = PX * Y2 - PY * X2;
    if (ccw == 0.0) {
        // The point is colinear, classify based on which side of
        // the segment the point falls on.  We can calculate a
        // relative value using the projection of PX,PY onto the
        // segment - a negative value indicates the point projects
        // outside of the segment in the direction of the particular
        // endpoint used as the origin for the projection.
        ccw = PX * X2 + PY * Y2;
        if (ccw > 0.0) {
        // Reverse the projection to be relative to the original X2,Y2
        // X2 and Y2 are simply negated.
        // PX and PY need to have (X2 - X1) or (Y2 - Y1) subtracted
        //    from them (based on the original values)
        // Since we really want to get a positive answer when the
        //    point is "beyond (X2,Y2)", then we want to calculate
        //    the inverse anyway - thus we leave X2 & Y2 negated.
        PX -= X2;
        PY -= Y2;
        ccw = PX * X2 + PY * Y2;
        if (ccw < 0.0) {
            ccw = 0.0;
        }
        }
    }
    return (ccw < 0.0) ? -1 : ((ccw > 0.0) ? 1 : 0);
    }
    
    /**
     * Returns the distance from a point to a line.
     * The distance measured is the distance between the specified
     * point and the closest point on the infinitely-extended line
     * defined by the specified coordinates.  If the specified point 
     * intersects the line, this method returns 0.0.
     * @param X1,&nbsp;Y1 the coordinates of one point on the
     *      specified line
     * @param X2,&nbsp;Y2 the coordinates of another point on the
     *      specified line
     * @param PX,&nbsp;PY the coordinates of the specified point being
     *      measured against the specified line
     * @return a double value that is the distance from the specified
     *           point to the specified line.
     * @see #ptSegDist(double, double, double, double, double, double)
     */
    public static double ptLineDist(double X1, double Y1,
                    double X2, double Y2,
                    double PX, double PY) {
    return Math.sqrt(ptLineDistSq(X1, Y1, X2, Y2, PX, PY));
    }
    
    /**
     * Returns the square of the distance from a point to a line.
     * The distance measured is the distance between the specified
     * point and the closest point on the infinitely-extended line
     * defined by the specified coordinates.  If the specified point 
     * intersects the line, this method returns 0.0.
     * @param X1,&nbsp;Y1 the coordinates of one point on the
     *      specified line
     * @param X2,&nbsp;Y2 the coordinates of another point on 
     *      the specified line
     * @param PX,&nbsp;PY the coordinates of the specified point being
     *      measured against the specified line
     * @return a double value that is the square of the distance from the
     *          specified point to the specified line.
     * @see #ptSegDistSq(double, double, double, double, double, double)
     */
    public static double ptLineDistSq(double X1, double Y1,
                      double X2, double Y2,
                      double PX, double PY) {
    // Adjust vectors relative to X1,Y1
    // X2,Y2 becomes relative vector from X1,Y1 to end of segment
    X2 -= X1;
    Y2 -= Y1;
    // PX,PY becomes relative vector from X1,Y1 to test point
    PX -= X1;
    PY -= Y1;
    double dotprod = PX * X2 + PY * Y2;
    // dotprod is the length of the PX,PY vector
    // projected on the X1,Y1=>X2,Y2 vector times the
    // length of the X1,Y1=>X2,Y2 vector
    double projlenSq = dotprod * dotprod / (X2 * X2 + Y2 * Y2);
    // Distance to line is now the length of the relative point
    // vector minus the length of its projection onto the line
    double lenSq = PX * PX + PY * PY - projlenSq;
    if (lenSq < 0) {
        lenSq = 0;
    }
    return lenSq;
    }

    public double getMiddleBarX() {
        return mBarX + mBarWidth / 2;
    }
}
