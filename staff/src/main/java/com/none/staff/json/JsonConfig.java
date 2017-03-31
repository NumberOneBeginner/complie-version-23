package com.none.staff.json;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;

public class JsonConfig {
	@Expose
	ArrayList<ZipInfo> webresources;
    @Expose
    String Version;
    @Expose
    String AppUpgradeLink;
    @Expose
    ArrayList<AR> AR;
    /**
     * @return the aR
     */
    public ArrayList<AR> getAR() {
        return AR;
    }
    public ArrayList<ZipInfo> getWebRresources() {
        return webresources;
    }	
	public String getVersion(){
	    return Version;
	}
	public String getAppUpgradeLink(){
        return AppUpgradeLink;
    }
}
