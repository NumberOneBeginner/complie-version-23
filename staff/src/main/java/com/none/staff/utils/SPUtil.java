package com.none.staff.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;
/**
 * SharedPreferences存储数据方式工具类
 * 
 */
public class SPUtil {

	private static String spFileName = "RedApp";


	public static String COLLECT_STATE = "collect_state" ;
	public static String DELIVER_STATE = "deliver_state" ;
	public static String SET_AMOUNT_LAISEE = "amount_laisee" ;
	public static String SET_NOOF_LAISEE = "noof_laisee" ;
	public static String USER_ID = "userid" ;
	public static String STAFF_REQUEST_TIMEOUT = "staff_request_timeout" ;
	public static String DISPLAY_ACTIVITY = "display_activity" ;
	public static String ACTIVITY_SUBJECT = "activity_subject" ;
	public static String ACTIVITY_LIST = "activity_list" ;

			/**
			 * 向SharedPreferences中存入数据
			 * 
			 * @param context
			 *            上下文
			 * @param key
			 *            Key值
			 * @param value
			 *            键值
			 */
			public static void putValue(Context context, String key, String value) {
		Editor edit = context.getSharedPreferences(spFileName,
				Context.MODE_PRIVATE).edit();
		edit.putString(key, value);
		edit.commit();
	}

	public static void putBoolean(Context context, String key ,boolean falg){
		Editor edit = context.getSharedPreferences(spFileName,
				Context.MODE_PRIVATE).edit();
		edit.putBoolean(key, falg) ;
		edit.commit();
	}

	public static Boolean getBoolean(Context context,String key){
		SharedPreferences sp = context.getSharedPreferences(spFileName,
				Context.MODE_PRIVATE);
		return sp.getBoolean(key,false) ;
	}

	/**
	 * 读取SharedPreferences中的数据
	 * @param context
	 *            上下文
	 * @param key
	 *            Key值
	 * @return value键值
	 */
	public static String getValue(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(spFileName,
				Context.MODE_PRIVATE);
		return sp.getString(key, "");
	}

	/**
	 * 读取SharedPreferences中的数据
	 * 
	 * @param context
	 *            上下文
	 * @param key   "channel_id"，就是channelid user_id
	 *            Key值
	 * @return value键值
	 */
	public static String getID(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(
				"com.elink.protector_bussiness_preferences",
				Context.MODE_PRIVATE);
		return sp.getString(key, "");
	}


	public static void clear(Context context,String flag){
		Editor edit = context.getSharedPreferences(spFileName,
				Context.MODE_PRIVATE).edit();
		edit.remove(flag);
		edit.commit();

	}
	/**
	 * 序列化对象
	 * 
	 * @param obj
	 * @return
	 */
	@SuppressLint("NewApi")
	public static String saveObjectToShared(Object obj) {

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(output);
			oos.writeObject(obj);
			oos.close();
		} catch (IOException e) {

		}
		return Base64.encodeToString(output.toByteArray(), Base64.DEFAULT);

	}

	/**
	 * 反序列化对象
	 * 
	 * @param str
	 * @return
	 */
	@SuppressLint("NewApi")
	public static Object readObjectToShared(String str) {
		if (str != null) {
			byte[] base64Bytes = Base64.decode(str.getBytes(), Base64.DEFAULT);
			ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
			ObjectInputStream ois;
			try {
				ois = new ObjectInputStream(bais);
				return ois.readObject();
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
