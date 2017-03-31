package com.none.staff.activity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hsbc.greenpacket.activities.HSBCActivity;
import com.hsbc.greenpacket.util.actions.HookConstants;

import com.libra.sinvoice.LogHelper;
import com.libra.sinvoice.SinVoiceRecognition;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.none.staff.R;
import com.none.staff.api.ApiManager;
import com.none.staff.domain.MessageResult;
import com.none.staff.utils.AniUtils;
import com.none.staff.utils.CommonUtils;
import com.none.staff.utils.NetworkUtils;

/***
 * 
 * staff SoundWave界面  只能接收声波
 * @author willis
 *
 */
public class StaffSoundWaveActivity extends HSBCActivity implements SinVoiceRecognition.Listener{
	
	//=====包含接收声波的代码
		 private static final String TAG = "CollectFragment";
		 private SinVoiceRecognition mRecognition;
		 private Handler mHanlder;
		 private char mRecgs[] = new char[100];
		 private int mRecgCount;
		// private PowerManager.WakeLock mWakeLock;
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
	    
	    private Button staff_home ;
	    
	    private PowerManager.WakeLock mWakeLock;
	    
	    //根布局
	    private RelativeLayout collect_bg ;
	    
	    private TextView staff_ops_tv ;
	    
	    private String location ;
	    
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
	    
	    static {
			System.loadLibrary("sinvoice");
			Log.d(TAG, "sinvoice jnicall loadlibrary");
		}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_staff_soundwave) ;
		
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
	    mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, TAG);
	     
	    
		initMecognition() ;
		initView() ;
		setOnClickListener() ;
	}

	@Override
	protected void onPause() {
		super.onPause();
		mWakeLock.release();
		mRecognition.stop();
		stopAniMationDrawable() ;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mWakeLock.acquire();
		animationDrawable.start() ;
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mRecognition.stop();
		stopAniMationDrawable() ;
		
		isContinue = false ;
		handler.removeMessages(STAFF_SEND_TO_SERVER_TIMEOUT) ;
	}
	
	private void initMecognition() {
		mRecognition = new SinVoiceRecognition();
		mRecognition.init();
		mRecognition.setListener(this);
		mHanlder = new RegHandler(this);
		mRecognition.start(DeliverFragment.TOKEN_LEN, false);
	}


	private void initView() {
		
		String getExtra = getIntent().getStringExtra("location") ;
		if(!TextUtils.isEmpty(getExtra)){
			location = getExtra ;
		}
		
		divide_linearlayout = (LinearLayout) this.findViewById(R.id.divide_linearlayout) ;
		green_sunandcloud_img = (ImageView) this.findViewById(R.id.green_sunandcloud_img) ;
		alert_recive_result_ll = (LinearLayout) this.findViewById(R.id.alert_recive_result_ll) ;
		alert_norecive_result_ll = (LinearLayout) this.findViewById(R.id.alert_norecive_result_ll) ;
		show_recive_text_ll = (LinearLayout) this.findViewById(R.id.show_recive_text_ll) ;
		
		fl_circle = (FrameLayout) this.findViewById(R.id.fl_circle) ;
		
		recive_sound_image = (ImageView) this.findViewById(R.id.recive_sound_image) ;
		animationDrawable = (AnimationDrawable) recive_sound_image.getBackground() ;
		animationDrawable.start() ;
		
		staff_home = (Button) this.findViewById(R.id.staff_home) ;
		collect_bg = (RelativeLayout) this.findViewById(R.id.collect_bg) ;
		staff_home.setVisibility(View.VISIBLE) ;
		staff_ops_tv = (TextView) this.findViewById(R.id.staff_ops_tv) ;
	}
	
	private void setOnClickListener() {
		staff_home.setOnClickListener(backOnClickListener) ;
	}


	private View.OnClickListener backOnClickListener   = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			isContinue = false ;
			handler.removeMessages(STAFF_SEND_TO_SERVER_TIMEOUT) ;
			
			finish() ;
            slideLeftToRight();
		}
	};
	// =====包含接收声波的代码
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

	// =================
	
	//=====包含接收声波的代码
		 @SuppressLint("ResourceAsColor")
		private  class RegHandler extends Handler {
		       
				private StringBuilder mTextBuilder = new StringBuilder();
		        private StaffSoundWaveActivity mAct;

		        public RegHandler(StaffSoundWaveActivity act) {
		            mAct = act;
		        }

		        @Override
		        public void handleMessage(Message msg) {
		            switch (msg.what) {
		            case DeliverFragment.MSG_SET_RECG_TEXT:
		                char ch = (char) msg.arg1;
//		                mTextBuilder.append(ch);
		                mAct.mRecgs[mAct.mRecgCount++] = ch;
		                break;

		            case DeliverFragment.MSG_RECG_START:
//		                mTextBuilder.delete(0, mTextBuilder.length());
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
		                        	if(NetworkUtils.isNetworkAvailable(StaffSoundWaveActivity.this)){ //有网络才可以操作
		                        		isContinue = true ;
		                        		
		                        		ApiManager.getInstance().joinGame(StaffSoundWaveActivity.this, strReg, new AsyncHttpResponseHandler(){

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

															if("two".equals(joinResult)){
															Log.e("two：", "two") ;	
															//只要打开就让停止了接收声波
															mRecognition.stop() ;
															isContinue = false ;
															handler.removeMessages(STAFF_SEND_TO_SERVER_TIMEOUT) ;
															//展开动画开始
															showDiverAni();	
															alert_norecive_result_ll.setVisibility(View.VISIBLE) ;
															staff_ops_tv.setText("You can only collect one laisee from the same distributor.") ;
															showResultThressSen(alert_norecive_result_ll) ;	
															
														}else{
															isContinue = true ;
							                        		//接收声波成功重复请求服务器看是否中奖
							                        			staffSendToServer(strReg) ;
							                        			handler.removeMessages(STAFF_SEND_TO_SERVER_TIMEOUT) ;
								                        		handler.sendMessageDelayed(handler.obtainMessage(STAFF_SEND_TO_SERVER_TIMEOUT), staffrequestToServerTimeOut*1000*60) ;
														}
													} catch (JSONException e) {
														e.printStackTrace();
													}
												
												}else{
													
												}
											}
		                        			
		                        		}) ;
		                        		
		                        	}else{
		                        		
		                        	}
		                        	Log.e("22222-->", strReg) ;
		                            Log.d(TAG, "reg error!!!!!!!!!!!!!");
		                           // mAct.mRecognisedTextView.setText(strReg);
		                            // mAct.mRegState.setText("reg err(" + msg.arg1 + ")");
		                            // mAct.mRegState.setText("reg err");
		                        }
		                    } catch (UnsupportedEncodingException e) {
		                        e.printStackTrace();
		                    }
		                }
		                break;

		            case DeliverFragment.MSG_PLAY_TEXT:
