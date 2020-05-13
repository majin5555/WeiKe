package com.weike.contral;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.text.format.Formatter;

import java.util.List;

/**
 * Created by majin on 2016/12/30.
 */

public class ClearMempry {

    public static void ClearMempry(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);//mi.availMem; 当前系统的可用内存
        Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化


        List<ActivityManager.RunningAppProcessInfo> list = am
                .getRunningAppProcesses();
        if (list != null)
            for (int i = 0; i < list.size(); i++) {
                ActivityManager.RunningAppProcessInfo apinfo = list.get(i);
                String[] pkgList = apinfo.pkgList;
                if (apinfo.importance >= ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (int j = 0; j < pkgList.length; j++) {
                        if (pkgList[j].equals(context.getPackageName())) {
                            continue;
                        }
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.FROYO) {
                            am.restartPackage(pkgList[j]);
                        } else {
                            am.killBackgroundProcesses(pkgList[j]);
                        }
                    }
                }
            }
    }

}
