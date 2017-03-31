package com.none.staff.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;


/**
 *
 */
public class IOUtils {
	private static final String TAG = "IOUtils";

    public static void close(Closeable ioObject) {
		if (null != ioObject) {
			try {
				if(ioObject instanceof Flushable){
					((Flushable) ioObject).flush();
				}				
				ioObject.close();
				ioObject=null;
			}
			catch (IOException e) {
				ioObject=null;
				Log.e(TAG,"IO stream close erorr:{}", e);
			}
		}
	}

	public static void copyStream(final InputStream is, final OutputStream os) throws IOException {
		byte[] buffer = new byte[1024];
		int n = 0;		
		while (-1 != (n = is.read(buffer))) {
			os.write(buffer, 0, n);
		}		
	}
	
	public static void copyIs(ByteArrayOutputStream baos,InputStream is){
		try {
			IOUtils.copyStream(is, baos);
		}
		catch (IOException e) {
		    Log.e(TAG,"copyIs erorr:", e);
			//EntitiesFileDownloader.Log.e(TAG,"Error copying stream", e);
		}
	}
}
