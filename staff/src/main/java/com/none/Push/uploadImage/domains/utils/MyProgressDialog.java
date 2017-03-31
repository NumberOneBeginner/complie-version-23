package com.none.Push.uploadImage.domains.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.none.Push.uploadImage.UploadActivity;
import com.none.staff.R;


public class MyProgressDialog extends ProgressDialog{



	public static MyProgressDialog progressDialog;
	private  Context context;
	private TextView alert_dialog_title;
	public static TextView upload_message;
	public static Button dialog_cancel;


	public MyProgressDialog(Context context) {
		super(context);
		this.context=context;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.dialog);
		findViewById();
	}

	private void findViewById() {

		alert_dialog_title=(TextView)findViewById(R.id.alert_dialog_title);
		upload_message = (TextView) findViewById(R.id.tvtvtv);
		dialog_cancel=(Button)findViewById(R.id.dialog_cancel);
		upload_message.setText("photo uploading....");
		alert_dialog_title.setText("Upload Photos");
		UploadActivity.handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == 1) {
					int data = msg.getData().getInt("uploadcount");
					int size = msg.getData().getInt("size");
					upload_message.setText(data+"/"+size+"  photo uploading....");
					Log.i("2222","data"+data);
				}
			}
		};
	}

	public static MyProgressDialog showProgressShow(Activity context,String message) {

		if (progressDialog == null || !progressDialog.isShowing()) {
			progressDialog = new MyProgressDialog(context);
			progressDialog.setCanceledOnTouchOutside(false);
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
