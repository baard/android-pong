package no.bouvet.android.pong;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

class BackgroundView extends View {
    private Bitmap mBackgroundImage;

    public BackgroundView(Context context) {
        super(context);
        Resources res = context.getResources();
        mBackgroundImage = BitmapFactory.decodeResource(res, R.drawable.earthrise);
    }

    void sizeChanged(int width, int height) {
        if (width > 1 && height > 1) {
            //TODO remove checks?
            mBackgroundImage = Bitmap.createScaledBitmap(mBackgroundImage, width, height, true);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBackgroundImage, 0, 0, null);
    }
}
