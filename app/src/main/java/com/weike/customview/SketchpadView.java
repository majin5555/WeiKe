package com.weike.customview;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaMetadataRetriever;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.weike.SketchpadMainActivity;
import com.weike.app.AppConfig;
import com.weike.interfaces.IUndoRedoCommand;
import com.weike.screenshot.SoftInputSurface;
import com.weike.util.BitmapUtil;
import com.weike.util.MediaUtils;
import com.weike.util.Mp4ParserUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Locale;

import androidx.annotation.Nullable;


/**
 * @author: mj
 * @date: 2020/5/12$
 * @desc: 自定义画板View
 */

public class SketchpadView extends View implements Runnable, IUndoRedoCommand {
    private static final String TAG = "SketPadView";

    private final SimpleDateFormat mDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US);

    private int widthSize;//画板的宽
    private int heightSize;//画板的高

    private SoftInputSurface softInputSurface;//音视频编码类
    private Mp4ParserUtils   mp4ParserUtils;//视频合并工具类

    public SoftInputSurface getSoftInputSurface() {
        return softInputSurface;
    }

    public void setSoftInputSurface(SoftInputSurface softInputSurface) {
        this.softInputSurface = softInputSurface;
    }

    public int getWidthSize() {
        return widthSize;
    }

    public int getHeightSize() {
        return heightSize;
    }


    private Bitmap bitmap;//整个画板显示的位图
    private Paint  paint  = new Paint();//画板的画笔
    private Canvas canvas = new Canvas();//画板的画布
    private float  xTouch = 0;//移动的位置
    private float  yTouch = 0;

    private Context context;

    private ArrayList<String> mMp4;

    public ArrayList<String> getmMp4() {
        if (mMp4 == null) {
            return new ArrayList<>();
        }
        return mMp4;
    }


    /**
     * 截图线程
     */
    private Thread captureBitmapThread;

    public Thread getCaptureBitmapThread() {
        return captureBitmapThread;
    }

    /**
     * 截图控制
     */
    private boolean mRecord;

    /**
     * 实时截图Bitmap
     */
    private Bitmap mCurrentBitmap;


    public SketchpadView(Context context) {
        super(context);
        initData();
    }

    public SketchpadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initData();
    }

    public SketchpadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }


    private void initData() {
        softInputSurface = new SoftInputSurface((Activity) context, this);
        captureBitmapThread = new Thread(this);
        captureBitmapThread.start();
        mMp4 = new ArrayList<>();
        mp4ParserUtils = new Mp4ParserUtils();

        String name = captureBitmapThread.getName();
        Log.d(TAG, "captureBitmapThread  " + name);
        setClickable(true);//设置为可点击才能获取到MotionEvent.ACTION_MOVE
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(7);
        //设置是否使用抗锯齿功能，抗锯齿功能会消耗较大资源，绘制图形的速度会减慢
        paint.setAntiAlias(true);
        //设置是否使用图像抖动处理，会使图像颜色更加平滑饱满，更加清晰
        paint.setDither(true);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int minimumWidth = getSuggestedMinimumWidth();
        final int minimumHeight = getSuggestedMinimumHeight();
        Log.d(TAG, "---minimumWidth = " + minimumWidth + "---minimumHeight = " + minimumHeight + "");
        widthSize = measureWidth(minimumWidth, widthMeasureSpec);
        heightSize = measureHeight(minimumHeight, heightMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);
        Log.d(TAG, "majin onMeasure widthSize=" + widthSize + "  heightSize=" + heightSize);
        initBitmap();
    }

    private int measureWidth(int defaultWidth, int measureSpec) {

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        Log.d(TAG, "---speSize = " + specSize + "");

        switch (specMode) {
            case MeasureSpec.AT_MOST:
                defaultWidth = (int) paint.measureText("") + getPaddingLeft() + getPaddingRight();
                Log.d(TAG, "---speMode = AT_MOST");
                break;
            case MeasureSpec.EXACTLY:
                Log.d(TAG, "---speMode = EXACTLY");
                defaultWidth = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                Log.d(TAG, "---speMode = UNSPECIFIED");
                defaultWidth = Math.max(defaultWidth, specSize);
        }
        return defaultWidth;
    }


    private int measureHeight(int defaultHeight, int measureSpec) {

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        Log.d(TAG, "---speSize = " + specSize + "");

        switch (specMode) {
            case MeasureSpec.AT_MOST:
                defaultHeight = (int) (- paint.ascent() + paint.descent()) + getPaddingTop() + getPaddingBottom();
                Log.d(TAG, "---speMode = AT_MOST");
                break;
            case MeasureSpec.EXACTLY:
                defaultHeight = specSize;
                Log.d(TAG, "---speSize = EXACTLY");
                break;
            case MeasureSpec.UNSPECIFIED:
                defaultHeight = Math.max(defaultHeight, specSize);
                Log.d(TAG, "---speSize = UNSPECIFIED");
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
        canvas.drawBitmap(bitmap, 0, 0, paint);
        //  Log.d(TAG, "onDraw");
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //   Log.d("ElecSignatureView", "onTouchEvent event=" + event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xTouch = event.getX();
                yTouch = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                canvas.drawLine(xTouch, yTouch, event.getX(), event.getY(), paint);
                xTouch = event.getX();
                yTouch = event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 获取画好的电子签名
     *
     * @return
     */
    public Bitmap getBitmap1() {
        return bitmap;
    }

    /**
     * 清除电子签名
     */
    public void clear() {
        initBitmap();
        invalidate();
    }

    /**
     * 获取橡皮
     */
    public void setEraser() {
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(50);
    }

    /**
     * 获取铅笔
     */
    public void setPen(int type) {
        paint.setStrokeWidth(7);

        paint.setColor(Color.BLACK);
        switch (type) {


            default:
                break;
        }
    }

    long start;
    long end;

    public void startRecord(Context context, boolean record) {
        //tart = System.currentTimeMillis();
        softInputSurface.startRecord();
        mRecord = record;
        this.context = context;
    }

    public void stopRecord(final Context context, boolean record) {
        mRecord = record;
        this.context = context;
        softInputSurface.stopRecord();
        //添加视频路径到集合
        mMp4.add(getSoftInputSurface().getMediaVideoEncoder().getOutputPath());
    }

    @Override
    public void run() {
        /*** 进行截图*/
        do {
            if (mRecord) {
                try {
                    Thread.sleep(5);
                    /**截屏幕核心模块*/
                    Bitmap bitmap = convertViewToBitmap(this, widthSize, heightSize);
                    softInputSurface.getMediaVideoEncoder().flushFrame(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, " 截屏幕核心模块 出错了 " + e.getMessage());
                }
            } else {
                //TODO 不做任何操作
            }

        } while (true);
    }

    private Bitmap convertViewToBitmap(View view, int bitmapWidth, int bitmapHeight) {
        Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.RGB_565);
        view.draw(new Canvas(bitmap));
        return bitmap;
    }


    @Override
    public void pageDataRedo() {

    }


    @Override
    public void pageDataUndo() {

    }

    /**
     * 完成录制 合并所有视频片段
     */


    //TODO 合成所有视频片段 视频和缩略图名称一致
    public String mergeVideo() {
        String filePath = "";
        if (getmMp4().size() > 0) {
            ((SketchpadMainActivity) context).showDialog("视频合成中...");
            String outputPath = getSoftInputSurface().getMediaVideoEncoder().getOutputPath();
            Log.d(TAG, " outputPath " + outputPath);
            String filename = getDateTimeString();
            File mergeVideoFile = new File(AppConfig.getInstance().getPathRoot() + filename + " .mp4");
            filePath = mp4ParserUtils.mergeVideo(getmMp4(), mergeVideoFile);
            /**保存缩略图*/
            saveThumbnail(filePath, filename);
            ((SketchpadMainActivity) context).dismissDialog();
        }
        return filePath;
    }


    private MediaMetadataRetriever retriever = new MediaMetadataRetriever();

    /** 截取缩略图 */
    public void saveThumbnail(String f, String filename) {
        retriever.setDataSource(f);
        String fileLength = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        Bitmap bitmap = decodeFrame(Long.parseLong(fileLength));

        MediaUtils.saveToDcim(bitmap, filename + ".jpg", context);
    }

    /**
     * 获取视频某一帧
     *
     * @param timeMs 毫秒
     */
    public Bitmap decodeFrame(long timeMs) {
        if (retriever != null) {
            Bitmap bitmap = BitmapUtil.compressImage(retriever.getFrameAtTime(timeMs * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC));
            return bitmap;
        }
        return null;
    }

    /**
     * get current date and time as String
     *
     * @return
     */
    private final String getDateTimeString() {
        final GregorianCalendar now = new GregorianCalendar();
        return mDateTimeFormat.format(now.getTime());
    }
}
