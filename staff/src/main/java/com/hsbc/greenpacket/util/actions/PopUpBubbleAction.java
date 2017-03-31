package com.hsbc.greenpacket.util.actions;

import org.json.JSONException;
import org.json.JSONObject;

import com.hsbc.greenpacket.hook.Hook;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.webkit.WebView;

public class PopUpBubbleAction extends HsbcGetJsDataAction {

    @Override
    public void dataProcess(Context context, WebView webview, Handler mHandler, String data, JSONObject jo)throws JSONException {
        String callbackJs = jo.getString(HookConstants.CALLBACK_JS);
        Message message = Message.obtain();
        message.what = HookConstants.ALERT_FOR_NEW_MSG;
        Bundle mBundle = new Bundle();
        mBundle.putString(HookConstants.MESSAGE_DATA, data);
        message.setData(mBundle);
        mHandler.sendMessage(message);
        String scriptUrl = HSBCURLAction.getCallbackJs(callbackJs, "");
        loadUrlInMainThread(webview,scriptUrl);
        
    }

}
