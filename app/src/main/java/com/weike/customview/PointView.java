package com.weike.customview;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author: mj
 * @date: 2020/5/16$
 * @desc: 指针View
 */
public class PointView extends View {

    public float currentX = - 100;
    public float currentY = - 100;

    Paint paint = new Paint();

    public PointView(Context context) {
        super(context);
    }

    public PointView(Context context, AttributeSet set) {
        super(context, set);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        canvas.drawCircle(currentX, currentY, 30, paint);
        canvas.restore();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        // 多点触摸的时候 必须加上MotionEvent.ACTION_MASK
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                currentX = event.getX();
                currentY = event.getY();
            case MotionEvent.ACTION_POINTER_DOWN:
                break;

            case MotionEvent.ACTION_MOVE:
                currentX = event.getX();
                currentY = event.getY();
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                currentX = - 100;
                currentY = - 100;
                invalidate();
                break;
        }
        return true;
    }
}