/*
 * Copyright (C) 2017 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.encrypt.photo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.encrypt.R;
import com.example.encrypt.activity.BseApplication;
import com.example.encrypt.vault.PrivatePhotoFragment;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // A menu item view type.
    private static final int MENU_ITEM_VIEW_TYPE = 0;

    private static final int UNIFIED_NATIVE_AD_VIEW_TYPE = 1;
    // An Activity's Context.
    private final Context mContext;

    // The list of Native ads and menu items.
    private final List<Object> mRecyclerViewItems;


    public RecyclerViewAdapter(Context context, List<Object> mRecyclerViewItems) {
        this.mContext = context;

        this.mRecyclerViewItems = mRecyclerViewItems;
    }

    @Override
    public int getItemCount() {
        return mRecyclerViewItems.size();
    }

    @Override
    public int getItemViewType(int position) {

        Object recyclerViewItem = mRecyclerViewItems.get(position);
        if (recyclerViewItem instanceof UnifiedNativeAd) {
            return UNIFIED_NATIVE_AD_VIEW_TYPE;
        }
        return MENU_ITEM_VIEW_TYPE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case UNIFIED_NATIVE_AD_VIEW_TYPE:
                View unifiedNativeLayoutView = LayoutInflater.from(
                        viewGroup.getContext()).inflate(R.layout.xxx,
                        viewGroup, false);

                return new UnifiedNativeAdViewHolder(unifiedNativeLayoutView);
            case MENU_ITEM_VIEW_TYPE:
                // Fall through.
            default:
                View menuItemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.item_album_gridview, viewGroup, false);
                return new MenuItemViewHolder(menuItemLayoutView);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case UNIFIED_NATIVE_AD_VIEW_TYPE:
                UnifiedNativeAd nativeAd = (UnifiedNativeAd) mRecyclerViewItems.get(position);
                populateNativeAdView(nativeAd, ((UnifiedNativeAdViewHolder) holder).getAdView());
                break;
            case MENU_ITEM_VIEW_TYPE:
                // fall through
            default:
                final MenuItemViewHolder menuItemHolder = (MenuItemViewHolder) holder;
                try {
                    final ImageItem menuItem = (ImageItem) mRecyclerViewItems.get(position);


                    Glide.with(mContext).load(menuItem.getImagePath()).into(menuItemHolder.imageView);
                    menuItemHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (menuItemHolder.checkBox.isChecked()) {
                                Bimp.tempSelectBitmap.add(menuItem);
                            } else {
                                Bimp.tempSelectBitmap.remove(menuItem);
                            }
                            PrivatePhotoFragment.showDec();
                        }
                    });
                    if (Bimp.tempSelectBitmap.contains(menuItem)) {
                        menuItemHolder.checkBox.setChecked(true);


                    } else {
                        menuItemHolder.checkBox.setChecked(false);

                    }

                    menuItemHolder.imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int item = position;
                            if (position > 219) {
                                item = position - 4;
                            } else if (position > 146) {
                                item = position - 3;
                            } else if (position > 73) {
                                item = position - 2;
                            } else if (position > 6) {
                                item = position - 1;
                            }
                            BseApplication.editor.putBoolean("privAlbumToGallery", true).commit();
                            mContext.startActivity(new Intent(mContext, Gallery.class).putExtra("position", item).putExtra("isFromPrivateAlbum", true));

                        }
                    });
                } catch (Exception e) {
                }
        }
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

    public class MenuItemViewHolder extends RecyclerView.ViewHolder {
        public RoundedImageView imageView;
        public CheckBox checkBox;

        MenuItemViewHolder(View view) {
            super(view);
            checkBox = view.findViewById(R.id.checkBox);
            imageView = view.findViewById(R.id.image_view);
        }
    }
}
