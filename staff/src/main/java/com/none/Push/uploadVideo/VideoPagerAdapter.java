package com.none.Push.uploadVideo;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.none.Push.uploadVideo.domains.VideoInfo;
import com.none.staff.R;

public class VideoPagerAdapter extends PagerAdapter {
	public PhotoViewClickListener listener;
	private LayoutInflater mLayoutInflater;
	private ImageView checkmark_item;
	private TextView checkmark_tv;
	private Context mcontext;
	private List<VideoInfo>mSelectedVideos_item=new ArrayList<VideoInfo>();
	private List<VideoInfo> paths = new ArrayList<VideoInfo>();
	public VideoPagerAdapter(Context context,List<VideoInfo> paths,List<VideoInfo> videoCount_Selected) {
		mcontext=context;
		this.paths = paths;
		this.mSelectedVideos_item=videoCount_Selected;
		mLayoutInflater=LayoutInflater.from(context);
	}

	public void setPhotoViewClickListener(PhotoViewClickListener listener){
		this.listener = listener;
	}


	public interface PhotoViewClickListener{
		void OnPhotoTapListener(View view, float v, float v1);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return paths.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {

		return view == object;
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {

		View itemView = mLayoutInflater.inflate(R.layout.item_preview, null);
		PhotoView imageView = (PhotoView) itemView.findViewById(R.id.iv_pager);
		checkmark_item=(ImageView)itemView.findViewById(R.id.checkmark_item);
		checkmark_tv=(TextView)itemView.findViewById(R.id.checkmark_tv);

		selectCheck(paths.get(position));
		checkmark_tv.setText(mSelectedVideos_item.size() +"/9"+" Selected");

		final String path = paths.get(position).thumbImage;

		final Uri uri;
		if (path.startsWith("http")) {
			uri = Uri.parse(path);
		} else {
			uri = Uri.fromFile(new File(path));
		}

		Glide.with(mcontext)
		.load(uri)
		//            .placeholder(R.mipmap.default_error)
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

				if(mSelectedVideos_item.size()>8){
					if(mSelectedVideos_item.contains(paths.get(position))){
						selectCheckbox(paths.get(position));
						sendBrodcast();
						return;

					}else{
						Toast.makeText(mcontext, "最多只能选9张", 1).show();
						return;
					}
				}
				selectCheckbox(paths.get(position));

				sendBrodcast();
			}

			private void sendBrodcast() {
				Intent intent = new Intent("data.broadcast.action");
				intent.putExtra("qqqq", (Serializable) mSelectedVideos_item);
				mcontext.sendBroadcast(intent);
				checkmark_tv.setText(mSelectedVideos_item.size() +"/9"+" Selected");
			}

			private void selectCheckbox(VideoInfo path) {
				if (mSelectedVideos_item.contains(path)) {
					mSelectedVideos_item.remove(path);
					checkmark_item.setImageResource(R.drawable.btn_unselected);
				} else {
					mSelectedVideos_item.add(path);
					checkmark_item.setImageResource(R.drawable.btn_selected);
				}
			}
		});

		return itemView;

	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {

		container.removeView((View) object);
	}



	@Override
	public int getItemPosition(Object object) {

		return POSITION_NONE; 
	}




	private void selectCheck(VideoInfo video) {

		if(mSelectedVideos_item.contains(video)) {

			Log.i("post" , "if   selectCheck");
			checkmark_item.setImageResource(R.drawable.btn_selected);
		}
		Log.i("post" , " result  selectCheck");
	}

}
