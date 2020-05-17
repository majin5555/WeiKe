package com.weike.contral;

import android.app.Activity;
import android.util.Log;

import com.weike.BuildConfig;
import com.weike.screenshot.encoder.MediaAudioEncoder;
import com.weike.screenshot.encoder.MediaEncoder;
import com.weike.screenshot.encoder.MediaMuxerWrapper;
import com.weike.screenshot.encoder.MediaVideoEncoder;

import java.io.IOException;

/**
 * @author: mj
 * @date: 2020/5/13$
 * @desc: 音视频 编码解码工具类
 */
public class MediaEncoderContral {
    private static final String TAG = "SoftInputSurface";

    private MediaMuxerWrapper mMuxer;
    private MediaAudioEncoder mediaAudioEncoder;//音频
    private MediaVideoEncoder mediaVideoEncoder;//视频

    public MediaMuxerWrapper getmMuxer() {
        return mMuxer;
    }

    public void setmMuxer(MediaMuxerWrapper mMuxer) {
        this.mMuxer = mMuxer;
    }

    public MediaAudioEncoder getMediaAudioEncoder() {
        return mediaAudioEncoder;
    }

    public void setMediaAudioEncoder(MediaAudioEncoder mediaAudioEncoder) {
        this.mediaAudioEncoder = mediaAudioEncoder;
    }

    public MediaVideoEncoder getMediaVideoEncoder() {
        return mediaVideoEncoder;
    }

    public void setMediaVideoEncoder(MediaVideoEncoder mediaVideoEncoder) {
        this.mediaVideoEncoder = mediaVideoEncoder;
    }


    private Activity             activity;
    private SketchpadViewContral sketchpadViewContral;

    /**
     * 构造方法初始化数据
     *
     * @param activity
     * @param sketchpadViewContral
     */
    public MediaEncoderContral(Activity activity, SketchpadViewContral sketchpadViewContral) {
        this.activity = activity;
        this.sketchpadViewContral = sketchpadViewContral;
    }


    public void startRecord() {
        try {
            //if you record audio only, ".m4a" is also OK.
            mMuxer = new MediaMuxerWrapper(".mp4");
            // for audio capturing
            mediaAudioEncoder = new MediaAudioEncoder(mMuxer, mMediaEncoderListener);
            /*判断width width是奇数报错 原因不详*/
            int width = 0;
            int height = 0;
            int w = sketchpadViewContral.getSketchpadView().getWidthSize() % 16;
            int h = sketchpadViewContral.getSketchpadView().getHeightSize() % 16;

            width = sketchpadViewContral.getSketchpadView().getWidthSize() - (w);
            height = sketchpadViewContral.getSketchpadView().getHeightSize() - (h);

            Log.d(TAG, "  width  " + width);
            Log.d(TAG, "   height  " + height);

            Log.d(TAG, "   sketchpadViewContral.getSketchpadView().getWidthSize()  " + sketchpadViewContral.getSketchpadView().getWidthSize() + "  sketchpadViewContral.getSketchpadView().getHeightSize() " + sketchpadViewContral.getSketchpadView().getHeightSize());
            // for video capturing
            mediaVideoEncoder = new MediaVideoEncoder(mMuxer, mMediaEncoderListener, width, height);


            mMuxer.prepare();
            mMuxer.startRecording();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * callback methods from encoder
     */
    private final MediaEncoder.MediaEncoderListener mMediaEncoderListener = new MediaEncoder.MediaEncoderListener() {
        @Override
        public void onPrepared(final MediaEncoder encoder) {
            if (encoder instanceof MediaVideoEncoder) {//视频

            } else {

            }

        }

        @Override
        public void onStopped(final MediaEncoder encoder) {
            if (encoder instanceof MediaVideoEncoder) {

            } else {

            }

        }
    };

    public void stopRecord() {
        if (BuildConfig.DEBUG) Log.d(TAG, "releasing encoder objects");

        /**让截图线程等待100毫秒*/
        synchronized (sketchpadViewContral.getCaptureBitmapThread()) {
            try {
                sketchpadViewContral.getCaptureBitmapThread().wait(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.d(TAG, "等待100出错啦   " + e.getMessage());
            }
        }

        if (mMuxer != null) {
            mMuxer.stopRecording();
            mMuxer = null;
        }
    }





}
