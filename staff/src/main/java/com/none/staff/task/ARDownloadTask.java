package com.none.staff.task;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.none.staff.activity.Sp;
import com.none.staff.json.AR;
import com.none.staff.json.JsonConfig;
import com.none.staff.json.VideoInfo;
import com.none.staff.util.DownloadUtil;
import com.none.staff.util.ZipUtil;

/**
 * 
 * <p>
 * <b> AR data base and videos will be downloaded from server and save to
 * internal storage </b>
 * </p>
 */
public class ARDownloadTask extends AsyncTaskWithCallback<JsonConfig, Void, Boolean> {
    private final Activity context;
    private final Handler handler;
    private boolean needDownloadDatabase = true;
    private int totalcount=1;
    private int arDownloadCount=0;
    private int totalKb=0;
    private ArrayList<TempDownloadInfo> tempDownloadInfoList = null;
    public static final String ZIPURL = "zipurl";
    public static final String HASHCODE = "hashcode";
    public static final String SIZE = "size";
    private static final String TAG = "ARDownloadTask";

    public ARDownloadTask(final Activity context, final ActivityCallback callback, final int ref, Handler handler) {
        super(callback, ref);
        if (context == null) {
            throw new IllegalArgumentException("owner must not be null");
        }
        this.context = context;
        this.handler = handler;
    }

    @Override
    protected Boolean doInBackground(JsonConfig... params) {
        boolean result = false;
        try {
            JsonConfig config = params[0];
            ArrayList<AR> arList = config.getAR();
            if (arList != null) {
                AR ar = arList.get(0);
                downloadARDataBase(ar,totalcount);
                downloadARVideo(ar,totalcount);
            }
            result=true;
        } catch (Exception e) {
            this.setError(FAILED);
            Log.e(TAG, "DownloadResourceTask doInBackground fail", e);
        }
        return result;
    }

