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
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.encrypt.R;
import com.example.encrypt.util.Notifi;
import com.example.encrypt.vault.PrivatePhotoFragment;
import com.example.encrypt.activity.BaseActivity;
import com.example.encrypt.activity.BseApplication;
import com.example.encrypt.util.XorEncryptionUtil;


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
            PrivatePhotoFragment.decryptAndEncryptPhotosTemporary();
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
        buttonAdd = (Button) findViewById(R.id.buttonAdd);
        buttonMin = (Button) findViewById(R.id.buttonMin);
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
        pager = (ViewPagerFixed) findViewById(R.id.gallery01);
        pager.setOnPageChangeListener(this);
        //GalleryViewPagerAdapter
        adapter = new GalleryViewPagerAdapter(Gallery.this);
        pager.setAdapter(adapter);

        int id = intent.getIntExtra("position", 0);
        pager.setCurrentItem(id);
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
                    PrivatePhotoFragment.encryptPhotosTemporary();
                }

                if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                    PrivatePhotoFragment.encryptPhotosTemporary();
                }
            }
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

    //点击监听
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


    public class SingleEncryptionOrDecryptionTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setCancelable(false);
            progressDialog.setMessage(isFromPrivateAlbum ? getString(R.string.decrypting) : getString(R.string.encrypting));
            progressDialog.show();
        }

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
            Notifi.message(Gallery.this,showMessage,result);

            //  Toast.makeText(Gallery.this, showMessage, Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

    }

    public boolean encryptSinglePhoto() {
        ImageItem item = Album.dataList.get(location);
        String imagePath = item.getImagePath();
        String privImagePath = imagePath.replaceFirst("/storage/emulated/0", "/data/data/" + getPackageName() + "/files/storage/emulated/0");
        //boolean b = AESEncryptionUtil.encryptFile(imagePath, privImagePath);
        boolean b = XorEncryptionUtil.encrypt(imagePath, privImagePath);
        if (b) {//成功
            if (Bimp.tempSelectBitmap.contains(Album.dataList.get(location))) {
                Bimp.tempSelectBitmap.remove(Album.dataList.get(location));
            }
            Album.dataList.remove(location);
            //adapter.notifyDataSetChanged(); 在异步任务的子线程中不能刷新UI线程，所以注释了
            Album.delete(item, privImagePath, getContentResolver());
            return true;
        } else {//失败
            //加密失败：再进行一次异或（相当于事务回退）
            XorEncryptionUtil.encrypt(imagePath, null);
            return false;
        }
    }

    /**
     * 解密单张图片
     */
    public boolean decryptSinglePhoto() {
        ImageItem item = PrivatePhotoFragment.dateList.get(location);
        String privImagePath = item.getImagePath(); //这个私密文件的绝对路径
        String imagePath = privImagePath.replaceFirst("/data/data/" + getPackageName() + "/files/storage/emulated/0", "/storage/emulated/0");

        boolean b = XorEncryptionUtil.copyFile(privImagePath, imagePath);
        if (b) {
            if (Bimp.tempSelectBitmap.contains(PrivatePhotoFragment.dateList.get(location))) {
                Bimp.tempSelectBitmap.remove(PrivatePhotoFragment.dateList.get(location));
            }
            PrivatePhotoFragment.dateList.remove(location);
            PrivatePhotoFragment.delete(item, imagePath, getContentResolver());
            return true;
        } else {
            return false;
        }
    }

    /**
     * 切换 “加密” “解密” 按钮的显示与隐藏
     */
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


}
