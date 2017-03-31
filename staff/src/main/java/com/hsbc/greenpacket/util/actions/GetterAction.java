package com.hsbc.greenpacket.util.actions;

import java.util.Map;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebView;

import com.hsbc.greenpacket.hook.Hook;
import com.hsbc.greenpacket.util.AlgorithmUtil;
import com.hsbc.greenpacket.util.NameValueStore;
import com.hsbc.greenpacket.util.StringUtil;

/**
 * hsbc://function=Getter&key=<key>&callbackjs=<javascript function for callback>
 * 
 * @author 43580172
 * 
 */
public class GetterAction extends HSBCURLAction {

    @Override
    public void execute(Context context, WebView webview, Hook hook) {
        try {
            Map<String, String> map = this.getParams();
            if (map == null) {
                throw new HookException();
            }
            String key = map.get(HookConstants.KEY);
            String callbackJs = map.get(HookConstants.CALLBACK_JS);
            if (StringUtil.IsNullOrEmpty(key) || StringUtil.IsNullOrEmpty(callbackJs)) {
                throw new HookException("request parameter missing");
            }
            NameValueStore store = new NameValueStore(context);
            String value = store.getAttribute(key);
            //Tracy add encrypt for ISR advice
            if(!StringUtil.IsNullOrEmpty(value)){
                String aesKey=AlgorithmUtil.gen256Key(HookConstants.SECURE_KEY);
                value=new String(AlgorithmUtil.aesDecrypt(aesKey,Base64.decode(value, Base64.NO_WRAP)));
            }
            loadUrlInMainThread(webview, getCallbackJs(callbackJs, value));
        } catch (Exception e) {
            executeHookAPIFailCallJs(webview);
            Log.e("Getter","Fail to execute the hook:"+ e.getMessage());
        }
    }
}
