package com.hsbc.greenpacket.util.web;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hsbc.greenpacket.util.ActivityUtil;
import com.hsbc.greenpacket.util.actions.HookConstants;
import com.hsbc.greenpacket.util.actions.MailToAction;

/**
 * Implementation of {@link WebViewClient} for HSBC mobile side.
 * 
 */
public class HSBCWebClient extends EnchancedWebViewClient {
	private static final String TAG = "HSBCWebClient";
    /**
	 * 
	 */
	private Activity owningActivity;
	// Project Cobra [Jul-2013] Promote core extendability
	protected final int progressDialogRef;
	// Project Cobra [Jul-2013] Promote core extendability
	protected final int noConnectionDialogRef;
	private boolean allowAllSSL = false;

	/**
	 * @param owningActivity
	 *            The activity displaying the webview this {@link WebViewClient} is been used for. This is used to display and hide dialogs
	 *            as required.
	 * @param progressDialogRef
	 *            The reference will be passed to {@link Activity#showDialog(int)} to display a "loading" dialog.
	 * @param noConnectionDialogRef
	 *            The reference will be passed to {@link Activity#showDialog(int)} to display a dialog if the no connection is available.
	 * @param invalidVersionDialogReg
	 *            The reference will be passed to {@link Activity#showDialog(int)} to display a dialog if the web site is the incorrect
	 *            version.
	 */
	public HSBCWebClient(final Activity owningActivity, final int progressDialogRef, final int noConnectionDialogRef,
			final int invalidVersionDialogRef) {
		this.owningActivity = owningActivity;
		this.progressDialogRef = progressDialogRef;
		this.noConnectionDialogRef = noConnectionDialogRef;
		allowAllSSL = true;
	}

	/**
	 * Indicates that the {@link Activity} been used to show dialogs has been destroyed. This is added as its possible for the web client to
	 * be informed of events even if the {@link Activity} owning the {@link WebView} this is attached to has been destroy. Trying to
	 * show/hide dialogs at this point results in an error been thrown even though the app has been closed.
	 **/
	public void ownerDestroyed() {
		// Synchronized around this to ensure we don't cause owning activity to be null while it is been used
		synchronized (this) {
			this.owningActivity = null;
		}
	}

	/**
	 * Calls {@link Activity#showDialog(int)} if the owning activity hasn't been destroyed.
	 * 
	 * @param dialogId
	 */
	public void showDialog(final int dialogId) {
		// Synchronized around this to ensure we call show Dialog only on a valid activity.
		synchronized (this) {
			if (this.owningActivity != null) {
				this.owningActivity.runOnUiThread(new Runnable() {
					public void run() {
						try{
							HSBCWebClient.this.owningActivity.showDialog(dialogId);
						}catch(Exception e){
							Log.e(TAG,"showDialog error");
						}
					}
				});
			}
		}
	}

	/**
	 * Calls {@link Activity#hideDialog(int)} if the owning activity hasn't been destroyed.
	 * 
	 * @param dialogId
	 */
	public void dismissDialog(final int dialogId) {
		// Synchronized around this to ensure we call dismiss Dialog only on a valid activity.
		synchronized (this) {
			if (this.owningActivity != null) {
				this.owningActivity.runOnUiThread(new Runnable() {
					public void run() {
						try{
							HSBCWebClient.this.owningActivity.dismissDialog(dialogId);
						}catch(Exception e){
							Log.e(TAG,"dismissDialog error");
						}
					}
				});
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.webkit.WebViewClient#onPageStarted(android.webkit.WebView, java.lang.String, android.graphics.Bitmap)
	 */
	@Override
	public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
		showDialog(this.progressDialogRef);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.webkit.WebViewClient#onPageFinished(android.webkit.WebView, java.lang.String)
	 */
	@Override
	public void onPageFinished(final WebView view, final String url) {
		dismissDialog(this.progressDialogRef);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.webkit.WebViewClient#onReceivedError(android.webkit.WebView, int, java.lang.String, java.lang.String)
	 */
	@Override
	public void onReceivedError(final WebView view, final int errorCode, final String description, final String failingUrl) {
		// TODO: When an error occurs the default error page is displayed in the webview. This isn't exactly something we want to be
		// showing to the user. Need to find a way to work around this.
		switch (errorCode) {
		case ERROR_CONNECT:
			showDialog(this.noConnectionDialogRef);
			break;
		case ERROR_HOST_LOOKUP:
			showDialog(this.noConnectionDialogRef);
			break;
		default:
			break;
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hsbc.util.LoggedWebViewClient#onReceivedSslError(android.webkit.WebView, android.webkit.SslErrorHandler,
	 * android.net.http.SslError)
	 */
	@Override
	public void onReceivedSslError(final WebView view, final SslErrorHandler handler, final SslError error) {
		// TODO: Implement a dialog around this choice, however this should only be a issue when using internal test servers as the actual
		// live web server SHOULD have a correct SSL Cert.
		if(allowAllSSL){
			handler.proceed();
		}else{
			Log.i(TAG,"The SSL Cert is not valid!");
			handler.cancel();
		}
	}

	
	/*
	 * Just use those code in version 1.3
	 * 
	 * @see android.webkit.WebViewClient#shouldOverrideUrlLoading(android.webkit.WebView, java.lang.String)
	 */
	@Override
	public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
		// As we only want to do something with the url if the owning activity is still running (i.e this view this client is attached to is
		// still displayed), if it's not we can do nothing.
		if (this.owningActivity == null) {
			return false;
		}
		if (url.startsWith(HookConstants.TEL)) {
		    return ActivityUtil.startCall(owningActivity, url);
		}else if (url.startsWith(HookConstants.MAIL_TO)) {
			MailToAction action =new MailToAction();
			action.execute(this.owningActivity, url);
			return true; 
		}else {
			return super.shouldOverrideUrlLoading(view, url);
		}
	}
	
}