package com.example.encrypt.video;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.encrypt.R;
import com.example.encrypt.activity.AdvancedSetup;
import com.example.encrypt.activity.BaseActivity;
import com.example.encrypt.database.DatabaseAdapter;
import com.example.encrypt.database.PsDatabaseHelper;
import com.example.encrypt.photo.AlbumHelper;
import com.example.encrypt.photo.Bimp;

import com.example.encrypt.photo.Gallery;
import com.example.encrypt.util.Notifi;
import com.example.encrypt.util.XorEncryptionUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;



public class VideoAlbum extends BaseActivity implements OnClickListener {
    private GridView gridView;
    private VideoAlbumGridViewAdapter gridVideoAdapter;
    public static ArrayList<VideoItem> videoList;
    private EncryptionTask mTask = null;
    public static ExecutorService executorService;
    private static DatabaseAdapter databaseAdapter;
    TextView file_count;
   public static Button button_add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        addAppActivity(VideoAlbum.this);
        file_count=findViewById(R.id.file_count);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
         button_add = findViewById(R.id.button_add);
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Bimp.tempSelectVideo.clear();
        if (null != progressDialog && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        videoList = null;
        databaseAdapter = null;
        executorService = null;
        /*if (null != mTask && !mTask.isCancelled()){
            mTask.cancel(true);
        }*/
    }


    public void initData(){
        executorService = Executors.newFixedThreadPool(20);
        databaseAdapter = new DatabaseAdapter(VideoAlbum.this);

        videoList = AlbumHelper.getSystemVideoList(VideoAlbum.this);
    }


