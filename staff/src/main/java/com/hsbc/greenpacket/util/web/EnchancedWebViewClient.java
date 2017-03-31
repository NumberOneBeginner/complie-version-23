/*
 * COPYRIGHT. HSBC HOLDINGS PLC 2011. ALL RIGHTS RESERVED.
 *
 * This software is only to be used for the purpose for which it has been
 * provided. No part of it is to be reproduced, disassembled, transmitted,
 * stored in a retrieval system nor translated in any human or computer
 * language in any way or for any other purposes whatsoever without the
 * prior written consent of HSBC Holdings plc.
 */
package com.hsbc.greenpacket.util.web;

import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * A slightly enhanced {@link WebViewClient}. This provides the following features
 * <ul>
 * <li>Ensures that links loaded via the {@link WebView} stay in the {@link WebView} rather than launching in the default browser</li>
 * <li>A dialog to allow the user to enter a user name and password if authentication is required, this can be necessary is the user is
 * behind a proxy.</li>
 * <ul>
 * XXX: Don't use {@link LoggedWebViewClient} for production, this is a utility class to aid development debugging.
 */
public class EnchancedWebViewClient extends WebViewClient {

	private static final String TAG = "EnchancedWebViewClient";

    private static class AuthCredential {
		public String username;
		public String password;

		/**
		 * 
		 */
		public AuthCredential(final String username, final String password) {
			this.username = username;
			this.password = password;
		}
	}

	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hsbc.util.LoggedWebViewClient#onReceivedSslError(android.webkit.WebView, android.webkit.SslErrorHandler,
	 * android.net.http.SslError)
	 */
	@Override
	public void onReceivedSslError(final WebView view, final SslErrorHandler handler, final SslError error) {
		// TODO: Implement a dialog around this choice, however this should only be a issue when using internal test servers as the actual
		// live web server SHOULD have a correct SSL Cert.
		handler.proceed();
	}
}
