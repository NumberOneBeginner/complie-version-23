package com.hsbc.greenpacket.task;

import java.net.URLEncoder;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.util.Log;

import com.hsbc.greenpacket.activities.Constants;
import com.hsbc.greenpacket.util.HSBCHttpClient;

import com.none.staff.task.ActivityCallback;
import com.none.staff.task.AsyncTaskWithCallback;

public class ProxyTask extends AsyncTaskWithCallback<String, Void, ProxyResponse> {
	private static final String TAG = "ProxyTask";
    private final Context context;
	
	public ProxyTask(final Context context, final ActivityCallback callback, final int ref) {
		super(callback, ref);
		if (context == null) {
			throw new IllegalArgumentException("owner must not be null");
		}
		this.context=context;
	}
	@Override
	protected ProxyResponse doInBackground(String... params) {
		// TODO Auto-generated method stub
		ProxyResponse proxyRsp=new ProxyResponse();
		
		try{
			if(params!=null&&params.length==4){
				String url=params[0];
				String urlPara=params[1];
				String method=params[2];
				String callbackJs=params[3];
				proxyRsp.setCallbackJs(callbackJs);
				StringBuffer sf=null;
				HSBCHttpClient httpClient=HSBCHttpClient.getInstance(this.context);
				if(method!=null&&method.toUpperCase().equals(Constants.REQUEST_POST)){
					List<NameValuePair> paraList=HSBCHttpClient.parseURL(urlPara); //will not return null;
					Log.d("proxy"," ========request parameter "+paraList.toString());
					sf= httpClient.connectURLByPost(url, null, paraList);
					proxyRsp.setResponseStr(sf);
					Log.d("proxy","==response== "+new String(sf));
					return proxyRsp;
				}else if(method!=null&&method.toUpperCase().equals(Constants.REQUEST_GET)){
					//urlPara=getURLPara(urlPara);
					if(urlPara!=null){
						if(url!=null&&url.indexOf("?")!=-1){
							url=url+"&"+urlPara;
						}else{
							url=url+"?"+urlPara;
						}
					}
					if(url.indexOf(" ")!=-1){
			        	url=url.replaceAll(" ", "%20");
			        }
					sf= httpClient.connectURL(url, null);
                    Log.d(TAG,"==responce== "+new String(sf));
					proxyRsp.setResponseStr(sf);
					return proxyRsp;
				}else{
					Log.d(TAG,"parameter error");
				}
			}else{
				this.setError(FAILED,"Proxy parameters are not enought.");
			}
		}catch(Exception e){
			Log.e(TAG,"Proxy error!",e);
			this.setError(FAILED,e.getMessage());
		}
		return proxyRsp;
	}
	public String getURLPara(String urlPara){
		try{
			List<NameValuePair> paraList=HSBCHttpClient.parseURL(urlPara);
			StringBuffer para=new StringBuffer();
			for(int i=0;i<paraList.size();i++){
				NameValuePair nvp =paraList.get(i);
				if(i>0){
					para.append("&");
				}
				para.append(URLEncoder.encode(nvp.getName(),HTTP.UTF_8))
				.append("=")
				.append(URLEncoder.encode(nvp.getValue(),HTTP.UTF_8));
			}
			return para.toString();
		}catch(Exception e){
			Log.e(TAG,"URL parameter parse error",e);
		}
		return null;
	}
	
}
