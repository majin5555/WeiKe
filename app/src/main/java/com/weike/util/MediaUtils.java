package com.weike.util;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.weike.app.AppConfig;
import com.weike.bean.MediaDataVideos;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author: mj
 * @date: 2020/5/14$
 * @desc:
 */
public class MediaUtils {

    private static final String TAG = "MediaUtils";

    /**
     * 保存图片到文件夹
     */

    public static Uri saveToDcim(Bitmap bitmap, String filename, Context context) {
        ContentValues contentValues = null;
        Intent intent = null;

        Uri uri;
        if (contentValues == null) {
            contentValues = new ContentValues();

        }

        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, filename + ".jpg");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, filename + ".jpg");
        //  Log.d(TAG, "Build.VERSION.SDK_INT  " + Build.VERSION.SDK_INT);

        //兼容Android Q和以下版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //android Q中不再使用DATA字段，而用RELATIVE_PATH代替
            //RELATIVE_PATH是相对路径不是绝对路径
            //DCIM是系统文件夹，关于系统文件夹可以到系统自带的文件管理器中查看，不可以写没存在的名字
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, AppConfig.PATH_THUMBNAIL_ROOT_ANDROIDQ);
        } else {
            contentValues.put(MediaStore.Images.Media.DATA, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + AppConfig.PATH_WEIKE_ROOT + filename + ".jpg");
        }
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        OutputStream outputStream = null;
        Log.d(TAG, "截取缩略图  的uri  " + uri);
        String filePathByUri = UriTool.getFilePathByUri(context, uri);
        // Log.d(TAG, "截取缩略图  的video filePathByUri  " + filePathByUri);

        try {
            outputStream = context.getContentResolver().openOutputStream(uri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            return uri;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (intent == null) {
            intent = new Intent();
        }
        intent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(uri);
        context.sendBroadcast(intent);
        return null;
    }


    /**
     * 查询图片
     */
    public static void selectImages(Activity activity) {
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Thumbnails.DATA
        };
        //全部图片
        String where = MediaStore.Images.Media.MIME_TYPE + "=? or "
                + MediaStore.Images.Media.MIME_TYPE + "=? or "
                + MediaStore.Images.Media.MIME_TYPE + "=?";
        //指定格式
        String[] whereArgs = {"image/jpeg", "image/png", "image/jpg"};
        //查询
        Cursor mCursor = activity.getContentResolver().query(
                mImageUri, projection, null, null,
                null);
        if (mCursor != null) {
            while (mCursor.moveToNext()) {
                // 获取图片的路径
                int thumbPathIndex = mCursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA);
                int timeIndex = mCursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);
                int pathIndex = mCursor.getColumnIndex(MediaStore.Images.Media.DATA);
                int id = mCursor.getColumnIndex(MediaStore.Images.Media._ID);

                Long date = mCursor.getLong(timeIndex) * 1000;
                String filepath, thumbPath;
                //适配Android Q
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
                    thumbPath = MediaStore.Images.Media
                            .EXTERNAL_CONTENT_URI
                            .buildUpon()
                            .appendPath(String.valueOf(mCursor.getInt(id))).build().toString();
                    filepath = thumbPath;
                    //                    if (FileManagerUtils.isContentUriExists(MSApplication.getmContext(), Uri.parse(filepath))) {
                    //                        MediaData fi = new MediaData(id, MediaConstant.IMAGE, filepath, "", getPhotoUri(mCursor), date, "", false);
                    //                        mediaBeen.add(fi);
                    //                    }
                } else {
                    thumbPath = mCursor.getString(thumbPathIndex);
                    filepath = mCursor.getString(pathIndex);
                    //判断文件是否存在，存在才去加入
                    //                    boolean b = FileManagerUtils.fileIsExists(filepath);
                    //                    if (b) {
                    //                        File f = new File(filepath);
                    //                        MediaData fi = new MediaData(id, MediaConstant.IMAGE, filepath, thumbPath, null, date, f.getName(), false);
                    //                        mediaBeen.add(fi);
                    //                    }
                }
            }
            mCursor.close();
        }
    }


    /**
     * 对文件进行写操作
     *
     * @param uri
     */
    private void writeFile(Context context, Uri uri) {
        try {
            AssetFileDescriptor assetFileDescriptor = context.getContentResolver().openAssetFileDescriptor(uri, "rw");
            FileOutputStream outputStream = assetFileDescriptor.createOutputStream();
            String str = "123";
            outputStream.write(str.getBytes());
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
            bufferedWriter.write(str);
            bufferedWriter.close();
            outputStream.close();
            //  Log.e(TAG, "updateFile: " + uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除文件
     */
    public static void deleteFile(Context context, Uri uri) {
        int deleteNum = context.getContentResolver().delete(uri, null, null);
        // Log.e(TAG, "deleteFile: " + deleteNum);
    }

    /**
     * 获取缩略图
     */
    public static Bitmap getVideoThumbnail(String uri) {
        MediaMetadataRetriever retr = new MediaMetadataRetriever();
        retr.setDataSource(uri, new HashMap<String, String>());
        Bitmap bitmap = retr.getFrameAtTime();
        return bitmap;
    }


    /** ------------------------------------------------------------------------------------------------ */
    /**
     * 按条件 查询所有视频
     */
    public static List<MediaDataVideos> getAllMediaVideos(Context content) {
        List<MediaDataVideos> videoList = new ArrayList<>();
        Cursor mCursor = null;
        try {
            Uri targetUrl = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            String[] projection = new String[]{
                    MediaStore.Video.Media.DATA,
                    MediaStore.Video.Media.DISPLAY_NAME,
                    MediaStore.Video.Media.DATE_ADDED,
                    MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.MIME_TYPE,
                    MediaStore.Video.Media.RELATIVE_PATH
            };
            String selection = null;
            String[] selectionArgs = null;
            String sortOrder = MediaStore.Video.Media.DATE_MODIFIED + " DESC";
            //TODO  兼容Android Q和以下版本
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                selection = MediaStore.Video.Media.RELATIVE_PATH + "=? ";
                selectionArgs = new String[]{AppConfig.PATH_VIDEO_ROOT_ANDROIDQ};
                mCursor = content.getContentResolver().query(targetUrl, projection, selection, selectionArgs, sortOrder);

            } else {
                selection = MediaStore.Video.Media.DATA + " like '%" + AppConfig.PATH_VIDEO_ROOT + "%'";
                selectionArgs = null;
                mCursor = content.getContentResolver().query(targetUrl, null, selection, selectionArgs, sortOrder);
            }

            if (mCursor != null && mCursor.moveToFirst()) {
                do {
                    int videoId = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Video.Media._ID));
                    // 获取视频的路径
                    String filePath = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DATA));
                    String displayName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));

                    int timeIndex = mCursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);
                    long date = mCursor.getLong(timeIndex) * 1000;
                    Uri uriVideo = Uri.parse("content://media/external/video/media/" + videoId);
                    videoList.add(new MediaDataVideos(videoId, displayName, date, filePath, uriVideo));

                } while (mCursor.moveToNext());
            }
        } catch (Exception e) {
            //Log.d(TAG, "-------------------5");
            e.printStackTrace();
        } finally {
            // Log.d(TAG, "-------------------6");
            try {
                if (mCursor != null) {
                    mCursor.close();
                }
            } catch (Exception e) {
            }
        }
        Log.d("VideoListActivity", "videoList  " + videoList);
        return videoList;
    }

    /**
     * 数显系统视频文件
     */
    public static boolean flushVideo(File file, Context context) {
        Intent intent = null;
        ContentValues contentValues = new ContentValues();
        // Log.d("majin", "file.getName() " + file.getName());
        //兼容Android Q和以下版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, AppConfig.PATH_VIDEO_ROOT_ANDROIDQ);
        } else {
            contentValues.put(MediaStore.Video.Media.DATA, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + File.separator + AppConfig.PATH_WEIKE_ROOT + file.getName());
        }

        contentValues.put(MediaStore.Video.Media.TITLE, file.getName());
        contentValues.put(MediaStore.Video.Media.DESCRIPTION, file.getName());
        contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, file.getName());
        contentValues.put(MediaStore.Video.VideoColumns.MIME_TYPE, "video/mp4");
        contentValues.put(MediaStore.Video.Media.DATA, file.getAbsolutePath());
        contentValues.put(MediaStore.Video.Media.SIZE, Long.valueOf(file.length()));
        //        contentValues.put("datetaken", Long.valueOf(paramLong));
        //        contentValues.put("date_modified", Long.valueOf(paramLong));
        //        contentValues.put("date_added", Long.valueOf(paramLong));

        Log.d(TAG, "保存的视频 长度  " + Long.valueOf(file.length()));
        Uri uri = context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
        //    Log.d(TAG, "保存的视频 uri  " + uri);

        if (intent == null) {
            intent = new Intent();
        }
        intent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(uri);
        context.sendBroadcast(intent);
        return true;
    }

    /**------------------------------------------------------------------------------------------------*/

}
