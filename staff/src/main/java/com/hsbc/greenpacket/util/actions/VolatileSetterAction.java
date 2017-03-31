package com.hsbc.greenpacket.util.actions;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.webkit.WebView;

import com.hsbc.greenpacket.hook.Hook;
import com.hsbc.greenpacket.util.EntityUtil;
import com.hsbc.greenpacket.util.KeyValueMemoryStore;
import com.hsbc.greenpacket.util.StringUtil;

public class VolatileSetterAction extends HsbcGetJsDataAction {
	private final static String ENTITY_ID_ISNULL_VOLATILE_SETTER = "entityIdIsNull";
	
	@Override
    public void validate(Context context, WebView webview, Hook hook) throws HookException {
        // TODO Auto-generated method stub
        super.validate(context, webview, hook);
        Map<String, String> map=this.getParams();
        String key = map.get(HookConstants.KEY);
        if(StringUtil.IsNullOrEmpty(key)){
            throw new HookException("request parameter missing");
        }
    }

	
    @Override
    public void dataProcess(Context context, WebView webview, Handler mHandler, String dataValue, JSONObject jo)throws JSONException {
        String callbackJs = null;
        if (jo.has(HookConstants.CALLBACK_JS)) {
            callbackJs = jo.getString(HookConstants.CALLBACK_JS);
        }
        String key = jo.getString(HookConstants.KEY);
        String entityRegionalId = EntityUtil.getSavedEntityId(context);
        if (!StringUtil.IsNullOrEmpty(key)) {
            if (StringUtil.IsNullOrEmpty(entityRegionalId)) {
                entityRegionalId = ENTITY_ID_ISNULL_VOLATILE_SETTER;
            }
            KeyValueMemoryStore.setmData(key, dataValue, entityRegionalId);
            if (!StringUtil.IsNullOrEmpty(callbackJs)) {
                loadUrlInMainThread(webview,HSBCURLAction.getCallbackJs(callbackJs,dataValue));
            }
        }
	}

}
