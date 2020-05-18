package com.weike.actiity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.codingending.popuplayout.PopupLayout;
import com.weike.R;
import com.weike.app.AppConfig;
import com.weike.contral.SketchpadViewContral;
import com.weike.customview.CommomDialog;
import com.weike.customview.SketchpadView;
import com.weike.util.MediaUtils;

import java.io.File;
import java.lang.ref.WeakReference;

import baseLibrary.activity.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 视频录制
 */
public class SketchpadMainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "SketchpadMainActivity";


    /** 延迟时间 */
    public static final int DELAYED_TIME           = 100;
    /** 更新录制UI延迟时间 */
    public static final int UPDATE_UI_DELAYED_TIME = 500;

    /** 更新录制按钮 开始 */
    public static final int UPDATE_UI_CHRONOMETER      = 0;
    /** 更新录制按钮  停止 */
    public static final int UPDATE_UI_STOP_CHRONOMETER = 1;

    //private ImageView pen1, pen2, pen3, pen4, pen5, pen6, pen7, pen8;
    @BindView(R.id.exit)
    ImageView exit;
    @BindView(R.id.undo)
    ImageView undo;
    @BindView(R.id.redo)
    ImageView redo;
    @BindView(R.id.pen)
    ImageView pen;

    public ImageView getPen() {
        return pen;
    }

    @BindView(R.id.eraser)
    ImageView eraser;

    public ImageView getEraser() {
        return eraser;
    }

    @BindView(R.id.hand)
    ImageView hand;

    public ImageView getHand() {
        return hand;
    }

    @BindView(R.id.pointer)
    ImageView pointer;

    public ImageView getPointer() {
        return pointer;
    }

    @BindView(R.id.attachment)
    ImageView     attachment;
    @BindView(R.id.what)
    ImageView     what;
    @BindView(R.id.chronometer)
    Chronometer   chronometer;
    @BindView(R.id.record)
    ImageButton   mRecord;
    @BindView(R.id.pre_pad)
    ImageView     prePad;
    @BindView(R.id.next_pad)
    ImageView     nextPad;
    @BindView(R.id.remove_pad)
    TextView      removePad;
    @BindView(R.id.add_pad)
    TextView      addPad;
    /**
     * 自定义画板视图 需要控制画板数量 增减页码
     */
    @BindView(R.id.sketch_view)
    SketchpadView sketchpadView;
    @BindView(R.id.sketch_content_root)
    FrameLayout   sketchContentRoot;

    public FrameLayout getSketchPicContentRoot() {
        return sketchPicContentRoot;
    }

    /** 屏幕截图存放的viewRoot */
    @BindView(R.id.sketch_pic_content_root)
    FrameLayout sketchPicContentRoot;

    public FrameLayout getSketchContentRoot() {
        return sketchContentRoot;
    }


    private TextView       mTvComplete;
    private MergeVideoTask mergeVideoTask;


    /**
     * 控制类
     */
    private SketchpadViewContral mContral;
    /**
     * 当前图层
     */
    private SketchpadView        mCurrentSketchpadView;

    /**
     * 最少1页
     */
    private final int pageSizeMin  = 1;
    /**
     * 最多10页
     */
    private final int pazeSizeMax  = 10;
    /**
     * 当前页数
     */
    private       int mCurrentPage = 1;

    private boolean penSelected = true;//画笔标志位


    public static void startSketchpadActivity(Activity context, int requestCode) {

        Intent intent = new Intent(context, SketchpadMainActivity.class);
        context.startActivityForResult(intent, requestCode);
    }


    private WeakHandler handler;
    private PopupLayout mExit;


    private class WeakHandler extends Handler {
        WeakReference<SketchpadMainActivity> weakReference;

        public WeakHandler(SketchpadMainActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (weakReference.get() != null) {
                switch (msg.what) {
                    case UPDATE_UI_CHRONOMETER:
                        chronometer.setVisibility(View.VISIBLE);
                        //            Log.d(TAG, "UPDATE_UI_CHRONOMETER ");
                        if (! isRecord) {
                            this.sendEmptyMessageDelayed(UPDATE_UI_STOP_CHRONOMETER, UPDATE_UI_DELAYED_TIME);
                        } else {
                            handler.removeMessages(UPDATE_UI_CHRONOMETER);
                            handler.removeMessages(UPDATE_UI_STOP_CHRONOMETER);
                        }

                        break;
                    case UPDATE_UI_STOP_CHRONOMETER:
                        chronometer.setVisibility(View.INVISIBLE);
                        this.sendEmptyMessageDelayed(UPDATE_UI_CHRONOMETER, UPDATE_UI_DELAYED_TIME);

                        break;
                    default:
                        break;

                }
            }
        }
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_sketchpad);
        ButterKnife.bind(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        //第一页
        mCurrentSketchpadView = sketchpadView;
        //  sketchContentRoot.addView(mCurrentSketchpadView);

        mContral = new SketchpadViewContral(SketchpadMainActivity.this);

        mContral.setSketchpadView(mCurrentSketchpadView);
        mergeVideoTask = new MergeVideoTask(mContral);


    }

    private void setAllTag() {
        pen.setTag(R.drawable.pen2);
        pointer.setTag(R.drawable.pointer_2x);
        eraser.setTag(R.drawable.eraser_2x);
        hand.setTag(R.drawable.hand_2x);
    }

    private void init() {
        AppConfig.getInstance().init();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        handler = new WeakHandler(this);
        setAllTag();


        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                //    Log.d(TAG, " chronometer  " + chronometer.toString());
            }
        });
        View mExitView = View.inflate(SketchpadMainActivity.this, R.layout.layout_exit, null);
        mExit = PopupLayout.init(SketchpadMainActivity.this, mExitView);
        mExitView.findViewById(R.id.mcontinue).setOnClickListener(this);
        mExitView.findViewById(R.id.mrenounce).setOnClickListener(this);
        mTvComplete = mExitView.findViewById(R.id.mcomplete);
        mTvComplete.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mcontinue:
                recordVideo();
                break;
            case R.id.mrenounce:
                CommomDialog commomDialog = new CommomDialog(SketchpadMainActivity.this, R.style.dialog, "确定要放弃录制么？", new CommomDialog.OnCloseListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean confirm) {
                        if (confirm) {
                            setResult(RESULT_OK);
                            finishActivity();
                        }

                        dialog.dismiss();
                    }
                });
                commomDialog.setTitle("提示").show();


                break;
            case R.id.mcomplete:
                mergeVideoTask.execute();
                break;
        }
        mExit.dismiss();

    }


    @OnClick({R.id.exit, R.id.undo, R.id.redo, R.id.pen, R.id.eraser, R.id.hand, R.id.attachment, R.id.pointer, R.id.what, R.id.record, R.id.pre_pad, R.id.next_pad, R.id.remove_pad, R.id.add_pad})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.exit:

                if (isRecord) recordVideo();

                if (mContral.getmMp4().size() == 0)
                    mTvComplete.setVisibility(View.GONE);
                else
                    mTvComplete.setVisibility(View.VISIBLE);

                mExit.show(PopupLayout.POSITION_LEFT);
                break;
            case R.id.record:
                recordVideo();
                break;
            case R.id.pen:
                mContral.removePointView();
                mContral.clearHandState();
                mContral.clearPointState();
                mContral.clearEaserState();

                if (penSelected) {
                    mContral.showPenDialog(view);
                } else {
                    mContral.setPenState();
                }
                mContral.setSketchpadViewMode(SketchpadView.MODE_SKETCHPAD.PEN);
                penSelected = true;

                break;
            case R.id.eraser:
                mContral.removePointView();
                mContral.clearHandState();
                mContral.clearPointState();
                mContral.clearPenSelectState();

                if ((int) eraser.getTag() == R.drawable.eraser_2x) mContral.setEaserState();
                mContral.setEraser();
                mContral.setSketchpadViewMode(SketchpadView.MODE_SKETCHPAD.EASER);
                penSelected = false;
                break;
            case R.id.hand:

                mContral.removePointView();
                mContral.clearHandState();
                mContral.clearPointState();
                mContral.clearPenSelectState();

                if ((int) hand.getTag() == R.drawable.hand_2x) mContral.setHandState();

                mContral.addScreenshot(true);
                mContral.setSketchpadViewMode(SketchpadView.MODE_SKETCHPAD.HANLER);

                penSelected = false;
                break;
            case R.id.pointer:

                mContral.clearHandState();
                mContral.clearPenSelectState();
                mContral.clearEaserState();
                if ((int) pointer.getTag() == R.drawable.pointer_2x) mContral.addPointView();

                mContral.setPointState();
                mContral.setSketchpadViewMode(SketchpadView.MODE_SKETCHPAD.PONIT);
                penSelected = false;
                break;
            case R.id.attachment:
                penSelected = false;
                break;
            case R.id.what:
                penSelected = false;
                break;
            case R.id.undo:
                break;
            case R.id.redo:
                break;
            case R.id.pre_pad:
                break;
            case R.id.next_pad:
                break;
            case R.id.remove_pad:
                //                if (mCurrentPage > pageSizeMin) {
                //                    mCurrentPage--;
                //                    //                    sketchContentRoot.removeView(mCurrentSketchpadView);
                //                    //                    mContral.setSketchpadView(mCurrentSketchpadView);
                //                } else {
                //                    mCurrentPage = 1;
                //                }
                //
                //                addPad.setText(String.valueOf(mCurrentPage));
                //                removePad.setText(String.valueOf(mCurrentPage));

                break;
            case R.id.add_pad:

                //                if (mCurrentPage < pageSizeMin) {
                //                    mCurrentPage = 1;
                //                } else {
                //                    mCurrentPage++;
                //                }
                //
                //                addPad.setText(String.valueOf(mCurrentPage));
                //                removePad.setText(String.valueOf(mCurrentPage));

                //                mCurrentSketchpadView = new SketchpadView(SketchpadMainActivity.this);
                //                sketchContentRoot.addView(mCurrentSketchpadView);
                //                mContral.setSketchpadView(mCurrentSketchpadView);

                break;
            default:
                break;
        }

    }


    /**
     * 制作视频
     */
    boolean isRecord;

    private void recordVideo() {
        isRecord = ! isRecord;
        if (isRecord) {
            mContral.startRecord(isRecord);
            startRecord();
        } else {
            mContral.stopRecord(isRecord);
            stopRecord();
        }
    }

    private void startRecord() {
        startTime();
        mRecord.setBackgroundResource(R.drawable.recording_2x);
    }

    private void stopRecord() {
        stopTime();
        mRecord.setBackgroundResource(R.drawable.pause_2x);
    }

    private long rangeTime;

    private void startTime() {
        handler.sendEmptyMessage(UPDATE_UI_CHRONOMETER);
        //start Chronometer
        chronometer.setBase(SystemClock.elapsedRealtime() - rangeTime);
        chronometer.start();
        chronometer.setTextColor(Color.RED);
    }

    private void stopTime() {
        rangeTime = SystemClock.elapsedRealtime() - chronometer.getBase();
        //Log.d(TAG, "stopTime   rangeTime" + rangeTime);
        handler.sendEmptyMessage(UPDATE_UI_STOP_CHRONOMETER);
        chronometer.setTextColor(Color.WHITE);
        chronometer.stop();
    }


    class MergeVideoTask extends AsyncTask<String, Integer, File> {
        SketchpadViewContral sk;

        public MergeVideoTask(SketchpadViewContral sk) {
            this.sk = sk;
        }

        @SuppressLint("WrongThread")
        @Override
        protected File doInBackground(String... strings) {
            File mergeVideoFile = sk.mergeVideo();
            sk.getmMp4().clear();
            return mergeVideoFile;

        }

        @Override
        protected void onPreExecute() {
            showDialog("视频合成中...");
        }


        @Override
        protected void onPostExecute(File result) {
            /**保存合并的视频*/
            MediaUtils.flushVideo(result, SketchpadMainActivity.this);
            showDialog("视频合成成功");
            setResult(RESULT_OK);
            finishActivity();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContral.clearSketchpadView();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    /**
     * 隐藏虚拟按键，并且全屏
     */
    //    protected void hideBottomUIMenu() {
    //        //隐藏虚拟按键，并且全屏
    //        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
    //            View v = this.getWindow().getDecorView();
    //            v.setSystemUiVisibility(View.GONE);
    //        } else if (Build.VERSION.SDK_INT >= 19) {
    //            //for new api versions.
    //            View decorView = getWindow().getDecorView();
    //            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    //                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
    //            decorView.setSystemUiVisibility(uiOptions);
    //        }
    //    }


    //TODO 测试合并摄像头视频

    //                 ArrayList list = new ArrayList();
    //                String str1 = "/storage/emulated/0/Movies/Weike/TempAVRecSample/2020-05-14-20-22-51.mp4";
    //                list.add(str1);
    //                String str3 = "/storage/emulated/0/Movies/Weike/TempAVRecSample/2020-05-14-21-26-15.mp4";
    //                list.add(str3);

    //                String str2 = "/storage/emulated/0/Movies/Weike/mlecture/2020-05-16-01-19-39.mp4";
    //                list.add(str2);
    //                String str4 = "/storage/emulated/0/Movies/Weike/mlecture/hello.mp4";
    //                list.add(str4);
    //
    //                Mp4ParserUtils.getSingleInstance().mergeVideo(list, new File(AppConfig.getInstance().getPathVideoRoot() + "merge.mp4"));
    //  Log.d(TAG, "测试完毕。。。。。。");
}
