package com.hsbc.share;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.none.staff.R;


public class InfomationDialog extends AlertDialog {



	private  Context context;
	private String tittle,message;
	private int id;
	private View cancel;

	public InfomationDialog(Context context,int type) {
		super(context);
		this.context=context;
		if(type==1) {
			tittle = "eExpense号码";
			message = "如下图，你可以在你的eExpense system找到eExpense号码。";
			id = R.drawable.img_form;
		} else {
			tittle = "电子发票号码";
			message = "如下图，你可以在发票的右上角找到发票号码。";
			id = R.drawable.img_invoice;
		}

	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_info);
		cancel=findViewById(R.id.cancel);
		setupView();
	}

	private void setupView() {
		((TextView)findViewById(R.id.tittle)).setText(tittle);
		((TextView) findViewById(R.id.message)).setText(message);
		((ImageView) findViewById(R.id.image)).setImageDrawable(getContext().getResources().getDrawable(id));
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InfomationDialog.this.dismiss();
			}
		});
	}

	public static InfomationDialog showInfoDialog(Activity context,int type) {
		InfomationDialog infoDialog = new InfomationDialog(context,type);
		infoDialog.setCanceledOnTouchOutside(true);
		infoDialog.show();
		return infoDialog;
	}

}
