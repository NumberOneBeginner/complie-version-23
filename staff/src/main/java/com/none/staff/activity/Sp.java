package com.none.staff.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hsbc.greenpacket.activities.Constants;
import com.hsbc.greenpacket.activities.MainBrowserActivity;
import com.hsbc.greenpacket.customersegment.util.CustomerSegmentUtil;
import com.hsbc.greenpacket.process.ProcessUtil;
import com.hsbc.greenpacket.util.AppVersionComparator;
import com.hsbc.greenpacket.util.NameValueStore;
import com.hsbc.greenpacket.util.StringUtil;
import com.hsbc.greenpacket.util.UIUtil;
import com.hsbc.share.ShareUtils;
import com.none.Plugin.pdfUpload.InvoiceUploadPlugin;
import com.none.staff.R;
import com.none.staff.json.JsonConfig;
import com.none.staff.task.ARDownloadTask;
import com.none.staff.task.ActivityCallback;
import com.none.staff.task.AppUpdateTask;
import com.none.staff.task.AsyncTaskWithCallback;
import com.none.staff.task.DownloadResourceTask;
import com.none.staff.task.GetRegionConfigTask;
import com.none.staff.util.DownloadUtil;
import com.none.staff.utils.NetworkUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import fi.iki.elonen.SimpleWebServer;

public class Sp extends FragmentActivity implements ActivityCallback {
    AssetManager assetManager = null;
    private static final String TAG = "splash Activity";
    private static final int GET_REGION_CONFIG_TASK = 1;
    private static final int DOWNLOAD_CLIENT_PACK_TASK = 2;
    private static final int APP_UPGRADE_TASK = 3;
    private static final int DOWNLOAD_AR_TASK = 4;
    
    public static final int CLIENT_PACK_DOWNLOAD_FAIL_DIALOG=0;
    public static final int CLIENT_PACK_DOWNLOAD_FAIL_CANCELABLE_DIALOG=1;
    public static final int APP_UPDATE_DIALOG=2;
    public static final int CLIENT_PACK_DOWNLOAD_SIZE_ALERT_DIALOG=3;
    
