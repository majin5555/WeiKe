package com.weike.actiity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.weike.R;

import baseLibrary.activity.BaseActivity;

public class WelcomeActivity extends BaseActivity {

    protected final int goMain = -1;
    protected final int EQUALS = -2;

    @SuppressLint("HandlerLeak")
    protected Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case goMain:
                    VideoListActivity.startVideoListActivity(WelcomeActivity.this, 2000);

                    finishActivity();
                    break;
                case EQUALS:
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler.sendEmptyMessageDelayed(goMain, 3000);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_welcome);

    }
}