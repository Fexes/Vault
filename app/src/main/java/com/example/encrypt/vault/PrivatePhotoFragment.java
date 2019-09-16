package com.example.encrypt.vault;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import com.example.encrypt.activity.BseApplication;
import com.example.encrypt.database.DatabaseAdapter;
import com.example.encrypt.photo.Bimp;
import com.example.encrypt.photo.Folders;
import com.example.encrypt.photo.ImageItem;
import com.example.encrypt.photo.PrivateImageRecyclerViewAdapter;
import com.example.encrypt.util.Notifi;
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


public class PrivatePhotoFragment extends Fragment {

    public static ArrayList<ImageItem> dateList;


    private static DatabaseAdapter databaseAdapter;
    private ProgressDialog progressDialog;
    private static TextView tvNoPicture;
    TextView file_count;
    static Button button_min;
    public static Context c;
    View supervire;
    private boolean result = true;

    public static void hideNoPictureTip() {
        tvNoPicture.setVisibility(View.GONE);
    }

    public static void delete(ImageItem item, String imagePath, ContentResolver contentResolver) {

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

    static CheckBox checkbox_select_all;


    private List<ImageItem> mRecyclerViewImageItems2 = new ArrayList<>();


    public static void showDec() {
        if (Bimp.tempSelectBitmap.size() == 0) {

            button_min.setVisibility(View.GONE);
        } else {
            button_min.setVisibility(View.VISIBLE);

        }

    }

    public static RecyclerView.Adapter adapter;
    public static RecyclerView mRecyclerView;

    private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();
    static FloatingActionButton cancel, floatingActionButton;

    @SuppressLint("RestrictedApi")
    public static void cancel_long() {

        cancel.setVisibility(View.VISIBLE);
        floatingActionButton.setVisibility(View.GONE);
    }
    public void initButtons(View view) {


        floatingActionButton = view.findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Folders.class));
            }
        });
        cancel = view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                PrivateImageRecyclerViewAdapter.long_click = false;

                cancel.setVisibility(View.GONE);
                floatingActionButton.setVisibility(View.VISIBLE);
                Bimp.tempSelectBitmap.clear();
                showDec();
                adapter.notifyDataSetChanged();
            }
        });
        Button button_min = view.findViewById(R.id.button_min);
        button_min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Bimp.tempSelectBitmap.size() == 0) {
                    Notifi.message(getActivity(), getString(R.string.choose_at_least_one_picture), true);

                }
                DecryptionTask decryptionTask = new DecryptionTask(Bimp.tempSelectBitmap);
                decryptionTask.execute();
            }
        });
        checkbox_select_all = view.findViewById(R.id.checkbox_select_all);
        checkbox_select_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bimp.tempSelectBitmap.clear();
                if (((CheckBox) view).isChecked()) {
                    Bimp.tempSelectBitmap.addAll(mRecyclerViewImageItems2);
                    PrivateImageRecyclerViewAdapter.long_click = true;
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

                Collections.reverse(mRecyclerViewImageItems);
                adapter = new PrivateImageRecyclerViewAdapter(getActivity(), mRecyclerViewImageItems);
                mRecyclerView.setAdapter(adapter);

            }

        });
    }

    public static void showNoPictureTip() {
        tvNoPicture.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Bimp.tempSelectBitmap.clear();
        dateList = null;
        databaseAdapter = null;
        PrivateImageRecyclerViewAdapter.long_click = false;
        tvNoPicture = null;
    }

    public void resetView() {
        dateList = databaseAdapter.getPhoto();


        tvNoPicture = supervire.findViewById(R.id.tv_no_picture);
        adapter = new PrivateImageRecyclerViewAdapter(getActivity(), mRecyclerViewImageItems);
        if (dateList.size() == 0) {
            showNoPictureTip();
        } else {
            hideNoPictureTip();
        }
        if(dateList.size()==1){
            file_count.setText(dateList.size()+" Image");
        }else {
            file_count.setText(dateList.size()+" Images");
        }
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (dateList.size() == 1) {
                    file_count.setText(dateList.size() + " Image");
                } else {
                    file_count.setText(dateList.size() + " Images");
                }
            }
        });
        mRecyclerView.setAdapter(adapter);

    }

    @Override
    public void onPause() {
        super.onPause();
        PrivateImageRecyclerViewAdapter.long_click = false;
        showDec();
         if (!BseApplication.sp.getBoolean("privAlbumToGallery", false)) {

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean decryptFileList(final ArrayList<ImageItem> arrayList) {

        int current = 0;
        for (final ImageItem item : arrayList) {
            final String privImagePath = item.getImagePath();

            final String imagePath = privImagePath.replaceFirst("/data/data/" + getActivity().getPackageName() + "/files/storage/emulated/0", "/storage/emulated/0");
            boolean b = moveFile(getContext(), privImagePath, imagePath, arrayList.size(), current);
            if (b) {
                delete(item, imagePath, getActivity().getContentResolver());
                current++;
            } else {

                if (moveFile(getContext(), privImagePath, imagePath, arrayList.size(), current)) {
                    delete(item, imagePath, getActivity().getContentResolver());
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

                //    progressDialog.setMessage("Files :" + TotalFiles + " / " + current + "\n" + "Progress :" + progress + " % ");

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

    @Override
    public void onResume() {
        super.onResume();
        relodads();
        resetView();

    }

    private AdLoader adLoader;
    private List<Object> mRecyclerViewImageItems = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vault_fragment, container, false);
        // addAppActivity(PrivateAlbum.this);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getActivity().getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        mRecyclerViewImageItems = getRecyclerViewImageItems();
        mRecyclerViewImageItems2 = getRecyclerViewImageItems2();
        //  View rootView = inflater.inflate(R.layout.vault_fragment, container, false);
        mRecyclerView = view.findViewById(R.id.recycler_view);

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView.
        mRecyclerView.setHasFixedSize(false);

        // Specify a linear layout manager.
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 3);


        mRecyclerView.setLayoutManager(layoutManager);

        // Specify an adapter.
        adapter = new PrivateImageRecyclerViewAdapter(getActivity(), mRecyclerViewImageItems);
        mRecyclerView.setAdapter(adapter);


        loadImageData();
        loadNativeAds();


        supervire = view;
        c = getContext();
        button_min = view.findViewById(R.id.button_min);

        file_count = view.findViewById(R.id.file_count);

        databaseAdapter = new DatabaseAdapter(getActivity());

        initButtons(view);

        dateList = databaseAdapter.getPhoto();


        tvNoPicture = view.findViewById(R.id.tv_no_picture);


        if (dateList.size() == 1) {
            file_count.setText(dateList.size() + " Image");
        } else {
            file_count.setText(dateList.size() + " Images");
        }
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (dateList.size() == 1) {
                    file_count.setText(dateList.size() + " Image");
                } else {
                    file_count.setText(dateList.size() + " Images");
                }
            }
        });
        mRecyclerView.setAdapter(adapter);

        return view;
    }

    public List<ImageItem> getRecyclerViewImageItems2() {
        return mRecyclerViewImageItems2;
    }

    public List<Object> getRecyclerViewImageItems() {
        return mRecyclerViewImageItems;
    }

    private void relodads() {
        mRecyclerView.removeAllViews();
        mRecyclerViewImageItems.clear();
        mRecyclerViewImageItems2.clear();
        mRecyclerViewImageItems = getRecyclerViewImageItems();
        mRecyclerViewImageItems2 = getRecyclerViewImageItems2();
        //  View rootView = inflater.inflate(R.layout.vault_fragment, container, false);


        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView.
        mRecyclerView.setHasFixedSize(false);

        // Specify a linear layout manager.
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 3);


        mRecyclerView.setLayoutManager(layoutManager);

        // Specify an adapter.
        adapter = new PrivateImageRecyclerViewAdapter(getActivity(), mRecyclerViewImageItems);
        mRecyclerView.setAdapter(adapter);


        loadImageData();
        loadNativeAds();
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

        if (mRecyclerViewImageItems.size() > 219) {
            adLoader.loadAds(new AdRequest.Builder().build(), 4);
        } else if (mRecyclerViewImageItems.size() > 146) {
            adLoader.loadAds(new AdRequest.Builder().build(), 3);
        } else if (mRecyclerViewImageItems.size() > 73) {
            adLoader.loadAds(new AdRequest.Builder().build(), 2);
        } else if (mRecyclerViewImageItems.size() > 6) {
            adLoader.loadAds(new AdRequest.Builder().build(), 1);
        }

    }

    private void insertImageAds() {
        if (mNativeAds.size() <= 0) {
            return;
        }

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);

        try {
            mRecyclerViewImageItems.add(6, mNativeAds.get(0));
            mRecyclerViewImageItems.add(73, mNativeAds.get(1));
            mRecyclerViewImageItems.add(146, mNativeAds.get(2));
            mRecyclerViewImageItems.add(219, mNativeAds.get(3));


            adapter.notifyItemInserted(6);
            adapter.notifyItemInserted(73);
            adapter.notifyItemInserted(146);
            adapter.notifyItemInserted(219);
            adapter.notifyItemRangeInserted(6, mRecyclerViewImageItems.size());
            adapter.notifyItemRangeInserted(73, mRecyclerViewImageItems.size());
            adapter.notifyItemRangeInserted(146, mRecyclerViewImageItems.size());
            adapter.notifyItemRangeInserted(219, mRecyclerViewImageItems.size());
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

    private void loadImageData() {
        databaseAdapter = new DatabaseAdapter(getContext());
        dateList = databaseAdapter.getPhoto();
        for (int i = 0; i <= dateList.size() - 1; i++) {

            mRecyclerViewImageItems.add(dateList.get(i));
            mRecyclerViewImageItems2.add(dateList.get(i));
        }
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
            progressDialog.setTitle(getString(R.string.decrypting));
            progressDialog.setIcon(R.mipmap.ic_launcher);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public Boolean doInBackground(Void... params) {
            boolean result = decryptFileList(listPrivFliePath);
            int totalTime = 0;
            while (result && getActivity().getApplicationContext().getContentResolver().
                    query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null).getCount()
                    != (startSize + listPrivFliePath.size()) && totalTime < listPrivFliePath.size() / 2) {
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
        public void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            relodads();
            mRecyclerViewImageItems.removeAll(Bimp.tempSelectBitmap);
            adapter.notifyDataSetChanged();
            Bimp.tempSelectBitmap.clear();
            String showMessage = result ? getString(R.string.decrypt_success) : getString(R.string.partial_picture_decryption_failed);
            Notifi.message(getActivity(), showMessage, result);
            checkbox_select_all.setChecked(false);
            progressDialog.dismiss();
            resetView();
            showDec();
        }
    }

}
