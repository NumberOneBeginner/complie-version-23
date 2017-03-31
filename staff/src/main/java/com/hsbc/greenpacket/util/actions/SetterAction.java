package com.hsbc.greenpacket.util.actions;

import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.util.Base64;
import android.webkit.WebView;

import com.hsbc.greenpacket.hook.Hook;
import com.hsbc.greenpacket.util.AlgorithmUtil;
import com.hsbc.greenpacket.util.NameValueStore;
import com.hsbc.greenpacket.util.StringUtil;

/**
 * Update to extends HsbcGetJsDataAction as to inplement the IHsbcUrlAction
 * 
 * @author York Y K LI
 * 
 */
public class SetterAction extends HsbcGetJsDataAction {

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
    
    /**
     * York[2012/11/22] implement dataProcess
     */
    @Override
    public void dataProcess(Context context, WebView webview, Handler mHandler, String dataValue, JSONObject jo) {
        try {
            String callbackJs = jo.getString(HookConstants.CALLBACK_JS);
            String key = jo.getString(HookConstants.KEY);
            if (key != null) {
                //Tracy add encrypt for ISR advice
                save(key,dataValue,context);
                if (!StringUtil.IsNullOrEmpty(callbackJs)) {
                    loadUrlInMainThread(webview,HSBCURLAction.getCallbackJs(callbackJs, dataValue));
                }
            }
        } catch (Exception e) {
            this.executeHookAPIFailCallJs(webview);
        }

    }
    public static void save(String key,String dataValue,Context context) throws Exception{
        String aesKey=AlgorithmUtil.gen256Key(HookConstants.SECURE_KEY);
        if(!StringUtil.IsNullOrEmpty(dataValue)){
            byte[] byteA=AlgorithmUtil.aesEncrypt(aesKey,dataValue.getBytes());
            dataValue=Base64.encodeToString(byteA, Base64.NO_WRAP);
        }
        NameValueStore store = new NameValueStore(context);
        store.setAttribute(key, dataValue);
    }

}
