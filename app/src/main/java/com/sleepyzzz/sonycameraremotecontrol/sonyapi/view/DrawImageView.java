package com.sleepyzzz.sonycameraremotecontrol.sonyapi.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.sleepyzzz.sonycameraremotecontrol.activity.PlayerActivity;

/**
 * @ClassName：
 * @Description：TODO
 * @author：SleepyzzZ on 2016/3/25 10:44
 * @
 * @
 * @update：Administrator on 2016/3/25 10:44
 * @modify：
 */
public class DrawImageView extends ImageView {

    private static final int mHeight = 64;
    private static final int mWidth = 48;

    private static final String TAG = DrawImageView.class.getSimpleName();

    public DrawImageView(Context context, AttributeSet attrs) {

        super(context, attrs);
    }

    Paint paint = new Paint();
    {
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5.0f);
        paint.setAlpha(100);
    };

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int leftTopX = (int) ((PlayerActivity.mScreenWidthPixels - mWidth)/2.0f);
        int leftTopY = (int) ((PlayerActivity.mScreenHeightPixels - mHeight)/2.0f);
        canvas.drawRect(new Rect(leftTopX, leftTopY, leftTopX+mWidth, leftTopY+mHeight), paint);
    }
}
