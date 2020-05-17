package com.weike.customview;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.weike.bean.Screentshot;
import com.weike.contral.Circlectl;
import com.weike.contral.EraserCtl;
import com.weike.contral.LineCtl;
import com.weike.contral.OvaluCtl;
import com.weike.contral.PenuCtl;
import com.weike.contral.PlygonCtl;
import com.weike.contral.RectuCtl;
import com.weike.contral.SketchpadViewContral;
import com.weike.contral.Spraygun;
import com.weike.data.CommonDef;
import com.weike.interfaces.ISketchpadDraw;
import com.weike.interfaces.IUndoRedoCommand;

import androidx.annotation.Nullable;


/**
 * @author: mj
 * @date: 2020/5/12$
 * @desc: 自定义画板View
 */

public class SketchpadView extends View implements IUndoRedoCommand {
    private static final String TAG = "SketPadView";
    /***************************************************************/

    /**
     * 定义当前View的状态
     */
    public enum MODE_SKETCHPAD {
        PEN, EASER, HANLER, PONIT
    }

    /** 默认模式 */
    public MODE_SKETCHPAD mode_sketchpad = MODE_SKETCHPAD.PEN;

    /** 设置画板模式 */
    public void setSketchpadViewMode(MODE_SKETCHPAD mode) {
        mode_sketchpad = mode;
    }

    /***************************************************************/


    /**
     * 枚举类
     * 模式 NONE：无   NOMAL 正常可画
     */
    public enum MODE_PEN {
        NONE, NOMAL
    }

    /**
     * 获取铅笔模式
     */
    public MODE_PEN modePen = MODE_PEN.NOMAL;// 默认模式


    private int widthSize;//画板的宽
    private int heightSize;//画板的高

    public int getWidthSize() {
        return widthSize;
    }

    public int getHeightSize() {
        return heightSize;
    }

    private Bitmap   bitmap;//整个画板显示的位图
    private Paint    paint;//画笔
    private Canvas   canvas = new Canvas();//画板的画布;//画板的画布
    private float    xTouch = 0;//移动的位置
    private float    yTouch = 0;
    private Activity context;

    public SketchpadView(Context context) {
        super(context);
        this.context = (Activity) context;
    }


    public void setSketchpadViewContral(SketchpadViewContral sketchpadViewContral) {
        /**初始化画笔*/
        paint = sketchpadViewContral.getPaint();
        initData();

    }

    public SketchpadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SketchpadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private float minX, minY, maxX, maxY;//浮点行的最小X ，Y坐标

    //更新当前的最大最小X Y(传入发的是当前坐标X Y)
    private void updateMaxValue(float x, float y) {

        if (this.maxX < x) {
            this.maxX = x;
        }
        if (this.maxY < y) {
            this.maxY = y;
        }

        if (this.minX > x) {
            this.minX = x;
        }
        if (this.minY > y) {
            this.minY = y;
        }


    }

    /** 获取切图（自己操作在屏幕上操作的） */
    public Screentshot getCanvasSnapshot() {

        if (minX == 0 || minY == 0 || maxX == 0 || maxY == 0) {//或||有一个成立则成立 全为false 则为false
            return null;
        }
        setDrawingCacheEnabled(false);//销毁原来的cache(可以清理开启时的申请的内存)
        setDrawingCacheEnabled(true);//开启cache
        buildDrawingCache(true);//生成cache
        Bitmap bmp = getDrawingCache(true);//获得可画视图的缓存

        if (bmp == null) {
        }
        ;//小手截屏代码
        return new Screentshot(minX, minY, duplicateBitmap(bmp, minX, minY, maxX, maxY));
    }


    //    左minX        上minY        右maxX    下 maxY
    public Bitmap duplicateBitmap(Bitmap bmpSrc, float left, float top, float right, float bottom) {
        if (null == bmpSrc) {
            return null;
        }

        Bitmap bmpDest = null;
        //当bmpDest=null时
        //        if (left == 0 && top == 0 && right == 0 && bottom == 0) {//全都等于0的情况下
        //            bmpDest = Bitmap.createBitmap(SketchpadView.BITMAP_WIDTH, SketchpadView.BITMAP_HEIGHT, Bitmap.Config.RGB_565);
        //        }

        /*x：剪裁x方向的起始位置;    y：剪裁y方向的起始位置;    width：剪裁的宽度;   height：剪裁的高度;*/
        //   else {
        /**2016.12.20
         * Auther:majin
         * 修改在超出边界后报错问题
         *
         * */
        float r_l = right - left;
        float b_t = bottom - top;

        if (left < 0) {
            left = 0;
        }
        if (top < 0) {
            top = 0;
        }
        if (left + r_l > bmpSrc.getWidth()) {
            r_l = bmpSrc.getWidth() - left;
        }
        if (top + b_t > bmpSrc.getHeight()) {
            b_t = bmpSrc.getHeight() - top;
        }
        bmpDest = Bitmap.createBitmap(bmpSrc, (int) left, (int) top, (int) (r_l), (int) (b_t));
        // }

        if (bmpDest != null) {
            Canvas canvas = new Canvas(bmpDest);
            canvas.save();
            canvas.clipRect((int) left, (int) top, (int) right, (int) bottom);
            canvas.restore();
        }

        return bmpDest;
    }

