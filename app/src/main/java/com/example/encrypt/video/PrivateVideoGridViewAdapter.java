package com.example.encrypt.video;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.encrypt.R;
import com.example.encrypt.activity.BseApplication;
import com.example.encrypt.photo.Bimp;
import com.example.encrypt.vault.PrivateVideoFragment;
import com.example.encrypt.video.PrivateVideoPlayActivity;
import com.example.encrypt.video.VideoItem;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;


public class PrivateVideoGridViewAdapter extends BaseAdapter {

    private ArrayList<VideoItem> listPrivFlies = new ArrayList<>();
    private Context mContext;

    public PrivateVideoGridViewAdapter(Context c, ArrayList<VideoItem> list) {
        mContext = c;
        listPrivFlies = list;
    }


    public void selectAll(boolean isSelectedAll) {
        Bimp.tempSelectVideo.clear();
        if (isSelectedAll) {
            Bimp.tempSelectVideo.addAll(listPrivFlies);
        }
        notifyDataSetChanged();
    }


    public void refreshDataAfterDecrypt() {
        listPrivFlies.removeAll(Bimp.tempSelectVideo);
        notifyDataSetChanged();
        Bimp.tempSelectVideo.clear();
    }

    public int getCount() {
        if (listPrivFlies.size() == 0) {
            PrivateVideoFragment.showNoPictureTip();
        }
        return listPrivFlies.size();
    }
    public VideoItem getItem(int position) {
        return listPrivFlies.get(position);
    }
    public long getItemId(int position) {
        return position;
    }
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_video_album_gridview, parent, false);
            viewHolder.imageView =  convertView.findViewById(R.id.image_view);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            viewHolder.imagePlay = (ImageView) convertView.findViewById(R.id.image_play);
            viewHolder.imagePlay.setVisibility(View.VISIBLE);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Glide.with(mContext).load(listPrivFlies.get(position).getPath()).into(viewHolder.imageView);
        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (viewHolder.checkBox.isChecked()) {
                    Bimp.tempSelectVideo.add(listPrivFlies.get(position));
                } else {
                    Bimp.tempSelectVideo.remove(listPrivFlies.get(position));
                }
                PrivateVideoFragment.showDec();
            }
        });

        if (Bimp.tempSelectVideo.contains(listPrivFlies.get(position))) {
            viewHolder.checkBox.setChecked(true);
        } else {
            viewHolder.checkBox.setChecked(false);
        }

        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BseApplication.editor.putBoolean("privVideoAlbumToVideoPlay", true).commit();

                mContext.startActivity(new Intent(mContext, PrivateVideoPlayActivity.class).putExtra("videoPath", listPrivFlies.get(position).getPath()));
            }
        });
        return convertView;
    }


    public class ViewHolder {
        public RoundedImageView imageView;
        public CheckBox checkBox;
        public ImageView imagePlay;
    }


}