    private void initView() {
        TextView tvTitle = (TextView) findViewById(R.id.title);
        tvTitle.setText(R.string.select_video);

        gridView = (GridView) findViewById(R.id.album_GridView);
        gridVideoAdapter = new VideoAlbumGridViewAdapter(this, videoList);

        if(videoList.size()==1){
            file_count.setText(videoList.size()+" Video");
        }else {
            file_count.setText(videoList.size()+" Videos");
        }
        gridVideoAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if(videoList.size()==1){
                    file_count.setText(videoList.size()+" Video");
                }else {
                    file_count.setText(videoList.size()+" Videos");
                }
            }
        });
        gridView.setAdapter(gridVideoAdapter);
    }

    int grid_count=3;
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reverse:

                Collections.reverse(videoList);
                gridVideoAdapter = new VideoAlbumGridViewAdapter(VideoAlbum.this, videoList);
                gridView.setAdapter(gridVideoAdapter);
                break;
            case R.id.settings:
                startActivity(new Intent(VideoAlbum.this, AdvancedSetup.class));
                break;
            case R.id.grid:
                if(grid_count==4) {
                    gridView.setNumColumns(3);
                    grid_count--;
                }else if(grid_count==3) {
                    gridView.setNumColumns(2);
                    grid_count--;
                }else if(grid_count==2) {
                    gridView.setNumColumns(4);
                    grid_count=4;
                }

                break;
            case R.id.button_back:
              //  startActivity(new Intent(VideoAlbum.this, MainActivity.class));
                finish();
                break;
            case R.id.checkbox_select_all:

                gridVideoAdapter.selectAll(((CheckBox) view).isChecked());
                showDec();
                break;
            case R.id.button_add:
                if (Bimp.tempSelectVideo.size() == 0) {
                    Notifi.message(this,getString(R.string.choose_at_least_one_video),true);
                  //  Toast.makeText(this, getString(R.string.choose_at_least_one_video), Toast.LENGTH_SHORT).show();
                    break;
                }
                if (mTask != null) {
                    mTask.cancel(true);
                }
                mTask = new EncryptionTask(Bimp.tempSelectVideo);
                mTask.execute();
                break;
            default:
                break;
        }

    }
    public static void showDec(){
        if (Bimp.tempSelectVideo.size() == 0) {

            button_add.setVisibility(View.GONE);
        }else {
            button_add.setVisibility(View.VISIBLE);
        }

    }
    public void onBackPressed() {
        super.onBackPressed();
       // startActivity(new Intent(VideoAlbum.this, MainActivity.class));
        finish();
    }
    public ProgressDialog progressDialog;

    public class EncryptionTask extends AsyncTask<Void, Void, Boolean> {
        private ArrayList<VideoItem> mVideoArrayList;
        int startSize;

        public EncryptionTask(ArrayList<VideoItem> videoArrayList) {
            mVideoArrayList = videoArrayList;
            progressDialog = new ProgressDialog(VideoAlbum.this);
            progressDialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startSize = databaseAdapter.getPhoto().size();
            progressDialog.setMessage(getString(R.string.encrypting));
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean result = encryptVideoList(mVideoArrayList);
            int totalTime = 0;
            while (result && databaseAdapter.getPhoto().size() != (startSize + mVideoArrayList.size()) && totalTime < mVideoArrayList.size()) {
                try {
                    Thread.sleep(1000);
                    totalTime += 2;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            gridVideoAdapter.refreshDataAfterEncrypt();
            gridVideoAdapter = new VideoAlbumGridViewAdapter(VideoAlbum.this, videoList);
            gridView.setAdapter(gridVideoAdapter);
            String showMessage = result ? getString(R.string.encrypt_success) : getString(R.string.partial_video_encryption_failed);
            Notifi.message(VideoAlbum.this,showMessage,result);
            showDec();
            CheckBox checkbox_select_all=findViewById(R.id.checkbox_select_all);
            checkbox_select_all.setChecked(false);
          //  Toast.makeText(VideoAlbum.this, showMessage, Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

    }


    boolean result = true;

    public boolean encryptVideoList(ArrayList<VideoItem> arrayList) {
        ArrayList<Future<Boolean>> futures = new ArrayList<>();
        futures.clear();

        for (final VideoItem item : arrayList) {
            final String videoPath = item.getPath();
            final String privVideoPath = videoPath.replaceFirst("/storage/emulated/0", "/data/data/" + getPackageName() + "/files/storage/emulated/0");
            Future<Boolean> future = executorService.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() {
                    boolean b = XorEncryptionUtil.encrypt(videoPath, privVideoPath);
                    if (b) {
                        deleteVideo(item, privVideoPath, getContentResolver());
                    }else {

                        XorEncryptionUtil.encrypt(videoPath, null);
                        boolean x = XorEncryptionUtil.encrypt(videoPath, privVideoPath);
                        if (x) {
                            deleteVideo(item, privVideoPath, getContentResolver());
                        }else {

                            XorEncryptionUtil.encrypt(videoPath, null);
                        }
                        return x;
                    }
                    return b;
                }
            });
            futures.add(future);
        }

        for (Future<Boolean> future : futures) {
            try {
                result &= future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }




        return result;
    }


    public static void deleteVideo(VideoItem item, String privVideoPath, ContentResolver contentResolver) {

        new File(item.getPath()).delete();

        contentResolver.delete(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "_id=?", new String[]{item.getId()});


        ContentValues contentValues = new ContentValues();
        contentValues.put(PsDatabaseHelper.VideoClumns._ID, Integer.valueOf(item.getId()));
        contentValues.put(PsDatabaseHelper.VideoClumns.DATA, privVideoPath);
        contentValues.put(PsDatabaseHelper.VideoClumns.DISPLAY_NAME, item.getDisplayName());
        contentValues.put(PsDatabaseHelper.VideoClumns.SIZE, Integer.valueOf(item.getSize()));
        contentValues.put(PsDatabaseHelper.VideoClumns.MIME_TYPE, item.getMimeType());
        contentValues.put(PsDatabaseHelper.VideoClumns.DATE_ADDED, Long.valueOf(item.getDateAdded()));
        contentValues.put(PsDatabaseHelper.VideoClumns.TITLE, item.getDateAdded());
        contentValues.put(PsDatabaseHelper.VideoClumns.ALBUM, item.getAlbum());
        contentValues.put(PsDatabaseHelper.VideoClumns.BUCKET_ID, item.getBucketId());
        contentValues.put(PsDatabaseHelper.VideoClumns.BUCKET_DISPLAY_NAME, item.getBucketDisplayName());
        try {
            contentValues.put(PsDatabaseHelper.VideoClumns.WIDTH, Integer.valueOf(item.getWidth()));
            contentValues.put(PsDatabaseHelper.VideoClumns.HEIGHT, Integer.valueOf(item.getHeight()));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        databaseAdapter.insertVideo(contentValues);
    }


}
