package com.none.staff.util;

import java.io.FileInputStream;
import java.io.InputStream;

public class ZipUtil {


	
	/**
     * 
     * 
     * @param zipFileString
     *  
     * @param outPathString
     *
     * @throws Exception
     */
	public static void UnZipFolder(String zipPathName, String unzipPath) throws Exception {
		InputStream zipFileIs=null;
		java.util.zip.ZipInputStream inZip = null;
		java.io.File file = null;
		java.io.FileOutputStream out = null;
		java.io.File folder = null;
		
		try {
			zipFileIs = new FileInputStream(zipPathName);
			inZip = new java.util.zip.ZipInputStream(zipFileIs);
			java.util.zip.ZipEntry zipEntry;
			String szName = "";

			while ((zipEntry = inZip.getNextEntry()) != null) {
				szName = zipEntry.getName();

				if (zipEntry.isDirectory()) {

					// get the folder name of the widget
					szName = szName.substring(0, szName.length() - 1);
					folder = new java.io.File(unzipPath + java.io.File.separator + szName);
					// LOG.debug("Folder name:"+szName);
					if (!folder.exists()) {
						folder.mkdirs();
					}
				} else {

					file = new java.io.File(unzipPath + java.io.File.separator + szName);
					if (!file.getParentFile().exists()) {						
						file.getParentFile().mkdirs();
					}
					file.createNewFile();
					// get the output stream of the file
					out = new java.io.FileOutputStream(file);
					int len;
					byte[] buffer = new byte[1024];
					// read (len) bytes into buffer
					while ((len = inZip.read(buffer)) != -1) {
						// write (len) byte from buffer at the position 0
						out.write(buffer, 0, len);
						out.flush();
					}
//					out.close();
					IOUtils.close(out);

				}
			}// end of while
//			inZip.close();
			// end of func
		} catch (Exception e) {
			throw e;
		} finally {
			try{
				IOUtils.close(out);
				IOUtils.close(inZip);
				IOUtils.close(zipFileIs);
				
			}catch(Exception e){
				throw e;
			}
		}
	}
	
	public static void UnZipFolder(String zipPathName, String unzipPath,String targetPath) throws Exception {
		InputStream zipFileIs=null;
		java.util.zip.ZipInputStream inZip = null;
		java.io.File file = null;
		java.io.FileOutputStream out = null;
		java.io.File folder = null;
		
		try {
			zipFileIs = new FileInputStream(zipPathName);
			inZip = new java.util.zip.ZipInputStream(zipFileIs);
			java.util.zip.ZipEntry zipEntry;
			String szName = "";

			while ((zipEntry = inZip.getNextEntry()) != null) {
				szName = zipEntry.getName();

				if (zipEntry.isDirectory()) {

					// get the folder name of the widget
					szName = szName.substring(0, szName.length() - 1);
					folder = new java.io.File(targetPath + java.io.File.separator + szName);
					// LOG.debug("Folder name:"+szName);
					if (!folder.exists()) {
						folder.mkdirs();
					}
				} else {

					file = new java.io.File(targetPath + java.io.File.separator + szName);
					if (!file.getParentFile().exists()) {						
						file.getParentFile().mkdirs();
					}
					file.createNewFile();
					// get the output stream of the file
					out = new java.io.FileOutputStream(file);
					int len;
					byte[] buffer = new byte[1024];
					// read (len) bytes into buffer
					while ((len = inZip.read(buffer)) != -1) {
						// write (len) byte from buffer at the position 0
						out.write(buffer, 0, len);
						out.flush();
					}
					IOUtils.close(out);

				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try{
				IOUtils.close(out);
				IOUtils.close(inZip);
				IOUtils.close(zipFileIs);
			}catch(Exception e){
				throw e;
			}
		}
	}

}
