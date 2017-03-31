package com.none.staff.json;

import com.google.gson.annotations.Expose;

public class ZipInfo {
	@Expose
	int size;
	@Expose
	String hashcode;
	@Expose
	String zipurl;
    public String getZipurl() {
        return zipurl;
    }
    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }
    /**
     * @return the hashcode
     */
    public String getHashcode() {
        return hashcode;
    }
	
}
