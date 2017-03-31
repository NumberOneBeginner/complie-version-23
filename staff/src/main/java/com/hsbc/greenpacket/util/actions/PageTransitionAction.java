package com.hsbc.greenpacket.util.actions;

import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;

import com.hsbc.greenpacket.activities.Constants;
import com.hsbc.greenpacket.activities.HSBCActivity;
import com.hsbc.greenpacket.hook.Hook;
import com.hsbc.greenpacket.process.ProcessUtil;
import com.hsbc.greenpacket.util.EntityUtil;

import com.none.staff.util.DownloadUtil;

public class PageTransitionAction extends HSBCURLAction {

	private final String SLIDE="slide";

	@Override
	public void execute(Context context, WebView webview, Hook hook) {
		try{
			Map<String, String> map = this.getParams();
			if (map == null) {
				hook.sendMsg(HookConstants.HOOK_ERROR);
				return;
			}
			String url = map.get(Constants.URL);
//			String eid = EntityUtil.getSavedEntityId(context);
//			String webVersion = EntityUtil.getWebResourceVersion(context);
//			String webResourcePath = ProcessUtil.getWebResourcePath(context, eid,webVersion);
//			url = ProcessUtil.localUrlIntercept(url, webResourcePath);
			url = ProcessUtil.localUrlIntercept(url, DownloadUtil.getClientPackPath(context));
			if (url == null || "".equals(url.trim())) {
				hook.sendMsg(HookConstants.HOOK_ERROR);
				return;
			}
			String slide = map.get(SLIDE);
			if (slide == null || "".equals(slide.trim())) {
				hook.sendMsg(HookConstants.HOOK_ERROR);
				return;
			}
			if (HookConstants.SLIDE_L.equals(slide)) {
				hook.sendMsg(HookConstants.SLIDE_L_MSG);
			} else {
				hook.sendMsg(HookConstants.SLIDE_R_MSG);
			}
			hook.sendMsg(HookConstants.SHOW_PROGRESS_MSG);
			webview.clearView();
			loadUrlInMainThread(webview,url);
		}catch(Exception e){
			Log.e("Page Transition","backToApp error!");
		}
	}
}
