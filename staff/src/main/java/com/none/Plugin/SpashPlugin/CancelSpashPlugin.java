package com.none.Plugin.SpashPlugin;

import java.io.File;
import java.io.IOException;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.none.Push.MyPushEvent;
import com.none.staff.event.AppEvent;
import com.none.staff.utils.FileUtils;

import de.greenrobot.event.EventBus;

/**
 * 取消splash插件
 * @author willis
 *
 */
public class CancelSpashPlugin extends CordovaPlugin {

	private CallbackContext pushCallbackContext;
	private File sdCardDir;
	private File saveFile;
	
	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {

		if (action.equals("splashHide")) {
			EventBus.getDefault().post(new AppEvent(1));
			callbackContext.success("hide sucdess");
			return true;
		}else if(action.equals("pushJumpToPage")){

			this.pushCallbackContext = callbackContext;

			EventBus.getDefault().register(this,"setPushMessage",MyPushEvent.class) ;

			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				sdCardDir = Environment.getExternalStorageDirectory();
			}
			saveFile = new File(sdCardDir, "staff.txt"); 
			feach();			
			return true;
		}
		return true;
	}

private void feach() {

		try {

			String jsonString = FileUtils.readTextFile(saveFile);

			if(!jsonString.equals("")&&jsonString!=null){

				PluginResult result;
				try {
					result = new PluginResult(PluginResult.Status.OK,
							new JSONObject(jsonString));

					result.setKeepCallback(true);
					pushCallbackContext.sendPluginResult(result);
					FileUtils.deleteFile(saveFile);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setPushMessage(MyPushEvent event)
	{
		feach();
	}



}
