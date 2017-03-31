/*
 * COPYRIGHT. HSBC HOLDINGS PLC 2014. ALL RIGHTS RESERVED.
 *
 * This software is only to be used for the purpose for which it has been
 * provided. No part of it is to be reproduced, disassembled, transmitted,
 * stored in a retrieval system nor translated in any human or computer
 * language in any way or for any other purposes whatsoever without the
 * prior written consent of HSBC Holdings plc.
 */
package com.none.Plugin.back;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

import android.content.Intent;

/**
 * <p><b>
 * Set the app to background when back button pressed
 * </b></p>
 */
public class BackAction extends CordovaPlugin {
    public final static String BACK_ACTION="backaction";
    CallbackContext callbackContext;

    public BackAction() {
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public boolean execute(String action, org.json.JSONArray args,CallbackContext callbackContext) throws org.json.JSONException{
        this.callbackContext= callbackContext;
        if(action.equals(BACK_ACTION)){
          Intent homeIntent = new Intent(Intent.ACTION_MAIN);
          homeIntent.addCategory(Intent.CATEGORY_HOME);
          homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          cordova.getActivity().startActivity(homeIntent);
          return true;
        }
        return false;
        
    }
    
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        callbackContext.success();
    }
}
