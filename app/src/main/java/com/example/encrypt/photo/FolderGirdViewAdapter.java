package com.example.encrypt.photo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.encrypt.R;
import com.example.encrypt.album.Album;

import java.util.ArrayList;


/**
 * 适配器：将所有包含图片的文件夹  以GridView的形式展示
 *
 * @author Tom
 */
public class FolderGirdViewAdapter extends BaseAdapter {

	private Context mContext;
	private Intent mIntent;
	private DisplayMetrics dm;
	BitmapCache cache;
	final String TAG = getClass().getSimpleName();
	public FolderGirdViewAdapter(Context c) {
		cache = new BitmapCache();
		init(c);
	}

	// 初始化
	public void init(Context c) {
		mContext = c;
		mIntent = ((Activity) mContext).getIntent();
		dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
	}

	

	@Override
	public int getCount() {
		return Folders.contentList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	BitmapCache.ImageCallback callback = new BitmapCache.ImageCallback() {
		@Override
		public void imageLoad(ImageView imageView, Bitmap bitmap,
							  Object... params) {
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
		// 
//		public ImageView backImage;
		// 封面
		public ImageView imageView;
		public LinearLayout choose_back;
		// 文件夹名称
		public TextView folderName;
		// 文件夹里面的图片数量
		public TextView fileNum;
	}
	ViewHolder holder = null;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_folders_gridview, parent,false);
			holder = new ViewHolder();
//			holder.backImage = (ImageView) convertView.findViewById(R.id.file_back);
			holder.imageView = convertView.findViewById(R.id.file_image);
			holder.choose_back =  convertView.findViewById(R.id.album_lay);
			holder.folderName = convertView.findViewById(R.id.name);
			holder.fileNum = convertView.findViewById(R.id.filenum);
  			convertView.setTag(holder);
			//AutoLayout:对于ListView的item的适配，注意添加这一行，即可在item上使用px高度
//	        AutoUtils.autoSize(convertView);
		} else
			holder = (ViewHolder) convertView.getTag();
		String path;
		if (Folders.contentList.get(position).imageList != null) {

			path = Folders.contentList.get(position).imageList.get(0).imagePath;

			holder.folderName.setText(Folders.contentList.get(position).bucketName);
			if(Folders.contentList.get(position).count==1){
				holder.fileNum.setText( Folders.contentList.get(position).count +"  Image");
			}else {
				holder.fileNum.setText("" + Folders.contentList.get(position).count+"  Images");
			}

			
		} else
			path = "android_hybrid_camera_default";
		if (path.contains("android_hybrid_camera_default"))
			holder.imageView.setImageResource(R.drawable.plugin_camera_no_pictures);
		else {
//			holder.imageView.setImageBitmap( Folders.contentList.get(position).imageList.get(0).getBitmap());
			final ImageItem item = Folders.contentList.get(position).imageList.get(0);
			//holder.imageView.setTag(item.imagePath);
			//cache.displayBmp(holder.imageView, item.imagePath, item.imagePath,callback);
			//cache.displayBmp(holder.imageView, item.imagePath, item.imagePath,callback);
			try {
				Glide.with(mContext).load(item.imagePath).into(holder.imageView);
			}catch (Exception e){
				//Toast.makeText(mContext,e.toString(),Toast.LENGTH_LONG).show();
			}
		}
		// 为封面添加监听
		holder.imageView.setOnClickListener(new ImageViewClickListener(
				position, mIntent,holder.choose_back));
		
		return convertView;
	}

	// 为每一个文件夹（相册）构建 点击监听器
	private class ImageViewClickListener implements OnClickListener {
		private int position;
		private LinearLayout choose_back;
		public ImageViewClickListener(int position, Intent intent, LinearLayout choose_back) {
			this.position = position;
			this.choose_back = choose_back;
		}
		public void onClick(View v) {
			//给Album中的dataList赋值
			Album.dataList = (ArrayList<ImageItem>) Folders.contentList.get(position).imageList;
			Intent intent = new Intent();
			String folderName = Folders.contentList.get(position).bucketName; //点击的相册的 名字
			intent.putExtra("folderName", folderName);
			intent.setClass(mContext, Album.class);
			mContext.startActivity(intent);
			choose_back.setVisibility(View.VISIBLE);
			//((Activity)mContext).finish(); //跳转后，关闭Folders界面
		}
	}

	public int dipToPx(int dip) {
		return (int) (dip * dm.density + 0.5f);
	}

}
