package com.hsbc.greenpacket.util.actions;

import java.util.Map;

import org.apache.cordova.LOG;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;

import com.hsbc.greenpacket.activities.MainBrowserActivity;
import com.hsbc.greenpacket.hook.Hook;
import com.hsbc.greenpacket.util.StringUtil;

/**
 * hsbc://function=RegisterShakeListener&eventjs=XXX&callbackjs=YYY
 * @author Tracy Wang
 *
 */
public class RegisterShakeListenerAction extends HSBCURLAction {

    private static String eventJs;

    public void execute(Context context, WebView webview, Hook hook) {
        String callbackJs = null;
        try {
            Map<String, String> map = this.getParams();
            eventJs = map.get(HookConstants.EVENT_JS);
            callbackJs = map.get(HookConstants.CALLBACK_JS);

            if (map == null || StringUtil.IsNullOrEmpty(eventJs)) {
                throw new HookException("request parameter missing");
            }

            if (context instanceof MainBrowserActivity) {
                ((MainBrowserActivity) context).registerShakeListener();
                String script = HSBCURLAction.getCallbackJs(callbackJs, HSBCURLAction.getErrorResponseJson(HookConstants.SUCCESS));
                Log.d("shake", script);
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
            Log.e("RegisterShake","Fail to execute the hook:"+ e.getMessage());
        }
    }

    public static String getShakeEventJs() {
        return eventJs;
    }
}