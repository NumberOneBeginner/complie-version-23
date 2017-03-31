package com.none.PlayVideo;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;

public class  PlayVideo  extends CordovaPlugin{
	

	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		String url = args.getString(0);


		if(action.equals("presentMediaMp4")){
			playVideo(url);
		}
		return  true;
	}

	private void playVideo(String url) {
		Intent it = new Intent(cordova.getActivity().getBaseContext(),playActivity.class);  
		it.putExtra("url", url);
		cordova.getActivity().startActivity(it);	
		
		
	}


}
