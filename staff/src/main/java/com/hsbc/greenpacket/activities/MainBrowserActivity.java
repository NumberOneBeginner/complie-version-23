package com.hsbc.greenpacket.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.hsbc.greenpacket.hook.Hook;
import com.hsbc.greenpacket.listener.ShakeListener;
import com.hsbc.greenpacket.listener.ShakeListener.OnShakeListener;
import com.hsbc.greenpacket.util.ActivityUtil;
import com.hsbc.greenpacket.util.DeviceUtil;
import com.hsbc.greenpacket.util.EntityUtil;
import com.hsbc.greenpacket.util.HSBCHttpClient;
import com.hsbc.greenpacket.util.Header;
import com.hsbc.greenpacket.util.JSONConstants;
import com.hsbc.greenpacket.util.NameValueStore;
import com.hsbc.greenpacket.util.StringUtil;
import com.hsbc.greenpacket.util.UIUtil;
import com.hsbc.greenpacket.util.actions.BackToAppAction;
import com.hsbc.greenpacket.util.actions.CloseWebViewAction;
import com.hsbc.greenpacket.util.actions.HSBCURLAction;
import com.hsbc.greenpacket.util.actions.HSBCURLResolverUI15;
import com.hsbc.greenpacket.util.actions.HookConstants;
import com.hsbc.greenpacket.util.actions.IHsbcUrlAction;
import com.hsbc.greenpacket.util.actions.MailToAction;
import com.hsbc.greenpacket.util.actions.RegisterGestureListenerAction;
import com.hsbc.greenpacket.util.actions.RegisterLongPressListenerAction;
import com.hsbc.greenpacket.util.actions.RegisterLongPressListenerAction.LocationOfLongPress;
import com.hsbc.greenpacket.util.actions.RegisterShakeListenerAction;
import com.hsbc.greenpacket.util.actions.SetterAction;
import com.hsbc.greenpacket.util.web.HSBCWebClient;
import com.hsbc.greenpacket.util.web.HSBCWebTouchChromeClient;
import com.hsbc.greenpacket.view.CountDownFinishListener;
import com.hsbc.greenpacket.view.CountDownWidget;
import com.none.staff.R;

public class MainBrowserActivity extends HSBCActivity implements GestureDetector.OnGestureListener, View.OnTouchListener
{

	private static MainBrowserActivity theUniqueInstance;
	
	private final static String TAG="MainBrowserActivity";
    // -----------------------------------------------------------------------------------------private
    // member start
    private Timer mNewMessageBubbletimer;
    private static boolean pushLeft = true;
    private Map<String, String> additionalHeaders;
    private boolean menuIsShow = false;
    private ArrayList<HashMap<String, Object>> menuDataList;
    private Hook hook;
    private String savedLocale;
    private JSONObject moduleObj;
    private boolean webViewIsShow = false;
    private boolean isLogonFunc = false;
    private boolean logoned = false;
    private boolean hasMenu = false;
    private String firstURL = null;
    private String moduleId;
    private final static int ALERT_NEWEMAIL_MSG = 0;
    private double mMenuWeight = 0.75;
    private long webViewTimer = 0;
    private HSBCWebClient webClient1, webClient2, bannerWebClient,topWebviewWebClient;
    private HSBCWebTouchChromeClient chromeClient1, chromeClient2, bannerChromeClient,topWebviewChromeClient;
    // -------------------------------------------------------------------ui
    // control start
    private WebView wv1, wv2, currentWebView, topWebview;
    private WebView wvScmBanner;
    private ViewFlipper mViewFlipper;
    // -------------------------------------------------------------------ui
    // control end

    // -------------------------------------------------------------------popup
    // windows start
    private PopupWindow mPopupWindow;
    // -------------------------------------------------------------------popup
    // windows end
    /**
     * add by louischen for OOM
     */
    //private ImageView ImageViewBG;
    // -----------------------------------------------------------------------------------------private
    // member end

    // -----------------------------------------------------------------------------------------public
    // member start
    public final static int IMAGE_TASK_REF = 1;

    private Handler handler;
    // -----------------------------------------------------------------------------------------handle
    // end

  //For webView show
  	private Timer timer ;
  	private TimerTask timertask = null;

    /**
     * @author James M J Chen [5 July 13]
     * @description Gesture feature for post-logon
     * gestureDetector will deal with the onTouchEvent
     */
	private GestureDetector gestureDetector;

	private View touchedView;

    private boolean enableDrag = false;

    private boolean firstTimeEnableDrag = true;

    private boolean enableLongPress = false;
    private Dialog countDownTimerDialog = null;
    private boolean countDownFinished = false;
    //Tracy add for green packet
    private ShakeListener mShakeListener = null;
    private boolean isShakeRegistered = false;
    private boolean hasFocus=true;
    private Vibrator mVibrator;
    private SoundPool sp;
    private HashMap<Integer, Integer> spMap;
    private boolean isProductionMode=false;
    private ImageView ImageViewBG;
    
