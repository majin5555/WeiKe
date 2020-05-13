package com.weike.contral;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.weike.data.CommonDef;
import com.weike.interfaces.ISketchpadDraw;


/*画圆*/
public class Circlectl implements ISketchpadDraw {
    private float minX, minY, maxX, maxY;//浮点行的最小、最大 X ，Y坐标
    private float radius = 0;//半径
    private float startx = 0;  //开始X
    private float starty = 0;  //开始Y
    private float endx = 0;  //结束X
    private float endy = 0; //结束Y
    private boolean m_hasDrawn = false;
    private Paint mPaint = new Paint();//画笔


    /*构造函数初始化数据*/
    public Circlectl(int penSize, int penColor) {
        mPaint.setAntiAlias(true);//防抗锯齿
        mPaint.setDither(true);//防抖动
        mPaint.setColor(penColor);//设置颜色
        mPaint.setStyle(Paint.Style.FILL);//风格是实心圆
        mPaint.setStrokeJoin(Paint.Join.ROUND);//设置线段连接处样式，取值有：Join.MITER（结合处为锐角）、Join.Round(结合处为圆弧)、Join.BEVEL(结合处为直线)
        mPaint.setStrokeCap(Paint.Cap.ROUND);//设置线冒样式，取值有Cap.ROUND(圆形线冒)、Cap.SQUARE(方形线冒)、Paint.Cap.BUTT(无线冒)
        mPaint.setStrokeWidth(penSize);//画笔尺寸
    }

    public void draw(Canvas canvas) {
        // TODO Auto-generated method stub
        if (null != canvas) { //画圆
            canvas.drawCircle((startx + endx) / 2, (starty + endy) / 2, radius, mPaint);

            // Log.i("YUAN", "Circle=============================》画圆------X:"+(startx+endx)/2+"---------Y:"+(starty+endy)/2+"-----------半径："+radius);
        }
    }

    public boolean hasDraw() {//是否是画的状态
        // TODO Auto-generated method stub
        return m_hasDrawn;
        //return false;
    }

    public void cleanAll() {
        // TODO Auto-generated method stub
        mPaint.reset();
    }

    //按下时          //传进来的X Y
    public void touchDown(float x, float y) {
        // TODO Auto-generated method stub
        startx = x;
        starty = y;
        endx = x;
        endy = y;
        radius = 0;
    }

    //移动时
    public void touchMove(float x, float y) {
        // TODO Auto-generated method stub
        endx = x;
        endy = y;
        //开始点的（X坐标-结束X点的坐标）的平方+开始点的（Y坐标-结束Y点的坐标）开平方=半径
        //半径
        radius = (float) ((Math.sqrt((x - startx) * (x - startx) + (y - starty) * (y - starty))) / 2);
        m_hasDrawn = true;//可画状态为true


    }

    //抬起时
    public void touchUp(float x, float y) {
        // TODO Auto-generated method stub
        endx = x;
        endy = y;
        minX = (startx + endx) / 2 - radius - (CommonDef.SMALL_PEN_WIDTH * 0.5f);
        minY = (starty + endy) / 2 - radius - (CommonDef.SMALL_PEN_WIDTH * 0.5f);
        maxX = (startx + endx) / 2 + radius + (CommonDef.SMALL_PEN_WIDTH * 0.5f);
        maxY = (starty + endy) / 2 + radius + (CommonDef.SMALL_PEN_WIDTH * 0.5f);

    }

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
}
