package com.weike.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.weike.R;
import com.weike.bean.MediaDataVideos;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 1、继承RecyclerView.Adapter
 * 2、绑定ViewHolder
 * 3、实现Adapter的相关方法
 */
public class VideoListRecyclerViewAdapter extends RecyclerView.Adapter<VideoListRecyclerViewAdapter.MyViewHolder> {

    private static final String TAG = "VideoListRecyclerViewAd";

    private OnItemClickListener onItemClickListener;

    private RecyclerView mRv;

    private Context mContext;
    private int addDataPosition = -1;
    private List<MediaDataVideos> mediaDataVideos;
    private boolean isShow;


    public VideoListRecyclerViewAdapter(Context context, RecyclerView recyclerView, List<MediaDataVideos> mediaDataVideos) {
        this.mContext = context;
        this.mRv = recyclerView;
        this.mediaDataVideos = mediaDataVideos;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public void setDataSource(List<MediaDataVideos> mediaDataVideos) {
        this.mediaDataVideos = mediaDataVideos;
        notifyDataSetChanged();
    }


    public void flush(boolean isShow, List<MediaDataVideos> mediaDataVideos) {
        this.isShow = isShow;
        this.mediaDataVideos = mediaDataVideos;
        notifyDataSetChanged();
    }

    /**
     * 创建并且返回ViewHolder
     *
     * @param viewGroup
     * @param position
     * @return
     */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_layout, viewGroup, false));
    }

    /**
     * ViewHolder 绑定数据
     *
     * @param myViewHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, @SuppressLint("RecyclerView") int position) {


        Glide.with(mContext).load(mediaDataVideos.get(position).getPath()).into(myViewHolder.mIv);


        myViewHolder.mTv.setText(mediaDataVideos.get(position).getName());

        if (!isShow) {
            myViewHolder.checkbox.setVisibility(View.GONE);
            myViewHolder.checkbox.setChecked(true);

        } else {
            myViewHolder.checkbox.setVisibility(View.VISIBLE);
            myViewHolder.checkbox.setChecked(false);


        }


        myViewHolder.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                调用接口的回调方法
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });

        myViewHolder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaDataVideos.get(position).setDelete(myViewHolder.checkbox.isChecked());
            }
        });

    }


    /**
     * 返回数据数量
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return mediaDataVideos.size();
    }


    /**
     * 返回不同的ItemView高度
     *
     * @return
     */
    private int getRandomHeight() {
        return (int) (Math.random() * 1000);
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        View mItemView;
        ImageView mIv;
        TextView mTv;
        CheckBox checkbox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mIv = itemView.findViewById(R.id.iv);
            mTv = itemView.findViewById(R.id.tv);
            checkbox = itemView.findViewById(R.id.checkbox);
            mItemView = itemView;
        }
    }

    /**
     * ItemView点击事件回调接口
     */
    public interface OnItemClickListener {
        void onItemClick(int position);
    }


}
