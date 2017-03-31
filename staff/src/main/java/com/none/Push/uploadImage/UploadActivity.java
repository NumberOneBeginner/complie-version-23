package com.none.Push.uploadImage;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.none.Push.MyPushEvent;
import com.none.Push.uploadImage.domains.Image;
import com.none.Push.uploadImage.domains.utils.AddPopWindow;
import com.none.Push.uploadImage.domains.utils.AddPopWindow.OnItemOnClickListener;
import com.none.Push.uploadImage.domains.utils.HttpClientUtils;
import com.none.Push.uploadImage.domains.utils.ImageCompress;
import com.none.Push.uploadImage.domains.utils.MessageResult;
import com.none.Push.uploadImage.domains.utils.MyProgressDialog;
import com.none.staff.R;
import com.none.staff.activity.BaseActivity;
import com.none.staff.utils.SPUtil;

import de.greenrobot.event.EventBus;

/**
 * Created by gengyafeng on 16-5-16.
 */
public class UploadActivity extends BaseActivity {

	private GridView gridView;
	private GridAdapter gridAdapter;
	private ArrayList<String> list;
	private ArrayList<String> imagePaths = new ArrayList<String>();
	private ArrayList<File> compImage_list = new ArrayList<File>();
	private ArrayList<String> data_list;
	private Spinner spinner;
	private Button button_upload;
	private ArrayList<Image> list_image;
	private ImageView activity_back_regist;
	private String User_ID;
	private TextView spinner_et;
	protected Dialog selectTestUserDialog;
	protected String[] activity_id;
	protected String[] activity_subject;
	private String Activity_Id="";
	private EditText remark_upload;
	private LinearLayout uploadContainer;
	private MyProgressDialog progressDialog;
	protected JSONArray activityList;
	protected String activityId_Upload;
	private boolean isClicked=false;
	protected long currentTime;
	protected String remark;
	private Button item_popupwindows_medium;
	private Button item_popupwindows_large;
	private View item_popupwindows_cancel;
	private AlertDialog dialog;
	private ImageView image_spinner;
	private ImageView image_spinner_up;
	protected AddPopWindow addPopwindow;
	private float sumOldSize=0.0f;
	private float sumNewSize=0.0f;
	protected JSONArray array;
	private MyProgressDialog ProgressBar_Serach;



