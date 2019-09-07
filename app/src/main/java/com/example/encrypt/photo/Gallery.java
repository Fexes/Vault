package com.example.encrypt.photo;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import com.example.encrypt.R;
import com.example.encrypt.activity.BaseActivity;
import com.example.encrypt.activity.BseApplication;
import com.example.encrypt.util.Notifi;
import com.example.encrypt.util.NotificationUtil;
import com.example.encrypt.vault.PrivatePhotoFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


public class Gallery extends BaseActivity implements OnClickListener, OnPageChangeListener {
    private Intent intent;
    public static boolean isFromPrivateAlbum;
    private int location;//当前的位置
    private static Button buttonAdd, buttonMin;
    private ProgressDialog progressDialog;
    private GalleryViewPagerAdapter adapter;
    private ViewPagerFixed pager;
    static FrameLayout frameLayout;
    private SystemKeyEventReceiver receiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();// 隐藏掉ActionBar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_gallery);
        addAppActivity(Gallery.this);
        initData();
        frameLayout=findViewById(R.id.backcolor);
        frameLayout.setBackgroundColor(Color.BLACK);
    }

    protected void onResume() {
        super.onResume();
        //恢复privAlbumToGallery为false状态
        if (isFromPrivateAlbum) {
            BseApplication.editor.putBoolean("privAlbumToGallery", false).commit();

        }
        initViewAndCtrl(); //初始化view 和 ctrl
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        buttonAdd = null;
        buttonMin = null;
    }

    /**
     * 初始化数据
     */
    public void initData() {
        intent = getIntent();
        isFromPrivateAlbum = intent.getBooleanExtra("isFromPrivateAlbum", false);
        receiver = new SystemKeyEventReceiver();
        registerReceiver(receiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }

    /**
     * 初始化view和适配器ctrl
     */
    private void initViewAndCtrl() {
        //加密、解密按钮
        progressDialog = new ProgressDialog(Gallery.this);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonMin = findViewById(R.id.buttonMin);
        buttonAdd.setOnClickListener(this);
        buttonMin.setOnClickListener(this);
        if (isFromPrivateAlbum) {//私密相册
            buttonAdd.setVisibility(View.GONE);
            buttonMin.setVisibility(View.VISIBLE);
        } else {
            buttonAdd.setVisibility(View.VISIBLE);
            buttonMin.setVisibility(View.GONE);
        }

        //ViewPagerFixed
        pager = findViewById(R.id.gallery01);
        pager.setOnPageChangeListener(this);
        //GalleryViewPagerAdapter
        adapter = new GalleryViewPagerAdapter(Gallery.this);
        pager.setAdapter(adapter);

        int id = intent.getIntExtra("position", 0);
        pager.setCurrentItem(id);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean encryptSinglePhoto() {
        ImageItem item = Album.dataList.get(location);
        String imagePath = item.getImagePath();
        String privImagePath = imagePath.replaceFirst("/storage/emulated/0", "/data/data/" + getPackageName() + "/files/storage/emulated/0");
        //boolean b = AESEncryptionUtil.encryptFile(imagePath, privImagePath);
        boolean b = moveFile(getApplicationContext(), imagePath, privImagePath, 1, 1);
        if (b) {
            Bimp.tempSelectBitmap.remove(Album.dataList.get(location));
            Album.dataList.remove(location);
            Album.delete(item, privImagePath, getContentResolver());
            return true;
        } else {

            return false;
        }
    }


     @Override
    public void onPageSelected(int arg0) {
        location = arg0;
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonAdd://加密
                new SingleEncryptionOrDecryptionTask().execute();
                break;
            case R.id.buttonMin://解密
                new SingleEncryptionOrDecryptionTask().execute();
                break;
            default:
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean decryptSinglePhoto() {
        ImageItem item = PrivatePhotoFragment.dateList.get(location);
        String privImagePath = item.getImagePath();
        String imagePath = privImagePath.replaceFirst("/data/data/" + getPackageName() + "/files/storage/emulated/0", "/storage/emulated/0");

        boolean b = moveFile(getApplicationContext(), privImagePath, imagePath, 1, 1);
        if (b) {
            Bimp.tempSelectBitmap.remove(PrivatePhotoFragment.dateList.get(location));
            PrivatePhotoFragment.dateList.remove(location);
            PrivatePhotoFragment.delete(item, imagePath, getContentResolver());
            return true;
        } else {
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean moveFile(Context context, String sourcepath, String targetpath, int TotalFiles, int current) {
        current++;
        NotificationUtil notificationUtil = new NotificationUtil(context, "Files : " + current + " / " + TotalFiles, TotalFiles, current);
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

                progressDialog.setMessage("Files :" + TotalFiles + " / " + current + "\n" + "Progress :" + progress + " % ");

                // Log.d("progress :",   progress+"");
            }
            progressDialog.setProgress(current);
            progressDialog.setMax(TotalFiles);
            notificationUtil.updateNotification("Files : " + current + " / " + TotalFiles, TotalFiles, current);

            // cdd.update("Files : "+current+" / "+TotalFiles);
            //  cdd.showProgress(getContext(), "Files : "+current+" / "+TotalFiles, false);


            if (current == TotalFiles) {
                notificationUtil.cancel();
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

    private class SystemKeyEventReceiver extends BroadcastReceiver {
        private final String SYSTEM_DIALOG_REASON_KEY = "reason";
        private final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        private final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason == null) {
                    return;
                }

                if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {

                }

                if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {

                }
            }
        }
    }


    static boolean isHide = false;

    public static void switchButtonVisibility() {
        int color = Color.TRANSPARENT;
        Drawable background = frameLayout.getBackground();
        if (background instanceof ColorDrawable)
            color = ((ColorDrawable) background).getColor();
        if(color==Color.BLACK) {
            frameLayout.setBackgroundColor(Color.WHITE);
        }else {
            frameLayout.setBackgroundColor(Color.BLACK);
        }

        if (!isHide && !isFromPrivateAlbum) {
            ObjectAnimator.ofFloat(buttonAdd, "translationY", 0, 300).setDuration(200).start();
            isHide = true;
        } else if (isHide && !isFromPrivateAlbum) {
            ObjectAnimator.ofFloat(buttonAdd, "translationY", 300, 0).setDuration(200).start();
            isHide = false;
        } else if (!isHide && isFromPrivateAlbum) {
            ObjectAnimator.ofFloat(buttonMin, "translationY", 0, 300).setDuration(200).start();
            isHide = true;
        } else if (isHide && isFromPrivateAlbum) {
            ObjectAnimator.ofFloat(buttonMin, "translationY", 300, 0).setDuration(200).start();
            isHide = false;
        }
    }

    public class SingleEncryptionOrDecryptionTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setCancelable(false);
            progressDialog.setIcon(R.mipmap.ic_launcher);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setTitle(isFromPrivateAlbum ? getString(R.string.decrypting) : getString(R.string.encrypting));
            progressDialog.show();
            // DialogUtil cdd;

        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Boolean doInBackground(Void... params) {
            boolean result = false;
            if (isFromPrivateAlbum) {//从私密相册来，肯定是要解密了
                result = decryptSinglePhoto();//解密单张图片
            } else {//从正常相册来，肯定是要加密了
                result = encryptSinglePhoto();//加密单张图片
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (adapter.getCount() == 0) {
                finish();
            } else {
                adapter.notifyDataSetChanged();


            }
            String showMessage = result ? getString(R.string.success) : getString(R.string.fail);
            Notifi.message(Gallery.this, showMessage, result);

            //  Toast.makeText(Gallery.this, showMessage, Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

    }

}