    private void initData() {
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(7);
        //设置是否使用抗锯齿功能，抗锯齿功能会消耗较大资源，绘制图形的速度会减慢
        paint.setAntiAlias(true);
        //设置是否使用图像抖动处理，会使图像颜色更加平滑饱满，更加清晰
        paint.setDither(true);
        setClickable(true);//设置为可点击才能获取到MotionEvent.ACTION_MOVE

    }


    private void initBitmap() {
        if (null != bitmap) {
            bitmap.recycle();
        }
        bitmap = Bitmap.createBitmap(widthSize, heightSize, Bitmap.Config.RGB_565);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        canvas.setBitmap(bitmap);
        canvas.drawRect(0, 0, bitmap.getWidth(), bitmap.getHeight(), paint);
    }


    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mode_sketchpad == MODE_SKETCHPAD.PEN || mode_sketchpad == MODE_SKETCHPAD.EASER) {
            canvas.drawBitmap(bitmap, 0, 0, paint);
        } else if (mode_sketchpad == MODE_SKETCHPAD.HANLER) {
            //  canvas.drawBitmap(touchImg, 0, 0, paint);
        }
    }

    private ISketchpadDraw m_curTool = null;   //接口（绘制各种图形时都继承了接口）

    public static final int STROKE_PEN      = 1;
    public static final int STROKE_ERASER   = 2;
    public static final int STROKE_PLYGON   = 3;
    public static final int STROKE_RECT     = 4;
    public static final int STROKE_CIRCLE   = 5;
    public static final int STROKE_OVAL     = 6;
    public static final int STROKE_LINE     = 7;
    public static final int STROKE_SPRAYGUN = 8;
    public static final int STROKE_PAINTPOT = 9;
    private             int m_strokeType    = STROKE_PEN;   //  画笔状态

    private static int m_strokeColor = Color.RED;   //画笔颜色红色
    private static int m_penSize     = CommonDef.SMALL_PEN_WIDTH;// 画笔尺寸（默认为6）
    private static int m_eraserSize  = CommonDef.LARGE_ERASER_WIDTH;   //橡皮默认宽度


    //画图形
    public void setStrokeType(int type) {
        //, int color
        switch (type) {
            case STROKE_PEN://铅笔 12
                m_curTool = new PenuCtl(m_penSize, m_strokeColor);//实现了ISketchpadDraw的接口
                break;

            case STROKE_ERASER://橡皮 2
                m_curTool = new EraserCtl(m_eraserSize);
                break;
            case STROKE_PLYGON:  //多边形  10

                //绘制任意多边形
                m_curTool = new PlygonCtl(m_penSize, m_strokeColor);

                break;
            case STROKE_RECT:   //绘制矩形(正常)  9
                m_curTool = new RectuCtl(m_penSize, m_strokeColor, 1);
                break;

            case 13:            //绘制矩形（圆角矩形）13
                m_curTool = new RectuCtl(m_penSize, m_strokeColor, 2);
                break;

            case STROKE_CIRCLE:  //绘制圆形  8
                m_curTool = new Circlectl(m_penSize, m_strokeColor);
                break;
            case STROKE_OVAL:    //绘制椭圆   7
                m_curTool = new OvaluCtl(m_penSize, m_strokeColor);
                break;
            case STROKE_LINE:    //划线 6
                m_curTool = new LineCtl(m_penSize, m_strokeColor);
                break;
            case STROKE_SPRAYGUN:  //绘制任意多边形 5
                m_curTool = new Spraygun(m_penSize, m_strokeColor);
                break;
        }

        m_strokeType = type;//绘制完之后还是画笔状态
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //   Log.d(TAG, "  mode_sketchpad  " + mode_sketchpad);
        if (mode_sketchpad == MODE_SKETCHPAD.PEN || mode_sketchpad == MODE_SKETCHPAD.EASER) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                   // ClearMempry.ClearMempry(this.context);//清理内存
                  //  DataCleanManager.cleanInternalCache(this.context);
                    setStrokeType(m_strokeType);//绘制图形如果是画笔状态
                    minX = xTouch = event.getX();
                    minY = yTouch = event.getY();
                    //Log.d(TAG, "ACTION_DOWN   xTouch   " + xTouch + "  yTouch " + yTouch);
                    this.updateMaxValue(xTouch, yTouch);
                    break;
                case MotionEvent.ACTION_MOVE:
                    canvas.drawLine(xTouch, yTouch, event.getX(), event.getY(), paint);
                    xTouch = event.getX();
                    yTouch = event.getY();
                    this.updateMaxValue(xTouch, yTouch);
                    //  Log.d(TAG, "minX  " + minX + " minY " + minY + "  maxX " + maxX + "  maxY" + maxY);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    this.updateMaxValue(event.getX(), event.getY());
                    break;
            }
        } else if (mode_sketchpad == MODE_SKETCHPAD.HANLER) {
            Log.d(TAG, "SketchpadView  不处理---------------1 ");


            return false;
            // invalidate();
        } else if (mode_sketchpad == MODE_SKETCHPAD.PONIT) {

            // invalidate();
            return false;
        }


        return true;
    }


    @Override
    public void pageDataRedo() {

    }


    @Override
    public void pageDataUndo() {

    }

    /**
     * 获取橡皮
     */
    public void setEraser() {
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(50);
    }

    /**
     * 清除画板
     */
    public void clear() {
        if (null != bitmap) {
            bitmap.recycle();
        }
    }

    public void setTagPenStete(MODE_PEN modePen) {
        //  this.modePen = modePen;
    }

    public void setPen(int type) {
        paint.setStrokeWidth(7);
        paint.setColor(type);
        switch (type) {


            default:
                break;
        }
    }


    /***********************************************************************/
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int minimumWidth = getSuggestedMinimumWidth();
        final int minimumHeight = getSuggestedMinimumHeight();
        //   Log.d(TAG, "---minimumWidth = " + minimumWidth + "---minimumHeight = " + minimumHeight + "");
        widthSize = measureWidth(minimumWidth, widthMeasureSpec);
        heightSize = measureHeight(minimumHeight, heightMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);
        // Log.d(TAG, "majin onMeasure widthSize=" + widthSize + "  heightSize=" + heightSize);
        initBitmap();
    }


    private int measureWidth(int defaultWidth, int measureSpec) {

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        // Log.d(TAG, "---speSize = " + specSize + "");

        switch (specMode) {
            case MeasureSpec.AT_MOST:
                defaultWidth = (int) paint.measureText("") + getPaddingLeft() + getPaddingRight();
                //    Log.d(TAG, "---speMode = AT_MOST");
                break;
            case MeasureSpec.EXACTLY:
                //   Log.d(TAG, "---speMode = EXACTLY");
                defaultWidth = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                // Log.d(TAG, "---speMode = UNSPECIFIED");
                defaultWidth = Math.max(defaultWidth, specSize);
        }
        return defaultWidth;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    private int measureHeight(int defaultHeight, int measureSpec) {

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        //     Log.d(TAG, "---speSize = " + specSize + "");

        switch (specMode) {
            case MeasureSpec.AT_MOST:
                defaultHeight = (int) (- paint.ascent() + paint.descent()) + getPaddingTop() + getPaddingBottom();
                //   Log.d(TAG, "---speMode = AT_MOST");
                break;
            case MeasureSpec.EXACTLY:
                defaultHeight = specSize;
                //  Log.d(TAG, "---speSize = EXACTLY");
                break;
            case MeasureSpec.UNSPECIFIED:
                defaultHeight = Math.max(defaultHeight, specSize);
                //Log.d(TAG, "---speSize = UNSPECIFIED");
                //        1.基准点是baseline
                //        2.ascent：是baseline之上至字符最高处的距离
                //        3.descent：是baseline之下至字符最低处的距离
                //        4.leading：是上一行字符的descent到下一行的ascent之间的距离,也就是相邻行间的空白距离
                //        5.top：是指的是最高字符到baseline的值,即ascent的最大值
                //        6.bottom：是指最低字符到baseline的值,即descent的最大值

                break;
        }
        return defaultHeight;


    }
    /***********************************************************************/
}
