package com.hsbc.share;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.none.staff.R;


public class ShareUploadDialog extends ProgressDialog{



	public static ShareUploadDialog progressDialog;
	private  Context context;
//	private TextView alert_dialog_title;
	//public static TextView upload_message;
	private static  ImageView loadingIV;
	private static Animation anim;
	public static Button dialog_ok;
	private static View layout_uploading;
	private static View layout_successful;
//	static Handler handler;


	public ShareUploadDialog(Context context) {
		super(context);
		this.context=context;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.dialog_upload_invoice);
		findViewById();
/*		handler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				if (msg.what == 0x123)
				{
					if(progressDialog!=null&&progressDialog.isShowing()){
						progressDialog.dismiss();
						progressDialog=null;
					}
				}
			}
		};*/
	}

	private void findViewById() {
		layout_uploading=findViewById(R.id.uploading_layout);
		layout_successful=findViewById(R.id.successful_layout);

		//alert_dialog_title=(TextView)findViewById(R.id.alert_dialog_title);
		//upload_message = (TextView) findViewById(R.id.tvtvtv);
		dialog_ok=(Button)findViewById(R.id.ok_btn);
		loadingIV=(ImageView)findViewById(R.id.loading_image);
		anim= AnimationUtils.loadAnimation(context,R.anim.loading_upload_invoice);
//		upload_message.setText("Pdf uploading....");
//		alert_dialog_title.setText("Upload Pdf");
	//	loadingIV.setImageResource(R.drawable.loading1);
/*		UploadActivity.handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == 1) {
					int data = msg.getData().getInt("uploadcount");
					//int size = msg.getData().getInt("size");
					//upload_message.setText(data+"/"+size+"  Pdf uploading....");
					Log.i("2222","data"+data);
				}
			}
		};*/
		layout_uploading.setVisibility(View.VISIBLE);
		layout_successful.setVisibility(View.GONE);
		loadingIV.startAnimation(anim);
	}

	public static ShareUploadDialog showProgressShow(Activity context,String message) {

		if (progressDialog == null || !progressDialog.isShowing()) {
			progressDialog = new ShareUploadDialog(context);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setCancelable(false);
			progressDialog.show();
			//loadingIV.startAnimation(anim);
		}
		return progressDialog;
	}	

	public static void stopProgressDiaog(boolean isSuccessful){
		if(progressDialog!=null&&progressDialog.isShowing()){
			if(isSuccessful) {
				loadingIV.clearAnimation();
				layout_uploading.setVisibility(View.GONE);
				layout_successful.setVisibility(View.VISIBLE);
				progressDialog=null;
				//loadingIV.setBackgroundResource(R.drawable.icon_successful);
/*
				new Timer().schedule(new TimerTask()
				{
					@Override
					public void run()
					{
						handler.sendEmptyMessage(0x123);

					}
				}, 3500);
*/

			}else {
				progressDialog.dismiss();
				progressDialog=null;
			}
			//progressDialog.dismiss();
			//progressDialog=null;
		}
	}

}
