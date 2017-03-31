/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package com.none.staff.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.hsbc.greenpacket.util.actions.HookConstants;
import com.none.Plugin.Beacon.eventNotification;
import com.none.Push.Utils;
import com.none.staff.R;
import com.none.staff.event.AppEvent;
import com.none.staff.task.GetRegionConfigTask;
import com.none.staff.util.AndroidBug5497Workaround;
import com.none.staff.util.BackWebView;
import com.none.staff.util.SSLAcceptingCordovaWebViewClient;
import com.none.staff.util.SSLAcceptingIceCreamCordovaWebViewClient;

import org.apache.cordova.CordovaActivity;
import org.apache.cordova.CordovaChromeClient;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewClient;
import org.apache.cordova.IceCreamCordovaWebViewClient;

import java.io.File;

import de.greenrobot.event.EventBus;
import fi.iki.elonen.SimpleWebServer;


public class staff extends CordovaActivity {

	private static final String TAG = "Staff Activity";
	private static final String CHANGE_MANU_JS="changeMenuBackFromGreenLaiSee";
	private static final int START_SERVER_DONE_MSG = 111;
	private Handler handler = null;
	public final static int GO_TO_SCA_FUNCTION = 10001;
	private void loga() {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			WebView.setWebContentsDebuggingEnabled(true);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.e("bob","staff start");
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//			Window window = getWindow();
//			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |     WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//			window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//			window.setStatusBarColor(Color.TRANSPARENT);
//			window.setNavigationBarColor(Color.TRANSPARENT);
//		}else{
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		}

//		if (Build.VERSION.SDK_INT >= 21) {
//			full(false);
//			 this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		}else{
			//除去状态栏
//	        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//	        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		}
		//根据获取到的设备的NavigationBar的高度为DecorView设置下边距
		getWindow().getDecorView().findViewById(android.R.id.content)
				.setPadding(0, 0, 0, getNavigationBarHeight());
		//为decorview设置背景色
//        getWindow().getDecorView().findViewById(android.R.id.content)
//                .setBackgroundResource(R.color.black);
		StaffApplication application = (StaffApplication) getApplication();
		application.getActivityStack().put(String.valueOf(this.hashCode()), this);
		this.root.setBackgroundResource(R.drawable.screen);
//		super.init();
		this.init();
        AndroidBug5497Workaround.assistActivity(this);
		loga();
		displaySplash();
		setHandler();
		new Thread(new Runnable() {
			public void run() {
				startServer();
			}
		}).start();
		try{
			PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, Utils.getMetaValue(staff.this, "api_key"));
			Log.e("staff", "startWork");
		}catch(Exception e){
			Log.e("staff", "start push failed",e);
		}


	}


	@Override
	protected CordovaWebViewClient makeWebViewClient(CordovaWebView webView) {
		// TODO Auto-generated method stub
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB
				|| android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
			webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
			return new SSLAcceptingCordovaWebViewClient(this, webView);
		} else {
			webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
			return new SSLAcceptingIceCreamCordovaWebViewClient(this, webView);
		}
	}

	/**spalsh对话框**/
	private Dialog splashDialog ;

	//在webview上面添加一层spalsh

	private void displaySplash() {
		//super.appView.clearCache(true); 
		//		splashId = getResources().getIdentifier("screen", "drawable",this.getPackageName());
		//		if (splashId != 0) {
		//			super.setIntegerProperty("splashscreen", splashId);
		//		}
		//注册事件
		EventBus.getDefault().register(this) ;
		splashDialog = new Dialog(this,
				android.R.style.Theme_Translucent_NoTitleBar);
		//设置dialog占满全屏，填充到statusBar的位置上
		 WindowManager m = getWindowManager();    
	       Display d = m.getDefaultDisplay();
	       WindowManager.LayoutParams p = getWindow().getAttributes();
	       p.height = (int) (d.getHeight() * 1.0);    
	       p.width = (int) (d.getWidth() * 1.0);    
	       p.alpha = 1.0f;      
	       p.dimAmount = 0.0f;     
	       splashDialog.getWindow().setAttributes(p);
	       
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.sp_with_loading, null);
		splashDialog.setContentView(view);
		splashDialog.setCancelable(false);
		splashDialog.show();


	}

	//监听通知事件消失spalsh
	public void onEventMainThread(AppEvent event) {
		if(1==event.getType()){
			disMissDialog() ;
		}
	}

	private void disMissDialog(){
		if(splashDialog!=null && splashDialog.isShowing()){
			splashDialog.dismiss() ;
			splashDialog = null ;
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		Log.d(TAG, "notify receive1"); 
		super.onNewIntent(intent);
		//jammy added for hsbc150
		String id = intent.getStringExtra("id"); 
		if(id!=null){
			onActivityResult(GO_TO_SCA_FUNCTION,Activity.RESULT_OK,intent);
		}
		//end 
		setIntent(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.e("bob","staff resume");
		Intent intent = getIntent();
		if (intent.hasExtra("notifi")) {
			Log.d(TAG, "notify receive2");
			String notifi = intent.getStringExtra("notifi");
			eventNotification eventNotification = new eventNotification(notifi);
			EventBus.getDefault().post(eventNotification);
		}
	}
	@Override
	public void onDestroy() {
		Log.d(TAG, "staff onDestroy");
		super.onDestroy();
		SimpleWebServer server=SimpleWebServer.getServer();
		if (server != null){
			server.stop();
		}
		disMissDialog();
		((StaffApplication)this.getApplication()).getActivityStack().remove(String.valueOf(this.hashCode()));
	}
	@Override
	public void onPause(){
		super.onPause();
	}
	@Override
	public void onRestart(){
		Log.e(TAG, "onRestart");
		super.onRestart();
		SimpleWebServer server =SimpleWebServer.getServer();
		if(server==null||!server.isAlive()){
			Intent intent=new Intent(this, Sp.class) ;
			startActivity(intent);
			intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			overridePendingTransition(0, 0);
			finish();
			return;
		}
		if(GetRegionConfigTask.configurationExpired(this,"0")){
			Intent intent=new Intent(this, Sp.class) ;
			startActivity(intent);
			intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			overridePendingTransition(0, 0);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//jammy
		Log.i("2222", "onActivityResult ,requestCode= " + requestCode + " resultCode="+resultCode);
		//end
		switch (requestCode) {
		case GO_TO_SCA_FUNCTION:
			if (resultCode==Activity.RESULT_OK && data != null) {
				String functionId = data.getStringExtra(HookConstants.ID);
				//jammy
				Log.i("2222", "onActivityResult ,functionId= " + functionId);
				//end
				if(functionId!=null){
					StringBuffer sb=new StringBuffer();
					sb.append("javascript:").append(CHANGE_MANU_JS).append("(\"").append(functionId).append("\")");
					super.loadUrl(sb.toString());
				}else{
					Log.e(TAG,"functionId is neccessary!!");
				}
			}else{
				Log.e(TAG,"Data intent is neccessary!!");
			}
			break;
		}
	}

	private void startServer() {
		try {
			String port =this.getResources().getString(R.string.local_host_port);
			Log.i(TAG, "-d " + this.getFilesDir().getPath() + File.separator + "betta" + File.separator + "www");
			SimpleWebServer server =SimpleWebServer.getServer();
			if(server==null||!server.wasStarted() || !server.isAlive()){
				SimpleWebServer.StartServer(new String[] {"-d",
						this.getFilesDir().getPath() + File.separator + "betta" + File.separator + "www","-p",port});
			}
			handler.sendEmptyMessage(START_SERVER_DONE_MSG);
		}catch (Exception e) {
			Log.e(TAG, "start server error", e);
		}
	}
	private void setHandler() {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if (msg.what == START_SERVER_DONE_MSG) {
					staff.super.loadUrl(staff.this.getResources().getString(R.string.local_host_url));
				}
			}
		};
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

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event)  {
//		
//		
//	    if (keyCode == KeyEvent.KEYCODE_BACK) { //按下的如果是BACK，同时没有重复
//	       Toast.makeText(getApplicationContext(),"魔力去吧Back键测试",1).show();
//	        return true;
//	    }

//	    return super.onKeyDown(keyCode, event);
//	}
	
	
	 @Override
	    public void init() {
//	        只是把源码中的CordovaWebView换成NobackWebView，其他还是源码
	        CordovaWebView webView = new BackWebView(this);
//	        super.init(webView, makeWebViewClient(webView), makeChromeClient(webView));
	        CordovaWebViewClient webViewClient;
	        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
	            webViewClient = new CordovaWebViewClient(this, webView);
	        } else {
	            webViewClient = new IceCreamCordovaWebViewClient(this, webView);
	        }
	        this.init(webView, webViewClient,
	                new CordovaChromeClient(this, webView));
	    }
}
