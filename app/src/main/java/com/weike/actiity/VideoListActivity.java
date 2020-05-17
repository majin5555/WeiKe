package com.weike.actiity;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.weike.R;
import com.weike.adapter.VideoListRecyclerViewAdapter;
import com.weike.bean.MediaDataVideos;
import com.weike.customview.CommomDialog;
import com.weike.util.MediaUtils;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import baseLibrary.activity.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 视频列表
 */
public class VideoListActivity extends BaseActivity {

    private static final String TAG = "VideoListActivity";

    private static final int REQUEST_CODE = 0;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.attach)
    ImageView    mAttach;
    @BindView(R.id.edit)
    TextView     mEdit;

    private boolean isEdedit;


    private VideoListRecyclerViewAdapter mAdapter;
    private List<MediaDataVideos>        mediaDataVideos;
    private CommomDialog                 commomDialog;


    @Override
    public void initView() {
        setContentView(R.layout.activity_videolist);
        ButterKnife.bind(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaDataVideos = MediaUtils.selectVideos(VideoListActivity.this);
        Log.d(TAG, " mediaDataVideos  " + mediaDataVideos.size());


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

        //        itemView点击事件监听
        //        //mAdapter.setOnItemClickListener(new VideoListRecyclerViewAdapter.OnItemClickListener() {
        //            @Override
        //            public void onItemClick(int position) {
        //                Toast.makeText(VideoListActivity.this, "第" + position + "数据被点击", Toast.LENGTH_SHORT).show();
        //            }
        //        });
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
                isEdedit = ! isEdedit;
                if (isEdedit) {
                    mEdit.setText(R.string.videolist_delete);
                } else {
                    mEdit.setText(R.string.videolist_edit);
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
                MediaUtils.deleteFile(context, mediaDataVideos.get(i).getThumbnailUri());
                mediaDataVideos.remove(mediaDataVideos.get(i));
            }
        }

        mAdapter.flush(isEdedit, mediaDataVideos);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && requestCode == RESULT_OK) {
            Log.d(TAG, " onActivityResult  ");
            mediaDataVideos = MediaUtils.selectVideos(VideoListActivity.this);
            mAdapter.setDataSource(mediaDataVideos);
            // Log.d(TAG, " mediaDataVideos  " + mediaDataVideos.toString());
        }
    }

}
