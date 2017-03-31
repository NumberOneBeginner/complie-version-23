
package com.none.Plugin.AlarmClock;

import org.json.JSONArray;

import android.util.Log;



public class AlarmOptions {

	private String alarmTitle ;
	private String alarmContent ;
	private String notificataionID ;
	private String date ;
	private String interVal ;
	private long startTime;
	private long endTime;
	private String early_delayed;
	private long offset_time;
	private String unique_id;



	public String getUnique_id() {
		return unique_id;
	}

	public void setUnique_id(String unique_id) {
		this.unique_id = unique_id;
	}

	public String getEarly_delayed() {
		return early_delayed;
	}

	public void setEarly_delayed(String early_delayed) {
		this.early_delayed = early_delayed;
	}

	public long getOffset_time() {
		return offset_time;
	}

	public void setOffset_time(long offset_time) {
		this.offset_time = offset_time;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}



	private String location;



	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return the alarmTitle
	 */
	public String getAlarmTitle() {
		return alarmTitle;
	}

	/**
	 * @return the alarmContent
	 */
	public String getAlarmContent() {
		return alarmContent;
	}

	/**
	 * @return the notificataionID
	 */
	public String getNotificataionID() {
		return notificataionID;
	}

	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @return the interVal
	 */
	public String getInterVal() {
		return interVal;
	}

	/**
	 * @param interVal the interVal to set
	 */
	public void setInterVal(String interVal) {
		this.interVal = interVal;
	}



	public static AlarmOptions ParseJsonArray(JSONArray optionsArr){
		AlarmOptions alarmOptions = null ;
		try {
			alarmOptions = new AlarmOptions() ;
			alarmOptions.notificataionID = optionsArr.optString(0) ;//id也是时间戳
			alarmOptions.date = optionsArr.optString(0);
			alarmOptions.alarmTitle = optionsArr.optString(1) ;

			Log.i("111", "(optionsArr.opt(2))        "+optionsArr.opt(2));
			if(optionsArr.optString(2).equals("null")){

				Log.i("111", "执行if");
				alarmOptions.alarmContent="";	
			}else{
				Log.i("111", "执行else");	
				alarmOptions.alarmContent = optionsArr.optString(2) ;//date是时间戳
			}
			alarmOptions.interVal = optionsArr.optString(3) ;


			if(optionsArr.optString(4).equals("null")){

				alarmOptions.startTime=Long.parseLong(optionsArr.optString(0));	//还要乘以1000L

			}else{
				alarmOptions.startTime =Long.parseLong(optionsArr.optString(4)) ;//date是时间戳
			}

			if(optionsArr.optString(5).equals("null")){

				alarmOptions.endTime=Long.parseLong(optionsArr.optString(0))+3600;

			}else{
				alarmOptions.endTime =Long.parseLong(optionsArr.optString(5)) ;//date是时间戳
			}


			if(optionsArr.optString(6).equals("null")){

				alarmOptions.location="";	
			}else{
				alarmOptions.location = optionsArr.optString(6) ;
			}



			//没有提前或者延后,就不用取偏移时间了。
			if(optionsArr.optString(7).equals("null")){

				alarmOptions.early_delayed="";

			}else{
				//提前或者延后
				alarmOptions.early_delayed=optionsArr.optString(7);
			}



			if(optionsArr.optString(8).equals("null")){

				alarmOptions.offset_time=0;
			}else{
				alarmOptions.offset_time=optionsArr.optLong(8);

			}


			if(!optionsArr.optString(9).equals("null")){

				alarmOptions.unique_id=optionsArr.optString(9);

			}


			return alarmOptions ;
		} catch (Exception e) {
			e.printStackTrace() ;
			return null ;
		}
	}

}
