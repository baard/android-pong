package no.bouvet.android.pong;

import no.bouvet.android.pong.CanvasSurfaceView.FrameRenderer;
import android.app.Activity;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;


public class GameActivity extends Activity {
    private CanvasSurfaceView gameView;
    private PongGame game;
    private View ball;
    private View bar;
    private BackgroundView background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        game = new PongGame();

        bar = new BarView(this, game);
        ball = new BallView(this, game);
        background = new BackgroundView(this);

        gameView = new CanvasSurfaceView(this);
        gameView.setOnKeyListener(new GameKeyListener());
        gameView.setOnTouchListener(new GameTouchListener());
        gameView.setRenderer(new GameRenderer());
        gameView.setEvent(game);
        setContentView(gameView);
        
        game.setPaused(false);
    }
    
    class GameTouchListener implements OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            float xValue = event.getX();
            double barX = game.getMiddleBarX();
            if (Math.abs(xValue - barX) < 40) {
                // finger closer than 40px
                game.stopBar();
            } else if (xValue < barX) {
                game.moveBarLeft();
            } else if (xValue > barX) {
                game.moveBarRight();
            }
            return true;
        }
    }
    
    class GameRenderer implements FrameRenderer {
        @Override
        public void drawFrame(Canvas canvas) {
            background.draw(canvas);
            bar.draw(canvas);
            ball.draw(canvas);
        }

        @Override
        public void sizeChanged(int width, int height) {
            if (width > 1 && height > 1) {
                game.sizeChanged(width, height);
                background.sizeChanged(width, height);
            }
        }
    }
    
    class GameKeyListener implements OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                game.moveBarLeft();
                return true;
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                game.moveBarRight();
                return true;
            }
            else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
                game.stopBar();
                return true;
            }
            return false;
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameView.stopDrawing();
    }
}