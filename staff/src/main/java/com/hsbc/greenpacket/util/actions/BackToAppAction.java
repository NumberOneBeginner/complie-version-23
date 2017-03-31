package com.hsbc.greenpacket.util.actions;

import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;

import com.hsbc.greenpacket.activities.HSBCActivity;
import com.hsbc.greenpacket.hook.Hook;
import com.hsbc.greenpacket.util.StringUtil;

public class BackToAppAction extends HSBCURLAction {
	private static final String TAG = "BackToAppAction";

    @Override
	public void execute(final Context context, WebView webview,Hook hook) {
		try {
			Map<String, String> map=this.getParams();
			if(map==null){
				throw new HookException();
			}
			String id=map.get(HookConstants.ID);
			String lang=map.get(HookConstants.LANG);
			if(StringUtil.IsNullOrEmpty(id) ||
			   StringUtil.IsNullOrEmpty(lang)){
				throw new HookException("request parameter missing");
			}			
			if("home".equalsIgnoreCase(id)){
				((HSBCActivity)context).finish();
			}
			/**	for mobile 1.5 dont need handle the lang parameter		
			if(lang!=null){
				String savedLocale = EntityUtil.getSavedEntityLocale(context);
				if(!lang.equals(savedLocale)){
					Locale locale= EntityUtil.getLocale(lang);
					EntityUtil.changeLocale(context, locale);
				}
			}
			**/
		} catch(Exception e){
		    executeHookAPIFailCallJs(webview);
			Log.e(TAG,"Fail to execute the hook:"+e.getMessage());
		}
	}

}