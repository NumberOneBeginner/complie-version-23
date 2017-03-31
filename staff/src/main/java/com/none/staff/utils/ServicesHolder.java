package com.none.staff.utils;

import android.util.Log;
import android.util.SparseArray;
/***
 *  服务器api地址的管理类
 *  根据运行的环境和模块，返回不同的api地址
 *  在ServicesHolder中添加一个请求地址的标志，相应在mapmodule中put进去。
 * @author willis
 */
public class ServicesHolder {

	/**相应的功能请求地址在这里定义标志*/

	public static final int UPDATE_USER= 1; //用户登录
	public static final int GAME_START = 2 ; //开始发送声波
	public static final int JOIN_GAME = 3 ;//接收声波并获利
	public static final int END_GAME = 4 ;//老板派利结果
	public static final int SEND_TO_BOSS = 5 ;  //老板请求是否中奖
	public static final int BOSS_SEND_OR_CANCLE_PALISEE = 6;//老板发送或取消准备要派的利是
	public static final int SEND_RESULT_TO_STAFF = 7 ;//员工请求是否中奖
	public static final int PUSHINFO = 8;


	public static int DEBUGE = 0;//测试服务器标志
	public static int RELEASE = 1;//正式发布标志
	public static int environment = 1;
	public static SparseArray<String> env;
	public static SparseArray<String> mapmodule = null;
	public static final String ROOTURL = "http://120.24.55.7:9191" ;
	
	
	static {
		env = new SparseArray<String>();

		//换成自己的服务器地址即可。
//		env.put(RELEASE, "http://120.24.70.17:9090/soundWave") ;  //服务器根地址
//		env.put(RELEASE, "http://192.168.2.222:8080/staff") ; 
		
		env.put(RELEASE, "http://120.24.55.7:9191/staff");
		
		
		mapmodule = new SparseArray<String>();

		/***相应请求地址在这里put进去，配合标志进行*/
		mapmodule.put(UPDATE_USER, "/gameuser/updateUser") ;
		mapmodule.put(GAME_START, "/game/start") ;
		mapmodule.put(JOIN_GAME, "/gameuser/joinGame") ;
		mapmodule.put(END_GAME, "/game/endGame") ;
		mapmodule.put(SEND_TO_BOSS, "/gameuser/sendInfoToBoss") ;
		mapmodule.put(BOSS_SEND_OR_CANCLE_PALISEE, "/gameuser/sendOrCancel") ;
		mapmodule.put(SEND_RESULT_TO_STAFF, "/gameuser/sendResultToStaff") ;
		mapmodule.put(PUSHINFO, "/user/setPushInfo") ;
		
	}

	/*
	 * environment决定返回那个服务器 module决定返回那个模块
	 */
	public static String api(int module) {
		String path;
		//如果有测试服务器把下面打开，则打成发布包的时候自动就把地址换掉了(一定要检查是否打成签名包)
		//		Log.d("BuildConfig.DEBUG", BuildConfig.DEBUG+"");
		//		if(BuildConfig.DEBUG){
		//			path = env.get(DEBUGE) + mapmodule.get(module);	
		//			Log.d("DEBUGE path", path);
		//		}else{
		//			path = env.get(RELEASE) + mapmodule.get(module);
		//			Log.d(" RELEASE path", path);
		//		}

		path = env.get(RELEASE) +mapmodule.get(module);
		Log.d("path", path);
		return path;
	}

}
