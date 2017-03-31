package com.hsbc.share;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.none.staff.R;
import com.none.staff.activity.StaffApplication;


public class UploadDialog extends AlertDialog {



	private  Context context;
	private String fileName;

	private View confirming;
	private View confirmed;

	public UploadDialog(Context context,String fileName,View confirming,View confirmed) {
		super(context);
		this.context=context;
		this.fileName = fileName;
		this.confirming=confirming;
		this.confirmed=confirmed;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_upload);
		setupView();
	}

	private void setupView() {
		((TextView)findViewById(R.id.tittle)).setText(fileName);
		findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//Toast.makeText(context,"Success",Toast.LENGTH_SHORT).show();
				UploadDialog.this.dismiss();
				confirming.setVisibility(View.GONE);
				confirmed.setVisibility(View.VISIBLE);

			}
		});
		findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				UploadDialog.this.dismiss();
				StaffApplication application = (StaffApplication)(context.getApplicationContext());
				application.cleanActivity();
				application.killApplication();
			}
		});
	}

	public static UploadDialog showUploadDialog(Activity context,String fileName,View view1,View view2) {
		UploadDialog infoDialog = new UploadDialog(context,fileName,view1,view2);
		infoDialog.setCanceledOnTouchOutside(false);
		infoDialog.setCancelable(false);
		infoDialog.show();
		return infoDialog;
	}

}
