package com.hsbc.greenpacket.util;

import java.util.HashMap;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtil {
    public final static String DEFAULT_LOCALTE = "en";
    private static Gson gson = null;

    public static Gson getGsonInstance() {
        synchronized (JsonUtil.class) {
            if (gson == null) {
                GsonBuilder builder = new GsonBuilder();
                builder.excludeFieldsWithoutExposeAnnotation();
                gson = builder.create();
            }
            return gson;
        }
    }

    public static <T> String getJsonFromJavaObject(T src) {

        try {

            return getGsonInstance().toJson(src);

        } catch (Exception e) {
            Log.e("JsonUtil", "Get Json from Java object", e);
        }

        return null;
    }

    public static <T> T getObjectFromJson(String mStr, Class<T> toJsonClass) {

        try {

            return getGsonInstance().fromJson(mStr, toJsonClass);

        } catch (Exception e) {
            Log.e("JsonUtil", "Get Json from Java object", e);
        }

        return null;
    }
    public static String getRspCallbackJson(String statusCode,HashMap<String,?> body){
        HashMap<String,HashMap<String,?>> rspMap=new HashMap<String,HashMap<String,?>>();
        HashMap<String,String> header=new HashMap<String,String>();
        header.put(JSONConstants.RESPONCE_STATUS_CODE, statusCode);
        rspMap.put(JSONConstants.RESPONCE_HEADER, header);
        if(body!=null){
            rspMap.put(JSONConstants.RESPONCE_BODY, body);
        }
        return getJsonFromJavaObject(rspMap);
    }
}
