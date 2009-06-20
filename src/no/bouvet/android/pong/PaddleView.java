package no.bouvet.android.pong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

class PaddleView extends View {
    private final PongGame mGame;
    private final Drawable mPaddle;
    private final int mPaddleWidth;
    private final int mPaddleHeight;
    PaddleView(Context context, PongGame game) {
        super(context);
        mGame = game;
        mPaddle = context.getResources().getDrawable(R.drawable.paddle);
        mPaddleWidth = mPaddle.getIntrinsicWidth();
        mPaddleHeight = mPaddle.getIntrinsicHeight();
        game.setPaddleWidth(mPaddleWidth);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        int paddleTop = canvas.getHeight() - mGame.getPaddleFixedTop();
        int paddleLeft = mGame.getPaddleLeft();
        canvas.save();
        mPaddle.setBounds(paddleLeft, paddleTop, paddleLeft + mPaddleWidth, paddleTop + mPaddleHeight / 2);
        mPaddle.draw(canvas);
        canvas.restore();
    }
}
