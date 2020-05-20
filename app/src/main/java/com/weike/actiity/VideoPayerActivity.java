package com.weike.actiity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.weike.R;
import com.weike.bean.MediaDataVideos;
import com.weike.contral.PlayerViewContral;
import com.weike.videoplayer.utlis.MediaUtils;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import ijkPlayer.bean.VideoijkBean;
import ijkPlayer.listener.OnShowThumbnailListener;
import ijkPlayer.widgit.PlayStateParams;

/**
 * 视频播放器
 */
public class VideoPayerActivity extends AppCompatActivity {
    private static final String TAG = "HPlayerActivity";

    public static void startHPlayerActivity(Activity context, int requestCode, MediaDataVideos mediaDataVideos) {
        Intent intent = new Intent(context, VideoPayerActivity.class);
        intent.putExtra("DataVideos", mediaDataVideos);
        context.startActivityForResult(intent, requestCode);
    }

    private PlayerViewContral player;
    private Context           mContext;

    private PowerManager.WakeLock wakeLock;
    private View                  rootView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        Intent intent = getIntent();
        MediaDataVideos dataVideos = intent.getParcelableExtra("DataVideos");
        Log.d(TAG, "dataVideos  " + dataVideos);
        rootView = getLayoutInflater().from(this).inflate(R.layout.activity_h, null);
        setContentView(rootView);
        /**虚拟按键的隐藏方法*/
    /*    rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {

                //比较Activity根布局与当前布局的大小
                int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();
                if (heightDiff > 100) {
                    //大小超过100时，一般为显示虚拟键盘事件
                    rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                } else {
                    //大小小于100时，为不显示虚拟键盘或虚拟键盘隐藏
                    rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

                }
            }
        });*/


        VideoijkBean m1 = new VideoijkBean();
        //m1.setStream(dataVideos.getName());
        m1.setUrl(dataVideos.getPath());

        player = new PlayerViewContral(this, rootView) {
            @Override
            public PlayerViewContral toggleProcessDurationOrientation() {
                //      hideSteam(getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                return setProcessDurationOrientation(getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT ? PlayStateParams.PROCESS_PORTRAIT : PlayStateParams.PROCESS_LANDSCAPE);
            }

            @Override
            public PlayerViewContral setPlaySource(List<VideoijkBean> list) {
                return super.setPlaySource(list);
            }
        }
                .setTitle(dataVideos.getName())
                .setProcessDurationOrientation(PlayStateParams.PROCESS_PORTRAIT)
                .setScaleType(PlayStateParams.f16_9)
                .forbidTouch(false)
                .hideSteam(true)
                .hideCenterPlayer(true)
                .setForbidDoulbeUp(true)
                .showThumbnail(new OnShowThumbnailListener() {
                    @Override
                    public void onShowThumbnail(ImageView ivThumbnail) {
                        Glide.with(mContext)
                                .load(dataVideos.getPath())
                                .placeholder(R.color.gray_535353)
                                .error(R.color.gray_535353)
                                .into(ivThumbnail);
                    }
                })
                .setPlaySource(m1)
                //.setChargeTie(false, 60)
                .toggleFullScreen()
                .doOnConfigurationChanged(false)
                .startPlay();


    }


    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.onPause();
        }
        /**demo的内容，恢复系统其它媒体的状态*/
        MediaUtils.muteAudioFocus(mContext, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            player.onResume();
        }
        /**demo的内容，暂停系统其它媒体的状态*/
        MediaUtils.muteAudioFocus(mContext, false);
        /**demo的内容，激活设备常亮状态*/
        if (wakeLock != null) {
            wakeLock.acquire();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.onDestroy();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //        if (player != null) {
        //            player.onConfigurationChanged(newConfig);
        //        }
    }

    @Override
    public void onBackPressed() {
        if (player != null && player.onBackPressed()) {
            return;
        }
        super.onBackPressed();
        /**demo的内容，恢复设备亮度状态*/
        if (wakeLock != null) {
            wakeLock.release();
        }
    }

}
