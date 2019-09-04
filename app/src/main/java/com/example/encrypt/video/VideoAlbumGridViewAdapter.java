package com.example.encrypt.video;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.encrypt.R;
import com.example.encrypt.photo.Bimp;
import com.example.encrypt.photo.BitmapCache;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;



public class VideoAlbumGridViewAdapter extends BaseAdapter {

    private final String TAG = getClass().getSimpleName();
    private ArrayList<VideoItem> dataList;
    private Context context;
    private BitmapCache cache;

    public VideoAlbumGridViewAdapter(Context context, ArrayList<VideoItem> dataList) {
        this.context = context;
        this.dataList = dataList;
        cache = new BitmapCache();
    }

    public void selectAll(boolean selectAll) {
        Bimp.tempSelectVideo.clear();
        if (selectAll) {
            Bimp.tempSelectVideo.addAll(dataList);
        }
        notifyDataSetChanged();
    }


    public void refreshDataAfterEncrypt() {
        dataList.removeAll(Bimp.tempSelectVideo);
        notifyDataSetChanged();
        Bimp.tempSelectVideo.clear();
    }

    public int getCount() {
        return dataList.size();
    }

    public Object getItem(int position) {
        return dataList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }


    private class ViewHolder {
        public RoundedImageView imageView;
        public CheckBox checkBox;
        public ImageView imagePlay;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_video_album_gridview, parent, false);
            viewHolder.imageView =  convertView.findViewById(R.id.image_view);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            viewHolder.imagePlay = (ImageView) convertView.findViewById(R.id.image_play);
            viewHolder.imagePlay.setVisibility(View.VISIBLE);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        Glide.with(context).load(dataList.get(position).getPath()).into(viewHolder.imageView);
        viewHolder.checkBox.setTag(position);
        viewHolder.checkBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolder.checkBox.isChecked()) {
                    Bimp.tempSelectVideo.add(dataList.get(position));
                } else {
                    Bimp.tempSelectVideo.remove(dataList.get(position));
                }
                VideoAlbum.showDec();
            }
        });

        if (Bimp.tempSelectVideo.contains(dataList.get(position))) {
            viewHolder.checkBox.setChecked(true);
        } else {
            viewHolder.checkBox.setChecked(false);
        }

        viewHolder.imageView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Uri uri = Uri.parse(dataList.get(position).getPath());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "video/*");
                context.startActivity(intent);
            }
        });
        return convertView;
    }

}
