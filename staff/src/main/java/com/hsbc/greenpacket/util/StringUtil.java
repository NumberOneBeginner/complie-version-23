package com.hsbc.greenpacket.util;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Iterator;

import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.hsbc.greenpacket.activities.Constants;

/**
 * String tool class
 * 
 * @author Cherry
 * 
 */
public class StringUtil {

	private static final String TAG = "StringUtil";

    /**
	 * Judge whether a string is empty
	 */
	public static Boolean IsNullOrEmpty(String pStr){
		return pStr == null || pStr.trim().equals("") || pStr.trim().length() <= 0;
		
	}
	
	/**
	 * Change the string into integer
	 */
	public static int StringToInt(String pStr){
		return Integer.valueOf(pStr.replaceAll("[^0-9.]", ""));
		
	}
	
	public static String getJsonString(JSONObject object,String key){
	    try {
            if(object!=null&&object.has(key)){
                return object.getString(key);
            }else{
                return "";
            }
        } catch (JSONException e) {
        	Log.e(TAG,key+"is not in JSONObject");
            return "";
        }
	}
	/***
	 * @DESCRIPTION when http use "GET" method request,we need put Json String into name1=value1&name2=value2
	 * @author 43734332 Cherry
	 * @date 2013-05-31
	 * @param str
	 * @return
	 */
	public static String getGetParaFromJson(String str){
		try{
			if(str==null){
				return "";
			}
			StringBuffer paraBuf=new StringBuffer();
			JSONObject jobject = new JSONObject(str);
			if(jobject!=null){
				 Iterator e =jobject.keys();
				 int i=0;
				 while(e.hasNext()){
					 Object name=e.next();
					 Object value="";
					 if(name!=null&&name instanceof String){
						 if(i>0){
							 paraBuf.append("&");
						 }
						 value = jobject.get((String)name);
						 if(value!=null&&value instanceof String){
							 paraBuf.append(URLEncoder.encode((String)name,HTTP.UTF_8)).append("=").append(URLEncoder.encode((String)value,HTTP.UTF_8));
						 }						 
					 }
					 i++;
				 }
				 return paraBuf.toString();
			}
		}catch(Exception e){
			Log.e(TAG,"Get para from Json error",e);
		}
		return "";
	}
	
	public int compareVersions(String left, String right) {
	    if (left.equals(right)) {
	      return 0;
	    }
	    int leftStart = 0, rightStart = 0, result;
	    do {
	      int leftEnd = left.indexOf('.', leftStart);
	      int rightEnd = right.indexOf('.', rightStart);
	      Integer leftValue = Integer.parseInt(leftEnd < 0
	          ? left.substring(leftStart)
	          : left.substring(leftStart, leftEnd));
	      Integer rightValue = Integer.parseInt(rightEnd < 0
	          ? right.substring(rightStart)
	          : right.substring(rightStart, rightEnd));
	      result = leftValue.compareTo(rightValue);
	      leftStart = leftEnd;
	      rightStart = rightEnd;
	    } while (result == 0 && leftStart > 0 && rightStart > 0);
	    if (result == 0) {
	      if (leftStart > rightStart) {
	        return 1;
	      }
	      if (leftStart < rightStart) {
	        return -1;
	      }
	    }
	    return result;
	}
	
	/**
	 * @description Convert a given string to a SHA-512 hashed string
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String convertStringToSha512(final String str) throws Exception {
		MessageDigest md = MessageDigest.getInstance(Constants.ALGORITHM);
		md.update(str.getBytes("UTF-8"));
		byte[] bytes = md.digest();
		
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			String tmp = Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1);
			buffer.append(tmp);
		}
		return buffer.toString();
	}
    
}
