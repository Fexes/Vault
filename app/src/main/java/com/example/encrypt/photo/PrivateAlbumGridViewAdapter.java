package com.example.encrypt.photo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.encrypt.R;
import com.example.encrypt.activity.BseApplication;
import com.example.encrypt.vault.PrivatePhotoFragment;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;


public class PrivateAlbumGridViewAdapter extends BaseAdapter {

    private ArrayList<ImageItem> listPrivFlies = new ArrayList<ImageItem>();
    private Context mContext;

    public PrivateAlbumGridViewAdapter(Context c, ArrayList<ImageItem> list) {
        mContext = c;
        listPrivFlies = list;
    }


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

        if (listPrivFlies.get(position).getImageId().equals("ad")) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.xxx, parent, false);


        } else {

            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_album_gridview, parent, false);
                viewHolder.imageView = convertView.findViewById(R.id.image_view);
                viewHolder.checkBox = convertView.findViewById(R.id.checkBox);
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

        }
        return convertView;
    }

    public class ViewHolder {
        public RoundedImageView imageView;
        public CheckBox checkBox;
    }

    private void populateNativeAdView(UnifiedNativeAd nativeAd,
                                      UnifiedNativeAdView adView) {
        // Some assets are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        NativeAd.Image icon = nativeAd.getIcon();

        if (icon == null) {
            adView.getIconView().setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(icon.getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAd);
    }
}
