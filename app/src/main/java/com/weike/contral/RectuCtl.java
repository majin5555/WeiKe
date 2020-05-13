package com.weike.contral;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.weike.data.CommonDef;
import com.weike.interfaces.ISketchpadDraw;


/*function:
 * @author:
 * Date:
 *
 *
 * 矩形
 */
public class RectuCtl implements ISketchpadDraw {

    private Paint mPaint = new Paint();
    private boolean m_hasDrawn = false;

    private float startx = 0;
    private float starty = 0;
    private float endx = 0;
    private float endy = 0;
    private int type;

    private float minX, minY, maxX, maxY;//浮点行的最小、最大 X ，Y坐标

    @Override
    public float getMinX() {
        return minX;
    }

    @Override
    public float getMinY() {
        return minY;
    }

    @Override
    public float getMaxX() {
        return maxX;
    }

    @Override
    public float getMaxY() {
        return maxY;
    }

    public RectuCtl(int penSize, int penColor, int type) {
        this.type = type;
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(penColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(penSize);//���û��ʴ�ϸ
    }

    public void draw(Canvas canvas) {
        // TODO Auto-generated method stub
        if (null != canvas) {
            if (type == 1) {//正常矩形
                canvas.drawRect(startx, starty, endx, endy, mPaint);
            }
            {
                RectF r2 = new RectF();
                r2.left = startx;
                r2.right = endx;
                r2.top = starty;
                r2.bottom = endy;
                //drawRoundRect绘制圆角矩形
                canvas.drawRoundRect(r2, 20, 20, mPaint);
            }
        }
    }


    public boolean hasDraw() {
        // TODO Auto-generated method stub
        return m_hasDrawn;
        //return false;
    }


    public void cleanAll() {
        // TODO Auto-generated method stub
        mPaint.reset();
    }


    public void touchDown(float x, float y) {
        // TODO Auto-generated method stub
        startx = x;
        starty = y;
        endx = x;
        endy = y;
        minX = startx - (CommonDef.SMALL_PEN_WIDTH * 0.5f);
        minY = starty - (CommonDef.SMALL_PEN_WIDTH * 0.5f);


    }


    public void touchMove(float x, float y) {
        // TODO Auto-generated method stub
        endx = x;
        endy = y;
        m_hasDrawn = true;

    }
    public void touchUp(float x, float y) {
        // TODO Auto-generated method stub
        endx = x;
        endy = y;
        maxX = endx + (CommonDef.SMALL_PEN_WIDTH * 0.5f);
        maxY = endy + (CommonDef.SMALL_PEN_WIDTH * 0.5f);

    }

}
