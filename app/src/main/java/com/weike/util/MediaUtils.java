package com.weike.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import com.weike.app.AppConfig;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: mj
 * @date: 2020/5/14$
 * @desc:
 */
public class MediaUtils {

    /**
     * 保存图片到文件夹
     */

    public static boolean saveToDcim(Bitmap bitmap, String filename, Context context) {
        ContentValues contentValues = null;
        Intent intent = null;
        List<Uri> uriList = null;
        Uri uri;
        if (contentValues == null) {
            contentValues = new ContentValues();
            uriList = new ArrayList<>();
        }

        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, filename);
        //  Log.d(TAG, "Build.VERSION.SDK_INT  " + Build.VERSION.SDK_INT);

        //兼容Android Q和以下版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //android Q中不再使用DATA字段，而用RELATIVE_PATH代替
            //RELATIVE_PATH是相对路径不是绝对路径
            //DCIM是系统文件夹，关于系统文件夹可以到系统自带的文件管理器中查看，不可以写没存在的名字
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, AppConfig.PATH_THUMBNAIL_ROOT_ANDROIDQ);
        } else {
            contentValues.put(MediaStore.Images.Media.DATA, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM ) + File.separator + AppConfig.PATH_THUMBNAIL_ROOT + filename);
        }
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        OutputStream outputStream = null;
        try {
            outputStream = context.getContentResolver().openOutputStream(uri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
            outputStream.flush();
            outputStream.close();
            uriList.add(uri);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        if (intent == null) {
            intent = new Intent();
        }
        intent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(uri);
        context.sendBroadcast(intent);
        return true;
    }
}
