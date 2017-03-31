package com.none.staff.activity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.libra.sinvoice.LogHelper;
import com.libra.sinvoice.SinVoiceRecognition;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.none.staff.R;
import com.none.staff.api.ApiManager;
import com.none.staff.domain.MessageResult;
import com.none.staff.event.HomeOrBackEvent;
import com.none.staff.utils.AniUtils;
import com.none.staff.utils.CommonUtils;
import com.none.staff.utils.NetworkUtils;

import de.greenrobot.event.EventBus;

/***
 * Boss Collect Sound Wave Fragment
 * @author willis
 *
 */
public class CollectFragment extends Fragment implements SinVoiceRecognition.Listener{
	
	private DeliverActionInterFace mCallBack ;
	
	//=====包含接收声波的代码
	 private static final String TAG = "CollectFragment";
	 private SinVoiceRecognition mRecognition;
	 private Handler mHanlder;
	 private char mRecgs[] = new char[100];
	 private int mRecgCount;
	 //========
	 
	 /**分隔动画的根布局***/
	 private LinearLayout divide_linearlayout ;
	 
	 /**云和太阳的标志图片**/
	 private ImageView green_sunandcloud_img ;
	 
	 //界面分隔动画出来以后显示的布局
	 private LinearLayout alert_recive_result_ll ;
	 
	 /**接收声波时下面显示的字***/
	 private LinearLayout show_recive_text_ll ;
	 
	//接收波时声音动画
	private AnimationDrawable animationDrawable;
	
	/**接收声波的图片**/
	private ImageView recive_sound_image ;
	
	private boolean hideOrShowFragment ;//CollectFragment显示或隐藏
	
	/**不能重复中奖的根布局***/
	private LinearLayout alert_norecive_result_ll ;
	
	   //包含声波背影的布局
    private FrameLayout fl_circle = null ;
    
    private TextView boss_ops_tv ;
    
    private static final int STAFF_SEND_TOSERVER = 2 ;
    
  
    private static final int STAFF_SEND_TO_SERVER_TIMEOUT  = 3 ;
    
    //staff重复请求服务器，默认是5分钟
    private int staffrequestToServerTimeOut = 5 ;
    
    
    private boolean isContinue = false ;
    
    
    
    private Handler handler = new Handler(){
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
			case STAFF_SEND_TOSERVER:
				isContinue = true ;
				staffSendToServer((String)msg.obj) ;
				break;
			case STAFF_SEND_TO_SERVER_TIMEOUT:
				isContinue = false ;
				handler.removeMessages(STAFF_SEND_TOSERVER) ; //超时就停止掉请求
				break ;
			}
    	};
    } ;
    
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallBack = (DeliverActionInterFace) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnHeadlineSelectedListener");
		}
	}

	
	 @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//=====包含接收声波的代码
		mRecognition = new SinVoiceRecognition();
		mRecognition.init();
		mRecognition.setListener(this);
		mHanlder = new RegHandler(this);
		
	    //========
	    
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		//=====包含接收声波的代码  为了客户调试 暂时把这个注释掉了。不让接收声波  
		mRecognition.start(DeliverFragment.TOKEN_LEN, false);
		//=======
		 EventBus.getDefault().register(this,"getHomeOrBack",HomeOrBackEvent.class);
		 
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.collect_fragment, container,false) ;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		divide_linearlayout = (LinearLayout) view.findViewById(R.id.divide_linearlayout) ;
		green_sunandcloud_img = (ImageView) view.findViewById(R.id.green_sunandcloud_img) ;
		alert_recive_result_ll = (LinearLayout) view.findViewById(R.id.alert_recive_result_ll) ;
		alert_norecive_result_ll = (LinearLayout) view.findViewById(R.id.alert_norecive_result_ll) ;
		show_recive_text_ll = (LinearLayout) view.findViewById(R.id.show_recive_text_ll) ;
		
		fl_circle = (FrameLayout) view.findViewById(R.id.fl_circle) ;
		
		recive_sound_image = (ImageView) view.findViewById(R.id.recive_sound_image) ;
		animationDrawable = (AnimationDrawable) recive_sound_image.getBackground() ;
		handler.postDelayed(new Runnable (){
            @Override
            public void run() {
                if(animationDrawable!=null){
                    animationDrawable.start() ;
                }
            }
        }, 1000);
