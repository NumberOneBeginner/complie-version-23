package com.none.staff.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.none.staff.R;

public class DialogUtils {
	
	private static Dialog alertDialog ;
	//创建设置金额对话框
		public static  void createSettingDialog(final Context context) {
			alertDialog = new AlertDialog.Builder(context).create() ;
			alertDialog.show() ;
			alertDialog.setCanceledOnTouchOutside(true) ;
			Window window = alertDialog.getWindow() ;
			//这是让软键盘弹出的方法
			window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE  | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
			window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
			//设置dialog和屏幕一样宽
			window.setLayout( 
					android.view.WindowManager.LayoutParams.MATCH_PARENT, 
					android.view.WindowManager.LayoutParams.WRAP_CONTENT); 
			window.setContentView(R.layout.alert_dialog_setting) ;
			
			final EditText mAmount = (EditText) window.findViewById(R.id.et_amount) ;
			//final EditText mNoLaisee = (EditText) window.findViewById(R.id.et_nooflaisee) ;
			
			Button cancel = (Button) window.findViewById(R.id.dialog_btn1) ;
			
			final Button save = (Button) window.findViewById(R.id.dialog_btn2) ;
			
			String saveAmout = SPUtil.getValue(context, SPUtil.SET_AMOUNT_LAISEE) ;
			String saveNoOfLaisee = SPUtil.getValue(context, SPUtil.SET_NOOF_LAISEE) ;
			if(!TextUtils.isEmpty(saveAmout)){
				mAmount.setText(saveAmout) ;
				mAmount.setSelection(mAmount.length());
			}
//			if(!TextUtils.isEmpty(saveNoOfLaisee)){
//				mNoLaisee.setText(saveNoOfLaisee) ;
//				mNoLaisee.setSelection(mNoLaisee.length());
//			}
			
			save.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					String mAmoutContent = mAmount.getText().toString().trim() ;  //取得金额
					//String mNoLaiseeContent = mNoLaisee.getText().toString().trim() ; //取得金额数量
					//if(TextUtils.isEmpty(mAmoutContent) || TextUtils.isEmpty(mNoLaiseeContent)){
					if(TextUtils.isEmpty(mAmoutContent)){
						return ;
					}
					SPUtil.putValue(context, SPUtil.SET_AMOUNT_LAISEE, mAmoutContent) ;  //保存金额
				//	SPUtil.putValue(context, SPUtil.SET_NOOF_LAISEE, mNoLaiseeContent) ; //保存金额 数量
					dissMissDialog() ;
				}
			}) ;
			
			cancel.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dissMissDialog() ;
				}
			}) ;
		}
		
		private static void dissMissDialog(){
			if(null!=alertDialog && alertDialog.isShowing()){
				alertDialog.dismiss() ;
				alertDialog = null ;
			}
		}
}
