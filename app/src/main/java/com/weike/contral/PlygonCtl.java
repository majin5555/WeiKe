package com.weike.contral;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import com.weike.data.vertexDefine;
import com.weike.data.vertexStack;
import com.weike.interfaces.ISketchpadDraw;


/*function:
 * @author:
 * Date:
 *
 * 多边形
 */
public class PlygonCtl implements ISketchpadDraw {
	private        Path         mPath      =new Path();
	private        Paint        mPaint     =new Paint();
	private        boolean      m_hasDrawn = false;
	private static vertexDefine mPoint     = new vertexDefine();
	private static vertexDefine startPoint = new vertexDefine();
	private static vertexStack  pointStack = new vertexStack();
	private static int          countLine  = 0;
	//-------------------------------------------------------
	public PlygonCtl(int penSize, int penColor)
	{
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(penColor);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(penSize);//���û��ʴ�ϸ
	}
	public void cleanAll() {
		// TODO Auto-generated method stub
		mPath.reset();
	}	
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		if (null != canvas)
		{
			canvas.drawPath(mPath, mPaint);
			Log.i("sada022", "plygon02");
		}
	}	
	public boolean hasDraw() {
		// TODO Auto-generated method stub
		return m_hasDrawn;
		//return false;
	}	
	public void touchDown(float x, float y) {
		// TODO Auto-generated method stub
		mPath.reset();
		//�ж϶�ջ�Ƿ���Ϊ�գ���Ϊ����Ϊ�������ʼ��,�������ԱȽ϶�����Ƿ��գ�
		//����Ϊ���򵯳�Ԫ��
		if (pointStack.isEmpty()){
			mPoint.setPoint(x, y);
			startPoint.setPoint(mPoint.getX(), mPoint.getY());
		}
		else{
			//������һ�λ��Ƶ��յ㣬��Ϊ�˴λ��Ƶ�ʼ��
			mPoint = pointStack.pop();
			//���Ե�ǰ����λ����Ƿ��������򼴿ɻ�����һ�������
			if(mPoint.getX()>=startPoint.getX()-1 && mPoint.getX()<=startPoint.getX()+1
					&& mPoint.getY()>=startPoint.getY()-1 && mPoint.getY()<=startPoint.getY()+1){
				touchDown(x,y);			//���������һ�������
				Log.i("start","startPointX"+startPoint.getX()+","+"startPointY"+startPoint.getY());
				Log.i("end","mPointX"+mPoint.getX()+","+"mPointY"+mPoint.getY());
				return;
			}
		}
		mPath.moveTo(mPoint.getX(),mPoint.getY());
	}	
	public void touchMove(float x, float y) {
		//TODO Auto-generated method stub
		//�����ƶ����ĵ�����
		mPoint.setPoint(x, y);
		m_hasDrawn = true; //��ʾ�Ѿ�������
	}	
	public void touchUp(float x, float y) {
		// TODO Auto-generated method stub
		mPoint.setPoint(x, y);
		pointStack.push(mPoint);
		mPath.lineTo(mPoint.getX(), mPoint.getY());
		PlygonCtl.countLine++;
		// commit the path to our offscreen
		//mCanvas.drawPath(mPath, mPaint);
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

    //�Զ���ն����  -----ʷ�������
	public void lineDraw(Canvas canvas){
		canvas.drawLine(mPoint.getX(),mPoint.getY(),startPoint.getX(),startPoint.getY(), mPaint); 
		pointStack.pop();
	}	
	//���get����    2011-7-27
	public static vertexDefine getmPoint() {
		return mPoint;
	}
	public static vertexDefine getStartPoint() {
		return startPoint;
	}
	public static void setmPoint(float pointX,float pointY) {
		PlygonCtl.mPoint.setX(pointX);
		PlygonCtl.mPoint.setY(pointY);
	}
	//���get()��set()����    2011-7-28
	public static int getCountLine() {
		return countLine;
	}
	public static void setCountLine(int countLine) {
		PlygonCtl.countLine = countLine;
	}
	
}
