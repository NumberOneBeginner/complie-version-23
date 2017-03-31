package com.none.Plugin.AlarmClock;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;


public class AlarmHelper {

	private static final String TAG = "AlarmHelper";
	private Context context;
	private SharedPreferences sharedPreferences ;

	public AlarmHelper(Context context) {
		this.context = context;
		sharedPreferences = context.getSharedPreferences(AlarmClock.PLUGIN_NAME, Context.MODE_PRIVATE) ;
	}

	public boolean addAlarm(String notificationId){

		final long parseDate = Long.parseLong(notificationId) ;
		final Intent intent = new Intent(this.context, AlarmReceiver.class);
		intent.setAction("" + notificationId);

		intent.putExtra(AlarmReceiver.NOTIFICATION_ID, notificationId) ;

		final PendingIntent sender = PendingIntent.getBroadcast(this.context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

		final AlarmManager am = getAlarmManager();
		long now = System.currentTimeMillis() ;  

		Calendar passtime = Calendar.getInstance()  ;
		passtime.setTimeInMillis(parseDate*1000) ;

		if(now >parseDate*1000){   

//			passtime.add(Calendar.DATE, 1) ; //加一天
//			am.set(AlarmManager.RTC_WAKEUP, passtime.getTimeInMillis(), sender);
			Toast.makeText(context, "您所设置的时间已经过期，请重新设置", 0).show() ;

		}else{

			am.set(AlarmManager.RTC_WAKEUP, passtime.getTimeInMillis(), sender);//这个是不重复的
			Toast.makeText(context, "闹钟已经设置过了，到时间就会提醒您", 0).show() ;
		}

		return true;

	}






	public boolean cancelAlarm(String notificationId) {
		final Intent intent = new Intent(this.context, AlarmReceiver.class);
		intent.setAction("" + notificationId);  

		final PendingIntent pi = PendingIntent.getBroadcast(this.context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		final AlarmManager am = getAlarmManager();

		try {
			am.cancel(pi);
		} catch (Exception e) {
			return false;
		}
		return true;
	}


	/**
	 * 取得闹钟服务的方法
	 * @return  返回一个闹钟的管理类
	 */
	private AlarmManager getAlarmManager() {
		final AlarmManager am = (AlarmManager) this.context
				.getSystemService(Context.ALARM_SERVICE);

		return am;
	}

	/**
	 * 格式化时间戳  只是用来测试的
	 * @param timestamp  
	 * @return
	 */

	public static String getStandardTime(long timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
		Date date = new Date(timestamp);
		sdf.format(date);
		return sdf.format(date);
	}
}
