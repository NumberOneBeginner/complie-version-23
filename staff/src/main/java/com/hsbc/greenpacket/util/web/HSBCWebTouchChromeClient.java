package com.hsbc.greenpacket.util.web;

import android.app.Activity;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hsbc.greenpacket.util.actions.HookConstants;

public class HSBCWebTouchChromeClient extends WebChromeClient {
	
	/**
	 * 
	 */
	private Activity owningActivity;
	private static final String FUNCTION_DOM_LOAD_COMPLETE = "function=domLoadComplete";
    private static final String TAG = "HSBCWebTouchChromeClient";

	/**
	 * @param owningActivity
	 *            The activity displaying the webview this {@link WebViewClient} is been used for. This is used to display and hide dialogs
	 *            as required.
	 * @param progressDialogRef
	 *            The reference will be passed to {@link Activity#showDialog(int)} to display a "loading" dialog.
	 */
	public HSBCWebTouchChromeClient(final Activity owningActivity) {
		this.owningActivity = owningActivity;
	}

	/**
	 * Indicates that the {@link Activity} been used to show dialogs has been destroyed. This is added as its possible for the web client to
	 * be informed of events even if the {@link Activity} owning the {@link WebView} this is attached to has been destroy. Trying to
	 * show/hide dialogs at this point results in an error been thrown even though the app has been closed.
	 **/
	public void ownerDestroyed() {
		this.owningActivity = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.webkit.WebChromeClient#onJsAlert(android.webkit.WebView, java.lang.String, java.lang.String, android.webkit.JsResult)
	 */
	@Override
	public boolean onJsAlert(final WebView view, final String url, final String message, final JsResult result) {
		
		if (FUNCTION_DOM_LOAD_COMPLETE.equals(message)) {
			result.cancel();
			return true;
		}else if (message!=null&&message.trim().startsWith(HookConstants.ALERT_HOOK_FUNCTION)) {
			Log.w(TAG,message);
			result.cancel();
			return true;
		}
		
		return super.onJsAlert(view, url, message, result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.webkit.WebChromeClient#onConsoleMessage(java.lang.String, int, java.lang.String)
	 */
	@Override
	public void onConsoleMessage(final String message, final int lineNumber, final String sourceID) {
	    StringBuffer sb=new StringBuffer();
	    sb.append(message).append(" ").append(lineNumber).append(" ").append(sourceID);
		Log.i(TAG,sb.toString() );
	}
	
}
