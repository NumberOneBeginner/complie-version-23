package com.none.Plugin.Beacon;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;
import com.none.staff.R;
import com.none.staff.activity.staff;

import de.greenrobot.event.EventBus;



@SuppressLint("ShowToast")
public class ibeacon extends CordovaPlugin {

	public static final String staticUuid="proximityUUID";
	public static final String staticMajor="major";
	public static final String staticMinor="minor";
	public static final String staticId="identifier";
	private static final String TAG = "ListBeaconsActivity";
	private static final String Message = "pop_up_message";
	private static final String distance = "in_distance";
	private static final String hashCode = "hashCode";

	private BeaconManager beaconManager;
	private JSONArray arryJsonBeacon;
	private JSONObject dirBeaconCanche;

	private CallbackContext callbackContextThis;


	//eventBus callBack	
	public void setCallbackNotif(eventNotification event)
	{
		String stringEventJson = event.getJsonTemp();
		JSONObject obj;
		try {
			obj = new JSONObject(stringEventJson);
			PluginResult result;
			result = new PluginResult(PluginResult.Status.OK,
					obj);
			result.setKeepCallback(true);
			callbackContextThis.sendPluginResult(result);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	//get BeaconInputDataByBeacon
	public JSONObject getBeaconInputDataByBeacon( final Beacon beacon)
	{
		int major =0;
		int  minor =0;
		major=beacon.getMajor();
		String uuid = beacon.getProximityUUID();
		minor=beacon.getMinor();
		JSONObject jsonObject = this.getJsonObjectWithBeaconMessage(uuid,String.valueOf(major),String.valueOf(minor));
		return jsonObject;
	}

	// get jsonObject with becon的uuid ,major,minor
	public JSONObject getJsonObjectWithBeaconMessage(final String uuidInput, final String majorInput,final String minorInput)
	{
		for (int i=0;i<arryJsonBeacon.length();i++)
		{
			try {
				JSONObject jsonObjectTemp = arryJsonBeacon.getJSONObject(i);

				String uuidstr=jsonObjectTemp.getString(staticUuid);
				String majorstr=jsonObjectTemp.getString(staticMajor);
				String minorstr=jsonObjectTemp.getString(staticMinor);

				String InputUUidLowCase= uuidInput.toLowerCase();
				String uuidstrLowCase = uuidstr.toLowerCase();

				if (InputUUidLowCase.equalsIgnoreCase(uuidstrLowCase) && majorstr.equals(majorInput)&& minorstr.equals(minorInput))
				{
					return jsonObjectTemp;
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	//获取beacon 状态

	public Boolean getBeaconStatus(JSONObject jsonTemp)
	{
		try {

			Boolean boolTemp =dirBeaconCanche.getBoolean(jsonTemp.hashCode()+hashCode);
			return boolTemp;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	//设置beacon 状态

	public void setBeaconStatus(JSONObject jsonTemp ,Boolean boolStatus )
	{
		try {
			dirBeaconCanche.put(jsonTemp.hashCode()+hashCode, boolStatus);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  

	}


	// send message
	public void send( final JSONObject jsonInput ) {
		Log.e(TAG, "notifySend");

		try {
			String messgePop = jsonInput.getString(Message);
			String identistr=jsonInput.getString(staticId);
			Intent resultIntent = new Intent(cordova.getActivity(), staff.class);

			resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			//json 传入
			resultIntent.putExtra("notifi", jsonInput.toString());
			NotificationManager mNotificationManager =
					(NotificationManager) cordova.getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
			PendingIntent pi = PendingIntent.getActivity(cordova.getActivity(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			Builder builder = new Builder(cordova.getActivity())
			.setSmallIcon(R.drawable.icon)
			.setContentText(messgePop)
			.setWhen(System.currentTimeMillis())
			.setContentIntent(pi).setAutoCancel(true)
			.setDefaults(Notification.DEFAULT_VIBRATE);

			mNotificationManager.notify(identistr.hashCode(), builder.build());

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 	
	}

	@SuppressLint("ShowToast")
	public boolean execute(String action, final JSONArray args, CallbackContext callbackContext) throws JSONException {
		Log.d(TAG, "begin");
		if (action.equals("startMonitorBeaconLocate")) {
			if(1==1) return true;
			callbackContextThis = callbackContext;
			JSONArray messageTemp = args.getJSONArray(0);	

			EventBus.getDefault().unregister(this,eventNotification.class);
			EventBus.getDefault().register(this,"setCallbackNotif",eventNotification.class);

			Log.d(TAG, "begin2");

			// deleate before monitor
			if(beaconManager!= null &&arryJsonBeacon!=null &&arryJsonBeacon.length()>0 )
			{
				for (int i=0;i<arryJsonBeacon.length();i++)
				{
					JSONObject jsonObjectTemp =(JSONObject) arryJsonBeacon.get(i);
					String uuidstr=jsonObjectTemp.getString(staticUuid);
					String majorstr=jsonObjectTemp.getString(staticMajor);
					String minorstr=jsonObjectTemp.getString(staticMinor);
					String identistr=jsonObjectTemp.getString(staticId);

					Region ALL_ESTIMOTE_BEACONS2_REGION = new Region(identistr, uuidstr, Integer.valueOf(majorstr), Integer.valueOf(minorstr));
					try {
						beaconManager.stopMonitoring(ALL_ESTIMOTE_BEACONS2_REGION);
						beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS2_REGION);

					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.d(TAG, "stopError");
					}		    		
				}

				//remove object In or Out
				Iterator<?> jsonKeys = dirBeaconCanche.keys();
				while(jsonKeys.hasNext())
				{
					String temKeyIN = (String) jsonKeys.next().toString();
					dirBeaconCanche.remove(temKeyIN); 
				}
			}
			else
			{
				dirBeaconCanche = new JSONObject();
				beaconManager = new BeaconManager(this.cordova.getActivity());
				beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(1), 0);
			}


			//距离监听
			beaconManager.setRangingListener(new BeaconManager.RangingListener() {

				//测距的结果，beacon当前在范围之内，手机在这个范围之内，就可以取到一系列的beacon数据，包括距离a

				@Override
				public void onBeaconsDiscovered(Region region, final List<Beacon> beaconsTemp) {
					//					  Log.d(TAG, "Ranged beacons: " + beaconsTemp);
					if (!beaconsTemp.isEmpty())
					{
						for(int i=0;i<beaconsTemp.size();i++)
						{
							try {
								Beacon  beacon = beaconsTemp.get(i);
								JSONObject jsonTemp= ibeacon.this.getBeaconInputDataByBeacon(beacon);
								double inDistance = Double.parseDouble(jsonTemp.getString(distance))*10;
								double outDistance = inDistance+5.0F;

								if (inDistance >= outDistance)
								{
									callbackContextThis.error("In-distance can't greater than out-distance");
									return ;
								}

								double realDistance =Utils.computeAccuracy(beacon);
								Log.d(TAG, "Ranged beacons: " + realDistance+"in:"+inDistance+"out:"+outDistance);
								Log.d(TAG,"bool 值1"+ibeacon.this.getBeaconStatus(jsonTemp));
								if(ibeacon.this.getBeaconStatus(jsonTemp)) 
								{
									if(realDistance<inDistance)
									{
										Log.d(TAG,"bool是true");
										ibeacon.this.setBeaconStatus(jsonTemp, false);
										ibeacon.this.send(jsonTemp);
									}
								}
								else 
								{
									if(realDistance>outDistance)
									{
										Log.d(TAG,"退出了");
										ibeacon.this.setBeaconStatus(jsonTemp, true);
									} 
								}

							} catch (NumberFormatException e) {
								// TODO Auto-generated catch block
								Log.e(TAG, "Ranging数字错误");
								e.printStackTrace();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								Log.e(TAG, "Ranging json error");
								e.printStackTrace();
							}

						}
					}

				}


			});

			//监听的结果
			beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
				@Override
				//离开beacon 的范围
				public void onExitedRegion(Region arg0) {

					int major =0;
					int  minor =0;

					major=arg0.getMajor();
					String uuid = arg0.getProximityUUID();
					minor=arg0.getMinor();
					Log.e(TAG,"退出 minor是"+minor+"uuid是"+uuid+"major是"+major);

					JSONObject jsonObject = ibeacon.this.getJsonObjectWithBeaconMessage(uuid,String.valueOf(major),String.valueOf(minor));
					if(jsonObject != null)
					{
						ibeacon.this.setBeaconStatus(jsonObject, true);
					}
				}

				//进入beacon 的范围
				public void onEnteredRegion(Region arg0, List<Beacon> arg1) {
					//进入到所在的区域，得到所有的beacon




				}
			});


			//判断蓝牙是否存在
			if (!beaconManager.hasBluetooth()) {
				callbackContextThis.error("Hardware not supported.");
			}
			else if(!beaconManager.isBluetoothEnabled())
			{
				Toast.makeText(this.cordova.getActivity(), "Please enable bluetooth!", 3000).show();
				callbackContextThis.error("Please enable bluetooth!");
			}
			else
			{
				this.connectToService(messageTemp);
				return true;
			}
		}
		return false;
	}


	//开始监听beacon
	private void connectToService( final JSONArray Arrayessage ) {
		Log.e(TAG, "开始连接服务");
		beaconManager.connect(new BeaconManager.ServiceReadyCallback() {//服务准备好，开始通知客户端
			@Override
			public void onServiceReady() {
				try {
					Log.e(TAG, "开始连接服务成功");

					for(int i=0;i<Arrayessage.length();i++){

						JSONObject	jsonObjectTemp=Arrayessage.getJSONObject(i);

						Log.e(TAG, jsonObjectTemp.toString());
						String uuidstr=jsonObjectTemp.getString(staticUuid).trim();
						String majorstr=jsonObjectTemp.getString(staticMajor).trim();
						String minorstr=jsonObjectTemp.getString(staticMinor).trim();
						String identistr=jsonObjectTemp.getString(staticId).trim();

						Region ALL_ESTIMOTE_BEACONS2_REGION = new Region(identistr, uuidstr, Integer.valueOf(majorstr), Integer.valueOf(minorstr));
						beaconManager.startRanging(ALL_ESTIMOTE_BEACONS2_REGION);//开始测距离
						beaconManager.startMonitoring(ALL_ESTIMOTE_BEACONS2_REGION);//开始监听区域
					}

					arryJsonBeacon = Arrayessage;

					//beacon  所有Beacon开关
					for (int i=0;i<arryJsonBeacon.length();i++)
					{
						JSONObject	jsonObjectTemp=arryJsonBeacon.getJSONObject(i);
						dirBeaconCanche.put(jsonObjectTemp.hashCode()+hashCode, true);
						Log.d(TAG,"bool 值2"+ibeacon.this.getBeaconStatus(jsonObjectTemp));

					}
				}
				catch (Exception e) {
					Log.e(TAG, " 不能连接", e);
					callbackContextThis.error("ibeacon蓝牙连接失败"+e);
				}
			}
		});
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(beaconManager!=null){
			beaconManager.disconnect() ;
		}
	}
}




