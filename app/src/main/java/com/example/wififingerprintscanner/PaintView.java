package com.example.wififingerprintscanner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class PaintView extends View implements OnTouchListener{

    Paint paint;
    public float mX;
    public float mY;
    public int xX;
    public int yY;
    public boolean isClicked = false;

    public PaintView(Context context,AttributeSet attributeSet){
        super(context,attributeSet);

        paint = new Paint();
        mX = mY = -100;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setColor(Color.RED);
        canvas.drawCircle(mX, mY, 15, paint);
        invalidate();
    }

    @Override
    public boolean onTouch(View v, MotionEvent motionEvent) {
        if(!isClicked)
            isClicked = true;

        xX = Math.round((motionEvent.getX() / v.getWidth()) * 100);
        yY = Math.round((motionEvent.getY() / v.getHeight()) * 100);

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mX = motionEvent.getX();
                mY = motionEvent.getY();
        }
        return true;
    }
}