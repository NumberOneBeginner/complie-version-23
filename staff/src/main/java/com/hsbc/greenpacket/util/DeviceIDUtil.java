/*
 * ***************************************************************
 * Copyright.  HSBC Holdings plc 2012 ALL RIGHTS RESERVED.
 *
 * This software is only to be used for the purpose for which it
 * has been provided.  No part of it is to be reproduced,
 * disassembled, transmitted, stored in a retrieval system or
 * translated in any human or computer language in any way or
 * for any other purposes whatsoever without the prior written
 * consent of HSBC Holdings plc.
 * ***************************************************************
 *
 * Class Name			DeviceIDUtil.java
 *
 * Creation Date		Since Sept-2012
 *
 * Abstract				Since Soft OTP
 * 
 * 						There are different options to consider when choosing the target device specific attribute 
 * 						to compose the final Device ID on Android
 * 						1) Android ID
 * 						2) Serial Number
 * 						3) IMEI 
 * 						4) IMSI 
 * 						5) Wi-Fi MAC address
 * 						6) follow existing mobile 1.3 design - use Android UUID
 * 
 * 						Refer to the design document:
 * 						SoftOTP - Device ID Solution.ppt for detail implementation
 *
 * Amendment History   (In chronological sequence):
 *
 *    Amendment Date
 *    Programmer
 *    Description
 */
package com.hsbc.greenpacket.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.cordova.LOG;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;

public class DeviceIDUtil {
	// declare the Global Constant, use Protected to allow future extensibility
	protected final static String ANDROID_ID = "Android_ID";
	protected final static String SERIAL = "SerialNumber";
	protected final static String IMEI = "IMEI";
	protected final static String IMSI = "IMSI";
	protected final static String WIFI_MAC_ADDRESS = "Wifi_MAC_Address";
	protected final static String ANDROID_UUID = "Android_UUID";
    private static final String TAG = "DeviceIDUtil";
	
	protected static Context theContext;
	private static String encryptDeviceId;
	/*
	 * Program main logic
	 * This method is set to public to allow visibility from external caller
	 */
    public static String generateDeviceID(Context context) {
    	theContext = context.getApplicationContext();
    	
        final String TOKEN_SEPARATOR = "_";
    	String deviceID_str = "";
        ArrayList <String> deviceSpecificAttributeList;
        
        // detect if the device specific attribute has been instantiated before
        deviceSpecificAttributeList = retrieveMapping(theContext);
        if (deviceSpecificAttributeList==null || deviceSpecificAttributeList.size()<=0) {
        	Log.d(TAG,"device specific attribute not instaniated");
        	deviceSpecificAttributeList = analyzeDeviceSpecificAttribute();
        } else {
        	Log.d(TAG,"device specific attribute already instaniated");
        }
        
        /*
         * Device ID is constructed by 3 major parts:
         * Platform Name | Model | Device Unique Attribute
         * 
         * For example:
         * Platform Name: Android
         * Model: Galaxy Nexus
         * Device Specific Attribute: Android ID + Serial Number
         */
        // generate the platform name part
        String platformName = getPlatform();
        
        // generate the hardware model
        String hardwareModel = getModel();
        
        // generate the device specific attribute part
        String deviceSpecificAttribute = generateDeviceSpecificAttribute(deviceSpecificAttributeList);
        
        // combine all 3 elements to form the device ID string
        deviceID_str = platformName + TOKEN_SEPARATOR + hardwareModel + TOKEN_SEPARATOR + deviceSpecificAttribute;
        Log.d(TAG,"Generate the Device ID String: " + deviceID_str);
        
        return deviceID_str;
    }
    
