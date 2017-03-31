package com.none.Plugin.iconUpLoad;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;

import com.none.face.util.FaceUpLoadUtil;
import com.none.staff.uploadIcon.IconUpLoadActivity;

public class IconUpLoadPlugin extends CordovaPlugin {
	private Activity activity;
	private String userId,imageUrl;
	FaceUpLoadUtil mFaceUpLoadUtil;
	private CallbackContext callbackContext;
	private final static String ICON_UP_LOAD_ACTION = "iconUpLoad";
	
	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		this.callbackContext = callbackContext;
		if(args!=null){
			if(args.get(0)!=null){
				userId = args.getString(0);
			}
			if(args.get(1)!=null){
				imageUrl = args.getString(1);
			}else{
				imageUrl = " ";
			}	
		}
		CordovaInterface cordova = this.cordova;
		if (action.equals(ICON_UP_LOAD_ACTION)) {
			Intent intent = new Intent(activity,IconUpLoadActivity.class);
			intent.putExtra("userId", userId);
			intent.putExtra("imageUrl", imageUrl);
//			LOG.e("WW", "qq"+imageUrl);
			cordova.startActivityForResult((IconUpLoadPlugin)this, intent, 0);
			return true;
		} 
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == 1&&requestCode==0) {
			String imageUrl = intent.getStringExtra("imageUrl");
//			faceData = faceData.replace("\"","\\\"");
			PluginResult mPlugin = new PluginResult(PluginResult.Status.OK, imageUrl);
			mPlugin.setKeepCallback(true);
			callbackContext.sendPluginResult(mPlugin);
		}
		
	}


	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		this.activity = cordova.getActivity();
		this.webView = webView;
	}


}
