package com.hsbc.greenpacket.hook;

import java.util.Map;

import org.apache.cordova.LOG;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.hsbc.greenpacket.activities.Constants;
import com.hsbc.greenpacket.activities.HSBCActivity;
import com.hsbc.greenpacket.task.ProxyResponse;
import com.hsbc.greenpacket.task.ProxyTask;
import com.hsbc.greenpacket.util.StringUtil;
import com.hsbc.greenpacket.util.actions.HSBCURLAction;
import com.hsbc.greenpacket.util.actions.HSBCURLResolverUI15;
import com.hsbc.greenpacket.util.actions.HookConstants;
import com.hsbc.greenpacket.util.actions.IHsbcUrlAction;

import com.none.staff.task.ActivityCallback;
import com.none.staff.task.AsyncTaskWithCallback;
import com.none.staff.utils.NetworkUtils;

public class Hook implements ActivityCallback {
    protected final HSBCActivity context;
    protected final static int PROXY_TASK_REF = 3;
    protected final static int PROXY_JSON_TASK_REF = 4;
    protected final static int MULTIPLE_PROXY_TASK_REF = 5;//RDC Project : Proxy Connections Scholes 18Jun2013
    protected final static int GSP_PROXY_JSON_TASK_REF = 6;
    protected WebView webview;
    protected Handler handler;
    private Map<String, String> map;
    private String dataValue;

    /* for sotp add by June 2012-12-07 */
    public String logOffFlag = "";
    public String logOffFUrl = "";
    public String tokenType = "";
    
    private static final String PLATFORM_KEY  = "platform";
    private static final String PLATFORM = "A";
    private static final String DEVICE_TYPE_KEY = "devtype";
    private static final String DEVICE_TYPE_MOBILE = "M";
    private static final String DEVICE_TYPE_TABLET = "T";
    private static final String TAG = "Hook";
    
    //RDC Project : Proxy Connections Scholes 18Jun2013 start   
    public enum RequestType {
    	JSON,
    	PLAIN,
    	MULTIPLE;
    }
    //RDC Project : Proxy Connections Scholes 18Jun2013 end
    /**
     * @return the logOffFlag
     */
    public String getLogOffFlag() {
        return logOffFlag;
    }

    /**
     * @param logOffFlag
     *            the logOffFlag to set
     */
    public void setLogOffFlag(String logOffFlag) {
        this.logOffFlag = logOffFlag;
    }

    /**
     * @return the logOffFUrl
     */
    public String getLogOffFUrl() {
        return logOffFUrl;
    }

    /**
     * @param logOffFUrl
     *            the logOffFUrl to set
     */
    public void setLogOffFUrl(String logOffFUrl) {
        this.logOffFUrl = logOffFUrl;
    }

    /**
     * @return the tokenType
     */
    public String getTokenType() {
        return tokenType;
    }

    /**
     * @param tokenType
     *            the tokenType to set
     */
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public String getDataValue() {
        return dataValue;
    }
//RDC Project : Proxy Connections Scholes 18Jun2013 start
    @JavascriptInterface
    public void setProxyData(String url, String dataValue, String method, String callbackjs) {
        proxyAPI(url, dataValue, method, callbackjs, RequestType.PLAIN);
    }
    @JavascriptInterface
    public void setMultipleProxyData(String taskId, String url, String params, String method, String callbackJs) {
    	proxyAPI(taskId, url, params, method, callbackJs, RequestType.MULTIPLE);
    }
    @JavascriptInterface
    public void setProxyDataJson(String url, String dataValue, String method, String callbackjs) {
        proxyAPI(url, dataValue, method, callbackjs, RequestType.JSON);
    }
//RDC Project : Proxy Connections Scholes 18Jun2013 end
    @JavascriptInterface
    public void setHookData(String dataValue, String json) {
        this.dataValue = dataValue;
        try {
            JSONObject jo = new JSONObject(json);
            String function = jo.getString(HookConstants.FUNCTION);
            /**
             * York [2012/11/22] As more and more Hook API need support data
             * js. so Use factory mode to separate the data process code.
             */
            IHsbcUrlAction action = HSBCURLResolverUI15.resolveByFunction(function);
            if (action == null) {
                throw new Exception("Hook API not support!");
            }

            action.dataProcess(context, webview, handler, dataValue, jo, this);
        } catch (Exception e) {
            Log.e(TAG,"Hook API parameter error for:"+dataValue+"/"+json);
            Log.e(TAG,"Set hook data error",e);
            executeHookAPIFailCallJs(webview);
        }
    }


    public Hook(HSBCActivity context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    public WebView getWebview() {
        return webview;
    }

    public void setWebview(WebView webview) {
        this.webview = webview;
    }

    public void proxyAPI(String url, String params, String method, String callbackJs, RequestType type) {
    	proxyAPI(null, url, params, method, callbackJs, type);
    }
    
    public void proxyAPI(String taskId, String url, String params, String method, String callbackJs, RequestType type) {
        try {

            if (!NetworkUtils.deviceOnline(context)) {
                final String script = HSBCURLAction.getCallbackJs(callbackJs, HSBCURLAction.getErrorResponseJson(HookConstants.NETWORK_ERROR_CODE));
                Log.d(TAG,"device network unvailable:" + script);
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        webview.loadUrl(script);
                    }
                });
                //this.webview.loadUrl(script);
                return;
            }
            if (StringUtil.IsNullOrEmpty(url) || StringUtil.IsNullOrEmpty(callbackJs)) { // those two parameters
                                                     // are mandatory
                return;
            }
            // Log.d(TAG,url+" parameters:"+params+" method:"+method+" callback:"+callbackJs);
            url = java.net.URLDecoder.decode(url);
            if (url.indexOf(" ") != -1) {
                url = url.replaceAll(" ", "%20");
            }

