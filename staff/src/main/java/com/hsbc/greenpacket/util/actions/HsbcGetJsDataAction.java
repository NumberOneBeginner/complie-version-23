package com.hsbc.greenpacket.util.actions;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;

import com.hsbc.greenpacket.hook.Hook;
import com.hsbc.greenpacket.util.StringUtil;

public abstract class HsbcGetJsDataAction extends HSBCURLAction implements IHsbcUrlAction {
    protected Context context = null;
    protected WebView webview = null;
    
    public void validate(Context context, WebView webview, Hook hook) throws HookException {
        Map<String, String> map = this.getParams();
 Log.e("HsbcGetJsDataAction validate", map.toString()) ;       
        if (map == null) {
            throw new HookException();
        }
        String datajs = map.get(HookConstants.DATA_JS);
        String callbackJs = map.get(HookConstants.CALLBACK_JS);
        if (StringUtil.IsNullOrEmpty(datajs)||StringUtil.IsNullOrEmpty(callbackJs)) {
            throw new HookException("request parameter missing");
        }
    }

    @Override
    public void execute(Context context, WebView webview, Hook hook) {
        try {
            validate(context, webview, hook);
            /**Save the execute context and webview to prevent them override**/
            this.context=context;
            this.webview=webview;
            Map<String, String> map = this.getParams();
            hook.setMap(map);
            String js = executeDataJs(map);
            loadUrlInMainThread(webview,js);
            hook.setWebview(webview);
        } catch (Exception e) {
            executeHookAPIFailCallJs(webview);
            Log.e("","Fail to execute the hook:{}", e);
        }
    }

    @Override
    public void dataProcess(Context context, WebView webview, Handler mHandler, String dataValue, JSONObject jo, Hook hook)
        throws JSONException,HookException {
        dataProcess(this.context, this.webview, mHandler, dataValue, jo);
    }

    public abstract void dataProcess(Context context, WebView webview, Handler mHandler, String dataValue, JSONObject jo)
        throws JSONException,HookException;

    @Override
    public void onActionResult(Context context, Handler mHandler, int resultCode,Intent data) {
        // TODO Auto-generated method stub  
    }
    

}
