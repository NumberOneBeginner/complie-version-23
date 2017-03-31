package com.none.staff.uploadIcon;

import java.io.File;
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
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.none.Push.MyPushEvent;
import com.none.Push.uploadImage.FolderAdapter;
import com.none.Push.uploadImage.domains.Folder;
import com.none.Push.uploadImage.domains.Image;
import com.none.Push.uploadImage.domains.utils.ImageConfig;
import com.none.Push.uploadImage.domains.utils.SelectedImages;
import com.none.Push.uploadImage.domains.utils.ShowSelectPopup;
import com.none.staff.R;

import de.greenrobot.event.EventBus;

public class IconUpLoadPickerActivity extends FragmentActivity {
	public static final String TAG = IconUpLoadPickerActivity.class.getName();

	private Context mCxt;

	/** 图片选择模式，int类型 */
//	public static final String EXTRA_SELECT_MODE = "select_count_mode";
	/** 单选 */
//	public static final int MODE_SINGLE = 0;
	/** 多选 */
//	public static final int MODE_MULTI = 1;
	/** 最大图片选择次数，int类型 */
//	public static final String EXTRA_SELECT_COUNT = "max_select_count";
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
	/** 最大图片选择次数，int类型 */
//	public static final String EXTRA_USER_ID = "extra_user_id";
	// 结果数据
	private ArrayList<String> resultList = new ArrayList<String>();
	private ArrayList<Image> resultLists_select = new ArrayList<Image>();
	// 文件夹数据
	private ArrayList<Folder> mResultFolder = new ArrayList<Folder>();

	// 不同loader定义
	private static final int LOADER_ALL = 0;
	private static final int LOADER_CATEGORY = 1;

//	private MenuItem menuDoneItem;
	private GridView mGridView;
//	private View mPopupAnchorView;
//	private Button btnAlbum;
//	private Button btnPreview;

	private ImageConfig imageConfig; // 照片配置
	private IconPickerAdapter mIconPickerAdapter;
	private FolderAdapter mFolderAdapter;
//	private ListPopupWindow mFolderPopupWindow;

	private boolean hasFolderGened = false;
	private boolean mIsShowCamera = false;
	private ShowSelectPopup selectPopup;
	private RelativeLayout title_bar;
//	private Button nextbutton;
	private ImageView backImage;
	private TextView main_title_tv;
	private List<Image> SelectImageSizes;
	private int mPosition;

	private ImageView arrowdown;

	private ImageView arrowup;

	private String userId;

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
		setContentView(R.layout.activity_icon_picker);
		
		IntentFilter filter = new IntentFilter("data.broadcast.action");
		registerReceiver(broadcastReceiver, filter);
		Glide.get(this).clearMemory();//清楚缓存
		initViews();
		EventBus.getDefault().register(this);
		title_bar = (RelativeLayout) this.findViewById(R.id.icon_rl) ;
//		nextbutton=(Button)this.findViewById(R.id.nextbutton_right) ;
		arrowdown=(ImageView)findViewById(R.id.icon_arrowdown);
		arrowup=(ImageView)findViewById(R.id.icon_arrowup);
		main_title_tv=(TextView)this.findViewById(R.id.icon_title_tv);
		backImage=(ImageView)findViewById(R.id.backIcon);
		// 照片属性
		imageConfig = getIntent().getParcelableExtra(EXTRA_IMAGE_CONFIG);
		// 首次加载所有图片
		getSupportLoaderManager().initLoader(LOADER_ALL, null, mLoaderCallback);
		// 选择图片数量
		userId=getIntent().getStringExtra("userId");

		// 默认选择

