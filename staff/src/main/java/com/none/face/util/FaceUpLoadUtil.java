package com.none.face.util;

import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONArray;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.none.Push.uploadImage.domains.utils.HttpClientUtils;
import com.none.staff.R;

/**
 * @author peter
 * 
 */
public class FaceUpLoadUtil {
	private Context context;
	protected JSONArray activityList;
	Handler handler;
	String faceupload,iconUploadURL;
	public FaceUpLoadUtil(Context context,Handler handler) {
		this.context = context;
		this.handler = handler;
		iconUploadURL = context.getResources().getString(R.string.uploadIcon_url);
	}
     /**
      * 人脸识别的图片上传
      * @param usreId 用户的ID
      * @param faceBase64 用户拍照得到的要识别的人脸的照片
      */
	public void upFaceImage(String usreId, String faceBase64) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("userId", usreId);// 此处是传过来的User_id;
		params.put("faceImgBase64", faceBase64);
//		params.put("photo", faceBase64);
//		Log.e("tTIM", "diami4");
		HttpClientUtils.getInstance().post(
//				"http://192.168.88.110:8080/staff/androidUser/imageUpload",
				"http://120.24.70.17:8080/staff/faceLaiSee/scanToUnlock",
				null, params, context, new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						// TODO Auto-generated method stub
						//Toast.makeText(context, "上传图片失败", Toast.LENGTH_LONG).show();
						//Log.d("faceupload", arg3.toString());
						String jsonString = new String(arg3.toString());
						 Message message = handler.obtainMessage();
			                message.what = 1;
			                Bundle bundle = new Bundle();
			                bundle.putString("faceFail", jsonString);
			                message.setData(bundle);
			                message.sendToTarget();
					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						// TODO Auto-generated method stub
						//Toast.makeText(context, "上传图片成功", Toast.LENGTH_LONG).show();
						//Log.d("faceupload", arg2.toString());
						String jsonString = new String(arg2);
						 Message message = handler.obtainMessage();
			                message.what = 5;
			                Bundle bundle = new Bundle();
			                bundle.putString("face", jsonString);
			                message.setData(bundle);
			                message.sendToTarget();
//			                Log.e("tTIM", "diami5");
					}
				});
	}
    /**
     * 上传本地图片到个人头像
     * @param usreId   用户的ID
     * @param faceBase64  用户选择为头像的Bitmap 转换承德base64的值
     * @param actionType 用户选择头像文件的后缀名
     */
	public void upIconImage(String usreId, String faceBase64,String actionType) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("userId", usreId);// 此处是传过来的User_id;
		params.put("photo", faceBase64);
		params.put("actiontype",actionType);

		HttpClientUtils.getInstance().post(
				iconUploadURL,
				null, params, context, new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						// TODO Auto-generated method stub
//						Toast.makeText(context, "上传图片失败", Toast.LENGTH_LONG).show();
//						Log.d("Failureupload", arg3.toString());
						String jsonString = new String(arg3.toString());
						 Message message = handler.obtainMessage();
			                message.what = 0;
			                Bundle bundle = new Bundle();
			                bundle.putString("uplaodFail", jsonString);
			                message.setData(bundle);
			                message.sendToTarget();
					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						// TODO Auto-generated method stub
//						Toast.makeText(context, "上传图片成功", Toast.LENGTH_LONG).show();
//						Log.d("Successupload", arg2.toString());
						String jsonString = new String(arg2);
						 Message message = handler.obtainMessage();
			                message.what = 2;
			                Bundle bundle = new Bundle();
			                bundle.putString("uploadSuccess", jsonString);
			                message.setData(bundle);
			                message.sendToTarget();
			             
					}
				});
	}

}
