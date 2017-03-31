package com.none.staff.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.util.Log;

public class StaffHttpClient {
	
	private final static int CONN_TIMEOUT = 20000; // in milsec default is 30s

	private  HttpContext localContext = null;
	private  DefaultHttpClient client = null;
	private static StaffHttpClient myClient = null;
	private  boolean allowAllSSL=true;
	private  String cookiePolicy = CookiePolicy.NETSCAPE;
	public Context context;
    private static final String TAG="StaffHttpClient";
	
	private StaffHttpClient(Context context) {
		try{
			this.context=context.getApplicationContext();
			Log.d(TAG,"StaffHttpClient instance created");
		}catch(Exception e){
			Log.e(TAG,"StaffHttpClient initial error!",e);
		}
		try{
			initLocalContext(context);
		}catch(Exception e){
			Log.e(TAG,"initLocalContext error");
		}
	}

	private String getCookiePolicy(String policy){
		String defaultCookiePolicy = CookiePolicy.NETSCAPE;
		if(policy==null){
			return defaultCookiePolicy;
		}
		HashMap<String,String> map=new HashMap<String,String>();
		map.put(CookiePolicy.BEST_MATCH, "");
		map.put(CookiePolicy.BROWSER_COMPATIBILITY, "");
		map.put(CookiePolicy.NETSCAPE, "");
		map.put(CookiePolicy.RFC_2109, "");
		map.put(CookiePolicy.RFC_2965, "");
		if(map.get(policy)!=null){
			return policy;
		}else{
			return defaultCookiePolicy;
		}
		
	}
	

	
	public static StaffHttpClient getInstance(Context context) {
		    synchronized (StaffHttpClient.class) {
		    	if (myClient == null) {
					myClient = new StaffHttpClient(context);	
				}
				return myClient;
		    }
	}

	private  DefaultHttpClient getDefaultHttpClient(final Context context){
		DefaultHttpClient httpClient = new DefaultHttpClient(){
	        @Override
	        protected ClientConnectionManager createClientConnectionManager() { 
	            SchemeRegistry registry = new SchemeRegistry(); 
	            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80)); 
	            if(StaffHttpClient.this.allowAllSSL){
	            	registry.register(new Scheme("https", getInSecuSocketFactory(), 443)); 
	            }else{
	            	registry.register(new Scheme("https", getHttpsSocketFactory(), 443)); 
	            }
	            HttpParams params = getParams(); 
	            HttpConnectionParams.setConnectionTimeout(params, CONN_TIMEOUT); 
	            HttpConnectionParams.setSoTimeout(params, CONN_TIMEOUT); 
	            HttpProtocolParams.setUserAgent(params, HttpProtocolParams.getUserAgent(params)); 
	            return new ThreadSafeClientConnManager(params, registry); 
	        } 

	        protected SocketFactory getHttpsSocketFactory(){ 
	        	return SSLSocketFactory.getSocketFactory();
	
	        }
	        protected SocketFactory getInSecuSocketFactory(){
	        	try{
	    			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	    			trustStore.load(null, "".toCharArray());
	    			SSLSocketFactory sf = new StaffSSLSocketFactory(trustStore);
	    			sf.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
	    			return sf;
	    	    }catch(Exception e){
	    	    	Log.e(TAG,"getInSecuSocketFactory error");
	    	    	return SSLSocketFactory.getSocketFactory(); 
	    	    }
	    	}
		};
		return httpClient;
	}
	private void setCredentials(URI uri,String username, String password) {
		Credentials creds = new UsernamePasswordCredentials(username, password);
		client.getCredentialsProvider().setCredentials(new AuthScope(uri.getHost(), uri.getPort()), creds);
	}

	private void initLocalContext(Context context) {
		client = getDefaultHttpClient(context);
		HttpParams params = client.getParams();
		params.setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true); 
		setRequestInterceptor();
		HttpConnectionParams.setConnectionTimeout(params, CONN_TIMEOUT);
		HttpConnectionParams.setSoTimeout(params, CONN_TIMEOUT);
		HttpClientParams.setCookiePolicy(params, this.cookiePolicy);// must set it
	}


	public  void setRequestInterceptor() {
		HttpRequestInterceptor preemptiveAuth = new HttpRequestInterceptor() {
			public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
				AuthState authState = (AuthState) context.getAttribute(ClientContext.TARGET_AUTH_STATE);
				CredentialsProvider credsProvider = (CredentialsProvider) context
						.getAttribute(ClientContext.CREDS_PROVIDER);
				HttpHost targetHost = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
				if (authState.getAuthScheme() == null) {
					AuthScope authScope = new AuthScope(targetHost.getHostName(), targetHost.getPort());
					Credentials creds = credsProvider.getCredentials(authScope);
					if (creds != null) {
						authState.setAuthScheme(new BasicScheme());
						authState.setCredentials(creds);
					}
				}
			}
		};
		client.addRequestInterceptor(preemptiveAuth, 0);
	}

	public StringBuffer connectURL(String url) throws ClientProtocolException,IOException{
		HttpResponse response = getHttpResponse(url);
		if (response != null) {
			return getResponseStr(response);
		} else {
			return null;
		}

	}

	public HttpResponse getHttpResponse(String url) throws ClientProtocolException,IOException{
		HttpUriRequest request = createRequest(url);
		if(request==null){
			return null;
		}
		HttpResponse response = executeRequest(request);
		return response;
	}

	public StringBuffer connectURLByPost(String url, String cookieStr, List<NameValuePair> params)throws ClientProtocolException,IOException {
		HttpUriRequest request = createPostRequest(url, params);
		HttpResponse response = executeRequest(request);
		if (response != null) {
			return getResponseStr(response);
		} else {
			return null;
		}

	}




	public InputStream getResponseInputStream(final HttpResponse response) throws IllegalStateException,IOException{		
		HttpEntity entity = response.getEntity();
		InputStream is = entity.getContent();
		return is;
				
	}
