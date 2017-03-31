package com.none.staff.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
/**
 * 
 * @ClassName: MyProgressBar 
 * @Description: 菊花转加载进度条
 * @author willis
 */

public class MyProgressBar extends ImageView {
	/** 动画对象 **/
	private AnimationDrawable anim = null;
	/**
	 * 
	 * <p>Constructors: </p> 
	 * <p>Description: </p> 
	 * @param context
	 */
	public MyProgressBar(Context context) {
		super(context);
		 anim = (AnimationDrawable) this.getDrawable();
	}
	/**
	 * 
	 * <p>Constructors: </p> 
	 * <p>Description: </p> 
	 * @param context
	 * @param attrs
	 */
	public MyProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		 anim = (AnimationDrawable) this.getDrawable();
	}
	/**
	 * 
	 * <p>Constructors: </p> 
	 * <p>Description: </p> 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public MyProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		 anim = (AnimationDrawable) this.getDrawable();
	}
	/**
	 * 
	 * @Methods: show 
	 * @Description: 显示进度条
	 * @throws
	 */
	public void show(){
		this.setVisibility(View.VISIBLE);
		
	}
	/**
	 * 
	 * @Methods: dismiss 
	 * @Description: 隐藏进度条
	 * @throws
	 */
	public void dismiss(){
		this.setVisibility(View.GONE);
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		if(anim != null){
			if(anim.isRunning()){
				anim.stop();
			}else{
				anim.start();
			}
		}
	}

}
