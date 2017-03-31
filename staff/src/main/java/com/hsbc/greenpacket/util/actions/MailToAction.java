package com.hsbc.greenpacket.util.actions;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class MailToAction {
	public void execute(Context context, String url) {
		try{
			Intent intent = new Intent(Intent.ACTION_SENDTO,Uri.parse(url));                 
			context.startActivity(intent);
		}catch(Exception e){
			Log.e("Mail","MailToAction error!");
		}
	}
}
