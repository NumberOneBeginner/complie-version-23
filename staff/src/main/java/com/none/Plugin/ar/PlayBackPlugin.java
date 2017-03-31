/*
 * COPYRIGHT. HSBC HOLDINGS PLC 2014. ALL RIGHTS RESERVED.
 *
 * This software is only to be used for the purpose for which it has been
 * provided. No part of it is to be reproduced, disassembled, transmitted,
 * stored in a retrieval system nor translated in any human or computer
 * language in any way or for any other purposes whatsoever without the
 * prior written consent of HSBC Holdings plc.
 */
package com.none.Plugin.ar;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.none.staff.activity.zbarScanner.AnyEventType;
import com.none.staff.activity.zbarScanner.TestView;
import com.vuforia.VideoPlayback.app.VideoPlayback.VideoPlayback;

import de.greenrobot.event.EventBus;

/**
 * <p><b>
 * TODO : Insert description of the class's responsibility/role.
 * </b></p>
 */
public class PlayBackPlugin extends CordovaPlugin {
	public final static String PLAY_BACK_ACTION="showVideoPlaybackView";
	public final static String Zbar="showVideoPlaybackViewAndReceiveScan";
	CallbackContext callbackContext;
	CallbackContext callbackContextZbar;
	/**
	 * <p><b>
	 * TODO : Insert description of the method's responsibility/role.
	 * </b></p>
	 *
	 */
	public PlayBackPlugin() {

	}

	@Override
	public boolean execute(String action, org.json.JSONArray args,CallbackContext callbackContext) throws org.json.JSONException{
		Log.d("test", "AR ececute");
		this.callbackContext= callbackContext;
		if(action.equals(PLAY_BACK_ACTION)){
			Intent intent = new Intent(cordova.getActivity(),TestView.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			cordova.getActivity().overridePendingTransition(0, 0);
			cordova.startActivityForResult((PlayBackPlugin)this, intent, 200);

			return true;
		}else if(action.equals(Zbar)){
			EventBus.getDefault().register(this);
			callbackContextZbar=callbackContext;
			return true;
		}
		return false;

	}

	@Override
	public void onActivityResult(int requestCode,int resultCode,Intent intent){
		super.onActivityResult(requestCode, resultCode, intent);
		callbackContext.success();
	}

	public void onEvent(AnyEventType event) {

		if(event.getMsg().equals("error")){

			callbackContext.error("error");
			callbackContextZbar.error("error");

			Log.i("1111111", "callbackContext callbackContext callbackContext");

		}else{


			callbackContextZbar.success(event.getMsg());

			Log.i("1111111", "callbackContextZbar callbackContextZbar callbackContextZbar");

		}


	}  
	@Override
	public void onResume(boolean multitasking) {

		super.onResume(multitasking);

		EventBus.getDefault().unregister(this);

	}



}
