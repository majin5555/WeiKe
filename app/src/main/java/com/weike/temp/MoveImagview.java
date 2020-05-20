package com.weike.temp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
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
        LayoutParams params = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        // params.setMargins(10, 10, 10, 10);
        this.setLayoutParams(params);
        addScreenView();
        addDeleteView();

    }

    private void addScreenView() {
        //mImg = new MyImageView(context);
        mImg = new ImageView(context);
       // mImg.setBackgroundColor(context.getResources().getColor(R.color.app_style));
        //RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(400, 300);
        //mImg.setLayoutParams(params);
        //mImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mImg.setId(1);
        this.addView(mImg);
        mImg.setOnTouchListener(this);
    }

    /**
     * 小X
     */
    private void addDeleteView() {
        LayoutParams paramsT = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
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
        //设置ImageView的缩放类型
       // mImg.setScaleType(ImageView.ScaleType.MATRIX);
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
            case MotionEvent.ACTION_DOWN://单指触碰
                //                //this.bringToFront();
                //初始化起点坐标
                startX = (int) event.getRawX();
                startY = (int) event.getRawY();
                //起始矩阵先获取ImageView的当前状态

                break;
            case MotionEvent.ACTION_POINTER_DOWN://双指触碰

                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, " MoveImagview  startX  " + startX + "  startY " + startY);
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

                getParent().requestDisallowInterceptTouchEvent(true);
                layout(l, t, r, b);
                // 初始化起点坐标
                startX = (int) event.getRawX();
                startY = (int) event.getRawY();


                break;
            //TODO 有问题等待处理
            case MotionEvent.ACTION_UP://单指离开
            case MotionEvent.ACTION_POINTER_UP://双指离开

            default:
                break;
        }

        return true;
    }

    /**
     * 取两点的距离
     */
    //获取距离
    private float getDistance(MotionEvent event) {//获取两点间距离
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

}
