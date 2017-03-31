/*
 * COPYRIGHT. HSBC HOLDINGS PLC 2011. ALL RIGHTS RESERVED.
 *
 * This software is only to be used for the purpose for which it has been
 * provided. No part of it is to be reproduced, disassembled, transmitted,
 * stored in a retrieval system nor translated in any human or computer
 * language in any way or for any other purposes whatsoever without the
 * prior written consent of HSBC Holdings plc.
 */
package com.hsbc.greenpacket.util.actions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.WebView;

import com.hsbc.greenpacket.activities.Constants;
import com.hsbc.greenpacket.hook.Hook;
import com.hsbc.greenpacket.util.JSONConstants;
import com.hsbc.greenpacket.util.JsonUtil;


/**
 * Implementations of this class should be used to handled custom HSBC URLs received in the {@link WebView}.
 * 
 */
public abstract class HSBCURLAction {
	protected static final String TAG = "HSBCURLAction";
    /**
	 * fix ISR defect #OUT45, QCID #9102
     * JW/Nina 6-Nov-2013
	 */
	/**
	 * Params for this action, this will either be empty if no params are supplied or a unmodifiable map of params.
	 */
	private Map<String, String> params = Collections.emptyMap();
	

	
	public static String getJavascript(String callback,String[] parameter){
		StringBuffer parameterJs = new StringBuffer();
		for(int i=0;i<parameter.length;i++){
			if(i!=0){
				parameterJs.append(",");
			}
			try {
				parameter[i] = parameter[i].replaceAll("\r", "");
				parameter[i] = parameter[i].replaceAll("\n", "");
	        } catch (Exception e) {
	    		/**
	    		 * fix ISR defect #OUT45, QCID #9102
	             * JW/Nina 6-Nov-2013
	    		 */
	        	Log.e(TAG,e.getMessage(), e);
	        }
			parameterJs.append(HookConstants.QUOTA).append(parameter[i]).append(HookConstants.QUOTA);
		}
        StringBuffer str = new StringBuffer();
        str.append(HookConstants.JAVASCRIPT).append(callback).append("(").append(parameterJs.toString()).append(")");
        return str.toString();
	}
	
	public static String getJavascriptWithArray(String callback,String[] parameter){
		StringBuffer parameterJs = new StringBuffer();
		StringBuffer js = new StringBuffer();
		for(int i=0;i<parameter.length;i++){
			if(i!=0){
				parameterJs.append(",");
			}
			parameterJs.append(JsonUtil.getJsonFromJavaObject(parameter[i]));
		}
		
		js.append(HookConstants.JAVASCRIPT).append(callback).append("(").append(parameterJs).append(")");
		String data=js.toString();
		data = data.replaceAll("\r", "");
        data = data.replaceAll("\n", "");
		return data;
	}
//    public static String getCallbackJs(String callback,String data){
//        StringBuffer str=new StringBuffer();
//        data=data.replaceAll("\r", "");
//        data=data.replaceAll("\n", "");
//        if(isObject(data)){
//            data=data.replaceAll("\\\\", "\\\\\\\\");
//        }else{
//            /**
//             * @author York Y K LI[Nov 23, 2012] add data.replaceAll("\\\\\"", "\"") for special char handling
//             */
//            data = data.replaceAll("\\\\\"", "\"");
//        }
//        data=data.replaceAll("\"", "\\\\\"");
//        str.append(HookConstants.JAVASCRIPT).append(callback).append("(\"").append(data).append("\")");
//        return str.toString();
//    }
    /**
     * User gson to transfer string to json object
     * 
     * @author York Y K LI[Nov 23, 2012]
     * @param callback
     * @param data
     * @return
     * 
     */
    public static String getCallbackJs(String callback, String data) {
        StringBuffer str = new StringBuffer();
        try {
            data = data.replaceAll("\r", "");
            data = data.replaceAll("\n", "");
            data = JsonUtil.getJsonFromJavaObject(data);
        } catch (Exception e) {
            data = "";
        }
        str.append(HookConstants.JAVASCRIPT).append(callback).append("(").append(data).append(")");
        return str.toString();
    }
    /**
     * @description used for handling gsp proxy response
     * @author Cherry
     * @date 2013-05-28
     * @return
     */
    public static String getGspCallbackJs(String callback, String data,String statusCode) {
        StringBuffer str = new StringBuffer();
        try {
            data = data.replaceAll("\r", "");
            data = data.replaceAll("\n", "");
            data = JsonUtil.getJsonFromJavaObject(data);
        } catch (Exception e) {
            data = "";
        }
        str.append(HookConstants.JAVASCRIPT).append(callback).append("(").append(data).append(",").append("\"").append(statusCode).append("\"").append(")");
        return str.toString();
    }

  //RDC Project : Proxy Connections Scholes 13Jun2013  start
    public static String getCallbackJsWithTaskId(String taskId, String callback, String data) {
        StringBuffer str = new StringBuffer();
        try {
            data = data.replaceAll("\r", "");
            data = data.replaceAll("\n", "");
            data = JsonUtil.getJsonFromJavaObject(data);
        } catch (Exception e) {
            data = "";
        }
        str.append(HookConstants.JAVASCRIPT).append(callback).append("('").append(taskId).append("', ").append(data).append(")");
        return str.toString();
    }
  //RDC Project : Proxy Connections Scholes 13Jun2013  end

