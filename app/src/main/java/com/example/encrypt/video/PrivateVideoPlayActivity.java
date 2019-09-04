package com.example.encrypt.video;

import android.content.Context;
import android.content.ContextWrapper;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.encrypt.R;
import com.example.encrypt.vault.PrivateVideoFragment;
import com.example.encrypt.activity.BaseActivity;
import com.example.encrypt.activity.BseApplication;



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
        videoView = (VideoView) findViewById(R.id.video_view);
        mediaController = new MyMediaController(PrivateVideoPlayActivity.this);
        videoView.setMediaController(mediaController);
        playVideo(videoPath);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BseApplication.editor.putBoolean("privVideoAlbumToVideoPlay", false).commit();

        PrivateVideoFragment.decryptVideosTemporary();
    }

    @Override
    protected void onPause() {
        super.onPause();

        PrivateVideoFragment.encryptVideosTemporary();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
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

    /**
     * 播放视频
     */
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
