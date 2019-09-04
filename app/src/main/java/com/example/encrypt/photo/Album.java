package com.example.encrypt.photo;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.example.encrypt.util.Notifi;
import com.example.encrypt.util.XorEncryptionUtil;

import java.io.File;
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

    /**
     * view初始化
     */
    private void init() {
        //创建gridView并绑定适配器
        gridView = (GridView) findViewById(R.id.album_GridView);
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
/*        if (null != mTask && !mTask.isCancelled()){  注释掉此段，不取消mTask,让加解密在后台继续执行完成
            mTask.cancel(true);
        }*/
    }

     int grid_count=3;
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
           //     ((CheckBox) view).setText(((CheckBox) view).isChecked() ? getString(R.string.deselect_all) : getString(R.string.select_all));
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
    public static void showDec(){

        if (Bimp.tempSelectBitmap.size() > 0) {
            button_add.setVisibility(View.VISIBLE);
        }else {
            button_add.setVisibility(View.GONE);
        }

    }
    /**
     * 批量加密异步任务
     */
    public ProgressDialog progressDialog;

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
            progressDialog.setMessage(getString(R.string.encrypting));
            progressDialog.show();
        }

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
            CheckBox checkbox_select_all=findViewById(R.id.checkbox_select_all);
            checkbox_select_all.setChecked(false);
            showDec();
            Notifi.message(Album.this,showMessage,result);
            progressDialog.dismiss();
        }

    }


    boolean result = true;

    public boolean encryptFileList(ArrayList<ImageItem> arrayList) {
//        long l2 = System.currentTimeMillis();
        for (final ImageItem item : arrayList) {
            final String imagePath = item.getImagePath();
            final String privImagePath = imagePath.replaceFirst("/storage/emulated/0", "/data/data/" + getPackageName() + "/files/storage/emulated/0");
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                     boolean b = XorEncryptionUtil.encrypt(imagePath, privImagePath);
                    if (b) {
                        delete(item, privImagePath, getContentResolver());
                    } else {

                        XorEncryptionUtil.encrypt(imagePath, null);
                        boolean x = XorEncryptionUtil.encrypt(imagePath, privImagePath);
                        if (x) {
                            delete(item, privImagePath, getContentResolver());
                        } else {
                            XorEncryptionUtil.encrypt(imagePath, null);
                            result = x;
                        }
                    }
                }
            });
        }
        return result;
    }


    public static void delete(ImageItem item, String privImagePath, ContentResolver contentResolver) {
         File file = new File(item.getImagePath());
        file.delete();
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


}
