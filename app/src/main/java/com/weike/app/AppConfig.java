package com.weike.app;

import android.os.Environment;

import java.io.File;


public class AppConfig {
    // Singleton

    private static AppConfig instance;

    private AppConfig() {

    }

    //写成静态单利
    public static AppConfig getInstance() {

        if (instance == null) {

            instance = new AppConfig();
        }
        return instance;
    }

    // Const Config
    public static final String envConfig = Environment.getExternalStorageDirectory().getAbsolutePath();

    //devices data  设备的宽高
    public static int screenWidth;
    public static int screenHeight;

    private static final String LECTURE    = "mlecture" + File.separator;
    //视频片段临时文件夹 /storage/emulated/0/Movies/Weike/TempAVRecSample/
    public static final  String WEIKE      = "Weike" + File.separator;
    public static final  String VIDEO_TEMP = "TempAVRecSample" + File.separator;
    public static final  String VIDEO      = Environment.DIRECTORY_MOVIES + File.separator + AppConfig.WEIKE;


    //微课视频地址
    public static final String PATH_VIDEO_ROOT = envConfig + File.separator + VIDEO + LECTURE;//微课视频地址

    //微课缩略图地址
    public static final String PATH_THUMBNAIL_ROOT_ANDROIDQ = Environment.DIRECTORY_DCIM + File.separator + WEIKE + LECTURE;
    public static final String PATH_THUMBNAIL_ROOT          = WEIKE + LECTURE;


    public void init() {
        // App Init
        File folder = null;

        folder = new File(this.PATH_VIDEO_ROOT);
        if (! folder.exists()) {
            folder.mkdir();
        }
    }

    public static String getPathRoot() {
        return PATH_VIDEO_ROOT == null ? "" : PATH_VIDEO_ROOT;
    }
}
