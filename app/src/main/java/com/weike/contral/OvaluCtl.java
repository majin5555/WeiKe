package com.weike.contral;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import com.weike.interfaces.ISketchpadDraw;


/**function:������Բ
 * @author:
   绘制椭圆
 */
public class OvaluCtl implements ISketchpadDraw {
	private Paint mPaint=new Paint();
    private boolean m_hasDrawn = false; 
    private RectF rectf=new RectF();
    public OvaluCtl(int penSize, int penColor)
    {
    	mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(penColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(penSize);//���û��ʴ�ϸ
    }
	
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		if (null != canvas)
        {
			canvas.drawOval(rectf, mPaint);
            Log.i("sada022", "Ovalʵ��");
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
		rectf.left=x;
		rectf.top=y;
		rectf.right=x;
		rectf.bottom=y;
	}
	public void touchMove(float x, float y) {
		// TODO Auto-generated method stub
		rectf.right=x;
		rectf.bottom=y;
		m_hasDrawn=true;//��ʾ�Ѿ�������
	}	
	public void touchUp(float x, float y) {
		// TODO Auto-generated method stub
		rectf.right=x;
		rectf.bottom=y;
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
