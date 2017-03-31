package com.none.Push.uploadVideo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.none.Push.uploadVideo.domains.FolderVideo;
import com.none.staff.R;

public class FolderVideoAdapter  extends BaseAdapter{

	private Context mContext;
	private LayoutInflater mInflater;

	private List<FolderVideo> mFolders = new ArrayList<FolderVideo>();

	int mImageSize;

	int lastSelected = 0;

	public FolderVideoAdapter(Context context){
		mContext = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mImageSize = mContext.getResources().getDimensionPixelOffset(R.dimen.folder_cover_size);
	}


	/**
	 * 设置数据集
	 * @param folders
	 */
	public void setData(List<FolderVideo> folders) {
		if(folders != null && folders.size()>0){
			mFolders = folders;
		}else{
			mFolders.clear();
		}
		notifyDataSetChanged();
	}


	private int getTotalImageSize(){
		int result = 0;
		if(mFolders != null && mFolders.size()>0){
			for (FolderVideo f: mFolders){
				result += f.videos.size();
			}
		}
		return result;
	}

	@Override
	public int getCount() {

		return mFolders.size()+1;
	}

	@Override
	public FolderVideo getItem(int i) {
		if(i == 0) return null;
		return mFolders.get(i-1);
	}

	@Override
	public long getItemId(int i) {
		// TODO Auto-generated method stub
		return i;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		ViewHolder holder;
		if(view == null){
			view = mInflater.inflate(R.layout.item_folder, viewGroup, false);
			holder = new ViewHolder(view);
		}else{
			holder = (ViewHolder) view.getTag();
		}
		if (holder != null) {
			if(i == 0){
				holder.name.setText(mContext.getResources().getString(R.string.all_image));
				holder.size.setText(getTotalImageSize() + "张");
				if(mFolders.size()>0){
					FolderVideo f = mFolders.get(0);

					Glide.with(mContext)
					.load(new File(f.videoInfo.thumbImage))
					.error(R.drawable.default_error)
					.override(mImageSize, mImageSize)
					.centerCrop()
					.into(holder.cover);
				}
			}else {
				holder.bindData(getItem(i));
			}
			if(lastSelected == i){
				holder.indicator.setVisibility(View.VISIBLE);
			}else{
				holder.indicator.setVisibility(View.INVISIBLE);
			}
		}
		return view;
	}

	class ViewHolder{
		ImageView cover;
		TextView name;
		TextView size;
		ImageView indicator;
		ViewHolder(View view){
			cover = (ImageView)view.findViewById(R.id.cover);
			name = (TextView) view.findViewById(R.id.name);
			size = (TextView) view.findViewById(R.id.size);
			indicator = (ImageView) view.findViewById(R.id.indicator);
			view.setTag(this);
		}

		void bindData(FolderVideo data) {
			name.setText(data.name);
			size.setText(data.videos.size() + "张");
			// 显示图片
			Glide.with(mContext)
			.load(new File(data.videoInfo.thumbImage))
			.placeholder(R.drawable.default_error)
			.error(R.drawable.default_error)
			.override(mImageSize, mImageSize)
			.centerCrop()
			.into(cover);
		}
	}
}
