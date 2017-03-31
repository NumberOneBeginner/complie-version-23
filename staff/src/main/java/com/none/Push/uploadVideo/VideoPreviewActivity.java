package com.none.Push.uploadVideo;

import java.io.Serializable;
import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.none.Push.uploadImage.PhotoPickerActivity;
import com.none.Push.uploadImage.domains.utils.ViewPagerFixed;
import com.none.Push.uploadVideo.domains.VideoInfo;
import com.none.staff.R;
import com.none.staff.activity.BaseActivity;


public class VideoPreviewActivity extends BaseActivity implements VideoPagerAdapter.PhotoViewClickListener{


	public static final String EXTRA_PHOTOS = "extra_photos";
	public static final String EXTRA_PHOTOS_ACCOUNT = "extra_photos_account";
	public static final String EXTRA_CURRENT_ITEM = "extra_current_item";

	/** 选择结果，返回为 ArrayList&lt;String&gt; 图片路径集合  */
	public static final String EXTRA_RESULT = "preview_result";

	/** 预览请求状态码 */
	public static final int REQUEST_PREVIEW = 99;

	private ArrayList<VideoInfo> paths;
	private ViewPagerFixed mViewPager;
	private VideoPagerAdapter mPagerAdapter;
	private int currentItem = 0;
	private ImageView checkmark_item;
	private ImageView select_back_video;
	private Button nextbutton_selectvideo;
	private ArrayList<VideoInfo> videoCount_Selected;
	private String userId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		IntentFilter filter = new IntentFilter("data.broadcast.action");
		registerReceiver(broadcastReceiver, filter);
//		if (Build.VERSION.SDK_INT >= 21) {
//			full(false);
//			 this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		}else{
//			//除去状态栏
//	        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//	        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		}
		setContentView(R.layout.activity_video_preview);

		initViews();
		setListener();
	}
	private void setListener() {

		select_back_video.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				VideoPreviewActivity.this.finish();
			}
		});
		nextbutton_selectvideo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent data = new Intent(VideoPreviewActivity.this,UploadVideoActivity.class);
				data.putExtra(PhotoPickerActivity.EXTRA_RESULT, (Serializable) videoCount_Selected);
				data.putExtra(PhotoPickerActivity.EXTRA_USER_ID,userId );
				VideoPreviewActivity.this.startActivity(data);
				finish();
			}
		});


	}
	private void initViews(){
		mViewPager = (ViewPagerFixed) findViewById(R.id.vp_photos);
		paths = new ArrayList<VideoInfo>();
		videoCount_Selected= (ArrayList<VideoInfo>) getIntent().getSerializableExtra(EXTRA_PHOTOS_ACCOUNT);
		nextbutton_selectvideo=(Button)findViewById(R.id.nextbutton_selectvideo);
		if(videoCount_Selected.size()!=0){
			nextbutton_selectvideo.setEnabled(true);
		}else{
			nextbutton_selectvideo.setEnabled(false);
		}
		userId=getIntent().getStringExtra("userId");
		VideoInfo video_pathArr =(VideoInfo) getIntent().getSerializableExtra("33333");
		if(video_pathArr.thumbImage != null){
			paths.add(video_pathArr);
		}
		currentItem = getIntent().getIntExtra(EXTRA_CURRENT_ITEM, 0);

		mPagerAdapter = new VideoPagerAdapter(this, paths,videoCount_Selected);
		mViewPager.setAdapter(mPagerAdapter);

		select_back_video=(ImageView)findViewById(R.id.select_back_video);


	}


	@Override
	public void OnPhotoTapListener(View view, float v, float v1) {
		onBackPressed();
	}


	@Override
	public void onBackPressed() {

		Intent intent = new Intent();
		intent.putExtra(EXTRA_RESULT, paths);
		setResult(RESULT_OK, intent);
		finish();
		super.onBackPressed();
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if(item.getItemId() == android.R.id.home){
			onBackPressed();
			return true;
		}
		//
		return super.onOptionsItemSelected(item);
	}


	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			ArrayList<VideoInfo> video_list = (ArrayList<VideoInfo>) intent.getSerializableExtra("qqqq");
			if(video_list.size()!=0){
				nextbutton_selectvideo.setEnabled(true);
			}else{
				nextbutton_selectvideo.setEnabled(false);

			}


		}
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(broadcastReceiver);

	}


	/**
	 * statusbar
	 * @param enable
	 *            false 显示，true 隐藏
	 */
	private void full(boolean enable) {
		// TODO Auto-generated method stub
		WindowManager.LayoutParams p = this.getWindow().getAttributes();
		if (enable) {

			p.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;// |=：或等于，取其一

		} else {
			p.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);// &=：与等于，取其二同时满足，
																		// ~ ：
																		// 取反

		}
		getWindow().setAttributes(p);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
	}
	

}