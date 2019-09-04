package com.example.encrypt.vault;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.DataSetObserver;
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
import com.example.encrypt.util.Notifi;
import com.example.encrypt.util.XorEncryptionUtil;


import com.example.encrypt.video.PrivateVideoGridViewAdapter;
import com.example.encrypt.video.VideoAlbum;
import com.example.encrypt.video.VideoItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by User on 2/28/2017.
 */

public class PrivateVideoFragment extends Fragment {
    private GridView gridView;
    public static ArrayList<VideoItem> dateList;
    private PrivateVideoGridViewAdapter privateVideoAlbumGridViewAdapter;
    private static DatabaseAdapter databaseAdapter;
    private static ExecutorService executorService;
    private ProgressDialog progressDialog;
    private static TextView tvNoPicture;
    TextView file_count;
    static Button button_min;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vault_fragment,container,false);



        button_min=view.findViewById(R.id.button_min);

        file_count=view.findViewById(R.id.file_count);
        executorService = Executors.newCachedThreadPool();
        databaseAdapter = new DatabaseAdapter(getActivity());
        dateList = databaseAdapter.getVideo();

        initView(view);
        decryptVideosTemporary();


        return view;
    }
    @Override
    public void onResume() {
        super.onResume();

        executorService = Executors.newCachedThreadPool();
        databaseAdapter = new DatabaseAdapter(getActivity());
        dateList = databaseAdapter.getVideo();
        privateVideoAlbumGridViewAdapter = new PrivateVideoGridViewAdapter(getActivity(), dateList);
        gridView.setAdapter(privateVideoAlbumGridViewAdapter);
        if(dateList.size()==1){
            file_count.setText(dateList.size()+" File");
        }else {
            file_count.setText(dateList.size()+" Files");
        }
        privateVideoAlbumGridViewAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if(dateList.size()==1){
                    file_count.setText(dateList.size()+" File");
                }else {
                    file_count.setText(dateList.size()+" Files");
                }
            }
        });
        decryptVideosTemporary();
    }

    @Override
    public void onPause() {
        super.onPause();
     //   checkbox_select_all.setChecked(false);
        showDec();
        if (!BseApplication.sp.getBoolean("privVideoAlbumToVideoPlay", false)) {
            encryptVideosTemporary();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Bimp.tempSelectVideo.clear();

        dateList = null;
        databaseAdapter = null;
        executorService = null;
        tvNoPicture = null;
    }
   static CheckBox checkbox_select_all;
    int grid_count=4;
    public void initView(View view) {
        TextView tvTitle = (TextView) view.findViewById(R.id.title);
        tvTitle.setText(R.string.private_video_album);
        tvNoPicture = (TextView) view.findViewById(R.id.tv_no_picture);
        gridView = (GridView) view.findViewById(R.id.album_GridView);
        privateVideoAlbumGridViewAdapter = new PrivateVideoGridViewAdapter(getActivity(), dateList);
        gridView.setAdapter(privateVideoAlbumGridViewAdapter);
        if(dateList.size()==1){
            file_count.setText(dateList.size()+" File");
        }else {
            file_count.setText(dateList.size()+" Files");
        }
        privateVideoAlbumGridViewAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if(dateList.size()==1){
                    file_count.setText(dateList.size()+" File");
                }else {
                    file_count.setText(dateList.size()+" Files");
                }
            }
        });
        FloatingActionButton floatingActionButton=view.findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), VideoAlbum.class));
               // getActivity().finish();
            }
        });

        Button button_min=view.findViewById(R.id.button_min);
        button_min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Bimp.tempSelectVideo.size() == 0) {
                //    Toast.makeText(getActivity(), getString(R.string.choose_at_least_one_video), Toast.LENGTH_SHORT).show();
                    Notifi.message(getActivity(),getString(R.string.choose_at_least_one_video),true);
                }
                DecryptionTask decryptionTask = new DecryptionTask(Bimp.tempSelectVideo);
                decryptionTask.execute();
            }
        });
        checkbox_select_all=view.findViewById(R.id.checkbox_select_all);
        checkbox_select_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                privateVideoAlbumGridViewAdapter.selectAll(((CheckBox) view).isChecked());
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
                privateVideoAlbumGridViewAdapter = new PrivateVideoGridViewAdapter(getActivity(), dateList);
                gridView.setAdapter(privateVideoAlbumGridViewAdapter);
            }

        });
    }


    public static void showNoPictureTip() {
        tvNoPicture.setText(R.string.no_video);
        tvNoPicture.setVisibility(View.VISIBLE);
    }


    static boolean result = true;

    public static boolean decryptVideosTemporary() {
        if (BseApplication.sp.getBoolean("video_encrypt", true)) {

            for (final VideoItem item : dateList) {
                final String privVideoPath = item.getPath();
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        boolean b = XorEncryptionUtil.encrypt(privVideoPath, null);
                        if (!b) {
                            XorEncryptionUtil.encrypt(privVideoPath, null);
                        }
                        result &= b;
                    }
                });
            }



            BseApplication.editor.putBoolean("video_encrypt", false).commit();
        }
        return result;
    }


    static boolean result1 = true;

    public static boolean encryptVideosTemporary() {
        if (!BseApplication.sp.getBoolean("video_encrypt", false)) {
            for (final VideoItem item : dateList) {
                final String privVideoPath = item.getPath();
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        boolean b = XorEncryptionUtil.encrypt(privVideoPath, null);
                        if (!b) {
                            XorEncryptionUtil.encrypt(privVideoPath, null);
                        }
                        result1 &= b;
                    }
                });
            }
            BseApplication.editor.putBoolean("video_encrypt", true).commit();
        }
        return result1;
    }


    public static void showDec(){


        if (Bimp.tempSelectVideo.size() > 0) {
            button_min.setVisibility(View.VISIBLE);

        }else {
            button_min.setVisibility(View.GONE);

        }

    }

    public class DecryptionTask extends AsyncTask<Void, Void, Boolean> {
        private ArrayList<VideoItem> listPrivVideo;
        int startSize;

        public DecryptionTask(ArrayList<VideoItem> listPrivVideo) {
            this.listPrivVideo = listPrivVideo;
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startSize = getActivity().getApplicationContext().getContentResolver()
                    .query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null).getCount();
            progressDialog.setMessage(getString(R.string.decrypting));
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean result = decryptVideoList(listPrivVideo);
            int totalTime = 0;
            while (result && getActivity().getApplicationContext().getContentResolver().
                    query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null).getCount()
                    != (startSize + listPrivVideo.size()) && totalTime < listPrivVideo.size() / 2) {
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
            privateVideoAlbumGridViewAdapter.refreshDataAfterDecrypt();
            String showMessage = result ? getString(R.string.decrypt_success) : getString(R.string.partial_video_decryption_failed);
          //  Toast.makeText(getActivity(), showMessage, Toast.LENGTH_SHORT).show();
            Notifi.message(getActivity(),showMessage,result);
            checkbox_select_all.setChecked(false);
            progressDialog.dismiss();
            showDec();
        }
    }


    public boolean decryptVideoList(final ArrayList<VideoItem> arrayList) {
        ArrayList<Future<Boolean>> futures = new ArrayList<>();
        futures.clear();

        for (final VideoItem item : arrayList) {
            final String privVideoPath = item.getPath();

            final String videoPath = privVideoPath.replaceFirst("/data/data/" + getActivity().getPackageName() + "/files/storage/emulated/0", "/storage/emulated/0");
            Future<Boolean> future = executorService.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() {


                    boolean b = XorEncryptionUtil.copyFile(privVideoPath, videoPath);
                    if (b) {
                        deletePrivateVideo(item, videoPath,getActivity().getContentResolver());
                    }else {
                        XorEncryptionUtil.copyFile(privVideoPath, null);
                        boolean x = XorEncryptionUtil.copyFile(privVideoPath, videoPath);
                        if (x) {
                            deletePrivateVideo(item, videoPath, getActivity().getContentResolver());
                        } else {
                            XorEncryptionUtil.copyFile(privVideoPath, null);
                            b = x;
                            result=x;
                        }
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



    public static void deletePrivateVideo(VideoItem item, String videoPath, ContentResolver contentResolver) {

        new File(item.getPath()).delete();
        databaseAdapter.deleteVideo(item.getId());

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Video.Media._ID, item.getId());
        contentValues.put(MediaStore.Video.Media.DATA, videoPath);
        contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, item.getDisplayName());
        contentValues.put(MediaStore.Video.Media.SIZE, item.getSize());
        contentValues.put(MediaStore.Video.Media.MIME_TYPE, item.getMimeType());
        contentValues.put(MediaStore.Video.Media.DATE_ADDED, item.getDateAdded());
        contentValues.put(MediaStore.Video.Media.TITLE, item.getTitle());
        contentValues.put(MediaStore.Video.Media.ALBUM, item.getAlbum());
        contentValues.put(MediaStore.Video.Media.BUCKET_ID, item.getBucketId());
        contentValues.put(MediaStore.Video.Media.BUCKET_DISPLAY_NAME, item.getBucketDisplayName());
        contentValues.put(MediaStore.Video.Media.WIDTH, item.getWidth());
        contentValues.put(MediaStore.Video.Media.HEIGHT, item.getHeight());
        contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
    }



}
