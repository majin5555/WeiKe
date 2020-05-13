package com.weike.contral;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.weike.interfaces.ISketchpadDraw;

import java.util.Random;



/*绘制任意多边*/
public class Spraygun implements ISketchpadDraw {
	public float touchX;
	public float touchY;
	public int m_penSize;
	public int m_strokeColor;
	private Path path;
	private Paint paint ;
	private boolean m_hasDrawn = false;
	public Spraygun( int m_penSize,int m_strokeColor) {
		super();
		path = new Path();
		this.m_penSize = m_penSize;
		this.m_strokeColor = m_strokeColor;
		path.moveTo(touchX, touchY);
		path.lineTo(touchX, touchY);
		 paint = new Paint();
		// �����........
		paint.setAntiAlias(true);
		// ������.......
		paint.setDither(true);
		paint.setColor(m_strokeColor);
		paint.setStrokeWidth(1);
	}

	public void draw(Canvas canvas) {
		canvas.drawPath(path, paint);
		randomPoint(canvas, paint, touchX, touchY, m_penSize);
	}

	public boolean hasDraw() {
		return m_hasDrawn;
	}

	public void cleanAll() {
		path.reset();
	}

	public void touchDown(float x, float y) {
		touchX = x;
		touchY = y;
	}

	public void touchMove(float mx, float my) {
		touchX = mx;
		touchY = my;
		path.moveTo(mx, my);
		m_hasDrawn = true;
	}

	public void touchUp(float x, float y) {
		touchX = x;
		touchY = y;
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

    public void randomPoint(Canvas canvas, Paint paint, float x, float y,
			int size) {
		Random random = new Random();
		for (int i = 0; i < 160; i++) {
			float tempX1 = random.nextFloat();
			float tempY1 = random.nextFloat();
			int tempX2 = random.nextInt(size * 2);
			int tempY2 = random.nextInt(size * 2);
			float tempX = tempX1 + tempX2;
			float tempY = tempY1 + tempY2;
			if (i % 4 == 0) {
				tempX = (tempX1 + tempX2) * (-1);
				tempY = (tempY1 + tempY2) * (-1);
			} else if (i % 4 == 1) {
				tempY = (tempY1 + tempY2) * (-1);
			} else if (i % 4 == 2) {
				tempX = (tempX1 + tempX2) * (-1);
			}
			/*
			 * �ж���ȡ�㵽�����֮��ľ��룬ֻ���ھ���С�ڰ뾶ʱ�Ż����ĵ� *Բ�İ뾶ȡ size*10
			 */
			double sqrt = Math.sqrt(tempX * tempX + tempY * tempY);
			if (sqrt < size * 2) {
				// ����
				canvas.drawPoint(tempX + x - size * 2, tempY + y - size * 2,
						paint);
				i++;
			}
		}
	}

}
