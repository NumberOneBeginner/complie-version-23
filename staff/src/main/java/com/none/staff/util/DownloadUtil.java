package com.none.staff.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.none.staff.activity.Sp;

public class DownloadUtil {
	private static final String LAST_MODIFIED = "Last-Modified";
	private static final String TAG="downloadUtil";
	private final Context context;
	String lastModified = null;
	
	public DownloadUtil(Context context){
		this.context = context;
	}

	public String getLastModified() {
		return lastModified;
	}
	public ByteArrayOutputStream downloadResource(String url)throws ClientProtocolException,IOException{
		return downloadResourceAndLastModified(url);
	}

    public StringBuffer downloadText(String url) throws ClientProtocolException, IOException{
        StaffHttpClient httpclient = StaffHttpClient.getInstance(context);
        StringBuffer strBuf = null;
        if (url.indexOf('?') != -1) {
            url += "&" + new Date().getTime();
        } else {
            url += "?" + new Date().getTime();
        }
        strBuf = httpclient.connectURL(url);
        return strBuf;
    }


	public ByteArrayOutputStream downloadResourceAndLastModified(String url)throws ClientProtocolException,IOException{
		StaffHttpClient httpclient = StaffHttpClient.getInstance(context);
		InputStream is=null;		
		try{	
			if(url.indexOf('?')!=-1){
				url+="&"+new Date().getTime();
			}else{
				url+="?"+new Date().getTime();
			}
			//Log.d(TAG,"download:"+url);
			HttpResponse response = httpclient.getHttpResponse(url);
			if (response != null) {
				is= httpclient.getResponseInputStream(response);
				Header[] headers = response.getHeaders(LAST_MODIFIED);
				if (headers.length > 0) {
					lastModified = headers[0].getValue();
				}
			}
			if(is!=null){	
				if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					IOUtils.copyIs(baos,is);
					return baos;
				}else{
					Log.e(TAG,"Response error:"+response.getStatusLine().getReasonPhrase());
                    Log.e(TAG,"Response error:"+response.getStatusLine().getStatusCode());
				}
			}
		}finally{
			IOUtils.close(is);
		}
		return null;
	}

	/**
	 * 
	 * <p><b>
	 * </b></p>
	 *
	 * @param url
	 * @param resourcePath
	 * @param fileName
	 * @param handler for update progress
	 * @param givenSize for count download progress when we got multiple files to download in one progress bar.
	 * @param count how many files need to download
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
    public boolean downloadResourceAndSave(String url, String resourcePath, String fileName,Handler handler,long givenSize,int count) throws ClientProtocolException,
        IOException {
        StaffHttpClient httpclient = StaffHttpClient.getInstance(context);
        InputStream is = null;
        try {
            if (url.indexOf('?') != -1) {
                url += "&" + new Date().getTime();
            } else {
                url += "?" + new Date().getTime();
            }
            HttpResponse response = httpclient.getHttpResponse(url);
            long contentLength=0L;
            if (response != null) {
                is = httpclient.getResponseInputStream(response);
                contentLength=response.getEntity().getContentLength();
                Log.d(TAG, "contentLength"+contentLength);
                Log.d(TAG, "givenSize"+givenSize);
                
                if(contentLength<1){
                    contentLength=givenSize;
                }
            }
            if (is != null) {
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//                    Log.d(TAG,is.)
                    return saveResource(resourcePath, fileName, is,handler,contentLength,count);
                } else {
                    Log.e(TAG,"Response error:"+response.getStatusLine().getReasonPhrase()+response.getStatusLine()
                        .getStatusCode());
                }
            }
        } finally {
            IOUtils.close(is);
        }
        return false;
    }
    
    public boolean writeToFile(final byte[] bytes,String path) throws IOException {
		return saveFile(new ByteArrayInputStream(bytes),path);
	}
	public boolean writeToFile(final byte[] bytes,String path, Handler handler) throws IOException {
		return saveFile(new ByteArrayInputStream(bytes),path,handler,0,0);
	}

	public boolean saveFile(InputStream is,String path, Handler handler,long contentLength, int count)throws IOException{
		OutputStream os= null;
		boolean flag=false;
		try{
		    File savedFile=new File(path);
			os = new FileOutputStream(savedFile);
			byte[] buf = new byte[1024];
			int len;
			double updateCounter=0;
			long sendScale=contentLength*count/100;
			while ((len = is.read(buf)) > 0){
				updateCounter+=len;
				os.write(buf, 0, len);
                //send message to update progress bar
				if(updateCounter>=sendScale){
				    double persentage=updateCounter*100/contentLength/count;
				    sendUpdateProgressBarMsg(handler,(int)persentage);
					updateCounter=0;
				}
			}
			//send message to update progress bar
			double persentage=updateCounter*100/contentLength/count;
			sendUpdateProgressBarMsg(handler,(int)persentage);
			flag=true;
		}finally{
			IOUtils.close(os);
			IOUtils.close(is);
		}
		return flag;
	}
	
	public void sendUpdateProgressBarMsg(Handler handler,int persentage){
	    if(handler==null){
	        return;
	    }
	    Message msg = Message.obtain();
        msg.what = Sp.DOWNLOAD_PROGRESS_UPDATE_MSG;
        msg.arg1=persentage;
        handler.sendMessage(msg);
	}
	
	public boolean saveFile(InputStream is,String path)throws IOException{
		OutputStream os= null;
		boolean flag=false;
		try{
			os = new FileOutputStream(new File(path));
			byte[] buf = new byte[1024];
			int len;
			int i=0; 
			while ((len = is.read(buf)) > 0){
			    i+=len;
				os.write(buf, 0, len);
			}
			Log.d(TAG,"========++File Size ==++======="+i);
			flag=true;
		}finally{
			IOUtils.close(os);
			IOUtils.close(is);
		}
		return flag;
	}

	public String getStrFormByte(ByteArrayOutputStream baos) throws IOException{
		byte[] buf = null;
		String result = null;
		try {
			if(baos!=null){
				buf = baos.toByteArray();
				if(buf != null){
					result = new String(buf,HTTP.UTF_8);
				}
			}
		}finally{
			IOUtils.close(baos);
		}
		return result;
		
	}

    public  boolean saveResource(String resourcePath, String fileName, InputStream in, Handler handler,long contentLength, int count)throws IOException {
        String pathName = resourcePath + File.separator + fileName;
        File dir = new File(resourcePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        boolean flag = false;
        try {
            flag = saveFile(in, pathName,handler,contentLength,count);
        } finally {
            IOUtils.close(in);
        }
        return flag;
    }
    public boolean createAndSaveFile(InputStream is,String path,String name)throws IOException{
        OutputStream os= null;
        boolean flag=false;
        try{
            File file = new File(path, name);
            if(!file.exists()){
                file.createNewFile();  

            }
            os = new FileOutputStream(file);
            
            byte[] buf = new byte[1024];
            int len;
            int i=0; 
            while ((len = is.read(buf)) > 0){
                i+=len;
                os.write(buf, 0, len);
            }
            flag=true;
        }finally{
            IOUtils.close(os);
            IOUtils.close(is);
        }
        return flag;
    }
    public static boolean deviceOnline(Context context) {   
            ConnectivityManager mConnectivity = (ConnectivityManager) context   
                    .getSystemService(Context.CONNECTIVITY_SERVICE);   
            NetworkInfo info = mConnectivity.getActiveNetworkInfo();
            if (info == null) {  
                return false;   
            }   
            if (!info.isAvailable()|| !info.isConnected()) { 
                return false;   
            } else {
                return true;
            }
    }
    public static String getLocalCopyCurrentChecksum(String path,String algorithm) {
        String checksum = null;
        FileInputStream fis=null;
        
        if(!localCopyExist(path)){
            return null;
        }
        try{
            MessageDigest md = MessageDigest.getInstance(algorithm);
            fis = new FileInputStream(path);
            byte[] dataBytes = new byte[1024];
            int nread = 0; 
            while ((nread = fis.read(dataBytes)) != -1) {
              md.update(dataBytes, 0, nread);
            }
            byte[] mdbytes = md.digest();

           //convert the byte to hex format method 2
            StringBuffer hexString = new StringBuffer();
            for (int i=0;i<mdbytes.length;i++) {
                hexString.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));

            }
            checksum=hexString.toString();
        }catch(NoSuchAlgorithmException e){
            Log.e(TAG,e.getMessage());
        }catch(FileNotFoundException e){
            Log.e(TAG,e.getMessage());
        }catch(IOException e){
            Log.e(TAG,e.getMessage());
        }finally{
            IOUtils.close(fis);
        }
        return checksum;
    }
    public static boolean localCopyExist(String path) {
        File file=new File(path);
        if(file.exists()){
            return true;
        }else{
            return false;
        }
    }
    public static String getCacheFilePath(Context context){
        return context.getFilesDir().getPath()+"/cache";
    }
    public static String getClientPackPath(Context context){
        return context.getFilesDir().getPath()+"/betta";
    }
    public static String getARPath(Context context){
        return context.getFilesDir().getPath()+"/AR";
    }
}
