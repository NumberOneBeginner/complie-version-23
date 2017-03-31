package com.hsbc.greenpacket.util.actions;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.webkit.WebView;

import com.hsbc.greenpacket.hook.Hook;
/**
 * Let Hook API action to implement this interface for datajs process
 * @author York Y K LI
 *
 */
public interface IHsbcUrlAction {
    /** Add by York, for do process after got the datajs **/
    public void dataProcess(Context context, WebView webview, Handler mHandler, String dataValue, JSONObject jo, Hook hook)
        throws JSONException, HookException;

    /**
     * Something in the onActivityResult will handle in the related action
     * @author York Y K LI[Jan 19, 2013]
     * @param context
     * @param mHandler
     * @param data
     *
     */
    public void onActionResult(Context context, Handler mHandler, int resultCode,Intent data);
}
