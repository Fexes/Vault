package com.example.encrypt.album;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.example.encrypt.photo.Bimp;
import com.example.encrypt.photo.ImageItem;
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



public class Album extends BaseActivity implements OnClickListener {
    private GridView gridView;
    private AlbumGridViewAdapter gridImageAdapter;
    TextView file_count;
    public static ArrayList<ImageItem> dataList = new ArrayList<ImageItem>();
    private EncryptionTask mTask = null;
    public static ExecutorService executorService;
    private static DatabaseAdapter databaseAdapter;

    public static Button button_add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        addAppActivity(Album.this);
        Bimp.tempSelectBitmap.clear();
        file_count=findViewById(R.id.file_count);
        button_add=findViewById(R.id.button_add);
        executorService = Executors.newFixedThreadPool(20);
        databaseAdapter = new DatabaseAdapter(Album.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }


    public static void delete(ImageItem item, String privImagePath, ContentResolver contentResolver) {

        Uri baseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        contentResolver.delete(baseUri, "_id=?", new String[]{item.getImageId()});

        ContentValues contentValues = new ContentValues();
        contentValues.put(PsDatabaseHelper.FilesClumns._ID, Integer.valueOf(item.getImageId()));
        contentValues.put(PsDatabaseHelper.FilesClumns._DATA, privImagePath);
        contentValues.put(PsDatabaseHelper.FilesClumns._SOURCE_DATA, item.getImagePath());
        contentValues.put(PsDatabaseHelper.FilesClumns._SIZE, Integer.valueOf(item.getSize()));
        contentValues.put(PsDatabaseHelper.FilesClumns._DISPLAY_NAME, item.getDisplayName());
        contentValues.put(PsDatabaseHelper.FilesClumns.TITLE, item.getTitle());
        contentValues.put(PsDatabaseHelper.FilesClumns.DATE_ADDED, Long.valueOf(item.getDateAdded()));
        contentValues.put(PsDatabaseHelper.FilesClumns.MIME_TYPE, item.getMimeType());
        contentValues.put(PsDatabaseHelper.FilesClumns.BUCKET_ID, item.getBucketId());
        contentValues.put(PsDatabaseHelper.FilesClumns.BUCKET_DISPLAY_NAME, item.getBucket_display_name());
        try {
            contentValues.put(PsDatabaseHelper.FilesClumns.WIDTH, Integer.valueOf(item.getWidth()));
            contentValues.put(PsDatabaseHelper.FilesClumns.HEIGHT, Integer.valueOf(item.getHeight()));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        databaseAdapter.insertPhoto(contentValues);
    }

    private void init() {

        gridView = findViewById(R.id.album_GridView);
        if(dataList.size()==1){
            file_count.setText(dataList.size()+" Image");
        }else {
            file_count.setText(dataList.size()+" Images");
        }
        gridImageAdapter = new AlbumGridViewAdapter(this, dataList);
        gridImageAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if(dataList.size()==1){
                    file_count.setText(dataList.size()+" Image");
                }else {
                    file_count.setText(dataList.size()+" Images");
                }
            }
        });
        gridView.setAdapter(gridImageAdapter);
    }

    int grid_count = 3;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Bimp.tempSelectBitmap.clear();// Bimp.tempSelectBitmap
        if (null != progressDialog && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        dataList = null;
        databaseAdapter = null;
        executorService = null;

    }

    public static void showDec() {

        if (Bimp.tempSelectBitmap.size() > 0) {
            button_add.setVisibility(View.VISIBLE);
        } else {
            button_add.setVisibility(View.GONE);
        }

    }

    public ProgressDialog progressDialog;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reverse:

                Collections.reverse(dataList);
                gridImageAdapter = new AlbumGridViewAdapter(Album.this, dataList);
                gridView.setAdapter(gridImageAdapter);
                break;
            case R.id.settings:
                startActivity(new Intent(Album.this, AdvancedSetup.class));
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
                finish();
                break;
            case R.id.checkbox_select_all:
                gridImageAdapter.selectAll(((CheckBox) view).isChecked());
                showDec();
                break;
            case R.id.button_add:
                if (Bimp.tempSelectBitmap.size() == 0) {
                    Notifi.message(this,getString(R.string.choose_at_least_one_picture),true);
                //    Toast.makeText(this, getString(R.string.choose_at_least_one_picture), Toast.LENGTH_SHORT).show();
                    break;
                }
                if (mTask != null) {
                    mTask.cancel(true);
                }
                mTask = new EncryptionTask(Bimp.tempSelectBitmap);
                mTask.execute();
                break;
            default:
                break;
        }

    }


    boolean result = true;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean encryptFileList(final ArrayList<ImageItem> arrayList) {
//        long l2 = System.currentTimeMillis();
        int current = 0;
        for (final ImageItem item : arrayList) {
            final String imagePath = item.getImagePath();
            final String privImagePath = imagePath.replaceFirst("/storage/emulated/0", "/data/data/" + getPackageName() + "/files/storage/emulated/0");
            boolean b = moveFile(getApplicationContext(), imagePath, privImagePath, arrayList.size(), current);
            if (b) {
                delete(item, privImagePath, getContentResolver());
                current++;
            } else {

                if (moveFile(getApplicationContext(), imagePath, privImagePath, arrayList.size(), current)) {
                    delete(item, privImagePath, getContentResolver());
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
        //  NotificationUtil notificationUtil = new NotificationUtil(context, "Files : " + current + " / " + TotalFiles, TotalFiles, current);
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

                //        progressDialog.setMessage("Files :" + TotalFiles + " / " + current + "\n" + "Progress :" + progress + " % ");

                // Log.d("progress :",   progress+"");
            }
            progressDialog.setProgress(current);
            progressDialog.setMax(TotalFiles);
            //notificationUtil.updateNotification("Files : " + current + " / " + TotalFiles, TotalFiles, current);

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
        private ArrayList<ImageItem> mImageArrayList;
        int startSize;

        public EncryptionTask(ArrayList<ImageItem> imageArrayList) {
            this.mImageArrayList = imageArrayList;
            progressDialog = new ProgressDialog(Album.this);
            progressDialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startSize = databaseAdapter.getPhoto().size();
            progressDialog.setIcon(R.mipmap.ic_launcher);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setTitle(getString(R.string.encrypting));
            progressDialog.show();
            // DialogUtil cdd;

        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Boolean doInBackground(Void... params) {
            boolean result = encryptFileList(mImageArrayList); //加密文件集合
            int totalTime = 0;
            while (result && databaseAdapter.getPhoto().size() != (startSize + mImageArrayList.size()) && totalTime < mImageArrayList.size()) {
                try {
                    Thread.sleep(1000);
                    totalTime += 2;
                    //Log.d("EncryptionTask", "totalTime:" + totalTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            gridImageAdapter.refreshDataAfterEncrypt();

            String showMessage = result ? getString(R.string.encrypt_success) : getString(R.string.partial_picture_encryption_failed);
            //  Toast.makeText(Album.this, showMessage, Toast.LENGTH_SHORT).show();
            CheckBox checkbox_select_all = findViewById(R.id.checkbox_select_all);
            checkbox_select_all.setChecked(false);
            showDec();
            Notifi.message(Album.this, showMessage, result);
            progressDialog.dismiss();

        }

    }


}
