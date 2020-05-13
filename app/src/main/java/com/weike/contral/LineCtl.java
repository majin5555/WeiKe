package com.weike.contral;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.weike.interfaces.ISketchpadDraw;


/*
* 划线
*
* **/
public class LineCtl implements ISketchpadDraw {

	private Paint mPaint=new Paint();
    private boolean m_hasDrawn = false;  
    private float startx = 0;  
    private float starty = 0;  
    private float endx = 0;  
    private float endy = 0;  
    public LineCtl(int penSize, int penColor)
    {
    	mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(penColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(penSize);
    }
	
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		if (null != canvas)
        {
			canvas.drawLine(startx,starty,endx,endy, mPaint); 
        }
	}
	public boolean hasDraw() {
		// TODO Auto-generated method stub
		return m_hasDrawn;
		//return false;
	}
	public void cleanAll() {
		// TODO Auto-generated method stub
	}	
	public void touchDown(float x, float y) {
		// TODO Auto-generated method stub
		startx=x;
		starty=y;
		endx=x;
		endy=y;
	}
	public void touchMove(float x, float y) {
		// TODO Auto-generated method stub
		endx=x;
		endy=y;
		m_hasDrawn=true;
	}
	public void touchUp(float x, float y) {
		// TODO Auto-generated method stub
		endx=x;
		endy=y;
	}

    @Override
    public float getMaxX() {
        return 0;
    }

    @Override
    public float getMaxY() {
        return 0;
    }

    @Override
    public float getMinX() {
        return 0;
    }

    @Override
    public float getMinY() {
        return 0;
    }

}
