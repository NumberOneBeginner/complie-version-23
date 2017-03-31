package com.none.Push.uploadVideo;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
public class VideoPreviewIntent extends Intent {



	 public VideoPreviewIntent(Context packageContext) {
	        super(packageContext, VideoPreviewActivity.class);
	    }

	    /**
	     * 照片地址
	     * @param paths
	     */
	    public void setPhotoPaths(ArrayList<String> paths){
	        this.putStringArrayListExtra(VideoPreviewActivity.EXTRA_PHOTOS, paths);
	    }

	    /**
	     * 当前照片的下标
	     * @param currentItem
	     */
	    public void setCurrentItem(int currentItem){
	        this.putExtra(VideoPreviewActivity.EXTRA_CURRENT_ITEM, currentItem);
	    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
