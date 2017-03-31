package com.hsbc.greenpacket.util.actions;

import android.content.Context;
import android.webkit.WebView;

import com.hsbc.greenpacket.hook.Hook;
import com.hsbc.greenpacket.util.StringUtil;

public class ValidateSecureTokenAction extends HSBCURLAction {

    @Override
    public void execute(Context context, WebView webview, Hook hook) {
        try {
            String result = "false";
            String callbackJs = this.getParams().get(HookConstants.CALLBACK_JS);
            String value = this.getParams().get(HookConstants.VALUE);
            if (value.equalsIgnoreCase(HookConstants.SECURE_KEY)) {
                result = "true";
            }
            hook.sendMsg(HookConstants.SWITCH_WEBVIEW);
            if (!StringUtil.IsNullOrEmpty(callbackJs)) {
                String script = HSBCURLAction.getCallbackJs(callbackJs, result);
                hook.sendStringMsg(script);
            }

        } catch (Exception e) {
            executeHookAPIFailCallJs(webview);
        }
    }

}