		ArrayList<String> tmp = getIntent().getStringArrayListExtra(EXTRA_DEFAULT_SELECTED_LIST);
		if(tmp != null && tmp.size() > 0) {
			resultList.addAll(tmp);
		}
		// 是否显示照相机
		mIsShowCamera = getIntent().getBooleanExtra(EXTRA_SHOW_CAMERA, false);
		mIconPickerAdapter = new IconPickerAdapter(mCxt, false, getItemImageWidth());
		mGridView.setAdapter(mIconPickerAdapter);
		backImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		selectPopup = new ShowSelectPopup(this, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		main_title_tv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				selectPopup.show(title_bar);
				arrowdown.setVisibility(View.VISIBLE);
				arrowup.setVisibility(View.INVISIBLE);
			}
		}) ;


		selectPopup.setItemOnClickListener(new ShowSelectPopup.OnItemOnClickListener() {
			@Override
			public void onItemClick(Folder item, int position) {
				mIconPickerAdapter.setData(item.images);
				main_title_tv.setText(item.name);
				arrowdown.setVisibility(View.INVISIBLE);
				arrowup.setVisibility(View.VISIBLE);
			}
		});


		mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override

			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
				mPosition=position;
				Image image = (Image) adapterView.getAdapter().getItem(position);
				Log.e("iamgepath", "mag"+image.path);
				Intent intent = new Intent(IconUpLoadPickerActivity.this,IconPreviewActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("image", image);
				bundle.putString("userId", userId);
				intent.putExtras(bundle);
				startActivityForResult(intent,1);
			}
		});


		mFolderAdapter = new FolderAdapter(mCxt);

	}



	@Override
	protected void onDestroy() {
		super.onDestroy();

		unregisterReceiver(broadcastReceiver);
	}



	private void initViews(){
		mCxt = this;
		mGridView = (GridView) findViewById(R.id.grid_icon);
		mGridView.setNumColumns(getNumColnums());
//		mPopupAnchorView = findViewById(R.id.icon_picker_footer);
//		btnAlbum = (Button) findViewById(R.id.btn_icon_Album);
//		btnPreview = (Button) findViewById(R.id.btn_icon_Preview);
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
		mIconPickerAdapter.setItemSize(getItemImageWidth());
		int screenHeigh = getResources().getDisplayMetrics().heightPixels;

		super.onConfigurationChanged(newConfig);
	}



	// LoaderManager的界面

	private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {
	
		private final String[] IMAGE_PROJECTION = {
				MediaStore.Images.Media.DATA,
				MediaStore.Images.Media.DISPLAY_NAME,
				MediaStore.Images.Media.DATE_ADDED,
				MediaStore.Images.Media._ID };
		
		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {

			// 根据图片设置参数新增验证条件
			StringBuilder selectionArgs = new StringBuilder();

			if(imageConfig != null){
				if(imageConfig.minWidth != 0){
					selectionArgs.append(MediaStore.Images.Media.WIDTH + " >= " + imageConfig.minWidth);
				}

				if(imageConfig.minHeight != 0){
					selectionArgs.append("".equals(selectionArgs.toString()) ? "" : " and ");
					selectionArgs.append(MediaStore.Images.Media.HEIGHT + " >= " + imageConfig.minHeight);
				}

				if(imageConfig.minSize != 0f){
					selectionArgs.append("".equals(selectionArgs.toString()) ? "" : " and ");
					selectionArgs.append(MediaStore.Images.Media.SIZE + " >= " + imageConfig.minSize);
				}

				if(imageConfig.mimeType != null){
					selectionArgs.append(" and (");
					for(int i = 0, len = imageConfig.mimeType.length; i < len; i++){
						if(i != 0){
							selectionArgs.append(" or ");
						}
						selectionArgs.append(MediaStore.Images.Media.MIME_TYPE + " = '" + imageConfig.mimeType[i] + "'");
					}
					selectionArgs.append(")");
				}
			}

			if(id == LOADER_ALL) {
				CursorLoader cursorLoader = new CursorLoader(mCxt,
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
						selectionArgs.toString(), null, IMAGE_PROJECTION[2] + " DESC");
				
				return cursorLoader;
			}else if(id == LOADER_CATEGORY){
				String selectionStr = selectionArgs.toString();
				if(!"".equals(selectionStr)){
					selectionStr += " and" + selectionStr;
				}
				CursorLoader cursorLoader = new CursorLoader(mCxt,
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
						IMAGE_PROJECTION[0] + " like '%" + args.getString("path") + "%'" + selectionStr, null,
						IMAGE_PROJECTION[2] + " DESC");
			
				return cursorLoader;
			}
		
			return null;
		}


		//此处是得到相册的数据的内容。
		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			if (data != null) {
				List<Image> images = new ArrayList<Image>();//所有的图片
				int count = data.getCount();
				if (count > 0) {
					data.moveToFirst();
					do{
						String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
						String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
						long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));

						Image image = new Image(path, name, dateTime);
						images.add(image);
						if( !hasFolderGened ) {//如果这个文件夹不存在，将图片添加进去。
							// 获取文件夹名称
							File imageFile = new File(path);
							File folderFile = imageFile.getParentFile();
							Folder folder = new Folder();
							folder.name = folderFile.getName();
							folder.path = folderFile.getAbsolutePath();
							folder.cover = image;

						
							if (!mResultFolder.contains(folder)) {
								//								
								List<Image> imageList = new ArrayList<Image>();//每次都会清空这个集合，每个文件夹中的图片
								imageList.add(image);
								folder.images = imageList;
								mResultFolder.add(folder);
							} else {
								// 更新
								Folder f = mResultFolder.get(mResultFolder.indexOf(folder));
								f.images.add(image);
							}
						}

					}while(data.moveToNext());

					mIconPickerAdapter.setData(images);

					// 设定默认选择
					if(resultList != null && resultList.size()>0){
						mIconPickerAdapter.setDefaultSelected(resultList);
					}

					mFolderAdapter.setData(mResultFolder);
					hasFolderGened = true;//加载完了之后不需要重新加载一次
				}
			}

			for(int i=0;i<mResultFolder.size();i++){

				Folder folder = mResultFolder.get(i);

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
		return ((screenWidth - columnSpace * (cols-1))-10) / cols;
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

	public void onEvent(SelectedImages event) {

		SelectImageSizes=event.getImages();

		if(SelectImageSizes.size()!=0){

//			nextbutton.setEnabled(true);
		}else{

//			nextbutton.setEnabled(false);

		}



//		btnPreview.setText((SelectImageSizes.size()) + "/9 "+getResources().getString(R.string.preview));

		resultLists_select.clear();

		for (int i=0;i<SelectImageSizes.size();i++){

			resultLists_select.add(SelectImageSizes.get(i));
		}
	}

	public void onEvent(MyPushEvent event) {

		String success = event.getFlag();

		if(success.equals("success")){

			IconUpLoadPickerActivity.this.finish();
		}
	}

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			ArrayList<Image> Images_list = (ArrayList<Image>) intent.getSerializableExtra("qqqq");

			mIconPickerAdapter.setDataImage(Images_list);

		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			final Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(data!=null){
			if(requestCode==1){	
				String path = data.getStringExtra("bitmap");
				Intent i = new Intent();
				i.putExtra("reciverbitmap", path);
				setResult(3,i);
				finish();
			}	
		}else{
			finish();
		}
		
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
