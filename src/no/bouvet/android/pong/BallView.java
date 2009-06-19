package no.bouvet.android.pong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

public class BallView extends View {
    final PongGame game;
    private Drawable mBall;
    private int mBallWidth;
    private int mBallHeight;
    public BallView(Context context, PongGame game) {
        super(context);
        this.game = game;
        mBall = context.getResources().getDrawable(R.drawable.ball);
        mBallHeight = mBall.getIntrinsicHeight();
        mBallWidth = mBall.getIntrinsicWidth();
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        int yBallTop = canvas.getHeight() - ((int) game.getY() + mBallHeight / 2);
        int xBallLeft = (int) game.getX() - mBallWidth / 2;
        canvas.save();
        mBall.setBounds(xBallLeft, yBallTop, xBallLeft + mBallWidth, yBallTop + mBallHeight);
        mBall.draw(canvas);
        canvas.restore();
    }
}
