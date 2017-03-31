package com.none.Plugin.AlarmClock;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.none.staff.R;


public class AlarmReceiver extends BroadcastReceiver {


	public static final String TITLE = "ALARM_TITLE";
	//public static final String TICKER_TEXT = "ALARM_TICKER";

	public static final String CONTENT = "ALEARM_CONTENT";
	public static final String NOTIFICATION_ID = "NOTIFICATION_ID";
	private static final String TAG = "qqqq";
	private SharedPreferences sharedPreferences ;
	private String content ;
	private String notificationTitle ;
	private String notificationSubText ;

	

	@Override
	public void onReceive(Context context, Intent intent) {

		sharedPreferences = context.getSharedPreferences(AlarmClock.PLUGIN_NAME, Context.MODE_PRIVATE) ;

		final Intent intent2 = new Intent(context, AlarmReceiver.class);

		Log.d("AlarmReceiver", "AlarmReceiver invoked!");
		
		final Bundle bundle = intent.getExtras();
		final Object systemService = context.getSystemService(Context.NOTIFICATION_SERVICE);



		
		int notificationId = Integer.parseInt(bundle.getString(NOTIFICATION_ID));
		if(notificationId!=0){

			try {
                
                JSONArray jsonArray = new JSONArray(sharedPreferences.getString(notificationId+"", null)) ;
				AlarmOptions alarmOptions = AlarmOptions.ParseJsonArray(jsonArray);
				content = alarmOptions.getAlarmContent() ;
				notificationTitle = alarmOptions.getAlarmTitle() ;
				notificationSubText = alarmOptions.getAlarmContent() ;
				final long interval = Long.valueOf(alarmOptions.getInterVal()) ;
				final long parseDate = Long.valueOf(alarmOptions.getDate()) ;


				intent2.setAction("" + notificationId+"");
				intent2.putExtra(AlarmReceiver.NOTIFICATION_ID, notificationId+"") ;
				intent2.putExtra("interval", interval) ;
				intent2.putExtra(TITLE, notificationTitle) ;
				intent2.putExtra(CONTENT, content) ;

				Log.d(TAG, "notificationId是    "+notificationId+"interval "+interval+"notificationTitle"+notificationTitle+"content是"+content) ;


				final PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent2, PendingIntent.FLAG_CANCEL_CURRENT);
				final AlarmManager am = getAlarmManager(context);
				if(interval==2){
					
                    
                    Calendar passtime = Calendar.getInstance()  ;
					passtime.set(Calendar.MONTH,passtime.get(Calendar.MONTH)+1);
					am.set(AlarmManager.RTC_WAKEUP, passtime.getTimeInMillis(), sender) ;
							
				
                
                }if(interval==3){
					
                    Calendar passtime = Calendar.getInstance()  ;
					passtime.set(Calendar.YEAR,passtime.get(Calendar.YEAR)+1);
					am.set(AlarmManager.RTC_WAKEUP, passtime.getTimeInMillis(), sender) ;
					
                    
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}


		final NotificationManager notificationMgr = (NotificationManager) systemService;
		final Notification notification = new Notification(R.drawable.icon, content,
				System.currentTimeMillis());

		notification.flags |= Notification.FLAG_AUTO_CANCEL;
        
        
        final PendingIntent contentIntent = PendingIntent.getActivity(context, notificationId, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
        
        notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.vibrate = new long[] { 0, 100, 200, 300 };
		Notification notification1 = new Notification.Builder(context)
				.setAutoCancel(true)
				.setContentTitle(notificationTitle)
				.setContentText(notificationSubText)
				.setContentIntent(contentIntent)
				.setSmallIcon(R.drawable.icon)
				.setWhen(System.currentTimeMillis())
				.build();
		notificationMgr.notify(notificationId, notification1);

//		notification.setLatestEventInfo(context, notificationTitle, notificationSubText, contentIntent);

		
		
//        notificationMgr.notify(notificationId, notification);

		
	}
	private AlarmManager getAlarmManager(Context context) {
		final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		return am;
	}
	public static String getStandardTime(long timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
		Date date = new Date(timestamp);
		sdf.format(date);
		return sdf.format(date);
	}
}
