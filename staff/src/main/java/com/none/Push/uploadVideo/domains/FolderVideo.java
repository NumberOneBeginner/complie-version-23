package com.none.Push.uploadVideo.domains;

import java.util.List;

public class FolderVideo {

	public String name;
	public String path;
	public VideoInfo videoInfo;
	public List<VideoInfo> videos;


	@Override
	public boolean equals(Object o) {
		try {
			FolderVideo other = (FolderVideo) o;
			return this.name.equalsIgnoreCase(other.name);
		}catch (ClassCastException e){
			e.printStackTrace();
		}
		return super.equals(o);
	}


}
