package com.hsbc.greenpacket.util.actions;

import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;

import com.hsbc.greenpacket.activities.MainBrowserActivity;
import com.hsbc.greenpacket.hook.Hook;
/*Hook for unregister long press
 * hsbc://function=UnregisterLongPressListener&callbackjs=ZZZ
 * Karson Li[14 Dec 2013]
 */
public class UnregisterLongPressListenerAction extends HSBCURLAction {
	@Override
	public void execute(Context context, WebView webview, Hook hook) {
		String callbackjs = null;
		try {
			Map<String, String> map=this.getParams();
			callbackjs = map.get(HookConstants.CALLBACK_JS);
		
			if (context instanceof MainBrowserActivity) {
                ((MainBrowserActivity) context).setEnableLongPress(false);
			}else{
				throw new Exception("Request context is not MainBrowserActivity");
			}
			RegisterLongPressListenerAction.clearLocationOfLongPress();
			String script = HSBCURLAction.getCallbackJs(callbackjs, HSBCURLAction.getErrorResponseJson(HookConstants.SUCCESS));
			webview.loadUrl(script);
			
		} catch (Exception e) {
			if(callbackjs == null){
				executeHookAPIFailCallJs(webview);
			}else{
				 String script =
	                        HSBCURLAction
	                            .getCallbackJs(callbackjs, HSBCURLAction.getErrorResponseJson(HookConstants.API_EXCUTE_ERROR_CODE));
	                    webview.loadUrl(script);
			}
            Log.e("UnregisterLongPress","Fail to execute the hook:"+ e.getMessage());
		}
	}
	
}
