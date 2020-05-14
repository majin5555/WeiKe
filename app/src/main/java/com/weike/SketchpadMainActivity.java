package com.weike;

import android.Manifest;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codingending.popuplayout.PopupLayout;
import com.weike.app.AppConfig;
import com.weike.customview.SketchpadView;
import com.weike.util.PermissionUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import baseLibrary.activity.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 视频录制
 */
public class SketchpadMainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "SketchpadMainActivity";

    public static List<String> sNeedReqPermissions = new ArrayList<>();

    static {
        sNeedReqPermissions.add(Manifest.permission.RECORD_AUDIO);
        sNeedReqPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private             PermissionUtils mPermissionUtils;
    public static final int             PERMISSION_RQUEST_CODE = 100;

    /** 延迟时间 */
    public static final int DELAYED_TIME           = 100;
    /** 更新录制UI延迟时间 */
    public static final int UPDATE_UI_DELAYED_TIME = 500;

    /** 更新录制按钮 开始 */
    public static final int UPDATE_UI_CHRONOMETER      = 0;
    /** 更新录制按钮  停止 */
    public static final int UPDATE_UI_STOP_CHRONOMETER = 1;

    @BindView(R.id.exit)
    ImageView     exit;
    @BindView(R.id.undo)
    ImageView     undo;
    @BindView(R.id.redo)
    ImageView     redo;
    @BindView(R.id.pen)
    ImageView     pen;
    @BindView(R.id.eraser)
    ImageView     eraser;
    @BindView(R.id.hand)
    ImageView     hand;
    @BindView(R.id.attachment)
    ImageView     attachment;
    @BindView(R.id.pointer)
    ImageView     pointer;
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
    @BindView(R.id.tv1)
    TextView      tv1;
    @BindView(R.id.add_pad)
    TextView      addPad;
    @BindView(R.id.record_surface)
    SketchpadView sketchPadView;


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
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }


    private void init() {
        AppConfig.getInstance().init();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        handler = new WeakHandler(this);

        //首先判断当前的权限问题
        mPermissionUtils = new PermissionUtils(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mPermissionUtils.request(sNeedReqPermissions, PERMISSION_RQUEST_CODE, new PermissionUtils.CallBack() {
                @Override
                public void grantAll() {
                    //Toast.makeText(SketchpadMainActivity.this, "获取了全部权限", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void denied() {
                    //Toast.makeText(SketchpadMainActivity.this, "有权限未获取", Toast.LENGTH_SHORT).show();
                }
            });
        }
        ButterKnife.bind(this);


        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                //    Log.d(TAG, " chronometer  " + chronometer.toString());
            }
        });
        View view = View.inflate(SketchpadMainActivity.this, R.layout.layout_exit, null);
        mExit = PopupLayout.init(SketchpadMainActivity.this, view);
        view.findViewById(R.id.mcontinue).setOnClickListener(this);
        view.findViewById(R.id.mrenounce).setOnClickListener(this);
        view.findViewById(R.id.mcomplete).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mcontinue:
                Toast.makeText(this, "继续录制", Toast.LENGTH_SHORT).show();
                recordVideo();
                break;
            case R.id.mrenounce:
                Toast.makeText(this, "放弃录制", Toast.LENGTH_SHORT).show();
                //  finish();
                break;
            case R.id.mcomplete:

                sketchPadView.mergeVideo();
                sketchPadView.getmMp4().clear();

                //  Toast.makeText(this, "完成录制  " , Toast.LENGTH_SHORT).show();
                break;
        }
        mExit.dismiss();

    }


    @OnClick({R.id.exit, R.id.undo, R.id.redo, R.id.pen, R.id.eraser, R.id.hand, R.id.attachment, R.id.pointer, R.id.what, R.id.chronometer, R.id.record, R.id.pre_pad, R.id.next_pad, R.id.remove_pad, R.id.add_pad})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.exit:
                if (isRecord) recordVideo();
                mExit.show(PopupLayout.POSITION_LEFT);
                break;
            case R.id.undo:
                break;
            case R.id.redo:
                break;
            case R.id.pen:
                Toast.makeText(this, "获取铅笔", Toast.LENGTH_SHORT).show();
                sketchPadView.setPen(0);
                break;
            case R.id.eraser:
                Toast.makeText(this, "获取橡皮", Toast.LENGTH_SHORT).show();
                sketchPadView.setEraser();
                break;
            case R.id.hand:
                break;
            case R.id.attachment:
                break;
            case R.id.pointer:
                break;
            case R.id.what:
                break;
            case R.id.chronometer:
                break;
            case R.id.record:
                recordVideo();
                break;
            case R.id.pre_pad:
                break;
            case R.id.next_pad:
                break;
            case R.id.remove_pad:
                break;
            case R.id.add_pad:
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
            sketchPadView.startRecord(this, isRecord);
            startRecord();
        } else {
            sketchPadView.stopRecord(this, isRecord);
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


}
