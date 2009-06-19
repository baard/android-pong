package no.bouvet.android.pong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

public class BarView extends View {
    final PongGame game;
    private Drawable mBar;
    private int mBarWidth;
    private int mBarHeight;
    public BarView(Context context, PongGame game) {
        super(context);
        this.game = game;
        mBar = context.getResources().getDrawable(R.drawable.bar);
        mBarWidth = mBar.getIntrinsicWidth();
        mBarHeight = mBar.getIntrinsicHeight();
        game.setBarWidth(mBarWidth);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        int yBarTop = canvas.getHeight() - game.getMFixedHeight();
        int xBarLeft = (int) game.getBarX();
        canvas.save();
        mBar.setBounds(xBarLeft, yBarTop, xBarLeft + mBarWidth, yBarTop + mBarHeight);
        mBar.draw(canvas);
        canvas.restore();
    }
}
