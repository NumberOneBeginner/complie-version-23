package com.hsbc.greenpacket.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.hsbc.greenpacket.activities.Constants;

import com.none.staff.util.IOUtils;

public class HSBCHttpClient {
	
	private final static int CONN_TIMEOUT = 20000; // in milsec default is 30s

    protected static final String TAG = "HSBCHttpClient";

	private  HttpContext localContext = null;
	private  DefaultHttpClient client = null;
	private  BasicCookieStore cookieStore = null;
	//private final static String USER_AGENT = "Mozilla/5.0 (Linux; U; Android 2.1 Desire;) AppleWebKit/525.10+ (KHTML, like Gecko) Version/3.0.4 Mobile Safari/523.12.2";
	private static HSBCHttpClient myClient = null;
	private  boolean allowAllSSL=true;
	private  String cookiePolicy = CookiePolicy.NETSCAPE;
	public Context context;
	
	private HSBCHttpClient(Context context) {
		try{
			this.context=context;
            
			this.cookiePolicy = getCookiePolicy(cookiePolicy);
			Log.d(TAG,"HSBCHttpClient instance created");
		}catch(Exception e){
			Log.e(TAG,"HSBCHttpClient initial error!",e);
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
	
	/**
	 * @description The synchronous of "Synchronized" qualifier need more time-consuming
	 * @author Cherry 
	 * @param context
	 * @return
	 */
	public static HSBCHttpClient getInstance(Context context) {
		/**
		 * fix ISR defect #AND02, QCID #9239
         * JW/Nina 6-Nov-2013
		 */
		    synchronized (HSBCHttpClient.class) {
		    	if (myClient == null) {
					myClient = new HSBCHttpClient(context.getApplicationContext());	
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
	            if(HSBCHttpClient.this.allowAllSSL){
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

	        /** Gets an HTTPS socket factory with SSL Session Caching if such support is available, otherwise falls back to a non-caching factory 
	         * @return 
	         */
	        protected SocketFactory getHttpsSocketFactory(){ 
	        	return SSLSocketFactory.getSocketFactory();
	
	        }
	        protected SocketFactory getInSecuSocketFactory(){
	        	try{
	    			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	    			trustStore.load(null, "".toCharArray());
	    			SSLSocketFactory sf = new HSBCSSLSocketFactory(trustStore);
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
		// setProxyInfo(client);
		// Create a local instance of cookie store
		cookieStore = new BasicCookieStore(){
			@Override
			public synchronized void addCookie(Cookie cookie) {
					if(cookie!=null&&cookie.getExpiryDate()!=null){
						BasicClientCookie newCookie =  (BasicClientCookie)cookie;
						String[] datePattern={"d MMM yy HH:mm:ss z"};
						for(int i=0;i<datePattern.length;i++){
							try{
								java.text.SimpleDateFormat df=new java.text.SimpleDateFormat(datePattern[i],Locale.US);
								java.util.Date ed=df.parse(newCookie.getExpiryDate().toGMTString());
								newCookie.setExpiryDate(ed);
								//Log.d(TAG,"exipird:"+cookie.getExpiryDate().getTime());
								break;
							}catch(Exception e){
								Log.e(TAG,"Transform the cookie expire time error",e);
							}
						}
						super.addCookie(newCookie);	
					}else{
						super.addCookie(cookie);
					}
					/**Sync the original cookie to webview***/
					importCookie2Webview(cookie);
			}	
		};
		// Create local HTTP context
		localContext = new BasicHttpContext();
		// Bind custom cookie store to the local context
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);		
		HttpConnectionParams.setConnectionTimeout(params, HSBCHttpClient.CONN_TIMEOUT);
		HttpConnectionParams.setSoTimeout(params, HSBCHttpClient.CONN_TIMEOUT);
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

	public StringBuffer connectURL(String url, String cookieStr) throws ClientProtocolException,IOException{
		HttpResponse response = getHttpResponse(url, cookieStr);
		if (response != null) {
			return getResponseStr(response);
		} else {
			return null;
		}

	}
	public HttpResponse getHttpResponse(String url, String cookieStr) throws ClientProtocolException,IOException{
		return getHttpResponse(url,cookieStr,true);
	}
	public HttpResponse getHttpResponse(String url, String cookieStr,boolean setHeader) throws ClientProtocolException,IOException{
		if (cookieStr != null) {
			setCookie(url, cookieStr);
		}
		HttpUriRequest request = createRequest(url);
		if(request==null){
			return null;
		}
		/** Set default header for every request***/
        //Added addDeviceStatus by TW [Aug-2013] for end point security
        boolean addDeviceStatus=getDeviceStatusFlag(url);
        if(setHeader){
            this.setDefaultHeader(this.context, request,addDeviceStatus);
		}
		HttpResponse response = executeRequest(request);
		return response;
	}

	public StringBuffer connectURLByPost(String url, String cookieStr, List<NameValuePair> params)throws ClientProtocolException,IOException {
		setCookie(url, cookieStr);
		HttpUriRequest request = createPostRequest(url, params);
		/** Set default header for every request***/
        //Added addDeviceStatus by TW [Aug-2013] for end point security
        boolean addDeviceStatus=getDeviceStatusFlag(url);
        this.setDefaultHeader(this.context, request,addDeviceStatus);
		HttpResponse response = executeRequest(request);
		if (response != null) {
			return getResponseStr(response);
		} else {
			return null;
		}

	}
	
	/**
     * Check if there is a "devicestatus" parameter after the url . This is for end point security checking .
     *  If no such a parameter, it will return false.
     *  Added by TW [Aug-2013]
     * @param url
     * @return The boolean value of "devicestatus" parameter.
     */
    private boolean getDeviceStatusFlag(String url){
        
        return false;
    }
    /**
     * @param context
     * @param request
     * @param addDeviceStatus If you want to put device security status in header, pass true. (Added by TW[Aug-2013] for eps. )
     */
    public void setDefaultHeader(Context context,HttpUriRequest request,boolean addDeviceStatus){
		if(request==null){
			return;
		}
		Header header=new Header();
        Map<String, String> defaultHeaders=header.createHeaders(context,addDeviceStatus);
		java.util.Iterator<String> e = defaultHeaders.keySet().iterator();
		while(e.hasNext()){
			String key=e.next();
			request.addHeader(key, defaultHeaders.get(key));
		}
	}
	public static List<NameValuePair> parseURL(String url) {
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		if (url == null) {
			return list;
		}
		try {
			String[] params = url.split("&");
			for (int i = 0; i < params.length; i++) {
				String s = params[i];
				if (s != null) {
					/*String[] nvs = s.split("=");
					String key = null;
					String value = "";
					if (nvs != null && nvs.length > 0) {
						key = URLDecoder.decode(nvs[0],HTTP.UTF_8);
					}
					if (nvs != null && nvs.length > 1) {
						value = URLDecoder.decode(nvs[1],HTTP.UTF_8);
						//value = URLDecoder.decode(s.substring(s.indexOf("=")+1),HTTP.UTF_8);
					}
					if (key != null) {
						NameValuePair nv = new BasicNameValuePair(key, value);
						list.add(nv);
					}*/
					String key = null;
					String value = "";
					int pos=s.indexOf("=");
					if(pos!=-1){
						key=URLDecoder.decode(s.substring(0,pos),HTTP.UTF_8);
						value=URLDecoder.decode(s.substring(pos+1),HTTP.UTF_8);
					}
					if (key != null) {
						NameValuePair nv = new BasicNameValuePair(key, value);
						list.add(nv);
					}
				}

			}
		} catch (Exception e) {
			Log.e(TAG,"parse the URL parameter error", e);
		}
		return list;
	}

	private void setCookie(String url, String cookieStr) {
		List<BasicClientCookie> list = createListByCookieStr(cookieStr);
		for (int i = 0; i < list.size(); i++) {
			try{
				BasicClientCookie cookie = list.get(i);
				String domain = getDomain(url);
				//Log.d(TAG,"domain=" + domain);
				cookie.setDomain(domain);
				cookie.setPath("/");
				cookieStore.addCookie(cookie);
			}catch(Exception e){
				Log.e(TAG,"set cookie error!",e);
			}
		}
	}

	private String getDomain(String url) {
		if (url.indexOf("//") != -1) {
			String host = url.substring(url.indexOf("//") + 2);
			if (host.indexOf("/") != -1) {
				host = host.substring(0, host.indexOf("/"));
			}
			return host;
		} else {
			return url;
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
			    Log.e(TAG,"Http status code:"+response.getStatusLine().getStatusCode()+" "+response.getStatusLine().getReasonPhrase());
			}
			/*
			 * Cherry[5-Nov-2012]
			 * Fix ISR Defect #3983
			 * Unrestricted reads in the Android native application
			 * Catch OutOfMemoryError to avoid excessive resource consumption
			 * at Java runtime causing system crash
			 * 
			 */
		} catch(OutOfMemoryError memError){
			Log.d(TAG,"OutOfMemoryError in getResponseStr");
			
		}finally {
			/*
			 * Cherry[5-Nov-2012]
			 * Fix ISR Defect #3978 - Unreleased resources: Streams may introduce reliability
			 * issues in the Android native application
			 */
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


	public List<BasicClientCookie> createListByCookieStr(String cookieStr) {
		List<BasicClientCookie> list = new ArrayList<BasicClientCookie>();
		try{
			if (cookieStr != null) {
				String[] cookies = cookieStr.split(";");
				if (cookies != null) {
					for (int i = 0; i < cookies.length; i++) {
						String cookiePair = cookies[i];
						if (cookiePair != null && cookiePair.indexOf("=") != -1) {
							//String[] co = cookiePair.split("=");
							int pos=cookiePair.indexOf("=");
							String cookieName = null;
							String cookieValue = "";
//							if (co != null && co.length > 0) {
//								cookieName = co[0];
//							}
//							if (co != null && co.length > 1) {
//								//cookieValue = co[1];
//								cookieValue = cookiePair.substring(cookiePair.indexOf("=")+1);
//							}
							if(pos!=-1){
								cookieName=cookiePair.substring(0,pos);
								cookieValue=cookiePair.substring(pos+1);
							}
							if (cookieName != null) {
								BasicClientCookie cookie = new BasicClientCookie(cookieName, cookieValue);
								list.add(cookie);
							}
						}
					}
				}
			}
		}catch(Exception e){
			Log.e(TAG,"parse cookie string error!",e);
		}
		return list;
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
		    /**
		     * York 2012/11/21
		     * To support splash password for post method proxy
		     */
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
		//Log.d(TAG,"httpCode:" + statusCode);
		return response;
	}
	/**
	 * clear all cookie in httpclient
	 */
	public void clearAllCookie(){
		cookieStore.clear();
	}
	public void getAllCookie(){
		Log.d(TAG,"cookie:"+cookieStore.getCookies());
	}
	/**
	 * www.cd537.p2g.netd2.hsbc.com.hk
	 * @param name
	 * @param domain
	 * @return  if null then return ""
	 */
	public String getCookie(String name,String domain){
		if(name==null||domain==null){
			return "";
		}
		List<Cookie> cookieList = cookieStore.getCookies();
		for(int i=0;i<cookieList.size();i++){
			Cookie cookie = cookieList.get(i);
			if(name.equals(cookie.getName())&&domain.equals(cookie.getDomain())){
				return cookie.getValue();
			}
		}
		return "";
	}
	public void setCookie(String domain,String name,String value){
		//Log.d(TAG,"set cookie from client domain:"+domain+"name:"+name+ "="+value);
		BasicClientCookie cookie = new BasicClientCookie(name, value);
		cookie.setDomain(domain);
		cookie.setPath("/");
		cookieStore.addCookie(cookie);
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
		//Log.d(TAG,"cookiePolicy change to:"+cookiePolicy);
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
    
    public BasicCookieStore getCookieStore() {
        return cookieStore;
    }
    /**
     * sync cookie in the httpclient to webview by generating cookie string 
     * @author York Y K LI[Jan 21, 2013]
     * @param cookie
     *
     */
    public  void importCookie2Webview(Cookie cookie){
        if (cookie!=null){  
            //Log.d(TAG,"=================httpclient add cookie:{}==================",cookie.getName());
            CookieSyncManager.createInstance(this.context);  
            CookieManager cookieManager = CookieManager.getInstance();            
            StringBuffer cookieStrBuf =new StringBuffer();
            cookieStrBuf.append(cookie.getName()).append("=").append(cookie.getValue());
            cookieStrBuf.append("; ").append(Constants.DOMAIN).append("=").append(cookie.getDomain());
            if(cookie.getExpiryDate()!=null){
                cookieStrBuf.append("; ").append(Constants.EXPIRES).append("=").append(cookie.getExpiryDate().toGMTString());
            }
            cookieManager.setCookie(cookie.getDomain(), cookieStrBuf.toString());  
            CookieSyncManager.getInstance().sync();  
        } 
    }
	
    /**
     * Clear Cookies:According to the name to remove 
     * @author Cherry CHEN
	 * @date 2013-03-28
	 */
	public void clearCookiesByWhiteList(ArrayList<HashMap<String, String>> mWhiteLists) {
		List<Cookie> cookieList = cookieStore.getCookies();
		List<Cookie> listRestore = new ArrayList<Cookie>();
//		List<Cookie> listDelete = new ArrayList<Cookie>();
		Log.d(TAG,"clean some cookie before:"+cookieStore.getCookies());
		int size = cookieList.size();
		if(cookieList!=null && size>0){
			for(int i=0;i<size;i++){
				Cookie cookie = cookieList.get(i);				
				if(mWhiteLists!=null&&mWhiteLists.size()>0){
					for(int j=0;j<mWhiteLists.size();j++){
						HashMap<String, String> mWhiteList  = mWhiteLists.get(j);
						String mWhiteListName = mWhiteList.get(JSONConstants.WHITE_LIST_NAME);
						if(!StringUtil.IsNullOrEmpty(mWhiteListName) && cookie!=null){
							if(mWhiteListName.equals(cookie.getName())){	
								listRestore.add(cookie);
							}
						}
					}
				}					
				
			}
		}
		
        Cookie[] array = (Cookie[])listRestore.toArray(new Cookie[size]);
        cookieStore.clear();
        cookieStore.addCookies(array);
        /*York Y K LI[May 9, 2013]
         * fix production issue,
         * Need keep the white list cookie for webview.
         */
        importWhitelistCookies2Webview(array);
        //Log.d(TAG,"clean some cookie later:"+cookieStore.getCookies());
    }
	
    /**
     * import cookie to webview with coolie array
     * @author York Y K LI[May 9, 2013]
     * @param array
     *
     */
    public void importWhitelistCookies2Webview(Cookie[] array){
        if(array!=null){
            for(int i=0;i<array.length;i++){
                importCookie2Webview(array[i]);
            }
        }
    }
	
    public void close() {
        try {
            if (client != null) {
                client.getConnectionManager().shutdown();
                client=null;
                myClient=null;
                cookieStore=null;
                localContext=null;
            }
        } catch (Exception e) {
            Log.e(TAG,"httpclient shotdown error",e);
        }
    }
}
