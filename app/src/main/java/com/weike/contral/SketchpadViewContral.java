package com.weike.contral;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.weike.R;
import com.weike.actiity.SketchpadMainActivity;
import com.weike.app.AppConfig;
import com.weike.bean.Screentshot;
import com.weike.customview.MoveImagview;
import com.weike.customview.PointView;
import com.weike.customview.SketchpadView;
import com.weike.util.MediaUtils;
import com.weike.util.Mp4ParserUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * @author: mj
 * @date: 2020/5/12$
 * @desc: 自定义画板 呈现视图
 */

public class SketchpadViewContral implements Runnable {
    private static final String TAG = "SketchpadViewContral";

    private final SimpleDateFormat mDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    private Context               context;
    private SketchpadMainActivity activity;
    /**
     * 绘制视图
     */
    private SketchpadView         sketchpadView;

    public SketchpadView getSketchpadView() {
        return sketchpadView;
    }

    /**
     * 音视频编码类
     */
    private MediaEncoderContral mediaEncoderContral;

    public MediaEncoderContral getMediaEncoderContral() {
        return mediaEncoderContral;
    }


    /**
     * 画板的画笔
     */
    private Paint paint = new Paint();

    public Paint getPaint() {
        return paint;
    }

    /**
     * 截图线程
     */
    private Thread captureBitmapThread;

    public Thread getCaptureBitmapThread() {
        return captureBitmapThread;
    }

    /**
     * 实时截图Bitmap  (用于缩略图)
     */
    private Bitmap mCurrentBitmap;

    public Bitmap getmCurrentBitmap() {
        return mCurrentBitmap;
    }


    /**
     * 视频片段 数据源
     */
    private ArrayList<String> mMp4;

    public ArrayList<String> getmMp4() {
        if (mMp4 == null) {
            return new ArrayList<>();
        }
        return mMp4;
    }

    /**
     * 截图控制
     */
    private boolean   mRecord;
    /**
     * 指针
     */
    private PointView pointView;

    public SketchpadViewContral(Context context) {
        this.context = context;
        activity = (SketchpadMainActivity) context;
        initData();
    }

    public void setSketchpadView(SketchpadView sketchpadView) {
        this.sketchpadView = sketchpadView;
        sketchpadView.setSketchpadViewContral(this);
    }


    private void initData() {
        captureBitmapThread = new Thread(this);
        captureBitmapThread.start();
        mediaEncoderContral = new MediaEncoderContral((Activity) context, this);
        mMp4 = new ArrayList<>();
        //   String name = captureBitmapThread.getName();
        // Log.d(TAG, "captureBitmapThread  " + name);
    }


    /** 设置画板模式 */
    public void setSketchpadViewMode(SketchpadView.MODE_SKETCHPAD mode) {
        sketchpadView.setSketchpadViewMode(mode);

    }

    /**
     * 获取橡皮
     */
    public void setEraser() {
        sketchpadView.setEraser();
    }

    /**
     * 添加指针
     */
    public void addPointView() {
        pointView = new PointView(context);
        ((SketchpadMainActivity) context).getSketchContentRoot().addView(pointView);
    }

    /**
     * 移除指针
     */
    public void removePointView() {
        ((SketchpadMainActivity) context).getSketchContentRoot().removeView(pointView);
    }

    /**
     * 获取屏幕截图
     */
    MoveImagview scaleView;

    public void addScreenshot(boolean isIntercept) {
        // Bitmap touchImg = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        MoveImagview moveImagview = new MoveImagview(context);
        if (isIntercept) { //屏幕截取
            Screentshot canvasSnapshot = sketchpadView.getCanvasSnapshot();
            if (canvasSnapshot != null) {
                Log.d(TAG, "canvasSnapshot    " + canvasSnapshot);
                moveImagview.setImageBitamp(canvasSnapshot.getBitmap());
                ((SketchpadMainActivity) context).getSketchPicContentRoot().addView(moveImagview);
                ((SketchpadMainActivity) context).getSketchPicContentRoot().bringToFront();
                ((SketchpadMainActivity) context).getSketchPicContentRoot().bringChildToFront(moveImagview);
                // Log.d(TAG, "((SketchpadMainActivity) context).getSketchContentRoot()  " + ((SketchpadMainActivity) context).getSketchContentRoot().getChildCount());
            } else {
                Log.d(TAG, "canvasSnapshot null  ");
            }

        } else {//相册

        }

    }


