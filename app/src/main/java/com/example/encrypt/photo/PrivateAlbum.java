package com.example.encrypt.photo;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.TextView;

import com.example.encrypt.R;
import com.example.encrypt.activity.AdvancedSetup;
import com.example.encrypt.activity.BaseActivity;
import com.example.encrypt.activity.BseApplication;
import com.example.encrypt.database.DatabaseAdapter;
import com.example.encrypt.util.Notifi;
import com.example.encrypt.util.XorEncryptionUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by dongrp on 2017/7/13.
 * 所有私密图片 相册集
 */

public class PrivateAlbum extends BaseActivity implements View.OnClickListener {
    private GridView gridView;
    public static ArrayList<ImageItem> dateList;
    private PrivateAlbumGridViewAdapter privateAlbumGridViewAdapter;
    private static ExecutorService executorService; //线程池
    //    private int mFirstVisibleItem, mVisibleItemCount, mTotalItemCount;
    private static DatabaseAdapter databaseAdapter;
    private ProgressDialog progressDialog;
    //    private VisibleImageDecryptionTask visibleImageDecryptionTask;
    private static TextView tvNoPicture;
    TextView file_count;
    static Button button_min;
    public static Context c;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_private_album);
        addAppActivity(PrivateAlbum.this);
        c=PrivateAlbum.this;
        button_min=findViewById(R.id.button_min);
         //data初始化
        file_count=findViewById(R.id.file_count);
        executorService = Executors.newCachedThreadPool();//创建一个缓存线程池
        databaseAdapter = new DatabaseAdapter(PrivateAlbum.this);//数据库操作工具类
        //为实现onResume后还能记住选中的照片，所以必须在onResume之前初始化数据，这样Bimp.tempSelectBitmap 和 dateList操作的就是同一批数据
        dateList = databaseAdapter.getPhoto();

    }

    @Override
    protected void onResume() {
        super.onResume();
        decryptAndEncryptPhotosTemporary();//原路径解密全部图片
        //view及adapter初始化
        tvNoPicture = (TextView) findViewById(R.id.tv_no_picture);
        gridView = (GridView) findViewById(R.id.album_GridView);//组件
        privateAlbumGridViewAdapter = new PrivateAlbumGridViewAdapter(PrivateAlbum.this, dateList);
        if(dateList.size()==1){
      file_count.setText(dateList.size()+" Image");
        }else {
            file_count.setText(dateList.size()+" Images");
        }
        privateAlbumGridViewAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if(dateList.size()==1){
                    file_count.setText(dateList.size()+" Image");
                }else {
                    file_count.setText(dateList.size()+" Images");
                }
            }
        });
         gridView.setAdapter(privateAlbumGridViewAdapter);//绑定适配器
    }

    @Override
    protected void onPause() {
        super.onPause();
        //只要不是去Gallery界面产生的onPause,一律执行加密
        if (!BseApplication.sp.getBoolean("privAlbumToGallery", false)) {
            encryptPhotosTemporary();//退出时，再将图片全部原路径加密起来
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Bimp.tempSelectBitmap.clear();
        //将静态变量置空，防止内存泄露发生
        dateList = null;
        databaseAdapter = null;
        executorService = null;
        tvNoPicture = null;
    }

    public static void showNoPictureTip() {
        tvNoPicture.setVisibility(View.VISIBLE);
    }

    static boolean result2 = true;

    public static boolean decryptAndEncryptPhotosTemporary() {
        if (BseApplication.sp.getBoolean("photo_encrypt", true)) {//判断是否是加密状态，是，就执行解密
            for (ImageItem item : dateList) {
                final String privImagePath = item.getImagePath();
                //String fileName = privImagePath.substring(privImagePath.lastIndexOf("/") + 1);
                //final String imagePath = "/data/data/" + getPackageName() + "/files/" + fileName;
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        boolean b = XorEncryptionUtil.encrypt(privImagePath, null);
                        if (!b) {//加密失败，再进行一次异或解密（相当于事务回退）
                            XorEncryptionUtil.encrypt(privImagePath, null);
                            result2 = b;
                        }
                    }
                });
            }
            BseApplication.editor.putBoolean("photo_encrypt", false).commit();
        }
        return result2;
    }


    /**
     * 批量图片原地加密
     */
    static boolean result3 = true;

    public static boolean encryptPhotosTemporary() {
        if (!BseApplication.sp.getBoolean("photo_encrypt", false)) {//判断是否是解密状态，是，就执行加密
            for (ImageItem item : dateList) {
                final String privImagePath = item.getImagePath();
                //String fileName = privImagePath.substring(privImagePath.lastIndexOf("/") + 1);
                //final String imagePath = "/data/data/" + getPackageName() + "/files/" + fileName;
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        boolean b = XorEncryptionUtil.encrypt(privImagePath, null);
                        if (!b) {//解密失败，再进行一次异或解密（相当于事务回退）
                            XorEncryptionUtil.encrypt(privImagePath, null);
                            result3 = b;
                        }
                    }
                });
            }
            BseApplication.editor.putBoolean("photo_encrypt", true).commit();
        }
        return result3;
    }

    int grid_count=3;
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.reverse:

                Collections.reverse(dateList);
                privateAlbumGridViewAdapter = new PrivateAlbumGridViewAdapter(PrivateAlbum.this, dateList);
                gridView.setAdapter(privateAlbumGridViewAdapter);
                break;
            case R.id.settings:
                 startActivity(new Intent(PrivateAlbum.this, AdvancedSetup.class));
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
                privateAlbumGridViewAdapter.selectAll(((CheckBox) view).isChecked());

                showDec();
                 break;
            case R.id.button_min:
                if (Bimp.tempSelectBitmap.size() == 0) {
                    Notifi.message(this,getString(R.string.choose_at_least_one_picture),true);
                //    Toast.makeText(this, ), Toast.LENGTH_SHORT).show();
                    break;
                }
                DecryptionTask decryptionTask = new DecryptionTask(Bimp.tempSelectBitmap);
                decryptionTask.execute();
                break;
            default:
                break;

        }
    }

    public static void showDec(){
         if (Bimp.tempSelectBitmap.size() == 0) {

            button_min.setVisibility(View.GONE);
        }else {
            button_min.setVisibility(View.VISIBLE);
        }

    }
    public void addimage(View view) {
        startActivity(new Intent(PrivateAlbum.this, Folders.class));
        finish();
    }

    /**
     * 批量解密异步任务
     */
    public class DecryptionTask extends AsyncTask<Void, Void, Boolean> {
        private ArrayList<ImageItem> listPrivFliePath;
        int startSize;

        public DecryptionTask(ArrayList<ImageItem> listPrivFliePath) {
            this.listPrivFliePath = listPrivFliePath;
            progressDialog = new ProgressDialog(PrivateAlbum.this);
            progressDialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startSize = getApplicationContext().getContentResolver()
                    .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null).getCount();
            progressDialog.setMessage(getString(R.string.decrypting));
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean result = decryptFileList(listPrivFliePath); //解密文件集合
            int totalTime = 0;
            while (result && getApplicationContext().getContentResolver().
                    query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null).getCount()
                    != (startSize + listPrivFliePath.size()) && totalTime < listPrivFliePath.size() / 2) {
                try {
                    Thread.sleep(1000);
                    totalTime += 2;
                    //Log.d("DecryptionTask", "totalTime:" + totalTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            privateAlbumGridViewAdapter.refreshDataAfterDecrypt();
            //loadFirstScreenImage();
            String showMessage = result ? getString(R.string.decrypt_success) : getString(R.string.partial_picture_decryption_failed);
            Notifi.message(PrivateAlbum.this,showMessage,result);
            //  Toast.makeText(PrivateAlbum.this, showMessage, Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }


    boolean result = true;

    /**
     * 解密文件集合
     */
    public boolean decryptFileList(final ArrayList<ImageItem> arrayList) {
        for (final ImageItem item : arrayList) {
            final String privImagePath = item.getImagePath(); //这个私密文件的绝对路径
            //解密后：文件原来的路径
            final String imagePath = privImagePath.replaceFirst("/data/data/" + getPackageName() + "/files/storage/emulated/0", "/storage/emulated/0");
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    //boolean b = AESEncryptionUtil.decryptFile(privImagePath, imagePath);
                    //boolean b = XorEncryptionUtil.encrypt(privImagePath, imagePath);
                    //图片已经处于解密状态，copy会原路径即可
                    boolean b = XorEncryptionUtil.copyFile(privImagePath, imagePath);
                    if (b) {//解密成功，删除私密文件
                        delete(item, imagePath, getContentResolver());
                    } else {//解密失败，设置结果为false
                        //解密失败：再进行一次异或解密（相当于事务回退）
                        XorEncryptionUtil.copyFile(privImagePath, null);
                        result = b;
                    }
                }
            });
        }
        return result;
    }


    /**
     * 密文件删除、私密数据库记录删除、还原文件条目到系统数据库
     */
    public static void delete(ImageItem item, String imagePath, ContentResolver contentResolver) {
        //删除密文件
        File file = new File(item.getImagePath());
        file.delete();
        //删除私密数据库中该条文件记录
        databaseAdapter.deletePhoto(item.getImageId());
        //还原文件条目到系统数据库中
        Uri baseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media._ID, item.getImageId());
        contentValues.put(MediaStore.Images.Media.DATA, imagePath);
        contentValues.put(MediaStore.Images.Media.SIZE, item.getSize());
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, item.getDisplayName());
        contentValues.put(MediaStore.Images.Media.TITLE, item.getTitle());
        contentValues.put(MediaStore.Images.Media.DATE_ADDED, item.getDateAdded());
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, item.getMimeType());
        contentValues.put(MediaStore.Images.Media.BUCKET_ID, item.getBucketId());
        contentValues.put(MediaStore.Images.Media.BUCKET_DISPLAY_NAME, item.getBucket_display_name());
        contentValues.put(MediaStore.Images.Media.WIDTH, item.getWidth());
        contentValues.put(MediaStore.Images.Media.HEIGHT, item.getHeight());
        contentResolver.insert(baseUri, contentValues);
    }


}
