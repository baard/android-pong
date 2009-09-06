package no.bouvet.android.pong;

import no.bouvet.android.graphics.Line2D;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

// holds state and logic for the game
class Court {
    private static final float DEFAULT_BALL_SPEED = 100f;
    private static final float PADDLE_SPEED = 200f;

    private long mPhysicsLastUpdated;
    private float mPaddleLeft;
    private float mBallDX;
    private float mBallDY;
    private float mBallX;
    private float mBallY;
    private float mPaddleDX;
    private float mHeight = 1;
    private float mWidth = 1;
    private int mPaddleWidth;
    private int mPaddleFixedHeight = 50;
    private int score = 0;
    private final Handler mHandler;
    
    public Court(Handler handler) {
        //dropRandomNewBall();
        this.mHandler = handler;
    }
    
    public int getBallHorizontal() {
        return (int) mBallX;
    }
    
    public int getPaddleFixedTop() {
        return mPaddleFixedHeight;
    }
    
    public int getBallVertical() {
        return (int) mBallY;
    }
    
    public int getPaddleLeft() {
        return (int) mPaddleLeft;
    }

    void stopPaddle() {
        mPaddleDX = 0;
    }
    
    void sendPaddleLeft() {
        mPaddleDX = - PADDLE_SPEED;
    }
    
    void sendPaddleRight() {
        mPaddleDX = PADDLE_SPEED;
    }
    
    void setPaddleSpeed(float speed) {
        mPaddleDX = speed;
    }
    
    void updatePhysics() {
        long uptime = SystemClock.uptimeMillis();
        if (mPhysicsLastUpdated == 0) {
            // first update
            mPhysicsLastUpdated = uptime;
            return;
        }
        float elapsedSeconds = (float) (uptime - mPhysicsLastUpdated) / 1000;

        updatePaddle(elapsedSeconds);
        float newBallX = calculateNewBallX(elapsedSeconds);
        float newBallY = calculateNewBallY(elapsedSeconds);
        float oldBallX = mBallX;
        float oldBallY = mBallY;
        mBallY = newBallY;
        mBallX = newBallX;
        handlePaddleCollision(oldBallX, oldBallY, newBallX, newBallY);
        handleRightSideCollision(newBallX);
        handleLeftSideCollision(newBallX);
        handleCeilingCollision(newBallY);
        handleFloorCollision(newBallY);
        mPhysicsLastUpdated = uptime;
    }

    private void handleLeftSideCollision(float newBallX) {
        if (newBallX > mWidth) {
            mBallX = mWidth;
            flipBallX();
        }
    }

    private void handleFloorCollision(float newBallY) {
        if (newBallY < 0) {
            setScore(score - 1);
            courtHandler.ballLost();
        }
    }

    private void setScore(int i) {
        score = i;
        Message msg = mHandler.obtainMessage(score);
        mHandler.sendMessage(msg);
    }

    public void dropRandomNewBall() {
        float dx = (float) (Math.random() - 0.5f);
        float dy = -1;
        dropBall(0.5f, dx, dy);
    }
    
    public void resetPaddle() {
        mPaddleLeft = mWidth / 2;
        mPaddleDX = 0;
    }
    
    public void dropBall(float x, float dx, float dy) {
        mBallDX = DEFAULT_BALL_SPEED * dx;
        mBallDY = DEFAULT_BALL_SPEED * - dy;
        mBallX = mWidth * x;
        mBallY = mHeight;
        mPaddleDX = 0;
    }

    private void handleCeilingCollision(float newBallY) {
        if (newBallY > mHeight) {
            setScore(score + 1);
            mBallY = mHeight;
            courtHandler.ballToOpponent(mBallX / mWidth, mBallDX / DEFAULT_BALL_SPEED, mBallDY / DEFAULT_BALL_SPEED);
        }
    }

    private void handleRightSideCollision(float newBallX) {
        if (newBallX < 0) {
            mBallX = 0;
            flipBallX();
        }
    }

    private void flipBallX() {
        mBallDX = -mBallDX;
    }

    private void handlePaddleCollision(float oldBallX, float oldBallY, float newBallX, float newBallY) {
        // detect collision with bar
        float paddleRight = mPaddleLeft + mPaddleWidth;
        if (Line2D.linesIntersect(oldBallX, oldBallY, newBallX, newBallY, mPaddleLeft, mPaddleFixedHeight, paddleRight, mPaddleFixedHeight)) {
            //Log.i("pong", "ball collided with paddle");
            flipBallY();
            // dont let the ball pass the paddle
            mBallY = oldBallY;
            mBallX = oldBallX;
            // add half of paddle speed to ball dx
            mBallDX = mBallDX + mPaddleDX * 0.5f;
        }
    }

    public void flipBallY() {
        mBallDY = -mBallDY;
    }

    private float calculateNewBallY(float elapsedSeconds) {
        return mBallY + mBallDY * elapsedSeconds;
    }

    private float calculateNewBallX(float elapsedSeconds) {
        return mBallX + mBallDX * elapsedSeconds;
    }

    private void updatePaddle(float elapsedSeconds) {
        float newPaddleLeft = mPaddleLeft + mPaddleDX * elapsedSeconds;
        if (newPaddleLeft < 0 || newPaddleLeft > mWidth - mPaddleWidth) {
            return;
        }
        mPaddleLeft = newPaddleLeft;
    }

    void sizeChanged(int width, int height) {
        mWidth = width;
        mHeight = height;
        //dropRandomNewBall();
    }

    void setPaddleWidth(int barWidth) {
        mPaddleWidth = barWidth;
    }
    
    float getPaddleHorizontalCenter() {
        return mPaddleLeft + mPaddleWidth / 2;
    }

    int getScore() {
        return score;
    }
    
    private CourtEventHandler courtHandler;
    
    public void setCourtHandler(CourtEventHandler courtHandler) {
        this.courtHandler = courtHandler;
    }

    public void stopBall() {
        mBallDX = 0.0f;
        mBallDY = 0.0f;
        mBallX = 0.0f;
        mBallY = 0.0f;
    }
}
