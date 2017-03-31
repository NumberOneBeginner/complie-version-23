package com.none.staff.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import com.hsbc.greenpacket.util.EntityUtil;
import com.hsbc.greenpacket.util.NameValueStore;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 公共的工具类
 * @author willis
 *
 */
public class CommonUtils {


	/**
	 * 显示软件盘的方法，这里要采用延时操作，不然会显示不了软键盘。主要是登录，注册时一进去就自动弹出键盘。
	 */
	public static void showSoft(final EditText editText) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				InputMethodManager inputManager = (InputMethodManager) editText
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(editText, 0);
			}

		}, 50);

	}

	// 快速显示键盘的方法，引用回复的时候回复按钮一点弹出键盘就用这个方法。
	public static void ShowSoftFast(Context context) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		View view = ((Activity) context).getCurrentFocus();
		if (view != null) {
			imm.showSoftInput(view, 0); // 显示软键盘
		}
	}

	/**
	 * 隐藏软键盘的方法
	 * 
	 * @param context
	 *            要传入的上下文
	 */
	public static void hiddenSoft(Context context) {
		// 取得输入方法的服务类
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		View view = ((Activity) context).getCurrentFocus();
		if (view != null) {
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);// 隐藏软键盘

		}
	}

	// 快速隐藏键盘
	public static void hideSoftQuick(Activity context) {
		((InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(context.getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}


	/***
	 * 隐藏键盘
	 * @Description:TODO
	 * @param context
	 */

	public static void hideIme(Activity context) {
		if (context == null)
			return;
		final View v = context.getWindow().peekDecorView();
		if (v != null && v.getWindowToken() != null) {
			InputMethodManager imm = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		}
	}

	/***
	 * 键盘是否打开
	 * @Description:TODO
	 * @param context
	 * @return
	 */
	public static boolean isImeShow(Context context) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		return imm.isActive();
	}

	/***
	 * 判断是否有sd卡
	 * @Description:TODO
	 * @return
	 */
	public static boolean ExistSDCard() {  
		if (android.os.Environment.getExternalStorageState().equals(  
				android.os.Environment.MEDIA_MOUNTED)) {  
			return true;  
		} else  
			return false;  
	} 

	/**
	 * 取得arrays.xml中的数据以数组的形式返回
	 * @param context  上下文
	 * @param arrays   arrays.xml文件中的 string-name的名字
	 * @return
	 */
	public static String[] getStringFormArrays(Context context,int arrayres){
		Resources res = context.getResources();
		return res.getStringArray(arrayres); 
	}

	/**
	 * MD5加密算法
	 * 
	 * @param str
	 * @return
	 */
	public static String toMD5(String str) {
		try {
			MessageDigest algorithm = MessageDigest.getInstance("MD5");
			algorithm.reset();
			algorithm.update(str.getBytes("UTF-8"));
			return toHexString(algorithm.digest());
		} catch (NoSuchAlgorithmException e) {
			Log.v("MD5", "toMd5(): " + e);
			throw new RuntimeException(e);
		} catch (UnsupportedEncodingException e) {
			Log.v("MD5", "toMd5(): " + e);
		}
		return "";
	}
	private static String toHexString(byte[] bytes) {
		StringBuilder hexString = new StringBuilder();
		for (byte b : bytes) {
			hexString.append(Integer.toHexString(0xFF & b));
		}
		return hexString.toString();
	}


	/**
	 * 取得应用程序的版本名字
	 * @Methods: getAppVersionName
	 * @param context 上下文
	 * @return 返回当前程序版本名
	 * @throws
	 */
	public static String getAppVersionName(Context context) {
		String versionName = "";
		try {
			// 取得包信息
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
			if (versionName == null || versionName.length() <= 0) {
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return versionName;
	}


	/**
	 * 取得应用程序的版本号
	 * @Methods: getAppVersionCode
	 * @param context 上下文
	 * @return 返回当前程序版本code
	 * @throws
	 */
	public static int getAppVersionCode(Context context) {
		int versionCode = 0 ;
		try {
			// 取得包信息
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionCode = pi.versionCode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return versionCode;
	}
	/**创建选择对话框*/
	private static Dialog mDialog ;
	public static void createSelectDialog(Context context,String title,String[] strs, DialogInterface.OnClickListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context) ;
		builder.setTitle(title);
		builder.setItems(strs,listener) ;
		mDialog = builder.create() ;
		mDialog.show() ;
	}
	/**让选择对话框消失**/
	public static void  dismissCreateDialog(){
		if(null !=mDialog && mDialog.isShowing()){
			mDialog.dismiss() ;
			mDialog = null ;
		}
	}
	public static String getMyUUID(Context context) {

		final TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);   

		String tmDevice = "" + tm.getDeviceId();  

		String tmSerial = "" + tm.getSimSerialNumber();   

		String androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);   

		UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());   

		String uniqueId = deviceUuid.toString();

		Log.i("iii","uuid="+uniqueId);

		return uniqueId;
	}
	public static boolean accordance(String password1, String repassword1,Context context) {
		if(!password1.equals(repassword1)){
			Toast.makeText(context, "两次密码不一致，请重新输入", 1).show();
			return false;
		}
		return true;
	}

	public static  String Md5(String str) {

		byte[] hash;
		try {
			hash = MessageDigest.getInstance("MD5").digest(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Huh, MD5 should be supported?", e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Huh, UTF-8 should be supported?", e);
		}

		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash) {
			if ((b & 0xFF) < 0x10) hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}
		return hex.toString();
	}

	/***
	 * 截屏工具类
	 * @param activity
	 * @return
	 */
	public static Bitmap takeScreenShot(Activity activity){
		View view =activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();
		Rect rect = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		int statusBarHeight = rect.top;
		System.out.println(statusBarHeight);
		
		//取得屏幕的宽高
		DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels ;
        int height = dm.heightPixels ;
		
		Bitmap bitmap2 = Bitmap.createBitmap(bitmap,0,statusBarHeight, width, height - statusBarHeight);
		view.destroyDrawingCache();
		return bitmap2;
		}
	
	 private static long lastClickTime;
	 /**防止按钮连续点击**/
	    public synchronized static boolean isFastClick() {
	        long time = System.currentTimeMillis();   
	        if ( time - lastClickTime < 500) {   
	            return true;   
	        }   
	        lastClickTime = time;   
	        return false;   
	    }
	    
	    public static String getStoredValue(String key,Context context) {
	        if (key == null) {
	            return null;
	        }
	        String eid = EntityUtil.getSavedEntityId(context);
	        if (eid != null) {
	            NameValueStore store = new NameValueStore(context);
	            return store.getAttribute(key);
	        }
	        return null;
	    }
}
