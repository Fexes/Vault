package com.example.encrypt.video;

import android.content.Context;
import android.content.ContextWrapper;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.Nullable;

import com.example.encrypt.R;
import com.example.encrypt.activity.BaseActivity;



public class PrivateVideoPlayActivity extends BaseActivity {
    private VideoView videoView;
    private MyMediaController mediaController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        addAppActivity(PrivateVideoPlayActivity.this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        String videoPath = getIntent().getStringExtra("videoPath");
        videoView = findViewById(R.id.video_view);
        mediaController = new MyMediaController(PrivateVideoPlayActivity.this);
        videoView.setMediaController(mediaController);
        playVideo(videoPath);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //  BseApplication.editor.putBoolean("privVideoAlbumToVideoPlay", false).commit();


    }

    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new ContextWrapper(newBase) {
            @Override
            public Object getSystemService(String name) {
                if (Context.AUDIO_SERVICE.equals(name))
                    return getApplicationContext().getSystemService(name);
                return super.getSystemService(name);
            }
        });
    }


    public void playVideo(String videoPath) {
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                finish();
            }
        });
    }


    class MyMediaController extends MediaController {

        public MyMediaController(Context context) {
            super(context);
        }

        @Override
        public void show() {
            show(5 * 1000);
        }
    }


}
