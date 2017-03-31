package com.none.staff.HttpsHelper;

/**
 * @author 林楷鹏
 * @description 网络常量
 * @create 2014-9-27下午5:16:09
 * 
 */
public class NetValue {


	/** 默认超时，单位：毫秒 **/
	public static final int DEFAULT_TIMEOUT = 30 * 1000;

	/**
	 * 10**：网络异常码
	 */
	public static final int STATUS_NO_NETWORK = 1000;
	public static final int STATUS_TIMEOUT = 1001;
	public static final int STATUS_UNKNOWN = 1002;

	public static final String TIP_NO_NETWORK = "网络异常，请检查网络配置";
	public static final String TIP_TIMEOUT = "连接超时";
	public static final String TIP_UNKNOWN = "连接出错，请稍后重试";

	//	public static final String TEST_URL = "https://redmine.wizarpos.com/redmine/my/page";//  var url_base = 'http://192.168.2.222:8080/staff/';

	//public static final String Server_URL = "https://120.24.55.7:8449/staff/user/setPushInfo";
	//public static final String Server_URL = "https://120.24.55.7:8447/staff/user/setPushInfo";//UAT

}
