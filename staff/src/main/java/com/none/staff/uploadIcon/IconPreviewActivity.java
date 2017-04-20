package com.none.staff.uploadIcon;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.none.Push.uploadImage.domains.Image;
import com.none.face.util.FaceUpLoadUtil;
import com.none.staff.R;
import com.none.staff.activity.BaseActivity;
import com.none.staff.clipzoomutils.ClipImageLayout;

public class IconPreviewActivity extends BaseActivity {
	private ImageView imgBack;
	private ProgressDialog mSaveDialog = null;
	private ClipImageLayout mClipImageLayout;
	private int code;
	private View btnUse;
	private String imagePath, base64, imageUrl, msg, userId;
	private Bitmap imageBitmap, result, clipBitmap;
	private FaceUpLoadUtil mFaceUpLoadUtil;
	// 用于接收服务端返回的数据
	Handler handler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0:
				if (msg.getData() != null) {
					String a = msg.getData().getString("uplaodFail");
					pareJson(a);
					mSaveDialog.dismiss();
					Toast.makeText(IconPreviewActivity.this, "上传图片失败",
							Toast.LENGTH_LONG).show();
					finish();
				} else {
					mSaveDialog.dismiss();
					Toast.makeText(IconPreviewActivity.this, "上传出错", 1).show();
					finish();
				}

