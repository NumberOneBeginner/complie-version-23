package com.hsbc.greenpacket.util.actions;


import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;

import com.hsbc.greenpacket.activities.MainBrowserActivity;
import com.hsbc.greenpacket.hook.Hook;
import com.hsbc.greenpacket.util.StringUtil;

/**
 * Enable/disable drag gesture detector. <br/>
 * hsbc://function=RegisterGestureListener&eventjs=XXX&callbackjs=YYY
 * 
 * @author Karson Li
 * @date [13 Dec 2013]
 * 
 */
public class RegisterGestureListenerAction extends HSBCURLAction {
    private static String eventjs;

    @Override
    public void execute(Context context, WebView webview, Hook hook) {
        // TODO Auto-generated method stub
        String callbackJs = null;
        try {
            Map<String, String> map = this.getParams();
            callbackJs = map.get(HookConstants.CALLBACK_JS);
            String eventJs = map.get(HookConstants.EVENT_JS);

            if (map == null || StringUtil.IsNullOrEmpty(eventJs)) {
                throw new HookException("request parameter missing");
            }
            boolean enableDrag = true;
            eventjs = eventJs;
            if (context instanceof MainBrowserActivity) {
                ((MainBrowserActivity) context).setEnableDrag(enableDrag);
            } else {
                throw new Exception("Request context is not MainBrowserActivity");
            }
            String script = HSBCURLAction.getCallbackJs(callbackJs, HSBCURLAction.getErrorResponseJson(HookConstants.SUCCESS));
            Log.d("RegisterGesture", script);
            loadUrlInMainThread(webview, script);
        } catch (Exception e) {
            Log.e("RegisterGesture", "Fail to execute the hook:{}", e);
            if (callbackJs == null) {
                executeHookAPIFailCallJs(webview);
            } else {
                String script = HSBCURLAction.getCallbackJs(callbackJs,
                    HSBCURLAction.getErrorResponseJson(HookConstants.API_EXCUTE_ERROR_CODE));
                webview.loadUrl(script);
            }
            RegisterGestureListenerAction.cleanRegisterGestureListenerEvnetjs();
        }
    }

    public static String getRegisterGestureListenerEventjs() {
        return RegisterGestureListenerAction.eventjs;
    }

    public static void cleanRegisterGestureListenerEvnetjs() {
        RegisterGestureListenerAction.eventjs = null;
    }


}
