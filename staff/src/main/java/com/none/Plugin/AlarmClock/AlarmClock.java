package com.none.Plugin.AlarmClock;

import java.util.Calendar;
import java.util.TimeZone;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import com.none.staff.utils.SPUtil;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.util.Log;
import android.widget.Toast;

public class AlarmClock  extends CordovaPlugin{


	public static final String PLUGIN_NAME = "LocalNotification";
	private static String calanderURL = "content://com.android.calendar/calendars";
	private static String calanderEventURL = "content://com.android.calendar/events";
	private static String calanderRemiderURL = "content://com.android.calendar/reminders";


	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {


		if("addAlarmClock".equals(action)){
			initCalendars();
			AlarmOptions alarmOptions = AlarmOptions.ParseJsonArray(args) ;

			String content = alarmOptions.getAlarmContent() ; //取得提醒内容

			String title = alarmOptions.getAlarmTitle() ; //取得通知标题

			long startTime = alarmOptions.getStartTime();
			long endTime = alarmOptions.getEndTime();

			String early_delayed = alarmOptions.getEarly_delayed();
			long offset = alarmOptions.getOffset_time();

			String	unique_id = alarmOptions.getUnique_id();

			if(!early_delayed.equals("")){

				if(early_delayed.equals("back"))

					offset=-offset;
			}


			String location = alarmOptions.getLocation();

			String calId = "";
			Cursor userCursor = this.cordova.getActivity().getContentResolver().query(Uri.parse(calanderURL), null, null, null, null);
			if (userCursor.getCount() >0) {
				userCursor.moveToLast();  
				calId = userCursor.getString(userCursor.getColumnIndex("_id"));
			}

			ContentValues event = new ContentValues();
			event.put("title", title);
			event.put("description", content);


			event.put("calendar_id", calId);
			System.out.println("calId: " + calId);
			event.put("eventLocation", location);   
			Calendar mCalendar = Calendar.getInstance();

			mCalendar.setTimeInMillis(startTime*1000L);


			long start = mCalendar.getTimeInMillis();

			mCalendar.setTimeInMillis(endTime*1000L);

			long end = mCalendar.getTimeInMillis();


			event.put("dtstart", start); 
			event.put("dtend", end);

			event.put("hasAlarm", 1);

			event.put(Events.EVENT_TIMEZONE, "Asia/Shanghai"); 

			Uri newEvent =this.cordova.getActivity().getContentResolver().insert(Uri.parse(calanderEventURL), event);        

			long id = Long.parseLong(newEvent.getLastPathSegment());
			//在此处将id存起来
			SPUtil.putValue(this.cordova.getActivity(), unique_id, String.valueOf(id));
			addAlarm(id,offset);
			callbackContext.success("增加闹铃成功!"); 	

			return true;

		}else{


			JSONArray unique_deletell = args.getJSONArray(0);
			String unique_delete = unique_deletell.getString(0);
			deleteEventById(unique_delete);

			callbackContext.success("删除闹铃成功");

			return true;
		}
	}




	private void deleteEventById(String unique_delete) {

		String value = SPUtil.getValue(this.cordova.getActivity(), unique_delete);


		String calanderEventURL = "";
		if (Integer.parseInt(Build.VERSION.SDK) >= 8) {
			calanderEventURL = "content://com.android.calendar/events";
		} else {
			calanderEventURL = "content://calendar/events";
		}

		ContentResolver cre = this.cordova.getActivity().getContentResolver();
		int cure = cre.delete(Uri.parse(calanderEventURL), "_id=?", new String[]{String.valueOf(value)} ); 
		Log.i("111", "删除成功");
	}




	private void addAlarm(long myEventsId, long offset) {

		ContentValues values = new ContentValues();
		values.put("event_id", myEventsId);
		values.put( "method", 1 );
		this.cordova.getActivity().getContentResolver().insert(Uri.parse(calanderRemiderURL), values);
		Toast.makeText(this.cordova.getActivity(), "增加成功", Toast.LENGTH_LONG).show();


	}


	private void initCalendars() {

		TimeZone timeZone = TimeZone.getDefault();
		ContentValues value = new ContentValues();
		value.put(Calendars.NAME, "yy");

		value.put(Calendars.ACCOUNT_NAME, "mygmailaddress@gmail.com");
		value.put(Calendars.ACCOUNT_TYPE, "com.android.exchange");
		value.put(Calendars.CALENDAR_DISPLAY_NAME, "mytt");
		value.put(Calendars.VISIBLE, 1);
		value.put(Calendars.CALENDAR_COLOR, -9206951);
		value.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER);
		value.put(Calendars.SYNC_EVENTS, 1);
		value.put(Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
		value.put(Calendars.OWNER_ACCOUNT, "mygmailaddress@gmail.com");
		value.put(Calendars.CAN_ORGANIZER_RESPOND, 0);

		Uri calendarUri = Calendars.CONTENT_URI;
		calendarUri = calendarUri.buildUpon()
				.appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
				.appendQueryParameter(Calendars.ACCOUNT_NAME, "mygmailaddress@gmail.com")
				.appendQueryParameter(Calendars.ACCOUNT_TYPE, "com.android.exchange")    
				.build();
		this.cordova.getActivity().getContentResolver().insert(calendarUri, value);


	}


}
