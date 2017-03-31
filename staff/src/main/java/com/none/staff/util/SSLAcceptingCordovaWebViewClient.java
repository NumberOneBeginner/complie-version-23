/*
 * COPYRIGHT. HSBC HOLDINGS PLC 2015. ALL RIGHTS RESERVED.
 *
 * This software is only to be used for the purpose for which it has been
 * provided. No part of it is to be reproduced, disassembled, transmitted,
 * stored in a retrieval system nor translated in any human or computer
 * language in any way or for any other purposes whatsoever without the
 * prior written consent of HSBC Holdings plc.
 */
package com.none.staff.util;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewClient;

import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;

public class SSLAcceptingCordovaWebViewClient extends CordovaWebViewClient{
    
    public SSLAcceptingCordovaWebViewClient(CordovaInterface cordova, CordovaWebView view) {
        super(cordova, view);
        // TODO Auto-generated constructor stub
    }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            // testing against getPrimaryError() or hasErrors() will fail on Honeycomb or older.
            // You might check for something different, such as specific info in the certificate,
            //if (error.getPrimaryError() == SslError.SSL_IDMISMATCH) {
                handler.proceed();
            //} else {
            //    super.onReceivedSslError(view, handler, error);
            //}
        }
}