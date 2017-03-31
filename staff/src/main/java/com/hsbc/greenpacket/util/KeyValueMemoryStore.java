package com.hsbc.greenpacket.util;

import java.util.HashMap;

import android.util.Log;

public class KeyValueMemoryStore {
	
	private static HashMap<String, HashMap<String, String>> memoryStore = new HashMap<String, HashMap<String,String>>();	

	public static void clearmData(){		
		
		if(memoryStore != null){
			memoryStore.clear();
		}
	}
	
	public static void setmData(String key, String data,String entityId) {
	    HashMap<String,String> map = memoryStore.get(entityId);
	    if(map==null){
	        map=new HashMap<String,String>();
	    }
	    map.put(key, data);
	    memoryStore.put(entityId, map);
	    Log.d("KeyValueMemoryStore","setmData:"+memoryStore);
	}
	public static String getmEntityData(String entityId,String key) {
	    HashMap<String,String> map = memoryStore.get(entityId);
        if(map!=null){

            Log.d("KeyValueMemoryStore","getValue:"+map.get(key));
            return map.get(key);
            
        }
        Log.d("KeyValueMemoryStore","getmEntityData:"+map);
        
        return null;        
	} 
}