    /**
     * 
     * <p>
     * <b> download AR data base to files/cache/AR_db.zip and unzip to
     * files/AR/ </b>
     * </p>
     * 
     * @param ar
     *            AR config
     * @param totalCount
     *            to present the download progress, we need to know the total
     *            files count include client pack and AR
     */
    public void downloadARDataBase(AR ar, int totalCount) throws Exception {
        String url = ar.getDatabase().get(ZIPURL);
        String configChecksum = ar.getDatabase().get(HASHCODE);
        DownloadUtil downloadUtil = new DownloadUtil(context);
        String downloadedDir = DownloadUtil.getCacheFilePath(context);
        String zipFileName = url.substring(url.lastIndexOf("/") + 1);
        String zipFilePath = downloadedDir + "/" + zipFileName;
        if (tempDownloadInfoList == null) {
            generateTargetDownloadListFromConfig(ar);
        }
        String localChecksumFirst = DownloadUtil.getLocalCopyCurrentChecksum(zipFilePath, "SHA-512");
        if (localChecksumFirst == null || !configChecksum.equalsIgnoreCase(localChecksumFirst)) {
            needDownloadDatabase=true;
        }else{
            needDownloadDatabase=false;
        }
        if (needDownloadDatabase) {
            if (!DownloadUtil.deviceOnline(context)) {
                throw new Exception("device not on line");
            }
            /*
             * Save the new ZIP to download zip folder
             */
            Log.d(TAG, "start download " + zipFileName);
            Message msg = Message.obtain();
            msg.what = Sp.SHOW_PROGRESS_MSG;
            handler.sendMessage(msg);
            boolean isSaveSuccess = downloadUtil.downloadResourceAndSave(url, downloadedDir, zipFileName, handler,
                1024L * Integer.parseInt(ar.getDatabase().get(SIZE)), totalCount);
            if (!isSaveSuccess) {
                throw new Exception("download zip file fail");
            } else {
                // check checksum again
                String localChecksum = DownloadUtil.getLocalCopyCurrentChecksum(zipFilePath, "SHA-512");
                if (localChecksum == null || !configChecksum.equalsIgnoreCase(localChecksum)) {
                    Log.d(TAG, "local checksum is " + localChecksum);
                    Log.d(TAG, "config checksum is " + configChecksum);
                    throw new Exception("zip file checksum varify fail");
                }
            }
        }
        SharedPreferences sp = context.getSharedPreferences("SP", Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        String savedUnzippedChecksum = sp.getString(zipFileName, "");
        if (!savedUnzippedChecksum.equals(configChecksum)) {
            String arPath=DownloadUtil.getARPath(context);
            //delete old database file.
            File ARDir=new File(arPath);
            if(ARDir.exists()){
                File fileList[]=ARDir.listFiles();
                for (File file : fileList) {
                    if(file.isFile()){
                        file.delete();
                    }
                }
            }
            ZipUtil.UnZipFolder(zipFilePath, DownloadUtil.getARPath(context));
            editor.putString(zipFileName, configChecksum);
            editor.commit();
        }
    }

    /**
     * 
     * <p>
     * <b> download AR video to files/AR/VideoPlayback </b>
     * </p>
     * 
     * @param ar
     * @return
     */
    public boolean downloadARVideo(AR ar, int totalCount) {
        boolean restult = true;
        ArrayList<VideoInfo> video = ar.getVideo();
        String downloadedDir = DownloadUtil.getARPath(context) + "/VideoPlayback";

        if (tempDownloadInfoList == null) {
            generateTargetDownloadListFromConfig(ar);
        }
        for (TempDownloadInfo tempVideoInfo : tempDownloadInfoList) {

            String url = tempVideoInfo.url;
            String configChecksum = tempVideoInfo.configChecksum;
            DownloadUtil downloadUtil = new DownloadUtil(context);
            try {
                if (tempVideoInfo.needDownload) {
                    /*
                     * Save the new ZIP to download zip folder
                     */
                    Log.d(TAG, "start download " + tempVideoInfo.fileName);
                    Message msg = Message.obtain();
                    msg.what = Sp.SHOW_PROGRESS_MSG;
                    handler.sendMessage(msg);
                    boolean isSaveSuccess = downloadUtil.downloadResourceAndSave(url, downloadedDir, tempVideoInfo.fileName,
                        handler, 1024L * tempVideoInfo.size, totalCount);
                    if (!isSaveSuccess) {
                        throw new Exception("download zip file fail");
                    } else {
                        // check checksum again
                        String localChecksum = DownloadUtil.getLocalCopyCurrentChecksum(tempVideoInfo.filePath, "SHA-512");
                        if (localChecksum == null || !configChecksum.equalsIgnoreCase(localChecksum)) {
                            Log.d(TAG, "local checksum is " + localChecksum);
                            Log.d(TAG, "config checksum is " + configChecksum);
                            throw new Exception("zip file checksum varify fail");
                        }
                    }
                }
                SharedPreferences sp = context.getSharedPreferences("SP", Context.MODE_PRIVATE);
                Editor editor = sp.edit();
                editor.putString(tempVideoInfo.fileName, configChecksum);
                editor.commit();
            } catch (Exception e) {
                restult = false;
                Log.e(TAG, "download Video fail", e);
                File failedVideo = new File(tempVideoInfo.filePath);
                if (failedVideo.exists() && failedVideo.isFile()) {
                    failedVideo.delete();
                }
            }
        }

        // remove old videos which are not in the list
        File videoDir = new File(downloadedDir);
        if (videoDir.exists() && videoDir.isDirectory()) {
            File[] fileList = videoDir.listFiles();
            for (File file : fileList) {
                String name = file.getName();
                boolean isOutDate = true;
                for (TempDownloadInfo info : tempDownloadInfoList) {
                    if (name.equals(info.fileName)) {
                        isOutDate = false;
                        break;
                    }
                }
                if (isOutDate) {
                    file.delete();
                }
            }
        }
        return restult;
    }

    /**
     * 
     * <p>
     * <b> generate the temporary target download list before start download
     * </b>
     * </p>
     * 
     * @param ar
     * @return
     */
    public void generateTargetDownloadListFromConfig(AR ar) {
        // check if database need download
        if(ar==null){
            return;
        }
        totalKb = 0;
        arDownloadCount = 0;
        String url = ar.getDatabase().get(ZIPURL);
        String configChecksum = ar.getDatabase().get(HASHCODE);
        String downloadedDir = DownloadUtil.getCacheFilePath(context);
        String zipFileName = url.substring(url.lastIndexOf("/") + 1);
        String zipFilePath = downloadedDir + "/" + zipFileName;
        if (DownloadUtil.localCopyExist(zipFilePath)) {
            String localChecksum = DownloadUtil.getLocalCopyCurrentChecksum(zipFilePath, "SHA-512");
            if (localChecksum != null && configChecksum.equalsIgnoreCase(localChecksum)) {
                // equlas then no need download.
                needDownloadDatabase = false;
            } else {
                needDownloadDatabase = true;
            }
        } else {
            needDownloadDatabase = true;
        }
        if (needDownloadDatabase) {
            totalKb += Integer.parseInt(ar.getDatabase().get(SIZE));
            arDownloadCount++;
        }
        // loop the video list and find which need to download
        ArrayList<TempDownloadInfo> tempInfoList = new ArrayList<TempDownloadInfo>();
        ArrayList<VideoInfo> videoList = ar.getVideo();
        String downloadPath=DownloadUtil.getARPath(context)+"/VideoPlayback/";
        for (VideoInfo videoInfo : videoList) {
            TempDownloadInfo tempInfo = new TempDownloadInfo();
            tempInfo.url = videoInfo.getZipurl();
            tempInfo.fileName = videoInfo.getZipurl().substring(videoInfo.getZipurl().lastIndexOf("/") + 1);
            tempInfo.filePath = downloadPath+tempInfo.fileName;
            tempInfo.configChecksum = videoInfo.getHashcode();
            tempInfo.size = videoInfo.getSize();
            if (DownloadUtil.localCopyExist(tempInfo.filePath)) {
                String localChecksum = DownloadUtil.getLocalCopyCurrentChecksum(tempInfo.filePath, "SHA-512");
                if (localChecksum != null && tempInfo.configChecksum.equalsIgnoreCase(localChecksum)) {
                    // equlas then no need download.
                    tempInfo.needDownload = false;
                } else {
                    tempInfo.needDownload = true;
                }
            } else {
                tempInfo.needDownload = true;
            }
            tempInfoList.add(tempInfo);
            if (tempInfo.needDownload) {
                totalKb += tempInfo.size;
                arDownloadCount++;
            }
        }
        tempDownloadInfoList = tempInfoList;
    }

    /**
     * 
     * <p>
     * Get the total size of updates include database and videos <br/>
     * Should call it after {@link #generateTargetDownloadListFromConfig(AR)}
     * </p>
     * 
     * @param ar
     * @return total size by KB
     */
    public int getTotalSizeToUpdate() {
        return totalKb;
    }

    public int getARTotalDownloadFileNumber() {
        return arDownloadCount;
    }
    public void setTotalCounts(int count){
        this.totalcount=count;
    }
}


class TempDownloadInfo {
    String fileName = null;
    String filePath = null;
    String configChecksum = null;
    String url = null;
    boolean needDownload = true;
    int size = 100;
}