	public static Handler handler=new Handler();
	private String uploadURL;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.uploadlayout);
        uploadURL=getResources().getString(R.string.upload_url);
		//		EventBus.getDefault().register(this) ;
		setView();
		checkListSpinner("first");

		image_spinner.setOnClickListener(new OnClickListener() {


			@Override
			public void onClick(View arg0) {

				//是否已经连接网络，如果连接就checkSpinner();

				if (com.none.staff.utils.NetworkUtils.isNetworkAvailable(UploadActivity.this)){

					if(activity_subject==null||activity_subject.length==0){
						String Spu_activity_subject = SPUtil.getValue(UploadActivity.this, SPUtil.ACTIVITY_SUBJECT);

						if(Spu_activity_subject==null||Spu_activity_subject.length()==0){
							checkListSpinner("network");
						}else{
							try {
								array = new JSONArray(Spu_activity_subject);
								activity_subject=new String[array.length()] ;

								for(int i=0;i<array.length();i++){
									String activity_subject_array = array.getString(i);
									JSONObject jsonobj = new JSONObject(activity_subject_array);
									activity_subject[i]=jsonobj.getString("subject");
								}

							} catch (JSONException e) {
								e.printStackTrace();
							}
						}

						if(activity_subject==null||activity_subject.length==0){
							return;
						}
					}

				}else{

					if(activity_subject==null||activity_subject.length==0){
						String Spu_activity_subject = SPUtil.getValue(UploadActivity.this, SPUtil.ACTIVITY_SUBJECT);
						try {
							array = new JSONArray(Spu_activity_subject);
							activity_subject=new String[array.length()] ;

							for(int i=0;i<array.length();i++){
								String activity_subject_array = array.getString(i);
								JSONObject jsonobj = new JSONObject(activity_subject_array);
								activity_subject[i]=jsonobj.getString("subject");
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					if(activity_subject==null||activity_subject.length==0){
						selectDialog("获取数据失败,请确保网络连接");
						return;
					}

				}

				addPopwindow = new AddPopWindow(UploadActivity.this,activity_subject);
				addPopwindow.showPopwindowdhalf(spinner_et,60,300);  
				image_spinner.setVisibility(View.GONE);
				image_spinner_up.setVisibility(View.VISIBLE);
				addPopwindow.setItemOnClickListener(new OnItemOnClickListener() {

					@Override
					public void onItemClick(int position) {

						//这个position就是数组的下标
						image_spinner.setVisibility(View.VISIBLE);
						image_spinner_up.setVisibility(View.GONE);
					}
				});			

				addPopwindow.setOnDismissListener(new OnDismissListener() {

					@Override
					public void onDismiss() {
						image_spinner.setVisibility(View.VISIBLE);
						image_spinner_up.setVisibility(View.GONE);
					}
				});
			}

		});
		for (int i = 0; i < list_image.size(); i++) {
			list.add(list_image.get(i).path);
		}
		gridView = (GridView) findViewById(R.id.gridView_upload);
		int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
		cols = cols < 3 ? 3 : cols;
		gridView.setNumColumns(cols);
		loadAdpater(list);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id	) {

				if(	parent.getAdapter().getItem(position).equals("000000")){
					finish();
					overridePendingTransition(0, 0);
				}
			}
		});

		activity_back_regist.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		button_upload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				currentTime = getCurrentTime();
				String	Activity_Edit =spinner_et.getText().toString();
				remark = remark_upload.getEditableText().toString();

				if(remark==null){
					remark="";
				}

				if(array!=null&&array.length()!=0){
					for (int i = 0; i < array.length(); i++) {
						if(Activity_Edit.equalsIgnoreCase(activity_subject[i])){
							activityId_Upload = SPUtil.getValue(getApplicationContext(), activity_subject[i]);
							Log.i(" activityId_Upload :", "activityId_Upload"+activityId_Upload);
						}
					}
				}			

				if(activityId_Upload==null||activityId_Upload.equals("")){
					selectDialog("Please select Activity!");
					return;
				}

				displayDialog(sumOldSize,sumNewSize);
			}
		});
	}



	protected void selectDialog(String title) {

		AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this) ;
		builder.setTitle(title);
		builder.setNegativeButton("Cancel", null);
		builder.create().show() ;
	}


	private void setView() {
		User_ID=getIntent().getStringExtra(PhotoPickerActivity.EXTRA_USER_ID);
		button_upload = (Button) findViewById(R.id.button_upload);
		activity_back_regist=(ImageView)findViewById(R.id.activity_back_regist);
		uploadContainer=(LinearLayout)findViewById(R.id.uploadContainer);
		remark_upload=(EditText)findViewById(R.id.remark_upload);
		spinner_et=(TextView)findViewById(R.id.spinner_et);
		image_spinner=(ImageView)findViewById(R.id.image_spinner);
		image_spinner_up=(ImageView)findViewById(R.id.image_spinner_up);
		list_image = (ArrayList<Image>) getIntent().getSerializableExtra(PhotoPickerActivity.EXTRA_RESULT);

		data_list = new ArrayList<String>();
		list = new ArrayList<String>();

		new Thread(){

			public void run() {


				for (int i=0 ;i<list_image.size();i++){
					//在这里是压缩
					//					File imageFile = new File(list_image.get(i).path);
					//					Uri uri = Uri.fromFile(imageFile);
					//					Bitmap bitmap = BitmapFactory.decodeFile(list_image.get(i).path);
					//					File outputFile = new File(PhotoUtil.getSDPath(list_image.get(i).path),"image.txt");
					//					File scaledFile=ImageCompress.compressBmpToFile(bitmap, outputFile);
					//					compImage_list.add(scaledFile);
					//					int oldSize = (int)new File(uri.getPath()).length()/(1024);   //压缩图片
					//					int newSize = (int)scaledFile.length()/(1024);
					//					sumOldSize=sumOldSize+oldSize;
					//					sumNewSize=sumNewSize+newSize;

					File imageFile = new File(list_image.get(i).path);
					Uri uri = Uri.fromFile(imageFile);
					File scaledFile = ImageCompress.scal(uri);
					compImage_list.add(scaledFile);

					float oldSize = (float)new File(uri.getPath()).length()/1024;   //压缩图片
					float newSize = (float)scaledFile.length()/1024;
					sumOldSize=sumOldSize+oldSize;
					sumNewSize=sumNewSize+newSize;	
				}		

			};


		}.start();

	}		

	private void checkListSpinner(final String str) {


		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("userId", User_ID);//此处是传过来的User_id;

		HttpClientUtils.getInstance().post(uploadURL+"getActivityList", null, params, UploadActivity.this, new AsyncHttpResponseHandler() {

			private String[] activity_subjecStore;
			private String[] activity_idStore;

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {

				Log.i("faluire", "faluire");

			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] bytes) {
				String jsonString = new String(bytes);

				MessageResult messageResult = MessageResult.prase(jsonString);
				int code = messageResult.getCode();

				if(code==0){
					try {

						JSONObject jsonObject = new JSONObject(messageResult.getData())  ;
						activityList = jsonObject.getJSONArray("activityList");
						activity_idStore=new String[activityList.length()] ;
						activity_subjecStore=new String[activityList.length()];
						for(int i=0;i<activityList.length();i++){
							JSONObject arrray_string=(JSONObject)activityList.get(i);
							activity_idStore[i]=arrray_string.getString("id");
							activity_subjecStore[i]=arrray_string.getString("subject");
							SPUtil.putValue(getApplicationContext(), activity_subjecStore[i], activity_idStore[i]);
						}

						if(activityList!=null){

							SPUtil.putValue(getApplicationContext(), SPUtil.ACTIVITY_SUBJECT, activityList.toString());
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}

					if(str.equals("network")){
						addPopwindow = new AddPopWindow(UploadActivity.this,activity_subjecStore);	
						addPopwindow.showPopwindowdhalf(spinner_et,60,300);
						image_spinner.setVisibility(View.GONE);
						image_spinner_up.setVisibility(View.VISIBLE);

						addPopwindow.setItemOnClickListener(new OnItemOnClickListener() {

							@Override
							public void onItemClick(int position) {

								//这个position就是数组的下标

								activityId_Upload=activity_idStore[position];
								image_spinner.setVisibility(View.VISIBLE);
								image_spinner_up.setVisibility(View.GONE);
							}
						});			

						addPopwindow.setOnDismissListener(new OnDismissListener() {

							@Override
							public void onDismiss() {
								image_spinner.setVisibility(View.VISIBLE);
								image_spinner_up.setVisibility(View.GONE);
							}
						});

					}
				}		
			}
		});		
	}

	protected void displayDialog(Float oldSize,Float newSize) {

		View view_dialog = getLayoutInflater().inflate(R.layout.item_compress, null);
		item_popupwindows_medium=(Button)view_dialog.findViewById(R.id.item_popupwindows_medium);
		item_popupwindows_large=(Button)view_dialog.findViewById(R.id.item_popupwindows_large);
		item_popupwindows_cancel=view_dialog.findViewById(R.id.item_popupwindows_cancel);

		dialog = new AlertDialog.Builder(UploadActivity.this) 
		.create();  
		dialog.show();  

		Window window = dialog.getWindow();  
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.gravity=Gravity.BOTTOM;
		Display display = getWindowManager().getDefaultDisplay();  
		lp.width =(int)(display.getWidth()*0.95); 
		lp.y = 20; 
		window.setAttributes(lp);
		window.setContentView(view_dialog);


		String newsize_updte = new java.text.DecimalFormat("#0.0").format(newSize);

		if(oldSize/1024>1){

			String oldsize_updte = new java.text.DecimalFormat("#0.0").format((oldSize/1024));
			item_popupwindows_large.setText("Large ("+ oldsize_updte +" M ) " );
		}else {

			String oldsize_updte = new java.text.DecimalFormat("#0.0").format(oldSize);
			item_popupwindows_large.setText("Large ("+ oldsize_updte +" K ) " );

		}


		item_popupwindows_medium.setText("Medium (" +newsize_updte+" K ) ");


		item_popupwindows_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});

		item_popupwindows_large.setOnClickListener(new OnClickListener() {

			public int s=0;
			public	int index=0;

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();

				UploadImage(index);		
			}


			private void UploadImage(int index) {
				HashMap<String, Object> params = new HashMap<String, Object>();
				params.put("activityId",activityId_Upload);
				params.put("type", "activity");
				params.put("remark", remark);
				params.put("batch_flag", currentTime);
				params.put("userId", User_ID);
				params.put("fileNo", "0"+(index+1));
				params.put("actiontype", 1);
				params.put("fileNums", String.valueOf(list_image.size()));
				params.put("multipartFile", new File(list_image.get(index).path));


				Log.i("multipartFile", "list_image.get(index).path :" +list_image.get(index).path +" index :"+index);


				HttpClientUtils.getInstance().post(uploadURL+"saveUploadPicOrVideoSingle_android", null, params, UploadActivity.this, new AsyncHttpResponseHandler() {

					private MyProgressDialog ProgressBar;

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {

						MyProgressDialog.stopProgressDiaog();
						Toast.makeText(UploadActivity.this, "联网失败，上传失败", 1).show();

					}

					@Override
					public void onProgress(int bytesWritten, int totalSize) {
						// TODO Auto-generated method stub
						super.onProgress(bytesWritten, totalSize);
						ProgressBar.dialog_cancel.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {

								Log.i("0000000", "onProgress onProgress onProgress");
								//
								isClicked=true;

								ProgressBar.stopProgressDiaog();
							}
						});
					}

					@Override
					public void onStart() {

						super.onStart();
						//			

						ProgressBar=MyProgressDialog.showProgressShow(UploadActivity.this, "upload");
						MyProgressDialog.dialog_cancel.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								isClicked=true;
								ProgressBar.stopProgressDiaog();
							}
						});
					}


					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] bytes) {
						String jsonString = new String(bytes);
						MessageResult messageResult = MessageResult.prase(jsonString);
						int code = messageResult.getCode();
						if(code==0){

							s++;
							new Thread(new Runnable() {
								@Override
								public void run() {
									//发消息更新dialog的数据
									Message msg = new Message();
									msg.what = 1;
									msg.getData().putInt("uploadcount", s);
									msg.getData().putInt("size", list_image.size());
									handler.sendMessage(msg);
								}
							}
									).start();

							rereshCount(s);

							if(isClicked){
								isClicked=false;
								UploadCancle();

							}else{

								if(s<list_image.size()){
									UploadImage(s);
								}	
							}

						}
					}
					private void UploadCancle() {

						HashMap<String, Object> params = new HashMap<String, Object>();
						params.put("activityId", activityId_Upload);
						params.put("type", "activity");
						params.put("batch_flag", currentTime);
						params.put("userId", User_ID);
						params.put("ios_android_flag", "android");
						params.put("fileNums", String.valueOf(list_image.size()));

						HttpClientUtils.getInstance().post(uploadURL+"removeUploadPicOrVideo", null, params, UploadActivity.this, new AsyncHttpResponseHandler() {
							@Override
							public void onFailure(int arg0,
									Header[] arg1, byte[] arg2,
									Throwable arg3) {
							}

							@Override
							public void onSuccess(int arg0,
									Header[] arg1, byte[] bytes) {
								String jsonString = new String(bytes);
								MessageResult messageResult = MessageResult.prase(jsonString);
								int code = messageResult.getCode();
								if(code==0){
									MyProgressDialog.stopProgressDiaog();	
									Toast.makeText(getApplicationContext(), "取消成功", 1).show();
								}		
							}
						});

					}

					private void rereshCount(int count) {

						Log.i("0000", "list_image.size()"+list_image.size()+"count:"+count);

						if(list_image.size()==count){
							EventBus.getDefault().post(new MyPushEvent("success"));
							button_upload.setText("upload  finish");

							new Handler().postDelayed(new Runnable() {
								@Override
								public void run() {
									MyProgressDialog.stopProgressDiaog();
									UploadActivity.this.finish();
								}
							}, 1000);
							return;
						}
					}
				});			
			}

		});



		item_popupwindows_medium.setOnClickListener(new OnClickListener() {
			public int s=0;
			public	int index=0;

			@Override
			public void onClick(View arg0) {

				dialog.dismiss();

				UploadImage(index);	

			}
			private void UploadImage(int index) {
				HashMap<String, Object> params = new HashMap<String, Object>();
				params.put("activityId",activityId_Upload);
				params.put("type", "activity");
				params.put("remark", remark);
				params.put("batch_flag", currentTime);
				params.put("userId", User_ID);
				params.put("fileNo", "0"+(index+1));
				params.put("actiontype", 1);
				params.put("fileNums", String.valueOf(list_image.size()));
				params.put("multipartFile", compImage_list.get(index));

				HttpClientUtils.getInstance().post(uploadURL+"saveUploadPicOrVideoSingle_android", null, params, UploadActivity.this, new AsyncHttpResponseHandler() {

					private MyProgressDialog ProgressBar;

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {

						MyProgressDialog.stopProgressDiaog();
						Toast.makeText(UploadActivity.this, "联网失败，上传失败", 1).show();

					}

					@Override
					public void onProgress(int bytesWritten, int totalSize) {
						// TODO Auto-generated method stub
						super.onProgress(bytesWritten, totalSize);
						ProgressBar.dialog_cancel.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {

								Log.i("0000000", "onProgress onProgress onProgress");

								isClicked=true;
								ProgressBar.stopProgressDiaog();
							}
						});
					}

					@Override
					public void onStart() {

						super.onStart();


						ProgressBar=MyProgressDialog.showProgressShow(UploadActivity.this, "upload");
						MyProgressDialog.dialog_cancel.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								isClicked=true;
								ProgressBar.stopProgressDiaog();
							}
						});
					}


					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] bytes) {
						String jsonString = new String(bytes);
						MessageResult messageResult = MessageResult.prase(jsonString);
						int code = messageResult.getCode();
						if(code==0){

							Log.i("0000000", "onSuccess onSuccess onSuccess");

							s++;
							new Thread(new Runnable() {
								@Override
								public void run() {
									//发消息更新dialog的数据
									Message msg = new Message();
									msg.what = 1;
									msg.getData().putInt("uploadcount", s);
									msg.getData().putInt("size", list_image.size());
									handler.sendMessage(msg);
								}
							}
									).start();

							rereshCount(s);

							if(isClicked){
								isClicked=false;
								UploadCancle();

							}else{

								if(s<list_image.size()){
									UploadImage(s);
								}	
							}

						}
					}
					private void UploadCancle() {

						HashMap<String, Object> params = new HashMap<String, Object>();
						params.put("activityId", activityId_Upload);
						params.put("type", "activity");
						params.put("batch_flag", currentTime);
						params.put("userId", User_ID);
						params.put("ios_android_flag", "android");
						params.put("fileNums", String.valueOf(list_image.size()));

						HttpClientUtils.getInstance().post(uploadURL+"removeUploadPicOrVideo", null, params, UploadActivity.this, new AsyncHttpResponseHandler() {
							@Override
							public void onFailure(int arg0,
									Header[] arg1, byte[] arg2,
									Throwable arg3) {
								Toast.makeText(getApplicationContext(), "取消失败", 1).show();

							}
							//
							@Override
							public void onSuccess(int arg0,
									Header[] arg1, byte[] bytes) {
								String jsonString = new String(bytes);
								MessageResult messageResult = MessageResult.prase(jsonString);
								int code = messageResult.getCode();
								if(code==0){

									Toast.makeText(getApplicationContext(), "取消成功", 1).show();
								}		
							}
						});

					}

					private void rereshCount(int count) {

						Log.i("0000", "list_image.size()"+list_image.size()+"count:"+count);
						//			
						if(list_image.size()==count){
							EventBus.getDefault().post(new MyPushEvent("success"));
							button_upload.setText("upload  finish");

							new Handler().postDelayed(new Runnable() {
								@Override
								public void run() {
									MyProgressDialog.stopProgressDiaog();
									UploadActivity.this.finish();
								}
							}, 1000);
							return;
						}
					}
				});			


			}
		});

	}



	protected long getCurrentTime() {
		Calendar mCalendar = Calendar.getInstance();
		long start = mCalendar.getTimeInMillis();
		return start/1000L;
	}




	/**
	 * 调用此方法自动计算指定文件或指定文件夹的大小
	 *
	 * @param filePath
	 *            文件路径
	 * @return 计算好的带B、KB、MB、GB的字符串
	 */
	public static String getAutoFileOrFilesSize(String filePath) {
		File file = new File(filePath);
		long blockSize = 0;
		try {
			if (file.isDirectory()) {
				blockSize = getFileSizes(file);
			} else {
				blockSize = getFileSize(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("获取文件大小", "获取失败!");
		}
		return FormetFileSize(blockSize);
	}


	private static String FormetFileSize(long fileS) {
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		String wrongSize = "0B";
		if (fileS == 0) {
			return wrongSize;
		}
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "KB";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "MB";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "GB";
		}
		return fileSizeString;
	}

	private static long getFileSizes(File f) throws Exception {
		long size = 0;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getFileSizes(flist[i]);
			} else {
				size = size + getFileSize(flist[i]);
			}
		}
		return size;
	}

	private static long getFileSize(File file) throws Exception {
		long size = 0;
		if (file.exists()) {
			FileInputStream fis = null;
			fis = new FileInputStream(file);
			size = fis.available();
		} else {
			//	            file.createNewFile();
			Log.e("获取文件大小", "文件不存在!");
		}
		return size;
	}




	private void loadAdpater(ArrayList<String> paths) {

		Log.e("--", "loadAdpater!");

		if (imagePaths!=null&& imagePaths.size()>0){
			imagePaths.clear();
		}
		if (paths.contains("000000")){
			paths.remove("000000");
		}

		paths.add("000000");

		imagePaths.addAll(paths);
		gridAdapter  = new GridAdapter(imagePaths);
		gridView.setAdapter(gridAdapter);
		try{
			JSONArray obj = new JSONArray(imagePaths);
			Log.e("--", obj.toString());
		}catch (Exception e){
			e.printStackTrace();
		}
	}


	private class GridAdapter extends BaseAdapter {
		private ArrayList<String> listUrls;
		private LayoutInflater inflater;
		public GridAdapter(ArrayList<String> listUrls) {
			this.listUrls = listUrls;
			if(listUrls.size() == 10){
				listUrls.remove(listUrls.size()-1);
			}
			inflater = LayoutInflater.from(UploadActivity.this);
		}

		public int getCount(){
			return  listUrls.size();
		}
		@Override
		public String getItem(int position) {
			return listUrls.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.item_image, parent,false);
				holder.image = (ImageView) convertView.findViewById(R.id.imageView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder)convertView.getTag();
			}

			final String path=listUrls.get(position);

			if (path.equals("000000")){
				holder.image.setImageResource(R.drawable.icon_gridview);

			}else {
				Glide.with(UploadActivity.this)
				.load(path)
				.placeholder(R.drawable.default_error)
				.error(R.drawable.default_error)
				.centerCrop()
				.crossFade()
				.into(holder.image);
			}
			return convertView;
		}

		public class ViewHolder {
			public ImageView image;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		EventBus.getDefault().unregister(this);

	}

	/**
	 * statusbar
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







