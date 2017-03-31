package com.none.staff.HttpsHelper;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import android.util.Log;

public class NullHostNameVerifier implements HostnameVerifier {

	
	
	
	  @Override     
      public boolean verify(String hostname, SSLSession session) {  
          Log.i("111", "Approving certificate for " + hostname);  
          return true;  
      }  
    
	
 
  }	
