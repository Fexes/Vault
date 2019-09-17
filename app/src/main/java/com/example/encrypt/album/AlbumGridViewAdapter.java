package com.example.encrypt.album;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.encrypt.R;
import com.example.encrypt.gallery.Gallery;
import com.example.encrypt.photo.Bimp;
import com.example.encrypt.photo.BitmapCache;
import com.example.encrypt.photo.ImageItem;

import java.util.ArrayList;



public class AlbumGridViewAdapter extends BaseAdapter {

	private final String TAG = getClass().getSimpleName();
    private ArrayList<ImageItem> dataList;
    private Context context;
    private BitmapCache cache;
    //private DisplayMetrics dm;

    public AlbumGridViewAdapter(Context context, ArrayList<ImageItem> dataList) {
		this.context = context;
		this.dataList = dataList;
        cache = new BitmapCache();
        //dm = new DisplayMetrics();
		//((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
	}

	public void selectAll(boolean selectAll) {
		Bimp.tempSelectBitmap.clear();
		if (selectAll) {
			Bimp.tempSelectBitmap.addAll(dataList);
		}

		notifyDataSetChanged();
	}



    public void refreshDataAfterEncrypt(){
        dataList.removeAll(Bimp.tempSelectBitmap);
        notifyDataSetChanged();
        Bimp.tempSelectBitmap.clear();
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

	BitmapCache.ImageCallback callback = new BitmapCache.ImageCallback() {
        @Override
		public void imageLoad(ImageView imageView, Bitmap bitmap, Object... params) {
			if (imageView != null && bitmap != null) {
				String url = (String) params[0];
                if (url != null && url.equals(imageView.getTag())) {
                    imageView.setImageBitmap(bitmap);
				} else {
					Log.e(TAG, "callback, bmp not match");
				}
			} else {
				Log.e(TAG, "callback, bmp null");
			}
		}
	};


    private class ViewHolder {
		public ImageView imageView;
		public CheckBox checkBox;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_album_gridview, parent, false);
			viewHolder.imageView =  convertView.findViewById(R.id.image_view);
            viewHolder.checkBox = convertView.findViewById(R.id.checkBox);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String path;
		if (dataList != null && dataList.size() > position)
			path = dataList.get(position).imagePath;
		else
			path = "camera_default";
		if (path.contains("camera_default")) {
			viewHolder.imageView.setImageResource(R.color.greytext);
		} else {
			final ImageItem item = dataList.get(position);
//			viewHolder.imageView.setTag(item.imagePath);
//			cache.displayBmp(viewHolder.imageView, item.thumbnailPath, item.imagePath,callback);
			Glide.with(context).load(item.imagePath).into(viewHolder.imageView);

			viewHolder.imageView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					context.startActivity(new Intent(context, Gallery.class).putExtra("position", position).putExtra("isFromPrivateAlbum", false));
				}
			});


        }
		viewHolder.checkBox.setTag(position);
		viewHolder.checkBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (viewHolder.checkBox.isChecked()){
                    Bimp.tempSelectBitmap.add(dataList.get(position));
				}else {
                    Bimp.tempSelectBitmap.remove(dataList.get(position));
				}
				Album.showDec();
			}
		});
 		if (Bimp.tempSelectBitmap.contains(dataList.get(position))) {
			viewHolder.checkBox.setChecked(true);
		} else {
			viewHolder.checkBox.setChecked(false);
		}



		viewHolder.checkBox.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
				context.startActivity(new Intent(context,Gallery.class).putExtra("position",position).putExtra("isFromPrivateAlbum",false));

                return true;
			}
		});
		return convertView;
	}


}
