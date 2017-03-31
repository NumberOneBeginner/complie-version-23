package com.none.staff.uploadIcon;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.none.staff.R;
import com.none.staff.activity.BaseActivity;

public class IconUpLoadActivity extends BaseActivity implements
		View.OnClickListener {
//	private int width;
	private TextView tvChoose, tvSave, tvCancel;
	private String  userId, imageUrl;
	private LinearLayout btnBack;
	private ImageView imgMenu, imgShow;
	private PopupWindow popupWindow;
	private IconDownloadUtils mIconDownloadUtils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 根据API不同调用不同的方法来实现取掉statusbar的背景
//		if (Build.VERSION.SDK_INT >= 21) {
//			full(false);
//			this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		} else {
//			getWindow().addFlags(
//					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//			this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		}
		setContentView(R.layout.activity_icon_upload);
//	    width = this.getWindowManager().getDefaultDisplay().getWidth();
		// 初始化控件
		init();
		
	}

	/**
	 * 初始化控件的方法
	 */
	private void init() {
		userId = this.getIntent().getStringExtra("userId");
		imageUrl = this.getIntent().getStringExtra("imageUrl");
		mIconDownloadUtils = new IconDownloadUtils(IconUpLoadActivity.this);
		btnBack = (LinearLayout) findViewById(R.id.icon_back);
		imgMenu = (ImageView) findViewById(R.id.img_icon_menu);
		imgShow = (ImageView) findViewById(R.id.img_icon_show);
		btnBack.setOnClickListener(this);
		imgMenu.setOnClickListener(this);
		Glide.get(this).clearMemory();
//		 //设置imageview的宽高都为屏幕的宽
		setImageWidthHeight();
		Glide.with(this)
				.load(imageUrl)
				.error(R.drawable.defaultavator).crossFade().into(imgShow);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.icon_back:
			Intent i = new Intent();
			i.putExtra("imageUrl", imageUrl);
			setResult(1, i);
			finish();
			break;
		case R.id.img_icon_menu:
			// 创建布局
			View viewpop = LayoutInflater.from(this).inflate(
					R.layout.icon_menu_layout_item, null);
			// 创建PopupWindow实例,LayoutParams.MATCH_PARENT,400分别是宽度和高度
			popupWindow = new PopupWindow(viewpop, LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT, true);
			popupWindow.setFocusable(false);
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			// 设置显示位置
			popupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
			initPopup(viewpop);
			backgroundAlpha((float) 0.5);
			popupWindow
					.setOnDismissListener(new PopupWindow.OnDismissListener() {
						@Override
						public void onDismiss() {
							// popupwindow消失的时候恢复成原来的透明度
							backgroundAlpha((float) 1.0);
						}
					});
			break;
		default:

			break;

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			final Intent data) {
		// TODO Auto-generated method stub
		// super.onActivityResult(requestCode, resultCode, data);
		if (data == null) {
			return;
		}
		if(requestCode==0){
			imageUrl = data.getStringExtra("reciverbitmap");
			Glide.get(this).clearMemory();
			Glide.with(this)
			.load(imageUrl)
			.error(R.drawable.default_error).crossFade().into(imgShow);
		}
	}
    /**
     * 初始化popupwindow内部控件，设置点击事件
     * @param v
     */
	private void initPopup(View v) {
		tvChoose = (TextView) v.findViewById(R.id.tv_choose);
		tvSave = (TextView) v.findViewById(R.id.tv_save);
		tvCancel = (TextView) v.findViewById(R.id.tv_cancel);
		tvChoose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 点击跳转到展示手机相片的页面
				Intent i = new Intent(IconUpLoadActivity.this,IconUpLoadPickerActivity.class);
				i.putExtra("userId", userId);
				startActivityForResult(i,0);
				popupWindow.dismiss();
			}
		});
		tvSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 实现头像下载的地方
				if(imageUrl!=null){
					mIconDownloadUtils.downloadIcon(imageUrl);
				}else{
					popupWindow.dismiss();
					Toast.makeText(IconUpLoadActivity.this,"无法获取图片", Toast.LENGTH_LONG).show();
				}
				popupWindow.dismiss();
				
			}

		});
		tvCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
			}

		});
	}

	/**
	 * statusbar
	 * 
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

	/**
	 * 设置添加屏幕的背景透明度
	 * 
	 * @param bgAlpha
	 */
	public void backgroundAlpha(float bgAlpha) {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = bgAlpha; // 0.0-1.0
		getWindow().setAttributes(lp);
	}
	private void setImageWidthHeight(){
		int screenWidth = getScreenWidth(this); // 获取屏幕宽度  
		ViewGroup.LayoutParams lp = imgShow.getLayoutParams();  
		lp.width = screenWidth;  
		lp.height = screenWidth;  
		imgShow.setLayoutParams(lp);  
		  
		imgShow.setMaxWidth(screenWidth);  
		imgShow.setMaxHeight(screenWidth); 
	}
	
	
    //获取屏幕的宽度  
public static int getScreenWidth(Context context) {  
    WindowManager manager = (WindowManager) context  
            .getSystemService(Context.WINDOW_SERVICE);  
    Display display = manager.getDefaultDisplay();  
    return display.getWidth();  
}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
			Intent i = new Intent();
			i.putExtra("imageUrl", imageUrl);
			setResult(1, i);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);

	}

}
