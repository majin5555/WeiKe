package com.weike.customview;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.weike.interfaces.IUndoRedoCommand;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;


/**
 * @author: mj
 * @date: 2020/5/12$
 * @desc: 自定义画板View
 */

public class SketchpadView extends View implements Runnable, IUndoRedoCommand {
    private static final String TAG = "SketPadView";

    private int    widthSize;//画板的宽
    private int    heightSize;
    private Bitmap bitmap;//整个画板显示的位图
    private Paint  paint  = new Paint();//画板的画笔
    private Canvas canvas = new Canvas();//画板的画布
    private float  xTouch = 0;//移动的位置
    private float  yTouch = 0;

    private Context context;
    /**
     * 截图线程
     */
    private Thread  captureBitmapThread;

    /**
     * 截图数据源
     */
    private List<Bitmap> mRecordBitmapList = new ArrayList<>();

    /**
     * 截图控制
     */
    private boolean mRecord;


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
        captureBitmapThread = new Thread(this);
        captureBitmapThread.start();
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
        Log.d("ElecSignatureView", "onMeasure widthMeasureSpec=" + widthMeasureSpec + "  heightMeasureSpec=" + heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        Log.d("ElecSignatureView", "onMeasure widthSize=" + widthSize + "  heightSize=" + heightSize);
        initBitmap();
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
        Log.d(TAG, "onDraw");
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("ElecSignatureView", "onTouchEvent event=" + event);
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
        paint.setStrokeWidth(15);
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
        start = System.currentTimeMillis();
        mRecordBitmapList.clear();
        mRecord = record;
        this.context = context;

    }

    public void stopRecord(final Context context, boolean record) {

        mRecord = record;
        this.context = context;
        /**
         * 测试是否获取图像成功
         */
       /* new Thread(new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < mRecordBitmapList.size(); i++) {
                    saveToDcim(mRecordBitmapList.get(i), i + "i.jpg", context);
                    size++;
                }
                Log.d(TAG, "保存了 " + size + "张");

            }
        }).start();*/


    }

    int size = 0;

    @Override
    public void run() {
        /*** 进行截图*/
        do {
            if (mRecord) {
                try {
                    Thread.sleep(30);
                    //截屏幕核心模块
                    Bitmap bitmap = convertViewToBitmap(this, getWidth(), getHeight());
                    //   mRecordBitmapList.add(bitmap);
                    //  end = System.currentTimeMillis();
                } catch (InterruptedException e) {
                    e.printStackTrace();
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

    // 将图片保存到相册
    public boolean saveToDcim(Bitmap bitmap, String filename, Context context) {
        Uri uri;
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, filename);
        Log.d(TAG, "Build.VERSION.SDK_INT  " + Build.VERSION.SDK_INT);

        //兼容Android Q和以下版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //android Q中不再使用DATA字段，而用RELATIVE_PATH代替
            //RELATIVE_PATH是相对路径不是绝对路径
            //DCIM是系统文件夹，关于系统文件夹可以到系统自带的文件管理器中查看，不可以写没存在的名字
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM" + File.separator + "weike");
        } else {
            contentValues.put(MediaStore.Images.Media.DATA, Environment.getExternalStoragePublicDirectory("DCIM") + File.separator + "weike" + File.separator + filename);
        }
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        OutputStream outputStream = null;
        try {
            outputStream = context.getContentResolver().openOutputStream(uri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            size++;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(uri);
        context.sendBroadcast(intent);
        return true;
    }


    @Override
    public void pageDataRedo() {

    }

    @Override
    public void pageDataUndo() {

    }
}
