package com.none.Push.uploadImage;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.none.Push.uploadImage.domains.Image;
import com.none.staff.R;


public class PhotoPagerAdapter extends PagerAdapter {


	private ImageView checkmark_item;
	private TextView checkmark_tv;

	public interface PhotoViewClickListener{
		void OnPhotoTapListener(View view, float v, float v1);
	}

	public interface setbackViewListenr{
		void OnbackViewListenr(OnClickListener listner);
	}

	public PhotoViewClickListener listener;
	private List<Image> paths = new ArrayList<Image>();
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private List<Image>mSelectedImages_item=new ArrayList<Image>();
	private String User_Id;


	public PhotoPagerAdapter(Context mContext, List<Image> paths,List<Image> imageCount_Selected,String User_id) {

		this.mContext = mContext;
		this.paths = paths;
		this.mSelectedImages_item=imageCount_Selected;
		mLayoutInflater = LayoutInflater.from(mContext);
		this.User_Id=User_id;
	}


	public void setPhotoViewClickListener(PhotoViewClickListener listener){
		this.listener = listener;
	}

	@Override public Object instantiateItem(ViewGroup container, final int position) {

		View itemView = mLayoutInflater.inflate(R.layout.item_preview, container, false);

		PhotoView imageView = (PhotoView) itemView.findViewById(R.id.iv_pager);

		checkmark_item=(ImageView)itemView.findViewById(R.id.checkmark_item);
		checkmark_tv=(TextView)itemView.findViewById(R.id.checkmark_tv);




		//图片的路径
		selectCheck(paths.get(position));
		checkmark_tv.setText(mSelectedImages_item.size() +"/9"+" Selected");

		//在此处发送个广播，更新PhotoPicker界面

		final String path = paths.get(position).path;
		final Uri uri;
		if (path.startsWith("http")) {
			uri = Uri.parse(path);
		} else {
			uri = Uri.fromFile(new File(path));
		}

		Glide.with(mContext)
		.load(uri)
		.error(R.drawable.default_error)
		.crossFade()
		.into(imageView);

		imageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
			@Override
			public void onPhotoTap(View view, float v, float v1) {
				if (listener != null) {
					listener.OnPhotoTapListener(view, v, v1);
				}
			}
		});

		container.addView(itemView);


		checkmark_item.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				//在此处发送个广播，更新PhotoPicker界面
				if(mSelectedImages_item.size()>8){

					if(mSelectedImages_item.contains(paths.get(position))){
						selectCheckbox(paths.get(position));
						sendBrodcast();
						return;

					}else{
						Toast.makeText(mContext, "最多只能选9张", 1).show();
						return;
					}
				}
				selectCheckbox(paths.get(position));


				sendBrodcast();
			}

			private void sendBrodcast() {

				Intent intent = new Intent("data.broadcast.action");
				intent.putExtra("qqqq", (Serializable) mSelectedImages_item);
				intent.putExtra("bbbbb", position);
				mContext.sendBroadcast(intent);
				checkmark_tv.setText(mSelectedImages_item.size() +"/9"+" Selected");
			}

			private void selectCheckbox(Image path) {
				if (mSelectedImages_item.contains(path)) {
					mSelectedImages_item.remove(path);
					checkmark_item.setImageResource(R.drawable.btn_unselected);
				} else {
					mSelectedImages_item.add(path);
					checkmark_item.setImageResource(R.drawable.btn_selected);
				}
			}
		});





		return itemView;
	}

	private void selectCheck(Image image) {

		if(mSelectedImages_item.contains(image)) {
			checkmark_item.setImageResource(R.drawable.btn_selected);

		}
	}


	@Override public int getCount() {
		return paths.size();
	}


	@Override public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}


	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public int getItemPosition (Object object) { return POSITION_NONE; }

}