package com.weike.bean;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * @author: mj
 * @date: 2020/5/17$
 * @desc: 屏幕截图
 */
public class Screentshot implements Serializable {

    private float  minX;
    private float  minY;
    private Bitmap bitmap;

    public Screentshot(float minX, float minY, Bitmap bitmap) {
        this.minX = minX;
        this.minY = minY;
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public float getMinX() {
        return minX;
    }

    public void setMinX(float minX) {
        this.minX = minX;
    }

    public float getMinY() {
        return minY;
    }

    public void setMinY(float minY) {
        this.minY = minY;
    }
}
