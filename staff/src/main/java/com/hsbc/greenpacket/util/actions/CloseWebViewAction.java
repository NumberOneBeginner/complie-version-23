package com.hsbc.greenpacket.util.actions;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.webkit.WebView;

import com.hsbc.greenpacket.hook.Hook;

public class CloseWebViewAction extends HSBCURLAction {
	private static final String TAG = "CloseWebViewAction";

    @Override
	public void execute(final Context context, WebView webview,Hook hook) {
		try {
		    ((Activity)context).finish();
		} catch(Exception e){
		    executeHookAPIFailCallJs(webview);
			Log.e(TAG,"Fail to execute the hook:"+e.getMessage());
		}
	}

}