package com.none.Push.uploadImage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.none.Push.uploadImage.domains.Image;
import com.none.Push.uploadImage.domains.utils.SelectedImages;
import com.none.staff.R;

import de.greenrobot.event.EventBus;
public class ImageGridAdapter extends BaseAdapter {

	private static final int TYPE_CAMERA = 0;
	private static final int TYPE_NORMAL = 1;

	private Context mContext;

	private LayoutInflater mInflater;
	private boolean showCamera = true;
	private boolean showSelectIndicator = true;

	private List<Image> mImages = new ArrayList<Image>();
	private List<Image> mSelectedImages = new ArrayList<Image>();
	private int mItemSize;
	private GridView.LayoutParams mItemLayoutParams;
	ArrayList<ImageView> listttt = new ArrayList<ImageView>();

	ArrayList<Image> isChecked = new ArrayList<Image>();


	public ImageGridAdapter(Context context, boolean showCamera, int itemSize){
		mContext = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.showCamera = showCamera;
		this.mItemSize = itemSize;
		mItemLayoutParams = new GridView.LayoutParams(mItemSize, mItemSize);
	}

	/**
	 * 显示选择指示器
	 * @param b
	 */
	public void showSelectIndicator(boolean b) {
		showSelectIndicator = b;
	}

	public void setShowCamera(boolean b){
		if(showCamera == b) return;

		showCamera = b;
		notifyDataSetChanged();
	}

	public boolean isShowCamera(){
		return showCamera;
	}

	/**
	 * 选择某个图片，改变选择状态
	 * @param image
	 */
	public void select(Image image) {
		if(mSelectedImages.contains(image)){
			mSelectedImages.remove(image);
		}else{
			mSelectedImages.add(image);
		}
		notifyDataSetChanged();
	}

	/**
	 * 通过图片路径设置默认选择
	 * @param resultList
	 */
	public void setDefaultSelected(ArrayList<String> resultList) {
		mSelectedImages.clear();
		for(String path : resultList){
			Image image = getImageByPath(path);
			if(image != null){
				mSelectedImages.add(image);
			}
		}
		notifyDataSetChanged();
	}

	private Image getImageByPath(String path){
		if(mImages != null && mImages.size() > 0){
			for(Image image : mImages){
				if(image.path.equalsIgnoreCase(path)){
					return image;
				}
			}
		}
		return null;
	}

	/**
	 * 设置数据集
	 * @param images
	 */
	public void setData(List<Image> images) {
		//        mSelectedImages.clear();
		if(images != null && images.size() > 0){
			mImages = images;
		}else{
			mImages.clear();
		}
		notifyDataSetChanged();
	}

	public void setDataImage(List<Image> images) {
		mSelectedImages.clear();
		if(images != null && images.size() > 0){
			mSelectedImages=images;
		}
		EventBus.getDefault().post(new SelectedImages(mSelectedImages));
		notifyDataSetChanged();
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

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		if(showCamera){
			return position == 0 ? TYPE_CAMERA : TYPE_NORMAL;
		}
		return TYPE_NORMAL;
	}

	@Override
	public int getCount() {
		return showCamera ? mImages.size() + 1 : mImages.size();
	}

	@Override
	public Image getItem(int i) {
		if(showCamera){
			if(i == 0){
				return null;
			}
			return mImages.get(i - 1);
		}else{
			return mImages.get(i);
		}
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {

		int type = getItemViewType(i);
		if(type == TYPE_CAMERA){
			view = mInflater.inflate(R.layout.item_select_image, viewGroup, false);
			view.setTag(null);
		}else if(type == TYPE_NORMAL){
			ViewHolde holde;
			if(view == null){
				view = mInflater.inflate(R.layout.item_select_image, viewGroup, false);
				holde = new ViewHolde(view);

			}else{
				holde = (ViewHolde) view.getTag();
				if(holde == null){
					view = mInflater.inflate(R.layout.item_select_image, viewGroup, false);
					holde = new ViewHolde(view);
				}
			}
			holde.indicator = (ImageView) view.findViewById(R.id.checkmark);
			final Image image_item = mImages.get(i);
			holde.indicator.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//点击的试试在此处判断返回的集合里边包含这个image图片的路径吗？如果包括mSelectedImages.add(image);如果不包括就移除
					selectCheckbox(image_item);
				}
				private void selectCheckbox(Image image) {

					if(mSelectedImages.size()>8){
						if(isChecked.contains(image)){
							mSelectedImages.remove(image);
							isChecked.remove(image);
							EventBus.getDefault().post(new SelectedImages(mSelectedImages));
							notifyDataSetChanged();
							return;
						}else{
							Toast.makeText(mContext, "最多只能选9张", 1).show();
							return;
						}
					}

					if(mSelectedImages.contains(image)){
						mSelectedImages.remove(image);
					}else{

						mSelectedImages.add(image);
						isChecked.add(image);
					}
					EventBus.getDefault().post(new SelectedImages(mSelectedImages));
					notifyDataSetChanged();
				}
			});

			if(holde != null) {
				holde.bindData(getItem(i));
			}
		}
		/** Fixed View Size */
		GridView.LayoutParams lp = (GridView.LayoutParams) view.getLayoutParams();
		if(lp.height != mItemSize){
			view.setLayoutParams(mItemLayoutParams);
		}

		return view;
	}

	class ViewHolde {
		ImageView image;
		ImageView indicator;
		View mask;

		ViewHolde(View view){
			image = (ImageView) view.findViewById(R.id.image);
			indicator = (ImageView) view.findViewById(R.id.checkmark);
			mask = view.findViewById(R.id.mask);
			view.setTag(this);
		}

		void bindData(final Image data){

			if(data == null) return;
			// 处理单选和多选状态
			if(showSelectIndicator){
				indicator.setVisibility(View.VISIBLE);
				if(mSelectedImages.contains(data)){
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
			File imageFile = new File(data.path);

			if(mItemSize > 0) {
				// 显示图片
				Glide.with(mContext)
				.load(data.path)
				.placeholder(R.drawable.default_error)
				.override(mItemSize, mItemSize)
				.centerCrop()
				.into(image);
			}
		}
	}

	class ViewHolder{
		ImageView image;
		ImageView indicator;
		View mask;
	}

}
