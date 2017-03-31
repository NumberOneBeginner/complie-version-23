package com.none.Push.uploadImage.domains.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import android.net.Uri;
import android.os.Environment;

public class PhotoUtil {

	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;


	/**
	 * 一切都操作uri
	 * @return
	 * 
	 */
	public static String getSDPath(String folderName) {

		String path = Environment.getExternalStorageDirectory()+ File.separator+folderName;
		File dumpFolder = new File(Environment.getExternalStorageDirectory(),folderName);

		if (!dumpFolder.exists()) {
			boolean ret = dumpFolder.mkdir();
		}
		return path;
	}


	public static Uri createImageFile() throws IOException{
		// Create an image file name
		//		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		//		String imageFileName = "JPEG_" + timeStamp + "_";
		//		File storageDir = Environment.getExternalStoragePublicDirectory(
		//				Environment.DIRECTORY_PICTURES);


		String imageFileName = getSDPath("00000");

		File image = null;
		image=new File(imageFileName);

		// Save a file: path for use with ACTION_VIEW intents
		return Uri.fromFile(image);
	}
	public static void copyFileUsingFileChannels(File source, File dest){
		FileChannel inputChannel = null;
		FileChannel outputChannel = null;
		try {
			try {
				inputChannel = new FileInputStream(source).getChannel();
				outputChannel = new FileOutputStream(dest).getChannel();
				outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} finally {
			try {
				inputChannel.close();
				outputChannel.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static File getOutputMediaFile(int type) {

		// External sdcard location
		File mediaStorageDir = new File(
				Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"android upLoadView");
		// Create the storage directory if it does not exist
		//		if (mediaStorageDir.exists()) {
		//			if (!mediaStorageDir.mkdirs()) {
		//				Log.d(TAG, "Oops! Failed create "
		//						+ Config.IMAGE_DIRECTORY_NAME + " directory");
		//				return null;
		//			}
		//		}

		if(!mediaStorageDir.exists()){
			mediaStorageDir.mkdir();
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "VID_" + timeStamp + ".mp4");
		} else {
			return null;
		}

		return mediaFile;
	}
}
