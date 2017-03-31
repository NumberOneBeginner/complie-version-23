package com.hsbc.greenpacket.util;

import java.io.File;

public class FileUtil {
	/**
	 * delete single file
	 * 
	 * @param fileName
	 *            
	 * @return success return true,otherwise return false
	 */
	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		if (file.isFile() && file.exists()) {
			file.delete();			
			return true;
		} else {			
			return false;
		}
	}

	/**
	 * delete folder and files
	 * 
	 * @param dir
	 *            
	 * @return success return true,otherwise return false
	 */
	public static boolean deleteDirectory(String dir) {		
		//in activity class ,add this code to get path: 
		//this.getFilesDir().getParent()+ File.separator+"files" or "shared_prefs"
		if (!dir.endsWith(File.separator)) {
			dir = dir + File.separator;
		}
		File dirFile = new File(dir);		
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			//System.out.println("fail to delete single file" + dir + " It is not exist");
			return false;
		}
		boolean flag = true;
		
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag) {
					break;
				}
			}
			
			else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag) {
					break;
				}
			}
		}

		if (!flag) {
			//System.out.println("fail to delete folder");
			return false;
		}
		
		if (dirFile.delete()) {
			//System.out.println("delete folder " + dir + " successfully");
			return true;
		} else {
			//System.out.println("delete folder " + dir + " fail");
			return false;
		}
	}
}
