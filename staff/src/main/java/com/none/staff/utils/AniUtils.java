package com.none.staff.utils;

import java.util.ArrayList;

import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
/**
 * 动画工具类
 * @author willis
 *
 */
public class AniUtils {
	
	 private static final int DURATION_TIME = 600;
	/****
	   * 开启动画
	 * @return 
	   */
	  public static void startAnim(ArrayList<ImageView> images) {
	    AnimationSet animup = new AnimationSet(true);
	    TranslateAnimation mytranslateanimup0 = new TranslateAnimation(
	        Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
	        Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
	        -0.5f);
	    mytranslateanimup0.setDuration(DURATION_TIME);
//	    TranslateAnimation mytranslateanimup1 = new TranslateAnimation(
//	        Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
//	        Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
//	        +0.5f);
//	    mytranslateanimup1.setDuration(DURATION_TIME);
//	    mytranslateanimup1.setStartOffset(DURATION_TIME);
	    animup.addAnimation(mytranslateanimup0);
	    //让动画保持在插入完的状态
	    animup.setFillAfter(true) ;
	//    animup.addAnimation(mytranslateanimup1);
	    images.get(0).startAnimation(animup);

	    AnimationSet animdn = new AnimationSet(true);
	    TranslateAnimation mytranslateanimdn0 = new TranslateAnimation(
	        Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
	        Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
	        +0.5f);
	    mytranslateanimdn0.setDuration(DURATION_TIME);
//	    TranslateAnimation mytranslateanimdn1 = new TranslateAnimation(
//	        Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
//	        Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
//	        -0.5f);
//	    mytranslateanimdn1.setDuration(DURATION_TIME);
//	    mytranslateanimdn1.setStartOffset(DURATION_TIME);
	    animdn.addAnimation(mytranslateanimdn0);
	   // animdn.addAnimation(mytranslateanimdn1);
	    //让动画保持在插入完的状态
	    animdn.setFillAfter(true) ;
	    images.get(1).startAnimation(animdn);

	    // 动画监听，开始时显示加载状态，
	    mytranslateanimdn0.setAnimationListener(new AnimationListener() {

	      @Override
	      public void onAnimationStart(Animation animation) {
	        
	      }

	      @Override
	      public void onAnimationRepeat(Animation animation) {

	      }

	      @Override
	      public void onAnimationEnd(Animation animation) {
	        
	      }
	    });
	  }
}
