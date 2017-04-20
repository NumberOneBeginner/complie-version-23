package com.none.staff.activity.zbarScanner;


import org.cloudsky.cordovaPlugins.ZBarScannerActivity;

import com.none.staff.R;
import com.vuforia.VideoPlayback.app.VideoPlayback.VideoPlayback;

import de.greenrobot.event.EventBus;

import android.Manifest;
import android.app.ActivityGroup;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TestView  extends ActivityGroup implements OnClickListener{

	private LinearLayout container;
	private ImageView btn1;
	private ImageView btn2;
	private RelativeLayout RelativeLayout_Qrcode;
	private RelativeLayout RelativeLayout_Discover;
	private TextView tv_Qrcode;
	private TextView tv_Discover;
	private LinearLayout LinearyLayoutlll;
	private ImageView arBackButton;
	private TextView scan_discover;
	private static int SCAN_CODE = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (Build.VERSION.SDK_INT >= 21) {
			full(false);
			 this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		}else{

	        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
	        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
		//根据获取到的设备的NavigationBar的高度为DecorView设置下边距
        getWindow().getDecorView().findViewById(android.R.id.content)
                .setPadding(0, 0, 0, getNavigationBarHeight());
		//为decorview设置背景色
//        getWindow().getDecorView().findViewById(android.R.id.content)
//                .setBackgroundResource(R.color.black);
		setContentView(R.layout.testviewlayout);
		EventBus.getDefault().register(this);
		initViews();
		setListener();
		if (isCameraCanUse()) {

			//跳转到相关的拍照/扫描 页面
			btn2.setAlpha(0.5f);
			tv_Discover.setAlpha(0.5f);

			container.addView(
					getLocalActivityManager().startActivity(
							"Module1",
							new Intent(TestView.this, ZBarScannerActivity.class)
									.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
							.getDecorView());

		} else {

			//当前APP没有摄像头权限弹层，或者其他相关提示
			createPermissionDialog();
		}

	}

	private void setListener() {
		arBackButton.setOnClickListener(this);
		scan_discover.setOnClickListener(this);
		RelativeLayout_Qrcode.setOnClickListener(this);
		RelativeLayout_Discover.setOnClickListener(this);
	}

	private void initViews() {
		container = (LinearLayout) findViewById(R.id.relativelayout_whole);
		arBackButton=(ImageView)findViewById(R.id.arBackButton);
		scan_discover = (TextView)findViewById(R.id.scan_discover);
		LinearyLayoutlll=(LinearLayout)findViewById(R.id.LinearyLayoutlll);
		btn1=(ImageView)findViewById(R.id.btn1);
		btn2=(ImageView)findViewById(R.id.btn2);
		tv_Qrcode=(TextView)findViewById(R.id.tv_Qrcode);
		tv_Discover=(TextView)findViewById(R.id.tv_Discover);
		RelativeLayout_Qrcode=(RelativeLayout)findViewById(R.id.RelativeLayout_Qrcode);
		RelativeLayout_Discover=(RelativeLayout)findViewById(R.id.RelativeLayout_Discover);
	}

	public void onEvent(AnyEventType event) {
		TestView.this.finish();
	}  



	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if ((keyCode == KeyEvent.KEYCODE_BACK)) {  

			EventBus.getDefault().post(new AnyEventType("error"));
			return true;  

		}else {  
			return super.onKeyDown(keyCode, event);  
		}  
	}



	@Override
	public void onClick(View view) {



		switch (view.getId()) {

		case R.id.RelativeLayout_Qrcode:


			if(!getLocalActivityManager().getCurrentId().equals("Module1")){

				getLocalActivityManager().destroyActivity("Module2",true);

				container.removeAllViews();

				container.addView(
						getLocalActivityManager().startActivity(
								"Module1",
								new Intent(TestView.this, ZBarScannerActivity.class)
								.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
								.getDecorView());

				btn2.setAlpha(0.5f);
				tv_Discover.setAlpha(0.5f);
				btn1.setAlpha(1f);
				tv_Qrcode.setAlpha(1f);
			}

			break;



		case R.id.RelativeLayout_Discover:


			if(!getLocalActivityManager().getCurrentId().equals("Module2")){

				getLocalActivityManager().destroyActivity("Module1",true);

				container.removeAllViews();

				container.addView(
						getLocalActivityManager().startActivity(
								"Module2", 
								new Intent(TestView.this, VideoPlayback.class)
								.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
								.getDecorView());
				btn1.setAlpha(0.5f);
				tv_Qrcode.setAlpha(0.5f);
				btn2.setAlpha(1f);
				tv_Discover.setAlpha(1f);
			}

			break;

		case R.id.arBackButton:
			finish();
			EventBus.getDefault().post(new AnyEventType("error"));
			break;

		case R.id.scan_discover:


			if(!getLocalActivityManager().getCurrentId().equals("Module1")){

				getLocalActivityManager().destroyActivity("Module2",true);

				container.removeAllViews();

				container.addView(
						getLocalActivityManager().startActivity(
								"Module1",
								new Intent(TestView.this, ZBarScannerActivity.class)
								.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
								.getDecorView());

				btn2.setAlpha(0.5f);
				tv_Discover.setAlpha(0.5f);
				btn1.setAlpha(1f);
				tv_Qrcode.setAlpha(1f);
			}
			break;
		}



	}

	/**
	 * statusbar
	 * @param enable
	 *            false
	 */
	private void full(boolean enable) {
		// TODO Auto-generated method stub
		WindowManager.LayoutParams p = this.getWindow().getAttributes();
		if (enable) {

			p.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;

		} else {
			p.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);

		}
		getWindow().setAttributes(p);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
	}
	/**
	 * 动态获取设备NavigationBar的高度
	 * @return
	 */
    public int getNavigationBarHeight() {

        boolean hasMenuKey = ViewConfiguration.get(this).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        if (!hasMenuKey && !hasBackKey) {
            Resources resources = getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            //获取NavigationBar的高度
            int height = resources.getDimensionPixelSize(resourceId);
            return height;
        }
        else{
            return 0;
        }
    }
//
//
//	public void requestPermission(){
//		//判断当前Activity是否已经获得了该权限
//		if (ContextCompat.checkSelfPermission(this,
//				Manifest.permission.CAMERA)
//				!= PackageManager.PERMISSION_GRANTED) {
//
//			//如果App的权限申请曾经被用户拒绝过，就需要在这里跟用户做出解释
//			if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//					Manifest.permission.CAMERA)) {
//				Toast.makeText(this,"please give me the permission",Toast.LENGTH_SHORT).show();
//			} else {
//				//进行权限请求
//				ActivityCompat.requestPermissions(this,
//						new String[]{Manifest.permission.CAMERA},
//						0);
//			}
//
//		}else{
//
//		}
//	}
//
//	@Override
//	public void onRequestPermissionsResult(int requestCode,
//										   String permissions[], int[] grantResults) {
//		switch (requestCode) {
//			case 0: {
//				// 如果请求被拒绝，那么通常grantResults数组为空
//				if (grantResults.length > 0
//						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//					//申请成功，进行相应操作
//
//				} else {
//					//申请失败，可以继续向用户解释。
//					requestPermission();
//				}
//				return;
//			}
//		}
//	}
	/**
	 * 判断摄像头是否可用
	 * 主要针对6.0 之前的版本，现在主要是依靠try...catch... 报错信息，感觉不太好，
	 * 以后有更好的方法的话可适当替换
	 *
	 * @return
	 */
	public static boolean isCameraCanUse() {
		boolean canUse = true;
		Camera mCamera = null;
		try {
			mCamera = Camera.open();
			// setParameters 是针对魅族MX5 做的。MX5 通过Camera.open() 拿到的Camera
			// 对象不为null
			Camera.Parameters mParameters = mCamera.getParameters();
			mCamera.setParameters(mParameters);
		} catch (Exception e) {
			canUse = false;
		}
		if (mCamera != null) {
			mCamera.release();
		}
		return canUse;
	}
	/**
	 * Create a pop-up prompt after the camera permissions are not rejected
	 */
	private void createPermissionDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		builder.setTitle("shexiangtou");
		builder.setMessage("Camera permissions are turned off, please first open the permissions.");
		builder.setNegativeButton("seting", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				getAppDetailSettingIntent(TestView.this);
				dialog.dismiss();
				finish();
			}
		});
		builder.setPositiveButton("cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
		});
		builder.setCancelable(false);//设置点击空白处不消失
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	/**
	 * 跳转到权限设置界面
	 */
	private void getAppDetailSettingIntent(Context context){
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if(Build.VERSION.SDK_INT >= 9){
			intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
			intent.setData(Uri.fromParts("package", getPackageName(), null));
		} else if(Build.VERSION.SDK_INT <= 8){
			intent.setAction(Intent.ACTION_VIEW);
			intent.setClassName("com.android.settings","com.android.settings.InstalledAppDetails");
			intent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
		}
		startActivity(intent);
	}
}


