package no.bouvet.android.pong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

class BallView extends View {
    private final PongGame game;
    private final Drawable mBall;
    private final int mBallWidth;
    private final int mBallHeight;

    BallView(Context context, PongGame game) {
        super(context);
        this.game = game;
        mBall = context.getResources().getDrawable(R.drawable.ball);
        mBallHeight = mBall.getIntrinsicHeight();
        mBallWidth = mBall.getIntrinsicWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int ballTop = canvas.getHeight() - game.getBallVertical();
        int ballLeft = (int) game.getBallHorizontal() - mBallWidth / 2;
        canvas.save();
        mBall.setBounds(ballLeft, ballTop, ballLeft + mBallWidth / 2, ballTop + mBallHeight / 2);
        mBall.draw(canvas);
        canvas.restore();
    }
}