//		animationDrawable.start() ;
		
		boss_ops_tv = (TextView) view.findViewById(R.id.boss_ops_tv) ;
		
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
	}
	//=====包含接收声波的代码
	 private  class RegHandler extends Handler {
	       
			private StringBuilder mTextBuilder = new StringBuilder();
	        private CollectFragment mAct;

	        public RegHandler(CollectFragment act) {
	            mAct = act;
	        }

	        @Override
	        public void handleMessage(Message msg) {
	            switch (msg.what) {
	            case DeliverFragment.MSG_SET_RECG_TEXT:
	                char ch = (char) msg.arg1;
//	                mTextBuilder.append(ch);
	                mAct.mRecgs[mAct.mRecgCount++] = ch;
	                break;

	            case DeliverFragment.MSG_RECG_START:
//	                mTextBuilder.delete(0, mTextBuilder.length());
	                mAct.mRecgCount = 0;
	                break;

	            case DeliverFragment.MSG_RECG_END:
	                LogHelper.d(TAG, "recognition end gIsError:" + msg.arg1);
	                if ( mAct.mRecgCount > 0 ) {
	                    byte[] strs = new byte[mAct.mRecgCount];
	                    for ( int i = 0; i < mAct.mRecgCount; ++i ) {
	                        strs[i] = (byte)mAct.mRecgs[i];
	                    }
	                    try {
	                        final String strReg = new String(strs, "UTF8");
	                        if (msg.arg1 >= 0) {
	                            Log.d(TAG, "reg ok!!!!!!!!!!!!");
	                            if (null != mAct) {
	                              //  mAct.mRecognisedTextView.setText(strReg);
	                                // mAct.mRegState.setText("reg ok(" + msg.arg1 + ")");
	                            	Log.e("1111111-->", strReg) ;
	                            }
	                        } else {
	                        	if(NetworkUtils.isNetworkAvailable(getActivity())){ //有网络才可以操作
	                        	
	                        		//recivedSound = strReg ;
	                        		isContinue = true ;
	                        		
	                        		
	                        		
	                        	//请求成功或失败打开动画，显示相应的提示
	                        	if(getActivity()!=null){
	                        		ApiManager.getInstance().joinGame(getActivity(), strReg, new AsyncHttpResponseHandler(){

										@Override
										public void onFailure(int arg0,Header[] arg1, byte[] arg2,Throwable arg3) {
											isContinue = false ;
										}

										@Override
										public void onSuccess(int arg0,Header[] arg1, byte[] arg2) {
											String jsonString = new String(arg2) ;
											MessageResult messageResult = MessageResult.prase(jsonString) ;
											
											if(0==messageResult.getCode()){
												
												JSONObject jsonObject;
												try {
													jsonObject = new JSONObject(messageResult.getData());
													Log.e("中奖结果：", jsonObject.toString()) ;												
													String joinResult = jsonObject.optString("joinResult") ;
													
													if(!TextUtils.isEmpty(jsonObject.optString("timeout"))){
														staffrequestToServerTimeOut = Integer.parseInt(jsonObject.optString("timeout")) ;
													}
														if("two".equals(joinResult)){ //不能重复中奖
															//只要打开就让停止了接收声波
														mRecognition.stop() ;
														
														isContinue = false ;
														
														handler.removeMessages(STAFF_SEND_TO_SERVER_TIMEOUT) ;
														
														
														//展开动画开始
														showDiverAni();	
															
														Log.e("two：", "two") ;	
														alert_norecive_result_ll.setVisibility(View.VISIBLE) ;
														boss_ops_tv.setText("You can only collect one laisee from the same distributor.") ;
														showResultThressSen(alert_norecive_result_ll) ;
													}else{ //否则重复请求服务器
														isContinue = true ;
														staffSendToServer(strReg) ;
						                        			handler.removeMessages(STAFF_SEND_TO_SERVER_TIMEOUT) ;
						                        			handler.sendMessageDelayed(handler.obtainMessage(STAFF_SEND_TO_SERVER_TIMEOUT), staffrequestToServerTimeOut*1000*60) ;
													}
													
												} catch (JSONException e) {
													e.printStackTrace();
												}
											
											}else{
//												alert_norecive_result_ll.setVisibility(View.VISIBLE) ;
//												showResultThressSen(alert_norecive_result_ll) ;
												
//												handler.removeCallbacks(staffSendToServerRunnable) ;
//												handler.removeCallbacks(staffSendToServerTimeOutRunnable) ;
											}
										}
									
	                        			
	                        		}) ;
	                        	}
	                        	}else{
	                        		
	                        	}
	                        	Log.e("22222-->", strReg) ;
	                            Log.d(TAG, "reg error!!!!!!!!!!!!!");
	                       
	                        }
	                    } catch (UnsupportedEncodingException e) {
	                        e.printStackTrace();
	                    }
	                }
	                break;

	            case DeliverFragment.MSG_PLAY_TEXT:
//	                mAct.mPlayTextView.setText(mAct.mPlayText);
	                break;
	            }
	            super.handleMessage(msg);
	        }
	    }
	//=====
	
	 //=====包含接收声波的代码
	@Override
	public void onSinVoiceRecognitionStart() {
		 mHanlder.sendEmptyMessage(DeliverFragment.MSG_RECG_START);
	}

	@Override
	public void onSinVoiceRecognition(char ch) {
		 mHanlder.sendMessage(mHanlder.obtainMessage(DeliverFragment.MSG_SET_RECG_TEXT, ch, 0));
	}

	@Override
	public void onSinVoiceRecognitionEnd(int result) {
		 mHanlder.sendMessage(mHanlder.obtainMessage(DeliverFragment.MSG_RECG_END, result, 0));
	}
	//=================
	
	/**员工请求是否中奖***/
	private void staffSendToServer(final String gameId){
		
		
		if(!isContinue)
			return ;
		
		if(getActivity() !=null){
			ApiManager.getInstance().sendResultToStaff(getActivity(), gameId, new AsyncHttpResponseHandler() {
				
				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
					if(!isContinue)
						return ;
					
					String jsonString = new String(arg2) ;
	Log.e("sendResultToStaff", jsonString);			
					MessageResult messageResult = MessageResult.prase(jsonString) ;
					if(0==messageResult.getCode()){
						try {
							JSONObject jsonObject = new JSONObject(messageResult.getData());
							
							
							if(!TextUtils.isEmpty(jsonObject.optString("userId"))){
								String joinResult = jsonObject.optString("joinResult") ;
								if("draw".equals(joinResult)){
									isContinue = false ;
									//展开动画开始
									showDiverAni();	
									mRecognition.stop() ;
									
									alert_recive_result_ll.setVisibility(View.VISIBLE) ;
									showResultThressSen(alert_recive_result_ll) ;
									
									//然后三秒钟跳进MyLaisee界面
								}
							}else{
//								//重复请求
								handler.removeMessages(STAFF_SEND_TOSERVER) ;
								handler.sendMessageDelayed(handler.obtainMessage(STAFF_SEND_TOSERVER,gameId), 1000) ;
						
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else{
						
					}
				}
				
				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {

					if(!isContinue)
						return ;
				}
			}) ;
		}
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		hideOrShowFragment = hidden ;
		if(hidden){ //隐藏时调用

			if(null !=mRecognition){  //要判断一下，不然有可能会空指针
				Log.e("onHiddenChanged", hidden+"") ; 
				mRecognition.stop() ;
				stopAniMationDrawable() ;
			}
			
			isContinue = false ;
			handler.removeMessages(STAFF_SEND_TOSERVER) ;
			handler.removeMessages(STAFF_SEND_TO_SERVER_TIMEOUT) ;

			
		}else{//显示时调用
			if(null !=mRecognition){
				Log.e("onHiddenChanged", hidden+"") ;	
				mRecognition.start(DeliverFragment.TOKEN_LEN, false);
				animationDrawable.start() ;
			}
			
		}
	}

	private void stopAniMationDrawable(){
		if(animationDrawable.isRunning() && null != animationDrawable){
			animationDrawable.stop() ;
		}
	}
	
	
	
	@Override
	public void onPause() {
		super.onPause();
		isContinue = false ;
		mRecognition.stop();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.e("destory:asdfa", "------------------") ;
		mRecognition.stop();
		isContinue = false ;
		handler.removeMessages(STAFF_SEND_TO_SERVER_TIMEOUT) ;
	}
	
	public void getHomeOrBack(HomeOrBackEvent event){
		if (!hideOrShowFragment) {  //如果CollectFragment显示的话 点击返回建 退出应用程序。
			if ("back".equals(event.getFlag())) { 
				if (null != getActivity()) {
					handler.removeMessages(STAFF_SEND_TO_SERVER_TIMEOUT) ;
					
					isContinue = false ;
					getActivity().finish();
				}
			}

		}
	}
	
	/**展开切割动画***/	
	private void showDiverAni() {
		show_recive_text_ll.setVisibility(View.INVISIBLE) ; //发送文字
		green_sunandcloud_img.setVisibility(View.INVISIBLE) ;//云和太阳标志的图片
		Bitmap bitmap = CommonUtils.takeScreenShot(getActivity()) ;
		//上下平分图片的方法
		Bitmap bm1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), (bitmap.getHeight() / 2));
		Bitmap bm2 = Bitmap.createBitmap(bitmap, 0, (bitmap.getHeight() / 2), bitmap.getWidth(), (bitmap.getHeight() / 2));
		 Bitmap[] bitmaps= new Bitmap[2] ;
		 bitmaps[0] = bm1 ;
		 bitmaps[1] = bm2 ;
		 
		 divide_linearlayout.removeAllViews() ;
		 ArrayList<ImageView> images = new ArrayList<ImageView>() ;
		 images.clear() ;
		for (int i = 0; i < 2; i++) {
			View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.divide_image, divide_linearlayout,false) ;
			ImageView iv= (ImageView) view1.findViewById(R.id.image2) ;
			iv.setImageBitmap(bitmaps[i]) ;
			divide_linearlayout.addView(iv) ;
			images.add(iv) ;
		}
		
		mCallBack.hideHome() ;
		mCallBack.changeSoundWaveBg(true) ;
		
		AniUtils.startAnim(images);
		
		
		bitmaps = null ;
		bm1= null ;
		bm2=null ;
		bitmap = null ;
		fl_circle.setVisibility(View.GONE) ;
	}
	
	
	/**
	 * 显示3秒钟展开结果 如果是中奖的话就跳转到MyLaisee界面 ，如果没有中奖，还在当前页
	 * @param view  要隐藏的View是变化的，当参数传进来
	 */
	private void showResultThressSen(final View view) {
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				
				divide_linearlayout.removeAllViews() ;
				mCallBack.showHome() ;
				mCallBack.changeSoundWaveBg(false) ;
				fl_circle.setVisibility(View.VISIBLE) ;
				
				view.setVisibility(View.GONE) ;
				
				green_sunandcloud_img.setVisibility(View.VISIBLE) ; //云和太阳标志的图片
				show_recive_text_ll.setVisibility(View.VISIBLE) ;
				if(view==alert_recive_result_ll){//中奖了
				Log.d("result回调了", "result回调了") ;	//这里使用了测试数据 不知道是否有误 对应的就是
					mCallBack.setResult("") ;
				}
			}
		}, 3000) ;
	}
}

