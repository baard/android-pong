package no.bouvet.android.pong;

import no.bouvet.android.graphics.CanvasSurfaceView;
import no.bouvet.android.graphics.CanvasSurfaceView.FrameRenderer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class GameActivity extends Activity {
    private Court mCourt;
    private TextView mScoreView;
    private CanvasSurfaceView mCanvasView;
    private RESTClient restClient;
    
    class ScoreUpdatedHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            int score = msg.what;
            mScoreView.setText(Integer.toString(score));
        }
    }
    
    class GameUpdater implements Runnable {
        public void run() {
            mCourt.updatePhysics();
        }
    }
    
    class TiltListener implements SensorListener {
        public static final float THRESHOLD = 0.3f;
        final SensorManager mSensorManager;
        TiltListener(Context context) {
            mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
            mSensorManager.registerListener(this, SensorManager.SENSOR_ACCELEROMETER, SensorManager.SENSOR_DELAY_GAME);
        }
        public void onSensorChanged(int sensor, float[] values) {
            float x = values[SensorManager.RAW_DATA_X];
            mCourt.setPaddleSpeed(x * 100);
        }
        
        public void onAccuracyChanged(int sensor, int accuracy) {
            // do nothing
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        restClient.stopThread();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        restClient.startThread();
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.game);
        
        mCourt = new Court(new ScoreUpdatedHandler());
        restClient = new RESTClient(mCourt);
        mCourt.setCourtHandler(restClient);
        mScoreView = (TextView) findViewById(R.id.score);

        mCanvasView = (CanvasSurfaceView) findViewById(R.id.surface);
        mCanvasView.setOnKeyListener(new GameKeyListener());
        mCanvasView.setOnTouchListener(new GameTouchListener());
        mCanvasView.setRenderer(new GameRenderer(this));
        mCanvasView.setEvent(new GameUpdater());
        new TiltListener(this);
    }

    class GameTouchListener implements OnTouchListener {
        public boolean onTouch(View v, MotionEvent event) {
            float horizontalTouch = event.getX();
            double paddleHorizontalCenter = mCourt.getPaddleHorizontalCenter();
            if (Math.abs(horizontalTouch - paddleHorizontalCenter) < 40) {
                // finger within 40px of paddle
                mCourt.stopPaddle();
            } else if (horizontalTouch < paddleHorizontalCenter) {
                // finger left of paddle
                mCourt.sendPaddleLeft();
            } else if (horizontalTouch > paddleHorizontalCenter) {
                // finger right of paddle
                mCourt.sendPaddleRight();
            }
            return true;
        }
    }

    class GameRenderer implements FrameRenderer {
        private final View mBallView;
        private final View mPaddleView;
        private final BackgroundView mBackgroundView;
        public GameRenderer(Context context) {
            mPaddleView = new PaddleView(context, mCourt);
            mBallView = new BallView(context, mCourt);
            mBackgroundView = new BackgroundView(context); 
        }
        public void drawFrame(Canvas canvas) {
            mBackgroundView.draw(canvas);
            // paddle and ball draws over the background
            mPaddleView.draw(canvas);
            mBallView.draw(canvas);
        }

        public void sizeChanged(int width, int height) {
            if (width > 1 && height > 1) {
                mCourt.sizeChanged(width, height);
                mBackgroundView.sizeChanged(width, height);
            }
        }
    }

    class GameKeyListener implements OnKeyListener {
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                mCourt.sendPaddleLeft();
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                mCourt.sendPaddleRight();
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                mCourt.stopPaddle();
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