package com.none.Push;

import java.io.File;
import java.io.IOException;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;

import com.none.Push.uploadImage.PhotoPickerIntent;
import com.none.Push.uploadImage.domains.utils.SelectModel;
import com.none.Push.uploadVideo.VideoPickerIntent;
import com.none.staff.activity.zbarScanner.AnyEventType;
import com.none.staff.utils.FileUtils;

import de.greenrobot.event.EventBus;

public class PushPlugin extends CordovaPlugin {
	private String tag = "111";
	private CallbackContext pushCallbackContext;
	private File sdCardDir;
	private File saveFile;
	private CallbackContext ImageCallbackContext;

	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		this.pushCallbackContext = callbackContext;
		String user_Id = args.getString(0);
		if(action.equals("pushJumpToPage")){

			EventBus.getDefault().register(this,"setPushMessage",MyPushEvent.class) ;

						if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			sdCardDir = Environment.getExternalStorageDirectory();
							if (!sdCardDir.exists()) {
								boolean ret = sdCardDir.mkdirs();
							}
						}
			saveFile = new File(sdCardDir, "staff.txt"); 
			feach();			
			return true;
		}else if(action.equals("alertButtonShow")){

			String[] alerMessage = new String[]{};

			JSONArray alertButton = args.getJSONArray(0);

			for(int i=0;i<alertButton.length();i++){
				alerMessage[i] = alertButton.getString(i);
			}
			createAlertDialogBuilder()
			.setTitle(alerMessage[0])
			.setMessage(alerMessage[1])
			.setNegativeButton(alerMessage[2],new ButtonClickedListener("NOT"))
			.setPositiveButton(alerMessage[3], new ButtonClickedListener("YES"))
			.show();
			return true;
		}else if(action.equals("pushImagePage")){

			this.ImageCallbackContext = callbackContext;
			EventBus.getDefault().register(this);
			PhotoPickerIntent intent = new PhotoPickerIntent(cordova.getActivity());
			intent.setSelectModel(SelectModel.MULTI);
			intent.setShowCarema(true); 
			intent.setUserId(user_Id);
			cordova.getActivity().startActivity(intent);
			return true;

		}else if(action.equals("pushVideoPage")){
			this.ImageCallbackContext = callbackContext;
			EventBus.getDefault().register(this);
			VideoPickerIntent intent = new VideoPickerIntent(cordova.getActivity());
			intent.setSelectModel(SelectModel.MULTI);
			intent.setUserId(user_Id);
			cordova.getActivity().startActivity(intent);

			return true;
		}
		return true;
	}

	private void feach() {

		try {

			Log.d("MyPushMessageReceiver.class", "jsonString111111 :");
			String jsonString = FileUtils.readTextFile(saveFile);
			Log.d("MyPushMessageReceiver.class", "jsonString 222222 :"+jsonString);

			if(!jsonString.equals("")&&jsonString!=null){

				PluginResult result;
				try {
					result = new PluginResult(PluginResult.Status.OK,
							new JSONObject(jsonString));
					result.setKeepCallback(true);
					pushCallbackContext.sendPluginResult(result);
					FileUtils.deleteFile(saveFile);

				} catch (JSONException e) {
					e.printStackTrace();
				}		
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setPushMessage(MyPushEvent event)
	{
		Log.d("MyPushMessageReceiver.class", "setPushMessage");
		feach();
	}

	protected AlertDialog.Builder createAlertDialogBuilder() {

		return new AlertDialog.Builder(this.cordova.getActivity());	

	}


	private class ButtonClickedListener implements DialogInterface.OnClickListener {
		private String mShowWhenClicked;

		public ButtonClickedListener(String showWhenClicked) {
			mShowWhenClicked = showWhenClicked;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			
						if(which==-2){
			
							pushCallbackContext.success(0);
			
						}else{
			
							pushCallbackContext.success(1);
						}	
		}
	}

	public void onEvent(MyPushEvent event) {

		Log.i("onEvent :", "onEvent sucess sucess sucess");

		if(event.getFlag().equals("success")){
			ImageCallbackContext.success("sucess");
		}  

	}

			@Override
			public void onResume(boolean multitasking) {
		
				super.onResume(multitasking);
		
				EventBus.getDefault().unregister(this);
		
			}


}
