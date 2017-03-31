package com.hsbc.greenpacket.util.actions;

import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;

import com.hsbc.greenpacket.activities.MainBrowserActivity;
import com.hsbc.greenpacket.hook.Hook;
/**
 * Enable/disable drag  gesture detector. <br/>
 * hsbc://function=UnregisterGestureListener&callbackjs=YYY
 * 
 * @author Karson Li 
 * @date [13 Dec 2013]
 * 
 */
public class UnregisterGestureListenerAction extends HSBCURLAction {
	@Override
	public void execute(Context context, WebView webview, Hook hook) {
		// TODO Auto-generated method stub
		String callbackJs = null;
		try {
			Map<String, String> map=this.getParams();
			callbackJs=map.get(HookConstants.CALLBACK_JS);
			
			boolean enableDrag = false;
			if (context instanceof MainBrowserActivity) {
                ((MainBrowserActivity) context).setEnableDrag(enableDrag);
			}else{
				throw new Exception("Request context is not MainBrowserActivity");
			}
			RegisterGestureListenerAction.cleanRegisterGestureListenerEvnetjs();
			 String script = HSBCURLAction.getCallbackJs(callbackJs, HSBCURLAction.getErrorResponseJson(HookConstants.SUCCESS));
	         webview.loadUrl(script);
		}catch(Exception e){
            Log.e("UnregisterGesture","Fail to execute the hook:"+ e.getMessage());;
			if(callbackJs == null){
				executeHookAPIFailCallJs(webview);
			}else {
                String script =
                        HSBCURLAction
                            .getCallbackJs(callbackJs, HSBCURLAction.getErrorResponseJson(HookConstants.API_EXCUTE_ERROR_CODE));
                    webview.loadUrl(script);
                }
		}
	}
	
	

}
