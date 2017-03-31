package com.none.staff.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 
 * @author willis
 *  网络工具类
 */
public class NetworkUtils {

	public static int NETWORN_NONE = 0;
	public static int NETWORN_WIFI = 1;
	public static int NETWORN_MOBILE = 2;
	public static int NETWORN_USE = 3;

	/**
	 * 检查网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 检查wifi网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWifiConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null) {
				return mWiFiNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	// public static boolean isNetworkAvailable(Activity mActivity) {
	// Context context = mActivity.getApplicationContext();
	// ConnectivityManager connectivity = (ConnectivityManager)
	// context.getSystemService(Context.CONNECTIVITY_SERVICE);
	// if (connectivity == null) {
	// return false;
	// } else {
	// NetworkInfo[] info = connectivity.getAllNetworkInfo(); //取得所有的网络类型 wifi
	// 上行下行
	// if (info != null) {
	// for (int i = 0; i < info.length; i++) {
	// if (info[i].getState() == NetworkInfo.State.CONNECTED) {
	// //如果有网络连接就返回True，否则就返回false
	// return true;
	// }
	// }
	// }
	// }
	// return false;
	// }
	//
	/**
	 * 是否是 手机网络
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isMobileConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mMobileNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mMobileNetworkInfo != null) {
				return mMobileNetworkInfo.isAvailable();
			}
		}
		return false;
	}
	public static boolean deviceOnline(Context context) {   
        ConnectivityManager mConnectivity = (ConnectivityManager) context   
                .getSystemService(Context.CONNECTIVITY_SERVICE);   
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        if (info == null) {  
            return false;   
        }
        if (!info.isAvailable()|| !info.isConnected()) { 
            return false;   
        } else {   
            return true;
        }
	}
	/**
	 * 获取网络连接类型
	 * 
	 * @param context
	 * @return
	 */
	public static int getConnectedType(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
				return mNetworkInfo.getType();
			}
		}
		return -1;
	}

	/**
	 * 如果没有网络弹出对话框提示
	 * 
	 * @param activity
	 *            要传入的activity
	 */
	public static void netWorkDialog(final Activity activity) {

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("No internet connection. Please check your network. ");
		builder.setPositiveButton("setting",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						 // TODO Auto-generated method stub
			                Intent intent=null;
			                //判断手机系统的版本  即API大于10 就是3.0或以上版本 
			                if(android.os.Build.VERSION.SDK_INT>10){
			                    //intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
			                	intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
			                }else{
			                    intent = new Intent();
			                    ComponentName component = new ComponentName("com.android.settings","com.android.settings.WirelessSettings");
			                    intent.setComponent(component);
			                    intent.setAction("android.intent.action.VIEW");
			                }
			                activity.startActivity(intent);
					}

				});
		builder.setNegativeButton("cancel", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				activity.finish() ;
			}
		});
		builder.show();

	}
}
