package com.example.ijkplayerlibrary.ijkPlayer.listener;

import android.widget.ImageView;

/**
 * =======================================
 * <p>
 * 作 者：马晋
 * ========================================
 */
public interface OnShowThumbnailListener {

    /**回传封面的view，让用户自主设置*/
    void onShowThumbnail(ImageView ivThumbnail);
}