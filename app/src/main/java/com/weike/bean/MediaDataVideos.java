package com.weike.bean;

import android.net.Uri;

import java.io.Serializable;

/**
 * @author: mj
 * @date: 2020/5/15$
 * @desc:
 */
public class MediaDataVideos  implements Serializable {
    private int    videoId;//MediaStore.Video.Media._ID
    private String name;//MediaStore.Video.Media.DISPLAY_NAME
    private long   duration;//MediaStore.Video.Media.DURATION
    private long   size;//时长MediaStore.Video.Media.SIZE
    private long   date;//
    private String path;//地址
    private Uri   videoUri;//MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    private Uri thumbnailUri;
    private boolean isDelete;

    public MediaDataVideos(int videoId, String name, long duration, long size, long date, String path, Uri videoUri, Uri thumbnailUri) {
        this.videoId = videoId;
        this.name = name;
        this.duration = duration;
        this.size = size;
        this.date = date;
        this.path = path;
        this.videoUri = videoUri;
        this.thumbnailUri = thumbnailUri;
    }

    public int getVideoId() {
        return videoId;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public long getSize() {
        return size;
    }

    public String getPath() {
        return path == null ? "" : path;
    }

    public Uri getVideoUri() {
        return videoUri;
    }

    public long getDuration() {
        return duration;
    }

    public long getDate() {
        return date;
    }

    public Uri getThumbnailUri() {
        return thumbnailUri;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    @Override
    public String toString() {
        return "MediaDataVideos{" +
                "videoId=" + videoId +
                ", name='" + name + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", date=" + date +
                ", path='" + path + '\'' +
                ", videoUri=" + videoUri +
                ", thumbnailUri=" + thumbnailUri +
                ", isDelete=" + isDelete +
                '}';
    }
}
