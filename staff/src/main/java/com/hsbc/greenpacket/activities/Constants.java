package com.hsbc.greenpacket.activities;

public class Constants {
	public final static String PAGE_TRANSATION = "PageTransition";
//	public final static int SLIDE_L=1;
//	public final static int SLIDE_R=2;
	//Cherry [2013-08-30]
	public final static String BARCODE_ON_MOBILE = "mobile";
	public final static String BARCODE_ON_TABLET = "tablet";
	public final static String HOMETYPE_WEB="web";
	public final static String HOMETYPE_APP="app";
	
	public final static String URL="url";
	public final static String INTENT_URL_KEY = "url";
	public final static String INTENT_SHORTNAME_KEY="shortName";
	public final static String INTENT_ISLOGONFUNC_KEY = "isLogonFunc";
	public final static String INTENT_COOKIE_KEY = "cookie";
	public final static String INTENET_MODULE_ID = "moduleId";
	public final static String DOMAIN = "domain";
	public final static String EXPIRES = "expires";
	public final static String COOKIES_DOMAIN = "cookiesdomain";
	public final static String COOKIES = "cookies";
	public final static String MOBILE_SUPPORT_TYPE_VALUE = "1";
	
	public final static String MOBILE_SUPPORT_TYPE = "mobile_support_type";
	public final static String DEVICE_ID_COOKIE_NAME = "device-id";
	public final static String DEVICE_TYPE_COOKIE_NAME = "device-type";
	public final static String NATIVE_APP_COOKIE_NAME = "native-app";
	public final static String VERSION_PREFIX = "-v";
	public final static String BACK_TO_APP_COOKIE_NAME = "back-to-app-id";
	public final static String BACK_TO_APP_COOKIE_NAME_VALUE = "home";
	
	public final static String REQUEST_POST="POST";
	public final static String REQUEST_GET="GET";
	public final static String REQUEST_PUT="PUT";
	public final static String REQUEST_DELETE="DELETE";
	public final static String CONTENT_TYPE="application/json";
	public final static String ALGORITHM = "SHA-512";

	
	public final static String ENTITIES="entities";
	public final static String LAN_LIST="lanList";
	public final static String GLOBAL="global";
	
	public final static int PROGRESS_DIALOG = 0;
	public final static int NO_CONNECTION_RETRY_DIALOG = 1;
	public final static int INVALID_VER_DIALOG = 3;
	public final static int APP_ERROR_DIALOG = 4;
	public final static int NETWORK_ERROR_DIALOG = 5;
	public final static int APP_NOT_SUPPORT = 6;
	/**
	 * add Dialog references
	 */
	public final static int CLOSE_APPALERT_DIALOG = 7;
	public final static int NETWORK_ERROR_SELECT_COUNTRY_DIALOG = 8;
	public final static int RESOURCE_UPDATE_DIALOG = 9;
	public final static int DEVICE_SETTIONG_WARNING_DIALOG = 10;
	//added by Tracy for LoadDetailsWebView error dialog [18-Sep_2013]
    public final static int LOADING_ERROR_DIALOG_INDEX = 11;
    public final static int COUNT_DOWN_TIMER_DIALOG_INDEX = 12;


	/* Project Cobra
	 * JW [Aug-2013]
	 * Integration with NFC Update Service with post-logon         
	 */
    public static final int NFC_UPDATE_SERVICE_POP_UP_LOGOFF_CONFIRM = 98;
    
	public final static String ANDROID="android";
	public final static String ANDROID_Capital="Android";
	
	public final static int SSO_OPEN_NEW_URL = 111;
	//SSO ERROR TYPE
	public final static String SSO_ERROR = "P003";
	public final static String SSO_ERROR_ENTITY_NOT_FIND = "P003";
	public final static String SSO_ERROR_ENTITY_PARSER_EXCPETION = "P003";
	public final static String SSO_ERROR_CONFIG_PARSER_EXCPETION = "P003";
	public final static String SSO_ERROR_NETWORK_EXCPETION = "P001";
	public final static String SSO_SUCCESS = "0000";
	//absl
	public final static int ABSL_MAP_STANDARD_MODE = 0;
	public final static int ABSL_MAP_STATELITE_MODE = 1;
	
	//HSBCActivity
	public static final String COUNTRY_SELECTOR_PARAM_NAME="hideCancelButton";
	public static final String COUNTRY_SELECTOR_PARAM_VALUE="ture";
	
	/**
     * Constant for {@link #importance}: this process does not running
     */
    public static final int IMPORTANCE_NOT_RUNNING = 0;
    
    public static final String FIRST_LAUNCH = "firstLaunch";
    /**
     * For bar code
     * Cherry [2013-08-19]
     */
    public final static String SCAN_ISBN = "scanIsbn";

    	/** 
         * @author CapGemini  
         * @description
         * Special Annountment & Warning Updated related constents 
         **/ 
    public final static String SPECIALANNSHOWMSG = "specialAnnDntShowMsgCheck";
    public final static String SPECIALANNVERSION = "specialAnnVersion";
    public final static String APPUPDATEVERSION = "appUpdateVersion";
    public final static String WARNINGUPDATETITLE = "warningUpdateTitle";
    public final static String WARNINGUPDATEMESSAGE = "warningUpdateMessage";
    public final static String WARNINGUPDATEPOSITIVE = "warningUpdatePositiveButton";
    public final static String WARNINGUPDATENEGA = "warningUpdateNegativeButton";
    public final static String WARNINGUPDATELINK = "warningUpdateLink";
    public final static String SPECIALANNOCHECKBOX = "specialAnnouncementCheckbox";
    public final static String SPECIALANNOCLOSE = "specialAnnouncementCloseButton";
    public final static String SPECIALANNOMESSAGE = "specialAnnouncementMessage";
    public final static String SPECIALANNOCTITLE = "specialAnnouncementTitle";
    //CG end


    public final static String SCA_INFO = "ScaInfo";
    public final static String SECURE_TOKEN = "secureToken";
    public final static String CUSTOMER_INFO = "customerInfo";
    
    public final static String ONBOARDING_PAGE_SHOWED = "onboardingPageShowed";
}
