package com.none.Plugin.About;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import com.none.staff.activity.AboutActivity;

import android.content.Intent;


public class ShowAboutPlugin extends CordovaPlugin {
	
	public static final String SHOW_ABOUT = "showabout" ;

	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		
		if(SHOW_ABOUT.equals(args)){
			Intent intent = new Intent(cordova.getActivity(),AboutActivity.class) ;
			cordova.getActivity().startActivity(intent) ;
			return true ;
		}
		
		return false ;
		
	}
}