//		                mAct.mPlayTextView.setText(mAct.mPlayText);
		                break;
		            }
		            super.handleMessage(msg);
		        }
		    }
	
	
	private void stopAniMationDrawable() {
		if (animationDrawable.isRunning() && null != animationDrawable) {
			animationDrawable.stop();
		}
	}
	/**展开切割动画***/	
	private void showDiverAni() {
		show_recive_text_ll.setVisibility(View.INVISIBLE) ; //发送文字
		green_sunandcloud_img.setVisibility(View.INVISIBLE) ;//云和太阳标志的图片
		Bitmap bitmap = CommonUtils.takeScreenShot(StaffSoundWaveActivity.this) ;
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
			View view1 = LayoutInflater.from(StaffSoundWaveActivity.this).inflate(R.layout.divide_image, divide_linearlayout,false) ;
			ImageView iv= (ImageView) view1.findViewById(R.id.image2) ;
			iv.setImageBitmap(bitmaps[i]) ;
			divide_linearlayout.addView(iv) ;
			images.add(iv) ;
		}
		
		staff_home.setVisibility(View.GONE) ;
		collect_bg.setBackgroundResource(R.color.white_color) ;
		
		AniUtils.startAnim(images);
		
		
		bitmaps = null ;
		bm1= null ;
		bm2=null ;
		bitmap = null ;
		fl_circle.setVisibility(View.GONE) ;
	}
	

	/**员工请求是否中奖***/
	private void staffSendToServer(final String gameId){
		if(!isContinue)
			return ;
		
		ApiManager.getInstance().sendResultToStaff(StaffSoundWaveActivity.this, gameId, new AsyncHttpResponseHandler() {
			
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				
				if(!isContinue)
					return ;
				
				String jsonString = new String(arg2) ;
				MessageResult messageResult = MessageResult.prase(jsonString) ;
				if(0==messageResult.getCode()){
					try {
						JSONObject jsonObject = new JSONObject(messageResult.getData());
						
Log.e("staff重复请求server", jsonObject.toString()) ;
						
						if(!TextUtils.isEmpty(jsonObject.optString("userId"))){
							String joinResult = jsonObject.optString("joinResult") ;
							if("draw".equals(joinResult)){
								//展开动画开始
								showDiverAni();	
								isContinue = false ;
								mRecognition.stop() ;
								alert_recive_result_ll.setVisibility(View.VISIBLE) ;
								showResultThressSen(alert_recive_result_ll) ;
								
								//然后三秒钟跳进MyLaisee界面
							}
						}else{
							//重复请求
							handler.removeMessages(STAFF_SEND_TOSERVER) ;
							handler.sendMessageDelayed(handler.obtainMessage(STAFF_SEND_TOSERVER,gameId), 1000) ;
						}
					} catch (JSONException e) {
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
	
	/**
	 * 显示3秒钟展开结果
	 * @param view  要隐藏的View是变化的，当参数传进来
	 */
	private void showResultThressSen(final View view) {
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				divide_linearlayout.removeAllViews() ;
				staff_home.setVisibility(View.VISIBLE) ;
				collect_bg.setBackgroundResource(R.drawable.green_laisee_bg) ;
				fl_circle.setVisibility(View.VISIBLE) ;
				
				view.setVisibility(View.GONE) ;
				
				green_sunandcloud_img.setVisibility(View.VISIBLE) ; //云和太阳标志的图片
				show_recive_text_ll.setVisibility(View.VISIBLE) ;
				if(view==alert_recive_result_ll){
					Log.e("staff result回调了", "staff result回调了") ;	
					Intent intent = new Intent();
					//String result = "" ; //请求中奖的结果 格式 不知道
				//	intent.putExtra(HookConstants.SOUND_WAVE_RESULT, result);
					intent.putExtra(HookConstants.FUNCTION, HookConstants.SOUNDWAVE_TO_NATIVE);
					setResult(Activity.RESULT_OK, intent) ;
					finish() ;
					slideLeftToRight();
				}
			}
		}, 3000) ;
	}
}
