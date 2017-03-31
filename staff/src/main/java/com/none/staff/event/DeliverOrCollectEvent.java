package com.none.staff.event;

public class DeliverOrCollectEvent {

	private String flag ;
	
	public DeliverOrCollectEvent(String flag) {
		this.flag = flag ;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}
	
	
}
