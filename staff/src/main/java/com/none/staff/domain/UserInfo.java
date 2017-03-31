package com.none.staff.domain;

import com.google.gson.Gson;

public class UserInfo {

	
			
	private String account ;
	private String fullName ;
	private String secureToken ;
	private String location ;
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getSecureToken() {
		return secureToken;
	}
	public void setSecureToken(String secureToken) {
		this.secureToken = secureToken;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	@Override
	public String toString() {
		return "UserInfo [account=" + account + ", fullName=" + fullName
				+ ", secureToken=" + secureToken + ", location=" + location
				+ "]";
	}
	
	public static UserInfo  parse(String jsonString){
		Gson gson = new Gson() ;
		return gson.fromJson(jsonString, UserInfo.class) ;
	}
	
}
