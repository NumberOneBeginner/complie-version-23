package com.none.staff.network;

/**
 * 检测网络有无的事件监听器
 * @author willis
 *
 */
public interface CheckNetWorkListener {

	/**有网络时调用的方法*/
	public void hasNetWorkEvent() ;
	/**无网络时调用的方法*/
	public void noNetWorkEvent() ;
}