    /**
     * 获取屏幕截图
     */
    public void RemoveScreenshot() {
        ((SketchpadMainActivity) context).getSketchPicContentRoot().removeView(scaleView);
        //        if (scaleView.getTouchImg() != null) {
        //            scaleView.getTouchImg().recycle();
        //        }

    }


    /**
     * 设置画笔颜色
     */
    public void setStrokeColor(int color) {
        sketchpadView.setPen(color);
    }

    /**
     * 开始录制
     */
    public void startRecord(boolean record) {
        //tart = System.currentTimeMillis();
        mediaEncoderContral.startRecord();
        mRecord = record;
    }

    /**
     * 停止录制
     */
    public void stopRecord(boolean record) {
        mRecord = record;
        mediaEncoderContral.stopRecord();
        //添加视频路径到集合
        mMp4.add(getMediaEncoderContral().getMediaVideoEncoder().getOutputPath());
        //  Log.d(TAG, "视频片段outputPath   " + getSoftInputSurface().getMediaVideoEncoder().getOutputPath());
    }

    /**
     * 清除画板
     */
    public void clearSketchpadView() {
        sketchpadView.clear();
    }

    /**
     * 完成录制 合并所有视频片段
     * 视频和缩略图名称一致
     */
    public File mergeVideo() {
        File mergeVideoFile = null;
        if (getmMp4().size() > 0) {
            String filename = getDateTimeString();
            mergeVideoFile = new File(AppConfig.getInstance().getPathVideoRoot() + filename + ".mp4");
            Mp4ParserUtils.getSingleInstance().mergeVideo(getmMp4(), mergeVideoFile);
        }
        return mergeVideoFile;
    }

    /** 截取缩略图 */
    public void saveThumbnail(String filename) {
        MediaUtils.saveToDcim(getmCurrentBitmap(), filename, context);
    }

    /**
     * get current date and time as String
     *
     * @return
     */
    final GregorianCalendar now = new GregorianCalendar();

    private final String getDateTimeString() {
        return mDateTimeFormat.format(now.getTime());
    }