    public static final int SHOW_PROGRESS_MSG = 1;
    public static final int DOWNLOAD_PROGRESS_UPDATE_MSG = 2;
    private ArrayList<AsyncTaskWithCallback> taskPool = null;
    private AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
    private LoadingDialogFragment loadingDialogFragment = new LoadingDialogFragment();
    private ProgressDialogFragment progressDialogFragment = new ProgressDialogFragment();
    private Handler handler = null;
    private int theSizeHaventUpdateToUI=0;
    private JsonConfig config=null;
    private DownloadResourceTask downloadResourceTask =null;
    private ARDownloadTask arDownloadTask=null;
    private ImageView spBgImageView = null;
    private boolean shouldShowCancelableDialog=false;
    private boolean downloadClientPackSuccess=false;
    private boolean isStandAloneGreenLaiseeApp=false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"on sp create");
        SharedPreferences invoiceSharedPreferences=getSharedPreferences(InvoiceUploadPlugin.PLUGIN_NAME, Context.MODE_PRIVATE);
        Editor invoiceEditor=invoiceSharedPreferences.edit().clear();
        final Intent intent = getIntent();
        final String intentAction = intent.getAction();
        // Log.e("action=",intentAction);
        String type = intent.getType();
        String pdfPath ="";
        if (Intent.ACTION_SEND.equals(intentAction) && type != null) {
                finishAllActivity();
         //   try {
                Uri pdfUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                Log.i("bob","pdfUri="+ pdfUri.toString());
                invoiceEditor.putString(InvoiceUploadPlugin.ENTER_TYPE,"Invoke");
                pdfPath = ShareUtils.getPath(this, pdfUri);//URLDecoder.decode(pdfUri.toString(), "utf-8");
                Log.i("bob","pdfPath="+  pdfPath );
                invoiceEditor.putString(InvoiceUploadPlugin.PDF_PATH,pdfPath );
                invoiceEditor.commit();
        /*        if (!TextUtils.isEmpty(pdfPath)) {
                    Intent mintent = new Intent(this, ShareMainActivity.class);
                    mintent.putExtra("pathUrl", pdfUri);
                    startActivity(mintent);
                    this.finish();
                }*/
        /*    } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }*/
        }
        else {
            invoiceEditor.putString(InvoiceUploadPlugin.ENTER_TYPE, "Normal");
            invoiceEditor.commit();
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |     WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
//        if (Build.VERSION.SDK_INT >= 21) {
//            full(false);
//            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        }
 else{
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        //根据获取到的设备的NavigationBar的高度为DecorView设置下边距
        getWindow().getDecorView().findViewById(android.R.id.content)
                .setPadding(0, 0, 0, getNavigationBarHeight());
        //为decorview设置背景色
//        getWindow().getDecorView().findViewById(android.R.id.content)
//                .setBackgroundResource(R.color.black);

        isStandAloneGreenLaiseeApp = this.getResources().getBoolean(R.bool.standalone_mode);
        StaffApplication application = (StaffApplication) getApplication();
        //check if this is the task root, that means no other activity had started before it.
        if (!isTaskRoot()) {
//            final Intent intent = getIntent();
//            final String intentAction = intent.getAction();
            //check if it start from launch mode or start from recent task manager stack
            //As requirement, even it start from click icon, it should also show the previous browser activity
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intentAction != null && intentAction.equals(Intent.ACTION_MAIN)) {
                //check if config expired, if yes, it should start from head
                if (!GetRegionConfigTask.configurationExpired(this, "0")) {
                    //check if it start from app update, if yes, it should start from head
                    ActivityManager manager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
                    List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(2);
                    if (runningTaskInfos != null) {
                        ComponentName cn = runningTaskInfos.get(0).baseActivity;
                        if (cn != null) {
                            if (!cn.getPackageName().equals("com.android.packageinstaller")) {
                                Log.d(TAG, "Main Activity is not the root.  Finishing Main Activity instead of launching.");
                                finish();
                                return;
                            }
                        }
                    }
                }
            }
        } else {
            Log.d(TAG, "is the root.");
        }
        if(isStandAloneGreenLaiseeApp){
            setContentView(R.layout.sp_for_green_laisee);
            ImageView iv=(ImageView)this.findViewById(R.id.green_ground_img);
            UIUtil.adjustImageViewSizeFixToImageRadio(this,R.drawable.green_home_bottom,iv);
        }else{
            setContentView(R.layout.sp);
            spBgImageView=(ImageView)findViewById(R.id.spBgImageView);
            CustomerSegmentUtil.setImageBgByName(spBgImageView, this, "screen");
        }
        application.getActivityStack().put(String.valueOf(this.hashCode()), this);
        setHandler();

        boolean ifUseLocalFile = this.getResources().getBoolean(R.bool.local_mode);
        if(ifUseLocalFile){
            Log.d("bob","sp use local file");
            showLoadingDialog();  
            assetManager = getAssets();
            try {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            SharedPreferences sp = getSharedPreferences("SP", Context.MODE_PRIVATE);
                            if (!sp.getBoolean("ifLocalFileCopied", false)) {
                                doCopyAssets();
                                Editor editor = sp.edit();
                                editor.putBoolean("ifLocalFileCopied", true);
                                editor.commit();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (isStandAloneGreenLaiseeApp) {
                            gotoStandAloneGreenLaisee();
                        } else {
                            startServer();
                        }
                    }
                }).start();
            }
            catch (IllegalThreadStateException E)
            {
                Log.e("bob","using local server have started");
            }
        }else{
            startDownloadConfig();
        }
        //startServer();
    }
	  /**
	 * statusbar
	 * @param enable
	 *            false
	 */
	private void full(boolean enable) {
		// TODO Auto-generated method stub
		WindowManager.LayoutParams p = this.getWindow().getAttributes();
		if (enable) {

			p.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;

		} else {
			p.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);



		}
		getWindow().setAttributes(p);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
	}


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("bob","sp destory");
        Log.d(TAG, "on destroy clear task");
        if (taskPool != null) {
            for (AsyncTaskWithCallback task : taskPool) {
                task.cancel(true);
            }
            taskPool.clear();
        }

        //UIUtil.releaseBitmap(spBgImageView);
        ((StaffApplication)this.getApplication()).getActivityStack().remove(String.valueOf(this.hashCode()));
    }

    // -------------------handler start--------------------//
    private void setHandler() {
        handler = new Handler() {
            /*
             * (non-Javadoc)
             * 
             * @see android.os.Handler#handleMessage(android.os.Message)
             */
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == SHOW_PROGRESS_MSG) {
                    Log.d(TAG, "show progress");
                    showProgressDialog();
                } else if (msg.what == DOWNLOAD_PROGRESS_UPDATE_MSG) {
                    updateProgress(msg.arg1);
                }
            }


        };
    }

    // -------------------handler end--------------------//


    // -------------------Tasks function start--------------------//

    private void startDownloadConfig() {
        try {
            showLoadingDialog();
            GetRegionConfigTask task = new GetRegionConfigTask(this, this, GET_REGION_CONFIG_TASK, this.getResources().getString(
                R.string.config_url), "0", false);
            addTask(task);
            task.execute();
        } catch (Exception e) {
            Log.e(TAG, "GetRegionConfigTask fail", e);
            //TODO update to a general fail dialog
            showAlertDialog(CLIENT_PACK_DOWNLOAD_FAIL_DIALOG);
        }
    }

    @Override
    public void handleCallback(AsyncTaskWithCallback task, int ref) {
        try {
            this.taskPool.remove(task);
            switch (task.getRef()) {
            case GET_REGION_CONFIG_TASK:
                handleGetConfigTask((GetRegionConfigTask) task);
                break;
            case DOWNLOAD_CLIENT_PACK_TASK:
                handleDownloadClientPackTask((DownloadResourceTask) task);
                break;
            case APP_UPGRADE_TASK:
                handleAppUpgradeTask((AppUpdateTask)task);
                break;
            case DOWNLOAD_AR_TASK:
                handleDownloadARTask((ARDownloadTask)task);
                break;
            default:
                break;
            }
        } catch (Exception e) {
            Log.e(TAG, "get update fail", e);
            //TODO update to a general fail dialog
            showAlertDialog(CLIENT_PACK_DOWNLOAD_FAIL_DIALOG);
        }
    }


    private void addTask(AsyncTaskWithCallback task) {
        if (this.taskPool == null) {
            taskPool = new ArrayList<AsyncTaskWithCallback>();
        }
        taskPool.add(task);
    }


    private void handleGetConfigTask(GetRegionConfigTask task) {
        if (task.getError() == AsyncTaskWithCallback.SUCCESS) {
            StringBuffer regionalConfigStr = task.getResult();
            GsonBuilder builder = new GsonBuilder();
            builder.excludeFieldsWithoutExposeAnnotation();
            Gson gson = builder.create();
            config = gson.fromJson(regionalConfigStr.toString(), JsonConfig.class);
            //check if need force app upgrade
            if(isAppOutdate(config)){
                showAlertDialog(APP_UPDATE_DIALOG);
                return;
            }

            StaffApplication application = (StaffApplication) getApplication();
            application.config=config;
            //check end
            HashMap<String, Activity> map = ((StaffApplication)this.getApplication()).getActivityStack();
            if(task.isConfigUpdate){
                if(map.size()>1){
                    for (Entry<String, Activity> entry : map.entrySet()) {
                        Activity activity = entry.getValue();
                        if (!(activity instanceof Sp)) {
                            activity.finish();
                        }
                    }
                }
            }else if(!isTaskRoot()){
                Log.d(TAG, "Not the task root");
                finish();
                return;
            }
            //Check network type
            int networkType=NetworkUtils.getConnectedType(this);
            initClientPackAndARDownloadTask(config);
            if(networkType==ConnectivityManager.TYPE_WIFI){
                addTask(downloadResourceTask);
                downloadResourceTask.execute(config.getWebRresources());
            }else{
                int totalSize=0;
                if(isStandAloneGreenLaiseeApp){
                    totalSize=downloadResourceTask.getClientPackTotalSizeToUpdate();
                }else {
                    totalSize=downloadResourceTask.getClientPackTotalSizeToUpdate()+arDownloadTask.getTotalSizeToUpdate();
                }
                if(totalSize>0){
                    alertDialogFragment.setMessage(replaceValue(this.getString(R.string.client_pack_download_alert),totalSize/1000f));
                    showAlertDialog(CLIENT_PACK_DOWNLOAD_SIZE_ALERT_DIALOG);
                }else{
                    addTask(downloadResourceTask);
                    downloadResourceTask.execute(config.getWebRresources());
                }
            }
        } else {
            //TODO update to a general fail dialog
            showAlertDialog(CLIENT_PACK_DOWNLOAD_FAIL_DIALOG);
        }
    }
    public void finishAllActivity(){
        Log.d(TAG, "finish all activities");
        HashMap<String, Activity> map = ((StaffApplication) this.getApplication()).getActivityStack();
        for (Entry<String, Activity> entry : map.entrySet()) {
            Activity activity = entry.getValue();
            activity.finish();
        }
    }
    private void handleAppUpgradeTask(AppUpdateTask task){
        if (task.getError() == AsyncTaskWithCallback.SUCCESS &&task.getResult()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String downloadedFilepath=new StringBuffer().append(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)).toString();
            //=DownloadUtil.getCacheFilePath(context)+"/app";
            String url=config.getAppUpgradeLink();
            String apkName=url.substring(url.lastIndexOf("/"));
            String apkPath=downloadedFilepath+apkName;
            Uri data = Uri.parse("file://"+apkPath);
            intent.setDataAndType(data, "application/vnd.android.package-archive");
            startActivity(intent);
            finishAllActivity();
        }else{
            showAlertDialog(CLIENT_PACK_DOWNLOAD_FAIL_DIALOG);
        }
    }
    private void handleDownloadClientPackTask(DownloadResourceTask task) {
        if (task.getError() == AsyncTaskWithCallback.SUCCESS&&task.getResult()) {
            downloadClientPackSuccess=true;
            shouldShowCancelableDialog=true;
        }else{
            shouldShowCancelableDialog=task.ableToUseCache;
            downloadClientPackSuccess=false;
            showAlertDialog(CLIENT_PACK_DOWNLOAD_FAIL_DIALOG);
            return;
        }
        if(isStandAloneGreenLaiseeApp){
            ProgressBarDialog dialog=(ProgressBarDialog)progressDialogFragment.getDialog();
            if(dialog!=null){
                dialog.setProgress(100);
            }
            gotoStandAloneGreenLaisee();
        }else{
            addTask(arDownloadTask);
            arDownloadTask.execute(config);
        }
    }
    
    private void gotoStandAloneGreenLaisee(){

        NameValueStore store = new NameValueStore(this);
        boolean isOnboardingPageShowed = Boolean.valueOf(store.getAttribute(Constants.ONBOARDING_PAGE_SHOWED));
        //boolean isOnboardingPageShowed =true;

		if(isOnboardingPageShowed){
            Intent intent = new Intent(this,MainBrowserActivity.class);
            Bundle bundle = new Bundle();
            String url = "file:///web/prelogon/logon/logon.html";
            String value = store.getAttribute(Constants.CUSTOMER_INFO);
            if(!StringUtil.IsNullOrEmpty(value)){
                url="file:///web/postlogon/menu/menu.html";
            }
            url = ProcessUtil.localUrlIntercept(url, DownloadUtil.getClientPackPath(this));
            bundle.putString(Constants.INTENT_URL_KEY, url);
            intent.putExtras(bundle);
            this.startActivity(intent);
        }else{
            Intent intent = new Intent(this,OnboardingActivity.class);
            this.startActivity(intent);
        }
        overridePendingTransition(R.anim.page_in_rightleft, R.anim.page_out_rightleft);
        finish();
    }
    
    private void handleDownloadARTask(ARDownloadTask task) {
        if (task.getError() == AsyncTaskWithCallback.SUCCESS&&task.getResult()&&downloadClientPackSuccess) {
            ProgressBarDialog dialog=(ProgressBarDialog)progressDialogFragment.getDialog();
            if(dialog!=null){
                dialog.setProgress(100);
            }
            assetManager = getAssets();
            try{
            new Thread(new Runnable() {
                public void run() {
                    startServer();
                }
            }).start();}
            catch (IllegalThreadStateException E)
            {
                Log.e("bob","download server have started");
            }
        }else{
            if(shouldShowCancelableDialog){
                showAlertDialog(CLIENT_PACK_DOWNLOAD_FAIL_CANCELABLE_DIALOG);
            }else{
                showAlertDialog(CLIENT_PACK_DOWNLOAD_FAIL_DIALOG);
            }
        }
    }
    
    private void initClientPackAndARDownloadTask(JsonConfig config){
        downloadResourceTask = new DownloadResourceTask(this, this, DOWNLOAD_CLIENT_PACK_TASK, handler);
        downloadResourceTask.generateTargetDownloadListFromConfig(config.getWebRresources());
        int totalcount=0;
        if(isStandAloneGreenLaiseeApp){
            totalcount=downloadResourceTask.countTotalTargetDownloadFileNumber();
        
        }else{
            arDownloadTask = new ARDownloadTask(this, this, DOWNLOAD_AR_TASK, handler);
            if(config.getAR()!=null){
                arDownloadTask.generateTargetDownloadListFromConfig(config.getAR().get(0));
            }
            totalcount=downloadResourceTask.countTotalTargetDownloadFileNumber()+arDownloadTask.getARTotalDownloadFileNumber();
        }
        downloadResourceTask.setTotalCounts(totalcount);
        if(arDownloadTask!=null){
            arDownloadTask.setTotalCounts(totalcount);
        }
    }
    private void downloadClientPack(JsonConfig config) {
        DownloadResourceTask task = new DownloadResourceTask(this, this, DOWNLOAD_CLIENT_PACK_TASK, handler);
        addTask(task);
        task.execute(config.getWebRresources());
    }
    
    public boolean isAppOutdate(JsonConfig config){
        String currentVersion=AppUpdateTask.getVersion(this).trim();
        String targetAppVersion=config.getVersion();
        Log.i(TAG,"Force APP upgrade enabled,Target APP version:"+targetAppVersion);
        if (!StringUtil.IsNullOrEmpty(currentVersion) && !StringUtil.IsNullOrEmpty(targetAppVersion)) {
            int mIfUpdateTemp = AppVersionComparator.compareVersion(targetAppVersion,currentVersion);
            if (mIfUpdateTemp > 0) {
                /*
                 * code review comment:a positive integer as 
                 * current service version is new than target version
                 */
                return true;
            } else {
                return false;
            }

        } else {
            return false; // code review comment:version is null and return false
        }
    }
    
    public void downloadApp(){
        AppUpdateTask appUpdateTask=new AppUpdateTask(Sp.this, Sp.this, APP_UPGRADE_TASK, handler);
        addTask(appUpdateTask);
        String url=config.getAppUpgradeLink();
        appUpdateTask.execute(url);
        showProgressDialog();
    }
    // -------------------Tasks function end--------------------//

    // -------------------start server--------------------//
    private void startServer() {
        try {
            Log.e("staff", "sp startServer");
            Log.e("bob","on sp startServer");
            String port =this.getResources().getString(R.string.local_host_port);
            Log.i(TAG, "-d " + this.getFilesDir().getPath() + File.separator + "betta" + File.separator + "www");
            SimpleWebServer.StartServer(new String[] {"-d",
                this.getFilesDir().getPath() + File.separator + "betta" + File.separator + "www","-p",port});
            // No log

            // SimpleWebServer.StartServer(new String[] {"-d",
            // this.getFilesDir().getPath()
            // + File.separator + "betta" + File.separator + "www","-q"});
            Log.e("bob","start activity staff");
            Intent mainIntent = new Intent(Sp.this, staff.class);
            //mainIntent.setFlags(Intent.FLAG_ACTIVITY_TOP);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            Sp.this.startActivity(mainIntent);
            Sp.this.finish();
            overridePendingTransition(0, 0);
        } catch (Exception e) {
            Log.e(TAG, "start server error", e);
            showAlertDialog(this.CLIENT_PACK_DOWNLOAD_FAIL_DIALOG);
        }
    }

    // -------------------start server end--------------------//

    // -------------------dialogs -------------------------//
    public class AlertDialogFragment extends DialogFragment {
        private int dialogType=0;
        private String messgae=null;
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Log.d(TAG, "create a new dialog");
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            switch (dialogType) {
                case APP_UPDATE_DIALOG: {
                    //app upgrade alert
                    builder.setMessage(R.string.app_update_msg)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Log.d(TAG, "start download apk");
                            downloadApp();
                        }
                    }).setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ((StaffApplication)Sp.this.getApplication()).exitApp();
                        }
                    }).setCancelable(false);
                    break;
                }
                case CLIENT_PACK_DOWNLOAD_FAIL_CANCELABLE_DIALOG: {
                    //cancelable client pack download fail alert
                    builder.setMessage(R.string.update_fail_msg)
                        .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startDownloadConfig();
                            }
                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new Thread(new Runnable() {
                                    public void run() {
                                        startServer();
                                    }
                                }).start();
                            }
                        }).setCancelable(false);
                    break;
                }
                case CLIENT_PACK_DOWNLOAD_SIZE_ALERT_DIALOG:
                    //TODO enable the optional downlaod
                    builder.setMessage(messgae)
                    .setPositiveButton(R.string.download, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            addTask(downloadResourceTask);
                            downloadResourceTask.execute(config.getWebRresources());
                        }
                    }).setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ((StaffApplication)Sp.this.getApplication()).exitApp();
                        }
                    }).setCancelable(false);
                    break;
                case CLIENT_PACK_DOWNLOAD_FAIL_DIALOG:
                default: {
                    //uncancelable client pack download fail alert
                    builder.setMessage(R.string.update_fail_msg)
                        .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startDownloadConfig();
                            }
                        }).setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ((StaffApplication)Sp.this.getApplication()).exitApp();
                            }
                        }).setCancelable(false);
                    break;
                }
            }

            Dialog dialog = builder.create();
            this.setCancelable(false);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            return dialog;

        }
        public void setDialogType(int type){
            dialogType=type;
        }
        public void setMessage(String msg){
            this.messgae=msg;
        }
    }
    public class AlertCancelDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Log.d(TAG, "create a new dialog");
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.update_fail_msg).setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    startDownloadConfig();
                }
            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    new Thread(new Runnable() {
                        public void run() {
                            startServer();
                        }
                    }).start();
                }
            }).setCancelable(false);
            Dialog dialog = builder.create();
            this.setCancelable(false);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            return dialog;

        }
    }
    public static class LoadingDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Log.i(TAG, "create loading dialog");
            LoadingDialog dialog = new LoadingDialog(getActivity(), null);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            this.setCancelable(false);

            return dialog;

        }
    }
    public static class ProgressDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Log.i(TAG, "create loading dialog");
            ProgressBarDialog dialog = new ProgressBarDialog(getActivity(), null);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            this.setCancelable(false);
            return dialog;
        }
        
    }

    public void showLoadingDialog() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = this.getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        loadingDialogFragment.show(ft, "dialog");
    }

    public void showAlertDialog(int dialogType) {
        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        Fragment prev = this.getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        alertDialogFragment.setDialogType(dialogType);
        alertDialogFragment.show(ft, "dialog");
    }

    public void showProgressDialog() {
        if (!progressDialogFragment.isAdded()) {
            FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
            Fragment prev = this.getSupportFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            progressDialogFragment.show(ft, "dialog");
        }
    }

    private void updateProgress(int arg1) {
        
        if (progressDialogFragment != null) {
            ProgressBarDialog dialog=(ProgressBarDialog) progressDialogFragment.getDialog();
            if(dialog==null){
                theSizeHaventUpdateToUI+=arg1;
            }else{
                if(theSizeHaventUpdateToUI>0){
                    dialog.setProgress(theSizeHaventUpdateToUI+arg1);
                    theSizeHaventUpdateToUI=0;
                }else{
                    dialog.updateProgress(arg1);
                }
            }
        }

    }
    private String replaceValue(String source, float size) {
        String replacedString = source;
        if (replacedString.contains("%size%")) {
            DecimalFormat format = new DecimalFormat("#0.00"); 
            String s=format.format(size);
            replacedString=replacedString.replace("%size%", s);
        }
        return replacedString;
    }
    // -------------------dialogs end-------------------------//
    
    // -------copy local asset file to client pack folder, just for local test use-----------//
    public void doCopyARAssets() throws IOException {
        File file = new File(getFilesDir().getPath()+"/AR");
        if (!file.exists()) {
            file.mkdirs();
        }
        doCopy("", getFilesDir().getPath()+"/AR/");
    }

    public void doCopyAssets() throws IOException {
        File file = new File(getFilesDir().getPath()+"/betta");
        if (!file.exists()) {
            file.mkdirs();
        }
        doCopy("", getFilesDir().getPath()+"/betta/");

    }
    
    private void doCopy(String dirName, String outPath) throws IOException {

        String[] srcFiles = assetManager.list(dirName);// for directory
        for (String srcFileName : srcFiles) {
            String outFileName = outPath + File.separator + srcFileName;
            String inFileName = dirName + File.separator + srcFileName;
//Log.e("filename:", srcFileName)    ;      
            if (dirName.equals("")) {// for first time
                inFileName = srcFileName;
            }
            try {
                InputStream inputStream = assetManager.open(inFileName);
                copyAndClose(inputStream, new FileOutputStream(outFileName));
            } catch (IOException e) {// if directory fails exception
                new File(outFileName).mkdir();
                doCopy(inFileName, outFileName);
            }

        }
    }
    
    private void doCopy2(String dirName, String outPath) throws IOException {

        String[] srcFiles = assetManager.list(dirName);// for directory
        for (String srcFileName : srcFiles) {
//Log.e("filename:", srcFileName)    ;      
            if (srcFileName.compareTo("images") == 0 || srcFileName.compareTo("sounds") == 0 || srcFileName.compareTo("webkit") == 0) {
                continue;
            }
                if(srcFileName.equals("plugins") || srcFileName.equals("phonegap.js") || srcFileName.equals("cordova.js") || srcFileName.equals("cordova_plugins.js")){
            String outFileName = outPath + File.separator + srcFileName;
            String inFileName = dirName + File.separator + srcFileName;
//Log.e("filename111111:", srcFileName)  ;      
            if (dirName.equals("")) {// for first time
                inFileName = srcFileName;
            }
            try {
                InputStream inputStream = assetManager.open(inFileName);
                copyAndClose(inputStream, new FileOutputStream(outFileName));
            } catch (IOException e) {// if directory fails exception
                new File(outFileName).mkdir();
                doCopy(inFileName, outFileName);
            }
                }

        }
    }
    

    public static void closeQuietly(OutputStream output) {
        try {
            if (output != null) {
                output.close();
            }
        } catch (IOException ioe) {
            // skip
        }
    }

    public static void closeQuietly(InputStream input) {
        try {
            if (input != null) {
                input.close();
            }
        } catch (IOException ioe) {
            // skip
        }
    }

    public static void copyAndClose(InputStream input, OutputStream output)
            throws IOException {
        copy(input, output);
        closeQuietly(input);
        closeQuietly(output);
    }

    public static void copy(InputStream input, OutputStream output)
            throws IOException {
        byte[] buffer = new byte[1024];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
    }
    /**
     * 动态获取设备NavigationBar的高度
     * @return
     */
    public int getNavigationBarHeight() {

        boolean hasMenuKey = ViewConfiguration.get(this).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        if (!hasMenuKey && !hasBackKey) {
            Resources resources = getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            //获取NavigationBar的高度
            int height = resources.getDimensionPixelSize(resourceId);
            return height;
        }
        else{
            return 0;
        }
    }
}