/**
 * Proxy use this method
 * @param response
 * @return
 * @throws IllegalStateException
 * @throws IOException
 */
	public StringBuffer getResponseStr(final HttpResponse response)throws IllegalStateException,IOException {
		StringBuffer sf = new StringBuffer();
		InputStream is = getResponseInputStream(response);
		BufferedReader br = null;
		InputStreamReader ireader=null;
		if (is == null) {
			return null;
		}
		try {
			if(response.getStatusLine().getStatusCode()<400){
			    ireader=new java.io.InputStreamReader(is, HTTP.UTF_8);
				br = new BufferedReader(ireader);
				String str = "";
				while ((str = br.readLine()) != null) {
					sf.append(str);
				}
				return sf; 
			}else{
			    Log.e(TAG,"Http status code:"+response.getStatusLine().getStatusCode());
			}
		} catch(OutOfMemoryError memError){
			Log.d(TAG,"OutOfMemoryError in getResponseStr");
			
		}finally {
			IOUtils.close(is);//Housekeeping - release the resource in the finally block	
			if(ireader!=null){
			    ireader.close();
			}
			if (br != null) {
				//Housekeeping - release the resource in the finally block
				br.close();
			}
		}
		return null;
	}

/**
 * 
 * @param url
 * @return
 */
	private HttpUriRequest createRequest(String url) {
		HttpGet request = new HttpGet();
		try {
			URI uri = new URI(url);
			setHttpsCredentials(uri);
			request.setURI(uri);
		} catch (URISyntaxException e) {
			Log.e(TAG,"Create request",e);
			return null;
		}
		return request;
	}

	private void setHttpsCredentials(URI uri) {
		NameValuePair pair = getHttpsUserInfo(uri);
		if (pair != null && pair.getName() != null && pair.getValue() != null) {
			setCredentials(uri,pair.getName(), pair.getValue());
		}
	}

	private NameValuePair getUserInfo(URI uri) {
		NameValuePair pair = null;
		String userInfo = uri.getUserInfo();
		if (userInfo != null && userInfo.indexOf(":") != -1) {
			String[] info = userInfo.split(":");
			String username = info[0];
			String password = info[1];
			pair = new BasicNameValuePair(username, password);
		}
		return pair;
	}

	private NameValuePair getHttpsUserInfo(URI uri) {
		if ("https".equals(uri.getScheme()) || "http".equals(uri.getScheme())) {
			return getUserInfo(uri);
		} else {
			return null;
		}
	}

	private HttpUriRequest createPostRequest(String url, List<NameValuePair> params) {
		HttpPost request = new HttpPost(url);
		try {
		    URI uri = new URI(url);
            setHttpsCredentials(uri);
            if(params!=null){
                request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            }
		} catch (Exception e) {
			Log.e(TAG,"Create post request fail!", e);
		}
		return request;
	}


	/**
	 * 
	 * @param request
	 * @return <code>null</code> if an error has occurred
	 */
	private HttpResponse executeRequest(final HttpUriRequest request)throws ClientProtocolException,IOException {

		HttpResponse response = null;
		response = client.execute(request, localContext);
		int statusCode = response.getStatusLine().getStatusCode();
		Log.d(TAG,"httpCode:" + statusCode);
		return response;
	}

	public void setTimeOut(int sec){
		HttpParams params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(params, sec*1000);
		HttpConnectionParams.setSoTimeout(params, sec*1000);
	}
	public void setCookiePolicy(String policy){
		HttpParams params = client.getParams();
		String cookiePolicy = getCookiePolicy(policy);
		HttpClientParams.setCookiePolicy(params, cookiePolicy);
		//LOG.debug("cookiePolicy change to:"+cookiePolicy);
	}
	private String getUserAgent(Context context,String defaultHttpClientUserAgent){ 
        String versionName; 
        try { 
            versionName = context.getPackageManager().getPackageInfo( 
            		context.getPackageName(), 0).versionName; 
        } catch (NameNotFoundException e) { 
            throw new RuntimeException(e); 
        } 
        StringBuilder ret = new StringBuilder(); 
        ret.append(context.getPackageName()); 
        ret.append("/"); 
        ret.append(versionName); 
        ret.append(" ("); 
        ret.append("Linux; U; Android "); 
        ret.append(Build.VERSION.RELEASE); 
        ret.append("; "); 
        ret.append(Locale.getDefault()); 
        ret.append("; "); 
        ret.append(Build.PRODUCT); 
        ret.append(")"); 
        if(defaultHttpClientUserAgent!=null){ 
            ret.append(" "); 
            ret.append(defaultHttpClientUserAgent); 
        } 
        return ret.toString(); 
    } 
	public void close() {
        try {
            if (client != null) {
                client.getConnectionManager().shutdown();
                client=null;
                myClient=null;
                localContext=null;
            }
        } catch (Exception e) {
            Log.e(TAG,"httpclient shotdown error");
        }
    }
}
