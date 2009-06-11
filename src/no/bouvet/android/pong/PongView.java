package no.bouvet.android.pong;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class PongView extends SurfaceView implements SurfaceHolder.Callback {
    class PongThread extends Thread {
        private boolean mRun = false;
        @Override
        public void run() {
            while (mRun) {
                Canvas c = null;
                try {
                    c = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {
                        game.updatePhysics();
                        repaintCanvas(c);
                    }
                } finally {
                    if (c != null) {
                        mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }
    }
    
    private Drawable mBar;
    private Drawable mBall;
    private Bitmap mBackgroundImage;
    
    private int mCanvasHeight = 1;

    private SurfaceHolder mSurfaceHolder;
    private int mBarWidth;
    private int mBarHeight;
    private int mBallWidth;
    private int mBallHeight;

    private PongThread thread;
    private PongGame game;

    void doStart() {
        synchronized (mSurfaceHolder) {
            game.setPaused(false);
        }
    }

    void restoreState(Bundle savedState) {
        synchronized (mSurfaceHolder) {
            game = (PongGame) savedState.getSerializable("game");
        }
    }

    Bundle saveState(Bundle map) {
        synchronized (mSurfaceHolder) {
            if (map != null) {
                map.putSerializable("game", game);
            }
        }
        return map;
    }

    void pause() {
        synchronized (mSurfaceHolder) {
            game.setPaused(true);
        }
    }

    private void repaintCanvas(Canvas canvas) {
        canvas.drawBitmap(mBackgroundImage, 0, 0, null);
        int yBallTop = mCanvasHeight - ((int) game.getY() + mBallHeight / 2);
        int xBallLeft = (int) game.getX() - mBallWidth / 2;

        int yBarTop = mCanvasHeight - (mBarHeight);
        int xBarLeft = (int) game.getBarX() - mBarWidth / 2;

        canvas.save();
        mBall.setBounds(xBallLeft, yBallTop, xBallLeft + mBallWidth, yBallTop + mBallHeight);
        mBall.draw(canvas);
        canvas.restore();
        canvas.save();
        mBar.setBounds(xBarLeft, yBarTop, xBarLeft + mBarWidth, yBarTop + mBarHeight);
        mBar.draw(canvas);
        canvas.restore();
    }

    public PongView(Context context, AttributeSet attrs) {
        super(context, attrs);
        game = new PongGame();
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);

        Resources res = context.getResources();
        mBar = res.getDrawable(R.drawable.bar);
        mBall = res.getDrawable(R.drawable.ball);
        mBackgroundImage = BitmapFactory.decodeResource(res,
                R.drawable.earthrise);

        mBarWidth = mBar.getIntrinsicWidth();
        game.setBarWidth(mBarWidth);
        mBarHeight = mBar.getIntrinsicHeight();
        mBallHeight = mBall.getIntrinsicHeight();
        mBallWidth = mBall.getIntrinsicHeight();

        thread = new PongThread();

        setFocusable(true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent msg) {
        synchronized (mSurfaceHolder) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                game.moveBarLeft();
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                game.moveBarRight();
            }
            return false;
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent msg) {
        boolean handled = false;

        synchronized (mSurfaceHolder) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                game.stopBar();
                handled = true;
            }
        }

        return handled;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (!hasWindowFocus) {
            pause();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        synchronized (mSurfaceHolder) {
            game.setGameSize(width, height);
            mCanvasHeight = height;
            mBackgroundImage = Bitmap.createScaledBitmap(mBackgroundImage, width, height, true);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.mRun = true;
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        thread.mRun = false;
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }
}