	public static String gethookAPIFailCallScript(){
		return getCallbackJs(HookConstants.API_FAIL_CALL_FUNC,getErrorResponseJson(HookConstants.API_EXCUTE_ERROR_CODE));
	}
	public void executeHookAPIFailCallJs(WebView webview){
	    loadUrlInMainThread(webview,gethookAPIFailCallScript());
	}
	
	/**
	 * @description when execute general Hook API error ,call the function
	 * @author Cherry 2012-01-17
	 * @param handler
	 */
	public void hookAPIGeneralErrorCallback(Handler handler){
		Hook.sendStringMsg(handler, HSBCURLAction.gethookAPIFailCallScript());
	}
	
	private static boolean isObject(String str){
		if(str==null){
			return false;
		}
		try{
			JSONObject json= new JSONObject(str);
			json=null;
			return true;
		}catch(Exception e){
			return false;
		}
		
	}
	/**
	 * Used by the {@link HSBCURLResolver} to set the parameters for this action.
	 * 
	 * @param params
	 *            A non null map of parameters for this action.
	 */
	public final void setParams(final Map<String, String> params) {
		if (params == null) {
			throw new IllegalArgumentException("Action parameters may not be null");
		}
		this.params = Collections.unmodifiableMap(params);
	}

	/**
	 * 
	 * @return A unmodifiable map of parameters for given to this action.
	 */
	protected final Map<String, String> getParams() {
		return this.params;
	}

	/**
	 * Subclasses should implement this method to execute the action this class is responsible for.
	 * 
	 * @param context
	 */
	//public abstract void execute(final Context context, WebView webview);
	public abstract void execute(final Context context,WebView webview,Hook hook);
//	public void sendStringMsg(Handler handler,String message){
//	    
//	}
//	public String getSetDataHookJs(String datajs_function_name){
//		String js="javascript:window."+HookConstants.HOOK_OBJECT+".setDataValue("+datajs_function_name+"())";
//		return js;
//	}
	public String getProxyDataJs(String url,String datajs,String method,String callbackjs,String dataMethod){
		if(method==null||method.length()==0){
			method=Constants.REQUEST_GET;
		}
		StringBuffer sb=getDataJs(dataMethod);
		sb.append("(");
		sb.append("\"").append(url).append("\"");
		sb.append(",");
		sb.append(datajs).append("()");
		sb.append(",");
		sb.append("\"").append(method).append("\"");
		sb.append(",");
		sb.append("\"").append(callbackjs).append("\"");
		sb.append(")");
		return sb.toString();
	}
	public String executeDataJs(Map<String,String> map){
		String datajs=map.get(HookConstants.DATA_JS);
		String paras=mapToJson(map);
		StringBuffer sb=getDataJs("setHookData");
		sb.append("(");
		sb.append(datajs).append("()");
		sb.append(",");
		sb.append("'").append(paras).append("'");
		sb.append(")");
		return sb.toString();
	}
	public String mapToJson(Map<String,String> map){
		String json= JsonUtil.getJsonFromJavaObject(map);		
        return json;
	}
	public StringBuffer getDataJs(String methodName){
		StringBuffer sb=new StringBuffer();
		sb.append("javascript:window.").append(HookConstants.HOOK_OBJECT).append(".").append(methodName);
		return sb;
	}
	
	public static String getErrorResponseJson(String code) {
		/**
		 * Call JsonUtil function by converting HashMap<String,String>() to Json
		 * Avoid repeated code
		 * @author Cherry
		 * @date 2013-01-17
		 */
	    	return JsonUtil.getRspCallbackJson(code, null);
	    }

	    /**
	     * @description When toggle Language successfully callback and provide
	     *              return parameters
	     *              
	     *   call JsonUtil function by converting HashMap<String,String>() to Json           
	     * @author Cherry [2013-01-17]
	     */
	    public static String getSuccessfulResponseJson(String code, String locale) {
	    	HashMap<String,String> body=new HashMap<String,String>();
	    	body.put(HookConstants.APPLOCALE, locale);
	    	return JsonUtil.getRspCallbackJson(code, body);
	    }
	    
	    /**
	     * @description When IsSupportedFeatureInAppAction successfully callback and provide
	     *              return parameters
	     *              call JsonUtil function by converting HashMap<String,String>() to Json
	     * @author Cherry [2013-01-17]
	     */
	    public static String getSuccessfulForSupportFeatureResponseJson(String code, String name, boolean isSupported) {
	        HashMap<String,String> body=new HashMap<String,String>();	        
	    	body.put(HookConstants.FEATURE_NAME, name);
	    	body.put(JSONConstants.IS_SUPPORT, String.valueOf(isSupported));
	    	return JsonUtil.getRspCallbackJson(code, body);
	    	
	    }
	    /**
	     * Make the webview.loadUrl() which in Hook action switch to execute in main thread to avoid the warning.
	     * @author York Y K LI[May 6, 2013]
	     * @param view
	     * @param url
	     *
	     */
	    public static void loadUrlInMainThread(final WebView view,final String url){
	        final Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    view.loadUrl(url);
                }
            });
	    }
}
