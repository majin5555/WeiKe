package com.weike.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.weike.R;
import com.weike.actiity.SketchpadMainActivity;
import com.weike.bean.Screentshot;

import androidx.annotation.Nullable;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;


/**
 * Created by lenovo on 2018/1/16.
 */

@SuppressLint("AppCompatCustomView")
public class MoveImagview extends RelativeLayout implements View.OnTouchListener, View.OnClickListener {

    private static final String TAG = "MoveImagview";

    private Context   context;
    private int       startX;
    private int       startY;
    private int       l;
    private int       r;
    private int       t;
    private int       b;
    private ImageView mImg;


    /*构造*/
    public MoveImagview(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

    }

    /*构造*/
    public MoveImagview(Context context) {
        super(context);
        this.context = context;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);

        this.setLayoutParams(params);
        addScreenView();
        addDeleteView();

    }

    private void addScreenView() {
        mImg = new ImageView(context);
        mImg.setBackgroundColor(context.getResources().getColor(R.color.app_style));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(400, 300);
        params.setMargins(10, 10, 10, 10);
        mImg.setLayoutParams(params);
       // mImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        mImg.setId(1);
        this.addView(mImg);
        mImg.setOnTouchListener(this);
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
    }

    @Override
    protected void onDraw(Canvas canvas) {

    }


    @Override
    public void onClick(View v) {
       // Toast.makeText(context, "删除。。。", Toast.LENGTH_SHORT).show();
        ((SketchpadMainActivity) context).getSketchPicContentRoot().removeView(MoveImagview.this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //  this.bringToFront();
                // 初始化起点坐标
                startX = (int) event.getRawX();
                startY = (int) event.getRawY();

                break;
            case MotionEvent.ACTION_POINTER_DOWN:

                break;
            case MotionEvent.ACTION_MOVE:
                //  Log.d(TAG, " MoveImagview  startX  " + startX + "  startY " + startY);

                int endX = (int) event.getRawX();
                int endY = (int) event.getRawY();
                // 计算移动偏移量
                int dx = endX - startX;
                int dy = endY - startY;
                //                // 更新左上右下距离
                l = MoveImagview.this.getLeft() + dx;
                r = MoveImagview.this.getRight() + dx;
                t = MoveImagview.this.getTop() + dy;
                b = MoveImagview.this.getBottom() + dy;

                //  move(v, dx, dy);
                getParent().requestDisallowInterceptTouchEvent(true);
                layout(l, t, r, b);

                // 初始化起点坐标
                startX = (int) event.getRawX();
                startY = (int) event.getRawY();
                Log.d(TAG, " mImg    " + this.getLeft());
                //  Log.d(TAG, " mImg  getRight " + mImg.getLeft());
                //  Log.d(TAG, " mImg  mImg.getTop() " + mImg.getTop());

                break;
            //TODO 有问题等待处理
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:

            default:
                break;
        }
        return true;
    }


    /**
     * 取两点的距离
     */
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
