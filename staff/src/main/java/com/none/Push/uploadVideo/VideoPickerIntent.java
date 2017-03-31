package com.none.Push.uploadVideo;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;

import com.none.Push.uploadImage.PhotoPickerActivity;
import com.none.Push.uploadImage.domains.utils.ImageConfig;
import com.none.Push.uploadImage.domains.utils.SelectModel;



public class VideoPickerIntent  extends Intent{

	public VideoPickerIntent(Context packageContext) {
		super(packageContext, VideoPickerActivity.class);
	}

	public void setShowCarema(boolean bool){
		this.putExtra(VideoPickerActivity.EXTRA_SHOW_CAMERA, bool);
	}

	public void setMaxTotal(int total){
		this.putExtra(VideoPickerActivity.EXTRA_SELECT_COUNT, total);
	}



	public void setUserId(String user_id){
		this.putExtra(VideoPickerActivity.EXTRA_USER_ID, user_id);
	}



	/**
	 * 选择
	 * @param model
	 */
	public void setSelectModel(SelectModel model){
		this.putExtra(VideoPickerActivity.EXTRA_SELECT_MODE, Integer.parseInt(model.toString()));
	}

	/**
	 * 已选择的照片地址
	 * @param imagePathis
	 */
	public void setSelectedPaths(ArrayList<String> imagePathis){
		this.putStringArrayListExtra(VideoPickerActivity.EXTRA_DEFAULT_SELECTED_LIST, imagePathis);
	}

	/**
	 * 显示相册图片的属性
	 * @param config
	 */
	public void setImageConfig(ImageConfig config){
		this.putExtra(VideoPickerActivity.EXTRA_IMAGE_CONFIG, config);
	}


}
