package com.hsbc.greenpacket.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.util.Log;
import android.view.WindowManager;

public class DeviceUtil {
    private final static int ANDROID4 = 14;
    private final static int ANDROID41 = 16;
    public final static int WIFI = 0;
    public final static int MOBILE_NETWORK = 1;
    public final static int NO_NETWORK = -1;
    private static final String TAG = "DeviceUtil";

    /**
     * android 4.0=14
     */
    public static void setSecureFlag(Activity activity) {
        try {
            /**
             * @description If debug mode is enabled, there will be NO blocking
             *              of screen capture. otherwise (production release),
             *              the screen capture will be BLOCKED for security
             *              reason as Group ISR request.
             * @author Cherry CHEN
             * @date 2013-03-11
             */
            if ((activity.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
                // android:debuggable="@bool/debug_mode" defined in
                // AndroidManifest.xml is true
                // allow screen shot
            } else {
                int versionNum = android.os.Build.VERSION.SDK_INT;
                if (versionNum >= ANDROID4) {
                    activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "set Secure flag error", e);
        }
    }

    /**
     * Change the input mode,prevent the soft keyboard override the input field
     * in some device which is Android 4.1 or above.
     * 
     * @author York Y K LI[Jan 29, 2013]
     * @param activity
     * 
     */
    public static void setSoftInputMode(Activity activity) {
        try {
            int versionNum = android.os.Build.VERSION.SDK_INT;
            if (versionNum >= ANDROID41) {
                activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            } else {
                activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            }
        } catch (Exception e) {
            Log.e(TAG, "set Secure flag error", e);
        }
    }

    public static int getCurrentNetType(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi != null && wifi.getState() == State.CONNECTED) {
            return WIFI;
        } else if (mobile != null && mobile.getState() == State.CONNECTED) {
            return MOBILE_NETWORK;
        }
        return NO_NETWORK;
    }

}
