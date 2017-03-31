package com.none.staff.task;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.none.staff.activity.Sp;
import com.none.staff.json.ZipInfo;
import com.none.staff.util.DownloadUtil;
import com.none.staff.util.ZipUtil;

public class DownloadResourceTask extends AsyncTaskWithCallback<ArrayList<ZipInfo>, Void, Boolean> {
    private final Activity context;
    private final Handler handler;
    private static final String TAG = "DownloadResourceTask";
    private ArrayList<TempZipInfo> tempZipInfoList = null;
    public boolean ableToUseCache = true;
    private int totalcount=1;
    private int downloadCount=0;
    private int totalKb=0;

    /**
     * 
     * <p><b>
     * download client pack to files/cache/www_andorid.zip and unzip to files/betta
     * </b></p>
     *
     * @param context
     * @param callback
     * @param ref
     * @param handler
     */
    public DownloadResourceTask(final Activity context, final ActivityCallback callback, final int ref, Handler handler) {
        super(callback, ref);
        if (context == null) {
            throw new IllegalArgumentException("owner must not be null");
        }
        this.context = context;
        this.handler = handler;
    }

    @Override
    protected Boolean doInBackground(ArrayList<ZipInfo>... params) {
        ArrayList<ZipInfo> webResources = null;
        boolean result = false;
        SharedPreferences sp = context.getSharedPreferences("SP", Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        boolean noDownloadFail=false;
        try {
            webResources = params[0];
            // loop webresources
            if(tempZipInfoList==null){
                generateTargetDownloadListFromConfig(webResources);
            }
            // Download zips
            for (TempZipInfo tempZipInfo : tempZipInfoList) {

                /**
                 * need download case
                 */
                // start download process
                DownloadUtil downloadUtil = new DownloadUtil(context);
                if (tempZipInfo.needDownload) {
                    if (!DownloadUtil.deviceOnline(context)) {
                        throw new Exception("device not on line");
                    }
                    /*
                     * Save the new ZIP to download zip folder
                     */
                    Log.d(TAG, "start download " + tempZipInfo.zipFileName);
                    Message msg = Message.obtain();
                    msg.what = Sp.SHOW_PROGRESS_MSG;
                    handler.sendMessage(msg);
                    boolean isSaveSuccess = downloadUtil.downloadResourceAndSave(tempZipInfo.zipUrl, tempZipInfo.zipDownloadPath,
                        tempZipInfo.zipFileName, handler, 1024L*tempZipInfo.size , totalcount);
                    if (!isSaveSuccess) {
                        throw new Exception("download zip file fail");
                    } else {
                        // check checksum again
                        String localChecksum = DownloadUtil.getLocalCopyCurrentChecksum(tempZipInfo.zipPathName, "SHA-512");
                        if (localChecksum == null || !tempZipInfo.configChecksum.equalsIgnoreCase(localChecksum)) {
                            Log.d(TAG, "local checksum is " + localChecksum);
                            Log.d(TAG, "config checksum is " + tempZipInfo.configChecksum);
                            throw new Exception("zip file checksum varify fail");
                        }
                    }
                }
            }
            noDownloadFail=true;
            // download finish
            // start unzip
            int i = 0;
            int zipCount = tempZipInfoList.size();
            while (i < zipCount) {
                TempZipInfo tempZipInfo = tempZipInfoList.get(i);
                i++;
                String savedUnzippedChecksum = sp.getString(tempZipInfo.zipFileName, "");
                if (!savedUnzippedChecksum.equals(tempZipInfo.configChecksum)) {
                    ableToUseCache = false;
                    editor.putString(tempZipInfo.zipFileName, "");
                    editor.commit();
                    Log.d(TAG, "start unzip " + tempZipInfo.zipFileName);
                    ZipUtil.UnZipFolder(tempZipInfo.zipPathName, DownloadUtil.getClientPackPath(context));
                    editor.putString(tempZipInfo.zipFileName, tempZipInfo.configChecksum);
                    editor.commit();
                } else {
                    Log.d(TAG, "unzip finish " + tempZipInfo.zipFileName);
                }
            }
            Log.d(TAG, "========= preparing client pack completed");
            result = true;

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
        //the fail case may come from download fail or unzip fail
        //if any zip has no unzip record
        if (!result&&noDownloadFail && ableToUseCache) {
            int i = 0;
            int zipCount = tempZipInfoList.size();
            while (i < zipCount) {
                i++;
                TempZipInfo tempZipInfo = tempZipInfoList.get(i);
                String savedUnzippedChecksum = sp.getString(tempZipInfo.zipFileName, null);
                if (savedUnzippedChecksum==null) {
                    ableToUseCache = false;
                    break;
                }
            }
        }
        return result;
    }
    
    
    public void generateTargetDownloadListFromConfig(ArrayList<ZipInfo> webResources){
        downloadCount=0;
        ArrayList<TempZipInfo> zipInfoList = new ArrayList<TempZipInfo>();
        for (ZipInfo zipInfo : webResources) {
            TempZipInfo tempZipInfo = new TempZipInfo();
            tempZipInfo.zipUrl = zipInfo.getZipurl();
            tempZipInfo.zipDownloadPath = DownloadUtil.getCacheFilePath(context);
            tempZipInfo.zipFileName = tempZipInfo.zipUrl.substring(tempZipInfo.zipUrl.lastIndexOf("/")+1);
            tempZipInfo.zipPathName = tempZipInfo.zipDownloadPath + "/" + tempZipInfo.zipFileName;
            tempZipInfo.configChecksum = zipInfo.getHashcode();
            tempZipInfo.size = zipInfo.getSize();
            if (DownloadUtil.localCopyExist(tempZipInfo.zipPathName)) {
                String localChecksum = DownloadUtil.getLocalCopyCurrentChecksum(tempZipInfo.zipPathName, "SHA-512");
                if (localChecksum != null && tempZipInfo.configChecksum.equalsIgnoreCase(localChecksum)) {
                    // equlas then no need download.
                    tempZipInfo.needDownload = false;
                } else {
                    tempZipInfo.needDownload = true;
                }
            }else{
                tempZipInfo.needDownload = true;
            }
            if(tempZipInfo.needDownload){
                downloadCount++;
                totalKb+=tempZipInfo.size;
            }
            zipInfoList.add(tempZipInfo);
        }
        tempZipInfoList=zipInfoList;
    }
    public int countTotalTargetDownloadFileNumber(){
        return downloadCount;
    }
    public void setTotalCounts(int count){
        this.totalcount=count;
    }
    public int getClientPackTotalSizeToUpdate(){
        return totalKb;
    }
}


class TempZipInfo {
    String zipFileName = null;
    String zipPathName = null;
    String configChecksum = null;
    String zipDownloadPath = null;
    String zipUrl = null;
    boolean needDownload=true;
    int size = 100;
}