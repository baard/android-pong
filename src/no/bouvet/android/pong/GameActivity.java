package no.bouvet.android.pong;

import no.bouvet.android.graphics.CanvasSurfaceView;
import no.bouvet.android.graphics.CanvasSurfaceView.FrameRenderer;

import android.app.Activity;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;

public class GameActivity extends Activity {
    private CanvasSurfaceView mCanvasView;
    private PongGame mGame;
    private View mBallView;
    private View mPaddleView;
    private BackgroundView mBackgroundView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGame = new PongGame();

        mPaddleView = new PaddleView(this, mGame);
        mBallView = new BallView(this, mGame);
        mBackgroundView = new BackgroundView(this);

        mCanvasView = new CanvasSurfaceView(this);
        mCanvasView.setOnKeyListener(new GameKeyListener());
        mCanvasView.setOnTouchListener(new GameTouchListener());
        mCanvasView.setRenderer(new GameRenderer());
        mCanvasView.setEvent(mGame);
        setContentView(mCanvasView);

        mGame.setPhysicsPaused(false);
    }

    class GameTouchListener implements OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            float horizontalTouch = event.getX();
            double paddleHorizontalCenter = mGame.getPaddleHorizontalCenter();
            if (Math.abs(horizontalTouch - paddleHorizontalCenter) < 40) {
                // finger within 40px of paddle
                mGame.stopPaddle();
            } else if (horizontalTouch < paddleHorizontalCenter) {
                // finger left of paddle
                mGame.sendPaddleLeft();
            } else if (horizontalTouch > paddleHorizontalCenter) {
                // finger right of paddle
                mGame.sendPaddleRight();
            }
            return true;
        }
    }

    class GameRenderer implements FrameRenderer {
        @Override
        public void drawFrame(Canvas canvas) {
            mBackgroundView.draw(canvas);
            // paddle and ball draws over the background
            mPaddleView.draw(canvas);
            mBallView.draw(canvas);
        }

        @Override
        public void sizeChanged(int width, int height) {
            if (width > 1 && height > 1) {
                // TODO remove check?
                mGame.sizeChanged(width, height);
                mBackgroundView.sizeChanged(width, height);
            }
        }
    }

    class GameKeyListener implements OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                mGame.sendPaddleLeft();
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                mGame.sendPaddleRight();
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                mGame.stopPaddle();
                return true;
            }
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCanvasView.stopDrawing();
    }
}