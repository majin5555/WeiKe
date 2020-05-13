package com.weike.app;

import android.app.Activity;
import android.app.Application;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.util.ArrayList;

/**
 * @author: majin
 * @date: 2020/5/11$
 * @desc:
 */
public class App extends Application {
    public static ArrayList<Activity> Alist = new ArrayList<Activity>();

    private FFmpeg fFmpeg;

    public FFmpeg getfFmpeg() {
        return fFmpeg;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        loadFFMpegLib();
    }

    /**
     * Activity关闭时，删除Activity列表中的Activity对象
     */
    public static void removeActivity(Activity a) {
        Alist.remove(a);
    }

    /**
     * 向Activity列表中添加Activity对象
     */
    public static void addActivity(Activity a) {
        Alist.add(a);
    }

    /**
     * 关闭Activity列表中的所有Activity
     */
    public void finishActivity() {
        for (Activity activity : Alist) {
            if (null != activity) {
                activity.finish();
            }
        }
        //杀死该应用进程
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 导入ffmpeg库
     */
    private void loadFFMpegLib() {
        //获得实例和载入库
        fFmpeg = FFmpeg.getInstance(this);
        try {
            fFmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {
                }

                @Override
                public void onFailure() {
                }

                @Override
                public void onSuccess() {
                }

                @Override
                public void onFinish() {
                }
            });
        } catch (FFmpegNotSupportedException e) {

        }
    }
}
