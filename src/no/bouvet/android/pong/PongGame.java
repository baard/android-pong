package no.bouvet.android.pong;

import java.io.Serializable;

// holds state and logic for the game
public class PongGame implements Serializable {
    private static final long serialVersionUID = 1L;
    static final int BAR_MOVING_LEFT = 0;
    static final int BAR_STOPPED = 1;
    static final int BAR_MOVING_RIGHT = 2;

    private long mLastTime;
    private double mBarX;
    private double mDX = 25.;
    private double mDY = 25.;
    private double mX;
    private double mY;
    private boolean mPaused = true;
    public static final int PX_PER_SEC = 120;
    private int mBarState = BAR_STOPPED;
    private int mGameHeight = 1;
    private int mGameWidth = 1;
    int mBarWidth;
    
    public double getX() {
        return mX;
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
    
    void updatePhysics() {
        if (!mPaused) {
            doUpdatePhysics();
        }
    }

    private void doUpdatePhysics() {
        long now = System.currentTimeMillis();

        if (mLastTime > now) {
            return;
        }

        double elapsed = (now - mLastTime) / 1000.0;

        if (mBarState == BAR_MOVING_LEFT) {
            mBarX += elapsed * PX_PER_SEC;
        }
        if (mBarState == BAR_MOVING_RIGHT) {
            mBarX -= elapsed * PX_PER_SEC;
        }
        mX += elapsed * mDX;
        mY += elapsed * mDY;

        mX = mX % mGameWidth;
        mY = mY % mGameHeight;
        mLastTime = now;
    }

    void setGameSize(int height, int width) {
        mGameWidth = width;
        mGameHeight = height;
    }

    public void setBarWidth(int barWidth) {
        mBarWidth = barWidth;
    }
}
