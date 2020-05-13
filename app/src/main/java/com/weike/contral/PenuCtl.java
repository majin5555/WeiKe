package com.weike.contral;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Pair;

import com.weike.data.CommonDef;
import com.weike.interfaces.ISketchpadDraw;

import java.util.ArrayList;


/**
 * function:
 *
 * @author: 铅笔
 */
public class PenuCtl implements ISketchpadDraw {

    private boolean m_hasDrawn = false;

    private float minX, minY, maxX, maxY;//浮点行的最小、最大 X ，Y坐标
    private float mX = 0, mY = 0;
    private static final float TOUCH_TOLERANCE = 4;//公差

    private int penColor;

    private Canvas m_Canvas;
    private Path m_Path ;
    private Paint m_Paint;
    private ArrayList<Pair<Path, Paint>> paths = new ArrayList<>();




    public PenuCtl(int penSize, int penColor) {//尺寸和颜色
        this.penColor = penColor;
        m_Paint = new Paint();
        m_Paint.setAntiAlias(true);
        m_Paint.setDither(true);
        m_Paint.setColor(penColor);
        m_Paint.setStyle(Paint.Style.STROKE);
        m_Paint.setStrokeJoin(Paint.Join.ROUND);
        m_Paint.setStrokeCap(Paint.Cap.ROUND);
        m_Paint.setStrokeWidth(penSize);
        m_Canvas = new Canvas();

        m_Path = new Path();
        Paint newPaint = new Paint(m_Paint);
        paths.add(new Pair<>(m_Path, newPaint));
    }

    public void cleanAll() {
        // TODO Auto-generated method stub
        m_Path.reset();
    }

    public void draw(Canvas canvas) {


        //遍历方法三
        for (int i = 0; i < paths.size(); i++) {
            canvas.drawPath(paths.get(i).first, paths.get(i).second);
        }
    }

    public boolean hasDraw() {
        // TODO Auto-generated method stub
        return m_hasDrawn;
    }

    public void touchDown(float x, float y) {

        // TODO Auto-generated method stub
        if (x <= 0) {
            x = 0;
        }
        if (y <= 0) {
            x = 0;
        }

        m_Paint.setColor(penColor);
        m_Paint.setStrokeWidth(2);
        Paint newPaint = new Paint(m_Paint); // Clones the mPaint object
        paths.add(new Pair<>(m_Path, newPaint));


        m_Path.reset();
        m_Path.moveTo(x, y);
        mX = x;
        mY = y;

        if (minX == 0 || minX > x - (CommonDef.SMALL_PEN_WIDTH * 0.5f)) {
            //最小X=落下点的坐标-画笔的宽度*0.5
            minX = x - (CommonDef.SMALL_PEN_WIDTH * 0.5f);
        }

        if (minY == 0 || minY > x - (CommonDef.SMALL_PEN_WIDTH * 0.5f)) {
            minY = y - (CommonDef.SMALL_PEN_WIDTH * 0.5f);
        }

    }

    public void touchMove(float x, float y) {
        if (x <= 0) {
            minX = 3;

        }
        if (y <= 0) {
            minY = 3;
        }

        float dx = Math.abs(x - mX);//绝对值
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            m_Path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            m_hasDrawn = true;
            mX = x;
            mY = y;
        }


        if (minX == 0 || minX > x - (CommonDef.SMALL_PEN_WIDTH * 0.5f)) {
            minX = x - (CommonDef.SMALL_PEN_WIDTH * 0.5f);
        }
        if (minY == 0 || minY > y - (CommonDef.SMALL_PEN_WIDTH * 0.5f)) {
            minY = y - (CommonDef.SMALL_PEN_WIDTH * 0.5f);
        }


        if (maxX == 0 || maxX < x + (CommonDef.SMALL_PEN_WIDTH * 0.5f)) {
            maxX = x + (CommonDef.SMALL_PEN_WIDTH * 0.5f);
        }
        if (maxY == 0 || maxY < y + (CommonDef.SMALL_PEN_WIDTH * 0.5f)) {
            maxY = y + (CommonDef.SMALL_PEN_WIDTH * 0.5f);
        }

    }

    public void touchUp(float x, float y) {
        // TODO Auto-generated method stub
        m_Path.lineTo(mX, mY);
        // commit the path to our offscreen
        m_Canvas.drawPath(m_Path, m_Paint);
        // kill this so we don't double draw
        m_Path = new Path();
        Paint newPaint = new Paint(m_Paint); // Clones the mPaint object
        paths.add(new Pair<>(m_Path, newPaint));


    }

    @Override
    public float getMaxX() {
        return maxX;
    }

    @Override
    public float getMaxY() {
        return maxY;
    }

    @Override
    public float getMinX() {
        return minX;
    }

    @Override
    public float getMinY() {
        return minY;
    }


}
