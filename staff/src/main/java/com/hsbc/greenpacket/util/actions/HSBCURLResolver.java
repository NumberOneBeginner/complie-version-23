/*
 * COPYRIGHT. HSBC HOLDINGS PLC 2011. ALL RIGHTS RESERVED.
 *
 * This software is only to be used for the purpose for which it has been
 * provided. No part of it is to be reproduced, disassembled, transmitted,
 * stored in a retrieval system nor translated in any human or computer
 * language in any way or for any other purposes whatsoever without the
 * prior written consent of HSBC Holdings plc.
 */
package com.hsbc.greenpacket.util.actions;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

/**
 * This class is used to resolve a given HSBC url to its related action. These URL's can be defined thought the HSBC Mobile web app, and
 * will used the protocol definition of hsbc: to differentiate itself from normal URL links.
 * 
 */
public class HSBCURLResolver {
	/**
	 * Holds the available action strategies.
	 */

	private static class StrategiesHolder {

	}

	private final static Map<String, HSBCURLAction> actionStrategies = new HashMap<String, HSBCURLAction>();
    private static final String TAG = "HSBCURLResolver";
	static {
		HSBCURLResolver.actionStrategies.put(HookConstants.GETTER, new GetterAction());
		HSBCURLResolver.actionStrategies.put(HookConstants.SETTER, new SetterAction());
	}
	
	public static HSBCURLAction resolve(final String url) {
		return HSBCURLResolver.resolve(url,HSBCURLResolver.actionStrategies);
	}
	
    /**
     * Add resolveByFunction to get the action instance to execute the
     * dataProcess method
     * 
     * @author York
     * @param function
     * @return
     */
    public static IHsbcUrlAction resolveByFunction(final String function) {
        HSBCURLAction action = HSBCURLResolver.actionStrategies.get(function);
        if (action instanceof IHsbcUrlAction) {
            return (IHsbcUrlAction) action;
        }
        return null;
    }
    
	/**
	 * Resolves a given URL to a implementation of {@link HSBCURLAction}.
	 * 
	 * @param url
	 *            the url to resolve.
	 * @return The implementation or <code>null</code> if not such class exists.
	 */
	public static HSBCURLAction resolve(final String url,Map<String,HSBCURLAction> map) {
		// Assert HSBC URL
		if (!url.startsWith(HookConstants.HSBC_URL_PREFIX)) {
			throw new IllegalArgumentException(String.format("%s is not a valid HSBC URL", url));
		}

		// Parse params
		String paramsString = url.substring(HookConstants.HSBC_URL_PREFIX.length());
		Map<String, String> params = processParams(paramsString);
		if (params != null) {
			String function = params.get(HookConstants.FUNCTION);
			HSBCURLAction action = map.get(function);
			if (action == null) {
				Log.e(TAG,"URL Action has no function");
				return null;
			}
			action.setParams(params);
			return action;
		}
		else {
			Log.e(TAG,"URL Action has no params");
			return null;
		}
	}

	/**
	 * Extracts the key value pairs from the list of params. It must at least contain the param "function"
	 * 
	 * @param paramsString
	 * @return A list populated with params, or a empty list if there are no params. In the case an error occurs, <code>null</code> rather
	 *         than an empty list will be returned.
	 */
	public static Map<String, String> processParams(final String paramsString) {
		String[] paramsKVPairs = paramsString.split("&");
		Map<String, String> params = new HashMap<String, String>(paramsKVPairs.length);
		for (String keyValuePair : paramsKVPairs) {
			if(keyValuePair!=null&&keyValuePair.indexOf("=")!=-1){
				try {
					String[] kv = keyValuePair.split("=");
					String key=null;
					String value="";

					if(kv.length>1){
						value=kv[1];
					}
					if(kv.length>0){
						key=kv[0];
						params.put(key, URLDecoder.decode(value, "UTF-8"));
					}
				}catch (UnsupportedEncodingException e) {
					// Unable to parse the params, possibly incorrectly encoded, set params to null.
					params = null;
				}
			}
		}
		if (params!=null&&!params.containsKey(HookConstants.FUNCTION)) {
			// This means the URL is invalid, return null;
			params = null;
		}
		return params;
	}

}
