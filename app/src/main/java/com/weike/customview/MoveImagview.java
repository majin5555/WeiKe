package com.weike.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.weike.R;

import androidx.annotation.Nullable;


/**
 * Created by lenovo on 2018/1/16.
 */

@SuppressLint("AppCompatCustomView")
public class MoveImagview extends RelativeLayout {
    private static final String TAG = "MoveImagview";

    private Context context;
    private int     startX;
    private int     startY;
    private int     l;
    private int     r;
    private int     t;
    private int     b;


    /*构造*/
    public MoveImagview(Context context, @
            Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /*构造*/
    public MoveImagview(Context context) {
        super(context);
        this.context = context;

        //        RelativeLayout.LayoutParams paramsT = new RelativeLayout.LayoutParams(300, 200);
        //        this.setLayoutParams(paramsT);
        this.setBackgroundColor(context.getResources().getColor(R.color.app_style));
        //AddMaskView();

    }


    /*添加遮罩*/
    private void AddMaskView() {
        /*正确答案 添加遮罩*/
        LayoutParams paramsT = new LayoutParams(100, 100);
        paramsT.addRule(RelativeLayout.CENTER_IN_PARENT);
        ImageView imageViewT = new ImageView(context);
        imageViewT.setLayoutParams(paramsT);
        imageViewT.setBackgroundResource(R.drawable.close_icon2x);
        // this.addView(imageViewT);
    }

    ImageView imageViewT;

    /*添加截图*/
    public void AddSkechtpadView(Bitmap bitmap) {
        /*正确答案 添加遮罩*/
        LayoutParams paramsT = new LayoutParams(300, 200);
        paramsT.addRule(RelativeLayout.CENTER_IN_PARENT);
        imageViewT = new ImageView(context);
        imageViewT.setLayoutParams(paramsT);
        imageViewT.setImageBitmap(bitmap);
        this.addView(imageViewT);
    }

    //  private Bitmap  touchImg   = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
    //    public void setTouchImg(Bitmap canvasSnapshot) {
    //      //  touchImg = canvasSnapshot;
    //    }
    //    public Bitmap getTouchImg() {
    //     //   return touchImg;
    //    }

    private Matrix  matrix     = new Matrix();
    private Matrix  matrix1    = new Matrix();
    private Matrix  saveMatrix = new Matrix();
    private float[] x          = new float[4];
    private float[] y          = new float[4];
    private int     screenWidth, screenHeight;
    public              int     mode    = 0;
    private             float   x_down  = 0;
    private             float   y_down  = 0;
    final public static int     DRAG    = 1;
    final public static int     ZOOM    = 2;
    private             float   initDis = 1f;
    private             PointF  mid     = new PointF();
    private             boolean flag    = false;


    @Override
    protected void onDraw(Canvas canvas) {

        //        canvas.save();
        //        // 根据 matrix 来重绘新的view
        //        if (touchImg != null)
        //            canvas.drawBitmap(touchImg, matrix, null);
        //        canvas.restore();
    }

    private boolean isFirstDown;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.bringToFront();
                // 初始化起点坐标
                startX = (int) event.getRawX();
                startY = (int) event.getRawY();

                break;
            case MotionEvent.ACTION_POINTER_DOWN:

                //    saveMatrix.set(matrix);
                // 初始的两个触摸点间的距离
                //   initDis = spacing(event);
                // 设置为缩放模式
                //  mode = ZOOM;
                // 多点触摸的时候 计算出中间点的坐标
                // midPoint(mid, event);
                break;

            case MotionEvent.ACTION_MOVE:
                //  Log.d(TAG, " MoveImagview  startX  " + startX + "  startY " + startY);

                int endX = (int) event.getRawX();
                int endY = (int) event.getRawY();
                // 计算移动偏移量
                int dx = endX - startX;
                int dy = endY - startY;
                //                // 更新左上右下距离
                l = this.getLeft() + dx;
                r = this.getRight() + dx;
                t = this.getTop() + dy;
                b = this.getBottom() + dy;
                //                /*获取拖动的中心点*/
                //                float centerPointX = (((float) 1 / (float) 2) * this.getWidth()) + l;
                //                float centerPointY = (((float) 1 / (float) 2) * getHeight()) + t;

                // getParent().requestDisallowInterceptTouchEvent(true);
                layout(l, t, r, b);
                Log.d(TAG, "  ");

                // 重新初始化起点坐标
                startX = (int) event.getRawX();
                startY = (int) event.getRawY();

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                //TODO 有问题等待处理
                //                ViewGroup.LayoutParams layoutParams = getLayoutParams();
                //                layoutParams.width = startX + imageViewT.getWidth();
                //                layoutParams.height = startY + imageViewT.getHeight();
                // setLayoutParams(layoutParams);
                break;
            default:
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
}
