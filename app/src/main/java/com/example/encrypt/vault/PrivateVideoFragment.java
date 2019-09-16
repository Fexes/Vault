package com.example.encrypt.vault;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.encrypt.R;
import com.example.encrypt.database.DatabaseAdapter;
import com.example.encrypt.photo.Bimp;
import com.example.encrypt.photo.PrivateImageRecyclerViewAdapter;
import com.example.encrypt.util.Notifi;
import com.example.encrypt.video.PrivateVideoRecyclerViewAdapter;
import com.example.encrypt.video.VideoAlbum;
import com.example.encrypt.video.VideoItem;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by User on 2/28/2017.
 */

public class PrivateVideoFragment extends Fragment {

    public static ArrayList<VideoItem> dateList;

    private static DatabaseAdapter databaseAdapter;

    private ProgressDialog progressDialog;
    private static TextView tvNoPicture;
    TextView file_count;
    static Button button_min;
    static CheckBox checkbox_select_all;

    public static void hideNoPictureTip() {
        tvNoPicture.setText(R.string.no_video);
        tvNoPicture.setVisibility(View.GONE);
    }

    public static RecyclerView.Adapter adapter;
    public static RecyclerView mRecyclerView;
    private List<Object> mRecyclerViewItems;
    private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();

    private List<Object> mRecyclerViewVideoItems = new ArrayList<>();
    private List<VideoItem> mRecyclerViewVideoItems2 = new ArrayList<>();

    public static void showNoPictureTip() {
        tvNoPicture.setText(R.string.no_video);
        tvNoPicture.setVisibility(View.VISIBLE);
    }

    static FloatingActionButton cancel, floatingActionButton;

    @Override
    public void onResume() {
        super.onResume();
        relodads();
        resetView();
    }

    @SuppressLint("RestrictedApi")
    public static void cancel_long() {

        cancel.setVisibility(View.VISIBLE);
        floatingActionButton.setVisibility(View.GONE);
    }


    static boolean result = true;

