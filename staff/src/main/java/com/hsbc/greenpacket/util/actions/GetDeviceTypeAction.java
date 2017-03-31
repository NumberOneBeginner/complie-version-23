package com.hsbc.greenpacket.util.actions;

import java.util.Map;

import android.content.Context;
import android.webkit.WebView;

import com.hsbc.greenpacket.hook.Hook;
import com.hsbc.greenpacket.util.StringUtil;

public class GetDeviceTypeAction extends HSBCURLAction {

    private static final String DEVICE_TYPE_MOBILE = "M";
    private static final String DEVICE_TYPE_TABLET = "T";
	
	public void execute(Context context, WebView webview,Hook hook) {
		try {
			Map<String, String> map=this.getParams();
			if(map==null){
				throw new HookException();
			}
			hook.setMap(map);
			String callbackJs=map.get(HookConstants.CALLBACK_JS);
			String deviceType = getDeviceType(context);
			if(StringUtil.IsNullOrEmpty(callbackJs)||StringUtil.IsNullOrEmpty(deviceType)){
				throw new HookException("request parameter missing");
			}
			//webview.loadUrl(getJavascript(callbackJs,new String[]{locale}));
			loadUrlInMainThread(webview,getCallbackJs(callbackJs,deviceType));
		}catch(Exception e){
			executeHookAPIFailCallJs(webview);
		}
	}

    private String getDeviceType(Context context) {
//    	return context.getResources().getBoolean(R.bool.isSupportOrientationChange)? DEVICE_TYPE_TABLET : DEVICE_TYPE_MOBILE;
        return DEVICE_TYPE_MOBILE;
    }
}
