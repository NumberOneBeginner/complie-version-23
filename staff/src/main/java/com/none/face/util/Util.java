package com.none.face.util;

import com.none.staff.activity.OpenglActivity;

/**
 * Created by staff 2017-01-13
 */
public class Util {
	public static String API_KEY="HE1uKBSyLlKyxBM7qShwqNo2eYgh1cb3";
	public static String API_SECRET="yfAH7OHKQpKSZxF4wFUx9GEBKOnymWeR";
	public static int getPhoneWidth() {
		int width = OpenglActivity.instance.getContext().getResources().getDisplayMetrics().widthPixels;
		return width;
	}
	public static int getPhoneHeight() {
		int height = OpenglActivity.instance.getContext().getResources().getDisplayMetrics().heightPixels;
		return height;
	}


}
