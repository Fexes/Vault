package com.example.encrypt.photo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.bumptech.glide.Glide;
 import com.example.encrypt.R;
import com.example.encrypt.vault.PrivatePhotoFragment;
import com.example.encrypt.activity.BseApplication;

import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

/**
 * Created by dongrp on 2017/7/13.
 */

public class PrivateAlbumGridViewAdapter extends BaseAdapter {

    private ArrayList<ImageItem> listPrivFlies = new ArrayList<ImageItem>();
    private Context mContext;

    public PrivateAlbumGridViewAdapter(Context c, ArrayList<ImageItem> list) {
        mContext = c;
        listPrivFlies = list;
    }

    /**
     * 适配器 数据全选、取消全选 的方法
     *
     * @param isSelectedAll
     */

    public void selectAll(boolean isSelectedAll) {
        Bimp.tempSelectBitmap.clear();
        if (isSelectedAll) {
            Bimp.tempSelectBitmap.addAll(listPrivFlies);
        }
        notifyDataSetChanged();
    }


    public ArrayList<ImageItem> getDataList() {
        return listPrivFlies;
    }


    public ArrayList<ImageItem> getSelectedData() {
        return Bimp.tempSelectBitmap;
    }

    public void refreshDataAfterDecrypt() {
        listPrivFlies.removeAll(Bimp.tempSelectBitmap);
        notifyDataSetChanged();
        Bimp.tempSelectBitmap.clear();
    }


    public int getCount() {
        if (listPrivFlies.size() == 0) {
            PrivatePhotoFragment.showNoPictureTip();
        }
        return listPrivFlies.size();
    }

    public ImageItem getItem(int position) {
        return listPrivFlies.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    ArrayList<View> c=new ArrayList<View>();
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        c.add(convertView);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_album_gridview, parent, false);
            viewHolder.imageView =  convertView.findViewById(R.id.image_view);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Glide.with(mContext).load(listPrivFlies.get(position).getImagePath()).into(viewHolder.imageView);
        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (viewHolder.checkBox.isChecked()) {
                    Bimp.tempSelectBitmap.add(listPrivFlies.get(position));
                } else {
                    Bimp.tempSelectBitmap.remove(listPrivFlies.get(position));
                }
                PrivatePhotoFragment.showDec();
            }
        });
         if (Bimp.tempSelectBitmap.contains(listPrivFlies.get(position))) {
            viewHolder.checkBox.setChecked(true);


        } else {
            viewHolder.checkBox.setChecked(false);

        }

        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BseApplication.editor.putBoolean("privAlbumToGallery", true).commit();
                mContext.startActivity(new Intent(mContext, Gallery.class).putExtra("position", position).putExtra("isFromPrivateAlbum", true));

            }
        });


        return convertView;
    }

    public class ViewHolder {
        public RoundedImageView imageView;
        public CheckBox checkBox;
    }


}
