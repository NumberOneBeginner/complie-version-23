
package com.none.staff.task;

import java.io.IOException;
import java.util.Date;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.none.staff.util.DownloadUtil;


public class GetRegionConfigTask extends AsyncTaskWithCallback<Void, Void, StringBuffer>{
	
	private final String configURL;
	private final Context context;
	private String eid;
	private final static String PERIOD_KEY="ConfigUpdatePeriod";
	java.util.Date begin=new java.util.Date();
	private boolean applyConfig=true;
	private static final String TAG="GetRegionConfigTask";
    private static final String CONFIGURATION = "config";
    private static final String CONFIG_CHECKING_TIME_STAMP = "configTimeStamp";
    private static final String CONFIG_EXPIRE_PERIOD = "configExpirePeriod";
    public boolean isConfigUpdate=false;
	public GetRegionConfigTask(final Context context, final ActivityCallback callback, final int ref, String configURL,String eid) {
		super(callback, ref);
		if (context == null) {
			throw new IllegalArgumentException("owner must not be null");
		}
		this.context=context;
		this.configURL=configURL;
		this.eid=eid;
	}
    public GetRegionConfigTask(final Context context, final ActivityCallback callback, final int ref, String configURL,String eid,boolean apply) {
        super(callback, ref);
        if (context == null) {
            throw new IllegalArgumentException("owner must not be null");
        }
        this.context=context;
        this.configURL=configURL;
        this.eid=eid;
        this.applyConfig=apply;
    }
	public String getEid() {
		return eid;
	}

	@Override
	protected StringBuffer doInBackground(Void... params) {
		try{
            StringBuffer buf = getConfigurationFromPrefs(context, eid);
            if (buf != null) {
                long refreshPeriod = getRefreshPeriod(buf);
                if (configurationExpired(this.eid, refreshPeriod) && DownloadUtil.deviceOnline(context)) {
                    // cache expried then download it
                    buf = downloadAndSaveJsonConfig(this.configURL, this.eid);
                } else {
                    Log.d(TAG,"==use cache regional config");
                }
            } else {
                Log.d(TAG,"==cache config not exist");
                buf = downloadAndSaveJsonConfig(this.configURL, this.eid);
            }
            return buf;
		}catch(Exception e){
			this.setError(FAILED, "get regional config error.");
			Log.e(TAG,"get regional config error",e);
		}catch(OutOfMemoryError error){
		    this.setError(FATAL);
            Log.e(TAG,"get regional config outofmemory error",error);
		}
		return null;
	}
	private StringBuffer downloadAndSaveJsonConfig(String url,String eid) throws ClientProtocolException,IOException,Exception{
	    StringBuffer buf = downloadRegionConfig(url);
        saveConfiguration(buf,eid);
        return buf;
	}
	private StringBuffer downloadRegionConfig(String url)throws ClientProtocolException,IOException,Exception{
		if(url==null||url.length()==0){
			return null;
		}
		Date begin=new Date();
		DownloadUtil downloadUtil = new DownloadUtil(this.context);
		StringBuffer strBuf = downloadUtil.downloadText(url);
		Log.d(TAG,"==download regioan config time:"+(new java.util.Date().getTime()-begin.getTime()));
		return strBuf;
	}
	public static StringBuffer getConfigurationFromPrefs(Context ctx,String eid){
	    SharedPreferences prefs = getJsonConfigPrefs(ctx,eid);
		String jsonStr=prefs.getString(getJsonConfigPrefsKey(eid), null);
		if(jsonStr!=null){
			return new StringBuffer(jsonStr);
		}
		return null;
	}

