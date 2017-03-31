/*
 * COPYRIGHT. HSBC HOLDINGS PLC 2014. ALL RIGHTS RESERVED.
 *
 * This software is only to be used for the purpose for which it has been
 * provided. No part of it is to be reproduced, disassembled, transmitted,
 * stored in a retrieval system nor translated in any human or computer
 * language in any way or for any other purposes whatsoever without the
 * prior written consent of HSBC Holdings plc.
 */
package com.none.Plugin.greenLaisee;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.megvii.facepp.sdk.Facepp;
import com.megvii.licencemanage.sdk.LicenseManager;
import com.none.face.util.ConUtil;
import com.none.face.util.ICamera;
import com.none.face.util.Util;
import com.none.staff.activity.OpenglActivity;
import com.none.staff.activity.StaffApplication;

/**
 * <p>
 * <b> TODO : Insert description of the class's responsibility/role. </b>
 * </p>
 */
public class OpenFacePlugin extends CordovaPlugin {
	StaffApplication application;
	public final static String OPEN_FACE_ACTION = "scanResult";
	CallbackContext callbackContext;
	private LicenseManager licenseManager;
	private HashMap<String, Integer> resolutionMap;
	private ArrayList<HashMap<String, Integer>> cameraSize;
	private String staffID;
	private Activity activity;
	private CordovaWebView webView;
	public OpenFacePlugin() {
		// TODO Auto-generated constructor stub
	}

	// com.megvii.facepp.sdk.jni.FaceApi.nativeGetApiName();
	@Override
	public boolean execute(String action, org.json.JSONArray args,
			CallbackContext callbackContext) throws org.json.JSONException {
		this.callbackContext = callbackContext;
		staffID = args.getString(0);
		if (action.equals(OPEN_FACE_ACTION)) {
//			activity = cordova.getActivity();
//			cameraSize = ICamera.getCameraPreviewSize(0);
//			Log.e("www", "xs"+cameraSize);
//			resolutionMap = cameraSize.get(0);
			network();
			return true;
		}
		return false;

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == 1&&requestCode==0) {
			String faceData = intent.getStringExtra("faceW");
//			faceData = faceData.replace("\"","\\\"");
			 JSONObject o;
			try {
				o = new JSONObject(faceData);
				PluginResult mPlugin = new PluginResult(PluginResult.Status.OK, o);
				mPlugin.setKeepCallback(true);
				callbackContext.sendPluginResult(mPlugin);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		if (resultCode == 2&&requestCode==0) {
			openMenu();
		}
	}

	private void network() {
		licenseManager = new LicenseManager(activity);
		 licenseManager.setAuthTime(Facepp.getApiExpication(activity) * 1000);
		// licenseManager.setAgainRequestTime(againRequestTime);
		String uuid = ConUtil.getUUIDString(activity);
		long[] apiName = { Facepp.getApiName() };
		String content = licenseManager.getContent(uuid,
				LicenseManager.DURATION_30DAYS, apiName);
		String errorStr = licenseManager.getLastError();
		// Log.w("ceshi", "getContent++++errorStr===" + errorStr);
		boolean isAuthSuccess = licenseManager.authTime();
		// Log.w("ceshi", "isAuthSuccess===" + isAuthSuccess);
		if (isAuthSuccess) {
			authState(true);
		} else {
			AsyncHttpClient mAsyncHttpclient = new AsyncHttpClient();
//			 String url = "https://api.megvii.com/megviicloud/v1/sdk/auth";
			String url = "https://api-cn.faceplusplus.com/sdk/v1/auth";
			RequestParams params = new RequestParams();
			params.put("api_key", Util.API_KEY);
			params.put("api_secret", Util.API_SECRET);
			params.put("auth_msg", content);
			mAsyncHttpclient.post(url, params, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						byte[] responseByte) {
					String successStr = new String(responseByte);
					boolean isSuccess = licenseManager.setLicense(successStr);
					if (isSuccess) {
						// Log.w("ceshi1", "content:" + 1);
						authState(true);
					} else {
						// Log.w("ceshi1", "content:" + 2);
						authState(false);
					}
					String errorStr = licenseManager.getLastError();
					// Log.w("ceshi3", "setLicense++++errorStr===" + errorStr);
				}

				@Override
				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					error.printStackTrace();
					// Log.w("ceshi2", "setLicense++++errorStr===" +statusCode+
					// "      "+error.getMessage());
					authState(false);
				}
			});
		}

	}

	private void authState(boolean isSuccess) {
		CordovaInterface cordova = this.cordova;
        int min_face_size = Integer.parseInt("200");
        int detection_interval = Integer.parseInt("25");
		if (isSuccess) {
			Intent i = new Intent(activity, OpenglActivity.class);
			i.putExtra("staffId", staffID);
			i.putExtra("isdebug", false).putExtra("isBackCamera", true)
					.putExtra("isStartRecorder", false).putExtra("faceSize", min_face_size)
					.putExtra("interval", detection_interval)
					.putExtra("resolution", resolutionMap) 
					.putExtra("ROIDetect", false)// huakuang
					.putExtra("isFaceProperty", false);
//			activity.overridePendingTransition(0, 0);
			cordova.startActivityForResult((OpenFacePlugin) this, i, 0);
		} else {

			return;
		}
	}
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        this.activity = cordova.getActivity();
        this.webView = webView;
    }
    
//    showMenu()
    @JavascriptInterface
    private void openMenu() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:showMenu()");
            }
        });
    }

	
	
}
