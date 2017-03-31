package com.none.Plugin.pdfUpload;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.hsbc.share.ShareMainActivity;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import de.greenrobot.event.EventBus;

/**
 * Created by JinBo on 2017/1/14.
 */
public class InvoiceUploadPlugin extends CordovaPlugin {
    public static final String PLUGIN_NAME="InvoiceUploadPlugin";
    public static final String ENTER_METHOD= "enterMethod" ;
    public static final String UPLOAD_INVOICE= "uploadInvoice" ;
    public static final String ENTER_TYPE= "enterType";
    public static final String PDF_PATH= "pdfPath" ;
    private CallbackContext invoiceCallbackContext;

    @Override
    public boolean execute(String action, JSONArray args,
                           CallbackContext callbackContext) throws JSONException {
        String name = args.getString(0);
        SharedPreferences sharedPreferences=cordova.getActivity().getSharedPreferences(PLUGIN_NAME, Context.MODE_PRIVATE);
        if (action.equals(ENTER_METHOD)) {
            Log.d("bob","action ENTER_METHOD!");
            String type=sharedPreferences.getString(ENTER_TYPE,"Normal");
            callbackContext.success(type);
            return true;
        }else if(action.equals(UPLOAD_INVOICE)){
            Log.d("bob","action  UPLOAD_INVOICE!");
            this.invoiceCallbackContext = callbackContext;
            EventBus.getDefault().register(this);
            String pdfPath=sharedPreferences.getString(PDF_PATH,"");
            Log.d("bob","action  pdf="+pdfPath);
            if (!TextUtils.isEmpty(pdfPath)) {
                    Intent mintent = new Intent(cordova.getActivity(), ShareMainActivity.class);
                    String pdfUri=sharedPreferences.getString(PDF_PATH,"");
                    mintent.putExtra("pathUrl", pdfUri);
                    mintent.putExtra("username", name);
                    cordova.getActivity().startActivity(mintent);
                    //cordova.getActivity().finish();
                }
           //Intent intent = new Intent(cordova.getActivity(),ShareMainActivity.class) ;
           // cordova.getActivity().startActivity(intent);
            return true;
        }
        Log.d("bob","action name not right!");
        return false;

    }

    public void onEvent(InvoicePushEvent event) {

        Log.i("InvoiceonEvent :", "onEvent sucess sucess sucess");

        invoiceCallbackContext.success(event.getFlag());
        EventBus.getDefault().unregister(this);

    }

    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }
}