	/**
	 * replace invalid char in the json string
	 * @param jsonStrBuf
	 */
	public void saveConfiguration(StringBuffer jsonStrBuf,String eid)throws JSONException{
		if(jsonStrBuf==null || jsonStrBuf.toString().length()==0){
			return;
		}
		String str = jsonStrBuf.toString();
		Date begin=new Date();
		SharedPreferences prefs = getJsonConfigPrefs(context,eid);	  
		//verify the downloaded string
		JSONObject newConfig = new JSONObject(str);
		JSONObject savedConfig =null;
		//if just download in background and not apply to app then set the flag to true.
        if (!applyConfig) {
            String savedConfigStr = prefs.getString(getJsonConfigPrefsKey(eid), null);
            if (savedConfigStr != null) {
                try {
                    savedConfig = new JSONObject(savedConfigStr);
                } catch (Exception e) {
                    deleteDownloadedJsonConfig(context, eid);
                }
            }
            if (newConfig != null && savedConfig != null && newConfig.toString().equals(savedConfig.toString())) {
                Log.d(TAG,"====config not update===");
            } else {
                Log.d(TAG,"====config have update===");
                isConfigUpdate=true;
            }           
        }
		Editor editor = prefs.edit();
		editor.putString(getJsonConfigPrefsKey(eid), str);
		if(editor.commit()){
			updateCheckingTimestamp(eid);
            long refreshPeriod = newConfig.optInt(PERIOD_KEY)*3600000L;
            if(refreshPeriod>0){
                updateExpirePeriod(eid,refreshPeriod);
            }
		}
		
	}
	public static SharedPreferences getJsonConfigPrefs(Context ctx,String eid){
	    SharedPreferences prefs = ctx.getSharedPreferences(CONFIGURATION+"_"+eid, Context.MODE_PRIVATE);
	    return prefs;
	}
	public static String getJsonConfigPrefsKey(String eid){
	    return CONFIGURATION + "_" + eid;
	}
	
	
	public void updateCheckingTimestamp(String eid) {

		Date now=new Date();
		long timestamp=now.getTime();
		SharedPreferences prefs = getJsonConfigPrefs(context,eid);
		Editor editor = prefs.edit();
		editor.putLong(CONFIG_CHECKING_TIME_STAMP+"_"+eid,timestamp);
		editor.commit();
	}
	public void updateExpirePeriod(String eid,long expireTime) {
        SharedPreferences prefs = getJsonConfigPrefs(context,eid);
        Editor editor = prefs.edit();
        editor.putLong(CONFIG_EXPIRE_PERIOD+"_"+eid,expireTime);
        editor.commit();
    }
	/**
	 * if the timestamp is not exist then will treat it as expired.
	 * @return
	 */
	public boolean configurationExpired(String eid,long duration) {
		Date now=new Date();
		long currentTimestamp=now.getTime();
		SharedPreferences prefs = getJsonConfigPrefs(context,eid);
		long timestamp = prefs.getLong(CONFIG_CHECKING_TIME_STAMP+"_"+eid,-1L);
		if(currentTimestamp-timestamp > duration ||currentTimestamp-timestamp<0){
			return true;
		}else{
			return false;
		}
	}
	public long getRefreshPeriod(StringBuffer jsonStrBuf){
		if(jsonStrBuf==null){
			return -1L;
		}
		try{
			String str= jsonStrBuf.toString();
			JSONObject jobject = new JSONObject(str);  
			return jobject.getInt(PERIOD_KEY)*1000;		
		}catch(JSONException e){
			Log.e(TAG,"Get refresh period error");
		}
		return -1L;
		
	}
	public static boolean configurationExpired(Context context,String eid){
        Date now=new Date();
        long currentTimestamp=now.getTime();
	    SharedPreferences prefs = getJsonConfigPrefs(context,eid);
        long period = prefs.getLong(CONFIG_EXPIRE_PERIOD+"_"+eid,3600000L);//default is 1 hour
        long timestamp = prefs.getLong(CONFIG_CHECKING_TIME_STAMP+"_"+eid,-1L);
        if(currentTimestamp-timestamp > period ||currentTimestamp-timestamp<0){
            return true;
        }else{
            return false;
        }
    }
    public static void deleteDownloadedJsonConfig(Context ctx,String eid) {
        Log.d(TAG,"==delete config");
        if (eid != null) {
            SharedPreferences prefs = GetRegionConfigTask.getJsonConfigPrefs(ctx, eid);
            Editor editor = prefs.edit();
            editor.clear();
            editor.commit();
        }
    }
}
