package com.none.Push.uploadImage.domains.utils;


import de.greenrobot.event.EventBus;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ProgressDialogUtils {
	public static MyProgressDialog progressDialog;
	private static Button btn2;

	public static MyProgressDialog showProgressShow(Activity context,String message) {

		if (progressDialog == null || !progressDialog.isShowing()) {
			progressDialog = new MyProgressDialog(context);
			progressDialog.setCanceledOnTouchOutside(true);
			progressDialog.show();
		}
		return progressDialog;

	}

	public static void stopProgressDiaog(){
		if(progressDialog!=null&&progressDialog.isShowing()){
			progressDialog.dismiss();
			progressDialog=null;

		}

	}
}


