package com.none.staff.activity;

/**
 * 发利动作的接口
 * @author willis
 *
 */
public interface DeliverActionInterFace {

	public void startDeliver() ; //开始派利
	public void endDeliver() ; //结束派利
	public void hideHome() ; //隐藏home键
	public void showHome() ; //显示home键
	public void changeSoundWaveBg(boolean flag) ; //改变背影
	
	public void setResult(String result) ;
}
