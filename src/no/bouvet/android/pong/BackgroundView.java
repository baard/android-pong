package no.bouvet.android.pong;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

public class BackgroundView extends View {
    private Bitmap mBackgroundImage;
    

    public BackgroundView(Context context) {
        super(context);
        Resources res = context.getResources();
        mBackgroundImage = BitmapFactory.decodeResource(res,
                R.drawable.earthrise);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBackgroundImage, 0, 0, null);
    }

    public void sizeChanged(int width, int height) {
        mBackgroundImage = Bitmap.createScaledBitmap(mBackgroundImage, width, height, true);
    }
}
