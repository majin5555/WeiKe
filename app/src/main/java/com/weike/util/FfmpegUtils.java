package com.weike.util;

/**
 * @author: majin
 * @date: 2020/5/13$
 * @desc:
 */
public class FfmpegUtils {

    //双重检查锁
    private static FfmpegUtils singObj;

    private FfmpegUtils() {
    }

    public static FfmpegUtils getSingleInstance() {
        if (null == singObj) {
            synchronized (FfmpegUtils.class) {
                if (null == singObj)
                    singObj = new FfmpegUtils();
            }
        }
        return singObj;
    }


}