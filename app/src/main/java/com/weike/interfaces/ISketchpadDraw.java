package com.weike.interfaces;

import android.graphics.Canvas;

public interface ISketchpadDraw {

	public void draw(Canvas canvas);
	public boolean hasDraw();
	public void cleanAll();
	public void touchDown(float x, float y);
	public void touchMove(float x, float y);
	public void touchUp(float x, float y);

    //定义接口获取最大最小 XY
    public float getMaxX();
    public float getMaxY();
    public float getMinX();
    public float getMinY();

}
