package com.hsbc.greenpacket.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

import com.hsbc.greenpacket.activities.PrefConstants;
import com.none.staff.R;

public class Header {
	
	/**
	 * Additional HTTP headers to send with requests.
	 */
	private final static String NATIVE_APP_HEADER_NAME = "native-app";
	private final static String DEVICE_ID_HEADER_NAME = "device-id";
	private final static String DEVICE_TYPE_HEADER_NAME = "device-type";
    private final static String DEVICE_STATUS_HEADER_NAME = "device-status";
    
	public Map<String, String> createHeaders(final Context context) {
        /*
         * modified by JW [25-Oct-2012] since Soft OTP- replace the existing
         * UUID with Core Device ID
         */
        // return createHeaders(context,getUUID(context));
	    //Tracy Wang add a false param for eps  Aug-2013
        return createHeaders(context, DeviceIDUtil.generateDeviceID(context),false);
	}

    /**
     * @param context
     * @param addDeviceStatus
     *            If you want to put device security status in header, pass true. (Added by TW[Aug-2013] for eps. )
     * @return
     */
    public Map<String, String> createHeaders(final Context context, boolean addDeviceStatus) {
        return createHeaders(context, DeviceIDUtil.generateDeviceID(context), addDeviceStatus);
    }
	/**
	 * Setups up the additional headers to be sent to HSBC mobile websites.
	 */
	public Map<String, String> createHeaders(final Context context, final String deviceID, boolean addDeviceStatus) {
		Map<String, String> headers = new HashMap<String, String>();
		String deviceTypeHeader = String.format(context.getString(R.string.device_type_header), Build.VERSION.RELEASE);
		headers.put(Header.DEVICE_TYPE_HEADER_NAME, deviceTypeHeader);
        // JW [25-Oct-2012] since Soft OTP- replace the existing UUID with Core
        // Device ID
        headers.put(Header.DEVICE_ID_HEADER_NAME, deviceID);
        // End JW [25-Oct-2012]
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			String appName = String.format(context.getString(R.string.native_app_header), info.versionName);
			headers.put(Header.NATIVE_APP_HEADER_NAME, appName);
		}
		catch (NameNotFoundException e) {
		}
		return headers;
	}
	
	/**
	 * Gets the UUID for this install, if a UUID hasn't been generated for this install, it will be generated. If a previously generated
	 * UUID exists, this will be returned instead
	 * 
	 * @return
	 */
	public synchronized String getUUID(final Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PrefConstants.PREFS_NAME, Context.MODE_PRIVATE);
		if (!prefs.contains(PrefConstants.UUID_KEY)) {
			Editor editor = prefs.edit();
			editor.putString(PrefConstants.UUID_KEY, UUID.randomUUID().toString());
			editor.commit();
		}
		return prefs.getString(PrefConstants.UUID_KEY, null);
	}
}
