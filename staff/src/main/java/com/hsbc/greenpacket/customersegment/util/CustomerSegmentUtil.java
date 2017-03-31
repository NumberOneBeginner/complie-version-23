package com.hsbc.greenpacket.customersegment.util;

import java.lang.reflect.Field;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.hsbc.greenpacket.util.UIUtil;
import com.none.staff.R;


public final class CustomerSegmentUtil {
    private static final String TAG = "CustomerSegmentUtil";


	public static boolean isAppRememberCustInfo(Context context) {
		return false;
	}

	public static String setBackgroundImage(ImageView view, Context context) {
		String themeID = "";
		try {
		    String resourceName="screen";
            setImageBgByName(view, context, resourceName);
		} catch (Exception e) {
			Log.e(TAG,"SetBackgroundImage Error" + e);
		}
		return themeID;
	}

	/**
	 * @author Tracy [26 Oct 13]
	 * @description For Proposition Immage
	 */
	public static void setImageBgByName(ImageView view, Context context, String resourcename) {
		Class res = R.drawable.class;
		Field field;
		int BgImageId=0;
		try {
			field = res.getField(resourcename);
			BgImageId = field.getInt(null);
		} catch (Exception e) {
			Log.e(TAG,"image "+resourcename+" not found ");
		}
		Bitmap bitmap = UIUtil.readBitMap(context, BgImageId);
		UIUtil.releaseBitmap(view);
		view.setDrawingCacheEnabled(false);
		view.setImageBitmap(bitmap);
	}

}
