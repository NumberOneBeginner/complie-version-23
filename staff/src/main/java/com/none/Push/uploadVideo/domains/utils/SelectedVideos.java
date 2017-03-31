package com.none.Push.uploadVideo.domains.utils;

import java.util.List;

import com.none.Push.uploadVideo.domains.VideoInfo;
public class SelectedVideos {

	private List<VideoInfo> videos;

	public SelectedVideos(List<VideoInfo> size){

		this.videos = size;
	}
	public List<VideoInfo> getVideos(){

		return videos;
	}
}
