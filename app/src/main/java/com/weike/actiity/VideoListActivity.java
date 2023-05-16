package com.weike.actiity;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import com.weike.R;
import com.weike.adapter.VideoListRecyclerViewAdapter;
import com.weike.bean.MediaDataVideos;
import com.weike.customview.CommomDialog;
import com.weike.util.MediaUtils;
import com.weike.util.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import baseLibrary.activity.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 视频列表
 */
public class VideoListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    public static void startVideoListActivity(Activity context, int requestCode) {
        Intent intent = new Intent(context, VideoListActivity.class);
        context.startActivityForResult(intent, requestCode);
    }


    public static List<String> sNeedReqPermissions = new ArrayList<>();

    static {
        sNeedReqPermissions.add(Manifest.permission.CAMERA);
        sNeedReqPermissions.add(Manifest.permission.RECORD_AUDIO);
        sNeedReqPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        sNeedReqPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private PermissionUtils mPermissionUtils;
    public static final int PERMISSION_RQUEST_CODE = 100;

    private static final String TAG = "VideoListActivity";

    private static final int REQUEST_CODE = 0;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.attach)
    ImageView mAttach;
    @BindView(R.id.edit)
    TextView mEdit;
    @BindView(R.id.SwipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private boolean isEdedit;

    private VideoListRecyclerViewAdapter mAdapter;
    private List<MediaDataVideos> mediaDataVideos;
    private CommomDialog commomDialog;


    @Override
    public void initView() {
        setContentView(R.layout.activity_videolist);
        ButterKnife.bind(this);
        //首先判断当前的权限问题
        mPermissionUtils = new PermissionUtils(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mPermissionUtils.request(sNeedReqPermissions, PERMISSION_RQUEST_CODE, new PermissionUtils.CallBack() {
                @Override
                public void grantAll() {
                    //Toast.makeText(SketchpadMainActivity.this, "获取了全部权限", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void denied() {
                    //Toast.makeText(SketchpadMainActivity.this, "有权限未获取", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        swipeRefreshLayout.setOnRefreshListener(this);
        mediaDataVideos = MediaUtils.getAllMediaVideos(this);

        // 线性布局
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        mRecyclerView.setLayoutManager(gridLayoutManager);
        mAdapter = new VideoListRecyclerViewAdapter(this, mRecyclerView, mediaDataVideos);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        commomDialog = new CommomDialog(VideoListActivity.this, R.style.dialog, "确定要删除么？", new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    deleteVideos();
                } else {
                    //更改删除标志为false
                    for (int i = 0; i < mediaDataVideos.size(); i++) {
                        if (mediaDataVideos.get(i).isDelete()) {
                            mediaDataVideos.get(i).setDelete(false);
                        }
                    }
                    mAdapter.flush(isEdedit, mediaDataVideos);
                }

                dialog.dismiss();
            }
        });


        mAdapter.setOnItemClickListener(position -> {

//            MediaDataVideos mediaDataVideos = VideoListActivity.this.mediaDataVideos.get(position);
//            VideoPayerActivity.startHPlayerActivity(VideoListActivity.this, 1000, mediaDataVideos);
//
           //调用系统播放器
            String extension = MimeTypeMap.getFileExtensionFromUrl(VideoListActivity.this.mediaDataVideos.get(position).getPath());
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            Intent mediaIntent = new Intent(Intent.ACTION_VIEW);
            mediaIntent.setDataAndType(VideoListActivity.this.mediaDataVideos.get(position).getVideoUri(), mimeType);
            startActivity(mediaIntent);

        });

    }

    /**
     * 获取删除标志位 true 提示删除框
     *
     * @return
     */
    private boolean deleteFlag() {
        for (int i = 0; i < mediaDataVideos.size(); i++) {
            if (mediaDataVideos.get(i).isDelete()) {
                return true;
            }
        }
        return false;
    }


    @OnClick({R.id.attach, R.id.edit})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.edit:
                isEdedit = !isEdedit;
                if (isEdedit) {
                    mEdit.setText(R.string.videolist_delete);
                    // mAttach.setVisibility(View.INVISIBLE);
                } else {
                    mEdit.setText(R.string.videolist_edit);
                    //  mAttach.setVisibility(View.VISIBLE);
                }

                if (deleteFlag()) {
                    commomDialog.setTitle("提示").show();
                    return;
                }
                mAdapter.flush(isEdedit, mediaDataVideos);
                break;
            case R.id.attach:

                Animation animation = AnimationUtils.loadAnimation(VideoListActivity.this, R.anim.ref_anim);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        SketchpadMainActivity.startSketchpadActivity(VideoListActivity.this, REQUEST_CODE);

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                mAttach.startAnimation(animation);

                break;

            default:
                break;
        }

    }


    private void deleteVideos() {

        for (int i = 0; i < mediaDataVideos.size(); i++) {
            if (mediaDataVideos.get(i).isDelete()) {
                MediaUtils.deleteFile(context, mediaDataVideos.get(i).getVideoUri());
                //  MediaUtils.deleteFile(context, mediaDataVideos.get(i).getThumbnailUri());
                mediaDataVideos.remove(mediaDataVideos.get(i));
            }
        }

        mAdapter.flush(isEdedit, mediaDataVideos);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && requestCode == RESULT_OK) {
            mediaDataVideos = MediaUtils.getAllMediaVideos(this);
            Log.d(TAG, "2 mediaDataVideos  onActivityResult  " + mediaDataVideos);
            mAdapter.setDataSource(mediaDataVideos);
            // Log.d(TAG, " mediaDataVideos  " + mediaDataVideos.toString());
        }
    }

    @Override
    public void onRefresh() {
        mediaDataVideos = MediaUtils.getAllMediaVideos(this);
        mAdapter.setDataSource(mediaDataVideos);
        swipeRefreshLayout.setRefreshing(false);
    }
}
