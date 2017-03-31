/*
 * COPYRIGHT. HSBC HOLDINGS PLC 2014. ALL RIGHTS RESERVED.
 *
 * This software is only to be used for the purpose for which it has been
 * provided. No part of it is to be reproduced, disassembled, transmitted,
 * stored in a retrieval system nor translated in any human or computer
 * language in any way or for any other purposes whatsoever without the
 * prior written consent of HSBC Holdings plc.
 */
package com.none.Plugin.greenLaisee;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.hsbc.greenpacket.activities.Constants;
import com.hsbc.greenpacket.activities.MainBrowserActivity;
import com.hsbc.greenpacket.process.ProcessUtil;
import com.hsbc.greenpacket.util.NameValueStore;

import com.none.staff.R;
import com.none.staff.activity.OnboardingActivity;
import com.none.staff.activity.StaffApplication;
import com.none.staff.activity.staff;
import com.none.staff.util.DownloadUtil;

/**
 * <p><b>
 * TODO : Insert description of the class's responsibility/role.
 * </b></p>
 */
public class OpenGreenLaiseePlugin extends CordovaPlugin {
	
	StaffApplication application ;
    public final static String OPEN_GREEN_LAISEE_ACTION="openGreenLaisee";
    CallbackContext callbackContext;
    /**
     * <p><b>
     * TODO : Insert description of the method's responsibility/role.
     * </b></p>
     *
     */
    public OpenGreenLaiseePlugin() {
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public boolean execute(String action, org.json.JSONArray args,CallbackContext callbackContext) throws org.json.JSONException{
        this.callbackContext= callbackContext;
        if(action.equals(OPEN_GREEN_LAISEE_ACTION)){
            Activity activity=cordova.getActivity();
            NameValueStore store = new NameValueStore(activity);
            boolean isOnboardingPageShowed = Boolean.valueOf(store.getAttribute(Constants.ONBOARDING_PAGE_SHOWED));
            //boolean isOnboardingPageShowed =true;
            
            if(isOnboardingPageShowed){
                Intent intent = new Intent(activity,MainBrowserActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                Bundle bundle = new Bundle();
                String url = "file:///web/postlogon/menu/menu.html";
                url = ProcessUtil.localUrlIntercept(url, DownloadUtil.getClientPackPath(cordova.getActivity()));
                bundle.putString(Constants.INTENT_URL_KEY, url);
                bundle.putString(Constants.SCA_INFO, args.getString(0));
                intent.putExtras(bundle);
                activity.overridePendingTransition(0, 0);
                activity.startActivityForResult(intent, staff.GO_TO_SCA_FUNCTION);
            }else{
                Bundle bundle = new Bundle();
                bundle.putString(Constants.SCA_INFO, args.getString(0));
                Intent intent = new Intent(activity,OnboardingActivity.class);
                intent.putExtras(bundle);
                activity.startActivityForResult(intent, staff.GO_TO_SCA_FUNCTION);
                activity.overridePendingTransition(R.anim.page_in_rightleft, R.anim.page_out_rightleft);
            }
            //.startActivity(intent);
//            cordova.startActivityForResult((OpenGreenLaiseePlugin)this, intent, 200);
//            cordova.getActivity().finish();
            return true;
        }

        
        
//        if(action.equals(OPEN_GREEN_LAISEE_ACTION)){
////            Intent intent = new Intent(cordova.getActivity(),MainBrowserActivity.class);
////            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
////            Bundle bundle = new Bundle();
////            String url = "file:///web/postlogon/menu/menu.html";
////            url = ProcessUtil.localUrlIntercept(url, DownloadUtil.getClientPackPath(cordova.getActivity()));
////            bundle.putString(Constants.INTENT_URL_KEY, url);
////            bundle.putString(Constants.SCA_INFO, args.getString(0));
////            intent.putExtras(bundle);
//       Log.e("args0000000", args+"") ;	
//       Log.e("456465", args.getString(0)) ;
//       application = (StaffApplication) cordova.getActivity().getApplication() ;
//       		UserInfo userInfo = UserInfo.parse(args.getString(0)) ;
//       		if(null !=userInfo){
//       			application.setUser(userInfo) ;
//       			Log.e("userinfo", userInfo.toString()) ;
//       			SPUtil.putValue(cordova.getActivity(), SPUtil.USER_ID, userInfo.getAccount()) ;
//       			Intent intent = new Intent(cordova.getActivity(),GreenLaiseeActivity.class) ;
//       			intent.putExtra("location", userInfo.getLocation()) ;
//                cordova.getActivity().overridePendingTransition(0, 0);
//                cordova.getActivity().startActivityForResult(intent, staff.GO_TO_SCA_FUNCTION);
//       		}
//       		
//        	
//            //.startActivity(intent);
////            cordova.startActivityForResult((OpenGreenLaiseePlugin)this, intent, 200);
////            cordova.getActivity().finish();
//            return true;
//        }
        return false;
        
    }
    
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        callbackContext.success();
    }
}
