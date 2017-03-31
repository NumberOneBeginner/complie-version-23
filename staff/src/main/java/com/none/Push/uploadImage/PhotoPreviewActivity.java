package com.none.Push.uploadImage;

import java.io.Serializable;
import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.none.Push.uploadImage.domains.Image;
import com.none.Push.uploadImage.domains.utils.ViewPagerFixed;
import com.none.staff.R;
import com.none.staff.activity.BaseActivity;


public class PhotoPreviewActivity extends BaseActivity implements PhotoPagerAdapter.PhotoViewClickListener{


	public static final String EXTRA_PHOTOS = "extra_photos";
	public static final String EXTRA_PHOTOS_ACCOUNT = "extra_photos_account";
	public static final String EXTRA_CURRENT_ITEM = "extra_current_item";

	/** 选择结果，返回为 ArrayList&lt;String&gt; 图片路径集合  */
	public static final String EXTRA_RESULT = "preview_result";

	/** 预览请求状态码 */
	public static final int REQUEST_PREVIEW = 99;

	private ArrayList<Image> paths;
	private ViewPagerFixed mViewPager;
	private PhotoPagerAdapter mPagerAdapter;
	private int currentItem = 0;
	private ImageView checkmark_item;
	private String userId;
	private ImageView select_back;
	private Button nextbutton_select;
	private ArrayList<Image> imageCount_Selected;

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
		setContentView(R.layout.activity_image_preview);
		initViews();
		setListener();
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
	
	private void setListener() {

		nextbutton_select.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent data = new Intent(PhotoPreviewActivity.this,UploadActivity.class);
				data.putExtra(PhotoPickerActivity.EXTRA_RESULT, (Serializable) imageCount_Selected);
				data.putExtra(PhotoPickerActivity.EXTRA_USER_ID, userId);
				PhotoPreviewActivity.this.startActivity(data);
				finish();
			}
		});

		select_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

	private void initViews(){
		nextbutton_select=(Button)findViewById(R.id.nextbutton_select);
		select_back=(ImageView)findViewById(R.id.select_back);
		mViewPager = (ViewPagerFixed) findViewById(R.id.vp_photos);
		paths = new ArrayList<Image>();
		imageCount_Selected= (ArrayList<Image>) getIntent().getSerializableExtra(EXTRA_PHOTOS_ACCOUNT);

		if(imageCount_Selected.size()!=0){
			nextbutton_select.setEnabled(true);
		}else{
			nextbutton_select.setEnabled(false);

		}


		Intent intent = this.getIntent();
		Image Image_pathArr = (Image) intent.getSerializableExtra(EXTRA_PHOTOS);
		userId=intent.getStringExtra("userId");

		if(Image_pathArr.path != null){
			paths.add(Image_pathArr);
		}
		currentItem = getIntent().getIntExtra(EXTRA_CURRENT_ITEM, 0);
		mPagerAdapter = new PhotoPagerAdapter(this, paths,imageCount_Selected,userId);
		mViewPager.setAdapter(mPagerAdapter);
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






	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			ArrayList<Image> Images_list = (ArrayList<Image>) intent.getSerializableExtra("qqqq");

			if(Images_list.size()!=0){
				nextbutton_select.setEnabled(true);
			}else{
				nextbutton_select.setEnabled(false);

			}
		}
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(broadcastReceiver);

	}





}
