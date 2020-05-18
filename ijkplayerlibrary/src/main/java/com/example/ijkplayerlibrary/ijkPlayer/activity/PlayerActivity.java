//package com.example.ijkplayerlibrary.ijkPlayer.activity;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.content.res.Configuration;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ImageView;
//
//import com.bumptech.glide.Glide;
//import com.example.ijkplayerlibrary.ijkPlayer.listener.OnPlayerBackListener;
//import com.example.ijkplayerlibrary.ijkPlayer.listener.OnShowThumbnailListener;
//import com.example.ijkplayerlibrary.ijkPlayer.widgit.PlayStateParams;
//import com.weike.R;
//import com.weike.videoplayer.ijkplayer.widget.PlayerView;
//
//import androidx.annotation.Nullable;
//
//
//public class PlayerActivity extends Activity {
//    public static void startPlayerActivity(Activity context, int requestCode) {
//        Intent intent = new Intent(context, PlayerActivity.class);
//        context.startActivityForResult(intent, requestCode);
//    }
//
//
//    private PlayerView player;
//    private Context    mContext;
//    private View       rootView;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        this.mContext = this;
//        rootView = getLayoutInflater().from(this).inflate(R.layout.simple_player_view_player, null);
//        setContentView(rootView);
//        String url = "/storage/emulated/0/Movies/Weike/mlecture/2020-05-18-13-08-01.mp4";
//        player = new PlayerView(this, rootView)
//                .setTitle("什么")
//                .setScaleType(PlayStateParams.fitparent)
//                .forbidTouch(false)
//                .hideMenu(true)
//                .showThumbnail(new OnShowThumbnailListener() {
//                    @Override
//                    public void onShowThumbnail(ImageView ivThumbnail) {
//                        Glide.with(mContext)
//                                .load("http://pic2.nipic.com/20090413/406638_125424003_2.jpg")
//                                .placeholder(R.color.app_style)
//                                .error(R.color.colorAccent)
//                                .into(ivThumbnail);
//                    }
//                })
//                .setPlaySource(url)
//                .setPlayerBackListener(new OnPlayerBackListener() {
//                    @Override
//                    public void onPlayerBack() {
//                        //这里可以简单播放器点击返回键
//                        finish();
//                    }
//                })
//                .startPlay();
//
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (player != null) {
//            player.onPause();
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (player != null) {
//            player.onResume();
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
//    }
//
//}
