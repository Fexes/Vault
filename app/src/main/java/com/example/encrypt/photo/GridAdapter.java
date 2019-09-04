package com.example.encrypt.photo;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.encrypt.R;

/**
 * 适配器：将选中的8张图片以GridView的形式进行适配显示
 * @author Tom
 *
 */
public class GridAdapter extends BaseAdapter {
	private Context context;
	private boolean shape;
	private int selectedPosition = -1;

	// 带参构造
	public GridAdapter(Context context) {
		super();
		this.context = context;
	}

	// 自己添加的 get()、set()方法
	public boolean isShape() {
		return shape;
	}

	public void setShape(boolean shape) {
		this.shape = shape;
	}

	public void setSelectedPosition(int position) {
		selectedPosition = position;
	}

	public int getSelectedPosition() {
		return selectedPosition;
	}

	// 继承BaseAdapter必须重写的四个方法
	public int getCount() {
		if (Bimp.tempSelectBitmap.size() == 8) {
			return 8;
		}
		return (Bimp.tempSelectBitmap.size() + 1);
	}

	public Object getItem(int arg0) {
		return null;
	}

	public long getItemId(int arg0) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.item_published_gridview, parent, false);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView.findViewById(R.id.item_grida_image);
			convertView.setTag(holder);
			//集成 AutoLayout:对于ListView的item的适配，注意添加这一行，即可在item的模版布局上使用px高度
//	        AutoUtils.autoSize(convertView);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (position == Bimp.tempSelectBitmap.size()) {
			holder.image.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.take_photo));
			if (position == 8) {
				holder.image.setVisibility(View.GONE);
			}
		} else {
			holder.image.setImageBitmap(Bimp.tempSelectBitmap.get(position).getBitmap());
		}

		return convertView;
	}

	// viewHolder用于存放 gridView模板布局中的控件
	private class ViewHolder {
		public ImageView image;
	}

	
	/**
	 * 接收upDate()方法中传递过来的消息，然后刷新适配器
	 */
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				notifyDataSetChanged();
				break;
			}
			super.handleMessage(msg);
		}
	};
	/**
	 * 其他的类 可以通过调用此方法刷新适配器
	 */
	public void upDate() {
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					if (Bimp.max == Bimp.tempSelectBitmap.size()) { //图片达到8张，就跳出这个死循环
						Message message = new Message();
						message.what = 1;
						handler.sendMessage(message);
						break;
					} else { //图片未达到8张就不停的刷新适配器
						Bimp.max += 1;
						Message message = new Message();
						message.what = 1;
						handler.sendMessage(message);//通知主线程刷新适配器
					}
				}
			}
		}).start();
	}
	
	
	
}