package com.none.Push.uploadVideo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.none.Push.MyPushEvent;
import com.none.Push.uploadImage.PhotoPickerActivity;
import com.none.Push.uploadImage.domains.utils.AddPopWindow;
import com.none.Push.uploadImage.domains.utils.AddPopWindow.OnItemOnClickListener;
import com.none.Push.uploadImage.domains.utils.HttpClientUtils;
import com.none.Push.uploadImage.domains.utils.MessageResult;
import com.none.Push.uploadImage.domains.utils.SpinerAdapter;
import com.none.Push.uploadVideo.domains.VideoInfo;
import com.none.staff.R;
import com.none.staff.activity.BaseActivity;
import com.none.staff.utils.NetworkUtils;
import com.none.staff.utils.SPUtil;

import de.greenrobot.event.EventBus;

public class UploadVideoActivity  extends BaseActivity implements SpinerAdapter.IOnItemSelectListener{

	private GridView gridView;
	private GridAdapter gridAdapter;
	private ArrayList<String> list;
	private ArrayList<String> imagePaths = new ArrayList<String>();
	private Button button_upload;
	private ArrayList<VideoInfo> list_video;
	protected Uri uri;
	protected File scaledFile;
	private List<String>  mListType = new ArrayList<String>(); 
	private TextView mTView;
	private ImageView activity_back_regist;
	protected String [] activity_id;
	protected String []activity_subject;
	private String UserId;
	private TextView spinner_et;
	private String Activity_Id="";
	protected AlertDialog selectTestUserDialog;
	private EditText remark_upload;
	protected JSONArray activityList;
	protected String activityId_Upload;
	private boolean isClicked=false;
	protected long currentTime;
	protected String remark;
	private ImageView image_spinner;
	private ImageView image_spinner_up;
	protected JSONArray array;
	protected AddPopWindow addPopwindow;
	public static Handler handler = new Handler();
	private String uploadURL;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.uploadlayout);
		uploadURL=getResources().getString(R.string.uploadVideo_url);
		setViews();
		checkListSpinner("first");
		setListener();

	}

	private void setListener() {

		image_spinner.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if(NetworkUtils.isNetworkAvailable(UploadVideoActivity.this)){
					if(activity_subject==null||activity_subject.length==0){
						String activity_subject_string = SPUtil.getValue(UploadVideoActivity.this, SPUtil.DISPLAY_ACTIVITY);
						if(activity_subject_string==null||activity_subject_string.length()==0){
							checkListSpinner("network");
						}else{

							try {
								array = new JSONArray(activity_subject_string);
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
						String activity_subject_string = SPUtil.getValue(UploadVideoActivity.this, SPUtil.DISPLAY_ACTIVITY);
						try {
							array = new JSONArray(activity_subject_string);
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

				addPopwindow = new AddPopWindow(UploadVideoActivity.this,activity_subject);
				addPopwindow.showPopwindowdhalf(spinner_et,60,300);  

				image_spinner.setVisibility(View.GONE);
				image_spinner_up.setVisibility(View.VISIBLE);

				addPopwindow.setItemOnClickListener(new OnItemOnClickListener() {

					@Override
					public void onItemClick(int position) {
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

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {

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

			public int s=0;
			public	int index=0;
			@Override
			public void onClick(View arg0) {

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

							Log.i("Sputil", "activityId_Upload :" +activityId_Upload);
						}
					}
				}	

				if(activityId_Upload==null||activityId_Upload.equals("")){
					selectDialog("Please select Activity!");
					return;
				}

				float sumNewSize=0;

				for (int i=0 ;i<list_video.size();i++){

					File imageFile = new File(list_video.get(i).thumbImage);
					//压缩图片
					float newSize = (float)imageFile.length()/(1024*1024);
					sumNewSize=sumNewSize+newSize;
					long lentght = imageFile.length();

				}


				if(sumNewSize>100){

					selectDialog("视频不能大于100M,请重新上传");	
					return;
				}

				UploadImage(index);
			}

			private void UploadImage( int index) {

				HashMap<String, Object> params = new HashMap<String, Object>();
				params.put("activityId",activityId_Upload);
				params.put("type", "activity");
				params.put("remark", remark);
				params.put("batch_flag", currentTime);
				params.put("userId", UserId);
				params.put("fileNo", "0"+(index+1));
				params.put("actiontype", 3);
				params.put("fileNums", String.valueOf(list_video.size()));
				params.put("multipartFile", new File(list_video.get(index).thumbImage));

				HttpClientUtils.getInstance().post(uploadURL+"saveUploadPicOrVideoSingle_android", null, params, UploadVideoActivity.this, new AsyncHttpResponseHandler() {

					private MyProgressDialogVideo Progress;

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {

						Log.i("code", "onFailure");

						MyProgressDialogVideo.stopProgressDiaog();
						Toast.makeText(UploadVideoActivity.this, "联网失败，上传失败", 1).show();
					}

					@Override
					public void onProgress(int bytesWritten, int totalSize) {
						// TODO Auto-generated method stub
						super.onProgress(bytesWritten, totalSize);

						Progress.dialog_cancel.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {

								isClicked=true;
								Progress.stopProgressDiaog();
							}
						});

					}

					@Override
					public void onStart() {

						super.onStart();

						Progress=MyProgressDialogVideo.showProgressShow(UploadVideoActivity.this, "upload");
						MyProgressDialogVideo.dialog_cancel.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {

								isClicked=true;
								Progress.stopProgressDiaog();

							}
						});
					}


					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] bytes) {
						String jsonString = new String(bytes);
						MessageResult messageResult = MessageResult.prase(jsonString);
						int code = messageResult.getCode();

						Log.i("code", "suceess code"+code);

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
									msg.getData().putInt("size", list_video.size());
									handler.sendMessage(msg);
								}
							}
									).start();

							rereshCount(s);

							//如果我点击取消的话，就调用客户remove的API，如果不点击取消就继续上传

							if(isClicked){

								isClicked=false;
								UploadCancle();
							}else{

								if(s<list_video.size()){

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
						params.put("userId", UserId);
						params.put("ios_android_flag", "android");
						params.put("fileNums", String.valueOf(list_video.size()));

						HttpClientUtils.getInstance().post(uploadURL+"removeUploadPicOrVideo", null, params, UploadVideoActivity.this, new AsyncHttpResponseHandler() {
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
									MyProgressDialogVideo.stopProgressDiaog();	
									Toast.makeText(getApplicationContext(), "取消成功", 1).show();

								}		
							}
						});

					}

					private void rereshCount(int count) {

						if(list_video.size()==count){
							button_upload.setText("upload  finish");
							new Handler().postDelayed(new Runnable() {
								@Override
								public void run() {
									MyProgressDialogVideo.stopProgressDiaog();
									EventBus.getDefault().post(new MyPushEvent("success"));
									UploadVideoActivity.this.finish();
								}
							}, 1000);
							return;
						}
					}
				});			
			}

			private long getCurrentTime() {
				Calendar mCalendar = Calendar.getInstance();
				long start = mCalendar.getTimeInMillis();
				return start/1000L;
			}
		});
	}




	protected void selectDialog(String title) {

		AlertDialog.Builder builder = new AlertDialog.Builder(UploadVideoActivity.this) ;
		builder.setTitle(title);
		builder.setNegativeButton("Cancel", null);
		builder.create().show() ;

	}


	private void setViews() {

		remark_upload=(EditText)findViewById(R.id.remark_upload);
		spinner_et=(TextView)findViewById(R.id.spinner_et);
		image_spinner=(ImageView)findViewById(R.id.image_spinner);
		image_spinner_up=(ImageView)findViewById(R.id.image_spinner_up);
		UserId=getIntent().getStringExtra(VideoPickerActivity.EXTRA_USER_ID);
		list_video = (ArrayList<VideoInfo>) getIntent().getSerializableExtra(PhotoPickerActivity.EXTRA_RESULT);
		list = new ArrayList<String>();

		for (int i = 0; i < list_video.size(); i++) {
			list.add(list_video.get(i).thumbImage);
		}

		activity_back_regist=(ImageView)findViewById(R.id.activity_back_regist);
		button_upload = (Button) findViewById(R.id.button_upload);
		gridView = (GridView) findViewById(R.id.gridView_upload);
		int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
		cols = cols < 3 ? 3 : cols;
		gridView.setNumColumns(cols);
		loadAdpater(list);

	}



	private void checkListSpinner(final String str) {

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("userId", UserId);
		HttpClientUtils.getInstance().post(uploadURL+"getActivityList", null, params, UploadVideoActivity.this, new AsyncHttpResponseHandler() {

			private String[] activity_subjectStore;
			private String[] activity_idStore;

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
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
						activity_idStore=new String[activityList.length()];
						activity_subjectStore=new String[activityList.length()];
						for(int i=0;i<activityList.length();i++){
							JSONObject arrray_string=(JSONObject)activityList.get(i);
							activity_idStore[i]=arrray_string.getString("id");
							activity_subjectStore[i]=arrray_string.getString("subject");
							SPUtil.putValue(getApplicationContext(), activity_subjectStore[i], activity_idStore[i]);
							Log.i("SPUtil", "Sputil..putvalue" + "activity_idStore"+activity_idStore[i]);
						}

						if(activityList!=null){
							SPUtil.putValue(UploadVideoActivity.this, SPUtil.DISPLAY_ACTIVITY, activityList.toString());
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}

					if(str.equals("network")){
						addPopwindow = new AddPopWindow(UploadVideoActivity.this,activity_subjectStore);	
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
			Log.e("获取文件大小", "文件不存在!");
		}
		return size;
	}
	/**
	 * 将文件转换为String
	 *
	 * @param path
	 * @return
	 */
	public static String base64String(String path) {

		for(int i=0;i<58;i++) {

			return base64StringBySplit(path, 58,i);
		}
		return null;
	}

	/**
	 * 获取文件指定size分割的指定index对应的String
	 *
	 * @param path
	 * @param size  分割份数
	 * @param index 索引
	 * @return
	 */
	public static String base64StringBySplit(String path, int size, int index) {
		File f = new File(path);
		int length = (int) f.length();
		int part = (length + (size - 1)) / size;
		int start = index * part;
		int len;
		if (index == size - 1) {
			len = length - index * part;
		} else {
			len = part;
		}
		return base64StringInRange(path, start, start + len);
	}

	/**
	 * 获取文件位置区间上的String
	 *
	 * @param path
	 * @param start 开始位置
	 * @param end   结束位置
	 * @return
	 */
	public static String base64StringInRange(String path, int start, int end) {
		File f = new File(path);
		try {
			RandomAccessFile raf = new RandomAccessFile(f, "r");
			raf.seek(start);
			int len = end - start;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] bts = new byte[1024 * 10];
			int readLen;
			while ((readLen = raf.read(bts)) > 0) {
				baos.write(bts, 0, readLen);
				baos.flush();
				if (baos.size() >= len) {
					break;
				}
			}
			bts = baos.toByteArray();
			String base64String = Base64.encodeToString(bts, 0, len, Base64.DEFAULT);
			baos.close();
			raf.close();
			return base64String;
		} catch (Exception e) {
			Log.e("FileUtils", "base64StringInRange" + e);
		}
		return "";
	}




	private void loadAdpater(ArrayList<String> paths) {


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
			inflater = LayoutInflater.from(UploadVideoActivity.this);
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
				Glide.with(UploadVideoActivity.this)
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
	public void onItemClick(int pos) {

		if (pos >= 0 && pos <= mListType.size()){
			String value = mListType.get(pos);
			mTView.setText(value.toString());
		}
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
