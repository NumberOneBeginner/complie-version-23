package com.none.Push.uploadImage.domains.utils;

import java.io.File;
import java.io.IOException;

import com.none.staff.activity.StaffApplication;
import com.none.staff.utils.CommonUtils;
import com.none.staff.utils.FileUtils;

import android.os.Environment;
import android.util.Log;

/**
 * 缓存配置
 * @author willis
 */
public class CacheConfig {
	private static final String TAG = CacheConfig.class.getName();
	private static String mCacheDataDir;

//	public static final int CONFIG_CACHE_MOBILE_TIMEOUT  = 3600000;  //1 hour  在移动 网下缓存1个小时
//	public static final int CONFIG_CACHE_WIFI_TIMEOUT    = 300000;   //5 minute   在wifi下缓存五分钟

	
	
	
	
	/**
	 * 获取缓存信息
	 * 
	 * @param url存放不同文件的标志
	 * @param params
	 * @return
	 */
	public static String getUrlCache(String url) {
		if (url == null) {
			return null;
		}

		
	
		
		
		
		
		
		String result = null;
		File file = new File(StaffApplication.mCacheDataDir + "/" + getCacheDecodeString(url));

		if (file.exists() && file.isFile()) {
			//当前时间送去文件最后修改时间
			long expiredTime = System.currentTimeMillis() - file.lastModified();

			// 1. in case the system time is incorrect (the time is turn back
			// long ago)
			// 2. when the network is invalid, you can only read the cache
			// if (ElinkAppApplication.mNetWorkState != NetworkUtils.NETWORN_NONE
			// && expiredTime < 0) {
			// return null;
			// }
//			if (CharityWalkathonApplication.mNetWorkState == NetworkUtils.NETWORN_WIFI
//					&& expiredTime > CONFIG_CACHE_WIFI_TIMEOUT ) {
//				return null;
//			} else if (CharityWalkathonApplication.mNetWorkState == NetworkUtils.NETWORN_MOBILE
//					&& expiredTime > CONFIG_CACHE_MOBILE_TIMEOUT ) {
//				return null;
//			}
			try {
				Log.d(TAG, "get cache = " + file.getAbsolutePath());
				result = FileUtils.readTextFile(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 设置缓存信息
	 * 
	 * @param data
	 * @param url
	 */
	public static void setUrlCache(String data, String url) {
		if (url == null) {
			return;
		}

		File file = new File(StaffApplication.mCacheDataDir + "/" + getCacheDecodeString(url));
		try {
			// 创建缓存数据到磁盘，就是创建文件
			Log.d(TAG, "set cache = " + file.getAbsolutePath());
			FileUtils.writeTextFile(file, data);
		} catch (IOException e) {
			Log.d(TAG, "write " + file.getAbsolutePath() + " data failed!");
			e.printStackTrace();
		}
	}

	/**
	 * 删除缓存文件
	 * 
	 * @param url
	 */
	public static void deleteCache(String url) {

		if (url == null) {
			return;
		}

		File file = new File(StaffApplication.mCacheDataDir + "/" + getCacheDecodeString(url));

		if (file.exists() && file.isFile()) {
			file.delete();
		}
	}

	/**
	 * 清除所有缓存文件
	 */
	public static void clearCache() {
		File cacheDir = new File(StaffApplication.mCacheDataDir);
		if (cacheDir.exists() && cacheDir.isDirectory()) {
			deleteDir(cacheDir);
		}
	}

	private static void deleteDir(File dir) {
		File[] list = dir.listFiles();
		if (list != null) {
			for (int i = 0; i < list.length; ++i) {
				if (list[i].isDirectory()) {
					deleteDir(list[i]);
				} else {
					list[i].delete();
				}
			}
		}
	}

	private static String getCacheDecodeString(String url) {
		// 1. 处理特殊字符
		// 2. 去除后缀名带来的文件浏览器的视图凌乱(特别是图片更需要如此类似处理，否则有的手机打开图库，全是我们的缓存图片)
		if (url != null) {
			// return url.replaceAll("[.:/,%?&=]", "+").replaceAll("[+]+", "+");
			return CommonUtils.toMD5(url);
		}
		return null;
	}

}
