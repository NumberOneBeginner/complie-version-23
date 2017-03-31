package com.hsbc.greenpacket.util.actions;

import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;

import com.hsbc.greenpacket.hook.Hook;
import com.hsbc.greenpacket.util.DeviceIDUtil;
import com.hsbc.greenpacket.util.StringUtil;

/**
 * @Description this Hook("hsbc://function=GetEncryptedDeviceId&callbackjs=YYY") used for 
 * 		bind logon account.
 * @author Cherry
 * @return device id which encrypted by SHA512
 * @date 2013-12-13
 */
public class GetEncryptedDeviceIdAction extends HSBCURLAction {
	
	private static final String TAG = "GetEncryptedDeviceId";

    @Override
	public void execute(Context context, WebView webview, Hook hook) {
		//encrypted device id
		String encryptDeviceId = DeviceIDUtil.generateEncryptedDeviceID(context);
		
		try {
			Map<String,String> map=this.getParams();
			if(map==null){
				throw new HookException();
			}
			hook.setMap(map);
			String callbackJs = map.get(HookConstants.CALLBACK_JS);
			
			if(StringUtil.IsNullOrEmpty(encryptDeviceId) || StringUtil.IsNullOrEmpty(callbackJs)){
				Log.e(TAG,"encryptedDeviceId:"+encryptDeviceId);
				throw new HookException("request parameter missing");
			}
			String script = HSBCURLAction.getCallbackJs(callbackJs, encryptDeviceId);
    		Log.d(TAG,"excute data script:"+script);
    		loadUrlInMainThread(webview, script);
		} catch (HookException e) {
			executeHookAPIFailCallJs(webview);
			Log.e(TAG,"Fail to execute the hook:",e);
		}
	}
}
