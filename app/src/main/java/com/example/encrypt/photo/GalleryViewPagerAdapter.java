package com.example.encrypt.photo;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.encrypt.vault.PrivatePhotoFragment;

/**
 * 自定义的PagerAdapter：用于GalleryActivity界面的ViewPagerFixed的适配
 * 功能：已选图片在画廊进行预览
 *
 * @author Tom
 */
public class GalleryViewPagerAdapter extends PagerAdapter {

    private Context mContext;

    //构造
    public GalleryViewPagerAdapter(Context context) {
        super();
        mContext = context;
    }

    public int getCount() {
        if (Gallery.isFromPrivateAlbum) {
            return PrivatePhotoFragment.dateList.size();
        } else {
            return Album.dataList.size();
        }
    }

    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView photoView = new PhotoView(mContext);
        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                Gallery.switchButtonVisibility();
            }
        });
        if (Gallery.isFromPrivateAlbum) {
            String privImagePath = PrivatePhotoFragment.dateList.get(position).getImagePath();
            if (privImagePath.endsWith(".gif")) {
                Glide.with(mContext).load(privImagePath).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(photoView);
            } else {

                Glide.with(mContext).load(privImagePath).into(photoView);
            }
        } else {
            String imagePath = Album.dataList.get(position).getImagePath();
            if (Album.dataList.get(position).getImagePath().endsWith(".gif")) {
                Glide.with(mContext).load(imagePath).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(photoView);
            } else {
                Glide.with(mContext).load(imagePath).into(photoView);
            }
        }
        photoView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        container.addView(photoView);
        return photoView;
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
    }



    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


}
