package com.hsbc.greenpacket.util.actions;

import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;

import com.hsbc.greenpacket.hook.Hook;
import com.hsbc.greenpacket.util.StringUtil;

/**
 * hsbc://function=Proxy&datajs=<javascript function where the data from>&callbackjs=<javascrpt function for callback>
 * @author 43580172
 *
 */
public class ProxyJsonAction extends HSBCURLAction {


	@Override
	public void execute(Context context, WebView webview,Hook hook) {
		try{
			Map<String, String> map=this.getParams();
			if(map==null){
				throw new HookException();
			}
			String datajs=map.get(HookConstants.DATA_JS);
			String callbackJs=map.get(HookConstants.CALLBACK_JS);
			String url=map.get(HookConstants.URL);
			String method = map.get(HookConstants.METHOD);
			if(StringUtil.IsNullOrEmpty(datajs)||StringUtil.IsNullOrEmpty(callbackJs)
					||StringUtil.IsNullOrEmpty(url)){
				throw new HookException("request parameter missing");
			}
			
			hook.setMap(map);
			String js= getProxyDataJs(url,datajs,method,callbackJs,"setProxyDataJson");
			//LOG.debug("excute data js:"+js);
			loadUrlInMainThread(webview,js);
			hook.setWebview(webview);//test
		}catch(Exception e){
			executeHookAPIFailCallJs(webview);
			Log.e(TAG,"Fail to execute the hook:{}",e);
		}
	}

}