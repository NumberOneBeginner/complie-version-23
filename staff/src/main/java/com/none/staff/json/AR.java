package com.none.staff.json;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.annotations.Expose;

public class AR {
	@Expose
	HashMap<String,String> database;
    @Expose
    ArrayList<VideoInfo> video;

    /**
     * @return the database
     */
    public HashMap<String, String> getDatabase() {
        return database;
    }
    /**
     * @return the video
     */
    public ArrayList<VideoInfo> getVideo() {
        return video;
    }
}
