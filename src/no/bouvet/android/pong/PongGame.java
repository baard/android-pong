package no.bouvet.android.pong;

import no.bouvet.android.graphics.Line2D;
import android.os.SystemClock;
import android.util.Log;

// holds state and logic for the game
class PongGame implements Runnable {
    private static final double DEFAULT_SPEED = 50.;
    private static final int BAR_MOVING_LEFT = 0;
    private static final int BAR_STOPPED = 1;
    private static final int BAR_MOVING_RIGHT = 2;
    private static final double PX_PER_SEC = 120;

    private long mPhysicsLastUpdated;
    private int mPaddleLeft;
    private double mBallDX = DEFAULT_SPEED;
    private double mBallDY = DEFAULT_SPEED;
    private int mBallX;
    private int mBallY;
    private boolean mPhysicsPaused = true;
    private int mPaddleState = BAR_STOPPED;
    private int mHeight = 1;
    private int mWidth = 1;
    private int mPaddleWidth;
    private int mPaddleFixedHeight = 50;
    boolean lost = false;
    
    public int getBallHorizontal() {
        return mBallX;
    }
    
    public int getPaddleFixedTop() {
        return mPaddleFixedHeight;
    }
    
    public int getBallVertical() {
        return mBallY;
    }
    
    public int getPaddleLeft() {
        return mPaddleLeft;
    }

    void setPhysicsPaused(boolean paused) {
        mPhysicsPaused = paused;
    }
    
    void stopPaddle() {
        mPaddleState = BAR_STOPPED;
    }
    
    void sendPaddleLeft() {
        mPaddleState = BAR_MOVING_LEFT;
    }
    
    void sendPaddleRight() {
        mPaddleState = BAR_MOVING_RIGHT;
    }
    
    public void run() {
        if (!mPhysicsPaused) {
            updatePhysics();
        }
    }
    
    private void updatePhysics() {
        long uptime = SystemClock.uptimeMillis();
        if (mPhysicsLastUpdated == 0) {
            // first update
            mPhysicsLastUpdated = uptime;
            return;
        }
        long elapsedMillis = uptime - mPhysicsLastUpdated;

        updatePaddle(elapsedMillis);
        int newBallX = calculateNewBallX(elapsedMillis);
        int newBallY = calculateNewBallY(elapsedMillis);
        int oldBallX = mBallX;
        int oldBallY = mBallY;
        mBallY = newBallY;
        mBallX = newBallX;
        handlePaddleCollision(oldBallX, oldBallY, newBallX, newBallY);
        handleRightSideCollision(newBallX);
        handleLeftSideCollision(newBallX);
        handleCeilingCollision(newBallY);
        handleFloorCollision(newBallY);
        mPhysicsLastUpdated = uptime;
    }

    private void handleLeftSideCollision(int newBallX) {
        if (newBallX > mWidth) {
            mBallX = mWidth;
            flipBallX();
        }
    }

    private void handleFloorCollision(int newBallY) {
        if (newBallY < 0) {
            //TODO alert UI
            Log.e("pong", "You lost!");
            mBallY = 0;
            flipBallY();
        }
    }

    private void handleCeilingCollision(int newBallY) {
        if (newBallY > mHeight) {
            //TODO send ball back to opponent
            mBallY = mHeight;
            flipBallY();
        }
    }

    private void handleRightSideCollision(int newBallX) {
        if (newBallX < 0) {
            mBallX = 0;
            flipBallX();
        }
    }

    private void flipBallX() {
        mBallDX = -mBallDX;
        // adjust velocity slightly
        mBallDY += Math.random();
    }

    private void handlePaddleCollision(int oldBallX, int oldBallY, int newBallX, int newBallY) {
        // detect collision with bar
        int paddleRight = mPaddleLeft + mPaddleWidth;
        if (Line2D.linesIntersect(oldBallX, oldBallY, newBallX, newBallY, mPaddleLeft, mPaddleFixedHeight, paddleRight, mPaddleFixedHeight)) {
            Log.i("pong", "ball collided with paddle");
            flipBallY();
            // dont let the ball pass the paddle
            mBallY = oldBallY;
            mBallX = oldBallX;
        }
    }

    private void flipBallY() {
        mBallDY = -mBallDY;
        // adjust velocity slightly
        mBallDX += Math.random();
    }

    private int calculateNewBallY(long elapsedMillis) {
        return (int) (mBallDY / 1000 * elapsedMillis + mBallY);
    }

    private int calculateNewBallX(long elapsedMillis) {
        return (int) (mBallDX / 1000 * elapsedMillis + mBallX);
    }

    private void updatePaddle(long elapsedMillis) {
        int paddleMovement = (int) (PX_PER_SEC / 1000 * elapsedMillis);
        if (mPaddleState == BAR_MOVING_LEFT && !isPaddleMaxLeft()) {
            mPaddleLeft -= paddleMovement;
        }
        if (mPaddleState == BAR_MOVING_RIGHT && !isPaddleMaxRight()) {
            mPaddleLeft += paddleMovement;
        }
    }

    private boolean isPaddleMaxRight() {
        return mPaddleLeft > mWidth - mPaddleWidth;
    }

    private boolean isPaddleMaxLeft() {
        return mPaddleLeft < 0;
    }

    void sizeChanged(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    void setPaddleWidth(int barWidth) {
        mPaddleWidth = barWidth;
    }
    
    int getPaddleHorizontalCenter() {
        return mPaddleLeft + mPaddleWidth / 2;
    }
}