    public static void showDec(){


        if (Bimp.tempSelectVideo.size() > 0) {
            button_min.setVisibility(View.VISIBLE);

        }else {
            button_min.setVisibility(View.GONE);

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean decryptVideoList(final ArrayList<VideoItem> arrayList) {
        int current = 0;


        for (final VideoItem item : arrayList) {
            final String privVideoPath = item.getPath();

            final String videoPath = privVideoPath.replaceFirst("/data/data/" + getActivity().getPackageName() + "/files/storage/emulated/0", "/storage/emulated/0");


            boolean b = moveFile(getContext(), privVideoPath, videoPath, arrayList.size(), current);
                    if (b) {
                        deletePrivateVideo(item, videoPath,getActivity().getContentResolver());
                        current++;
                    }else {

                        if (moveFile(getContext(), privVideoPath, videoPath, arrayList.size(), current)) {
                            deletePrivateVideo(item, videoPath, getActivity().getContentResolver());
                            current++;
                        }
                    }


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

                //        progressDialog.setMessage("Files :" + TotalFiles + " / " + current + "\n" + "Progress :" + progress + " % ");

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

    public void resetView() {
        dateList = databaseAdapter.getVideo();
        databaseAdapter = new DatabaseAdapter(getActivity());

        adapter = new PrivateVideoRecyclerViewAdapter(getActivity(), mRecyclerViewItems);

        if (dateList.size() == 0) {
            showNoPictureTip();
        } else {
            hideNoPictureTip();
        }
        if (dateList.size() == 1) {
            file_count.setText(dateList.size() + " File");
        } else {
            file_count.setText(dateList.size() + " Files");
        }
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (dateList.size() == 1) {
                    file_count.setText(dateList.size() + " File");
                } else {
                    file_count.setText(dateList.size() + " Files");
                }
            }
        });
        mRecyclerView.setAdapter(adapter);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Bimp.tempSelectVideo.clear();
        PrivateImageRecyclerViewAdapter.long_click = false;
        dateList = null;
        databaseAdapter = null;

        tvNoPicture = null;
    }

    private AdLoader adLoader;

    @Override
    public void onPause() {
        super.onPause();
        showDec();
        PrivateImageRecyclerViewAdapter.long_click = false;
    }

    public void initView(View view) {
        TextView tvTitle = view.findViewById(R.id.title);
        tvTitle.setText(R.string.private_video_album);
        tvNoPicture = view.findViewById(R.id.tv_no_picture);

        floatingActionButton = view.findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), VideoAlbum.class));
            }
        });
        cancel = view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {

                PrivateVideoRecyclerViewAdapter.long_click = false;
                cancel.setVisibility(View.GONE);
                floatingActionButton.setVisibility(View.VISIBLE);
                Bimp.tempSelectVideo.clear();
                showDec();
                adapter.notifyDataSetChanged();
            }
        });
        Button button_min = view.findViewById(R.id.button_min);
        button_min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Bimp.tempSelectVideo.size() == 0) {
                    Notifi.message(getActivity(), getString(R.string.choose_at_least_one_video), true);
                }
                DecryptionTask decryptionTask = new DecryptionTask(Bimp.tempSelectVideo);
                decryptionTask.execute();
            }
        });
        checkbox_select_all = view.findViewById(R.id.checkbox_select_all);
        checkbox_select_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bimp.tempSelectVideo.clear();

                if (((CheckBox) view).isChecked()) {
                    Bimp.tempSelectVideo.addAll(mRecyclerViewVideoItems2);
                    PrivateVideoRecyclerViewAdapter.long_click = true;
                }

                adapter.notifyDataSetChanged();
                showDec();
            }
        });
        ImageView grid = view.findViewById(R.id.grid);
        grid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        ImageView reverse = view.findViewById(R.id.reverse);
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Collections.reverse(mRecyclerViewVideoItems);
                adapter = new PrivateImageRecyclerViewAdapter(getActivity(), mRecyclerViewVideoItems);
                mRecyclerView.setAdapter(adapter);
            }

        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vault_fragment, container, false);

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        button_min = view.findViewById(R.id.button_min);

        file_count = view.findViewById(R.id.file_count);

        databaseAdapter = new DatabaseAdapter(getActivity());
        dateList = databaseAdapter.getVideo();


        mRecyclerViewItems = getRecyclerViewVideoItems();
        mRecyclerViewVideoItems2 = getRecyclerViewVideoItems2();
        //  View rootView = inflater.inflate(R.layout.vault_fragment, container, false);
        mRecyclerView = view.findViewById(R.id.recycler_view);

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView.
        mRecyclerView.setHasFixedSize(false);

        // Specify a linear layout manager.
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 3);


        mRecyclerView.setLayoutManager(layoutManager);

        // Specify an adapter.
        adapter = new PrivateVideoRecyclerViewAdapter(getActivity(), mRecyclerViewItems);
        mRecyclerView.setAdapter(adapter);

        initView(view);
        loadVideoData();
        loadNativeAds();

        return view;
    }

    private void loadNativeAds() {

        AdLoader.Builder builder = new AdLoader.Builder(getContext(), "ca-app-pub-3940256099942544/2247696110");
        adLoader = builder.forUnifiedNativeAd(
                new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {

                        mNativeAds.add(unifiedNativeAd);
                        if (!adLoader.isLoading()) {
                            insertImageAds();
                        }
                    }
                }).withAdListener(
                new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {

                        Log.e("MainActivity", "The previous native ad failed to load. Attempting to"
                                + " load another.");
                        if (!adLoader.isLoading()) {
                            insertImageAds();
                        }
                    }
                }).build();

        if (mRecyclerViewVideoItems.size() > 219) {
            adLoader.loadAds(new AdRequest.Builder().build(), 4);
        } else if (mRecyclerViewVideoItems.size() > 146) {
            adLoader.loadAds(new AdRequest.Builder().build(), 3);
        } else if (mRecyclerViewVideoItems.size() > 73) {
            adLoader.loadAds(new AdRequest.Builder().build(), 2);
        } else if (mRecyclerViewVideoItems.size() > 6) {
            adLoader.loadAds(new AdRequest.Builder().build(), 1);
        }

    }

    private void insertImageAds() {
        if (mNativeAds.size() <= 0) {
            return;
        }

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);

        try {
            mRecyclerViewVideoItems.add(6, mNativeAds.get(0));
            mRecyclerViewVideoItems.add(73, mNativeAds.get(1));
            mRecyclerViewVideoItems.add(146, mNativeAds.get(2));
            mRecyclerViewVideoItems.add(219, mNativeAds.get(3));


            adapter.notifyItemInserted(6);
            adapter.notifyItemInserted(73);
            adapter.notifyItemInserted(146);
            adapter.notifyItemInserted(219);
            adapter.notifyItemRangeInserted(6, mRecyclerViewItems.size());
            adapter.notifyItemRangeInserted(73, mRecyclerViewItems.size());
            adapter.notifyItemRangeInserted(146, mRecyclerViewItems.size());
            adapter.notifyItemRangeInserted(219, mRecyclerViewItems.size());
        } catch (Exception e) {
            // Toast.makeText(getContext(),e.toString(),Toast.LENGTH_SHORT).show();
        }

        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {


                if (position == 6 || position == 73 || position == 146 || position == 219 && position != 0) {

                    return 3;
                } else
                    return 1;

            }
        });
        mRecyclerView.setLayoutManager(layoutManager);

    }

    public List<Object> getRecyclerViewVideoItems() {
        return mRecyclerViewVideoItems;
    }

    public List<VideoItem> getRecyclerViewVideoItems2() {
        return mRecyclerViewVideoItems2;
    }

    private void loadVideoData() {
        databaseAdapter = new DatabaseAdapter(getActivity());
        dateList = databaseAdapter.getVideo();
        for (int i = 0; i <= dateList.size() - 1; i++) {

            mRecyclerViewVideoItems.add(dateList.get(i));
            mRecyclerViewVideoItems2.add(dateList.get(i));
        }
    }

    private void relodads() {
        mRecyclerView.removeAllViews();
        mRecyclerViewVideoItems.clear();
        mRecyclerViewVideoItems2.clear();
        mRecyclerViewVideoItems = getRecyclerViewVideoItems();
        mRecyclerViewVideoItems2 = getRecyclerViewVideoItems2();
        //  View rootView = inflater.inflate(R.layout.vault_fragment, container, false);


        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView.
        mRecyclerView.setHasFixedSize(false);

        // Specify a linear layout manager.
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 3);


        mRecyclerView.setLayoutManager(layoutManager);

        // Specify an adapter.
        adapter = new PrivateImageRecyclerViewAdapter(getActivity(), mRecyclerViewVideoItems);
        mRecyclerView.setAdapter(adapter);


        loadVideoData();
        loadNativeAds();
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

            progressDialog.setIcon(R.mipmap.ic_launcher);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setTitle(getString(R.string.decrypting));
            progressDialog.show();


        }

        @RequiresApi(api = Build.VERSION_CODES.N)
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
            relodads();
            mRecyclerViewVideoItems.removeAll(Bimp.tempSelectVideo);
            adapter.notifyDataSetChanged();
            Bimp.tempSelectVideo.clear();
            String showMessage = result ? getString(R.string.decrypt_success) : getString(R.string.partial_video_decryption_failed);
            Notifi.message(getActivity(), showMessage, result);
            checkbox_select_all.setChecked(false);
            progressDialog.dismiss();
            showDec();
            resetView();
        }
    }
}