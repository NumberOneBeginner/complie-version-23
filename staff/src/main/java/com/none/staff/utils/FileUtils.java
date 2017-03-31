package com.none.staff.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 文件 工具类
 * @author willis
 */
public class FileUtils {

	
	
	

	/**
	 * 写入一个文本文件
	 * 
	 * @param file
	 * @param data
	 * @throws IOException
	 */
	public static void writeTextFile(File file, String data) throws IOException {
		try {
			if (data != null) {
				FileOutputStream fout = new FileOutputStream(file);
				fout.write(data.getBytes());
				fout.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	

	/**
	 * 读取一个文本文件的内容
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String readTextFile(File file) throws IOException {

		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(file), "UTF-8"));
		StringBuffer sb = new StringBuffer();
		String line;
		if (bufferedReader != null) {
			while ((line = bufferedReader.readLine()) != null) {
				sb.append(line);
			}
			bufferedReader.close();
		}
		return sb.toString();
	}
	
	
	public static boolean  deleteFile(File file){
    	return file.delete(); 
    }
	
	
	
	/** 
     * 删除此路径名表示的文件或目录。 
     * 如果此路径名表示一个目录，则会先删除目录下的内容再将目录删除，所以该操作不是原子性的。 
     * 如果目录中还有目录，则会引发递归动作。 
     * @param filePath 
     *            要删除文件或目录的路径。 
     * @return 当且仅当成功删除文件或目录时，返回 true；否则返回 false。 
     */  
    public static boolean deleteDir(String filePath) {  
        File file = new File(filePath);  
        return deleteFile(file);  
    }  
      
    private static boolean deleteFiles(File file){  
        File[] files = file.listFiles();  
        for(File deleteFile : files){  
            if(deleteFile.isDirectory()){  
                //如果是文件夹，则递归删除下面的文件后再删除该文件夹  
                if(!deleteFile(deleteFile)){  
                    //如果失败则返回  
                    return false;  
                }  
            } else {  
                if(!deleteFile.delete()){  
                    //如果失败则返回  
                    return false;  
                }  
            }  
        }  
        return file.delete();  
    }  
    /**
     * 把输入流转化成字符串
     * @param is
     * @return
     * @throws Exception
     */
	public static String convertStreamToString(InputStream is) throws Exception {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();
	    String line = null;
	    while ((line = reader.readLine()) != null) {
	      sb.append(line).append("\n");
	    }
	    reader.close();
	    return sb.toString();
	}
    
}
