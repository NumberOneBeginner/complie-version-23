package com.none.Push.uploadImage.domains.utils;

import android.R.integer;
import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 * 对异步http请求的封装
 *
 */
public class HttpClientUtils {

	private static HttpClientUtils httpClientUtils ;

	private static AsyncHttpClient asyncClient  ;

	/**私有构造方法*/
	private HttpClientUtils() {
		asyncClient = new AsyncHttpClient() ;
		//		asyncClient.setTimeout(3000) ;

		asyncClient.setTimeout(6000000) ;

	}

	/***
	 * 取得单一实例+线程安全
	 * @Description:TODO
	 * @return
	 */
	public static HttpClientUtils getInstance(){
		if(null == httpClientUtils){
			synchronized (HttpClientUtils.class) {
				if(null == httpClientUtils){
					httpClientUtils = new HttpClientUtils() ;
				}
			}
		}
		return httpClientUtils ;
	}

	private static final String TAG = "HttpClientUtils";

	//private static AsyncHttpClient asyncClient = new AsyncHttpClient();

	/**
	 * 发起post网络访问
	 *
	 * @param url
	 * @param context
	 * @param responseHandler
	 */
	public  void post(String url,String userId,Context context, AsyncHttpResponseHandler responseHandler) {
		post(url,userId, null, context,  responseHandler);
	}

	/**
	 * 发起post网络访问
	 *
	 * @param url
	 *            访问地址
	 * @param params
	 *            携带的参数
	 * @param context
	 * @param responseHandler
	 *            回调接口
	 */
	public   void  post(String url, String userId,Map<String, Object> params, Context context, AsyncHttpResponseHandler responseHandler) {
		System.out.println("uriuriuri是"+url);
		if (null == context) {
			//			Log.e(TAG, "必须传入Context参数。");
			return;
		}
		if(userId !=null){
			Log.e("userid---------------", userId) ;
			asyncClient.addHeader("BasicAuthUsername", userId) ;
		}

		RequestParams requestParams = null;
		if (null != params) {
			requestParams = new RequestParams();

			requestParams.setHttpEntityIsRepeatable(true) ;

			for (Map.Entry<String, Object> entry : params.entrySet()) {
				if (entry.getValue() instanceof File) {
					try {
						requestParams.put(entry.getKey(), (File) entry.getValue());
					} catch (FileNotFoundException e) {
						Log.e(TAG, e.toString());
					}
				} else if (entry.getValue() instanceof String) {
					requestParams.put(entry.getKey(), (String) entry.getValue());
				}
				else{

					Log.i("else", "requestParams requestParams requestParams"+entry.getValue());

					requestParams.put(entry.getKey(),entry.getValue());
				}
			}
		}
		Log.e(TAG, url + "?" + requestParams);
		asyncClient.post(context, url, requestParams , responseHandler);

	}


	/**
	 * 发起get请求访问
	 * @param context
	 * @param url 请求的地址
	 * @param params 请求参数
	 * @param responseHandler 回调接口
	 */
	public  void  get(Context context, String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		if (null == context) {
			Log.e(TAG, "必须传入Context参数。");
			return;
		}

		PersistentCookieStore cookieStore = new PersistentCookieStore(context);
		asyncClient.setCookieStore(cookieStore);
		asyncClient.get(url, params, responseHandler);
	}

	/**
	 * 取消连接
	 *
	 * @param context
	 */
	public  void cancel(Context context) {
		asyncClient.cancelRequests(context, true);
	}
	/**
	 * 取消所有请求
	 */
	public  void cancleAll(){
		asyncClient.cancelAllRequests(true) ;
	}
}
