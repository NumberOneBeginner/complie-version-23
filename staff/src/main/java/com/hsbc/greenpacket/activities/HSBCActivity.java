package com.hsbc.greenpacket.activities;



import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;

import com.hsbc.greenpacket.util.ActivityUtil;

import com.none.staff.R;
import com.none.staff.activity.Sp;
import com.none.staff.activity.StaffApplication;
import com.none.staff.task.AsyncTaskWithCallback;

public class HSBCActivity extends Activity {
	public static final int STOP = 0;
	public static final int PAUSE = 2;
	public static final int START = 1;
	public static final int DESTORY = -1;
    private static final String TAG = "HSBCActivity";	
	private int activityStatus = 0;
	public String commonsavedLocaleStr;
	private final List<AsyncTaskWithCallback<?, ?, ?>> runningTasks = new ArrayList<AsyncTaskWithCallback<?, ?, ?>>();
	List<String> domainList = new ArrayList<String>();
    private String loadingMessage;
	
	
	/*************************************************/
	public void addTask(final AsyncTaskWithCallback<?, ?, ?> task) {
		this.runningTasks.add(task);
	}

	public void removeTask(final AsyncTaskWithCallback<?, ?, ?> task) {
		this.runningTasks.remove(task);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= 21) {
			full(false);
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		} else {
			// 除去状态栏
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
		//根据获取到的设备的NavigationBar的高度为DecorView设置下边距
		getWindow().getDecorView().findViewById(android.R.id.content)
				.setPadding(0, 0, 0, getNavigationBarHeight());
		//为decorview设置背景色
//        getWindow().getDecorView().findViewById(android.R.id.content)
//                .setBackgroundResource(R.color.black);
        StaffApplication application = (StaffApplication) getApplication();
        application.getActivityStack().put(String.valueOf(this.hashCode()), this);
	}
	@Override
	protected void onDestroy(){
	    super.onDestroy();
	    ((StaffApplication)this.getApplication()).getActivityStack().remove(String.valueOf(this.hashCode()));
	}
    
	public int getActivityStatus() {
		return activityStatus;
	}


	protected Dialog createNetworkErrorDialog() {
	    return ActivityUtil.createOkButtonDialog(this, this.getString(R.string.networkError),  this.getString(R.string.networkErrorMessage),  this.getString(R.string.ok));
	}

