package com.hsbc.greenpacket.util.actions;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;

import com.hsbc.greenpacket.activities.MainBrowserActivity;
import com.hsbc.greenpacket.hook.Hook;
import com.hsbc.greenpacket.util.JsonUtil;
import com.hsbc.greenpacket.util.StringUtil;

import com.google.gson.annotations.Expose;

/*
 * Hook for register long press <a herf="hsbc://function=RegisterLongPressListener&datajs=XXX&eventjs=YYY&callbackjs=ZZZ Karson Li[14 Dec
 * 2013]
 */
public class RegisterLongPressListenerAction extends HsbcGetJsDataAction {
    private static LocationOfLongPress location;
    private static String eventJs = null;

    @Override
    public void validate(Context context, WebView webview, Hook hook) throws HookException {
        super.validate(context, webview, hook);
        Map<String, String> map = this.getParams();
        String eventjs = map.get(HookConstants.EVENT_JS);
        String datajs = map.get(HookConstants.DATA_JS);
        if (StringUtil.IsNullOrEmpty(eventjs) || StringUtil.IsNullOrEmpty(datajs)) {
            throw new HookException("request parameter missing");
        }
    }

    @Override
    public void dataProcess(Context context, WebView webview, Handler mHandler, String dataValue, JSONObject jo)
        throws JSONException {
        String callbackJs = null;
        try {
            if (jo.has(HookConstants.EVENT_JS)) {
                eventJs = jo.getString(HookConstants.EVENT_JS);
                callbackJs = jo.getString(HookConstants.CALLBACK_JS);
            }
            location = datajsProgress(dataValue);
            if (context instanceof MainBrowserActivity) {
                ((MainBrowserActivity) context).setEnableLongPress(true);
            } else {
                throw new Exception("Request context is not MainBrowserActivity");
            }
            if (!StringUtil.IsNullOrEmpty(callbackJs)) {
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
            RegisterLongPressListenerAction.clearLocationOfLongPress();
            Log.e("RegisterLongPress","Fail to execute the hook:"+ e.getMessage());
        }

    }


    public static void clearLocationOfLongPress() {
        if (null != location || null != eventJs) {
            location = null;
            eventJs = null;
        }
    }

    private LocationOfLongPress datajsProgress(String datajs) {
        LocationOfLongPress location = JsonUtil.getObjectFromJson(datajs, LocationOfLongPress.class);
        return location;
    }

    public class LocationOfLongPress {
        @Expose
        private float topleft_x;
        @Expose
        private float topleft_y;
        @Expose
        private float bottomright_x;
        @Expose
        private float bottomright_y;
        public float getTopleft_x() {
            return topleft_x;
        }
        public float getTopleft_y() {
            return topleft_y;
        }
        public float getBottomright_x() {
            return bottomright_x;
        }
        public float getBottomright_y() {
            return bottomright_y;
        }
        public String toString(){
            return "topleft_x: "+topleft_x;
        }
    }

    public static LocationOfLongPress getLocation() {
        return location;
    }

    public static String getEventJs() {
        return eventJs;
    }

}
