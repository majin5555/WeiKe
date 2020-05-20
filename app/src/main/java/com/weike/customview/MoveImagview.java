package com.weike.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.weike.R;
import com.weike.actiity.SketchpadMainActivity;
import com.weike.bean.Screentshot;

import androidx.annotation.Nullable;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;


/**
 * Created by lenovo on 2018/1/16.
 */

@SuppressLint("AppCompatCustomView")
public class MoveImagview extends RelativeLayout implements   View.OnClickListener, ScaleGestureDetector.OnScaleGestureListener {

    private static final String TAG = "MoveImagview";

    private Context context;

    private int          l;
    private int          r;
    private int          t;
    private int          b;
    private ImageView    mImg;
    private LayoutParams paramsMovieView;

    /******************************************************************************/

  

 
    private float scale = 1.0f;


    // Where the finger first  touches the screen
    private float startX = 0f;
    private float startY = 0f;

    private static final float MIN_ZOOM = 1.0f;
    private static final float MAX_ZOOM = 4.0f;
    private Integer mLeft, mTop, mRight, mBottom;
    private int centerX, centerY;
    private float mLastScale = 1.0f;
    private float totleScale = 1.0f;

    private ScaleGestureDetector scaleDetector;
    private enum MODE {
        ZOOM, DRAG, NONE
    }
    private MODE mode;
    private float lastX, lastY;
 

    /******************************************************************************/

    /*构造*/
    public MoveImagview(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /*构造*/
    public MoveImagview(Context context) {
        super(context);

        this.context = context;
        scaleDetector = new ScaleGestureDetector(context, MoveImagview.this);

        paramsMovieView = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        this.setLayoutParams(paramsMovieView);
        addScreenView();
        addDeleteView();
    }

    private void addScreenView() {
        mImg = new ImageView(context);
        mImg.setId(1);
        this.addView(mImg);
      //  this.setOnTouchListener(this);

    }

    /**
     * 小X
     */
    private void addDeleteView() {
        RelativeLayout.LayoutParams paramsT = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        paramsT.addRule(RelativeLayout.RIGHT_OF, mImg.getId());
        paramsT.addRule(RelativeLayout.ALIGN_RIGHT, 5);

        ImageView imageViewT = new ImageView(context);
        imageViewT.setLayoutParams(paramsT);
        imageViewT.setImageResource(R.drawable.close_icon2x);
        this.addView(imageViewT);
        imageViewT.setOnClickListener(this);
    }

    /**
     * 添加截图
     */
    public void addSkechtpadView(Screentshot screentshot) {
        mImg.setImageBitmap(screentshot.getBitmap());
        setX(screentshot.getMinX());
        setY(screentshot.getMinY());
        mImg.setScaleType(ImageView.ScaleType.MATRIX);
    }

    @Override
    public void onClick(View v) {
        ((SketchpadMainActivity) context).getSketchPicContentRoot().removeView(MoveImagview.this);
    }

    @Override
    protected void onDraw(Canvas canvas) {

    }


    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scaleFactor = detector.getScaleFactor();
        // Log.d(TAG, "onScale" + scaleFactor);
        Log.d(TAG, "onScale: --------------------");


        Log.d(TAG, "onScale: -------------------- scale " + scale);

        return true;
    }

    Matrix scaleMatrix = new Matrix();

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        Log.d(TAG, "onScaleBegin: -----------------------1");

        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        Log.d(TAG, "onScaleEnd: -----------------------1");
        mode = MODE.NONE;
        scale = 1;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() > 1) {
            return scaleDetector.onTouchEvent(event);
        }

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = MODE.DRAG;
                bringToFront();
                //初始化起点坐标
                lastX = event.getRawX();
                lastY = event.getRawY();

            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == MODE.DRAG) {
                    int endX = (int) event.getRawX();//距离屏幕
                    int endY = (int) event.getRawY();
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
                    //计算当前的左上角坐标
                    float left = layoutParams.leftMargin + endX - lastX;
                    float top = layoutParams.topMargin + endY - lastY;
                    //设置坐标
                    layoutParams.leftMargin = (int) left;
                    layoutParams.topMargin = (int) top;
                    setLayoutParams(layoutParams);
                    lastX = endX;
                    lastY = endY;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                mode = MODE.NONE;
                break;
        }

        return true;
    }
    /**
     * 两点的距离
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }


    /**
     * 两点的中点
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
}
