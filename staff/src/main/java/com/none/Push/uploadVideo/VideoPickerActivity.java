package com.none.Push.uploadVideo;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.none.Push.MyPushEvent;
import com.none.Push.uploadImage.PhotoPreviewActivity;
import com.none.Push.uploadImage.domains.utils.ImageConfig;
import com.none.Push.uploadVideo.domains.FolderVideo;
import com.none.Push.uploadVideo.domains.VideoInfo;
import com.none.Push.uploadVideo.domains.utils.SelectedVideos;
import com.none.Push.uploadVideo.domains.utils.ShowSelectPopupVideo;
import com.none.staff.R;

import de.greenrobot.event.EventBus;

public class VideoPickerActivity extends FragmentActivity{

	public static final String TAG = VideoPickerActivity.class.getName();

	private Context mCxt;

	/** 图片选择模式，int类型 */
	public static final String EXTRA_SELECT_MODE = "select_count_mode";
	/** 单选 */
	public static final int MODE_SINGLE = 0;
	/** 多选 */
	public static final int MODE_MULTI = 1;
	/** 最大图片选择次数，int类型 */
	public static final String EXTRA_SELECT_COUNT = "max_select_count";
	/** 默认最大照片数量 */
	public static final int DEFAULT_MAX_TOTAL= 9;
	/** 是否显示相机，boolean类型 */
	public static final String EXTRA_SHOW_CAMERA = "show_camera";
	/** 默认选择的数据集 */
	public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_result";
	/** 筛选照片配置信息 */
	public static final String EXTRA_IMAGE_CONFIG = "image_config";
	/** 选择结果，返回为 ArrayList&lt;String&gt; 图片路径集合  */
	public static final String EXTRA_RESULT = "select_result";

	public static final String EXTRA_USER_ID = "extra_user_id";
	// 结果数据
	private ArrayList<String> resultList = new ArrayList<String>();
	private ArrayList<VideoInfo> resultLists_select = new ArrayList<VideoInfo>();
	// 文件夹数据
	private ArrayList<FolderVideo> mResultFolder = new ArrayList<FolderVideo>();

	// 不同loader定义
	private static final int LOADER_ALL = 0;
	private static final int LOADER_CATEGORY = 1;

	private MenuItem menuDoneItem;
	private GridView mGridView;
	private View mPopupAnchorView;
	private Button btnAlbum;
	private Button btnPreview;

	// 最大照片数量
	private int mDesireImageCount;
	private ImageConfig imageConfig; // 照片配置

	private VideoGridAdapter mVideoAdapter;
	private FolderVideoAdapter mFolderVideoAdapter;
	private ListPopupWindow mFolderPopupWindow;

	private boolean hasFolderGened = false;
	private boolean mIsShowCamera = false;
	private ShowSelectPopupVideo selectPopup;
	private RelativeLayout title_bar;
	private Button nextbutton;
	private ImageView backImage;
	private TextView main_title_tv;
	private List<VideoInfo> SelectImageSizes;
	private int mPosition;
	private Button bt_dropdown;

	private ImageView arrowdown;

	private ImageView arrowup;

