package com.example.encrypt.vault;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.encrypt.R;
import com.example.encrypt.activity.BseApplication;
import com.example.encrypt.database.DatabaseAdapter;
import com.example.encrypt.photo.Bimp;
import com.example.encrypt.photo.Folders;
import com.example.encrypt.photo.ImageItem;
 import com.example.encrypt.photo.PrivateAlbumGridViewAdapter;
import com.example.encrypt.util.Notifi;
import com.example.encrypt.util.XorEncryptionUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class PrivatePhotoFragment extends Fragment {
    private GridView gridView;
    public static ArrayList<ImageItem> dateList;
    private PrivateAlbumGridViewAdapter privateAlbumGridViewAdapter;
    private static ExecutorService executorService;
    private static DatabaseAdapter databaseAdapter;
    private ProgressDialog progressDialog;
    private static TextView tvNoPicture;
    TextView file_count;
    static Button button_min;
    public static Context c;
    View supervire;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vault_fragment,container,false);
       // addAppActivity(PrivateAlbum.this);

        supervire=view;
        c=getContext();
        button_min=view.findViewById(R.id.button_min);

        file_count=view.findViewById(R.id.file_count);
        executorService = Executors.newCachedThreadPool();
        databaseAdapter = new DatabaseAdapter(getActivity());

        initButtons(view);

        dateList = databaseAdapter.getPhoto();

        decryptAndEncryptPhotosTemporary();

        tvNoPicture = (TextView) view.findViewById(R.id.tv_no_picture);
        gridView = (GridView) view.findViewById(R.id.album_GridView);//组件
        privateAlbumGridViewAdapter = new PrivateAlbumGridViewAdapter(getActivity(), dateList);
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
        gridView.setAdapter(privateAlbumGridViewAdapter);


        return view;
    }
    @Override
    public void onResume() {
        super.onResume();

        dateList = databaseAdapter.getPhoto();

        decryptAndEncryptPhotosTemporary();

        tvNoPicture = (TextView) supervire.findViewById(R.id.tv_no_picture);
        gridView = (GridView) supervire.findViewById(R.id.album_GridView);//组件
        privateAlbumGridViewAdapter = new PrivateAlbumGridViewAdapter(getActivity(), dateList);
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
        gridView.setAdapter(privateAlbumGridViewAdapter);


    }
    static CheckBox checkbox_select_all;
    int grid_count=3;
    public void initButtons(View view){
        FloatingActionButton floatingActionButton=view.findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Folders.class));
                //getActivity().finish();
            }
        });

        Button button_min=view.findViewById(R.id.button_min);
        button_min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Bimp.tempSelectBitmap.size() == 0) {
                 //   Toast.makeText(getActivity(), getString(R.string.choose_at_least_one_picture), Toast.LENGTH_SHORT).show();
                    Notifi.message(getActivity(),getString(R.string.choose_at_least_one_picture),true);

                }
                PrivatePhotoFragment.DecryptionTask decryptionTask = new PrivatePhotoFragment.DecryptionTask(Bimp.tempSelectBitmap);
                decryptionTask.execute();
            }
        });
        checkbox_select_all=view.findViewById(R.id.checkbox_select_all);
        checkbox_select_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                privateAlbumGridViewAdapter.selectAll(((CheckBox) view).isChecked());
                showDec();
            }
        });
        ImageView grid=view.findViewById(R.id.grid);
        grid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });

        ImageView reverse=view.findViewById(R.id.reverse);
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Collections.reverse(dateList);
                privateAlbumGridViewAdapter = new PrivateAlbumGridViewAdapter(getActivity(), dateList);
                gridView.setAdapter(privateAlbumGridViewAdapter);
            }

        });
    }


    public static void showDec(){
        if (Bimp.tempSelectBitmap.size() == 0) {

            button_min.setVisibility(View.GONE);
        }else {
            button_min.setVisibility(View.VISIBLE);

        }

    }
    @Override
    public void onPause() {
        super.onPause();

        showDec();
         if (!BseApplication.sp.getBoolean("privAlbumToGallery", false)) {
            encryptPhotosTemporary();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Bimp.tempSelectBitmap.clear();
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
                        if (!b) {
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
                        if (!b) {
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




    public class DecryptionTask extends AsyncTask<Void, Void, Boolean> {
        private ArrayList<ImageItem> listPrivFliePath;
        int startSize;

        public DecryptionTask(ArrayList<ImageItem> listPrivFliePath) {
            this.listPrivFliePath = listPrivFliePath;
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setCancelable(false);
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            startSize = getActivity().getContentResolver()
                    .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null).getCount();
            progressDialog.setMessage(getString(R.string.decrypting));
            progressDialog.show();
        }

        @Override
        public Boolean doInBackground(Void... params) {
            boolean result = decryptFileList(listPrivFliePath); //解密文件集合
            int totalTime = 0;
            while (result && getActivity().getApplicationContext().getContentResolver().
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
        public void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            privateAlbumGridViewAdapter.refreshDataAfterDecrypt();
            //loadFirstScreenImage();
            String showMessage = result ? getString(R.string.decrypt_success) : getString(R.string.partial_picture_decryption_failed);
           // Toast.makeText(getActivity(), showMessage, Toast.LENGTH_SHORT).show();
            Notifi.message(getActivity(),showMessage,result);
            checkbox_select_all.setChecked(false);
            progressDialog.dismiss();
            showDec();
        }
    }


    boolean result = true;


    public boolean decryptFileList(final ArrayList<ImageItem> arrayList) {
        for (final ImageItem item : arrayList) {
            final String privImagePath = item.getImagePath();

            final String imagePath = privImagePath.replaceFirst("/data/data/" + getActivity().getPackageName() + "/files/storage/emulated/0", "/storage/emulated/0");
            executorService.submit(new Runnable() {
                @Override
                public void run() {

                    boolean b = XorEncryptionUtil.copyFile(privImagePath, imagePath);
                    if (b) {
                        delete(item, imagePath, getActivity().getContentResolver());
                    } else {
                        XorEncryptionUtil.copyFile(privImagePath, null);
                        boolean x = XorEncryptionUtil.copyFile(privImagePath, imagePath);
                        if (x) {
                            delete(item, imagePath, getActivity().getContentResolver());
                        } else {
                            XorEncryptionUtil.copyFile(privImagePath, null);
                            result = x;
                        }
                    }
                }
            });
        }
        return result;
    }


    public static void delete(ImageItem item, String imagePath, ContentResolver contentResolver) {
         File file = new File(item.getImagePath());
         file.delete();
         databaseAdapter.deletePhoto(item.getImageId());
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