            if (method == null) {
                method = "GET";
            } else {
                method = method.toUpperCase();
            }
            if (!method.equals(Constants.REQUEST_POST) && !method.equals(Constants.REQUEST_GET)) {
                method = "GET";
            }
            ProxyTask task = new ProxyTask(this.context, this, PROXY_TASK_REF);
            context.addTask(task);
            task.execute(url, params, method, callbackJs);

        } catch (Exception e) {
            Log.e(TAG,"proxyAPI error", e);
            executeHookAPIFailCallJs(webview);
        }
    }


    public String getAppInfo() {
        // android.os.Build.MODEL;
        return null;
    }

    @SuppressWarnings("rawtypes")
    public void handleCallback(final AsyncTaskWithCallback task, final int ref) {
        try {
            context.removeTask(task);
            if (PROXY_TASK_REF == ref) {
                handleProxyTask((ProxyTask) task);
            } 
        } catch (Exception e) {
            Log.e(TAG,"handle callback in hook error.", e);
            executeHookAPIFailCallJs(webview);
        }

    }

    private void handleProxyTask(final ProxyTask task) {
        handleProxyTask(task.getError(), task.getResult());
    }
    //RDC Project : Proxy Connections Scholes 18Jun2013 end
    private void handleProxyTask(int flag, ProxyResponse result) {
        String callbackJs = result.getCallbackJs();
        switch (flag) {
            case AsyncTaskWithCallback.SUCCESS:
                StringBuffer results = result.getResponseStr();
                // handler.sendEmptyMessage(HookConstants.HIDE_PROGRESS_MSG);
                if (results != null) {
                    String rs = results.toString();
                        int count = (int) (Math.ceil((double) rs.length() / 3000));
                        for (int i = 0; i < count; i++) {
                            if (i == count - 1) {
                                Log.d(TAG,"proxy response:"+ rs.substring(i * 3000, rs.length()));
                            } else
                                Log.d(TAG,"proxy response:"+ rs.substring(i * 3000, (i + 1) * 3000));
                        }
                    String script = HSBCURLAction.getCallbackJs(callbackJs, rs);
                    Log.d(TAG,"excute callback:"+script);
                    this.webview.loadUrl(script);
                } else {
                    String script = HSBCURLAction.getCallbackJs(callbackJs, HSBCURLAction.getErrorResponseJson(HookConstants.OTHER_ERROR_CODE));
                    Log.d(TAG,"excute callback:"+script);
                    this.webview.loadUrl(script);
                }
                break;
            case AsyncTaskWithCallback.FAILED:
                // handler.sendEmptyMessage(HookConstants.HIDE_PROGRESS_MSG);
                if (StringUtil.IsNullOrEmpty(callbackJs)) {
                    /**
                     * callback js not exist and then call the hook api error
                     */
                    String script = HSBCURLAction.gethookAPIFailCallScript();
                    this.webview.loadUrl(script);
                } else {
                    String script = HSBCURLAction.getCallbackJs(callbackJs, HSBCURLAction.getErrorResponseJson(HookConstants.TIMEOUT_ERROR_CODE));
                    Log.d(TAG,"excute callback:"+script);
                    this.webview.loadUrl(script);
                }
                break;
        }
	
    }

    public void sendMsg(int MsgId) {
        if (handler != null) {
            handler.sendEmptyMessage(MsgId);
        } else {
            Log.e(TAG,"handler should not be null");
        }
    }

    public void sendMsgObj(Message msg) {
        if (handler != null) {
            handler.sendMessage(msg);
        } else {
            Log.e(TAG,"handler should not be null!");
        }
    }
    /**
     * 
     * @author York Y K LI[Jan 16, 2013]
     * @param script
     *
     */
    public void sendStringMsg(String script) {
        sendStringMsg(this.handler,script,false);
    }
    public void loadUrlInCurrentWebview(String script,boolean setHeader) {
        sendStringMsg(this.handler,script,setHeader);
    }
    public static void sendStringMsg(Handler handler,String script) {
        sendStringMsg(handler,script,false);
    }
    /**
     * Send back the javascript to the MainBrowser's webview to execute
     * @author York Y K LI[Jan 16, 2013]
     * @param handler
     * @param script
     *
     */
    public static void sendStringMsg(Handler handler,String script,boolean setHeader){
        if (handler != null && !StringUtil.IsNullOrEmpty(script)) {
            Message message = Message.obtain();
            message.what = HookConstants.EXECUTE_JAVASCRIPT;
            Bundle mBundle = new Bundle();
            mBundle.putString(HookConstants.MESSAGE_DATA, script);
            if(setHeader){
                mBundle.putBoolean(HookConstants.SET_HEADER, true);
            }
            message.setData(mBundle);
            handler.sendMessage(message);
        } else {
            Log.e(TAG,"handler should not be null!");
        }
    }
    public void executeHookAPIFailCallJs(WebView webview) {
        if(webview!=null){
            webview.loadUrl(HSBCURLAction.gethookAPIFailCallScript());
        }
    }
    public void executeHookAPIFailCallJs() {
        sendStringMsg(HSBCURLAction.gethookAPIFailCallScript());
    }

}
