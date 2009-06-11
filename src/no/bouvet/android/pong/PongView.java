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
        public static final int PX_PER_SEC = 120;
        
        private static final int BAR_MOVING_LEFT = 0;
        private static final int BAR_STOPPED = 1;
        private static final int BAR_MOVING_RIGHT = 2;

        private Drawable mBar;
        private Drawable mBall;
        private Bitmap mBackgroundImage;
        private long mLastTime;
        private boolean mRun = false;
        private boolean mPaused = true;

        private double mBarX;
        private double mDX = 25.;
        private double mDY = 25.;
        private double mX;
        private double mY;
        
        private int mCanvasHeight = 1;
        private int mCanvasWidth = 1;

        private int mBarState = BAR_STOPPED;

        private SurfaceHolder mSurfaceHolder;
        private int mBarWidth;
        private int mBarHeight;
        private int mBallWidth;
        private int mBallHeight;

        public PongThread(SurfaceHolder surfaceHolder, Context context) {
            mSurfaceHolder = surfaceHolder;

            Resources res = context.getResources();
            mBar = res.getDrawable(R.drawable.bar);
            mBall = res.getDrawable(R.drawable.ball);
            // load background image as a Bitmap instead of a Drawable b/c
            // we don't need to transform it and it's faster to draw this way
            mBackgroundImage = BitmapFactory.decodeResource(res,
                    R.drawable.earthrise);

            mBarWidth = mBar.getIntrinsicWidth();
            mBarHeight = mBar.getIntrinsicHeight();
            mBallHeight = mBall.getIntrinsicHeight();
            mBallWidth = mBall.getIntrinsicHeight();
        }

        public void doStart() {
            synchronized (mSurfaceHolder) {
                mLastTime = System.currentTimeMillis() + 100;
                mPaused = false;
            }
        }

        public synchronized void restoreState(Bundle savedState) {
            synchronized (mSurfaceHolder) {
            }
        }

        public Bundle saveState(Bundle map) {
            synchronized (mSurfaceHolder) {
                if (map != null) {
                }
            }
            return map;
        }

        @Override
        public void run() {
            while (mRun) {
                Canvas c = null;
                try {
                    c = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {
                        if (!mPaused) {
                            updatePhysics();
                        }
                        doDraw(c);
                    }
                } finally {
                    if (c != null) {
                        mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }
        
        public void pause() {
            synchronized (mSurfaceHolder) {
                mPaused = true;
            }
        }


        public void setRunning(boolean b) {
            mRun = b;
        }

        public void setSurfaceSize(int width, int height) {
            synchronized (mSurfaceHolder) {
                mCanvasWidth = width;
                mCanvasHeight = height;
                // don't forget to resize the background image
                mBackgroundImage = Bitmap.createScaledBitmap(
                        mBackgroundImage, width, height, true);
            }
        }
        
        public void unpause() {
            synchronized (mSurfaceHolder) {
                mLastTime = System.currentTimeMillis() + 100;
                mPaused = false;
            }
        }

        boolean doKeyDown(int keyCode, KeyEvent msg) {
            synchronized (mSurfaceHolder) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    mBarState = BAR_MOVING_LEFT;
                }
                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    mBarState = BAR_MOVING_RIGHT;
                }
                return false;
            }
        }

        boolean doKeyUp(int keyCode, KeyEvent msg) {
            boolean handled = false;

            synchronized (mSurfaceHolder) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    mBarState = BAR_STOPPED;
                    handled = true;
                }
            }

            return handled;
        }

        private void doDraw(Canvas canvas) {
            canvas.drawBitmap(mBackgroundImage, 0, 0, null);
            int yBallTop = mCanvasHeight - ((int) mY + mBallHeight / 2);
            int xBallLeft = (int) mX - mBallWidth / 2;

            int yBarTop = mCanvasHeight - (mBarHeight);
            int xBarLeft = (int) mBarX - mBarWidth / 2;

            canvas.save();
            mBall.setBounds(xBallLeft, yBallTop, xBallLeft + mBallWidth, yBallTop
                    + mBallHeight);
            mBall.draw(canvas);
            canvas.restore();
            canvas.save();
            mBar.setBounds(xBarLeft, yBarTop, xBarLeft + mBarWidth, yBarTop
                    + mBarHeight);
            mBar.draw(canvas);
            canvas.restore();
        }

        private void updatePhysics() {
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

            mX = mX % mCanvasWidth;
            mY = mY % mCanvasHeight;
            mLastTime = now;
        }
    }

    private PongThread thread;

    public PongView(Context context, AttributeSet attrs) {
        super(context, attrs);

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        thread = new PongThread(holder, context);

        setFocusable(true);
    }

    public PongThread getThread() {
        return thread;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent msg) {
        return thread.doKeyDown(keyCode, msg);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent msg) {
        return thread.doKeyUp(keyCode, msg);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (!hasWindowFocus) {
            thread.pause();
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        thread.setSurfaceSize(width, height);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }
}
