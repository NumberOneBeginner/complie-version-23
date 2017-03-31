package com.hsbc.greenpacket.util.actions;

import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;

import com.hsbc.greenpacket.hook.Hook;
import com.hsbc.greenpacket.util.EntityUtil;
import com.hsbc.greenpacket.util.KeyValueMemoryStore;
import com.hsbc.greenpacket.util.StringUtil;

public class VolatileGetterAction extends HSBCURLAction {
	private final static String ENTITY_ID_ISNULL_VOLATILE_GETTER = "entityIdIsNull";
	
	@Override
	public void execute(Context context, WebView webview, Hook hook) {
		try {
			Map<String, String> map=this.getParams();
			if(map==null){
				throw new HookException();
			}
			String key = map.get(HookConstants.KEY);
			String callbackJs=map.get(HookConstants.CALLBACK_JS);
			if(StringUtil.IsNullOrEmpty(key)||StringUtil.IsNullOrEmpty(callbackJs)){
				throw new HookException("request parameter missing");
			}
			String entityRegionalId = EntityUtil.getSavedEntityId(context);
			if(StringUtil.IsNullOrEmpty(entityRegionalId)){
				entityRegionalId=ENTITY_ID_ISNULL_VOLATILE_GETTER;
			}
			String cacheValue = KeyValueMemoryStore.getmEntityData(entityRegionalId,key);
			if(StringUtil.IsNullOrEmpty(cacheValue)){
				cacheValue = "";
			}
			loadUrlInMainThread(webview,getCallbackJs(callbackJs,cacheValue));
		}catch(Exception e){
			executeHookAPIFailCallJs(webview);
            Log.e("VolatileGetter","Fail to execute the hook:"+ e.getMessage());
		}
	}

}
