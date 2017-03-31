package com.hsbc.greenpacket.util.actions;

import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.WebView;

import com.hsbc.greenpacket.hook.Hook;
import com.hsbc.greenpacket.util.StringUtil;

import com.none.staff.activity.staff;

public class GoToSCAAction extends HSBCURLAction {
	private static final String TAG = "GoToSCAAction";

    @Override
	public void execute(final Context context, WebView webview,Hook hook) {
		try {
			Map<String, String> map=this.getParams();
			if(map==null){
				throw new HookException();
			}
			String id=map.get(HookConstants.ID);
			if(!StringUtil.IsNullOrEmpty(id)) {
                Log.d(TAG,"target id is "+id);
			    Intent data= new Intent();
			    data.putExtra(HookConstants.ID, id);
                ((Activity)context).setResult(Activity.RESULT_OK, data);
                ((Activity)context).finish();
			}else{
	            Log.e(TAG,"target id should not be null");
			}
			
		} catch(Exception e){
		    executeHookAPIFailCallJs(webview);
			Log.e(TAG,"Fail to execute the hook:"+e.getMessage());
		}
	}

}