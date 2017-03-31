package com.hsbc.greenpacket.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.hsbc.greenpacket.activities.PrefConstants;


public class NameValueStore {
	private static final String TAG = "NameValueStore";
    private final Context context;
	private final String NAME_VALUE_STORE = "nameValueStore";
	SharedPreferences prefs;
	public NameValueStore(Context context){
		this.context = context.getApplicationContext();
		String eid = "0";
		prefs = this.context.getSharedPreferences(NAME_VALUE_STORE+"_"+eid, Context.MODE_PRIVATE);
	}
	public void setAttribute(String key,String value){
		Editor editor = prefs.edit();
		editor.putString(key,value);
		editor.commit();
	}
	/**
	 * not exist the value then return "";
	 * @param key
	 * @return
	 */
	public String getAttribute(String key){
		return prefs.getString(key, "");
	}
	
    /**
     * Remove record by key prefix.
     * @param context
     */
    public void cleanUnzipRecord(String keyPrefix){
    	Editor editor = prefs.edit();					
		//remove saved config when app upgrade,avoid exist no useful config cache in the shared pref.
		Map<String,?> map=prefs.getAll();
		Set<String> keys=map.keySet();
		Iterator<String> it=keys.iterator();
		while(it.hasNext()){
			String key=it.next();
			if(key.startsWith(keyPrefix)){
				Log.d(TAG,"==delete old regional config:"+key);
				editor.remove(key);
			}
		}
		editor.commit();
    }
    
    
  /**
   * 
   * @description: save key-vale in app level
   * @author [York Li 2013-12-27]
   * @param context
   * @param key
   * @param value
   */
    public static void saveKeyValueInAppLevel(Context context,String key,String value){
        SharedPreferences prefs = context.getSharedPreferences(PrefConstants.PREFS_NAME, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putString(key,value);
        editor.commit();
    }
    /**
     * 
     * @description: get saved key-vale in app level
     * @author [York Li 2013-12-27]
     * @param context
     * @param key
     * @return
     */
    public static String getSavedValueInInAppLevel(Context context,String key){
        SharedPreferences prefs = context.getSharedPreferences(PrefConstants.PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(key,null);
    }
}
