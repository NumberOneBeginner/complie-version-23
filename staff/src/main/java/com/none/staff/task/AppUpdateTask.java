package com.none.staff.task;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.none.staff.activity.Sp;
import com.none.staff.util.DownloadUtil;

public class AppUpdateTask extends AsyncTaskWithCallback<String, Void, Boolean> {
    private final Activity context;
    private final Handler handler;
    private static final String TAG = "AppUpdateTask";

    public AppUpdateTask(final Activity context, final ActivityCallback callback, final int ref, Handler handler) {
        super(callback, ref);
        if (context == null) {
            throw new IllegalArgumentException("owner must not be null");
        }
        this.context = context;
        this.handler = handler;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        boolean result = false;
        try {
            String url = params[0];
//            String appTargetChecksum = params[1];
            DownloadUtil downloadUtil = new DownloadUtil(context);
            boolean needDownload=true;
            //TODO download app to external or internal?
            String downloadedFilepath=new StringBuffer().append(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)).toString();
            //=DownloadUtil.getCacheFilePath(context)+"/app";
            String apkName=url.substring(url.lastIndexOf("/"));
            String apkPath=downloadedFilepath+apkName;
            //if we allow use cached file , uncomment below
//            if (DownloadUtil.localCopyExist(apkPath)) {
//                String localChecksum = DownloadUtil.getLocalCopyCurrentChecksum(apkPath, "SHA-512");
//                needDownload=false;
//                if (localChecksum != null && localChecksum.equalsIgnoreCase(appTargetChecksum)) {
//                    needDownload = false;
//                } else {
//                    needDownload = true;
//                }
//            }
            if (needDownload) {
                if (!DownloadUtil.deviceOnline(context)) {
                    throw new Exception("device not on line");
                }
                Message msg = Message.obtain();
                msg.what = Sp.SHOW_PROGRESS_MSG;
                handler.sendMessage(msg);
                boolean isSaveSuccess = downloadUtil.downloadResourceAndSave(url,downloadedFilepath,
                    apkName, handler, 13000L * 1024, 1);
                if(isSaveSuccess){
                    result=true;
                }
            } else{
                result=true;
            }


        } catch (ClientProtocolException e) {
            this.setError(FAILED);
            Log.e(TAG, "DownloadResourceTask doInBackground fail", e);
        } catch (IOException e) {
            this.setError(FAILED);
            Log.e(TAG, "DownloadResourceTask doInBackground fail", e);
        } catch (Exception e) {
            this.setError(FAILED);
            Log.e(TAG, "DownloadResourceTask doInBackground fail", e);
        }
        return result;
    }
    public static String getVersion(Context context){
        try{
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(),0);
            String version = packInfo.versionName;
            return version;
        }catch(Exception e){
            Log.e(TAG,"get version error",e);
        }
        return null;
    }
}
