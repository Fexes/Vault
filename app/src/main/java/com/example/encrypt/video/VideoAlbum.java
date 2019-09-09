package com.example.encrypt.video;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.encrypt.R;
import com.example.encrypt.activity.AdvancedSetup;
import com.example.encrypt.activity.BaseActivity;
import com.example.encrypt.database.DatabaseAdapter;
import com.example.encrypt.database.PsDatabaseHelper;
import com.example.encrypt.photo.AlbumHelper;
import com.example.encrypt.photo.Bimp;
import com.example.encrypt.util.Notifi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@RequiresApi(api = Build.VERSION_CODES.N)
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


    public static void deleteVideo(VideoItem item, String privVideoPath, ContentResolver contentResolver) {


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

    int grid_count = 3;

    private void initView() {
        TextView tvTitle = findViewById(R.id.title);
        tvTitle.setText(R.string.select_video);

        gridView = findViewById(R.id.album_GridView);
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

    public static void showDec() {
        if (Bimp.tempSelectVideo.size() == 0) {

            button_add.setVisibility(View.GONE);
        } else {
            button_add.setVisibility(View.VISIBLE);
        }

    }

    public void onBackPressed() {
        super.onBackPressed();
        // startActivity(new Intent(VideoAlbum.this, MainActivity.class));
        finish();
    }

    public ProgressDialog progressDialog;

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

    boolean result = true;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean encryptVideoList(final ArrayList<VideoItem> arrayList) {

        int current = 0;
        for (final VideoItem item : arrayList) {
            final String videoPath = item.getPath();
            final String privVideoPath = videoPath.replaceFirst("/storage/emulated/0", "/data/data/" + getPackageName() + "/files/storage/emulated/0");
            boolean b = moveFile(getApplicationContext(), videoPath, privVideoPath, arrayList.size(), current);
            if (b) {
                deleteVideo(item, privVideoPath, getContentResolver());
                current++;
            } else {

                if (moveFile(getApplicationContext(), videoPath, privVideoPath, arrayList.size(), current)) {
                    deleteVideo(item, privVideoPath, getContentResolver());
                    current++;
                }
            }
            result = b;

        }

        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean moveFile(Context context, String sourcepath, String targetpath, int TotalFiles, int current) {
        current++;
        //   NotificationUtil notificationUtil = new NotificationUtil(context, "Files : " + current + " / " + TotalFiles, TotalFiles, current);
        File sourceLocation = new File(sourcepath);
        File targetLocation = new File(targetpath);

        try {
            if (!targetLocation.getParentFile().exists()) {
                targetLocation.getParentFile().mkdirs();
            }
            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);
            long expectedBytes = sourceLocation.length();
            long totalBytesCopied = 0;
            byte[] buf = new byte[1024];
            int len;
            int progress;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
                totalBytesCopied += len;
                progress = (int) Math.round(((double) totalBytesCopied / (double) expectedBytes) * 100);

                //         progressDialog.setMessage("Files :" + TotalFiles + " / " + current + "\n" + "Progress :" + progress + " % ");

                // Log.d("progress :",   progress+"");
            }
            progressDialog.setProgress(current);
            progressDialog.setMax(TotalFiles);
            //notificationUtil.updateNotification("Files : " + current + " / " + TotalFiles, TotalFiles, current);

            // cdd.update("Files : "+current+" / "+TotalFiles);
            //  cdd.showProgress(getContext(), "Files : "+current+" / "+TotalFiles, false);


            if (current == TotalFiles) {
                //notificationUtil.cancel();
            }
            in.close();
            out.close();
            sourceLocation.delete();

            return true;
        } catch (Exception e) {
            Log.d("Error :", e.toString());
            return false;
        }
    }

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
            progressDialog.setTitle(getString(R.string.encrypting));

            progressDialog.setIcon(R.mipmap.ic_launcher);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
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
            Notifi.message(VideoAlbum.this, showMessage, result);
            showDec();
            CheckBox checkbox_select_all = findViewById(R.id.checkbox_select_all);
            checkbox_select_all.setChecked(false);
            //  Toast.makeText(VideoAlbum.this, showMessage, Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();

        }

    }

}
