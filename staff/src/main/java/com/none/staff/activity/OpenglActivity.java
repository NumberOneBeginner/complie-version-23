package com.none.staff.activity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;
import com.megvii.facepp.sdk.Facepp;
import com.megvii.facepp.sdk.Facepp.Face;
import com.megvii.facepp.sdk.Facepp.FaceppConfig;
import com.none.face.util.CameraMatrix;
import com.none.face.util.ConUtil;
import com.none.face.util.DialogUtil;
import com.none.face.util.FaceUpLoadUtil;
import com.none.face.util.ICamera;
import com.none.face.util.ImageUtil;
import com.none.face.util.MediaRecorderUtil;
import com.none.face.util.OpenGLDrawRect;
import com.none.face.util.OpenGLUtil;
import com.none.face.util.PointsMatrix;
import com.none.face.util.Screen;
import com.none.face.util.SensorEventUtil;
import com.none.face.util.Util;
import com.none.staff.R;

public class OpenglActivity extends BaseActivity implements PreviewCallback,
		Renderer, SurfaceTexture.OnFrameAvailableListener {
	public static final String TAG = OpenglActivity.class.getName();
	private Bitmap b1;
	public static OpenglActivity instance;
	private boolean isDebug, isBackCamera, isTiming, isROIDetect,
			isFaceProperty;
	private int printTime = 33;
	private GLSurfaceView mGlSurfaceView;
	private ICamera mICamera;
	private Camera mCamera;
	private DialogUtil mDialogUtil;
	private ImageView mImageGo, faceBack, faceBackMenu;
	// private TextView debugInfoText, debugPrinttext;
	HandlerThread mHandlerThread = new HandlerThread("s");
	Handler mHandler;
	private Facepp facepp;
	private MediaRecorderUtil mediaRecorderUtil;
	private boolean isStartRecorder = false;
	private int min_face_size = 200;
	private int detection_interval = 25;
	private HashMap<String, Integer> resolutionMap;
	private SensorEventUtil sensorUtil;
	private float roi_ratio = 0.8f;
	private String staffId, jsonString;
	private Dialog faceDialog;
//	RelativeLayout rv_dialog;
	// 接收上传图片返回的数据
	Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case 5:
				jsonString = msg.getData().getString("face");
				Intent result = new Intent();
				result.putExtra("faceW", jsonString);
				setResult(1, result);
				Log.e("tTIM", "diami6");
				finish();
				faceDialog.dismiss();
				break;
			case 1:
				String faceUpFail = msg.getData().getString("faceFail");
				Log.d(TAG, "up data fail：" + faceUpFail);
				finish();
				faceDialog.dismiss();
				break;
			default:
				break;
			}
			return true;
		}
	});

	// 点击GO按钮之后出现遮罩层
	private void displaySplash() {
		faceDialog = new Dialog(this,
				android.R.style.Theme_Translucent_NoTitleBar);
		View view = LayoutInflater.from(this)
				.inflate(R.layout.facedialog, null);
		Window dialogWindow = faceDialog.getWindow();
		dialogWindow.setGravity(Gravity.CENTER);
		faceDialog.setContentView(view);
		faceDialog.setCancelable(false);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		 if (Build.VERSION.SDK_INT >= 21) {
//		 full(false);
//		 this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		 } else {
//		 getWindow().addFlags(
//		 WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//		 this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		 }
		Screen.initialize(this);
		setContentView(R.layout.activity_opengl);
		init();
		displaySplash();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				startRecorder();
			}
		}, 1000);
		instance = this;
	}

	public Context getContext() {
		return this;
	}

	@SuppressWarnings("unchecked")
	private void init() {
		if (android.os.Build.MODEL.equals("PLK-AL10"))
			printTime = 50;
//		rv_dialog=  (RelativeLayout) findViewById(R.id.rv_dialog);
		faceBackMenu = (ImageView) findViewById(R.id.face_back_menu);
		faceBack = (ImageView) findViewById(R.id.face_back);
		mImageGo = (ImageView) findViewById(R.id.img_face_go);
		mImageGo.setVisibility(View.GONE);
		staffId = getIntent().getStringExtra("staffId");
		isDebug = getIntent().getBooleanExtra("isdebug", false);
		isFaceProperty = getIntent().getBooleanExtra("isFaceProperty", false);
		isBackCamera = getIntent().getBooleanExtra("isBackCamera", false);
		isStartRecorder = getIntent().getBooleanExtra("isStartRecorder", false);
		isROIDetect = getIntent().getBooleanExtra("ROIDetect", false);
		isTiming = getIntent().getBooleanExtra("isTiming", false);
		isTiming = true;
		min_face_size = getIntent().getIntExtra("faceSize", min_face_size);
		detection_interval = getIntent().getIntExtra("interval",
				detection_interval);
		resolutionMap = (HashMap<String, Integer>) getIntent()
				.getSerializableExtra("resolution");
		facepp = new Facepp();
		sensorUtil = new SensorEventUtil(this);
		mHandlerThread.start();
		mHandler = new Handler(mHandlerThread.getLooper());
		mGlSurfaceView = (GLSurfaceView) findViewById(R.id.opengl_layout_surfaceview);
		mGlSurfaceView.setEGLContextClientVersion(2);// 创建一个OpenGL ES 2.0
														// context
		mGlSurfaceView.setRenderer(this);// 设置渲染器进入gl
		// RENDERMODE_CONTINUOUSLY不停渲染
		// RENDERMODE_WHEN_DIRTY懒惰渲染，需要手动调用 glSurfaceView.requestRender() 才会进行更新
		mGlSurfaceView.setRenderMode(mGlSurfaceView.RENDERMODE_WHEN_DIRTY);// 设置渲染器模式
		mGlSurfaceView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				autoFocus();
			}
		});
		mICamera = new ICamera();
		mDialogUtil = new DialogUtil(this);
		faceBackMenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent result = new Intent();
				setResult(2, result);
				finish();
			}

		});
		faceBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}

		});
		// debugInfoText = (TextView)
		// findViewById(R.id.opengl_layout_debugInfotext);
		// debugPrinttext = (TextView)
		// findViewById(R.id.opengl_layout_debugPrinttext);
		// if (isDebug)
		// debugInfoText.setVisibility(View.VISIBLE);
		// else
		// debugInfoText.setVisibility(View.INVISIBLE);
	}

	/**
	 * 开始录制
	 */
	private void startRecorder() {
		if (isStartRecorder) {
			int Angle = 360 - mICamera.Angle;
			if (isBackCamera)
				Angle = 180 - mICamera.Angle;
			mediaRecorderUtil = new MediaRecorderUtil(this, mCamera,
					mICamera.cameraWidth, mICamera.cameraHeight);
			isStartRecorder = mediaRecorderUtil.prepareVideoRecorder(Angle);
			if (isStartRecorder) {
				mediaRecorderUtil.start();
				mICamera.actionDetect(this);
			}
		}
	}

	/**
	 * 自动对焦
	 */
	private void autoFocus() {
		if (mCamera != null && isBackCamera) {
			mCamera.cancelAutoFocus();
			Parameters parameters = mCamera.getParameters();
			// parameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
			parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
			mCamera.setParameters(parameters);
			// mCamera.autoFocus(null);
		}
	}

	private int Angle;

	@Override
	protected void onResume() {
		super.onResume();
		ConUtil.acquireWakeLock(this);
		startTime = System.currentTimeMillis();
		mCamera = mICamera.openCamera(isBackCamera, this, resolutionMap);

		if (mCamera != null) {
			Angle = 360 - mICamera.Angle;
			if (isBackCamera)
				Angle = 360 - mICamera.Angle - 180;
			CameraInfo cameraInfo = new CameraInfo();
			Camera.getCameraInfo(mICamera.cameraId, cameraInfo);
			RelativeLayout.LayoutParams layout_params = mICamera
					.getLayoutParam();
			// mGlSurfaceView.setLayoutParams(layout_params);
			int width = mICamera.cameraWidth;
			int height = mICamera.cameraHeight;
			int left = 0;
			int top = 0;
			int right = width;
			int bottom = height;
			// if (isROIDetect) {
			// float line = height * roi_ratio;
			// left = (int) ((width - line) / 2.0f);
			// top = (int) ((height - line) / 2.0f);
			// right = width - left;
			// bottom = height - top;
			// }

			String errorCode = facepp.init(this, ConUtil.getFileContent(this,
					R.raw.megviifacepp_0_2_0_model));
			// Log.w(TAG, "errorCode====" + errorCode);
			FaceppConfig faceppConfig = facepp.getFaceppConfig();
			faceppConfig.rotation = Angle;
			faceppConfig.interval = detection_interval;
			faceppConfig.minFaceSize = min_face_size;
			faceppConfig.roi_left = left;
			faceppConfig.roi_top = top;
			faceppConfig.roi_right = right;
			faceppConfig.roi_bottom = bottom;
			faceppConfig.detectionMode = FaceppConfig.DETECTION_MODE_TRACKING;
			facepp.setFaceppConfig(faceppConfig);
		} else {
			mDialogUtil.showDialog("打开相机失败");
		}
	}

	public Bitmap decodeToBitMap(byte[] data, Camera _camera) {
		Size size = _camera.getParameters().getPreviewSize();
		try {
			YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width,
					size.height, null);
			if (image != null) {
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				image.compressToJpeg(new Rect(0, 0, size.width, size.height),
						80, stream);
				Bitmap bmp = BitmapFactory.decodeByteArray(
						stream.toByteArray(), 0, stream.size());
				stream.close();
				return bmp;
			}
		} catch (Exception ex) {
		}
		return null;
	}

	/**
	 * 画绿色框
	 */
	private void drawShowRect() {
		mPointsMatrix.vertexBuffers = OpenGLDrawRect.drawCenterShowRect(
				isBackCamera, mICamera.cameraWidth, mICamera.cameraHeight,
				roi_ratio);
	}

	public void getSensor(float x, float y, float z) {
		// if (mPointsMatrix != null) {
		// mPointsMatrix.bottomVertexBuffer =
		// OpenGLDrawRect.drawBottomShowRect(0.15f, 0, 0f, x, y, z,180);
		// }
	}

	boolean isSuccess = false;
	float confidence;
	float pitch, yaw, roll;
	long startTime;
	long get3DPosefaceTime_end = 0;

	@Override
	public void onPreviewFrame(final byte[] imgData, final Camera camera) {
		// Face[] faces;
		if (System.currentTimeMillis() - startTime < 3000)
			return;
		if (isSuccess)
			return;
		isSuccess = true;
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				int width = mICamera.cameraWidth;
				int height = mICamera.cameraHeight;
				long faceDetectTime_action = System.currentTimeMillis();
				final Face[] faces = facepp.detect(imgData, width, height,
						Facepp.IMAGEMODE_NV21);
				if (faces != null) {
					final long algorithmTime = System.currentTimeMillis()
							- faceDetectTime_action;
					long actionMaticsTime = System.currentTimeMillis();
					float ling = width > height ? width : height;
					float widthValue_point = 0.3755f;
					float hightValue = 0.5f;
					ArrayList<ArrayList> pointsOpengl = new ArrayList<ArrayList>();
					ArrayList<FloatBuffer> vertexBuffersOpengl = new ArrayList<FloatBuffer>();
					confidence = 0.0f;
					if (faces.length >= 0) {
						for (int c = 0; c < faces.length; c++) {
							facepp.getLandMark(faces, c, Facepp.facePoints);
							long get3DPosefaceTime_action = System
									.currentTimeMillis();
							facepp.get3DPose(faces, c);
							get3DPosefaceTime_end = System.currentTimeMillis()
									- get3DPosefaceTime_action;
							RectF rectF = new RectF();
							float _x_offset = 0, _y_offset = 0;
							float max_len = height;
							if (width > height) {
								max_len = width;
								_x_offset = (width - height) / 2;
							} else
								_y_offset = (height - width) / 2;

							rectF.left = (faces[c].rect.left + _x_offset)
									/ max_len;
							rectF.top = (faces[c].rect.top + _y_offset)
									/ max_len;
							rectF.right = (faces[c].rect.right + _x_offset)
									/ max_len;
							rectF.bottom = (faces[c].rect.bottom + _y_offset)
									/ max_len;
							//
							// Log.w("ceshi", "faces[c].rect===" + max_len + ",
							// " + faces[c].rect + ", " + rectF);
							//
							// float _left = rectF.left * 2 - 1;
							// float _right = rectF.right * 2 - 1;
							// float _top = 1 - rectF.top * 2;
							// float _bottom = 1 - rectF.bottom * 2;
							// Log.w("ceshi", "111rect===" + _left + ", " +
							// _right + ", " + _top + ", " + _bottom);
							// if (isBackCamera) {
							// _left = -_left;
							// _right = -_right;
							// }
							// float delta_x = 3 / height, delta_y = 3 / height;
							// // 4个点分别是// top_left bottom_left bottom_right
							// // top_right
							// float rectangle_left[] = { _left, _top, 0, _left,
							// _bottom, 0, _left + delta_x, _bottom, 0,
							// _left + delta_x, _top, 0 };
							// float rectangle_top[] = { _left, _top, 0, _left,
							// _top - delta_y, 0, _right, _top - delta_y,
							// 0, _right, _top, 0 };
							// float rectangle_right[] = { _right - delta_x,
							// _top, 0, _right - delta_x, _bottom, 0, _right,
							// _bottom, 0, _right, _top, 0 };
							// float rectangle_bottom[] = { _left, _bottom +
							// delta_y, 0, _left, _bottom, 0, _right,
							// _bottom, 0, _right, _bottom + delta_y, 0 };
							//
							// FloatBuffer fb_left =
							// mPointsMatrix.floatBufferUtil(rectangle_left);
							// FloatBuffer fb_top =
							// mPointsMatrix.floatBufferUtil(rectangle_top);
							// FloatBuffer fb_right =
							// mPointsMatrix.floatBufferUtil(rectangle_right);
							// FloatBuffer fb_bottom =
							// mPointsMatrix.floatBufferUtil(rectangle_bottom);
							//
							// vertexBuffersOpengl.add(fb_left);
							// vertexBuffersOpengl.add(fb_top);
							// vertexBuffersOpengl.add(fb_right);
							// vertexBuffersOpengl.add(fb_bottom);

							pitch = faces[c].pitch;
							yaw = faces[c].yaw;
							roll = faces[c].roll;
							confidence = faces[c].confidence;

							ArrayList<FloatBuffer> triangleVBList = new ArrayList<FloatBuffer>();
							for (int i = 0; i < faces[c].points.length; i++) {
								float x = ((faces[c].points[i].x + _x_offset) / max_len) * 2 - 1;
								if (isBackCamera)
									x = -x;
								float y = 1 - (((faces[c].points[i].y + _y_offset) + _y_offset) / max_len) * 2;
								float[] pointf = { x, y, 0.0f };
								FloatBuffer fb = mCameraMatrix
										.floatBufferUtil(pointf);
								triangleVBList.add(fb);

							}
							pointsOpengl.add(triangleVBList);
						
						}
					} else {
						pitch = 0.0f;
						yaw = 0.0f;
						roll = 0.0f;
					}
					if (faces.length < 2) {
						if (faces.length > 0) {
							OpenglActivity.instance
									.runOnUiThread(new Runnable() {

										@Override
										public void run() {
											// 更新UI
											mImageGo.setVisibility(View.VISIBLE);
										}

									});
//
//							if (temp >= 82) {
//								runOnUiThread(new Runnable() {
//
//									@Override
//									public void run() {
//										// 更新UI
//										mImageGo.setVisibility(View.GONE);
//									}
//
//								});
//							}
							// GO anniu ke dianji dianji huoqu tupian
							mImageGo.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									Log.e("tTIM", "diami1");
									// TODO Auto-generated method stub
//									 Bitmap bFace=decodeToBitMap(imgData,camera);
									Bitmap bFace = mICamera.getBitMap(
											imgData, camera, true);
									Log.e("tTIM", "diami2");
									Log.e(TAG, "tupiandaxiao"+bFace.getRowBytes() * bFace.getHeight()+"tupian gaodu "+bFace.getHeight()+"kuandu=="+bFace.getWidth());
									Bitmap roBitmap = ImageUtil
											.getRotateBitmap(bFace,
													90.0f);
									//通过改变图片质量来压缩占用内存大小
								     ByteArrayOutputStream baos = new ByteArrayOutputStream();  
									roBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
							        int options = 100; 
							        //循环压缩
							        while ( baos.toByteArray().length / 1024>15) {  
							            baos.reset();
							            roBitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
							            options -= 10;
							        }  
							        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());  
							        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
								        Log.e("tTIM", "diami3+"+bitmap.getRowBytes() * bitmap.getHeight());
								    //bitmap转换成base64
									String faceBase64 = bitmapToBase64(bitmap);
                                    //上传图片
									FaceUpLoadUtil mFaceUpLoadUtil = new FaceUpLoadUtil(
											OpenglActivity.this,
											handler);
									//调用上传方法
									mFaceUpLoadUtil.upFaceImage(
											staffId, faceBase64);
									//更改UI，上传是出现半透明遮罩层
									OpenglActivity.instance
											.runOnUiThread(new Runnable() {

												@Override
												public void run() {
													// 更新UI
//													rv_dialog.setVisibility(View.VISIBLE);
													faceDialog.show();

												}

											});
								}
							});
						} else {
							OpenglActivity.instance
							.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									// 更新UI
									mImageGo.setVisibility(View.GONE);
								}

							});
						}

					} else {
						OpenglActivity.instance
						.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// 更新UI
								mImageGo.setVisibility(View.GONE);
							}

						});
					}
					
					if (isFaceProperty)
						mPointsMatrix.bottomVertexBuffer = OpenGLDrawRect
								.drawBottomShowRect(0.15f, 0, -0.7f, pitch,
										-yaw, roll, Angle);
					synchronized (mPointsMatrix) {
						mPointsMatrix.points = pointsOpengl;
						// mPointsMatrix.vertexBuffers = vertexBuffersOpengl;
					}

					final long matrixTime = System.currentTimeMillis()
							- actionMaticsTime;
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							DecimalFormat decimalFormat = new DecimalFormat(
									".00");// 构造方法的字符格式这里如果小数不足2位,会以0补足.
							String pitch_str = decimalFormat.format(pitch);// format
																			// 返回的是字符串
							String yaw_str = decimalFormat.format(yaw);// format
																		// 返回的是字符串
							String roll_str = decimalFormat.format(roll);// format
						}
					});
				}

				isSuccess = false;
				if (!isTiming) {
					timeHandle.sendEmptyMessage(1);
				}
			}
		});

	}

	@Override
	protected void onPause() {
		super.onPause();
		ConUtil.releaseWakeLock();
		if (mediaRecorderUtil != null) {
			mediaRecorderUtil.releaseMediaRecorder();
		}
		mICamera.closeCamera();
		mCamera = null;

		timeHandle.removeMessages(0);

		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		facepp.release();
	}

	private int mTextureID = -1;
	private SurfaceTexture mSurface;
	private CameraMatrix mCameraMatrix;
	private PointsMatrix mPointsMatrix;

	@Override
	public void onFrameAvailable(SurfaceTexture surfaceTexture) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// 黑色背景
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		mTextureID = OpenGLUtil.createTextureID();
		mSurface = new SurfaceTexture(mTextureID);
		// 这个接口就干了这么一件事，当有数据上来后会进到onFrameAvailable方法
		mSurface.setOnFrameAvailableListener(this);// 设置照相机有数据时进入
		mCameraMatrix = new CameraMatrix(mTextureID);
		mPointsMatrix = new PointsMatrix();
		mICamera.startPreview(mSurface);// 设置预览容器
		mICamera.actionDetect(this);
		if (isTiming) {
			timeHandle.sendEmptyMessageDelayed(0, printTime);
		}
		if (isROIDetect)
			drawShowRect();

	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		if (mCamera != null) {
			mCamera.autoFocus(new AutoFocusCallback() {

				@Override
				public void onAutoFocus(boolean success, Camera camera) {
					// TODO Auto-generated method stub
					autoFocus();
					// mCamera.cancelAutoFocus();
				}
			});
		}

		// 设置画面的大小
		GLES20.glViewport(0, 0, width, height);
		float ratio = (float) width / height;
		// this projection matrix is applied to object coordinates
		// in the onDrawFrame() method
		Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
		// Matrix.perspectiveM(mProjMatrix, 0, 0.382f, ratio, 3, 700);

	}

	private final float[] mMVPMatrix = new float[16];
	private final float[] mProjMatrix = new float[16];
	private final float[] mVMatrix = new float[16];
	private final float[] mRotationMatrix = new float[16];

	@Override
	public void onDrawFrame(GL10 gl) {
		final long actionTime = System.currentTimeMillis();
		// Log.w("ceshi", "onDrawFrame===");
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);// 清除屏幕和深度缓存
		float[] mtx = new float[16];
		mSurface.getTransformMatrix(mtx);
		mCameraMatrix.draw(mtx);
		// Set the camera position (View matrix)
		Matrix.setLookAtM(mVMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1f, 0f);
		// Calculate the projection and view transformation
		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
		mPointsMatrix.draw(mMVPMatrix);
		if (isDebug) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					final long endTime = System.currentTimeMillis()
							- actionTime;
					// debugPrinttext.setText("printTime: " + endTime);
				}
			});
		}
		mSurface.updateTexImage();// 更新image，会调用onFrameAvailable方法
	}

	Handler timeHandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				mGlSurfaceView.requestRender();// 发送去绘制照相机不断去回调
				timeHandle.sendEmptyMessageDelayed(0, printTime);
				break;
			case 1:
				mGlSurfaceView.requestRender();// 发送去绘制照相机不断去回调
				break;
			case 2:
				mImageGo.setVisibility(View.GONE);
				break;
			}
		}
	};
	/**
	 * bitmap转为base64
	 * 
	 * @param bitmap
	 * @return
	 */
	public static String bitmapToBase64(Bitmap bitmap) {

		String result = null;
		ByteArrayOutputStream baos = null;
		try {
			if (bitmap != null) {
				baos = new ByteArrayOutputStream();
				// bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
				baos.flush();
				baos.close();

				byte[] bitmapBytes = baos.toByteArray();
				result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
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
		return result;
	}

	public void detect(final Bitmap image) {

		new Thread(new Runnable() {

			public void run() {
				HttpRequests httpRequests = new HttpRequests(Util.API_KEY,
						Util.API_SECRET, true, true);
				// Log.v(TAG, "image size : " + img.getWidth() + " " +
				// img.getHeight());

				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				float scale = Math.min(
						1,
						Math.min(600f / image.getWidth(),
								600f / image.getHeight()));
				Matrix matrix = new Matrix();
				// matrix.postScale(scale, scale);

				// Bitmap imgSmall = Bitmap.createBitmap(image, 0, 0,
				// image.getWidth(), image.getHeight(), matrix, false);
				Bitmap imgSmall = Bitmap.createBitmap(image, 0, 0,
						image.getWidth(), image.getHeight());
				imgSmall.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				byte[] array = stream.toByteArray();

				try {
					// detect
					final JSONObject result = httpRequests
							.detectionDetect(new PostParameters().setImg(array));
					detectResult(result);
				} catch (FaceppParseException e) {
					e.printStackTrace();

				}

			}
		}).start();
	}

	//

	public void detectResult(JSONObject rst) {

		// Log.v(TAG, rst.toString());
		// use the red paint
		Paint paint = new Paint();
		paint.setColor(Color.RED);
		paint.setStrokeWidth(Math.max(b1.getWidth(), b1.getHeight()) / 100f);

		// create a new canvas
		Bitmap bitmap = Bitmap.createBitmap(b1.getWidth(), b1.getHeight(),
				b1.getConfig());
		Canvas canvas = new Canvas(bitmap);
		// canvas.drawBitmap(b1, new Matrix(), null);
		try {
			// find out all faces
			final int count = rst.getJSONArray("face").length();
			for (int i = 0; i < count; ++i) {
				float x, y, w, h;
				// get the center point
				x = (float) rst.getJSONArray("face").getJSONObject(i)
						.getJSONObject("position").getJSONObject("center")
						.getDouble("x");
				y = (float) rst.getJSONArray("face").getJSONObject(i)
						.getJSONObject("position").getJSONObject("center")
						.getDouble("y");

				// get face size
				w = (float) rst.getJSONArray("face").getJSONObject(i)
						.getJSONObject("position").getDouble("width");
				h = (float) rst.getJSONArray("face").getJSONObject(i)
						.getJSONObject("position").getDouble("height");

				// change percent value to the real size
				x = x / 100 * b1.getWidth();
				w = w / 100 * b1.getWidth() * 0.7f;
				y = y / 100 * b1.getHeight();
				h = h / 100 * b1.getHeight() * 0.7f;

				// draw the box to mark it out
				canvas.drawLine(x - w, y - h, x - w, y + h, paint);
				canvas.drawLine(x - w, y - h, x + w, y - h, paint);
				canvas.drawLine(x + w, y + h, x - w, y + h, paint);
				canvas.drawLine(x + w, y + h, x + w, y - h, paint);
				// mPointsMatrix.vertexBuffers =
				// OpenGLDrawRect.drawCenterShowRect(isBackCamera,x,y,roi_ratio);
			}
			// save new image
		} catch (JSONException e) {
			e.printStackTrace();

		}

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

}