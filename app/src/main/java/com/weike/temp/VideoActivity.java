package com.weike.temp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.weike.R;
import com.weike.bean.MediaDataVideos;

import androidx.appcompat.app.AppCompatActivity;

public class VideoActivity extends AppCompatActivity {

    public static void startVideoActivity(Activity context, int requestCode, MediaDataVideos mediaDataVideos) {
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra("DataVideos", mediaDataVideos);
        context.startActivityForResult(intent, requestCode);
    }

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        videoView = findViewById(R.id.videoView);
        Intent intent = getIntent();
        MediaDataVideos dataVideos = (MediaDataVideos) intent.getParcelableExtra("DataVideos");
        play_mp4(dataVideos);

    }

    //    private void play_rtsp()   {
    //        String videoUrl2 = "rtsp://192.168.110.227:1935/vod/sample.mp4" ;
    //        Uri uri = Uri.parse( videoUrl2 );
    //        videoView = (VideoView)this.findViewById(R.id.videoView );
    //        //videoView.setVideoPath(path);
    //        videoView.setVideoURI(uri);
    //        videoView.requestFocus();
    //        videoView.start();
    //    }

    private void play_mp4(MediaDataVideos dataVideos) {

        Uri uri = Uri.parse(dataVideos.getPath());
        videoView = (VideoView) this.findViewById(R.id.videoView);
        videoView.setMediaController(new MediaController(this));
        videoView.setVideoURI(uri);
        videoView.start();
    }


}
