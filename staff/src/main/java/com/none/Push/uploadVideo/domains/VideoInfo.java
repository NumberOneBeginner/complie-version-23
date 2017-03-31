package com.none.Push.uploadVideo.domains;

import java.io.Serializable;

public class VideoInfo implements Serializable{
	
	 public   String videoPath;
	    public   String title;
	    public   String albumName;
	    public   String mimeType;
	    public   String displayname;
	    public   String thumbImage;

	    public VideoInfo(String videoPath, String title, String albumName ,String mimeType, String displayname,String thumbImage){
	        this.videoPath = videoPath;
	        this.title = title;
	        this.albumName = albumName;
	        this.mimeType = mimeType;
	        this.displayname = displayname;
	        this.thumbImage = thumbImage;
	    }

	    @Override
	    public boolean equals(Object o) {
	        try {
	            VideoInfo other = (VideoInfo) o;
	            return this.thumbImage.equalsIgnoreCase(other.thumbImage);
	        }catch (ClassCastException e){
	            e.printStackTrace();
	        }
	        return super.equals(o);
	    }
	
	
	
}
