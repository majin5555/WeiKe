package com.weike.bean;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author: mj
 * @date: 2020/5/15$
 * @desc:
 */
public class MediaDataVideos  implements Parcelable {
    private int    videoId;//MediaStore.Video.Media._ID
    private String name;//MediaStore.Video.Media.DISPLAY_NAME

    private long   date;//
    private String path;//地址
    private Uri   videoUri;//MediaStore.Video.Media.EXTERNAL_CONTENT_URI

    private boolean isDelete;

    public MediaDataVideos(int videoId, String name , long date, String path, Uri videoUri ) {
        this.videoId = videoId;
        this.name = name;

        this.date = date;
        this.path = path;
        this.videoUri = videoUri;

    }

    protected MediaDataVideos(Parcel in) {
        videoId = in.readInt();
        name = in.readString();
        date = in.readLong();
        path = in.readString();
        videoUri = in.readParcelable(Uri.class.getClassLoader());
        isDelete = in.readByte() != 0;
    }

    public static final Creator<MediaDataVideos> CREATOR = new Creator<MediaDataVideos>() {
        @Override
        public MediaDataVideos createFromParcel(Parcel in) {
            return new MediaDataVideos(in);
        }

        @Override
        public MediaDataVideos[] newArray(int size) {
            return new MediaDataVideos[size];
        }
    };

    public int getVideoId() {
        return videoId;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public String getPath() {
        return path == null ? "" : path;
    }

    public Uri getVideoUri() {
        return videoUri;
    }

    public long getDate() {
        return date;
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
                ", date=" + date +
                ", path='" + path + '\'' +
                ", videoUri=" + videoUri +
                ", isDelete=" + isDelete +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(videoId);
        dest.writeString(name);
        dest.writeLong(date);
        dest.writeString(path);
        dest.writeParcelable(videoUri, flags);
        dest.writeByte((byte) (isDelete ? 1 : 0));
    }
}
