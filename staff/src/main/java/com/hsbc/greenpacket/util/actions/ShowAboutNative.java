package com.hsbc.greenpacket.util.actions;

import java.util.Map;

import org.apache.cordova.LOG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.WebView;

import com.hsbc.greenpacket.hook.Hook;

import com.none.staff.activity.AboutActivity;

public class ShowAboutNative extends HSBCURLAction{
	

	@Override
	public void execute(Context context, WebView webview, Hook hook) {
		
		try {
			 Map<String, String> map = this.getParams();
			 Log.e("map", map.toString()) ;
			 String key = map.get(HookConstants.SHOW_ABOUT_NATIVE) ;
			 Log.e("key", key) ;
//			 if(){
//				 
//			 }
			 Intent intent = new Intent(context,AboutActivity.class) ;
			 context.startActivity(intent) ;
		} catch (Exception e) {
		}
		
	}

}
