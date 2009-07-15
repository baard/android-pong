package no.bouvet.android.pong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

class BallView extends View {
    private final Court mGame;
    private final Drawable mBall;
    private final int mBallWidth;
    private final int mBallHeight;

    BallView(Context context, Court game) {
        super(context);
        mGame = game;
        mBall = context.getResources().getDrawable(R.drawable.ball);
        mBallHeight = mBall.getIntrinsicHeight();
        mBallWidth = mBall.getIntrinsicWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int ballTop = canvas.getHeight() - mGame.getBallVertical();
        int ballLeft = (int) mGame.getBallHorizontal() - mBallWidth / 2;
        canvas.save();
        mBall.setBounds(ballLeft, ballTop, ballLeft + mBallWidth, ballTop + mBallHeight);
        mBall.draw(canvas);
        canvas.restore();
    }
}
