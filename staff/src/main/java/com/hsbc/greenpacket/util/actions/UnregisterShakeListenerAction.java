package com.hsbc.greenpacket.util.actions;

import java.util.Map;

import org.apache.cordova.LOG;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;

import com.hsbc.greenpacket.activities.MainBrowserActivity;
import com.hsbc.greenpacket.hook.Hook;

/**
 * hsbc://function=UnregisterShakeListener&callbackjs=YYY
 * 
 * @author Tracy Wang
 * 
 */
public class UnregisterShakeListenerAction extends HSBCURLAction {

    @Override
    public void execute(Context context, WebView webview, Hook hook) {
        String callbackJs = null;
        try {
            Map<String, String> map = this.getParams();
            callbackJs = map.get(HookConstants.CALLBACK_JS);

            if (context instanceof MainBrowserActivity) {
                ((MainBrowserActivity) context).unregisterShakeListener();
                String script = HSBCURLAction.getCallbackJs(callbackJs, HSBCURLAction.getErrorResponseJson(HookConstants.SUCCESS));
                loadUrlInMainThread(webview, script);
            }
        } catch (Exception e) {
            if (callbackJs == null) {
                executeHookAPIFailCallJs(webview);
            } else {
                String script =
                    HSBCURLAction
                        .getCallbackJs(callbackJs, HSBCURLAction.getErrorResponseJson(HookConstants.API_EXCUTE_ERROR_CODE));
                loadUrlInMainThread(webview, script);
            }
            Log.e("UnregisterShake","Fail to execute the hook:"+ e.getMessage());
        }
    }

}
