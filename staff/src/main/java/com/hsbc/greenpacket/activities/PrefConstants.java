/*
 * COPYRIGHT. HSBC HOLDINGS PLC 2011. ALL RIGHTS RESERVED.
 *
 * This software is only to be used for the purpose for which it has been
 * provided. No part of it is to be reproduced, disassembled, transmitted,
 * stored in a retrieval system nor translated in any human or computer
 * language in any way or for any other purposes whatsoever without the
 * prior written consent of HSBC Holdings plc.
 */
package com.hsbc.greenpacket.activities;

/**
 * 
 */
public final class PrefConstants {
	public final static String PREFS_NAME = "GreenLaisee";
	public final static String LAST_MOD_KEY = "lastModified";
	public final static String UUID_KEY = "uuid";
	public final static String VERSION = "version";	
	

	public final static String CHECKSUN_KEY = "checksum";
	public final static String CHECKING_TIME_STAMP="checking_time_stamp";
	public final static String CHECKING_HOME_BANNER_TIME_STAMP="home_banner";
	public final static String CONFIG_CHECKING_TIME_STAMP="config_checking_time_stamp";
	public final static String CONFIGURATION="configuration";
	
	public final static String ENTITY_ID_KEY = "entity_id";
	public final static String ENTITY_SHORT_NAME_KEY = "entity_short_name";
	
	public final static String LOCALE = "locale";
	public final static String ENTITY_PATH = "entity_path";
	public final static String BACKUP_ENTITY_PATH = "backup_entity_path";
	
	public final static String CHECKSUM_LAST_MODIFIED = "checksumLastModified";
	
	//public final static String EULA_VERSION = "eula_version";
	public final static String EULA_APPVERSION = "eula_appVersion";
	
	public final static String USER_TYPE_MASS = "mass";
	public final static String USER_TYPE_ADVANCE = "advance";
	public final static String USER_TYPE_PREMIER = "premier";
    public final static String USER_TYPE_PB = "pvb";

	
	//CG start
    /**Tracy modified [26 Oct 13]*/
	public final static String MASS_IMAGE_PORTRAIT_2x = "mass_portrait_S.jpg";
	public final static String PVB_IMAGE_PORTRAIT_2x = "pb_portrait_S.jpg";
	public final static String PREMIER_IMAGE_PORTRAIT_2x = "premier_portrait_S.jpg";

	public final static String MASS_IMAGE_LANDSCAPE_2x = "mass_landscape_T.jpg";
	public final static String PVB_IMAGE_LANDSCAPE_2x = "pb_landscape_T.jpg";
	public final static String PREMIER_IMAGE_LANDSCAPE_2x = "premier_landscape_T.jpg";
	/**
	 * Constant class, private constructor
	 */
	private PrefConstants() {
		// TODO Auto-generated constructor stub
	}
	public final static String APPLICATION_TEME_ID = "AppCustomerTypeBackground";
	
	public final static String ORIENTATION_PORTRAIT = "portrait";
	public final static String ORIENTATION_LANDSCAPE = "landscape";
	public final static String IS_THEMECHANGE_ALLOWED ="is_theme_change";
	
	public final static String NETWORK_TYPE_REACHABLE_VIA_WWAN ="ReachableViaWWAN";
	public final static String NETWORK_TYPE_REACHABLE_VIA_WIFI = "ReachableViaWiFi";
	public final static String NETWORK_TYPE_NOTREACHABLE ="NotReachable";

	public final static String MASS_IMAGE = "mass_portrait.jpg";
	public final static String ADVANCE_IMAGE = "advance_portrait.jpg";
	public final static String PREMIER_IMAGE = "premier_portrait.jpg";
	
}
