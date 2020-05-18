//package com.example.ijkplayerlibrary.ijkPlayer.activity;
//
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.content.Context;
//import android.content.res.Configuration;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.os.PowerManager;
//import android.view.View;
//import android.widget.ImageView;
//
//import com.example.ijkplayerlibrary.R;
//import com.example.ijkplayerlibrary.ijkPlayer.listener.OnShowThumbnailListener;
//import com.example.ijkplayerlibrary.ijkPlayer.utils.MediaUtils;
//import com.example.ijkplayerlibrary.ijkPlayer.widgit.PlayStateParams;
//
//import androidx.annotation.Nullable;
//
//
///**
// * ========================================
// * <p/>
// * 版 权：深圳市晶网科技控股有限公司 版权所有 （C） 2015
// * <p/>
// * 作 者：陈冠明
// * <p/>
// * 个人网站：http://www.dou361.com
// * <p/>
// * 版 本：1.0
// * <p/>
// * 创建日期：2015/11/18 9:40
// * <p/>
// * 描 述：直播全屏竖屏场景
// * <p/>
// * <p/>
// * 修订历史：
// * <p/>
// * ========================================
// */
//public class PlayerLiveActivity extends Activity {
//
//    private PlayerView            player;
//    private Context               mContext;
//    private View                  rootView;
//    private List<LiveBean>        list;
//    private String                url      = "http://hdl.9158.com/live/744961b29380de63b4ff129ca6b95849.flv";
//    private String                title    = "标题";
//    private PowerManager.WakeLock wakeLock;
//    private Handler               mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (list.size() > 1) {
//                url = list.get(1).getLiveStream();
//                title = list.get(1).getNickname();
//            }
//            player.setPlaySource(url)
//                    .startPlay();
//        }
//    };
//
//    @SuppressLint("InvalidWakeLockTag")
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        this.mContext = this;
//        rootView = getLayoutInflater().from(this).inflate(R.layout.simple_player_view_player, null);
//        setContentView(rootView);
//
//        /**常亮*/
//        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "liveTAG");
//        wakeLock.acquire();
//
//        player = new PlayerView(this, rootView)
//                .setTitle(title)
//                .setScaleType(PlayStateParams.fitparent)
//                .hideMenu(true)
//                .hideSteam(true)
//                .setForbidDoulbeUp(true)
//                .hideCenterPlayer(true)
//                .hideControlPanl(true)
//                .showThumbnail(new OnShowThumbnailListener() {
//                    @Override
//                    public void onShowThumbnail(ImageView ivThumbnail) {
//                        Glide.with(mContext)
//                                .load("http://pic2.nipic.com/20090413/406638_125424003_2.jpg")
//                                .placeholder(R.color.app_style)
//                                .error(R.color.blue_0552fa)
//                                .into(ivThumbnail);
//                    }
//                });
//        new Thread() {
//            @Override
//            public void run() {
//                //这里多有得罪啦，网上找的直播地址，如有不妥之处，可联系删除
//                //list = ApiServiceUtils.getLiveList();
//                mHandler.sendEmptyMessage(0);
//            }
//        }.start();
//
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (player != null) {
//            player.onPause();
//        }
//        MediaUtils.muteAudioFocus(mContext, true);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (player != null) {
//            player.onResume();
//        }
//        MediaUtils.muteAudioFocus(mContext, false);
//        if (wakeLock != null) {
//            wakeLock.acquire();
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (player != null) {
//            player.onDestroy();
//        }
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        if (player != null) {
//            player.onConfigurationChanged(newConfig);
//        }
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (player != null && player.onBackPressed()) {
//            return;
//        }
//        super.onBackPressed();
//        if (wakeLock != null) {
//            wakeLock.release();
//        }
//    }
//
//}
