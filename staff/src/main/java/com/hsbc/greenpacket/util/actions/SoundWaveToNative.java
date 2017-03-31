package com.hsbc.greenpacket.util.actions;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebView;

import com.hsbc.greenpacket.activities.Constants;
import com.hsbc.greenpacket.hook.Hook;
import com.hsbc.greenpacket.process.ProcessUtil;
import com.hsbc.greenpacket.util.AlgorithmUtil;
import com.hsbc.greenpacket.util.StringUtil;

import com.none.staff.activity.BossSoundwaveActivity;
import com.none.staff.activity.StaffApplication;
import com.none.staff.activity.StaffSoundWaveActivity;
import com.none.staff.domain.UserInfo;
import com.none.staff.util.DownloadUtil;
import com.none.staff.utils.CommonUtils;
import com.none.staff.utils.SPUtil;
//location.href = "hsbc://function=SoundWaveToNative"
public class SoundWaveToNative extends HSBCURLAction implements IHsbcUrlAction{
	
	private StaffApplication application ;
	
	private WebView webview = null;

	@Override
	public void execute(Context context, WebView webview, Hook hook) {
		this.webview = webview ;
		application = (StaffApplication) context.getApplicationContext() ;
		try {
			 Map<String, String> map = this.getParams();
			 Log.e("soundmap", map.toString()) ;
			String storeValue =  CommonUtils.getStoredValue(Constants.CUSTOMER_INFO,context) ;
			
			  if(!StringUtil.IsNullOrEmpty(storeValue)){
	                String aesKey=AlgorithmUtil.gen256Key(HookConstants.SECURE_KEY);
	                storeValue=new String(AlgorithmUtil.aesDecrypt(aesKey,Base64.decode(storeValue, Base64.NO_WRAP)));
	            }
			  
			// {"location":"CN","secureToken":"d0a86970cfb52b2e2f4749943ee990b3e941a26a9bff998ef564ed9d2a8d5006","account":"99993001","fullName":"Test"}

			UserInfo userInfo = UserInfo.parse(storeValue);
			Log.e("userinfo", userInfo.toString());
			if (null !=userInfo) {
				application.setUser(userInfo);
				Log.e("userinfo", userInfo.toString());
				SPUtil.putValue(context, SPUtil.USER_ID, userInfo.getAccount());
				if (!TextUtils.isEmpty(userInfo.getSecureToken())) {
					Intent intent = new Intent(context,BossSoundwaveActivity.class);
					intent.putExtra("location", userInfo.getLocation());
					((Activity) context).startActivityForResult(intent,HookConstants.ACTION_RESULT);
				} else {
					Intent intent = new Intent(context,StaffSoundWaveActivity.class);
					intent.putExtra("location", userInfo.getLocation());
					((Activity) context).startActivityForResult(intent, HookConstants.ACTION_RESULT) ;
				}
            
   		}
			 String key = map.get(HookConstants.SOUNDWAVE_TO_NATIVE) ;
			 Log.e("suondkey", key) ;
		} catch (Exception e) {
		}
	}
	@Override
	public void dataProcess(Context context, WebView webview, Handler mHandler,
			String dataValue, JSONObject jo, Hook hook) throws JSONException,
			HookException {
		
	}      

	@Override
	public void onActionResult(Context context, Handler mHandler,
			int resultCode, Intent data) {
		
		  if(resultCode == Activity.RESULT_OK){
		      Log.d("SoundWaveToNative", "on Action result");
		      //如果想跳其他地方，应该由Intent data这里把目标地址传过来
                String url = "file:///web/postlogon/myLaisee/myLaiSee.html";
                url = ProcessUtil.localUrlIntercept(url, DownloadUtil.getClientPackPath(context));
                //String jumpUrl = "hsbc://function=PageTransition&url="+url+"&slide=RTL";
                Message msg=mHandler.obtainMessage();
                msg.what=HookConstants.PAGE_TRANSITION_MSG;
                Bundle bundle=new Bundle();
                bundle.putString(Constants.URL, url);
                msg.setData(bundle);
                mHandler.sendMessage(msg);
	        }
	}
	

}
