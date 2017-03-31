package com.hsbc.greenpacket.util.actions;

import java.util.Map;

import android.content.Context;
import android.webkit.WebView;

import com.hsbc.greenpacket.hook.Hook;
import com.hsbc.greenpacket.util.EntityUtil;
import com.hsbc.greenpacket.util.StringUtil;

public class GetLocaleAction extends HSBCURLAction {
	@Override
	public void execute(Context context, WebView webview,Hook hook) {
		try {
			Map<String, String> map=this.getParams();
			if(map==null){
				throw new HookException();
			}
			hook.setMap(map);
			String callbackJs=map.get(HookConstants.CALLBACK_JS);
			String locale = null;
//			if(context.getResources().getBoolean(R.bool.production)){
//			    locale="en";
//			}else {
			    locale = getLocale(context);
			    if(StringUtil.IsNullOrEmpty(callbackJs)||StringUtil.IsNullOrEmpty(locale)){
			        throw new HookException("request parameter missing");
	            }
//			}
				
			//webview.loadUrl(getJavascript(callbackJs,new String[]{locale}));
			loadUrlInMainThread(webview,getCallbackJs(callbackJs,locale));
		}catch(Exception e){
			executeHookAPIFailCallJs(webview);
		}
	}

	public String getLocale(Context context) {
		return "en";
	}

}
