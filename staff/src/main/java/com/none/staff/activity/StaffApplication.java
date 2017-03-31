/*
 * COPYRIGHT. HSBC HOLDINGS PLC 2014. ALL RIGHTS RESERVED.
 *
 * This software is only to be used for the purpose for which it has been
 * provided. No part of it is to be reproduced, disassembled, transmitted,
 * stored in a retrieval system nor translated in any human or computer
 * language in any way or for any other purposes whatsoever without the
 * prior written consent of HSBC Holdings plc.
 */
package com.none.staff.activity;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.none.staff.domain.UserInfo;
import com.none.staff.json.JsonConfig;
import com.none.staff.utils.SPUtil;

/**
 * <p><b>
 * TODO : Insert description of the class's responsibility/role.
 * </b></p>
 */
public class StaffApplication extends Application {
	public JsonConfig config=null;
	public static String mCacheDataDir; 
	private static StaffApplication app;
	private static final String TAG = StaffApplication.class.getName();


	@Override
	public void onCreate() {
		super.onCreate();
		app = this;
		applicationInit() ;
	}

	private void applicationInit() {

		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File externalStorageDirectory = Environment
					.getExternalStorageDirectory();
			mCacheDataDir = new StringBuilder()
			.append(externalStorageDirectory.getAbsolutePath())
			.append("/").append("red_staff/cache/")
			.toString();
		} else {

			File cacheDir = this.getCacheDir();
			mCacheDataDir = new StringBuilder()
			.append(cacheDir.getAbsolutePath()).append("/")
			.append("red_staff/").toString();
		}

		Log.e(TAG, "cache dir = " + mCacheDataDir);
		File cachePath = new File(mCacheDataDir);
		if (!cachePath.exists()) {
			cachePath.mkdirs();
		}


	}

	public static StaffApplication getApp(){
		return app;
	}



	public LinkedHashMap<String,Activity> activityStack= new LinkedHashMap<String,Activity>();
	public void cleanActivity(){
		java.util.Iterator<String> e= this.activityStack.keySet().iterator();
		while(e.hasNext()){
			try{
				String key= e.next();
				Activity activity = this.activityStack.get(key);
				if(activity!=null){
					activity.finish();
				}else{
					Log.d("staff application","activity is null");
				}
			}catch(Exception er){
				Log.e("staff application","cleanActivity error");
			}
		}
	}
	public HashMap<String, Activity> getActivityStack() {
		return activityStack;
	}
	public void exitApp(){
		if(activityStack.size()>0){
			for (Entry<String, Activity> entry : activityStack.entrySet()) {
				Activity activity = entry.getValue();
				activity.finish();
			}
		}
	}

	private UserInfo user = null;

	/**
	 * ����¼���û���Ϣ
	 */
	public void clearUser() {
		SPUtil.clear(getApplicationContext(), "user_info");
		user = null;
		// ("clearUser");
	}

	/**
	 * ��ȡ��¼���û���Ϣ
	 */
	public UserInfo getUser() {
		if (user == null) {
			user = (UserInfo) SPUtil.readObjectToShared(SPUtil.getValue(
					getApplicationContext(), "user_info"));

			// ("getUser"
			// + mSharedPreferences.getString("login_user", null));
		}
		return user;
	}

	/**
	 * ��ס��¼���û���Ϣ
	 */
	public void setUser(UserInfo user) {
		this.user = user;
		if (user != null) {
			SPUtil.putValue(getApplicationContext(), "user_info",
					SPUtil.saveObjectToShared(user));
		}
	}

	/**
	 * Kill app application and processes
	 * @author York Y K LI[May 13, 2013]
	 *
	 */
	public void killApplication(){

		//need permission : <uses-permission android:name="android.permission.RESTART_PACKAGES" />
		String packName=this.getPackageName();
		ActivityManager activityMgr=(ActivityManager)this.getSystemService(ACTIVITY_SERVICE);
		activityMgr.restartPackage(packName);
		//need permission : <uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES" />
		activityMgr.killBackgroundProcesses(packName);
		android.os.Process.killProcess(android.os.Process.myPid());
	}











}
