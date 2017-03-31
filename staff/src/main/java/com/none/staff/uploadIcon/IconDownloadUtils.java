package com.none.staff.uploadIcon;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

public class IconDownloadUtils {
	private final static String ALBUM_PATH
    = Environment.getExternalStorageDirectory() + "/red_icon/";
	Bitmap  mBitmap;
	Context context;
	private ProgressDialog mSaveDialog = null;
	private  String mFileName;
	private String mSaveMessage;
	File dirFile;
	public IconDownloadUtils(Context context){
		this.context = context;
	}
	public  void downloadIcon(final String url){
		 mSaveDialog = ProgressDialog.show(context, "保存图片", "图片正在保存中，请稍等...", true);
		  /*
	     * 连接网络
	     * 由于在4.0中不允许在主线程中访问网络，所以需要在子线程中访问
	     */
		  new Thread(new Runnable(){
			  @Override
		        public void run() {
		            try {
//		                String url = "http://img.my.csdn.net/uploads/201402/24/1393242467_3999.jpg";
		                mFileName = System.currentTimeMillis()+"icon.jpg";
		                //以下是取得图片的两种方法
		                //////////////// 方法1：取得的是byte数组, 从byte数组生成bitmap
		                byte[] data = getImage(url);
		                if(data!=null){
		                    mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);// bitmap
		                }else{
		                    Toast.makeText(context, "Image error!",Toast.LENGTH_LONG).show();
		                }
		                
		                Log.d("IocnDown", "set image ...");
		            } catch (Exception e) {
//		                Toast.makeText(context,"无法链接网络！", Toast.LENGTH_LONG).show();
		                e.printStackTrace();
		            }
		            if(mBitmap!=null){
		            try {
		                saveFile(mBitmap, mFileName);
		             // 其次把文件插入到系统图库
		              try {
		                  MediaStore.Images.Media.insertImage(context.getContentResolver(),
		                  		dirFile.getAbsolutePath(), mFileName, null);
		              } catch (FileNotFoundException e) {
		                  e.printStackTrace();
		              }
		              // 最后通知图库更新    "file://" + ALBUM_PATH
		              context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(dirFile.getAbsolutePath())));
		                mSaveMessage = "图片保存成功！";
		            } catch (IOException e) {
		                mSaveMessage = "图片保存失败！";
		                e.printStackTrace();
		            }
		           
		            }else{
		            	 mSaveMessage = "获取头像失败！";
			        }
		            messageHandler.sendMessage(messageHandler.obtainMessage());
		        }
		  }).start();
	}
	

  
    
    /**
     * Get image from newwork
     * @param path The path of image
     * @return byte[]
     * @throws Exception
     */
    public byte[] getImage(String path) throws Exception{
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        InputStream inStream = conn.getInputStream();
        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
            return readStream(inStream);
        }
        return null;
    }
    
    /**
     * Get data from stream
     * @param inStream
     * @return byte[]
     * @throws Exception
     */
    public static byte[] readStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while( (len=inStream.read(buffer)) != -1){
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }
    
    /**
     * 保存文件
     * @param bm
     * @param fileName
     * @throws IOException
     */
    public void saveFile(Bitmap bm, String fileName) throws IOException {
        dirFile = new File(ALBUM_PATH);
        if(!dirFile.exists()){
            dirFile.mkdir();
        }
        File myCaptureFile = new File(ALBUM_PATH + fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
        
        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
            		myCaptureFile.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"+myCaptureFile.getPath()))); 
    }
    
    private Handler messageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mSaveDialog.dismiss();
            Toast.makeText(context, mSaveMessage, Toast.LENGTH_SHORT).show();
        }
    };
}
