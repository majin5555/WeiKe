package com.weike.util;

import android.app.Activity;
import android.content.ContentResolver;
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
        // String filePathByUri = UriTool.getFilePathByUri(context, uri);
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


        //        int id = mCursor.getColumnIndex(MediaStore.Images.Media._ID);
        //        String thumbPath  = MediaStore.Images.Media
        //                .EXTERNAL_CONTENT_URI
        //                .buildUpon()
        //                .appendPath(String.valueOf(mCursor.getInt(id))).build().toString();

    }

    /**
     * 保存视频文件
     */
    public static boolean saveVideo(String fileName, Context context) {
        Intent intent = null;
        ContentValues contentValues = new ContentValues();

        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, fileName);
        //兼容Android Q和以下版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, AppConfig.PATH_VIDEO_ROOT_ANDROIDQ);
        } else {
            contentValues.put(MediaStore.Video.Media.DATA, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + File.separator + AppConfig.PATH_WEIKE_ROOT + fileName);
        }
        contentValues.put(MediaStore.Video.VideoColumns.MIME_TYPE, "video/mp4");

        Uri uri = context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
        Log.d(TAG, "保存的视频 uri  " + uri);

        if (intent == null) {
            intent = new Intent();
        }
        intent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(uri);
        context.sendBroadcast(intent);

        return true;
    }

    /**
     * 查询视频
     * <p>
     * MediaStore.Video.Media.DISPLAY_NAME,//视频文件在sdcard的名称
     * MediaStore.Video.Media.DURATION,//视频总时长
     * MediaStore.Video.Media.SIZE,//视频的文件大小
     * MediaStore.Video.Media.DATA,//视频的绝对地址
     * MediaStore.Video.Media.ARTIST,//歌曲的演唱者
     */


    public static List<MediaDataVideos> selectVideos(Activity activity) {
        List<MediaDataVideos> videoList = new ArrayList<>();
        Uri mVideoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.Video.Thumbnails._ID
                , MediaStore.Video.Thumbnails.DATA
                , MediaStore.Video.Media.DURATION
                , MediaStore.Video.Media.SIZE
                , MediaStore.Video.Media.DATE_ADDED
                , MediaStore.Video.Media.DISPLAY_NAME
                , MediaStore.Video.Media.DATE_MODIFIED};
        //        //全部视频
        //        String where = MediaStore.Images.Media.MIME_TYPE + "=? or "
        //                + MediaStore.Video.Media.MIME_TYPE + "=? or "
        //                + MediaStore.Video.Media.MIME_TYPE + "=? or "
        //                + MediaStore.Video.Media.MIME_TYPE + "=? or "
        //                + MediaStore.Video.Media.MIME_TYPE + "=? or "
        //                + MediaStore.Video.Media.MIME_TYPE + "=? or "
        //                + MediaStore.Video.Media.MIME_TYPE + "=? or "
        //                + MediaStore.Video.Media.MIME_TYPE + "=? or "
        //                + MediaStore.Video.Media.MIME_TYPE + "=?";
        String[] whereArgs = {"video/mp4", "video/3gp", "video/aiv", "video/rmvb", "video/vob", "video/flv",
                "video/mkv", "video/mov", "video/mpg"};


        String selection = null;
        String[] selectionArgs = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

        } else {
            selection = MediaStore.Images.Media.MIME_TYPE + "=?";
            selectionArgs = new String[]{"video/mp4"};
        }
        Cursor mCursor = activity.getContentResolver().query(mVideoUri, projection, selection, selectionArgs, MediaStore.Video.Media.DATE_ADDED + " DESC ");

        if (mCursor != null) {
            while (mCursor.moveToNext()) {
                // 获取视频的路径
                int videoId = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Video.Media._ID));
                String path;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    path = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                            .buildUpon()
                            .appendPath(String.valueOf(videoId)).build().toString();
                } else {
                    path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DATA));
                }
                long duration = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                long size = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Video.Media.SIZE)) / 1024; //单位kb
                if (size < 0) {
                    //某些设备获取size<0，直接计算
                    //  Log.d("majin", "this video size < 0 " + path);
                    size = new File(path).length() / 1024;
                }
                String displayName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                //用于展示相册初始化界面
                int timeIndex = mCursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);
                long date = mCursor.getLong(timeIndex) * 1000;

                Log.d(TAG, "视频路径查询 1 path   " + path  );

                //需要判断当前文件是否存在  一定要加，不然有些文件已经不存在图片显示不出来。这里适配Android Q
             //   synchronized (activity) {
                    boolean fileIsExists;
                    Log.d(TAG, " ------------------------------------0  Build.VERSION.SDK_INT " + Build.VERSION.SDK_INT);
                    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
                        Log.d(TAG, "视频路径查询 2 path   " + path + ".mp4");
                        //  Log.d(TAG, " ------------------------------------1" + displayName);

                        // fileIsExists = isContentUriExists(activity, videoContentUri);
                        fileIsExists = fileIsExists(path + ".mp4");
                        Log.d(TAG, " -------------------------------------2");
                        if (fileIsExists(path + ".mp4") && fileIsExists(AppConfig.PATH_THUMBNAIL_ROOT + displayName + ".jpg")) {
                            Log.d(TAG, " -------------------------------------3   缩略图");
                            videoList.add(new MediaDataVideos(videoId, displayName, duration, size, date, path, UriTool.getVideoContentUri(activity, new File(path + ".mp4")), UriTool.getImageContentUri(activity, new File(AppConfig.PATH_THUMBNAIL_ROOT + displayName + ".jpg"))));
                        } else {
                            Log.d(TAG, " -------------------------------------4 文件不存在");
                        }
                    } else {
                        fileIsExists = fileIsExists(path);
                        if (fileIsExists) {
                            // videoList.add(new MediaDataVideos(videoId, displayName, duration, size, date, path, Uri.parse(path), null));
                        } else {
                        }
                    }
                }
            }
            mCursor.close();
      //  }
        return videoList;
    }

    /**
     * 根据媒体文件的ID来获取文件的Uri
     *
     * @param id
     * @return
     */
    public String getMediaFileUriFromID(String id) {
        return MediaStore.Files.getContentUri("external").buildUpon().appendPath(String.valueOf(id)).build().toString();
    }

    public static boolean isContentUriExists(Context context, Uri uri) {
        if (null == context) {
            return false;
        }
        ContentResolver cr = context.getContentResolver();
        try {
            AssetFileDescriptor afd = cr.openAssetFileDescriptor(uri, "r");
            if (null == afd) {
                return false;
            } else {
                try {
                    afd.close();
                } catch (IOException e) {
                }
            }
        } catch (FileNotFoundException e) {
            return false;
        }

        return true;
    }

    //判断文件是否存在
    public static boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (! f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        return true;
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

    public static Bitmap getVideoThumbnail(String uri) {
        MediaMetadataRetriever retr = new MediaMetadataRetriever();
        retr.setDataSource(uri, new HashMap<String, String>());
        Bitmap bitmap = retr.getFrameAtTime();
        return bitmap;
    }


}
