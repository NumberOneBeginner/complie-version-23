package com.hsbc.greenpacket.task;

public class ProxyResponse {
StringBuffer responseStr;
String callbackJs;
String statusCode;

public StringBuffer getResponseStr() {
	return responseStr;
}
public void setResponseStr(StringBuffer responseStr) {
	this.responseStr = responseStr;
}
public String getCallbackJs() {
	return callbackJs;
}
public String getStatusCode() {
	return statusCode;
}

public void setCallbackJs(String callbackJs) {
	this.callbackJs = callbackJs;
}
public void setStatusCode(String statusCode) {
	this.statusCode = statusCode;
}

}