	private Dialog createInvalidVersionDialog() {
		return ActivityUtil.createOkButtonDialog(this, null, this.getString(R.string.notSupportedMessage), getString(R.string.ok));
	}
	private Dialog createDeviceSettingWarningDialog(){
	    return ActivityUtil.createOkButtonDialog(this, null, "Dont tick the option \"don't keep activites\" in developent option", getString(R.string.ok));
	}
	private Dialog createNotSupportError() {
		AlertDialog.Builder invalidVersionDialogBuilder = new AlertDialog.Builder(this);
		invalidVersionDialogBuilder.setTitle(getString(R.string.notSupported)).setMessage(this.getString(R.string.notSupportedMessage)).setCancelable(false)
				.setPositiveButton(this.getString(R.string.ok), new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, final int which) {
						// Go the contry selector.
						//goToCountrySelector();
					}
				});
		Dialog invalidVersionDialog = invalidVersionDialogBuilder.create();
		invalidVersionDialog.setCanceledOnTouchOutside(false);
		return invalidVersionDialog;
	}
	/**
	 * 
	 * @author York Y K LI[Dec 25, 2012]
	 * @return
	 *
	 */
    protected Dialog createProgressDialog() {
        return ActivityUtil.createProgressDialog(this, this.loadingMessage);
    }

	public void setActivityStatus(int activityStatus) {
		this.activityStatus = activityStatus;
	}

	@SuppressWarnings("rawtypes")
	public void handleCallback(final AsyncTaskWithCallback task, final int ref) {
		this.removeTask(task);
	}

	/**
	 * Sets up the webview to enable javascript and caching.
	 */
	protected void configureWebView(final WebView webview) {
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setScrollBarStyle(0);
		//set the background of webView to be transparent
//		webview.setBackgroundColor(0X00000000);
		// webview.getSettings().setUserAgentString(getString(R.string.user_agent));
		webview.getSettings().setDomStorageEnabled(true);
		webview.getSettings().setAllowFileAccess(true);
		webview.getSettings().setAppCacheEnabled(true);

		// Added by JW [22-Sept-2012] US Webtouch ABSL with GPS location
		webview.getSettings().setGeolocationEnabled(true);
		
		/* 
		 * Added by JW [3-Oct-2012] Fix ISR Defect ID#3987
		 * User logon credentials displayed in Android 
		 * Candidate View field when testing on simulators
		 */
		webview.getSettings().setSaveFormData(false);
		webview.getSettings().setSavePassword(false);
	}

	public void slideBottomToTop() {
		overridePendingTransition(R.anim.in_bottomtop, 0);
	}

	public void slideTopToBottom() {
		overridePendingTransition(0, R.anim.out_topbottom);
	}

	public void slideLeftToRight() {
		overridePendingTransition(R.anim.page_in_leftright, R.anim.page_out_leftright);
	}

	public void slideRightToLeft() {
		overridePendingTransition(R.anim.page_in_rightleft, R.anim.page_out_rightleft);
	}

	public boolean domainIsValid(String url) {
		// domain checking
		if (url != null && url.startsWith("file://")) {
			// return true;
		}
		if (domainList != null) {
			try {
				URI uri = new URI(url);
				String domain = uri.getHost();
				boolean exist = existDomian(domainList, domain);
				if (exist) {
					// url is valid
					Log.e(TAG,"url is valid:" + domain);
					return true;
				} else {
					// url is invalid
					Log.e(TAG,"url is invalid:" + domain);
					return false;
				}

			} catch (Exception e) {
				Log.e(TAG,"url get domian error");
			}
		}
		return false;
	}

	public boolean existDomian(List<String> domainList, String domain) {
		for (int i = 0; i < domainList.size(); i++) {
			String key = domainList.get(i);
			if (domain.endsWith(key)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateDialog(int)
	 */
	@Override
	protected Dialog onCreateDialog(final int id) {
		switch (id) {
		case Constants.PROGRESS_DIALOG:
			return createProgressDialog();
		case Constants.NO_CONNECTION_RETRY_DIALOG:
			return createNetworkErrorDialog();
		case Constants.NETWORK_ERROR_DIALOG:
			return createNetworkErrorDialog();
		case Constants.INVALID_VER_DIALOG:
			return createInvalidVersionDialog();
		case Constants.NETWORK_ERROR_SELECT_COUNTRY_DIALOG:
			return null;//createNetworkErrorAlertAndSelectCountry();
		case Constants.DEVICE_SETTIONG_WARNING_DIALOG:
            return createDeviceSettingWarningDialog();
		default:
			return super.onCreateDialog(id);
		}
	}

	public void showProgressDialog() {
		synchronized (this) {
			try {
				this.showDialog(Constants.PROGRESS_DIALOG);
			} catch (Exception e) {
				Log.e(TAG,"showProgressDialog error");
			}
		}
	}
	public void hideProgressDialog() {
		synchronized (this) {
			try {
				this.dismissDialog(Constants.PROGRESS_DIALOG);
			} catch (Exception e) {
				Log.e(TAG,"hideProgressDialog error");
			}
		}
	}

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (((StaffApplication) this.getApplication()).getActivityStack() == null) {
            this.finish();
            Intent activity = new Intent(this, Sp.class);
            activity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(activity);
            this.slideRightToLeft();
        }

    }

	public void release() {
		this.finish();
	}

	/**
	 * @description Recycling resources every time when the locale don't equal  
	 * @author Cherry
	 * @date 2012-11-27
	 */
	protected void recycleDialogResources(){
		removeDialog(Constants.PROGRESS_DIALOG);
		removeDialog(Constants.NO_CONNECTION_RETRY_DIALOG);
		removeDialog(Constants.NETWORK_ERROR_DIALOG);
		removeDialog(Constants.INVALID_VER_DIALOG);
		removeDialog(Constants.APP_ERROR_DIALOG);
		removeDialog(Constants.NETWORK_ERROR_SELECT_COUNTRY_DIALOG);
	}
	
	
    /**
     * @author Michael Y W Yeung [23 May 13]
     * @description RTL language enhancement
     * Add this to localize the dialog before showing it (since onCreateDialog will be just initialized once).
     **/
	@Override 
	protected void onPrepareDialog(int id, Dialog dialog){
		switch (id) {
		case Constants.PROGRESS_DIALOG:
			localizeAlertDialog(dialog, null,this.loadingMessage, null, null);
			break;
		case Constants.NO_CONNECTION_RETRY_DIALOG:
			 localizeAlertDialog(dialog, getString(R.string.networkError),getString(R.string.networkErrorMessage), getString(R.string.ok), null);
			 break;
		case Constants.NETWORK_ERROR_DIALOG:
			 localizeAlertDialog(dialog, getString(R.string.networkError),getString(R.string.networkErrorMessage), getString(R.string.ok), null);
			 break;
		case Constants.INVALID_VER_DIALOG:
			 localizeAlertDialog(dialog, null,getString(R.string.notSupportedMessage), getString(R.string.ok), null);
			 break;
		case Constants.APP_ERROR_DIALOG:						
			 localizeAlertDialog(dialog, getString(R.string.networkError),getString(R.string.networkErrorMessage), getString(R.string.ok), null);
			 break;
		
		default:
			 super.onPrepareDialog(id,dialog);
		}
	}
	
    /**
     * @author Michael Y W Yeung [23 May 13]
     * @description RTL language enhancement
     * Add this to localize the dialog.
     **/	
	protected void localizeAlertDialog(Dialog dialog, String title, String message, String positiveBtnTxt, String negativeBtnTxt){		
		if(dialog instanceof AlertDialog){
			AlertDialog alertDialog = (AlertDialog)dialog;
			
	    	if(title!=null){
	    		alertDialog.setTitle(title);
	    	}
	    	
	    	if(message!=null){
	    		alertDialog.setMessage(message);
	    	}
	    	
	    	if(positiveBtnTxt!=null){
	    		alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(positiveBtnTxt);
	    	}
	    	
	    	if(negativeBtnTxt!=null){
	    		alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setText(negativeBtnTxt);
	    	}
		}
    }
	
	/**
     * @author James M J Chen [9 Jul 13]
     * Checked by Michael Y W Yeung
     * @description RTL language enhancement
     * Get the saved locale or return the default locale.
     **/
	public String getSavedLocale(){
		
		return "en";
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
}
