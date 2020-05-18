package com.example.ijkplayerlibrary.ijkPlayer.services;

import android.net.Uri;

import com.example.ijkplayerlibrary.ijkPlayer.media.IMediaController;

import tv.danmaku.ijk.media.player.IMediaPlayer;


public interface IjkplayerUtils {

    /**
     * 参数aspectRatio(缩放参数)参见IRenderView的常量：IRenderView.AR_ASPECT_FIT_PARENT,
     * IRenderView.AR_ASPECT_FILL_PARENT,
     * IRenderView.AR_ASPECT_WRAP_CONTENT,
     * IRenderView.AR_MATCH_PARENT,
     * IRenderView.AR_16_9_FIT_PARENT,
     * IRenderView.AR_4_3_FIT_PARENT
    */
    void setAspectRatio(int aspectRatio);

    //改变视频缩放状态。
    int toggleAspectRatio();

    //设置视频路径。
    void setVideoPath(String path);

    //设置视频URI。（可以是网络视频地址）
    void setVideoURI(Uri uri);

    //停止视频播放，并释放资源。
    void stopPlayback();

    /**
     * 设置媒体控制器。
     * 参数controller:媒体控制器，注意是com.hx.ijkplayer_demo.widget.media.IMediaController。
     */
    void setMediaController(IMediaController controller);

    //改变媒体控制器显隐
    void toggleMediaControlsVisiblity();

    //注册一个回调函数，在视频预处理完成后调用。在视频预处理完成后被调用。此时视频的宽度、高度、宽高比信息已经获取到，此时可调用seekTo让视频从指定位置开始播放。
    void setOnPreparedListener(IMediaPlayer.OnPreparedListener l);

    //播放完成回调
    void setOnCompletionListener(IMediaPlayer.OnCompletionListener l);

    //播放错误回调
    void setOnErrorListener(IMediaPlayer.OnErrorListener l);

    //事件发生回调
    void setOnInfoListener(IMediaPlayer.OnInfoListener l);

    //获取总长度
    int getDuration();

    //获取当前播放位置。
    long getCurrentPosition();

    //设置播放位置。单位毫秒
    void seekTo(long msec);

    //是否正在播放。
    boolean isPlaying();

    //获取缓冲百分比。
    int getBufferPercentage();

}
