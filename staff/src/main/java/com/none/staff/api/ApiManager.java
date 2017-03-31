package com.none.staff.api;

import java.util.HashMap;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.none.staff.utils.HttpClientUtils;
import com.none.staff.utils.SPUtil;
import com.none.staff.utils.ServicesHolder;

/*
 * ApiManager
 */
public class ApiManager {

	/**懒汉式单例 + 线程安全***/
	private static ApiManager apiManager ;
	private ApiManager() {}
	
	public static ApiManager getInstance(){
		if(null == apiManager){
			synchronized (ApiManager.class) {
				if(null == apiManager){
					apiManager = new ApiManager() ;
				}
			}
		}
		return apiManager ;
	}
	
	/***
	 * 根据userId获取是老板还是员工API
	 */
	public void updateUser(Context context,AsyncHttpResponseHandler handler){
		HttpClientUtils.getInstance().post(ServicesHolder.api(ServicesHolder.UPDATE_USER), SPUtil.getValue(context,SPUtil.USER_ID), context, handler) ;
	}
	/**
	 * 发送声波的API
	 * @param context
	 * @param nums
	 * @param money
	 * @param handler
	 */
	public void start(Context context,String nums,String money,AsyncHttpResponseHandler handler){
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("nums", nums) ;
		params.put("money", money) ;
		HttpClientUtils.getInstance().post(ServicesHolder.api(ServicesHolder.GAME_START), SPUtil.getValue(context,SPUtil.USER_ID), params, context, handler) ;
	}
	
	/**
	 * 接收声波并获利与否的API
	 * @param context
	 * @param gameId
	 * @param handler
	 */
	public void joinGame(Context context,String gameId,AsyncHttpResponseHandler handler){
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("gameId", gameId) ;
		HttpClientUtils.getInstance().post(ServicesHolder.api(ServicesHolder.JOIN_GAME), SPUtil.getValue(context,SPUtil.USER_ID), params, context, handler) ;
	}
	/**
	 * Boss成功派利的数量
	 * @param context
	 * @param soundId
	 * @param handler
	 */
	public void endGame(Context context,String soundId,AsyncHttpResponseHandler handler){
		HashMap<String , Object> params = new HashMap<String, Object>() ;
		params.put("soundId", soundId) ;
		
		HttpClientUtils.getInstance().post(ServicesHolder.api(ServicesHolder.END_GAME), SPUtil.getValue(context,SPUtil.USER_ID), params, context, handler) ;
		
	}
	/**
	 * Boss请求中奖人员信息
	 * @param context
	 * @param handler
	 */
	public void sendInfoToBoss(Context context,AsyncHttpResponseHandler handler){
		
		HttpClientUtils.getInstance().post(ServicesHolder.api(ServicesHolder.SEND_TO_BOSS), SPUtil.getValue(context,SPUtil.USER_ID), context, handler) ;
		
	}
	/**
	 * Boss发送或取消派利 API
	 * @param context
	 * @param flag
	 * @param handler
	 */
	public void sendOrCancel(Context context,String flag,String gameId, AsyncHttpResponseHandler handler){
		HashMap<String , Object> params = new HashMap<String, Object>() ;
		params.put("flag", flag) ;
		params.put("gameId", gameId) ;
		HttpClientUtils.getInstance().post(ServicesHolder.api(ServicesHolder.BOSS_SEND_OR_CANCLE_PALISEE), SPUtil.getValue(context,SPUtil.USER_ID), params, context, handler) ;
	}
	/**
	 * Staff请求是否中奖 API
	 * @param context
	 * @param gameId  播放的游戏ID
	 * @param handler
	 */
	public void  sendResultToStaff(Context context,String gameId,AsyncHttpResponseHandler handler){
		HashMap<String , Object> params = new HashMap<String, Object>() ;
		params.put("gameId", gameId) ;
		HttpClientUtils.getInstance().post(ServicesHolder.api(ServicesHolder.SEND_RESULT_TO_STAFF), SPUtil.getValue(context,SPUtil.USER_ID), params, context, handler) ;
	}

	public void setPushInfo(Context context,
			String uuid,String channelId,String userId,String device_type, 
			AsyncHttpResponseHandler handler) {
	
		Log.i("111", "setPushInfo");
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("udid",uuid) ;
		params.put("channelId",channelId) ;
		params.put("baiduUserId",userId) ;
		params.put("device_type",device_type) ;

		Log.i("111", "params"+params.toString());
		HttpClientUtils.getInstance().post(
				ServicesHolder.api(ServicesHolder.PUSHINFO),SPUtil.getValue(context, SPUtil.USER_ID), params,context, handler);	

	}

}
