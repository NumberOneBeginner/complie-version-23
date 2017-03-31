package com.none.Push.uploadVideo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.none.Push.uploadVideo.domains.VideoInfo;
import com.none.Push.uploadVideo.domains.utils.SelectedVideos;
import com.none.staff.R;

import de.greenrobot.event.EventBus;

public class VideoGridAdapter extends BaseAdapter {


	private static final int TYPE_CAMERA = 0;
	private static final int TYPE_NORMAL = 1;

	private Context mContext;
	private LayoutInflater mInflater;
	private boolean showCamera = true;
	private boolean showSelectIndicator = true;
	private List<VideoInfo> mvideos = new ArrayList<VideoInfo>();
	private List<VideoInfo> mSelectedVideos = new ArrayList<VideoInfo>();
	private List<VideoInfo> isChecked = new ArrayList<VideoInfo>();
	private int mItemSize;
	private GridView.LayoutParams mItemLayoutParams;


	public VideoGridAdapter(Context context, boolean showCamera, int itemSize){
		mContext = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.showCamera = showCamera;
		this.mItemSize = itemSize;
		mItemLayoutParams = new GridView.LayoutParams(mItemSize, mItemSize);
	}

	/**
	 * 显示
	 */
	public void showSelectIndicator(boolean b) {
		showSelectIndicator = b;
	}


	public void setDataVideos(List<VideoInfo> video) {
		mSelectedVideos.clear();
		Log.i("ppppp","wai  wai wai setDataVideos setDataVideos setDataVideos"+video.size());
		if(video != null && video.size() > 0){
			Log.i("ppppp","setDataVideos setDataVideos setDataVideos"+video.size());
			mSelectedVideos=video;
		}
		EventBus.getDefault().post(new SelectedVideos(mSelectedVideos));//这个地方是删除还是选择
		notifyDataSetChanged();
	}

	/**
	 * 通过图片路径设置默认选择
	 * @param resultList
	 */
	public void setDefaultSelected(ArrayList<String> resultList) {
		mSelectedVideos.clear();
		for(String path : resultList){
			VideoInfo videoInfo = getImageByPath(path);
			if(videoInfo != null){
				mSelectedVideos.add(videoInfo);
			}
		}
		notifyDataSetChanged();
	}


	private VideoInfo getImageByPath(String path){
		if(mvideos != null && mvideos.size() > 0){
			for(VideoInfo video : mvideos){
				if(video.thumbImage.equalsIgnoreCase(path)){
					return video;
				}
			}
		}
		return null;
	}


	/**
	 * 重置每个Column的Size
	 * @param columnWidth
	 */
	public void setItemSize(int columnWidth) {

		if(mItemSize == columnWidth){
			return;
		}

		mItemSize = columnWidth;

		mItemLayoutParams = new GridView.LayoutParams(mItemSize, mItemSize);

		notifyDataSetChanged();
	}


	/**
	 * 设置数据集
	 * @param videos
	 */
	public void setData(List<VideoInfo> videos) {
		if(videos != null && videos.size() > 0){
			mvideos = videos;
		}else{
			mvideos.clear();
		}
		notifyDataSetChanged();
	}



	@Override
	public int getCount() {
		return showCamera ? mvideos.size() + 1 : mvideos.size();
	}

	@Override
	public VideoInfo getItem(int i) {
		if(showCamera){
			if(i == 0)
				return null;
			return mvideos.get(i - 1);
		}else
			return mvideos.get(i);
	}

	@Override
	public long getItemId(int i) {

		return i;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		int type = getItemViewType(i);


		if(type == TYPE_NORMAL){
			ViewHolde holde;
			if(view == null){
				view = mInflater.inflate(R.layout.item_select_image, viewGroup, false);
				holde = new ViewHolde(view);//一个View，则是一个holder
			}else{
				holde = (ViewHolde) view.getTag();
				if(holde == null){
					view = mInflater.inflate(R.layout.item_select_image, viewGroup, false);
					holde = new ViewHolde(view);
				}
			}
			holde.indicator = (ImageView) view.findViewById(R.id.checkmark);
			final VideoInfo video_item = mvideos.get(i);

			holde.indicator.setOnClickListener(new View.OnClickListener() {
				@Override
				//点击的试试在此处判断返回的集合里边包含这个image图片的路径吗？如果包括mSelectedImages.add(image);如果不包括就移除
				public void onClick(View v) {

					selectCheckbox(video_item);
				}

				private void selectCheckbox(VideoInfo video) {

					if(mSelectedVideos.size()>8){

						if(isChecked.contains(video)){
							mSelectedVideos.remove(video);
							isChecked.remove(video);
							EventBus.getDefault().post(new SelectedVideos(mSelectedVideos));
							notifyDataSetChanged();
							return;

						}else{
							Toast.makeText(mContext, "最多只能选9张", 1).show();
							return;
						}
					}

					if(mSelectedVideos.contains(video)) {
						mSelectedVideos.remove(video);
					} else {
						mSelectedVideos.add(video);
						isChecked.add(video);
					}
					EventBus.getDefault().post(new SelectedVideos(mSelectedVideos));
					notifyDataSetChanged();
				}
			});

			if(holde != null) {
				holde.bindData(getItem(i));
			}
		}

		GridView.LayoutParams lp = (GridView.LayoutParams) view.getLayoutParams();
		if(lp.height != mItemSize)
			view.setLayoutParams(mItemLayoutParams);
		return view;
	}

	@Override
	public int getItemViewType(int position) {
		if(showCamera)
			return position == 0 ? TYPE_CAMERA : TYPE_NORMAL;
		return TYPE_NORMAL;
	}
	class ViewHolde {
		ImageView image;
		ImageView indicator;
		View mask;

		ViewHolde(View view){
			image = (ImageView) view.findViewById(R.id.image);
			indicator = (ImageView) view.findViewById(R.id.checkmark);
			mask = view.findViewById(R.id.mask);
			view.setTag(this);//将View与viewholder绑定
		}
		void bindData(final VideoInfo data){

			Log.i("zzz"," zzz zzz zzz zzz zzz zzz  bind");

			if(data == null) return;
			// 处理单选和多选状态
			if(showSelectIndicator){
				indicator.setVisibility(View.VISIBLE);

				if(mSelectedVideos.contains(data)){
					// 设置选中状态
					indicator.setImageResource(R.drawable.btn_selected);
					mask.setVisibility(View.VISIBLE);
				}else{
					// 未选择
					indicator.setImageResource(R.drawable.btn_unselected);
					mask.setVisibility(View.GONE);
				}
			}else{
				indicator.setVisibility(View.GONE);
			}
			File imageFile = new File(data.thumbImage);

			if(mItemSize > 0) {
				// 显示图片
				Glide.with(mContext)
				.load(imageFile)
				.placeholder(R.drawable.default_error)
				.error(R.drawable.default_error)
				.override(mItemSize, mItemSize)
				.centerCrop()
				.into(image);
			}
		}
	}


}
