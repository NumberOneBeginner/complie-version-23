package com.none.staff.domain;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 返回json结果判断的javaBean
 * @author willis
 */
public class MessageResult {

	private String data ;
	private int code ;
	private String msg ;


	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;

	}


	/**解析返回结果是否有value如果成功就有，失败则没有这个字段*/
	public static MessageResult prase(String jsonObject) {
		MessageResult messageResult = null;
		JSONObject object = null ;
		try {
			object = new JSONObject(jsonObject) ;
			messageResult = new MessageResult();

			if (jsonObject == null) {
				return messageResult;
			}
			if (!object.isNull("data")) {
				messageResult.setData(object.getString("data"));
			}

			if(!object.isNull("msg")){
				messageResult.setMsg(object.getString("msg")) ;
			}
			if(!object.isNull("code")){
				messageResult.setCode(object.getInt("code")) ;
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return messageResult;

	}
}