    // -----------------------------------------------------------------------------------------override
    // function start
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        setActivityStatus(HSBCActivity.START);
        initMainBrowser();

        /**Change the input mode,prevent the soft keyboard override the input field in some device which is Android 4.1 or above.**/
        DeviceUtil.setSoftInputMode(this);
    	theUniqueInstance = this;

        setupShakeListener();
    }

    private void setupShakeListener() {
        mVibrator = (Vibrator) getApplication().getSystemService(VIBRATOR_SERVICE);

        mShakeListener = new ShakeListener(this);
        mShakeListener.setOnShakeListener(new OnShakeListener() {
            public void onShake() {
                if (MainBrowserActivity.this.getActivityStatus()==HSBCActivity.START && hasFocus) {
                    mShakeListener.stop();
                    mVibrator.cancel();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            Log.d(TAG,"call shake");
                            playSound(3, 0);
                            mVibrator.vibrate(500);
                            //mShakeListener.start();
                            String eventJs=RegisterShakeListenerAction.getShakeEventJs();
                            if(!StringUtil.IsNullOrEmpty(eventJs)){
                                String js=HSBCURLAction.getCallbackJs(eventJs, null);
                                Log.d(TAG,"===onShake"+js);
                                MainBrowserActivity.this.currentWebView.loadUrl(js);
                            }
                        }
                    }, 200);
                }
            }
        });
        InitSound();
    }
    
    public void registerShakeListener(){
        isShakeRegistered=true;
        mShakeListener.start();
    }
    
    public void unregisterShakeListener(){
        isShakeRegistered=false;
        mShakeListener.stop();
    }
    public void InitSound() {
        sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        spMap = new HashMap<Integer, Integer>();
        spMap.put(1, sp.load(this, R.raw.shake_match, 1));
        spMap.put(2, sp.load(this, R.raw.shake_nomatch, 1));
        spMap.put(3, sp.load(this, R.raw.shake_sound_male, 1));

    }

    public synchronized void playSound(int sound, int number) {
        AudioManager am = (AudioManager) this
                .getSystemService(Context.AUDIO_SERVICE);
        float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volumnCurrent = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        float volumnRatio = volumnCurrent / audioMaxVolumn;

        sp.play(spMap.get(sound), volumnRatio, volumnRatio, 1, number,  1f);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // return createDefaultMenu(menu); 
        menu.add("menu");
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        // TODO Auto-generated method stub
        // Log.d(TAG,"onOptionsMenuClosed");
        super.onOptionsMenuClosed(menu);
    }

    /**
     * Sets up the webview to enable javascript and caching.
     */
    @Override
    protected void configureWebView(final WebView webview) {
        super.configureWebView(webview);
        webview.addJavascriptInterface(hook, "hook");
        // webview.clearHistory();
        // webview.clearCache(true);
        // webview.clearFormData();
    }

    @Override
    protected void onDestroy() {
        /*
         * JW [Mar-2013] SGH Timeout Re-auth
         * make sure the SessionHandlingUtil clean up all timers, listenerJS 
         * when the MainBrowserActivity is destroyed
         */
        Log.d(TAG,"MainBrowserActivity - onDestroy");
        if (mShakeListener != null) {
            mShakeListener.stop();
            mShakeListener=null;
        }
    	
        ActivityUtil.freeWebviewAndClient(this.wv1,this.webClient1,this.chromeClient1);
        wv1=null;
        ActivityUtil.freeWebviewAndClient(this.wv2,this.webClient2,this.chromeClient2);
        wv2=null;
        ActivityUtil.freeWebviewAndClient(this.wvScmBanner,this.bannerWebClient,this.bannerChromeClient);
        wvScmBanner=null;
        ActivityUtil.freeWebviewAndClient(this.topWebview,this.topWebviewWebClient,this.topWebviewChromeClient);
        topWebview=null;
        if(timer != null){
			this.timer.cancel();
			this.timer= null;
		}
        popuptimerFinish();
        popupFinish();
        recycleDialogResources();
        UIUtil.releaseBitmap(ImageViewBG);
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        Log.d(TAG,"============onNewIntent");
        super.onNewIntent(intent);
        initMainBrowser();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case HookConstants.ACTION_RESULT:
                if (data != null) {
                    String function = data.getStringExtra(HookConstants.FUNCTION);
                    IHsbcUrlAction action = HSBCURLResolverUI15.resolveByFunction(function);
                    if (action != null) {
                        action.onActionResult(this, this.handler, resultCode, data);
                    } else {
                        Log.e(TAG,"Hook API not support!:"+function);
                    }
                }else{
                    Log.e(TAG,"Data intent is neccessary!!");
                }
                break;
        }
    }

    // -----------------------------------------------------------------------------------------popup
    // windows start
    private class initPopupWindow extends TimerTask {
        @Override
        public void run() {
            Message message = Message.obtain();
            message.what = ALERT_NEWEMAIL_MSG;
            handler.sendMessage(message);
        }

    }


    private void popupFinish() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            mPopupWindow = null;
        }
    }

    private void popuptimerFinish() {
        if (mNewMessageBubbletimer != null) {
            mNewMessageBubbletimer.cancel();
            mNewMessageBubbletimer = null;
        }
    }

    // -----------------------------------------------------------------------------------------popup
    // windows end

    private void setAnimation(ViewFlipper vf, int slide) {
        switch (slide) {
            case HookConstants.SLIDE_L_MSG:
                setPushLeftAnim(vf);
                break;
            case HookConstants.SLIDE_R_MSG:
                setPushRightAnim(vf);
                break;
        }
    }

    private OnItemClickListener onMenuItemClick = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
            try {
            } catch (Exception e) {
                Log.e(TAG,"Click menu list error", e);
            }
        }
    };
    /**
     * back button on page. javascript:try
     * {balances.inst.goBack();}catch(e){window.hook.sendMsg(10)}"
     */
    private void handleSoftBackButton() {
        StringBuffer script = new StringBuffer();
        script.append("javascript:try {");
        script.append(getString(R.string.page_back_func));
        script.append("}catch(e){");
        script.append("window.").append(HookConstants.HOOK_OBJECT).append(".sendMsg(").append(HookConstants.NO_SUCH_JAVASCRIPT)
            .append(")");
        script.append("}");
        currentWebView.loadUrl(script.toString());
        //showDialog(Constants.CLOSE_APPALERT_DIALOG);
    }
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        this.hasFocus=hasFocus;
    }

    /**
     * common web module button button on device
     */
    private void commonWebBackButtonHandle() {
        if (currentWebView == null) {
            finish();
        }
        String currentURL = currentWebView.getUrl();
        if (currentURL != null && currentURL.indexOf("#") != -1) {
            currentURL = currentURL.substring(0, currentURL.indexOf("#"));
        }
        if (this.currentWebView.canGoBack() && (this.firstURL != null && !this.firstURL.equals(currentURL))) {
            // Log.d(TAG,"currentWebView can go back:"+currentWebView.getUrl());
            loadurl(this.currentWebView, this.firstURL);
            currentWebView.clearHistory();
        } else {
            finish();
            this.slideLeftToRight();
        }
    }

    
    private void setCookie(String url, Bundle cookieBundle) {
        if (cookieBundle == null) {
            return;
        }
        try {
            CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(this);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            /**** set httpclient cookie ****/
            // HSBCHttpClient client=HSBCHttpClient.getInstance(this);

            String cookiesdomain = cookieBundle.getString(Constants.COOKIES_DOMAIN);
            Bundle unitBundle = cookieBundle.getBundle(Constants.COOKIES);
            if (unitBundle == null) {
                return;
            }
            Set<String> keys = unitBundle.keySet();
            Iterator<String> it = keys.iterator();
            while (it.hasNext()) {
                StringBuffer sb = new StringBuffer();
                String key = it.next();
                String value = unitBundle.getString(key);
                sb.append(key).append("=").append(value).append(";").append(Constants.DOMAIN).append("=").append(cookiesdomain)
                    .append(";path=/");
                cookieManager.setCookie(url, sb.toString());
                /**** set httpclient cookie ****/
                // client.setCookie(cookiesdomain, key, value);
            }
            cookieSyncManager.sync();
        } catch (Exception e) {
            Log.e(TAG,"set cookie error!", e);
        }
    }


    // -----------------------------------------------------------------------------------------private
    // function end

    // -----------------------------------------------------------------------------------------public
    // function start
    public void initMainBrowser() {
        setContentView(R.layout.menulayout);
        /** 
        * @author CapGemini  
        * @description
        * BG related Changes to set the background 
        **/ 
        /**Tracy modified for Proposition Image[26 Oct 13]*/
//        ImageViewBG = (ImageView) findViewById(R.id.webviewimage);
//        CustomerSegmentUtil.setBackgroundImage(ImageViewBG,this);
		//CG end
        createHandler();
        hook = new Hook(this, handler);
        wv1 = (WebView) findViewById(R.id.webview1);
        wv2 = (WebView) findViewById(R.id.webview2);
        initWebView1(wv1);
        initWebView2(wv2);
        
        //Karson Li [12-Dec-2013] for long pressed
        enableWebviewLongClick(wv1);
        enableWebviewLongClick(wv2);
        
        transition();
        currentWebView = wv1;
        wv1.setOnTouchListener(this);
        wv2.setOnTouchListener(this);
        
        Header header = new Header();
        this.additionalHeaders = header.createHeaders(this);
        
        Bundle bundle = getIntent().getExtras();
        this.firstURL = bundle.getString(Constants.INTENT_URL_KEY);//
        String scaInfo=bundle.getString(Constants.SCA_INFO);
        moduleId = bundle.getString(Constants.INTENET_MODULE_ID);
        this.isLogonFunc = bundle.getBoolean(Constants.INTENT_ISLOGONFUNC_KEY, false);
        
        if(!StringUtil.IsNullOrEmpty(scaInfo)){
            try {
                JSONObject jobject=new JSONObject(scaInfo);
                scaInfo=jobject.toString();
                SetterAction.save(Constants.CUSTOMER_INFO, scaInfo, this);
            } catch (Exception e) {
                Log.e(TAG, "Get Message error",e);
                Toast.makeText(this, "Green Laisee Error", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        Log.d(TAG, ""+firstURL);
        loadurl(currentWebView, this.firstURL);
        //Tracy disable webview time out feature
        //setWebViewTimeOut(regionalConfig);
    }
    
    

    public void createHandler() {
        handler = new Handler() {
            public void handleMessage(Message msg) {
                if (!Thread.currentThread().isInterrupted()) {
                    switch (msg.what) {
                        case HookConstants.SHOW_PROGRESS_MSG:
                            showProgressDialog();
                            break;
                        case HookConstants.HIDE_PROGRESS_MSG:
                            hideProgressDialog();
                            break;
                        case ALERT_NEWEMAIL_MSG:// remove the object of
                                                // mHandler
                            // when the time over the duration,close the bubble
                            popupFinish();
                            break;
                        case HookConstants.SLIDE_L_MSG:
                            setAnimation(mViewFlipper, HookConstants.SLIDE_L_MSG);
                            break;
                        case HookConstants.SLIDE_R_MSG:
                            setAnimation(mViewFlipper, HookConstants.SLIDE_R_MSG);
                            break;
                        case HookConstants.HOOK_ERROR:
                            currentWebView.loadUrl(HSBCURLAction.gethookAPIFailCallScript());
                            break;
                        case HookConstants.NO_SUCH_JAVASCRIPT:
                            Log.d(TAG,"javascript not difine");
                            break;
                        case HookConstants.EXECUTE_JAVASCRIPT:
                            /**If action need execute javascript then can send message to MainBrowser and execute it**/
                            executeJsInWebview(MainBrowserActivity.this.currentWebView,msg.getData());
                            break;
                        case HookConstants.SWITCH_WEBVIEW:
                            /***Switch webview hook api will send a message to UI thread to triggle it**/
                            switchWebview();
                            break;
                        case HookConstants.SWITCH_LOCALE:
                            /***Switch webview hook api will send a message to UI thread to triggle it**/
//                            toggleAppLanguageWhenServerDone(msg.getData());
                            break;
                        case HookConstants.ANIMATION_BACK_MSG:
                            slideLeftToRight();
                            break;
                        case HookConstants.LOGON_LOADING_FINISH_MSG:
    						showWebView();
    						break;
                        case HookConstants.PAGE_TRANSITION_MSG:
                            Bundle data= msg.getData();
                            if(data!=null){
                                String url=data.getString(Constants.URL);
                                currentWebView.loadUrl(url);
                            }
                            break;
                    }
                }
                if (msg != null) {
                    super.handleMessage(msg);
                }
            }
        };
    }

	/**
	 * @author [Karson Mar-06-2013]
	 * for show webView that is invisible
	 */
	private void showWebView() {
		if(currentWebView.getVisibility() == View.INVISIBLE){
			currentWebView.setVisibility(View.VISIBLE);
			webViewIsShow = true;
		}
	}
	
    public void hideProgressDialog() {
        super.hideProgressDialog();
    }

    public void transition() {
        mViewFlipper = (ViewFlipper) findViewById(R.id.details);
        mViewFlipper.setPersistentDrawingCache(ViewGroup.PERSISTENT_ALL_CACHES);
    }

    public void setPushLeftAnim(ViewFlipper vf) {
        if (!pushLeft) {
            vf.setInAnimation(getApplicationContext(), R.anim.push_left_in);
            vf.setOutAnimation(getApplicationContext(), R.anim.push_left_out);

            pushLeft = true;
        }
    }

    public void setPushRightAnim(ViewFlipper vf) {
        if (pushLeft) {
            vf.setInAnimation(getApplicationContext(), R.anim.push_right_in);
            vf.setOutAnimation(getApplicationContext(), R.anim.push_right_out);
            pushLeft = false;
        }
    }

    public boolean commomShouldOverrideUrlLoading(final WebView view, final String url) {
        // Log.d(TAG,"shouldOverrideUrlLoading.." + url);
        if (hookHandle(view, url)) {
            return true;
        }
        loadurl(view, url);
        return true;
    }

    /**
     * Judge if the url start with hsbc:,if true click it and dispaly on right
     * WebView page if false click it and then No response
     * 
     * @author 43734332 Cherry 2012-11-19
     * @param view
     * @param url
     * @return boolean
     */
    public boolean specificShouldOverrideUrlLoading(final WebView view, final String url) {
        // Log.d(TAG,"shouldOverrideUrlLoading.." + url);
        if (hookHandle(view, url)) {
            return true;
        }
        return true;
    }
    public boolean hookHandle(WebView webview, String url) {
        // HSBC prefix handling.
        try {
            if (url.startsWith(HookConstants.HSBC_URL_PREFIX)) {
                Log.d(TAG,"===========================hook url:"+url);
                HSBCURLAction action = HSBCURLResolverUI15.resolve(url);
                if (action != null) {
                    synchronized (this) {
                        if (url.indexOf(Constants.PAGE_TRANSATION) != -1) {
                            isShakeRegistered=false;
                            WebView secWebview;
                            if (currentWebView == wv1) {
                                // Log.d(TAG,"currentWebView:webview1");
                                secWebview = wv2;
                            } else {
                                // Log.d(TAG,"currentWebView:webview2");
                                secWebview = wv1;
                            }
                            secWebview.clearHistory();
                            action.execute(this, secWebview, hook);
                            // this.overridePendingTransition(R.anim.page_in_rightleft,
                            // R.anim.page_out_rightleft);
                            this.slideRightToLeft();
                        } else {
                            action.execute(this, webview, hook);
                            if (action instanceof BackToAppAction||action instanceof CloseWebViewAction) {
                                // this.overridePendingTransition(R.anim.page_in_leftright,
                                // R.anim.page_out_leftright);
                                this.slideLeftToRight();
                            } else {
                                // this.overridePendingTransition(R.anim.page_in_rightleft,
                                // R.anim.page_out_rightleft);
                                this.slideRightToLeft();
                            }
                        }
                    }
                } else {
                    // TODO: Error message for unknown action to the user?
                    //webview.loadUrl(HSBCURLAction.gethookAPIFailCallScript());
                    Log.e(TAG,"Unable to call hook "+ url);
                }
                return true;
            } else if (url.startsWith(HookConstants.TEL)) {
                return ActivityUtil.startCall(this, url);
            } else if (url.startsWith(HookConstants.MAIL_TO)) {
                MailToAction action = new MailToAction();
                action.execute(this, url);
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG,"hook handle error!", e);
        }
        return false;
    }

    public void initWebView1(WebView wv) {
    	// Porject Cobra - deprecated since Google API4.2
    	//WebView.enablePlatformNotifications();
    	
        configureWebView(wv);
        this.webClient1 =
            new HSBCWebClient(this, Constants.PROGRESS_DIALOG, Constants.NO_CONNECTION_RETRY_DIALOG, Constants.INVALID_VER_DIALOG) {

                public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                    return commomShouldOverrideUrlLoading(view, url);
                }

                public void onLoadResource(WebView view, String url) {
                    // TODO Auto-generated method stub
                    // Log.d(TAG,"loading resource.."+url);
                    super.onLoadResource(view, url);
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
 					// remain blank
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    // super.onPageFinished(view, url);
                }

            };
        chromeClient1 = new HSBCWebTouchChromeClient(this) {
            public void onProgressChanged(WebView view, int progress) {//
                super.onProgressChanged(view, progress);
                if (progress == 100) {
                    handler.sendEmptyMessage(HookConstants.HIDE_PROGRESS_MSG);
                    if(mViewFlipper.getDisplayedChild()!=0){
                        currentWebView = wv1;
                        mViewFlipper.setDisplayedChild(0);
                        wv1.requestFocus(View.FOCUS_DOWN);
                    }
                }
            }
        };
        wv.setWebViewClient(webClient1);
        wv.setWebChromeClient(chromeClient1);
        
        /**
         * @author James M J Chen [5 July 13]
         * @description Gesture feature for post-logon
         * Set the on-touch-listerner for webview
         */
    }
    public void switchWebview(){
        setAnimation(mViewFlipper, HookConstants.SLIDE_R_MSG);
        if (MainBrowserActivity.this.mViewFlipper.getDisplayedChild() != 0) {
            currentWebView = wv1;
            mViewFlipper.setDisplayedChild(0);
        }else if (MainBrowserActivity.this.mViewFlipper.getDisplayedChild() != 1) {
            currentWebView = wv2;
            mViewFlipper.setDisplayedChild(1);
        }
        
    }
    public void initWebView2(WebView wv) {

        configureWebView(wv);
        webClient2 =
            new HSBCWebClient(this, Constants.PROGRESS_DIALOG, Constants.NO_CONNECTION_RETRY_DIALOG, Constants.INVALID_VER_DIALOG) {
                public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                    return commomShouldOverrideUrlLoading(view, url);
                }

                public void onLoadResource(WebView view, String url) {
                    // TODO Auto-generated method stub
                    // Log.d(TAG,"loading resource.."+url);
                    super.onLoadResource(view, url);
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    // remain blank
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    // super.onPageFinished(view, url);
                }
            };
        chromeClient2 = new HSBCWebTouchChromeClient(this) {
            public void onProgressChanged(WebView view, int progress) {
                super.onProgressChanged(view, progress);
                if (progress == 100) {
                    handler.sendEmptyMessage(HookConstants.HIDE_PROGRESS_MSG);
                    if(mViewFlipper.getDisplayedChild() != 1) {
                        currentWebView = wv2;
                        mViewFlipper.setDisplayedChild(1);
                        wv2.requestFocus(View.FOCUS_DOWN);
                    }
                }
            }
        };
        wv.setWebViewClient(webClient2);
        wv.setWebChromeClient(chromeClient2);

        /**
         * @author James M J Chen [5 July 13]
         * @description Gesture feature for post-logon
         * Set the on-touch-listerner for webview
         */
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            
            handleSoftBackButton();
            //this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void initPopupWin() {

    }

    public void cleanMenuList() {
        if (!this.hasMenu) {
            return;
        }  
        if(menuDataList!=null&&menuDataList.size()>0){
        	menuDataList.clear();
        }
//        if (mAdapter != null) {
//            mAdapter.clearContent();
//            mAdapter.updateList(menuDataList);
//            updateLogonInfo("");
//            maskView.setVisibility(View.INVISIBLE);
//            //menuIsShow = false;
//            
//        }
    }
    public void initTopWebview(){
        LinearLayout panel = (LinearLayout)this.findViewById(R.id.topWebviewPanel);
        this.topWebview = new WebView(this);
        topWebview.setBackgroundColor(0);
        //topWebview.setVisibility(View.INVISIBLE);
        panel.setVisibility(View.INVISIBLE);
        panel.addView(topWebview);
        configureWebView(topWebview);
        this.topWebviewWebClient =
            new HSBCWebClient(this, Constants.PROGRESS_DIALOG, Constants.NO_CONNECTION_RETRY_DIALOG, Constants.INVALID_VER_DIALOG) {
                public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                    return commomShouldOverrideUrlLoading(view, url);
                }              
            };
        this.topWebviewChromeClient = new HSBCWebTouchChromeClient(this);
        topWebview.setWebViewClient(topWebviewWebClient);
        topWebview.setWebChromeClient(topWebviewChromeClient);
        //this.topWebview.setVisibility(View.VISIBLE);
        
    }

    public void loadurl(final WebView view, final String url) {
        hook.setWebview(view);
        handler.sendEmptyMessage(HookConstants.SHOW_PROGRESS_MSG);
        view.loadUrl(url, MainBrowserActivity.this.additionalHeaders);
    }
    public void loadurlWithoutLoading(final WebView view, final String url) {
        hook.setWebview(view);
        view.loadUrl(url, MainBrowserActivity.this.additionalHeaders);
    }
    public void getWebViewCookie() {
        CookieManager cookieManager = CookieManager.getInstance();
        Log.d(TAG,"cookie=" + cookieManager.getCookie(firstURL));
    }

    public String getHttpClientCookie(String name, String domain) {
        HSBCHttpClient client = HSBCHttpClient.getInstance(this);
        return client.getCookie(name, domain);
    }

    public String getStoredValue(String key) {
        if (key == null) {
            return null;
        }
        String eid = EntityUtil.getSavedEntityId(this);
        if (eid != null) {
            NameValueStore store = new NameValueStore(this);
            return store.getAttribute(key);
        }
        return null;
    }

    /**
     * for Web hook
     * 
     * @param message
     */
    public void displayLoadingByWeb(String message) {
        // pd.setMessage(message);
        handler.sendEmptyMessage(HookConstants.SHOW_PROGRESS_MSG);
    }

    public void hideLoadingByWeb() {
        handler.sendEmptyMessage(HookConstants.HIDE_PROGRESS_MSG);
    }

    public void setStoredValue(String key, String value) {
        String eid = EntityUtil.getSavedEntityId(this);
        if (eid != null) {
            NameValueStore store = new NameValueStore(this);
            store.setAttribute(key, value);
        }
    }

    public void setTimeOut(JSONObject personalJsonObj) {
        try {
            HSBCHttpClient client = HSBCHttpClient.getInstance(this);
            if (personalJsonObj.getString(JSONConstants.TYPE).equals("web")) {
                client.setTimeOut(personalJsonObj.getInt("proxyApiTimeout"));
            }
        } catch (Exception e) {
            Log.e(TAG,"Get proxyApiTimeout Json error", e);
        }
    }


    @Override
    protected void recycleDialogResources() {
        
    	//Added by Tracy
        removeDialog(Constants.COUNT_DOWN_TIMER_DIALOG_INDEX);
    	
        super.recycleDialogResources();
    }

    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        // TODO Auto-generated method stub
        switch (id) {
        	case Constants.COUNT_DOWN_TIMER_DIALOG_INDEX:
        	    return createCountDownTimerDialog();
        }
        return super.onCreateDialog(id, args);

    }
    
    @Override 
    protected void onPrepareDialog(int id, Dialog dialog){
        switch (id) {
            case Constants.COUNT_DOWN_TIMER_DIALOG_INDEX:
                CountDownWidget countDownWidget = (CountDownWidget) dialog.findViewById(R.id.count_down_widget);
                countDownWidget.resetCountDownTimer();
        }
    }

    protected Dialog createCountDownTimerDialog() {
        final Dialog dialog = new Dialog(this, R.style.LightProgressSpinner);
        dialog.setContentView(R.layout.count_down_spinner);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        final CountDownWidget countDownWidget = (CountDownWidget) dialog.findViewById(R.id.count_down_widget);
        countDownWidget.setCountDownFinishListener(new CountDownFinishListener() {
            @Override
            public void onCountDownFinish() {
                Log.d(TAG,"onCountDownFinish");
                Handler handler= new Handler();
                handler.postAtTime(new Runnable(){
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                }, 10);
                
                String longPressEventJs = RegisterLongPressListenerAction.getEventJs();
                if(!StringUtil.IsNullOrEmpty(longPressEventJs)){
                    currentWebView.loadUrl(HSBCURLAction.getCallbackJs(longPressEventJs, ""));
                }
            }
        });
        return dialog;
    }

    // -----------------------------------------------------------------------------------------public
    // function end

    // ---------------------------------------------------------------------add
    // by june for soft otp function.


    // -----------------------------------------------------------------------end
    // add by june for soft otp function.
    

	@Override
	protected void onStop() {
		Log.d(TAG,"MainBrowserActivity - onStop");
		super.onStop();

        setActivityStatus(HSBCActivity.STOP);
        if(isShakeRegistered){
            this.mShakeListener.stop();
        }
	}
	@Override
    protected void onPause() {
	    super.onPause();
        setActivityStatus(HSBCActivity.PAUSE);
	}

	@Override
	protected void onRestart() {
		Log.d(TAG,"MainBrowserActivity - onRestart");
		setActivityStatus(HSBCActivity.START);

        super.onRestart();
        //considering it may open other activity from browser, just comment the config expire logic for temperary 
//		if(GetRegionConfigTask.configurationExpired(this,"0")){
//            Intent intent=new Intent(this, Sp.class) ;
//            startActivity(intent);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//            overridePendingTransition(0, 0);
//        }else{
    		if(isShakeRegistered){
    		    this.mShakeListener.start();
    		}
//        }
	}
	
	/*
	 * JW [Mar-2013] SGH Timeout Re-auth
	 * Promote re-usability of MainBrowserActivity by Singleton
	 */
	public static boolean isLogon() {
		if (theUniqueInstance != null) {
			return theUniqueInstance.logoned;
		} else {
			return false;
		}
	}


    /**
     * This method is for Hook action
     * @author York Y K LI[Jan 15, 2013]
     * @param webview
     * @param mBundle
     *
     */
    public void executeJsInWebview(final WebView webview,Bundle mBundle){
        if (mBundle != null && mBundle.getString(HookConstants.MESSAGE_DATA) != null) {
            String script = mBundle.getString(HookConstants.MESSAGE_DATA);
            boolean setHeader = mBundle.getBoolean(HookConstants.SET_HEADER);
            Log.d(TAG,"===============setHeader:"+setHeader+" "+script);
            if(!StringUtil.IsNullOrEmpty(script)){
                if(setHeader){
                    loadurl(this.currentWebView,script);
                }else{
                    webview.loadUrl(script);
                }
            }
        }
    }
    
  

	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

   /**
     * @author James M J Chen [5 July 13]
     * @description Gesture feature for post-logon
     * Function to deal with onFling event
     */ 

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {

		if(Math.abs(e1.getX()-e2.getX())<Math.abs(e1.getY()-e2.getY())){
		    if (e1.getY() > e2.getY()) {
		        Log.d(TAG,"up fling");
		    }else {
		        Log.d(TAG,"down fling");
		    }
		}else{
			String eventJs = RegisterGestureListenerAction.getRegisterGestureListenerEventjs();
		    if (e1.getX() > e2.getX()) {
		        Log.d(TAG,"left fling");
		        if(!StringUtil.IsNullOrEmpty(eventJs)){
		        	loadurlWithoutLoading(currentWebView, HSBCURLAction.getCallbackJs(eventJs,"left"));
		        }
	        } else {
	            Log.d(TAG,"right fling");
	            if(!StringUtil.IsNullOrEmpty(eventJs)){
	            	loadurlWithoutLoading(currentWebView, HSBCURLAction.getCallbackJs(eventJs,"right"));
		        }
	        }
		}

		return false;
	}

	



	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

    /**
     * @author James M J Chen [5 July 13]
     * @description Gesture feature for post-logon
     * Function to deal with onShowPress event
     */

	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}


	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}
	
    /**
     * @author James M J Chen [5 July 13]
     * @description Gesture feature for post-logon
     * Function to deal with onTouch event, we dispatch this event to gestureDetector to separate to more detail sub-event to deal with
     */

	public boolean onTouch(View v, MotionEvent event) {		
        //TODO set flag
		if(enableDrag){
		  touchedView = v;
		  gestureDetector.onTouchEvent(event);
		}
		//for long press listener
		switch(event.getAction()){
            case MotionEvent.ACTION_DOWN: {
                if (enableLongPress) {
                    Log.d(TAG,"Activity onTouched down");
                    LocationOfLongPress localtion = RegisterLongPressListenerAction.getLocation();
                    if (null != localtion) {
                        Log.d(TAG,"onTouch" + localtion.toString());
                        float topleft_X = localtion.getTopleft_x();
                        float topleft_Y = localtion.getTopleft_y();
                        float bottomright_X = localtion.getBottomright_x();
                        float bottomright_Y = localtion.getBottomright_y();
                        Log.d(TAG, event.getX()+" "+event.getY());
                        if (topleft_X <= event.getX() && event.getX() <= bottomright_X && topleft_Y <= event.getY()
                            && event.getY() <= bottomright_Y) {
                            countDownTimerDialog = createCountDownTimerDialog();
                            countDownTimerDialog.show();
                        }
                    }
                }
            }
                break;
            case MotionEvent.ACTION_UP: {
                if (enableLongPress) {
                    Log.d(TAG,"Activity onTouched up");
                    if (countDownTimerDialog != null) {
                        countDownTimerDialog.dismiss();
                        countDownTimerDialog = null;
                    }
                }
            }
            case MotionEvent.ACTION_MOVE:{
                if (enableLongPress) {
                    Log.d(TAG,"Activity onTouched move");
                    if (countDownTimerDialog != null) {
                        LocationOfLongPress localtion = RegisterLongPressListenerAction.getLocation();
                        if (null != localtion) {
                            Log.d(TAG,"onTouch" + localtion.toString());
                            float topleft_X = localtion.getTopleft_x();
                            float topleft_Y = localtion.getTopleft_y();
                            float bottomright_X = localtion.getBottomright_x();
                            float bottomright_Y = localtion.getBottomright_y();
                            if (topleft_X <= event.getX() && event.getX() <= bottomright_X && topleft_Y <= event.getY()
                                && event.getY() <= bottomright_Y) {
                            }else{
                                countDownTimerDialog.dismiss();
                                countDownTimerDialog = null;
                            }
                        }
                    }
                }
            }
                break;
        }
        return false;
	}

	public void setEnableDrag(boolean enableDrag) {
		// do not permit to enable gesture feature when not logoned

		// ensure dragable only after finishing loading resource
		
		if(enableDrag){
			Thread enableDragThread = new Thread(){
				public void run(){
					try{
						synchronized(MainBrowserActivity.this){
							if(firstTimeEnableDrag){
							  Thread.sleep(2000);
							  firstTimeEnableDrag = false;
							}
							else{
							  Thread.sleep(1000);
							}
							MainBrowserActivity.this.enableDrag = true;
						}
					}
					catch(Exception ex){
						Log.e(TAG,"Drag menu fail");
					}
				}
			};
				
			enableDragThread.start();
		}
		else{
			synchronized(MainBrowserActivity.this){
				this.enableDrag = enableDrag;
			}
		}
		
	}	 
	/**
	 * @author Karson Li [14-Dec-2013]
	 * @param enableLongPress
	 */
	public void setEnableLongPress(boolean enableLongPress){
		this.enableLongPress = enableLongPress;
		enableWebviewLongClick(currentWebView);
	}
	/**
	 * @author Karson Li [14-Dec-2013]
	 * @param enableLongPress
	 */
	public void enableWebviewLongClick(WebView webview){
		webview.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View arg0) {
			    return enableLongPress;
			}
		});
	}
	


    public void onLongPress(MotionEvent arg0) {
        // TODO Auto-generated method stub
        
    }
}