    /**
     * 实时截图
     */
    @Override
    public void run() {
        /*** 进行截图*/
        do {
            if (mRecord) {
                try {
                    /**截屏幕核心模块*/
                    View view = ((SketchpadMainActivity) context).getSketchContentRoot();
                    Bitmap bitmap = convertViewToBitmap(view, sketchpadView.getWidthSize(), sketchpadView.getHeightSize());
                    mCurrentBitmap = bitmap;
                    mediaEncoderContral.getMediaVideoEncoder().flushFrame(bitmap);
                    Thread.sleep(5);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, " 截屏幕核心模块 出错了 " + e.getMessage());
                }
            } else {
                //TODO 不做任何操作
            }

        } while (true);
    }

    Canvas canvas = new Canvas();

    private Bitmap convertViewToBitmap(View view, int bitmapWidth, int bitmapHeight) {
        Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        view.draw(canvas);
        return bitmap;
    }


    @SuppressLint("ClickableViewAccessibility")
    public void showPenDialog(View view) {

        View contentView = LayoutInflater.from(context).inflate(
                R.layout.pop_window, null);
        final PopupWindow popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        (contentView.findViewById(R.id.pen1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStrokeColor(Color.BLACK);
                activity.getPen().setImageResource(R.drawable.pen2);
                activity.getPen().setTag(R.drawable.pen2);
                popupWindow.dismiss();
            }
        });
        (contentView).findViewById(R.id.pen2)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setStrokeColor(Color.GRAY);
                        activity.getPen().setImageResource(R.drawable.pen3);
                        activity.getPen().setTag(R.drawable.pen3);
                        popupWindow.dismiss();
                    }
                });
        (contentView).findViewById(R.id.pen3)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setStrokeColor(Color.WHITE);
                        activity.getPen().setImageResource(R.drawable.pen1);
                        activity.getPen().setTag(R.drawable.pen1);
                        popupWindow.dismiss();
                    }
                });

        (contentView).findViewById(R.id.pen4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStrokeColor(Color.RED);
                activity.getPen().setImageResource(R.drawable.pen4);
                activity.getPen().setTag(R.drawable.pen4);
                popupWindow.dismiss();
            }
        });
        (contentView).findViewById(R.id.pen5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStrokeColor(Color.BLUE);
                activity.getPen().setImageResource(R.drawable.pen5);
                activity.getPen().setTag(R.drawable.pen5);
                popupWindow.dismiss();
            }
        });
        (contentView).findViewById(R.id.pen6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStrokeColor(Color.GREEN);
                activity.getPen().setImageResource(R.drawable.pen6);
                activity.getPen().setTag(R.drawable.pen6);
                popupWindow.dismiss();
            }
        });
        (contentView).findViewById(R.id.pen7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStrokeColor(Color.YELLOW);
                activity.getPen().setImageResource(R.drawable.pen7);
                activity.getPen().setTag(R.drawable.pen7);
                popupWindow.dismiss();
            }
        });
        (contentView).findViewById(R.id.pen8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStrokeColor(0xffB23AEE);
                activity.getPen().setImageResource(R.drawable.pen8);
                activity.getPen().setTag(R.drawable.pen8);
                popupWindow.dismiss();
            }
        });


        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);

        popupWindow.setTouchInterceptor((v, event) -> false);

        popupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.pen_back_2x));
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, location[0] - 120, location[1] + 50);
        popupWindow.showAsDropDown(view);

    }

    /**
     * 设置画笔颜色
     */
    public void setPenState() {
        if ((int) activity.getPen().getTag() == R.drawable.pen0) {
            activity.getPen().setImageResource(R.drawable.pen1);
            activity.getPen().setTag(R.drawable.pen1);
            setStrokeColor(Color.WHITE);
        }
        if ((int) activity.getPen().getTag() == R.drawable.pen2_high) {
            activity.getPen().setImageResource(R.drawable.pen2);
            activity.getPen().setTag(R.drawable.pen2);
            setStrokeColor(Color.BLACK);
        }
        if ((int) activity.getPen().getTag() == R.drawable.pen3_high) {
            activity.getPen().setImageResource(R.drawable.pen3);
            activity.getPen().setTag(R.drawable.pen3);
            setStrokeColor(Color.GRAY);
        }
        if ((int) activity.getPen().getTag() == R.drawable.pen4_high) {
            activity.getPen().setImageResource(R.drawable.pen4);
            activity.getPen().setTag(R.drawable.pen4);
            setStrokeColor(Color.RED);
        }
        if ((int) activity.getPen().getTag() == R.drawable.pen5_high) {
            activity.getPen().setImageResource(R.drawable.pen5);
            activity.getPen().setTag(R.drawable.pen5);
            setStrokeColor(Color.BLUE);
        }
        if ((int) activity.getPen().getTag() == R.drawable.pen6_high) {
            activity.getPen().setImageResource(R.drawable.pen6);
            activity.getPen().setTag(R.drawable.pen6);
            setStrokeColor(Color.GREEN);
        }
        if ((int) activity.getPen().getTag() == R.drawable.pen7_high) {
            activity.getPen().setImageResource(R.drawable.pen7);
            activity.getPen().setTag(R.drawable.pen7);
            setStrokeColor(Color.YELLOW);
        }
        if ((int) activity.getPen().getTag() == R.drawable.pen8_high) {
            activity.getPen().setImageResource(R.drawable.pen8);
            activity.getPen().setTag(R.drawable.pen8);
            setStrokeColor(0xffB23AEE);
        }
    }

    /** 清除画笔状态 */
    public void clearPenSelectState() {

        if ((int) activity.getPen().getTag() == R.drawable.pen1) {
            activity.getPen().setImageResource(R.drawable.pen0);
            activity.getPen().setTag(R.drawable.pen0);
        }
        if ((int) activity.getPen().getTag() == R.drawable.pen2) {
            activity.getPen().setImageResource(R.drawable.pen2_high);
            activity.getPen().setTag(R.drawable.pen2_high);

        }
        if ((int) activity.getPen().getTag() == R.drawable.pen3) {
            activity.getPen().setImageResource(R.drawable.pen3_high);
            activity.getPen().setTag(R.drawable.pen3_high);
        }
        if ((int) activity.getPen().getTag() == R.drawable.pen4) {
            activity.getPen().setImageResource(R.drawable.pen4_high);
            activity.getPen().setTag(R.drawable.pen4_high);
        }
        if ((int) activity.getPen().getTag() == R.drawable.pen5) {
            activity.getPen().setImageResource(R.drawable.pen5_high);
            activity.getPen().setTag(R.drawable.pen5_high);
        }
        if ((int) activity.getPen().getTag() == R.drawable.pen6) {
            activity.getPen().setImageResource(R.drawable.pen6_high);
            activity.getPen().setTag(R.drawable.pen6_high);
        }
        if ((int) activity.getPen().getTag() == R.drawable.pen7) {
            activity.getPen().setImageResource(R.drawable.pen7_high);
            activity.getPen().setTag(R.drawable.pen7_high);
        }
        if ((int) activity.getPen().getTag() == R.drawable.pen8) {
            activity.getPen().setImageResource(R.drawable.pen8_high);
            activity.getPen().setTag(R.drawable.pen8_high);
        }
        //  activity.getEraser().setImageResource(R.drawable.eraser_2x);
        //   activity.getHand().setImageResource(R.drawable.hand_2x);
        //activity.getPointer().setImageResource(R.drawable.pointer_2x);
    }

    /** 设置橡皮状态 */
    public void setHandState() {
        if ((int) activity.getHand().getTag() == R.drawable.hand_2x) {
            activity.getHand().setImageResource(R.drawable.hand2_2x);
            activity.getHand().setTag(R.drawable.hand2_2x);
        }
    }


    /** 清除小手状态 */
    public void clearHandState() {
        if ((int) activity.getHand().getTag() == R.drawable.hand2_2x) {
            activity.getHand().setImageResource(R.drawable.hand_2x);
            activity.getHand().setTag(R.drawable.hand_2x);
        }
    }

    /** 设置橡皮状态 */
    public void setEaserState() {
        if ((int) activity.getEraser().getTag() == R.drawable.eraser_2x) {
            activity.getEraser().setImageResource(R.drawable.eraser2_2x);
            activity.getEraser().setTag(R.drawable.eraser2_2x);
        }
    }

    /** 清除橡皮状态 */
    public void clearEaserState() {
        if ((int) activity.getEraser().getTag() == R.drawable.eraser2_2x) {
            activity.getEraser().setImageResource(R.drawable.eraser_2x);
            activity.getEraser().setTag(R.drawable.eraser_2x);
        }
    }

    /** 设置指针 */
    public void setPointState() {
        if ((int) activity.getPointer().getTag() == R.drawable.pointer_2x) {
            activity.getPointer().setImageResource(R.drawable.pointer2_2x);
            activity.getPointer().setTag(R.drawable.pointer2_2x);
        }
    }

    /** 清除指针 */
    public void clearPointState() {
        if ((int) activity.getPointer().getTag() == R.drawable.pointer2_2x) {
            activity.getPointer().setImageResource(R.drawable.pointer_2x);
            activity.getPointer().setTag(R.drawable.pointer_2x);
        }
    }
}