				break;
			case 2:
				if (msg.getData() != null) {
					String b = msg.getData().getString("uploadSuccess");
					pareJson(b);
					if (code == 0) {
						Intent intent = new Intent();
						intent.putExtra("bitmap", imageUrl);
						Log.e("url", "imageUrl=" + imageUrl);
						setResult(2, intent);
						mSaveDialog.dismiss();
						Toast.makeText(IconPreviewActivity.this, "上传图片成功",
								Toast.LENGTH_LONG).show();
						finish();
					} else {
						Toast.makeText(IconPreviewActivity.this, "上传图片失败",
								Toast.LENGTH_LONG).show();
						mSaveDialog.dismiss();
						finish();
					}

				} else {
					mSaveDialog.dismiss();
					Toast.makeText(IconPreviewActivity.this, "上传出错", 1).show();
					finish();
				}
				break;
			default:
				break;

			}
			return false;
		}
	});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_icon_preview);
		init();
	}

	private void init() {
		// 上传图片工具类
		mFaceUpLoadUtil = new FaceUpLoadUtil(IconPreviewActivity.this, handler);
		Intent intent = this.getIntent();
		Image image = (Image) intent.getSerializableExtra("image");
		userId = intent.getStringExtra("userId");
		imgBack = (ImageView) findViewById(R.id.img_icon_back);
		mClipImageLayout = (ClipImageLayout) findViewById(R.id.img_icon_preview_show);
//		btnUse = (TestView) findViewById(R.id.btn_use);
		btnUse = findViewById(R.id.btn_use);
		btnUse.setFocusable(true);
		if (image != null) {
			imagePath = image.path;
		}
		int degreee = readBitmapDegree(imagePath);
		Bitmap bitmap = createBitmap(imagePath);
		if (bitmap != null) {
			if (degreee == 0) {
				mClipImageLayout.setImageBitmap(bitmap);
			} else {
				mClipImageLayout.setImageBitmap(rotateBitmap(degreee, bitmap));
			}
		} else {
			finish();
		}

		// 点击使用按钮处理事件
		btnUse.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mSaveDialog = ProgressDialog.show(IconPreviewActivity.this,
						"上传头像", "上传中，请稍等...", true);
				// mSaveDialog.setOnKeyListener(onKeyListener);
				btnUse.setFocusable(false);
				clipBitmap = mClipImageLayout.clip();
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						getBitmap(clipBitmap);
					}

				}).start();
			}

		});
		// 点击返回按钮处理事件
		imgBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}

		});

	}

	/**
	 * statusbar
	 * 
	 * @param enable
	 *            false 显示，true 隐藏
	 */
	private void full(boolean enable) {
		// TODO Auto-generated method stub
		WindowManager.LayoutParams p = this.getWindow().getAttributes();
		if (enable) {

			p.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;// |=：或等于，取其一

		} else {
			p.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);// &=：与等于，取其二同时满足，
																		// ~ ：
																		// 取反

		}
		getWindow().setAttributes(p);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
	}

	/**
	 * 创建图片
	 * 
	 * @param path
	 * @return
	 */
	private Bitmap createBitmap(String path) {
		if (path == null) {
			return null;
		}

		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = 1;
		opts.inJustDecodeBounds = false;// 这里一定要将其设置回false，因为之前我们将其设置成了true
		opts.inPurgeable = true;
		opts.inInputShareable = true;
		opts.inDither = false;
		opts.inPurgeable = true;
		FileInputStream is = null;
		Bitmap bitmap = null;
		try {
			is = new FileInputStream(path);
			bitmap = BitmapFactory.decodeFileDescriptor(is.getFD(), null, opts);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
					is = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return bitmap;
	}

	private void saveBitmap(Bitmap bitmap, String path) {
		File f = new File(path);
		if (f.exists()) {
			f.delete();
		}

		FileOutputStream fOut = null;
		try {
			f.createNewFile();
			fOut = new FileOutputStream(f);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			fOut.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (fOut != null)
					fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 读取图像的旋转度
	private int readBitmapDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	// 旋转图片
	private Bitmap rotateBitmap(int angle, Bitmap bitmap) {
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, false);
		return resizedBitmap;
	}

	/**
	 * 根据获取到的手机图片Uri获取到对应的bitmap
	 * 
	 * @param
	 */
	private void getBitmap(Bitmap bitmap) {
		zoomBitmap(bitmap);
		base64 = bitmapToBase64(result);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// // 调用上传方法
				Log.e("userId", "userId" + userId);
				mFaceUpLoadUtil.upIconImage(userId, base64, ".jpg");
			}
		});
	}

	/**
	 * 压缩图片大小的方法
	 */
	private void zoomBitmap(Bitmap bitmap) {
		Matrix matrix = new Matrix();
		matrix.reset();
		// 此处自己添加方法对图片进行大小
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
		// float zoom = (float)Math.sqrt(20 * 1024 /
		// (float)out.toByteArray().length);
		// matrix.setScale(0.9f, 0.9f);
		result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		out.reset();
		result.compress(Bitmap.CompressFormat.JPEG, 100, out);
		Log.e("DAXIAO", "tu片大小:" + out.toByteArray().length / 1024);
		while (out.toByteArray().length > 80 * 1024) {
			System.out.println(out.toByteArray().length);
			matrix.setScale(0.9f, 0.9f);
			result = Bitmap.createBitmap(result, 0, 0, result.getWidth(),
					result.getHeight(), matrix, true);
			out.reset();
			result.compress(Bitmap.CompressFormat.JPEG, 90, out);
		}
		Log.e("DAXIAO", "tu片大小:" + out.toByteArray().length / 1024);

	}

	/**
	 * add a keylistener for progress dialog
	 */
	private OnKeyListener onKeyListener = new OnKeyListener() {
		@Override
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				dismissDialog();
			}
			return false;
		}
	};

	/**
	 * dismiss dialog
	 */
	public void dismissDialog() {
		if (isFinishing()) {
			return;
		}
		if (null != mSaveDialog && mSaveDialog.isShowing()) {
			mSaveDialog.dismiss();
		}
	}

	/**
	 * bitmap转为base64
	 * 
	 * @param bitmap
	 * @return
	 */
	public static String bitmapToBase64(Bitmap bitmap) {

		String bitmapBase = null;
		ByteArrayOutputStream baos = null;
		try {
			if (bitmap != null) {
				baos = new ByteArrayOutputStream();
				// bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
				baos.flush();
				baos.close();

				byte[] bitmapBytes = baos.toByteArray();
				bitmapBase = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.flush();
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bitmapBase;
	}

	// 解析上传图片返回的数据
	private void pareJson(String s) {
		try {
			JSONObject o = new JSONObject(s);
			Log.e("oooo", "ooo"+o);
			// msg = o.optString("msg");
			// code = o.optInt("code", Integer.MIN_VALUE);
			// JSONObject data = o.optJSONObject("data");
			// imageUrl = data.optString("image");
			if (o.has("msg")) {
				msg = o.getString("msg");
			}
			if (o.has("code")) {
				if (o.get("code") instanceof Integer) {
					code = o.getInt("code");
				} else {
					code = Integer.MIN_VALUE;
				}
			}
			if (o.has("data")) {
				if(o.getJSONObject("data")!=null){
					JSONObject data = o.getJSONObject("data");
					if (data.has("image")) {
						imageUrl = data.getString("image");
					}
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
