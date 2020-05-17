package com.weike.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.weike.R;

/**
 * @author: mj
 * @date: 2020/5/16$
 * @desc:
 */

@SuppressLint("AppCompatCustomView")
public class ScaleView extends ImageView {

    final public static int DRAG = 1;
    final public static int ZOOM = 2;

    public int mode = 0;

    private Matrix matrix     = new Matrix();
    private Matrix matrix1    = new Matrix();
    private Matrix saveMatrix = new Matrix();

    private float x_down = 0;
    private float y_down = 0;

    private Bitmap touchImg = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

    private PointF mid = new PointF();


    private float initDis = 1f;

    private int screenWidth, screenHeight;

    private float[] x = new float[4];
    private float[] y = new float[4];

    private boolean flag = false;

    public ScaleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ScaleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScaleView(Context context) {
        super(context);
        touchImg = BitmapFactory.decodeResource(getResources(), R.drawable.zhizhen_2x);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        matrix = new Matrix();
        // this.setScaleType(ScaleType.MATRIX);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.save();
        // 根据 matrix 来重绘新的view
        canvas.drawBitmap(touchImg, matrix, null);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        // 多点触摸的时候 必须加上MotionEvent.ACTION_MASK
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                saveMatrix.set(matrix);
                x_down = event.getX();
                y_down = event.getY();
                // 初始为drag模式
                mode = DRAG;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                saveMatrix.set(matrix);
                // 初始的两个触摸点间的距离
                initDis = spacing(event);
                // 设置为缩放模式
                mode = ZOOM;
                // 多点触摸的时候 计算出中间点的坐标
                midPoint(mid, event);
                break;

            case MotionEvent.ACTION_MOVE:

                // drag模式
                if (mode == DRAG) {
                    // 设置当前的 matrix
                    matrix1.set(saveMatrix);
                    // 平移 当前坐标减去初始坐标 移动的距离
                    matrix1.postTranslate(event.getX() - x_down, event.getY()
                            - y_down);// 平移
                    // 判断达到移动标准
                    flag = checkMatrix(matrix1);
                    if (flag) {
                        // 设置matrix
                        matrix.set(matrix1);

                        // 调用ondraw重绘
                        invalidate();
                    }
                } else if (mode == ZOOM) {
                    matrix1.set(saveMatrix);
                    float newDis = spacing(event);
                    // 计算出缩放比例
                    float scale = newDis / initDis;

                    // 以mid为中心进行缩放
                    matrix1.postScale(scale, scale, mid.x, mid.y);
                    flag = checkMatrix(matrix1);
                    if (flag) {
                        matrix.set(matrix1);
                        invalidate();
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = 0;
                break;
        }

        return true;

    }

    //取两点的距离
    private float spacing(MotionEvent event) {
        try {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return (float) Math.sqrt(x * x + y * y);
        } catch (IllegalArgumentException ex) {
            Log.v("TAG", ex.getLocalizedMessage());
            return 0;
        }
    }

    //取两点的中点
    private void midPoint(PointF point, MotionEvent event) {
        try {
            float x = event.getX(0) + event.getX(1);
            float y = event.getY(0) + event.getY(1);
            point.set(x / 2, y / 2);
        } catch (IllegalArgumentException ex) {

            //这个异常是android自带的，网上清一色的这么说。。。。
            Log.v("TAG", ex.getLocalizedMessage());
        }
    }

    private boolean checkMatrix(Matrix m) {

        GetFour(m);

        // 出界判断
        //view的右边缘x坐标小于屏幕宽度的1/3的时候，
        // view左边缘大于屏幕款短的2/3的时候
        //view的下边缘在屏幕1/3上的时候
        //view的上边缘在屏幕2/3下的时候
        if ((x[0] < screenWidth / 3 && x[1] < screenWidth / 3
                && x[2] < screenWidth / 3 && x[3] < screenWidth / 3)
                || (x[0] > screenWidth * 2 / 3 && x[1] > screenWidth * 2 / 3
                && x[2] > screenWidth * 2 / 3 && x[3] > screenWidth * 2 / 3)
                || (y[0] < screenHeight / 3 && y[1] < screenHeight / 3
                && y[2] < screenHeight / 3 && y[3] < screenHeight / 3)
                || (y[0] > screenHeight * 2 / 3 && y[1] > screenHeight * 2 / 3
                && y[2] > screenHeight * 2 / 3 && y[3] > screenHeight * 2 / 3)) {
            return true;
        }
        // 图片现宽度
        double width = Math.sqrt((x[0] - x[1]) * (x[0] - x[1]) + (y[0] - y[1])
                * (y[0] - y[1]));
        // 缩放比率判断 宽度打雨3倍屏宽，或者小于1/3屏宽
        if (width < screenWidth / 3 || width > screenWidth * 3) {
            return true;
        }
        return false;

        // if ((x[0] >= 0 && x[1] >= 0 && x[2] >= 0 && x[3] >= 0)
        // && (x[0] <= screenWidth && x[1] <= screenWidth
        // && x[2] <= screenWidth && x[3] <= screenWidth)
        // && (y[0] >= 0 && y[1] >= 0 && y[2] >= 0 && y[3] >= 0) && (y[0] <=
        // screenHeight
        // && y[1] <= screenHeight && y[2] <= screenHeight && y[3] <=
        // screenHeight)) {
        //
        // return true;
        // }
        //
        // return false;
    }

    private void GetFour(Matrix matrix) {
        float[] f = new float[9];
        matrix.getValues(f);
        //        StringBuffer sb = new StringBuffer();
        //        for(float ff : f)
        //        {
        //            sb.append(ff+"  ");
        //        }
        // 图片4个顶点的坐标
        //矩阵  9     MSCALE_X 缩放的， MSKEW_X 倾斜的    。MTRANS_X 平移的
        x[0] = f[Matrix.MSCALE_X] * 0 + f[Matrix.MSKEW_X] * 0
                + f[Matrix.MTRANS_X];
        y[0] = f[Matrix.MSKEW_Y] * 0 + f[Matrix.MSCALE_Y] * 0
                + f[Matrix.MTRANS_Y];
        x[1] = f[Matrix.MSCALE_X] * touchImg.getWidth() + f[Matrix.MSKEW_X] * 0
                + f[Matrix.MTRANS_X];
        y[1] = f[Matrix.MSKEW_Y] * touchImg.getWidth() + f[Matrix.MSCALE_Y] * 0
                + f[Matrix.MTRANS_Y];
        x[2] = f[Matrix.MSCALE_X] * 0 + f[Matrix.MSKEW_X]
                * touchImg.getHeight() + f[Matrix.MTRANS_X];
        y[2] = f[Matrix.MSKEW_Y] * 0 + f[Matrix.MSCALE_Y]
                * touchImg.getHeight() + f[Matrix.MTRANS_Y];
        x[3] = f[Matrix.MSCALE_X] * touchImg.getWidth() + f[Matrix.MSKEW_X]
                * touchImg.getHeight() + f[Matrix.MTRANS_X];
        y[3] = f[Matrix.MSKEW_Y] * touchImg.getWidth() + f[Matrix.MSCALE_Y]
                * touchImg.getHeight() + f[Matrix.MTRANS_Y];
    }

}