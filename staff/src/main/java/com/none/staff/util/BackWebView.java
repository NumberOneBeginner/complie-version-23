package com.none.staff.util;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.cordova.CordovaWebView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.Toast;
/**
 * 
 * @author peter 2017/2/17
 * 重写CordovaWebView
 * 重写里面的onkeyDown方法 设置手机返回键事件
 *
 */
public class BackWebView extends CordovaWebView {

	public BackWebView(Context context, AttributeSet attrs, int defStyle,
			boolean privateBrowsing) {
		super(context, attrs, defStyle, privateBrowsing);
		// TODO Auto-generated constructor stub
	}

	public BackWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public BackWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public BackWebView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
	       Toast.makeText(this.getContext(),"点击了返回按钮",1).show();
	       this.loadUrl("javascript:newChangePage()");
	        return true;
	    }

	    return super.onKeyDown(keyCode, event);
	}
//	  @Override
//	    public boolean onKeyUp(int keyCode, KeyEvent event){
//	        
//	        if (keyCode == KeyEvent.KEYCODE_BACK) {
//	            exitBy2Click(); 
//	            return true;
//	        }else{
//	            return super.onKeyUp(keyCode, event);
//	        }
//	    }
	    
	    public void toastMessage(String msg, int duration) {
	        Toast.makeText(this.getContext(), msg, duration).show();
	    }
	    
	    private static Boolean isExit = false;  
	    
	    private void exitBy2Click() {  
	        Timer tExit = null;  
	        if (isExit == false) {  
	            isExit = true; // 准备退出  
	            toastMessage("再按一次退出程序", 2000);
	            tExit = new Timer();  
	            tExit.schedule(new TimerTask() {  
	                @Override  
	                public void run() {  
	                    isExit = false; // 取消退出  
	                }  
	            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务  
	        } else {  
	            System.exit(0);  
	        }  
	    }

}
