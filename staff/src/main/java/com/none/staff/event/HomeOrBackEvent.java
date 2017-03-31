package com.none.staff.event;

/**
 * 返回建或Home键的事件类
 * @author willis
 *
 */
public class HomeOrBackEvent {

	//标志按的是返回建还是home建
	private String flag ;
	
	public HomeOrBackEvent(String flag) {
		this.flag = flag ;
	}

	public String getFlag() {
		return flag;
	}

	
}