	private String userId;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= 21) {
			full(false);
			 this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		}else{
			//除去状态栏
	        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
	        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
		//根据获取到的设备的NavigationBar的高度为DecorView设置下边距
		getWindow().getDecorView().findViewById(android.R.id.content)
				.setPadding(0, 0, 0, getNavigationBarHeight());
		//为decorview设置背景色
//        getWindow().getDecorView().findViewById(android.R.id.content)
//                .setBackgroundResource(R.color.black);

		setContentView(R.layout.activity_photopicker);
		initViews();
		EventBus.getDefault().register(this);
		IntentFilter filter = new IntentFilter("data.broadcast.action");
		registerReceiver(broadcastReceiver, filter);
		title_bar = (RelativeLayout) this.findViewById(R.id.common_rl) ;
		nextbutton=(Button)this.findViewById(R.id.nextbutton_right) ;
		main_title_tv=(TextView)this.findViewById(R.id.main_title_tv);
		main_title_tv.setText("All Videos");
		backImage=(ImageView)findViewById(R.id.backImage);
		arrowdown=(ImageView)findViewById(R.id.arrowdown);
		arrowup=(ImageView)findViewById(R.id.arrowup);

		// 照片属性
		imageConfig = getIntent().getParcelableExtra(EXTRA_IMAGE_CONFIG);

		// 首次加载所有图片
		getSupportLoaderManager().initLoader(LOADER_ALL, null, mLoaderCallback);

		// 选择图片数量
		mDesireImageCount = getIntent().getIntExtra(EXTRA_SELECT_COUNT, DEFAULT_MAX_TOTAL);

		// 图片选择模式
		final int mode = getIntent().getExtras().getInt(EXTRA_SELECT_MODE, MODE_SINGLE);
		userId=getIntent().getStringExtra(EXTRA_USER_ID);
		// 默认选择
		if(mode == MODE_MULTI) {
			ArrayList<String> tmp = getIntent().getStringArrayListExtra(EXTRA_DEFAULT_SELECTED_LIST);
			if(tmp != null && tmp.size() > 0) {
				resultList.addAll(tmp);
			}
		}

		// 是否显示照相机
		mIsShowCamera = getIntent().getBooleanExtra(EXTRA_SHOW_CAMERA, false);
		mVideoAdapter = new VideoGridAdapter(mCxt, false, getItemImageWidth());
		// 是否显示选择指示器
		mVideoAdapter.showSelectIndicator(mode == MODE_MULTI);
		mGridView.setAdapter(mVideoAdapter);

		backImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		nextbutton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent data = new Intent(VideoPickerActivity.this,UploadVideoActivity.class);
				data.putExtra(EXTRA_RESULT, (Serializable) resultLists_select);
				data.putExtra(VideoPickerActivity.EXTRA_USER_ID ,userId);
				VideoPickerActivity.this.startActivity(data);
			}
		});

		selectPopup = new ShowSelectPopupVideo(this, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

		main_title_tv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				selectPopup.show(title_bar);

				selectPopup.show(title_bar);
				arrowdown.setVisibility(View.VISIBLE);
				arrowup.setVisibility(View.INVISIBLE);

			}
		}) ;

		selectPopup.setItemOnClickListener(new ShowSelectPopupVideo.OnItemOnClickListener() {
			@Override
			public void onItemClick(FolderVideo item, int position) {
				mVideoAdapter.setData(item.videos);
				main_title_tv.setText(item.name);
				arrowdown.setVisibility(View.INVISIBLE);
				arrowup.setVisibility(View.VISIBLE);

			}
		});

		mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
				mPosition=position;
				VideoInfo video = (VideoInfo) adapterView.getAdapter().getItem(position);
				VideoPreviewIntent intent = new VideoPreviewIntent(VideoPickerActivity.this);
				intent.setCurrentItem(position);
				Log.i("0000", "video video video video" + video);
				intent.putExtra("33333",video);
				intent.putExtra("userId", userId);
				intent.putExtra(PhotoPreviewActivity.EXTRA_PHOTOS_ACCOUNT, (Serializable) resultLists_select);
				startActivity(intent);
			}
		});


		mFolderVideoAdapter = new FolderVideoAdapter(mCxt);
	}



	private void initViews(){
		mCxt = this;
		mGridView = (GridView) findViewById(R.id.grid);
		mGridView.setNumColumns(getNumColnums());

		mPopupAnchorView = findViewById(R.id.photo_picker_footer);
		btnAlbum = (Button) findViewById(R.id.btnAlbum);
		btnPreview = (Button) findViewById(R.id.btnPreview);
	}

	public void onSingleImageSelected(String path) {
		Intent data = new Intent();
		resultList.add(path);
		data.putStringArrayListExtra(EXTRA_RESULT, resultList);
		setResult(RESULT_OK, data);
		finish();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.d(TAG, "on change");

		// 重置列数
		mGridView.setNumColumns(getNumColnums());
		// 重置Item宽度
		mVideoAdapter.setItemSize(getItemImageWidth());//
		int screenHeigh = getResources().getDisplayMetrics().heightPixels;
		super.onConfigurationChanged(newConfig);
	}



	//	// LoaderManager的界面

	private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

		private final String[] VIDEO_PROJECTION = {
				MediaStore.Video.Media.DATA,
				MediaStore.Video.Media.TITLE,
				MediaStore.Video.Media._ID,
				MediaStore.Video.Media.ALBUM,
				MediaStore.Video.Media.MIME_TYPE,
				MediaStore.Video.Media.DISPLAY_NAME,
				MediaStore.Video.Media.DATE_ADDED
		};


		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {

			Log.i("111","onCreateLoader");

			if(id == LOADER_ALL) {

				Log.i("111","onCreateLoader LOADER_ALL");

				CursorLoader cursorLoader = new CursorLoader(mCxt,
						MediaStore.Video.Media.EXTERNAL_CONTENT_URI, VIDEO_PROJECTION,
						null, null, VIDEO_PROJECTION[6] + " DESC");

				return cursorLoader;
			}else if(id == LOADER_CATEGORY){

				Log.i("111","onCreateLoader LOADER_CATEGORY");

				CursorLoader cursorLoader = new CursorLoader(mCxt,
						MediaStore.Video.Media.EXTERNAL_CONTENT_URI, VIDEO_PROJECTION,
						null, null,
						VIDEO_PROJECTION[6] + " DESC");
				return cursorLoader;
			}

			return null;
		}


		//此处是得到相册的数据的内容。
		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			Log.i("111","onLoadFinished"+data.toString());
			if (data != null) {
				List<VideoInfo> videos = new ArrayList<VideoInfo>();
				int count = data.getCount();
				if (count > 0) {
					data.moveToFirst();
					do{
						String videoPath = data.getString(data.getColumnIndexOrThrow(VIDEO_PROJECTION[0]));
						String videotitle = data.getString(data.getColumnIndexOrThrow(VIDEO_PROJECTION[1]));
						String albumName = data.getString(data.getColumnIndexOrThrow(VIDEO_PROJECTION[3]));
						String mimeType = data.getString(data.getColumnIndexOrThrow(VIDEO_PROJECTION[4]));
						String displayname = data.getString(data.getColumnIndexOrThrow(VIDEO_PROJECTION[5]));
						String thumbImage = data.getString(data.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA));
						Log.i("www","  thumbImage   "+thumbImage+"   videoPath   "+videoPath+"   videotitle   "+videotitle+"   albumName   "+albumName+"   displayname   "+displayname);
						VideoInfo  video= new VideoInfo(videoPath, videotitle, albumName,mimeType,displayname,thumbImage);
						videos.add(video);

						if( !hasFolderGened ) {
							// 获取文件夹名称

							File imageFile = new File(thumbImage);
							File folderFile = imageFile.getParentFile();
							FolderVideo folder = new FolderVideo();
							folder.name = folderFile.getName();
							folder.path = video.thumbImage;
							folder.videoInfo = video;
							if (!mResultFolder.contains(folder)) {
								List<VideoInfo> videoList = new ArrayList<VideoInfo>();
								videoList.add(video);
								folder.videos = videoList;
								mResultFolder.add(folder);
							} else {
								// 更新
								FolderVideo f = mResultFolder.get(mResultFolder.indexOf(folder));
								f.videos.add(video);
							}
						}

					}while(data.moveToNext());

					mVideoAdapter.setData(videos);

					// 设定默认选择
					if(resultList != null && resultList.size()>0){
						Log.i("6666","   resultList resultList bresultList resultList");
						mVideoAdapter.setDefaultSelected(resultList);
					}
					mFolderVideoAdapter.setData(mResultFolder);
					hasFolderGened = true;
				}
			}
			for(int i=0;i<mResultFolder.size();i++){

				FolderVideo folder = mResultFolder.get(i);

				selectPopup.addAction(folder);
			}
		}
		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
		}
	};

	/**
	 * 获取GridView Item宽度
	 * @return
	 */
	private int getItemImageWidth(){
		int cols = getNumColnums();
		int screenWidth = getResources().getDisplayMetrics().widthPixels;
		int columnSpace = getResources().getDimensionPixelOffset(R.dimen.space_size);
		return (screenWidth - columnSpace * (cols-1)-10) / cols;
	}

	/**
	 * 根据屏幕宽度与密度计算GridView显示的列数， 最少为三列
	 * @return
	 */
	private int getNumColnums(){
		int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
		return cols < 3 ? 3 : cols;
	}

	// 返回已选择的图片数据
	private void complete(){
		Intent data = new Intent();
		data.putStringArrayListExtra(EXTRA_RESULT, resultList);
		setResult(RESULT_OK, data);
		finish();
	}


	public void onEvent(SelectedVideos event) {

		SelectImageSizes=event.getVideos();


		if(SelectImageSizes.size()!=0){

			nextbutton.setEnabled(true);

			Log.i("66666", "nextbutton :" +nextbutton);

		}else{

			nextbutton.setEnabled(false);

		}



		btnPreview.setText((SelectImageSizes.size()) + "/9 " +getResources().getString(R.string.preview));

		resultLists_select.clear();

		for (int i=0;i<SelectImageSizes.size();i++){

			resultLists_select.add(SelectImageSizes.get(i));
		}
	}



	public void onEvent(MyPushEvent event) {

		String success = event.getFlag();


		if(success.equals("success")){

			VideoPickerActivity.this.finish();

		}

	}	







	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			//mContext.unregisterReceiver(this);

			Log.i("aaaa"," onReceive onReceive onReceive onReceive onReceive onReceive onReceive onReceive onReceive onReceive onReceive onReceive");
			// TODO Auto-generated method stub

			ArrayList<VideoInfo> video_list = (ArrayList<VideoInfo>) intent.getSerializableExtra("qqqq");

			Log.i("post", " back back broadcastReceiver broadcastReceiver broadcastReceiver" + video_list.size());

			//            mVideoAdapter.selectList(Images_list,mPosition);
			mVideoAdapter.setDataVideos(video_list);

		}
	};


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(broadcastReceiver);

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
	/**
	 * 动态获取设备NavigationBar的高度
	 * @return
	 */
	public int getNavigationBarHeight() {

		boolean hasMenuKey = ViewConfiguration.get(this).hasPermanentMenuKey();
		boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
		if (!hasMenuKey && !hasBackKey) {
			Resources resources = getResources();
			int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
			//获取NavigationBar的高度
			int height = resources.getDimensionPixelSize(resourceId);
			return height;
		}
		else{
			return 0;
		}
	}
}
