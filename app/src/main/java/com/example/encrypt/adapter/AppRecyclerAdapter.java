package com.example.encrypt.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.encrypt.R;
import com.example.encrypt.bean.AppInfo;

import java.util.List;

public class AppRecyclerAdapter extends RecyclerView.Adapter<AppRecyclerAdapter.MyViewHolder> {

	private List<AppInfo> appList;
	private Context mContext;
	private RecycleView_OnItemClickListener itemClickListener;
	private RecycleView_OnItemLongClickListener itemLongClickListener;
	private boolean isShowDeleteImageView = false;


	public AppRecyclerAdapter(Context context, List<AppInfo> appList) {
		this.mContext = context;
		this.appList = appList;
	}



	@Override
	public int getItemCount() {
		return appList.size();
	}


	@SuppressLint("NewApi") @Override
	public void onBindViewHolder(MyViewHolder holder, final int position) {
		holder.iv.setBackground(appList.get(position).getAppIcon());
		holder.tv.setText(appList.get(position).getAppName());
		if (isShowDeleteImageView) {
			if (position == appList.size() - 1 || position == 0 || position == 1) {
				holder.iv_delete.setVisibility(View.GONE);
			} else {
				holder.iv_delete.setVisibility(View.VISIBLE);
			}
		} else {
			holder.iv_delete.setVisibility(View.GONE);
		}
	}


	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(mContext).inflate(R.layout.item_app, parent, false);
		MyViewHolder holder = new MyViewHolder(view);
		return holder;
	}


	class MyViewHolder extends ViewHolder implements View.OnClickListener, View.OnLongClickListener {
		ImageView iv, iv_delete;
		TextView tv;

		public MyViewHolder(View view) {
			super(view);
			iv = (ImageView) view.findViewById(R.id.iv_appIcon);
			tv = (TextView) view.findViewById(R.id.tv_app_name);
			iv_delete = (ImageView) view.findViewById(R.id.iv_delete);

			iv.setOnClickListener(this);
			iv.setOnLongClickListener(this);
			iv_delete.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			if (itemClickListener != null) {
				itemClickListener.onItemClick(v, getPosition());
			}
		}

		@Override
		public boolean onLongClick(View v) {
			if (itemLongClickListener != null) {
				itemLongClickListener.onItemLongClick(v, getPosition());
			}
			return true;
		}
	}

	/**
	 * 自定义的展示、隐藏右上角的“叉号图”的方法
	 */
	public void showDeletImageView(boolean isShowDeleteImageView) {
		this.isShowDeleteImageView = isShowDeleteImageView;
		notifyDataSetChanged();
	}

	/**
	 * 定义setOnItemClickListener的方法，供外部使用
	 */
	public void setOnItemClickListener(RecycleView_OnItemClickListener itemClickListener) {
		this.itemClickListener = itemClickListener;
	}

	/**
	 * 定义setOnItemLongClickListener的方法，供外部使用
	 */
	public void setOnItemLongClickListener(RecycleView_OnItemLongClickListener itemLongClickListener) {
		this.itemLongClickListener = itemLongClickListener;
	}

	/**
	 * 自定义RecycleView的OnItemClick接口
	 */
	public interface RecycleView_OnItemClickListener {
		void onItemClick(View view, int position);
	}

	/**
	 * 自定义RecycleView的OnItemLongClick接口
	 */
	public interface RecycleView_OnItemLongClickListener {
		void onItemLongClick(View view, int position);
	}

}