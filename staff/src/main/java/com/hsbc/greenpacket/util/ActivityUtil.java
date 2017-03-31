package com.hsbc.greenpacket.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Surface;
import android.webkit.WebView;

import com.hsbc.greenpacket.activities.Constants;
import com.hsbc.greenpacket.activities.MainBrowserActivity;
import com.hsbc.greenpacket.util.actions.HookConstants;
import com.hsbc.greenpacket.util.web.HSBCWebClient;
import com.hsbc.greenpacket.util.web.HSBCWebTouchChromeClient;

import com.none.staff.activity.LoadingDialog;

/**
 * 
 * @author York Y K LI
 *
 */
public class ActivityUtil {
    
    public static void startMainBrowserActivity(Activity context,String url, boolean isLogonFunc, String moduleId) {
        if(StringUtil.IsNullOrEmpty(url)){
            return;
        }

        Intent activity = new Intent(context, MainBrowserActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putString(Constants.URL, url);
        if (isLogonFunc) {
            mBundle.putBoolean(Constants.INTENT_ISLOGONFUNC_KEY, true);
        }
        if (moduleId != null) {
            mBundle.putString(Constants.INTENET_MODULE_ID, moduleId);
        }
        activity.putExtras(mBundle);
        context.startActivity(activity);
        
        //context.overridePendingTransition(R.anim.page_in_rightleft, R.anim.page_out_rightleft);
    }
    
    
    
    /**
     * 
     * @author York Y K LI[Jan 9, 2013]
     * @param activiy
     * @param msg
     * @return
     *
     */
    public static Dialog createProgressDialog(Activity activiy,String msg){
        //ProgressDialog progressDialog = new ProgressDialog(this);
        LoadingDialog progressDialog = new LoadingDialog(activiy,msg);
        progressDialog.setMessage(msg);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }
    
    /**
     * 
     * @author York Y K LI[Jan 9, 2013]
     * @param activity
     * @param title
     * @param message
     * @param ok
     * @return
     *
     */
    public static Dialog createOkButtonDialog(Activity activity,String title,String message,String ok) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if(!StringUtil.IsNullOrEmpty(title)){
            builder.setTitle(title);
        }
        builder.setMessage(message);
        builder.setPositiveButton(ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        Dialog networkErrorDialog = builder.create();
        networkErrorDialog.setCanceledOnTouchOutside(false);
        return networkErrorDialog;
    }
    /**
     * This method is for Hook action
     * @author York Y K LI[Jan 15, 2013]
     * @param webview
     * @param mBundle
     *
     */
    public static void executeJsInWebview(final WebView webview,Bundle mBundle){
        if (mBundle != null && mBundle.getString(HookConstants.MESSAGE_DATA) != null) {
            String script = mBundle.getString(HookConstants.MESSAGE_DATA);
            if(!StringUtil.IsNullOrEmpty(script)){
                webview.loadUrl(script);
            }
        }
    }
    /**
     * Free webview and client
     * @author York Y K LI[Mar 5, 2013]
     * @param webview
     * @param webclient
     * @param chromeClient
     *
     */
    public static void freeWebviewAndClient(WebView webview,HSBCWebClient webclient,HSBCWebTouchChromeClient chromeClient){
        if (webclient != null) {
            webclient.ownerDestroyed();
            webclient = null;
        }
        if (chromeClient != null) {
            chromeClient.ownerDestroyed();
            chromeClient = null;
        }
       
        if (webview != null) {
            webview.freeMemory();
            webview.destroy();
            webview = null;
        }
    }
    
    /**
     * @description According to the direction of mobile phone , get the rotation Angle of camera preview screen
     * @author 43734332 Cherry
     * @date 2013-08-30  
     */
    public static int getPreviewDegree(Activity activity){
  	  int rotation = activity.getWindowManager().getDefaultDisplay().getOrientation();
  	  int degree = 0;
  	  switch(rotation){
  	  		case Surface.ROTATION_0:
  	  			degree = 90;
  	  			break;
  	  		case Surface.ROTATION_90:
  	  			degree = 0;
  	  			break;
  	  		case Surface.ROTATION_180:
  	  			degree = 270;
  	  			break;
  	  		case Surface.ROTATION_270:
  	  			degree = 180;
  	  			break;
  	  }
  	  return degree;
    }
    
    /**
     * <!-- For making phone call -->
     * @author York Li
     * @param context
     * @param url
     * @return
     */
    public static boolean startCall(Context context,String url){
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
        return true;
    }
}