    /**
     * @description Convert "deviceId" string to a SHA-512 hashed string
     * @param mContext
     * @return
     */
    public static String generateEncryptedDeviceID(Context mContext){
    	
    	if(StringUtil.IsNullOrEmpty(encryptDeviceId)){
    		String deviceId = generateDeviceID(mContext);
    		Log.d(TAG,"Generate the Device ID String2: " + deviceId);
        	try {        		
				String encryptedDeviceId = StringUtil.convertStringToSha512(deviceId);
				encryptDeviceId = encryptedDeviceId;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }else{
        	return encryptDeviceId;
        }
		return encryptDeviceId;
    }

    /*
     * This method contain the logic which govern what attributes will be chosen and 
     * how they are selected to form the device specific attributes
     * 
     * The selected attribute names will be contained in an ArrayList object and stored 
     * into local device storage (SharedPreference) for future referencing.
     * 
     * It will return the ArrayList object to the external caller
     */
    protected static ArrayList <String> analyzeDeviceSpecificAttribute() {
    	ArrayList <String> deviceSpecificAttributeList = new ArrayList <String> ();
    	
    	String android_id;
    	String serial_number;
    	String imei;
    	String imsi;
    	String wifi_mac_address;
    	String uuid;
    	
    	android_id = getAndroidID();
    	serial_number = getSerialNumber();
    	
    	if (android_id != null || serial_number != null) { // anyone of them is NOT null
    		if (android_id != null) {
    			deviceSpecificAttributeList.add(ANDROID_ID);
    		}
    		if (serial_number != null) {
    			deviceSpecificAttributeList.add(SERIAL);
    		}
    	} else {
    		// proceed to get IMEI
    		imei = getIMEI();
    		if (imei != null) {
    			deviceSpecificAttributeList.add(IMEI);
    		} else {
    			// proceed to get IMSI
    			imsi = getIMSI();
    			if (imsi != null) {
    				deviceSpecificAttributeList.add(IMSI);
    			} else {
    				// proceed to get Wifi MAC address
    				wifi_mac_address = getWifiMacAddress();
    				if (wifi_mac_address != null) {
    					deviceSpecificAttributeList.add(WIFI_MAC_ADDRESS);
    				} else {
    					// last resort, get UUID as in mobile 1.3
    					uuid = getUUID(theContext);
    					deviceSpecificAttributeList.add(ANDROID_UUID);
    				}
    			}
    		}
    	}
    	
    	generateMapping(theContext, deviceSpecificAttributeList); // save to local storage
    	deviceSpecificAttributeList = retrieveMapping(theContext); // ensure to retrieve from local storage 
    	return deviceSpecificAttributeList;
    }
    
    /*
     * This method is to rebuild the device specific attribute string 
     * from previous selected attributes (the ArrayList object re-constructed 
     * from SharedPreference)
     */
    protected static String generateDeviceSpecificAttribute(ArrayList <String> arrayList) {
    	String deviceSpecificAttribute = "";
    	
    	String tmp; // temporary local variable
    	for (int i=0; i<arrayList.size(); i++) {
    		String attributeName = (String) arrayList.get(i);
    		
    		if (attributeName.equalsIgnoreCase(ANDROID_ID)) {
    			tmp = getAndroidID();
    			Log.d(TAG,"Getting Android ID " + tmp);
    			deviceSpecificAttribute += tmp;
    			
    		} else if (attributeName.equalsIgnoreCase(SERIAL)) {
    			tmp = getSerialNumber();
    			Log.d(TAG,"Getting Serial Number " + tmp);
    			deviceSpecificAttribute += tmp;
    			
    		} else if (attributeName.equalsIgnoreCase(IMEI)) {
    			tmp = getIMEI();
    			Log.d(TAG,"Getting IMEI " + tmp);
    			deviceSpecificAttribute += tmp;
    			
    		} else if (attributeName.equalsIgnoreCase(IMSI)) {
    			tmp = getIMSI();
    			Log.d(TAG,"Getting IMSI " + tmp);
    			deviceSpecificAttribute += tmp;
    			
    		} else if (attributeName.equalsIgnoreCase(WIFI_MAC_ADDRESS)) {
    			tmp = getWifiMacAddress();
    			Log.d(TAG,"Getting Wi-Fi MAC Address " + tmp);
    			deviceSpecificAttribute += tmp;
    			
    		} else if (attributeName.equalsIgnoreCase(ANDROID_UUID)) {
    			tmp = getUUID(theContext);
    			Log.d(TAG,"Getting UUID " + tmp);
    			deviceSpecificAttribute += tmp;
    			
    		} else {
    			//unknown attribute name, cannot be supported
    		}
    	}
    	
    	return deviceSpecificAttribute;
    }
    
    /*
     * Retrieve the Android Release Version
     */
    public static String getReleaseVersion() {
    	String releaseVersion = null;
    	
    	// The user-visible version string (Android version)
    	// i.e. 4.1.2
    	releaseVersion = android.os.Build.VERSION.RELEASE;
    	
    	return releaseVersion;
    }
    
    /*
     * Retrieve the Android SDK Version
     */
    protected static int getSDKVersion() {
    	int sdkVersion;
    	
    	// The user-visible SDK version of the framework
    	// Android 4.1		(API 16)
    	// Android 4.0.3	(API 15)
    	// Android 4.0		(API 14)
    	// Android 3.2		(API 13)
    	// Android 3.1		(API 12)
    	// Android 3.0		(API 11)
    	// Android 2.3.3	(API 10)
    	// Android 2.2		(API 8)
    	// Android 2.1		(API 7)
    	// Android 1.6		(API 4)
    	// Android 1.5		(API 3)
    	sdkVersion = android.os.Build.VERSION.SDK_INT;
    	
    	return sdkVersion;
    }
    
    /*
     * Return the name of the platform 
     */
    protected static String getPlatform() {
    	return "Android";
    }
    
    /*
     * Return the name of the manufacturer
     */
    protected static String getManufacturer() {
    	return android.os.Build.MANUFACTURER;
    }
    
    /*
     * Return the name of the hardware model
     */
    public static String getModel() {
    	return android.os.Build.MODEL;
    }    
    
    /*
     * Retrieve the Android ID
     */
    protected static String getAndroidID() {
    	String android_id = Secure.getString(theContext.getContentResolver(), Secure.ANDROID_ID);
    	
		// convert the android id to null if its blank value
		if (android_id!=null && android_id.trim().equalsIgnoreCase("")) {
			android_id = null;
		}
    	
    	return android_id;
    }
    
    /*
     * Retrieve the Serial Number
     * 
     * [IMPORTANT]
     * Serial Number only available since API level 9
     * use java reflection technique to obtain serial number
     */
    protected static String getSerialNumber() {

		String serial = null;

		try {
			Class<?> c = Class.forName("android.os.Build");
			Field field = c.getField("SERIAL");
			serial = (String) field.get(null);
			
		} catch (Exception exc) {
			Log.e(TAG,"Get Serial Number Error", exc);
			serial = null;
		}
		
		// convert the serial to null if its blank value
		if (serial!=null && serial.trim().equalsIgnoreCase("")) {
			serial = null;
		}
		
		return serial;
    }
    
    /*
     * Retrieve the IMEI via getDeviceId() function
     * for example,the IMEI for GSM and the MEID or ESN for CDMA phones.  
     */
	protected static String getIMEI() {
		String imei = null;
		TelephonyManager telephonyManager;
		telephonyManager = (TelephonyManager) theContext.getSystemService(Context.TELEPHONY_SERVICE);
		
	    imei = telephonyManager.getDeviceId(); 
	    
	    int phoneType = telephonyManager.getPhoneType();
	    switch (phoneType) {
	    	case TelephonyManager.PHONE_TYPE_NONE:
	    		imei = "NONE:" + imei;
	    		break;
	    	case TelephonyManager.PHONE_TYPE_GSM:
	    		imei = "GSM:" + imei;
	    		break;
	    	case TelephonyManager.PHONE_TYPE_CDMA:
	    		imei = "MEID/ESN:" + imei;
	    		break;
    		 /*
    		  *  for API Level 11 or above
    		  *  case TelephonyManager.PHONE_TYPE_SIP:
    		  *  return "SIP";
    		  */
	    	default:
	    		imei = "UNKNOWN:" + imei;
	    		break;
	    }
	    
		// convert the IMEI to null if its blank value
		if (imei!=null && imei.trim().equalsIgnoreCase("")) {
			imei = null;
		}
	    
	    return imei;
	}
	
	/*
	 * Retrieve the IMSI via getSubscriberId() function 
	 * Returns the unique subscriber ID, for example, the IMSI for a GSM phone.
	 */
	protected static String getIMSI() {
		String imsi = null;
		
		TelephonyManager telephonyManager;
		telephonyManager = (TelephonyManager) theContext.getSystemService(Context.TELEPHONY_SERVICE);
		
		imsi = telephonyManager.getSubscriberId();
		
		// convert the IMSI to null if its blank value
		if (imsi!=null && imsi.trim().equalsIgnoreCase("")) {
			imsi = null;
		}
		
		return imsi;
	}
	
	/*
	 * Retrieve the Wi-Fi MAC address
	 */
    protected static String getWifiMacAddress() {
    	String macAddr = null;
    	
		WifiManager wifiMgr = (WifiManager) theContext.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
		macAddr = wifiInfo.getMacAddress();
		
		// convert the mac address to null if its blank value
		if (macAddr!=null && macAddr.trim().equalsIgnoreCase("")) {
			macAddr = null;
		}
		
		return macAddr;
    }
	
    /* 
     * Retrieve the Android UUID
     */
    protected static String getUUID(final Context context) {
    	// references Header.java
		SharedPreferences prefs = context.getSharedPreferences("HSBCHybridSharedPrefs", Context.MODE_PRIVATE);
		if (!prefs.contains("uuid")) {
			Editor editor = prefs.edit();
			editor.putString("uuid", UUID.randomUUID().toString());
			editor.commit();
		}
		return prefs.getString("uuid", null);
    }
    
    /*
     * Store the preferred device specific attribute (just the attribute name, not its value)
     * Note that the device specific attribute can be composed of more than one attribute
     * For example Android ID must combined together with Serial Number
     * 
     * Pass in the attribute names in an ArrayList, and use GsonBuilder to convert 
     * the ArrayList object into a JSON string for storage into SharedPreferences
     */
	protected static synchronized void generateMapping(final Context context, ArrayList <String> attributeList) {
		SharedPreferences prefs = context.getSharedPreferences("DeviceSpecificAttributeMap", Context.MODE_PRIVATE);
		
		// Do not overwrite existing value if already exist! 
		if (!prefs.contains("DeviceSpecificAttribute")) {
			// Convert the Object into JSON format string
			
	        String gsonStr = JsonUtil.getJsonFromJavaObject(attributeList);
	        Log.d(TAG,"Saving DeviceSpecificAttribute" + gsonStr);
	        			
			Editor editor = prefs.edit();
			editor.putString("DeviceSpecificAttribute", gsonStr);
			editor.commit();
		}
	}
    
	/*
	 * Retrieve the previous stored device specific attribute 
	 * 
	 * The attribute names are stored in the form of JSON string in SharedPreferences, 
	 * so need to convert back into an ArrayList object using GsonBuilder
	 */
	protected static synchronized ArrayList <String> retrieveMapping(final Context context) {
		SharedPreferences prefs = context.getSharedPreferences("DeviceSpecificAttributeMap", Context.MODE_PRIVATE);
		
		String gsonStr = prefs.getString("DeviceSpecificAttribute", null);
		if (gsonStr != null) {
			// convert the JSON String into original Object			
			ArrayList <String> arrayList = JsonUtil.getObjectFromJson(gsonStr, ArrayList.class);
			Log.d(TAG,"Restoring DeviceSpecificAttribute" + arrayList.toString());
			return arrayList;
		} else {
			// if the mapping does NOT exist just return null
			return null;
		}
	}
}